package org.gs4tr.termmanager.service.solr.restore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.termmanager.dao.ProjectLanguageDAO;
import org.gs4tr.termmanager.service.CloneTermsService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.RecodeTermsService;
import org.gs4tr.termmanager.service.solr.restore.model.RecodeOrCloneCommand;
import org.gs4tr.termmanager.service.solr.restore.model.RecodeOrCloneCommandParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

@Component("recodeOrCloneTermsProcessor")
@PropertySource(value = "environment:locale.properties")
public class RecodeOrCloneTermsProcessorImpl implements RecodeOrCloneTermsProcessor {

    private static final Log LOGGER = LogFactory.getLog(RecodeOrCloneTermsProcessorImpl.class);

    private static final String TASK = "RECODE_OR_CLONE";

    @Autowired
    private CloneTermsService _cloneTermsService;

    @Autowired
    private Environment _environment;

    private ListeningExecutorService _executorService;

    @Autowired
    private ProjectLanguageDAO _projectLanguageDAO;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private RecodeTermsService _recodeTermsService;

    @PostConstruct
    public void init() {
	_executorService = MoreExecutors
		.listeningDecorator(Executors.newFixedThreadPool(getThreadNumberForRecodeOrClone()));
    }

    @Override
    public void initAndValidateCommands(List<RecodeOrCloneCommand> recodeCommands,
	    List<RecodeOrCloneCommand> cloneCommands, String projectsToRecode, String projectsToClone) {

	initCommands(recodeCommands, cloneCommands, projectsToRecode, projectsToClone);
	validateCommands(recodeCommands, cloneCommands);
    }

    @Override
    public boolean isRebuildByProjectShortCodes() {
	Environment env = getEnvironment();
	String restoreOnlyFromCommand = env.getProperty("rebuild.by.project.shortcodes");
	return Boolean.parseBoolean(restoreOnlyFromCommand);
    }

    @Override
    public Set<String> recodeOrCloneTerms(List<RecodeOrCloneCommand> recodeCommands,
	    List<RecodeOrCloneCommand> cloneCommands) {
	StopWatch watch = new StopWatch(TASK);
	watch.start(TASK);

	recodeOrClone(recodeCommands, cloneCommands);

	watch.stop();
	LogHelper.info(LOGGER, watch.prettyPrint());

	return collectProjectShortCodes(recodeCommands, cloneCommands);
    }

    @Override
    public Set<String> recodeOrCloneTerms() {

	Environment env = getEnvironment();

	String projectsToRecode = env.getProperty("index.project.for.recode");
	String projectsToClone = env.getProperty("index.project.for.clone");

	return performRecodeOrCloneOperation(projectsToRecode, projectsToClone);

    }

    private void clone(RecodeOrCloneCommand command) {
	CloneTermsService service = getCloneTermsService();
	int dummyTermsCount = service.cloneTerms(command);
	service.cloneProjectLanguageDetail(command, dummyTermsCount);
	service.cloneProjectDetail(command);
	service.cloneProjectUserLanguage(command);
	service.cloneProjectLanguage(command);
    }

    private void collectCommandsForParallelExec(List<RecodeOrCloneCommand> commandsForParallelExec,
	    List<RecodeOrCloneCommand> commands) {

	int numOfThreads = getThreadNumberForRecodeOrClone();

	while (commandsForParallelExec.size() <= numOfThreads && !commands.isEmpty()) {
	    RecodeOrCloneCommand nextCommand = getNextCommand(commands, commandsForParallelExec);

	    if (Objects.nonNull(nextCommand)) {
		commandsForParallelExec.add(nextCommand);
	    } else {
		break;
	    }
	}
    }

    private Set<String> collectProjectShortCodes(List<RecodeOrCloneCommand> recodeCommands,
	    List<RecodeOrCloneCommand> cloneCommands) {
	Set<String> projectShortCodes = new HashSet<>();

	recodeCommands.forEach(command -> projectShortCodes.add(command.getProjectShortCode()));
	cloneCommands.forEach(command -> projectShortCodes.add(command.getProjectShortCode()));

	return projectShortCodes;
    }

    private void compareCommandByLocaleFrom(List<RecodeOrCloneCommand> commands, int index, String exceptionMessage) {
	for (int j = index + 1; j < commands.size(); j++) {

	    String locale1 = commands.get(index).getLocaleFrom();
	    String locale2 = commands.get(j).getLocaleFrom();

	    Long projectId1 = commands.get(index).getProjectId();
	    Long projectId2 = commands.get(j).getProjectId();

	    boolean isSameLanguage = locale1.equals(locale2);
	    boolean isSameProject = projectId1.equals(projectId2);

	    if (isSameLanguage && isSameProject) {
		throw new RuntimeException(String.format(exceptionMessage, locale1));
	    }
	}
    }

