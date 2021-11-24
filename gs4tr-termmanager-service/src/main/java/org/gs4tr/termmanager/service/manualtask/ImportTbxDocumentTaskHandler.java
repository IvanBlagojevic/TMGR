package org.gs4tr.termmanager.service.manualtask;

import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.gs4tr.termmanager.model.EntityTypeHolder.TERMENTRY;
import static org.gs4tr.termmanager.service.file.analysis.request.Context.Builder;
import static org.gs4tr.termmanager.service.utils.TaskHandlerConstants.PROJECTS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.eventlogging.spring.aop.EventLogger;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Task;
import org.gs4tr.foundation.modules.entities.model.TaskPriority;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.UserMessageTypeEnum;
import org.gs4tr.foundation.modules.webmvc.filters.model.RefreshUserContext;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.AttributeLevelEnum;
import org.gs4tr.termmanager.model.ImportErrorAction;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.ImportTypeEnum;
import org.gs4tr.termmanager.model.ProjectLanguage;
import org.gs4tr.termmanager.model.ProjectTerminologyCounts;
import org.gs4tr.termmanager.model.SystemTask;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.ImportAttributeReplacement;
import org.gs4tr.termmanager.model.dto.TmProjectDto;
import org.gs4tr.termmanager.model.dto.TmProjectDto.TermStatusDto;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.persistence.update.DescriptionImportOption;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.ImportTermService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.StatisticsService;
import org.gs4tr.termmanager.service.concurrency.RunnableCallback;
import org.gs4tr.termmanager.service.concurrency.ServiceThreadPoolHandler;
import org.gs4tr.termmanager.service.file.analysis.FilesAnalysisRequestHandler;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level;
import org.gs4tr.termmanager.service.file.analysis.request.FilesAnalysisRequest;
import org.gs4tr.termmanager.service.file.manager.FileManager;
import org.gs4tr.termmanager.service.impl.ImportProgressInfo;
import org.gs4tr.termmanager.service.lock.manager.ExclusiveWriteLockManager;
import org.gs4tr.termmanager.service.logging.util.EventContextConstants;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.termmanager.service.model.command.ImportCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoImportCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.project.terminology.counts.ProjectTerminologyCountsProvider;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;
import org.gs4tr.termmanager.service.utils.AdminTasksHolderHelper;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.termmanager.service.utils.TermEntryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;

@SystemTask(priority = TaskPriority.LEVEL_NINE)
public class ImportTbxDocumentTaskHandler extends AbstractManualTaskHandler {

    private static final String ANALYSIS_PROCESSING_ID = "analysisProcessingId";

    private static final String ASYNC_OPERATION_NAME = "ImportTermServiceImpl.importDocument"; //$NON-NLS-1$

    private static final String DEFAULT_IMPORT_TYPE = "defaultImportType"; //$NON-NLS-1$

    private static final String IGNORE_CASE = "ignoreCase"; //$NON-NLS-1$

    private static final String IMPORT_THREAD_NAMES = "importThreadNames"; //$NON-NLS-1$

    private static final List<SyncOption> IMPORT_TYPES = Arrays.asList(SyncOption.APPEND, SyncOption.OVERWRITE);

    private static final String IMPORT_TYPES_KEY = "importTypes"; //$NON-NLS-1$

    private static final String LANGUAGES_ADDED_KEY = "languagesAdded"; //$NON-NLS-1$

    private static final String LOCK_KEY = "add_new_languages_lock_key"; //$NON-NLS-1$

    private static final Log LOGGER = LogFactory.getLog(ImportTbxDocumentTaskHandler.class);

    private static final String TOTAL_NUMBER_OF_TERM_ENTRIES = "totalNumberOfTermEntries"; //$NON-NLS-1$

    @Autowired
    private FilesAnalysisRequestHandler _analysisRequestHandler;

    @Autowired
    @Qualifier("guavaCacheGateway")
    private CacheGateway<String, ImportProgressInfo> _cacheGateway;

    @Autowired
    private ExclusiveWriteLockManager _exclusiveWriteLockManager;

    @Autowired
    private ImportTermService _importTermService;

