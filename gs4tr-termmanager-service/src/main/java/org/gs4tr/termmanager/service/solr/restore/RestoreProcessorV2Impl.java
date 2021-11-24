package org.gs4tr.termmanager.service.solr.restore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrInputDocument;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation3.solr.impl.CloudHttpSolrClient;
import org.gs4tr.foundation3.solr.model.update.CommitOption;
import org.gs4tr.termmanager.io.tlog.config.PersistentStoreHandler;
import org.gs4tr.termmanager.io.tlog.impl.TransactionLogHandler;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.model.reindex.BackupSearchCommand;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.termmanager.persistence.solr.util.RouteHelper;
import org.gs4tr.termmanager.service.DbSubmissionTermEntryService;
import org.gs4tr.termmanager.service.DbTermEntryService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.solr.GlossaryConnectionManager;
import org.gs4tr.termmanager.service.solr.SolrServiceConfiguration;
import org.gs4tr.termmanager.service.solr.restore.converter.RestoreRegularConverter;
import org.gs4tr.termmanager.service.solr.restore.converter.RestoreSubmissionConverter;
import org.gs4tr.termmanager.service.solr.restore.model.RecodeOrCloneCommand;
import org.gs4tr.termmanager.service.solr.restore.model.ReindexCommand;
import org.gs4tr.tm3.api.TmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

@Component("restoreProcessorV2")
public class RestoreProcessorV2Impl implements IRestoreProcessorV2 {

    private static final Log LOGGER = LogFactory.getLog(RestoreProcessorV2Impl.class);

    private static final String TASK = "REINDEX";

    @Value("${index.batchSize:500}")
    private int _batchSize;

    @Autowired
    private DbSubmissionTermEntryService _dbSubmissionTermEntryService;

    @Autowired
    private DbTermEntryService _dbTermEntryService;

    private ListeningExecutorService _executorService;

    private volatile boolean _finished = false;

    @Autowired
    private GlossaryConnectionManager _glossaryConnectionManager;

    private volatile int _percentage = 0;

    @Value("#{'${index.project.codes.for.reindexed}'.split(',')}")
    private List<String> _projectCodesForReindex;

    @Value("#{'${index.project.codes.to.be.skipped}'.split(',')}")
    private List<String> _projectCodesToSkip;

    @Autowired(required = false)
    private ProjectService _projectService;

    @Autowired
    private RecodeOrCloneTermsProcessor _recodeOrCloneTermsProcessor;

    private List<ReindexCommand> _regularCommands;

    @Value("${index.restoreBatchSize:500}")
    private int _restoreBatchSize;

    @Autowired
    private SolrServiceConfiguration _solrConfig;

    private List<ReindexCommand> _submissionCommands;

    private volatile long _totalCount = 0;

    @Autowired
    private TransactionLogHandler _transactionLogHandler;

    @Autowired
    private PersistentStoreHandler storeHandler;

    @Override
    public int getPercentage() {
	return _percentage;
    }

    @Override
    public boolean isFinished() {
	return _finished;
    }

    @Override
    public void restore() throws Exception {
	StopWatch watch = new StopWatch(TASK);
	watch.start(TASK);

	recodeOrCloneTerms();

	performRestore();

	watch.stop();

	LogHelper.info(LOGGER, watch.prettyPrint());

	_finished = true;
    }

    @Override
    public void restoreRecodeOrClone(List<RecodeOrCloneCommand> recodeCommands,
	    List<RecodeOrCloneCommand> cloneCommands) throws Exception {
	StopWatch watch = new StopWatch(TASK);
	watch.start(TASK);

	Set<Long> projectsIdsLockedByRecodeOrClone = new HashSet<>();

	try {
	    Map<Long, String> projectIdShortCodeMap = getProjectIdShortCodeMap(recodeCommands, cloneCommands);

	    lockProjectsForRecodeOrClone(projectIdShortCodeMap, projectsIdsLockedByRecodeOrClone);

	    recodeOrCloneWhileAppRunning(recodeCommands, cloneCommands);

	    performRestore();

	    watch.stop();

	    LogHelper.info(LOGGER, watch.prettyPrint());
	} finally {
	    unlockProjects(projectsIdsLockedByRecodeOrClone);

	    _finished = true;
	}
    }

