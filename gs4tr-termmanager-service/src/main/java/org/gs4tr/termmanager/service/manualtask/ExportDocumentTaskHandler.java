package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.eventlogging.spring.aop.EventLogger;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Task;
import org.gs4tr.foundation.modules.entities.model.TaskPriority;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.AttributeLevelEnum;
import org.gs4tr.termmanager.model.BaseTypeEnum;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.SystemTask;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.concurrency.RunnableCallback;
import org.gs4tr.termmanager.service.concurrency.ServiceThreadPoolHandler;
import org.gs4tr.termmanager.service.export.ExportFormatEnum;
import org.gs4tr.termmanager.service.impl.ExportAdapter;
import org.gs4tr.termmanager.service.logging.util.EventContextConstants;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.termmanager.service.model.command.ExportCommand;
import org.gs4tr.termmanager.service.utils.AdminTasksHolderHelper;
import org.gs4tr.termmanager.service.utils.TaskHandlerConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;

@SystemTask(priority = TaskPriority.LEVEL_TEN)
public class ExportDocumentTaskHandler extends AbstractExportDocumentTaskHandler {

    private static final String ASYNC_OPERATION_NAME = "termEntryServiceImpl.exportDocument"; //$NON-NLS-1$

    private static final String DESCRIPTION_TYPES = "descriptionTypes"; //$NON-NLS-1$

    private static final String EXPORTS = "exports"; //$NON-NLS-1$

    private static final String EXPORT_MULTILINGUAL = "exportMultilingual"; //$NON-NLS-1$

    private static final String EXPORT_TYPE = "exportType"; //$NON-NLS-1$

    private static final String STATUSES = "statuses"; //$NON-NLS-1$

    private static final String THREAD_NAME = "threadName"; //$NON-NLS-1$

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, ExportAdapter> _cacheGateway;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private AdminTasksHolderHelper _tasksHolderHelper;

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private UserProfileService _userProfileService;

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {

	TaskModel taskModel = new TaskModel();

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	Set<Long> projectIds = userProfile.getProjectUserLanguages().keySet();

	List<Long> userProjectIds = new ArrayList<Long>(projectIds);

	List<TmProject> userProjects = getProjectService().findProjectByIds(userProjectIds);

	if (userProjects.size() == 0) {
	    throw new UserException(MessageResolver.getMessage("ExportTbxDocumentTaskHandler.2"), //$NON-NLS-1$
		    MessageResolver.getMessage("ExportTbxDocumentTaskHandler.4")); //$NON-NLS-1$
	}

	Map<Long, List<Attribute>> projectAttributesMap = getProjectService().findAttributesByProjectId(userProjectIds);

	List<Object> projectsList = new ArrayList<Object>();
	List<Map<String, Object>> descriptionTypesList = new ArrayList<Map<String, Object>>();

	for (TmProject project : userProjects) {

	    Long projectId = project.getProjectId();

	    List<Task> systemTasks = getTasksHolderHelper().getSystemEntityTasks(projectId, null,
		    EntityTypeHolder.TERMENTRY);
	    if (systemTasks.contains(new Task(taskName))) {
		Map<String, Object> projectMap = new LinkedHashMap<String, Object>();
		projectMap.put(TaskHandlerConstants.PROJECT_NAME, project.getProjectInfo().getName());
		projectMap.put(TaskHandlerConstants.PROJECT_TICKET, new Ticket(projectId));

		projectsList.add(projectMap);
	    }

	    List<Attribute> attributes = projectAttributesMap.get(projectId);

	    List<String> termEntryAttributeTypes = getDescriptionTypes(attributes, BaseTypeEnum.DESCRIPTION,
		    AttributeLevelEnum.TERMENTRY);
	    List<String> termNoteTypes = getDescriptionTypes(attributes, BaseTypeEnum.NOTE,
		    AttributeLevelEnum.LANGUAGE);
	    List<String> termAttributeTypes = getDescriptionTypes(attributes, BaseTypeEnum.DESCRIPTION,
		    AttributeLevelEnum.LANGUAGE);

	    Map<String, List<String>> descriptionsMap = new HashMap<String, List<String>>();
	    descriptionsMap.put("termEntryAttributes", termEntryAttributeTypes);
	    descriptionsMap.put("termAttributes", termAttributeTypes);
	    descriptionsMap.put("termNotes", termNoteTypes);

	    Map<String, Object> projectDescriptionsMap = new HashMap<String, Object>();
	    projectDescriptionsMap.put("projectTicket", new Ticket(project.getProjectId()));
	    projectDescriptionsMap.put("descriptions", descriptionsMap);

	    descriptionTypesList.add(projectDescriptionsMap);
	}

	if (projectsList.isEmpty()) {
	    throw new UserException(MessageResolver.getMessage("ExportTbxDocumentTaskHandler.2"), //$NON-NLS-1$
		    MessageResolver.getMessage("ExportTbxDocumentTaskHandler.4"));
	}

	taskModel.addObject(TaskHandlerConstants.PROJECTS, projectsList);

	List<Map<String, Object>> exportsList = new ArrayList<Map<String, Object>>();

	List<ExportFormatEnum> exportFormats = ExportFormatEnum.getExportFormats();
	for (ExportFormatEnum exportFormatEnum : exportFormats) {
	    Map<String, Object> exportMap = new LinkedHashMap<String, Object>();
	    exportMap.put(EXPORT_TYPE, exportFormatEnum.name());
	    exportMap.put(EXPORT_MULTILINGUAL, exportFormatEnum.isMultilingual());
	    exportsList.add(exportMap);
	}

	List<String> statuses = new ArrayList<String>();

	for (ItemStatusType status : ItemStatusTypeHolder.getTermStatusValues()) {
	    statuses.add(status.getName());
	}

	taskModel.addObject(STATUSES, statuses);
	taskModel.addObject(EXPORTS, exportsList);
	taskModel.addObject(DESCRIPTION_TYPES, descriptionTypesList);

	return new TaskModel[] { taskModel };
    }