    @Autowired
    private FileManager _manager;

    private String _notificationClassifier;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private StatisticsService _statisticsService;

    @Autowired
    private AdminTasksHolderHelper _tasksHolderHelper;

    @Autowired
    private ProjectTerminologyCountsProvider _terminologyCountsProvider;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoImportCommand.class;
    }

    public String getNotificationClassifier() {
	return _notificationClassifier;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {
	TaskModel taskModel = new TaskModel();
	taskModel.addObject(PROJECTS, getTmProjectDtos(taskName));
	taskModel.addObject(IMPORT_TYPES_KEY, IMPORT_TYPES);
	taskModel.addObject(DEFAULT_IMPORT_TYPE, SyncOption.OVERWRITE);
	taskModel.addObject(IGNORE_CASE, Boolean.TRUE);
	return new TaskModel[] { taskModel };
    }

    @RefreshUserContext
    @Override
    @LogEvent(action = TMGREventActionConstants.ACTION_IMPORT, actionCategory = TMGREventActionConstants.CATEGORY_UI_CONTROLLER)
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    final List<UploadedRepositoryItem> items) {

	final ImportCommand importCommand = (ImportCommand) command;
	return importCommand.isPreImportCheck() ? startAnalysis(importCommand) : startImport(importCommand);
    }

    public void setNotificationClassifier(String notificationClassifier) {
	_notificationClassifier = notificationClassifier;
    }

    @LogEvent(action = TMGREventActionConstants.ACTION_IMPORT, actionCategory = TMGREventActionConstants.CATEGORY_UI_CONTROLLER)
    public TaskResponse startImport(final ImportCommand importCommand) {
	final Long projectId = importCommand.getProjectId();
	if (getImportTermService().isLocked(projectId)) {
	    throw new UserException(MessageResolver.getMessage("project.is.locked.m"),
		    MessageResolver.getMessage("project.is.locked"), UserMessageTypeEnum.WARNING);
	}

	final String sourceLanguage = importCommand.getSourceLanguage();
	EventLogger.addProperty(EventContextConstants.SOURCE_LANGUAGE, sourceLanguage);

	Map<String, List<String>> importLanguagesPerFile = importCommand.getImportLanguagesPerFile();

	final List<String> importLanguageCodes = new ArrayList<>();
	importLanguagesPerFile.forEach((key, value) -> importLanguageCodes.addAll(value));

	EventLogger.addProperty(EventContextConstants.TARGET_LANGUAGES, importLanguageCodes);

	EventLogger.addProperty(EventContextConstants.PROJECT_ID, projectId);

	TmProject project = getProjectService().findProjectById(projectId, Attribute.class);
	EventLogger.addProperty(EventContextConstants.PROJECT_NAME, project.getProjectInfo().getName());
	EventLogger.addProperty(EventContextConstants.PROJECT_SHORT_CODE, project.getProjectInfo().getShortCode());

	final String folder = importCommand.getFolder();
	Validate.notEmpty(folder, Messages.getString("ImportTbxDocumentTaskHandler.7")); //$NON-NLS-1$

	Map<String, Integer> numberOfTermEntriesByFileName = importCommand.getNumberOfTermEntriesByFileName();

	List<File> files = new ArrayList<>();
	try {
	    Set<String> fileNames = numberOfTermEntriesByFileName.keySet();
	    files.addAll(getFileManager().read(fileNames, folder));
	} catch (IOException e) {
	    LogHelper.error(LOGGER, e.getMessage(), e);
	    throw new UserException(String.format(Messages.getString("ImportTbxDocumentTaskHandler.15"), folder), e); //$NON-NLS-1$
	}

	EventLogger.addProperty(EventContextConstants.NUMBER_OF_FILES, files.size());

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	Set<String> userProjectLanguageCodes = userProfile.getProjectUserLanguages().get(projectId);

	Set<String> languages = getImportLanguagesApplyReplacements(importCommand);

	List<String> newLanguageCodes = new ArrayList<>(languages);
	newLanguageCodes.removeAll(userProjectLanguageCodes);

	List<ProjectLanguage> newProjectLanguages = new ArrayList<>(newLanguageCodes.size());
	for (final String newLanguageCode : newLanguageCodes) {
	    ProjectLanguage projectLanguage = new ProjectLanguage();
	    projectLanguage.setLanguage(newLanguageCode);
	    newProjectLanguages.add(projectLanguage);
	}
	addNewLanguages(newProjectLanguages, projectId, userProfile.getUserName());
	addOrUpdateProjectAttributes(importCommand);

	final String defaultStatus = resolveDefaultTermStatus(importCommand, project);

	ProjectTerminologyCounts preImportCounts = getTerminologyCountsProvider().getProjectTerminologyCounts(projectId,
		new ArrayList<>(languages));

	AtomicInteger totalNumberOfTermEntries = new AtomicInteger(0);

	List<String> importIdentifiers = new ArrayList<>();

	Map<String, ImportProgressInfo> importProgressInfoPerFile = new HashMap<>();

	files.forEach(f -> {
	    String fileName = f.getName();
	    importIdentifiers.add(fileName);
	    totalNumberOfTermEntries.addAndGet(numberOfTermEntriesByFileName.get(fileName));

	    ImportProgressInfo importProgressInfo = new ImportProgressInfo(numberOfTermEntriesByFileName.get(fileName));
	    ImportSummary importSummary = importProgressInfo.getImportSummary();
	    importSummary.setStartTime(System.currentTimeMillis());
	    importSummary.setPreImportCounts(preImportCounts);
	    importSummary.setImportId(fileName);

	    getCacheGateway().put(CacheName.IMPORT_PROGRESS_STATUS, fileName, importProgressInfo);

	    importProgressInfoPerFile.put(fileName, importProgressInfo);
	});

	startImportAsync(files, importCommand, defaultStatus, project, importProgressInfoPerFile);

	return prepareResponseData(projectId, importIdentifiers, totalNumberOfTermEntries.get(), newProjectLanguages);
    }

    private void addNewLanguages(List<ProjectLanguage> newLanguages, Long projectId, String username) {
	if (CollectionUtils.isEmpty(newLanguages)) {
	    return;
	}
	getExclusiveWriteLockManager().acquireLock(LOCK_KEY, username);
	try {
	    getProjectService().addNewLanguagesOnImport(projectId, newLanguages);
	} finally {
	    getExclusiveWriteLockManager().releaseLock(LOCK_KEY, username);
	}
    }

    private void addOrUpdateProjectAttributes(final ImportCommand importCommand) {
	applyAttributeReplacements(importCommand);

	final Set<String> termAttributes = importCommand.getTermAttributeNames(),
		termEntryAttributes = importCommand.getTermEntryAttributeNames(),
		termNotes = importCommand.getTermNoteNames();

	final Long projectId = importCommand.getProjectId();
	/*
	 * [21-March-2017], since TMGR 5.0 we does not take into account
	 * DescriptionImportOption (i.e that option does not exist on import dialogue
	 * anymore)
	 */
	if (CollectionUtils.isNotEmpty(termEntryAttributes) || CollectionUtils.isNotEmpty(termAttributes)) {
	    EnumMap<AttributeLevelEnum, Set<String>> attributesByLevel = new EnumMap<>(AttributeLevelEnum.class);
	    attributesByLevel.put(AttributeLevelEnum.TERMENTRY, termEntryAttributes);
	    attributesByLevel.put(AttributeLevelEnum.LANGUAGE, termAttributes);
	    getProjectService().addOrUpdateProjectAttributesOnImport(projectId, attributesByLevel,
		    importCommand.isIgnoreCase());
	}

	if (CollectionUtils.isNotEmpty(termNotes)) {
	    getProjectService().addOrUpdateProjectNotesOnImport(projectId, termNotes, importCommand.isIgnoreCase());
	}
    }

    private void applyAttributeReplacements(final ImportCommand importCommand) {
	final Set<String> termAttributes = importCommand.getTermAttributeNames(),
		termEntryAttributes = importCommand.getTermEntryAttributeNames(),
		termNotes = importCommand.getTermNoteNames();

	List<ImportAttributeReplacement> replacements = importCommand.getAttributeReplacements();

	for (ImportAttributeReplacement attributeReplacement : replacements) {
	    String attributeName = attributeReplacement.getImportAttributeName();
	    String replacement = attributeReplacement.getReplacement();
	    String level = attributeReplacement.getLevel();
	    if (Level.TERM_ENTRY.name().equals(level)) {
		termEntryAttributes.remove(attributeName);
		termEntryAttributes.add(replacement);
	    } else if (Level.TERM_ATTRIBUTE.name().equals(level)) {
		termAttributes.remove(attributeName);
		termAttributes.add(replacement);
	    } else if (Level.TERM_NOTE.name().equals(level)) {
		termNotes.remove(attributeName);
		termNotes.add(replacement);
	    }
	}
    }

    private ImportOptionsModel createImportOptionsModel(String fileName, ImportCommand command, TmProject project,
	    String defaultStatus) {

	ImportOptionsModel importOptions = new ImportOptionsModel();
	importOptions.setDescriptionImportOption(DescriptionImportOption.IMPORT_ONLY_SELECTED);

	Map<Level, Set<String>> attributesByLevel = ManualTaskHandlerUtils
		.groupAttributesByLevel(project.getAttributes());

	Set<String> allowedTermEntryAttributes = ManualTaskHandlerUtils.resolveDescriptionTypes(
		command.getTermEntryAttributeNames(), attributesByLevel.get(Level.TERM_ENTRY), command.isIgnoreCase());
	Set<String> allowedTermAttributes = ManualTaskHandlerUtils.resolveDescriptionTypes(
		command.getTermAttributeNames(), attributesByLevel.get(Level.TERM_ATTRIBUTE), command.isIgnoreCase());
	Set<String> allowedTermNotes = ManualTaskHandlerUtils.resolveDescriptionTypes(command.getTermNoteNames(),
		attributesByLevel.get(Level.TERM_NOTE), command.isIgnoreCase());

	Map<String, Set<String>> allowedTermDescriptions = new HashMap<>(2);
	allowedTermDescriptions.put(Description.ATTRIBUTE, allowedTermAttributes);
	allowedTermDescriptions.put(Description.NOTE, allowedTermNotes);

	/*
	 * [19-May-2017] Since 5.0, user can choose which term entry/term attribute(s)
	 * or term note(s) to import.
	 */
	importOptions.setAllowedTermEntryAttributes(allowedTermEntryAttributes);
	importOptions.setAllowedTermDescriptions(allowedTermDescriptions);

	Map<String, Map<String, String>> attributeNoteReplacements = new HashMap<>(2);
	Map<String, String> termEntryAttributeReplacements = new HashMap<>();

	for (ImportAttributeReplacement attributeReplacement : command.getAttributeReplacements()) {
	    String importAttributeName = attributeReplacement.getImportAttributeName();
	    String replacement = attributeReplacement.getReplacement();
	    final String level = attributeReplacement.getLevel();

	    if (Level.TERM_ENTRY.name().equals(level)) {
		termEntryAttributeReplacements.put(importAttributeName, replacement);
	    } else if (Level.TERM_ATTRIBUTE.name().equals(level)) {
		attributeNoteReplacements.computeIfAbsent(Description.ATTRIBUTE, key -> new HashMap<>())
			.put(importAttributeName, replacement);
	    } else if (Level.TERM_NOTE.name().equals(level)) {
		attributeNoteReplacements.computeIfAbsent(Description.NOTE, key -> new HashMap<>())
			.put(importAttributeName, replacement);
	    }
	}
	importOptions.setTermEntryAttributeReplacements(termEntryAttributeReplacements);
	importOptions.setAttributeNoteReplacements(attributeNoteReplacements);
	/*
	 * [21-April-2017], According to spec, term entries missing source language
	 * terms should be imported as new entries.
	 */
	importOptions.setImportErrorAction(ImportErrorAction.IMPORT_TRANSLATIONS);
	importOptions.setSyncLanguageId(command.getSourceLanguage());

	importOptions.setLanguageReplacementByCode(new HashMap<>(command.getLanguageReplacementByCode()));

	List<String> importLanguages = new ArrayList<>(command.getImportLanguagesPerFile().get(fileName));
	importOptions.setImportLocales(importLanguages);

	importOptions.setProjectId(project.getProjectId());
	importOptions.setProjectName(project.getProjectInfo().getName());
	importOptions.setProjectShortCode(project.getProjectInfo().getShortCode());
	importOptions.setStatus(defaultStatus);
	importOptions.setIgnoreCase(command.isIgnoreCase());
	importOptions.setOverwriteByTermEntryId(command.isFinalyOverwriteByTermEntryId());
	importOptions.setSynonymNumber(getSynonymNumber());

	return importOptions;
    }

    private void executeImport(InputStream stream, ImportOptionsModel importOptions, ImportTypeEnum importType,
	    String fileEncoding, ImportProgressInfo importProgressInfo, SyncOption syncOption) throws Exception {

	ImportSummary importSummary = getImportTermService().importDocument(stream, importProgressInfo, importOptions,
		importType, fileEncoding, syncOption);
	getImportTermService().updateProjectDetailOnImport(importOptions, importSummary);
	getStatisticsService().updateStatisticsOnImport(importOptions.getProjectId(), importSummary);
    }

    private FilesAnalysisRequestHandler getAnalysisRequestHandler() {
	return _analysisRequestHandler;
    }

    private CacheGateway<String, ImportProgressInfo> getCacheGateway() {
	return _cacheGateway;
    }

    private ExclusiveWriteLockManager getExclusiveWriteLockManager() {
	return _exclusiveWriteLockManager;
    }

    private FileManager getFileManager() {
	return _manager;
    }

    private Set<String> getImportLanguagesApplyReplacements(ImportCommand command) {
	Map<String, List<String>> importLanguagesPerFile = command.getImportLanguagesPerFile();

	List<String> importLanguages = new ArrayList<>();

	importLanguagesPerFile.forEach((k, v) -> importLanguages.addAll(v));

	Map<String, String> languageReplacements = command.getLanguageReplacementByCode();
	if (MapUtils.isNotEmpty(languageReplacements)) {
	    for (Entry<String, String> entry : languageReplacements.entrySet()) {
		final String importLanguage = entry.getKey();
		final String replacement = entry.getValue();
		LogHelper.debug(LOGGER, String.format(Messages.getString("ImportTbxDocumentTaskHandler.6"), //$NON-NLS-1$
			importLanguage, replacement));

		int index = importLanguages.indexOf(importLanguage);
		importLanguages.set(index, replacement);
	    }
	}
	return new HashSet<>(importLanguages);
    }

    private ImportTermService getImportTermService() {
	return _importTermService;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private Set<String> getProjectUserLanguages(final Long projectId) {
	return nullSafeCopy(TmUserProfile.getCurrentUserProfile().getProjectUserLanguages().get(projectId));
    }

    private StatisticsService getStatisticsService() {
	return _statisticsService;
    }

    private AdminTasksHolderHelper getTasksHolderHelper() {
	return _tasksHolderHelper;
    }

    private ProjectTerminologyCountsProvider getTerminologyCountsProvider() {
	return _terminologyCountsProvider;
    }

    private List<TmProjectDto> getTmProjectDtos(final String taskName) {
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	List<TmProject> projects = getProjectService().getUserProjects(userProfile.getUserProfileId());

	final Task task = new Task(taskName);

	List<TmProjectDto> tmProjectDtos = new ArrayList<>(projects.size());
	for (final TmProject project : projects) {
	    final Long projectId = project.getProjectId();
	    List<Task> tasksList = getTasksHolderHelper().getSystemEntityTasks(projectId, null, TERMENTRY);

	    if (tasksList.contains(task)) {
		TmProjectDto tmProjectDto = new TmProjectDto(project.getProjectInfo().getName(), projectId);
		Set<String> userLanguageCodes = userProfile.getProjectUserLanguages().get(projectId);
		for (String languageCode : userLanguageCodes) {
		    tmProjectDto.addLanguage(languageCode);
		}

		tmProjectDto.setDefaultTermStatus(new TermStatusDto(ServiceUtils.decideTermStatus(project)));

		List<TermStatusDto> userPolicies = ServiceUtils.collectUserPolicies(userProfile, projectId);
		if (userPolicies.size() > 1) {
		    tmProjectDto.getTermStatuses().addAll(userPolicies);
		}
		tmProjectDtos.add(tmProjectDto);
	    }
	}
	return tmProjectDtos;
    }

    private Set<String> nullSafeCopy(final Set<String> set) {
	return set == null ? emptySet() : new HashSet<>(set);
    }

    private TaskResponse prepareResponseData(Long projectId, List<String> importIdentifiers,
	    int totalNumberOfTermEntries, List<ProjectLanguage> newProjectLanguages) {
	TaskResponse taskResponse = new TaskResponse(new Ticket(projectId));
	taskResponse.addObject(IMPORT_THREAD_NAMES, importIdentifiers);
	taskResponse.addObject(TOTAL_NUMBER_OF_TERM_ENTRIES, totalNumberOfTermEntries);
	taskResponse.addObject(LANGUAGES_ADDED_KEY, !newProjectLanguages.isEmpty());
	return taskResponse;
    }

    private String resolveDefaultTermStatus(ImportCommand importCommand, TmProject project) {
	final String status = importCommand.getDefaultTermStatus();
	return StringUtils.isNotEmpty(status) ? status : ServiceUtils.decideTermStatus(project).getName();
    }

    private TaskResponse startAnalysis(final ImportCommand command) {
	Long projectId = command.getProjectId();
	String folder = command.getFolder();

	Map<String, Set<String>> comboValuesPerAttribute = TermEntryUtils
		.getComboValuesPerAttribute(getProjectService(), projectId);

	Map<Long, List<Attribute>> attributes = getProjectService().findAttributesByProjectId(singletonList(projectId));
	List<Attribute> projectAttributes = attributes.get(projectId);

	Builder contextBuilder = new Builder(getProjectUserLanguages(projectId), command.isIgnoreCase());
	contextBuilder.sourceLanguage(command.getSourceLanguage());
	contextBuilder.comboValuesPerAttribute(comboValuesPerAttribute);
	contextBuilder.attributesByLevel(ManualTaskHandlerUtils.groupAttributesByLevel(projectAttributes));

	FilesAnalysisRequest request = new FilesAnalysisRequest(folder, contextBuilder.build());

	getAnalysisRequestHandler().handleAsync(request);

	TaskResponse taskResponse = new TaskResponse(new Ticket(projectId));
	taskResponse.addObject(ANALYSIS_PROCESSING_ID, request.getProcessingId());
	return taskResponse;
    }

    private void startImportAsync(List<File> files, ImportCommand command, String defaultStatus, TmProject project,
	    Map<String, ImportProgressInfo> importProgressInfoPerFile) {
	ServiceThreadPoolHandler.execute(new RunnableCallback() {
	    @Override
	    public void execute() {
		for (final File file : files) {
		    String fileName = file.getName();

		    ImportProgressInfo importProgressInfo = importProgressInfoPerFile.get(fileName);

		    ImportTypeEnum importType = ImportTypeEnum.getImportType(getExtension(fileName));
		    ImportOptionsModel importOptions = createImportOptionsModel(fileName, command, project,
			    defaultStatus);

		    try (InputStream stream = new FileInputStream(file)) {
			executeImport(stream, importOptions, importType, ServiceUtils.getFileEncoding(file),
				importProgressInfo, command.getSyncOption());
		    } catch (Exception e) {
			LogHelper.error(LOGGER, e.getMessage(), e);
			throw new RuntimeException(e);
		    } finally {
			ManualTaskHandlerUtils.forceDeleteFile(file);
		    }
		}
	    }

	    @Override
	    public String getRunnableOperation() {
		return ASYNC_OPERATION_NAME;
	    }
	}, SecurityContextHolder.getContext().getAuthentication());
    }
}