    private void addProjectShortCodes(Set<String> projectShortCodesFromRecodeOrClone) {
	List<String> projectCodesForReindex = getProjectCodesForReindex();

	Set<String> shortCodesToRecode = new HashSet<>();
	shortCodesToRecode.addAll(projectCodesForReindex);
	shortCodesToRecode.addAll(projectShortCodesFromRecodeOrClone);

	projectCodesForReindex.clear();

	projectCodesForReindex.addAll(shortCodesToRecode);
    }

    private void addWithDuplicateCheck(Set<String> projectShortCodesFromRecodeOrClone) {
	List<String> projectCodesForReindex = getProjectCodesForReindex();
	projectCodesForReindex.remove(StringUtils.EMPTY);

	boolean isRebuildByProjectShortCodes = getRecodeOrCloneTermsProcessor().isRebuildByProjectShortCodes();
	boolean isProjectCodesForReindexEmpty = projectCodesForReindex.isEmpty();

	if (isRebuildByProjectShortCodes || !isProjectCodesForReindexEmpty) {
	    addProjectShortCodes(projectShortCodesFromRecodeOrClone);
	}

    }

    private void awaitTermination() throws InterruptedException {
	if (!_executorService.awaitTermination(30, TimeUnit.SECONDS)) {
	    _executorService.shutdownNow();
	    if (!_executorService.awaitTermination(30, TimeUnit.SECONDS)) {
		LOGGER.error("Executor did not terminate");
	    }
	}
    }

    private void deleteAllFromV2Collections() throws TmException {
	GlossaryConnectionManager glossaryConnectionManager = getGlossaryConnectionManager();

	// delete all from regularV2 collection
	ITmgrGlossaryConnector regularConnector = glossaryConnectionManager.getConnector(getRegularV2Collection());
	ITmgrGlossaryUpdater regularUpdater = regularConnector.getTmgrUpdater();
	regularUpdater.deleteAll();

	// delete all from submissionV2 collection
	ITmgrGlossaryConnector submissionConnector = glossaryConnectionManager
		.getConnector(getSubmissionV2Collection());
	ITmgrGlossaryUpdater submissionUpdater = submissionConnector.getTmgrUpdater();
	submissionUpdater.deleteAll();
    }

    private void deleteByProjectShortCodes(List<String> shortCodes) throws TmException {
	List<Long> projectIds = getProjectService().findProjectIdsByShortCodes(shortCodes);

	GlossaryConnectionManager glossaryConnectionManager = getGlossaryConnectionManager();

	// delete all from regularV2 collection
	ITmgrGlossaryConnector regularConnector = glossaryConnectionManager.getConnector(getRegularV2Collection());
	ITmgrGlossaryUpdater regularUpdater = regularConnector.getTmgrUpdater();
	regularUpdater.deleteByProjects(projectIds);

	// delete all from submissionV2 collection
	ITmgrGlossaryConnector submissionConnector = glossaryConnectionManager
		.getConnector(getSubmissionV2Collection());
	ITmgrGlossaryUpdater submissionUpdater = submissionConnector.getTmgrUpdater();
	submissionUpdater.deleteByProjects(projectIds);
    }

    private void deleteCollections() throws TmException {
	List<String> projectCodesForReindex = getProjectCodesForReindex();
	projectCodesForReindex.remove(StringUtils.EMPTY);

	if (CollectionUtils.isEmpty(projectCodesForReindex)) {
	    deleteAllFromV2Collections();
	} else {
	    deleteByProjectShortCodes(projectCodesForReindex);
	}
    }

