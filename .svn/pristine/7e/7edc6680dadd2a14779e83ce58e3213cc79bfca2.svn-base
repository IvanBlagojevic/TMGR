package org.gs4tr.termmanager.glossaryV2;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.eventlogging.core.executor.builder.ExceptionEventDataBuilder;
import org.gs4tr.eventlogging.spring.aop.EventLogger;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.glossaryV2.blacklist.update.BlacklistStrategyUpdater;
import org.gs4tr.termmanager.glossaryV2.converter.BilingualTermConverter;
import org.gs4tr.termmanager.glossaryV2.converter.MultiThreadResponseConverter;
import org.gs4tr.termmanager.glossaryV2.logevent.LogEventExecutor;
import org.gs4tr.termmanager.glossaryV2.update.GlossaryStrategyUpdater;
import org.gs4tr.termmanager.model.StringConstants;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.update.ConnectionInfoHolder;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.gs4tr.tm3.api.LiteSegment;
import org.gs4tr.tm3.api.LiteSegmentSearchQuery;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.QueryMatchLocation;
import org.gs4tr.tm3.api.ServerInfo;
import org.gs4tr.tm3.api.TermsHolder;
import org.gs4tr.tm3.api.TmException;
import org.gs4tr.tm3.api.glossary.GlossaryConcordanceQuery;
import org.gs4tr.tm3.api.glossary.Term;
import org.gs4tr.tm3.httpconnector.resolver.model.GlossaryUpdateRequest;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsExceptionValue;
import org.gs4tr.tm3.httpconnector.resolver.model.ResolverContext;
import org.gs4tr.tm3.httpconnector.resolver.model.TmgrKey;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrGlossaryOperations;
import org.gs4tr.tm3.httpconnector.tmgr.TmgrGlossaryHttpConnectionInfo;
import org.springframework.util.StopWatch;

public class GlossaryOperations extends BaseOperations implements ITmgrGlossaryOperations {

    private static final boolean BLACKLIST_SEARCH = false;

    private static final int DEFAULT_THREAD_POOL_SIZE = 2;

    private static final Log LOGGER = LogFactory.getLog(GlossaryOperations.class);

    private static final String SEGMENT_TERM_SEARCH_TASK_ID = "Glossary segment term search";

    private static final String SEGMENT_TERM_SEARCH_TASK_NAME = "executingSegmentSearch";

    private static final String SOLR_SEARCH_TASK_NAME = "executingSolrSearchForSegment: ";

    private static final int THREAD_POOL_SIZE_LIMIT = 5;

    public GlossaryOperations(TmgrKey key, ResolverContext resolverContext, ConnectionInfoHolder infoHolder,
	    TermEntryService termEntryService, GlossaryStrategyUpdater updaterStrategy,
	    BlacklistStrategyUpdater blacklistStrategy, CacheGateway<String, ConnectionInfoHolder> cacheGateway,
	    LogEventExecutor logEventExecutor, int synonymNumber, int segmentSearchThreadPoolSize) {

	super(key, resolverContext, infoHolder, termEntryService, updaterStrategy, blacklistStrategy, cacheGateway,
		logEventExecutor, synonymNumber, segmentSearchThreadPoolSize);

    }

    @Override
    @SuppressWarnings("unchecked")
    // Log Event
    public Page<Term> concordanceSearch(GlossaryConcordanceQuery query) throws OperationsException {
	return (Page<Term>) getLogEventExecutor().logEvent(() -> concordanceSearchExec(query),
		TMGREventActionConstants.ACTION_CONCORDANCE_SEARCH, TMGREventActionConstants.ACTION_TYPE_GLOSSARY_V2);
    }

    @Override
    public ServerInfo connect(TmgrGlossaryHttpConnectionInfo tmgrGlossaryHttpConnectionInfo)
	    throws OperationsException {
	return connectInternal(TMGREventActionConstants.ACTION_TYPE_GLOSSARY_V2);
    }

    @Override
    public void delete(Term term) throws OperationsException {
	deleteTermInternal(term.getId(), term.getSource(), term.getTarget(), term.getSourceLocale().getCode(),
		term.getTargetLocale().getCode(), TMGREventActionConstants.ACTION_TYPE_GLOSSARY_V2);

    }

    @Override
    public void disconnect() throws OperationsException {
	disconnectInternal(TMGREventActionConstants.ACTION_TYPE_GLOSSARY_V2);
    }