    public AdminTasksHolderHelper getTasksHolderHelper() {
	return _tasksHolderHelper;
    }

    public TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    @Override
    @LogEvent(action = TMGREventActionConstants.ACTION_EXPORT, actionCategory = TMGREventActionConstants.CATEGORY_UI_CONTROLLER)
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {

	ExportCommand exportCommand = (ExportCommand) command;

	EventLogger.addProperty(EventContextConstants.PROJECT_ID, exportCommand.getProjectId());
	EventLogger.addProperty(EventContextConstants.SOURCE_LANGUAGE, exportCommand.getSourceLocale());
	EventLogger.addProperty(EventContextConstants.TARGET_LANGUAGES, exportCommand.getTargetLocales());

	TermEntrySearchRequest termEntrySearchRequest = createSearchRequestFromSearchCommand(exportCommand);

	PagedListInfo pageListInfo = createPageListInfo(termEntrySearchRequest);

	TmgrSearchFilter filter = createFilterFromRequest(termEntrySearchRequest, pageListInfo);

	String exportFormat = exportCommand.getExportFormat();
	EventLogger.addProperty(EventContextConstants.EXPORT_FORMAT, exportFormat);

	TmProject project = getProjectService().load(exportCommand.getProjectId());
	if (project != null && project.getProjectInfo() != null) {
	    EventLogger.addProperty(EventContextConstants.PROJECT_NAME, project.getProjectInfo().getName());
	    EventLogger.addProperty(EventContextConstants.PROJECT_SHORT_CODE, project.getProjectInfo().getShortCode());
	}

	String threadName = UUID.randomUUID().toString();

	startExport(termEntrySearchRequest, filter, exportFormat, threadName);

	TaskResponse taskResponse = new TaskResponse(null);
	taskResponse.addObject(THREAD_NAME, threadName);

	return taskResponse;

    }

    private void executeAsync(String threadName, TermEntrySearchRequest termEntrySearchRequest, TmgrSearchFilter filter,
	    String exportFormat, ExportAdapter exportAdapter) {
	getTermEntryService().exportDocument(termEntrySearchRequest, filter, exportFormat, exportAdapter);
    }

    private CacheGateway<String, ExportAdapter> getCacheGateway() {
	return _cacheGateway;
    }

    private List<String> getDescriptionTypes(List<Attribute> attributes, final BaseTypeEnum baseType,
	    final AttributeLevelEnum level) {
	if (CollectionUtils.isEmpty(attributes)) {
	    return new ArrayList<String>();
	}

	return attributes.stream().filter(a -> baseType == a.getBaseTypeEnum())
		.filter(a -> a.getAttributeLevel() == null || level == a.getAttributeLevel()).map(a -> a.getName())
		.collect(Collectors.toList());
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private void startExport(final TermEntrySearchRequest searchRequest, final TmgrSearchFilter filter,
	    final String exportFormat, final String threadName) {

	final ExportAdapter exportAdapter = new ExportAdapter(threadName, System.currentTimeMillis());

	getCacheGateway().put(CacheName.EXPORT_PROGRESS_STATUS, threadName, exportAdapter);

	ServiceThreadPoolHandler.execute(new RunnableCallback() {
	    @Override
	    public void execute() {
		executeAsync(threadName, searchRequest, filter, exportFormat, exportAdapter);
	    }

	    @Override
	    public String getRunnableOperation() {
		return ASYNC_OPERATION_NAME;
	    }

	}, SecurityContextHolder.getContext().getAuthentication());
    }
}
