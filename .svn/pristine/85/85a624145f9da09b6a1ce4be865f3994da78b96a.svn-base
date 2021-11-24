package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.model.command.AssignUserProjectCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoAssignUserProjectCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class AssignUserProjectTaskHandler extends AbstractManualTaskHandler {

    private static final String PROJECT_NAME = "projectName";

    private static final String SELECTED = "selected";

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private UserProfileService _userProfileService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoAssignUserProjectCommand.class;
    }

    public ProjectService getProjectService() {
	return _projectService;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {
	UserProfileService userProfileService = getUserProfileService();
	ProjectService projectService = getProjectService();

	Long id = parentIds[0];

	TmUserProfile userProfile = userProfileService.load(id);

	Set<Long> userProjects = userProfile.getProjectUserLanguages().keySet();

	List<TmProject> projects = projectService.findAllProjects();

	List<TaskModel> taskModels = new ArrayList<TaskModel>();

	for (TmProject project : projects) {
	    Long projectId = project.getProjectId();

	    TaskModel newTaskModel = new TaskModel(new Ticket(id), new Ticket(projectId));

	    newTaskModel.addObject(PROJECT_NAME, project.getProjectInfo().getName());

	    newTaskModel.addObject(SELECTED, userProjects.contains(project.getProjectId()));

	    taskModels.add(newTaskModel);
	}

	return (TaskModel[]) taskModels.toArray(new TaskModel[taskModels.size()]);
    }

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    public Boolean isVisible() {
	return Boolean.FALSE;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {

	AssignUserProjectCommand assignUserProjectCommand = (AssignUserProjectCommand) command;

	Long userId = assignUserProjectCommand.getUserId();

	return new TaskResponse(new Ticket(userId));
    }

}