    private void compareCommandByLocaleTo(List<RecodeOrCloneCommand> commands, int index, String exceptionMessage) {
	for (int j = index + 1; j < commands.size(); j++) {

	    String locale1 = commands.get(index).getLocaleTo();
	    String locale2 = commands.get(j).getLocaleTo();

	    Long projectId1 = commands.get(index).getProjectId();
	    Long projectId2 = commands.get(j).getProjectId();

	    boolean isSameLanguage = locale1.equals(locale2);
	    boolean isSameProject = projectId1.equals(projectId2);

	    if (isSameLanguage && isSameProject) {
		throw new RuntimeException(String.format(exceptionMessage, locale1));
	    }
	}
    }

    private void execParallelRecodeOrClone(Consumer<RecodeOrCloneCommand> commandConsumer,
	    List<RecodeOrCloneCommand> commandsForParallelExec) throws Exception {

	CountDownLatch cdl = new CountDownLatch(commandsForParallelExec.size());

	List<ListenableFuture<RecodeOrCloneCommand>> futures = new ArrayList<>();

	for (RecodeOrCloneCommand command : commandsForParallelExec) {

	    ListenableFuture<RecodeOrCloneCommand> future = _executorService
		    .submit(getRecodeOrCloneCallable(commandConsumer, command, cdl));

	    Futures.addCallback(future, new FutureCallback<>() {
		@Override
		public void onFailure(Throwable throwable) {
		    LOGGER.error(throwable.getMessage(), throwable);
		    throw new RuntimeException(throwable.getMessage(), throwable);
		}

		@Override
		public void onSuccess(RecodeOrCloneCommand recodeOrCloneCommand) {
		    LOGGER.info(recodeOrCloneCommand.toString());
		}
	    });
	    futures.add(future);
	}

	cdl.await();

	ListenableFuture<List<RecodeOrCloneCommand>> allAsList = Futures.allAsList(futures);

	try {
	    allAsList.get();
	} catch (Exception e) {
	    throw new RuntimeException("Recode or clone failed");
	}

    }

    private RecodeOrCloneCommand findCommandWithDifferentProjectId(List<RecodeOrCloneCommand> commands,
	    List<RecodeOrCloneCommand> commandsForParallelExec) {

	for (RecodeOrCloneCommand command : commands) {

	    Optional<RecodeOrCloneCommand> commandOptional = commandsForParallelExec.stream()
		    .filter(c -> command.getProjectId().equals(c.getProjectId())).findFirst();

	    if (commandOptional.isPresent()) {
		continue;
	    }

	    return command;
	}

	return null;
    }

    private CloneTermsService getCloneTermsService() {
	return _cloneTermsService;
    }

    private Environment getEnvironment() {
	return _environment;
    }

    private RecodeOrCloneCommand getNextCommand(List<RecodeOrCloneCommand> commands,
	    List<RecodeOrCloneCommand> commandsForParallelExec) {

	RecodeOrCloneCommand nextCommand = findCommandWithDifferentProjectId(commands, commandsForParallelExec);

	if (Objects.nonNull(nextCommand)) {
	    commands.remove(nextCommand);
	}

	return nextCommand;
    }

    private ProjectLanguageDAO getProjectLanguageDAO() {
	return _projectLanguageDAO;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private Callable<RecodeOrCloneCommand> getRecodeOrCloneCallable(Consumer<RecodeOrCloneCommand> commandConsumer,
	    RecodeOrCloneCommand command, CountDownLatch cdl) {
	return () -> {

	    try {
		commandConsumer.accept(command);
	    } finally {
		cdl.countDown();
	    }
	    return command;

	};

    }

    private RecodeTermsService getRecodeTermsService() {
	return _recodeTermsService;
    }

    private int getThreadNumberForRecodeOrClone() {
	Environment env = getEnvironment();
	String threadNumberString = env.getProperty("thread.number.recode.clone");

	if (StringUtils.isEmpty(threadNumberString)) {
	    return 5;
	}
	return Integer.parseInt(threadNumberString);
    }

    @SuppressWarnings("unchecked")
    private void initCommands(List<RecodeOrCloneCommand> recodeCommands, List<RecodeOrCloneCommand> cloneCommands,
	    String projectsToRecode, String projectsToClone) {

	List<Long> disabledProjects = getProjectService().findAllDisabledProjectIds();

	RecodeOrCloneCommandParser parser = new RecodeOrCloneCommandParser(getProjectService(), getProjectLanguageDAO(),
		disabledProjects);

	parser.setAsText(projectsToRecode);
	recodeCommands.addAll((List<RecodeOrCloneCommand>) parser.getValue());

	parser.setAsText(projectsToClone);
	cloneCommands.addAll((List<RecodeOrCloneCommand>) parser.getValue());
    }

    private Set<String> performRecodeOrCloneOperation(String projectsToRecode, String projectsToClone) {
	List<RecodeOrCloneCommand> recodeCommands = new ArrayList<>();
	List<RecodeOrCloneCommand> cloneCommands = new ArrayList<>();

	StopWatch watch = new StopWatch(TASK);
	watch.start(TASK);

	initAndValidateCommands(recodeCommands, cloneCommands, projectsToRecode, projectsToClone);

	recodeOrClone(recodeCommands, cloneCommands);

	watch.stop();
	LogHelper.info(LOGGER, watch.prettyPrint());

	return collectProjectShortCodes(recodeCommands, cloneCommands);
    }

    private void recode(RecodeOrCloneCommand command) {
	RecodeTermsService service = getRecodeTermsService();
	service.recodeProjectUserLanguage(command);
	service.recodeProjectLanguage(command);
	service.recodeProjectLanguageDetail(command);
	service.recodeTermEntriesHistories(command);
	service.recodeTerms(command);
	service.recodeSubmission(command);
	service.recodeSubmissionTermEntriesHistories(command);
	service.recodeSubmissionTerms(command);
	service.recodeSubmissionLanguages(command);
    }

    private void recodeOrClone(List<RecodeOrCloneCommand> recodeCommands, List<RecodeOrCloneCommand> cloneCommands) {

	List<RecodeOrCloneCommand> cloneCommandsCopy = new ArrayList<>(cloneCommands);
	List<RecodeOrCloneCommand> recodeCommandsCopy = new ArrayList<>(recodeCommands);

	try {
	    recodeOrCloneParallel(cloneCommandsCopy, this::clone);
	    recodeOrCloneParallel(recodeCommandsCopy, this::recode);
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage(), e);
	}

    }

