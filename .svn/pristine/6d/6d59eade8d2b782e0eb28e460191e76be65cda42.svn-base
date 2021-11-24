package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.search.ProjectSearchRequest;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.OrganizationService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.model.command.AssignOrganizationProjectCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoAssignOrganizationProjectCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class AssignOrganizationProjectHandler extends AbstractManualTaskHandler {

    private static final String CHECKED = "checked";

    private static final String PROJECTNAME = "projectName";

    private static final String UNCHECKED = "unchecked";

    private static final String VALUE_KEY = "value";

    @Autowired
    private OrganizationService _organizationService;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private UserProfileService _userProfileService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoAssignOrganizationProjectCommand.class;
    }

    public OrganizationService getOrganizationService() {
	return _organizationService;
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

	Long id = parentIds[0];

	TmOrganization organization = getOrganizationService().findById(id);

	TmProject[] projects = new TmProject[getProjectService().search(new ProjectSearchRequest(), new PagedListInfo())
		.getElements().length];

	projects = getProjectService().search(new ProjectSearchRequest(), new PagedListInfo()).getElements();

	List<TaskModel> taskModels = new ArrayList<TaskModel>();

	for (TmProject project : projects) {
	    TaskModel taskModel = new TaskModel(new Ticket(id), new Ticket(project.getProjectId()));

	    taskModel.addObject(PROJECTNAME, project.getProjectInfo().getName());

	    taskModel
		    .addObject(VALUE_KEY,
			    (project.getOrganization() != null && project.getOrganization().getOrganizationInfo()
				    .getName().equals(organization.getOrganizationInfo().getName()) ? CHECKED
					    : UNCHECKED));

	    taskModels.add(taskModel);
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
	AssignOrganizationProjectCommand assignCommand = (AssignOrganizationProjectCommand) command;

	Long organizationId = getOrganizationService().assignProjectsToOrganization(assignCommand.getOrganizationId(),
		assignCommand.getProjectIds());

	return new TaskResponse(new Ticket(organizationId));
    }

}