    private void flushBuffer(String collection, CommitOption commitOption, ReindexCommand command) throws TmException {
	ITmgrGlossaryUpdater updater = getGlossaryConnectionManager().getConnector(collection).getTmgrUpdater();

	List<SolrInputDocument> copy = new ArrayList<>(command.getBuffer());
	updater.saveSolrDocs(command.getRoute(), commitOption, copy);

	command.getBuffer().clear();
    }

    private int getBatchSize() {
	return _batchSize;
    }

    private DbSubmissionTermEntryService getDbSubmissionTermEntryService() {
	return _dbSubmissionTermEntryService;
    }

    private DbTermEntryService getDbTermEntryService() {
	return _dbTermEntryService;
    }

    private GlossaryConnectionManager getGlossaryConnectionManager() {
	return _glossaryConnectionManager;
    }

    private List<String> getProjectCodesForReindex() {
	return _projectCodesForReindex;
    }

    private List<String> getProjectCodesToSkip() {
	return _projectCodesToSkip;
    }

    private Map<Long, String> getProjectIdShortCodeMap(List<RecodeOrCloneCommand> recodeCommands,
	    List<RecodeOrCloneCommand> cloneCommands) {
	Map<Long, String> projectIdShortCodeMap = new HashMap<>();
	recodeCommands.forEach(c -> projectIdShortCodeMap.put(c.getProjectId(), c.getProjectShortCode()));
	cloneCommands.forEach(c -> projectIdShortCodeMap.put(c.getProjectId(), c.getProjectShortCode()));

	return projectIdShortCodeMap;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private RecodeOrCloneTermsProcessor getRecodeOrCloneTermsProcessor() {
	return _recodeOrCloneTermsProcessor;
    }

    private String getRegularV2Collection() {
	return getSolrConfig().getRegularCollection();
    }

    private int getRestoreBatchSize() {
	return _restoreBatchSize;
    }

    private String getRoute(String collection, Long projectId) {
	ITmgrGlossaryConnector connector = getGlossaryConnectionManager().getConnector(collection);

	CloudHttpSolrClient client = connector.getClient();

	return RouteHelper.getRoute(client, collection, projectId);
    }

    private List<String> getRoutes(String collection) {
	ITmgrGlossaryConnector connector = getGlossaryConnectionManager().getConnector(collection);
	return connector.getClient().routes(collection);
    }

    private SolrServiceConfiguration getSolrConfig() {
	return _solrConfig;
    }

    private PersistentStoreHandler getStoreHandler() {
	return storeHandler;
    }

    private String getSubmissionV2Collection() {
	return getSolrConfig().getSubmissionCollection();
    }

    private long getTotalCount() {
	List<String> projectCodesForReindex = getProjectCodesForReindex();
	projectCodesForReindex.remove(StringUtils.EMPTY);

	List<Long> projectIds = new ArrayList<>();
	_regularCommands.forEach(c -> projectIds.addAll(c.getProjectIds()));
	long regularCount = getDbTermEntryService().getTotalCount(projectIds);

	projectIds.clear();
	_submissionCommands.forEach(c -> projectIds.addAll(c.getProjectIds()));
	long submissionCount = getDbSubmissionTermEntryService().getTotalCount(projectIds);

	return regularCount + submissionCount;
    }

    private TransactionLogHandler getTransactionLogHandler() {
	return _transactionLogHandler;
    }

    private void init() {
	ProjectService projectService = getProjectService();

	String regularCollection = getRegularV2Collection();
	String submissionCollection = getSubmissionV2Collection();

	List<String> regularRoutes = getRoutes(regularCollection);
	List<String> submissionRoutes = getRoutes(submissionCollection);

	_regularCommands = new ArrayList<>();
	regularRoutes.forEach(r -> _regularCommands.add(new ReindexCommand(r, getRestoreBatchSize())));

	_submissionCommands = new ArrayList<>();
	submissionRoutes.forEach(r -> _submissionCommands.add(new ReindexCommand(r, getRestoreBatchSize())));

	List<String> projectCodesForReindex = getProjectCodesForReindex();

	List<Long> projectIds = CollectionUtils.isEmpty(projectCodesForReindex)
		? projectService.findAllEnabledProjectIds()
		: projectService.findProjectIdsByShortCodes(projectCodesForReindex);

	projectIds.forEach(p -> {
	    List<Long> regularList = _regularCommands.stream()
		    .filter(c -> c.getRoute().equals(getRoute(regularCollection, p))).findFirst().get().getProjectIds();
	    regularList.add(p);

	    List<Long> submissionList = _submissionCommands.stream()
		    .filter(c -> c.getRoute().equals(getRoute(submissionCollection, p))).findFirst().get()
		    .getProjectIds();
	    submissionList.add(p);
	});

	int nThreads = _regularCommands.size() + _submissionCommands.size();
	_executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(nThreads));
    }