    private void recodeOrCloneParallel(List<RecodeOrCloneCommand> commands,
	    Consumer<RecodeOrCloneCommand> commandConsumer) throws Exception {

	int numOfThreads = getThreadNumberForRecodeOrClone();

	/*
	 * List of commands for parallel execution should have different projectIds.
	 * Parallel recode or clone is possible only on different projects.
	 */
	List<RecodeOrCloneCommand> commandsForParallelExec = new ArrayList<>(numOfThreads);

	while (!commands.isEmpty()) {

	    collectCommandsForParallelExec(commandsForParallelExec, commands);

	    if (!commandsForParallelExec.isEmpty()) {

		execParallelRecodeOrClone(commandConsumer, commandsForParallelExec);

		commandsForParallelExec.clear();

	    }

	}
    }

    private void validateCommands(List<RecodeOrCloneCommand> recodeCommands, List<RecodeOrCloneCommand> cloneCommands) {

	String cloneExceptionMessageTo = "Multiple clone commands are with the same languageTo: [%s]";
	validateCommandsByLanguageTo(cloneCommands, cloneExceptionMessageTo);

	String recodeExceptionMessageTo = "Multiple recode commands are with the same languageTo: [%s]";
	String recodeExceptionMessageFrom = "Multiple recode commands are with the same languageFrom: [%s]";

	validateCommandsByLanguageTo(recodeCommands, recodeExceptionMessageTo);
	validateCommandsByLanguageFrom(recodeCommands, recodeExceptionMessageFrom);

	validateIfSameCloneRecodeLanguageTo(recodeCommands, cloneCommands);
    }

    private void validateCommandsByLanguageFrom(List<RecodeOrCloneCommand> commands, String exceptionMessage) {
	for (int i = 0; i < commands.size(); i++) {
	    compareCommandByLocaleFrom(commands, i, exceptionMessage);
	}
    }

    private void validateCommandsByLanguageTo(List<RecodeOrCloneCommand> commands, String exceptionMessage) {
	for (int i = 0; i < commands.size(); i++) {
	    compareCommandByLocaleTo(commands, i, exceptionMessage);
	}
    }

    private void validateIfRecodeContainsCloneLanguageTo(List<RecodeOrCloneCommand> cloneCommands,
	    RecodeOrCloneCommand recodeCommand) {

	Long recodeProjectId = recodeCommand.getProjectId();
	String recodeLocaleTo = recodeCommand.getLocaleTo();

	for (RecodeOrCloneCommand cloneCommand : cloneCommands) {
	    boolean isSameProject = cloneCommand.getProjectId().equals(recodeProjectId);
	    boolean isSameLanguageTo = cloneCommand.getLocaleTo().equals(recodeLocaleTo);

	    if (isSameProject && isSameLanguageTo) {
		String exceptionMessage = "Recode and clone command have the same localeTo: [%s]  on the project: [%s]";
		throw new RuntimeException(String.format(exceptionMessage, recodeCommand.getLocaleTo(),
			recodeCommand.getProjectShortCode()));
	    }
	}
    }

    private void validateIfSameCloneRecodeLanguageTo(List<RecodeOrCloneCommand> recodeCommands,
	    List<RecodeOrCloneCommand> cloneCommands) {
	recodeCommands.forEach(command -> validateIfRecodeContainsCloneLanguageTo(cloneCommands, command));
    }
}
