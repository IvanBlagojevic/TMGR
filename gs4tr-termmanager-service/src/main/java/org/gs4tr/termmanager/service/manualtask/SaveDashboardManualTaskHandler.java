package org.gs4tr.termmanager.service.manualtask;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.eventlogging.api.EventThreadContext;
import org.gs4tr.eventlogging.spring.aop.EventLogger;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.foundation.modules.entities.model.ProjectInfo;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.logging.util.EventContextConstants;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.termmanager.service.model.command.SaveDashboardCommand;
import org.gs4tr.termmanager.service.model.command.SaveDashboardCommands;
import org.gs4tr.termmanager.service.model.command.dto.DtoSaveDashboardCommands;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class SaveDashboardManualTaskHandler extends AbstractManualTaskHandler {

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private UserProfileService _userProfileService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoSaveDashboardCommands.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    @LogEvent(action = TMGREventActionConstants.ACTION_ADD_TERM, actionCategory = TMGREventActionConstants.CATEGORY_UI_CONTROLLER)
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {

	TaskResponse response = new TaskResponse(null);

	SaveDashboardCommands saveDashboardCommands = (SaveDashboardCommands) command;

	List<SaveDashboardCommand> saveDashboardCommandList = saveDashboardCommands.getSaveDashboardCommands();

	// Command is in a list because we had to support add/edit terms in a
	// different projects. In most cases, we will have just one command in a
	// list
	for (SaveDashboardCommand saveDashboardCommand : saveDashboardCommandList) {

	    EventThreadContext.addProperty(TMGREventActionConstants.ACTION_ADD_TERM,
		    TMGREventActionConstants.CATEGORY_UI_CONTROLLER);

	    EventLogger.addProperty(EventContextConstants.SOURCE_LANGUAGE, saveDashboardCommand.getSourceLanguage());
	    EventLogger.addProperty(EventContextConstants.TARGET_LANGUAGES, saveDashboardCommand.getTargetLanguage());

	    Long projectId = saveDashboardCommand.getProjectId();
	    EventLogger.addProperty(EventContextConstants.PROJECT_ID, projectId);

	    TmProject project = getProjectService().load(projectId);

	    ProjectInfo projectInfo = project.getProjectInfo();
	    EventLogger.addProperty(EventContextConstants.PROJECT_NAME, projectInfo.getName());
	    EventLogger.addProperty(EventContextConstants.PROJECT_SHORT_CODE, projectInfo.getShortCode());

	    ItemStatusType projectStatus = ServiceUtils.decideTermStatus(project);
	    resolveMissingSourceStatus(saveDashboardCommand, projectStatus);

	    getTermEntryService().updateTermEntries(saveDashboardCommand.getTranslationUnits(),
		    saveDashboardCommand.getSourceLanguage(), projectId, Action.EDITED);

	}

	getUserProfileService().updateHasChangedTerms(Boolean.TRUE);

	return response;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    private UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    private void resolveMissingSourceStatus(SaveDashboardCommand saveDashboardCommand, ItemStatusType projectStatus) {
	List<TranslationUnit> translationUnits = saveDashboardCommand.getTranslationUnits();
	for (TranslationUnit tu : translationUnits) {
	    List<UpdateCommand> sourceUpdateCommands = tu.getSourceTermUpdateCommands();
	    if (CollectionUtils.isEmpty(sourceUpdateCommands)) {
		continue;
	    }

	    for (UpdateCommand updateCommand : sourceUpdateCommands) {
		if (StringUtils.isEmpty(updateCommand.getStatus())) {
		    updateCommand.setStatus(projectStatus.getName());
		}
	    }
	}
    }
}