    private PagedListInfo initPageListInfo() {
	PagedListInfo info = new PagedListInfo();
	info.setIndex(0);
	info.setSize(getBatchSize());
	return info;
    }

    private boolean isBufferFull(ReindexCommand command) {
	return command.getBuffer().size() == getRestoreBatchSize();
    }

    private void lockProjectsForRecodeOrClone(Map<Long, String> projectIdShortCodeMap,
	    Set<Long> projectIdsLockedByRecodeOrClone) {
	TransactionLogHandler logHandler = getTransactionLogHandler();

	for (Map.Entry<Long, String> map : projectIdShortCodeMap.entrySet()) {
	    Long projectId = map.getKey();

	    boolean isProjectLocked = logHandler.isLocked(projectId);

	    if (isProjectLocked) {
		String shortCode = map.getValue();
		throw new RuntimeException(String.format(Messages.getString("project.is.locked.m"), shortCode));
	    }
	    logHandler.startAppending(projectId, "admin", "RecodeOrClone", getRegularV2Collection());
	    projectIdsLockedByRecodeOrClone.add(projectId);
	}
    }

    private void notifyPercentage(long count) {
	_percentage = (int) ((count / (float) _totalCount) * 100);
	if (count > 0 && count % getRestoreBatchSize() == 0) {
	    // write to log on every batch
	    LogHelper.info(LOGGER, Messages.getString("RestoreProcessorV2Impl.2"), count); //$NON-NLS-1$
	}
    }

    private void performRestore() throws Exception {
	deleteCollections();

	init();

	_totalCount = getTotalCount();

	runReIndex();

	shutdownAndAwaitTermination();
    }

    private void recodeOrCloneTerms() {
	Set<String> projectShortCodes = getRecodeOrCloneTermsProcessor().recodeOrCloneTerms();
	addWithDuplicateCheck(projectShortCodes);
    }

    private void recodeOrCloneWhileAppRunning(List<RecodeOrCloneCommand> recodeCommands,
	    List<RecodeOrCloneCommand> cloneCommands) {

	Set<String> projectShortCodes = getRecodeOrCloneTermsProcessor().recodeOrCloneTerms(recodeCommands,
		cloneCommands);

	List<String> projectCodesForReindex = getProjectCodesForReindex();

	projectCodesForReindex.clear();

	projectCodesForReindex.addAll(projectShortCodes);
    }

    private Callable<Boolean> restoreRegularBackup(AtomicLong counter, ReindexCommand command,
	    CountDownLatch countDownLatch) {
	return () -> {

	    DbTermEntryService dbTermEntryService = getDbTermEntryService();

	    PagedListInfo info = initPageListInfo();
	    BackupSearchCommand backupSearchCommand = new BackupSearchCommand(command.getProjectIds(), null);

	    PagedList<DbTermEntry> page = dbTermEntryService.getDbTermEntries(info, backupSearchCommand);

	    DbTermEntry[] entries = page.getElements();
	    while (ArrayUtils.isNotEmpty(entries)) {
		for (DbTermEntry entry : entries) {
		    notifyPercentage(counter.get());

		    if (getProjectCodesToSkip().contains(entry.getShortCode())) {
			counter.incrementAndGet();
			// 19-October-2016, as per [Task#TERII-4397]:
			continue;
		    }

		    if (isBufferFull(command)) {
			flushBuffer(getRegularV2Collection(), CommitOption.NONE, command);
		    }

		    SolrInputDocument doc = RestoreRegularConverter.convertFromDbTermEntryToSolrDoc(entry);
		    command.getBuffer().add(doc);
		    counter.incrementAndGet();
		}

		info.setIndex(info.getIndex() + 1);
		page = dbTermEntryService.getDbTermEntries(info, backupSearchCommand);
		entries = page.getElements();
	    }

	    if (!command.getBuffer().isEmpty()) {
		flushBuffer(getRegularV2Collection(), CommitOption.HARD, command);
	    }

	    countDownLatch.countDown();

	    return Boolean.TRUE;
	};
    }