    @Override
    public ResolverContext getResolverContext() throws OperationsException {
	return getInternalResolverContext();
    }

    /**
     * @implNote This implementation can be better (i.e segment search for batch of
     *           segments can run faster) because there is exploitable parallelism
     *           within a single batch segment search request. But that isn't a
     *           trivially so avoid premature optimization until it can be measured.
     */
    @Override
    @SuppressWarnings("unchecked")
    // Log Event
    public List<TermsHolder<Term>> segmentSearch(LiteSegmentSearchQuery query) throws OperationsException {
	return (List<TermsHolder<Term>>) getLogEventExecutor().logEvent(() -> segmentSearchExec(query),
		TMGREventActionConstants.ACTION_SEGMENT_SEARCH, TMGREventActionConstants.ACTION_TYPE_GLOSSARY_V2);
    }

    @Override
    // Log Event
    public BatchProcessResult update(GlossaryUpdateRequest request) throws OperationsException {
	return (BatchProcessResult) getLogEventExecutor().logEvent(() -> updateExec(request),
		TMGREventActionConstants.ACTION_ADD_TERM, TMGREventActionConstants.ACTION_TYPE_GLOSSARY_V2);
    }

    private Page<Term> concordanceSearchExec(GlossaryConcordanceQuery query) throws OperationsException {
	addProjectAndLanguageInfosToEventProperties();

	TmgrSearchFilter searchFilter = createConcordanceSearchFilter(query);
	List<TermEntry> termEntries = searchInternal(searchFilter);
	List<Term> bilingualTerms = convertToBilingualTerms(termEntries, TmUserProfile.getCurrentUserName());
	return new Page<>(bilingualTerms.size(), 0, searchFilter.getPageable().getPageSize(), bilingualTerms);
    }

    private List<Term> convertToBilingualTerms(List<TermEntry> termEntries, String username) {
	if (CollectionUtils.isEmpty(termEntries)) {
	    return new ArrayList<>();
	}
	Locale source = Locale.get(getKey().getSource());
	Locale target = Locale.get(getKey().getTarget());

	return termEntries.stream()
		.map(termEntry -> BilingualTermConverter.buildBilingualTermFromTermEntry(termEntry, source, target,
			username, getInfoHolder().getExportableStatuses()))
		.filter(CollectionUtils::isNotEmpty).flatMap(List::stream).collect(toList());
    }

    private int decideNumberOfThreads(int segmentsSize) {
	return segmentsSize >= 500 ? decideSegmentSearchThreadPoolSize() : 1;
    }

    private int decideSegmentSearchThreadPoolSize() {
	int size = getSegmentSearchThreadPoolSize();
	boolean isDefinedSizeOutOfLimits = size < 1 || size > THREAD_POOL_SIZE_LIMIT;
	return isDefinedSizeOutOfLimits ? DEFAULT_THREAD_POOL_SIZE : size;
    }

    private String getStartWatchString(LiteSegment segment, QueryMatchLocation location) {
	String segmentText = getSegmentText(segment, location);
	return Objects.isNull(segmentText) ? StringConstants.EMPTY : segmentText;
    }

    private List<TermsHolder<Term>> multiThreadSearch(int threadPoolSize, QueryMatchLocation location,
	    List<LiteSegment> liteSegments, int length, boolean includeFuzzy, String username)
	    throws OperationsException {

	MultiThreadResponseConverter results = new MultiThreadResponseConverter(liteSegments.size());

	ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

	IntStream.range(0, threadPoolSize).forEach(
		i -> searchAsync(executorService, liteSegments, location, length, includeFuzzy, username, results));

	executorService.shutdown();
	try {
	    executorService.awaitTermination(30, TimeUnit.MINUTES);
	} catch (InterruptedException e) {
	    throw new OperationsException(e.getMessage(), OperationsExceptionValue.EXECUTION);
	}

	return results.getResponseResults();
    }