    private Callable<Boolean> restoreSubmissionBackup(AtomicLong counter, ReindexCommand command,
	    CountDownLatch countDownLatch) {
	return () -> {

	    DbSubmissionTermEntryService dbSubmissionTermEntryService = getDbSubmissionTermEntryService();

	    PagedListInfo info = initPageListInfo();
	    BackupSearchCommand backupSearchCommand = new BackupSearchCommand(command.getProjectIds(), null);

	    PagedList<DbSubmissionTermEntry> page = dbSubmissionTermEntryService.getDbSubmissionTermEntries(info,
		    backupSearchCommand);
	    DbSubmissionTermEntry[] entries = page.getElements();

	    while (ArrayUtils.isNotEmpty(entries)) {
		for (DbSubmissionTermEntry entry : entries) {
		    notifyPercentage(counter.get());

		    if (getProjectCodesToSkip().contains(entry.getShortCode())) {
			counter.incrementAndGet();
			// 19-October-2016, as per [Task#TERII-4397]:
			continue;
		    }

		    if (isBufferFull(command)) {
			flushBuffer(getSubmissionV2Collection(), CommitOption.NONE, command);
		    }

		    SolrInputDocument doc = RestoreSubmissionConverter.convertFromDbTermEntryToSolrDoc(entry);
		    command.getBuffer().add(doc);
		    counter.incrementAndGet();
		}

		info.setIndex(info.getIndex() + 1);
		page = dbSubmissionTermEntryService.getDbSubmissionTermEntries(info, backupSearchCommand);
		entries = page.getElements();
	    }

	    if (!command.getBuffer().isEmpty()) {
		flushBuffer(getSubmissionV2Collection(), CommitOption.HARD, command);
	    }

	    countDownLatch.countDown();

	    return Boolean.TRUE;
	};
    }

    private void runReIndex() throws Exception {
	AtomicLong counter = new AtomicLong(0);
	CountDownLatch cdl = new CountDownLatch(_regularCommands.size() + _submissionCommands.size());

	List<Callable<Boolean>> tasks = new ArrayList<>();
	_regularCommands.forEach(c -> tasks.add(restoreRegularBackup(counter, c, cdl)));
	_submissionCommands.forEach(c -> tasks.add(restoreSubmissionBackup(counter, c, cdl)));

	List<ListenableFuture<Boolean>> futures = new ArrayList<>();
	for (Callable<Boolean> taks : tasks) {
	    futures.add(_executorService.submit(taks));
	}

	cdl.await();

	ListenableFuture<List<Boolean>> allAsList = Futures.allAsList(futures);
	allAsList.get();
    }

    private void shutdownAndAwaitTermination() {
	if (Objects.isNull(_executorService)) {
	    return;
	}
	_executorService.shutdown();
	try {
	    awaitTermination();
	} catch (Exception e) {
	    _executorService.shutdownNow();
	    Thread.currentThread().interrupt();
	}
    }

    private void unlockProjects(Set<Long> projectsIdsLockedByRecodeOrClone) {
	PersistentStoreHandler handler = getStoreHandler();
	projectsIdsLockedByRecodeOrClone.forEach(handler::closeAndClear);
    }
}