    private void searchAsync(ExecutorService executorService, List<LiteSegment> liteSegments,
	    QueryMatchLocation location, final int length, final boolean includeFuzzy, String username,
	    MultiThreadResponseConverter results) {
	executorService.execute(() -> {
	    int index;
	    StopWatch watch = new StopWatch();
	    while ((index = results.getNextIndex()) > -1) {
		LiteSegment liteSegment = liteSegments.get(index);
		watch.start(SOLR_SEARCH_TASK_NAME.concat(getSegmentText(liteSegment, location)));
		TmgrSearchFilter filter = createSegmentSearchFilter(liteSegment, location, length, includeFuzzy,
			BLACKLIST_SEARCH);
		filter.setHideBlanksLanguages(new ArrayList<>());
		filter.getHideBlanksLanguages().add(liteSegment.getSourceCode());
		filter.getHideBlanksLanguages().add(liteSegment.getTargetCode());
		List<TermEntry> termEntries = searchSegmentInternal(filter);
		List<Term> terms = convertToBilingualTerms(termEntries, username);
		TermsHolder<Term> holder = new TermsHolder<>(terms);
		watch.stop();
		LogHelper.debug(LOGGER, watch.prettyPrint());
		results.addResponseTermsHolder(index, holder);
	    }
	});
    }

    private List<TermsHolder<Term>> segmentSearchExec(LiteSegmentSearchQuery query) throws OperationsException {

	StopWatch watch = new StopWatch(SEGMENT_TERM_SEARCH_TASK_ID);
	watch.start(SEGMENT_TERM_SEARCH_TASK_NAME);

	addProjectAndLanguageInfosToEventProperties();

	Validate.notNull(query, Messages.getString("GlossaryOperations.0")); //$NON-NLS-1$
	List<LiteSegment> liteSegments = query.getSegments();
	if (CollectionUtils.isEmpty(liteSegments)) {
	    return new ArrayList<>(0);
	}
	QueryMatchLocation location = query.getLocation();
	String username = TmUserProfile.getCurrentUserName();
	boolean includeFuzzy = query.isFuzzySearchEnabled();
	int length = query.getMaxResults();

	watch.stop();
	LogHelper.debug(LOGGER, watch.prettyPrint());

	int threadPoolSize = decideNumberOfThreads(liteSegments.size());

	boolean isMultiThreadAction = threadPoolSize > 1;

	if (isMultiThreadAction) {
	    return multiThreadSearch(threadPoolSize, location, liteSegments, length, includeFuzzy, username);
	} else {
	    return singleThreadSearch(liteSegments, location, length, includeFuzzy, username, watch);
	}

    }

    private List<TermsHolder<Term>> singleThreadSearch(List<LiteSegment> liteSegments, QueryMatchLocation location,
	    int length, boolean includeFuzzy, String username, StopWatch watch) {
	List<TermsHolder<Term>> results = new ArrayList<>();

	for (final LiteSegment ls : liteSegments) {

	    watch.start(SOLR_SEARCH_TASK_NAME.concat(getStartWatchString(ls, location)));

	    TmgrSearchFilter filter = createSegmentSearchFilter(ls, location, length, includeFuzzy, BLACKLIST_SEARCH);
	    filter.setHideBlanksLanguages(new ArrayList<>());
	    filter.getHideBlanksLanguages().add(ls.getSourceCode());
	    filter.getHideBlanksLanguages().add(ls.getTargetCode());
	    List<TermEntry> termEntries = searchSegmentInternal(filter);
	    List<Term> terms = convertToBilingualTerms(termEntries, username);
	    TermsHolder<Term> holder = new TermsHolder<>(terms);
	    results.add(holder);

	    watch.stop();
	    LogHelper.debug(LOGGER, watch.prettyPrint());
	}

	return results;
    }

    private BatchProcessResult updateExec(GlossaryUpdateRequest request) throws OperationsException {

	TmgrKey key = getKey();

	addProjectAndLanguageInfosToEventProperties();

	ConnectionInfoHolder info = getInfoHolder();

	validateWriteAccess(info);

	GlossaryUpdateRequestExt requestExt = new GlossaryUpdateRequestExt(request);
	requestExt.setProjectId(info.getProjectId());
	requestExt.setShortCode(info.getProjectShortCode());
	requestExt.setSourceLanguageId(key.getSource());
	requestExt.setTargetLanguageId(key.getTarget());
	requestExt.setUsername(key.getUser());

	BatchProcessResult result = new BatchProcessResult();

	try {
	    result = getStrategyUpdater().update(requestExt);
	} catch (Exception e) {
	    Map<String, Object> exceptionMap = new ExceptionEventDataBuilder(e).build();
	    exceptionMap.forEach(EventLogger::addProperty);
	    // Exceptions log
	    String message = e.getMessage();
	    result.addErrorMessage(message);
	    if (e instanceof TmException) {
		TmException tmEx = (TmException) e;
		result.setError(tmEx);
	    }
	    LOGGER.error(e);
	}

	return result;
    }

}