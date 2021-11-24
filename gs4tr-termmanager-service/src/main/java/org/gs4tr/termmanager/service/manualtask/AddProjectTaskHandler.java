package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.TaskPriority;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.webmvc.filters.model.RefreshUserContext;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectLanguage;
import org.gs4tr.termmanager.model.SystemTask;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.OrganizationService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.RoleService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.model.command.AssignProjectUserLanguageCommand;
import org.gs4tr.termmanager.service.model.command.ProjectCommand;
import org.gs4tr.termmanager.service.model.command.UserLanguageCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoProjectCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;

@SystemTask(priority = TaskPriority.LEVEL_ZERO)
public class AddProjectTaskHandler extends AbstractProjectManualTaskHandler {

    private static final String ORGANIZATIONS = "organizations"; //$NON-NLS-1$

    private static final String ORGANIZATION_NAME = "organizationName"; //$NON-NLS-1$

    private static final String ORGANIZATION_TICKET = "ticket"; //$NON-NLS-1$

    private static final String PROJECT_ROLES_NAME_KEY = "projectRoles"; //$NON-NLS-1$

    @Autowired
    private OrganizationService _organizationService;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private RoleService _roleService;

    @Autowired
    private UserProfileService _userProfileService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoProjectCommand.class;
    }

    public OrganizationService getOrganizationService() {
	return _organizationService;
    }

    public ProjectService getProjectService() {
	return _projectService;
    }

    public RoleService getRoleService() {
	return _roleService;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {

	TaskModel model = new TaskModel();

	List<TmOrganization> organizations = getOrganizationService().findAllEnabledOrganizations();

	if (organizations.isEmpty()) {
	    throw new UserException(MessageResolver.getMessage("AddProjectTaskHandler.3"), //$NON-NLS-1$
		    MessageResolver.getMessage("AddProjectTaskHandler.1")); //$NON-NLS-1$ s
	}

	List<Map<String, Object>> organizationList = new ArrayList<Map<String, Object>>();

	for (TmOrganization organization : organizations) {
	    if (organization.getOrganizationInfo().isEnabled()) {
		Map<String, Object> newTaskModel = new HashMap<String, Object>();
		newTaskModel.put(ORGANIZATION_NAME, organization.getOrganizationInfo().getName());

		newTaskModel.put(ORGANIZATION_TICKET, new Ticket(organization.getOrganizationId()));

		organizationList.add(newTaskModel);
	    }

	}

	model.addObject(ORGANIZATIONS, organizationList);

	List<Role> allProjectRoles = getRoleService().findAllContextRoles();

	if (allProjectRoles.isEmpty()) {
	    throw new UserException(MessageResolver.getMessage("AddProjectTaskHandler.7"), //$NON-NLS-1$
		    MessageResolver.getMessage("AddProjectTaskHandler.0")); //$NON-NLS-1$
	}

	List<String> allProjectRoleIds = new ArrayList<String>();
	for (Role allProjectRole : allProjectRoles) {
	    if (allProjectRole.isGeneric()) {
		continue;
	    }

	    allProjectRoleIds.add(allProjectRole.getRoleId());
	}

	model.addObject(PROJECT_ROLES_NAME_KEY, allProjectRoleIds);

	List<Map<String, String>> termStatuses = ManualTaskHandlerUtils.createTermStatusModel();

	model.addObject(ManualTaskHandlerUtils.TERM_STATUSES, termStatuses);
	model.addObject(ManualTaskHandlerUtils.DEFAULT_TERM_STATUS, ItemStatusTypeHolder.PROCESSED.getName());

	return new TaskModel[] { model };
    }

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    @Override
    @RefreshUserContext
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> items) {

	ProjectService projectService = getProjectService();

	ProjectCommand addCommand = (ProjectCommand) command;

	String projectName = addCommand.getProjectInfo().getName();

	TmProject existingProject = getProjectService().findProjectByName(projectName);

	if (existingProject != null) {
	    throw new UserException(MessageResolver.getMessage("AddProjectTaskHandler.8"), //$NON-NLS-1$
		    String.format(MessageResolver.getMessage("AddProjectTaskHandler.2"), //$NON-NLS-1$
			    existingProject.getProjectInfo().getName()));
	}

	Long projectId = projectService.createProject(addCommand.getProjectInfo());

	TmProject project = projectService.findById(projectId);

	ItemStatusType defaultTermStatus = addCommand.getDefaultTermStatus();

	updateProjectIfDefaultTermStatusOrSharePendingTermsIsChanged(defaultTermStatus, project, projectService,
		addCommand);

	Long organizationId = addCommand.getOrganizationId();

	getOrganizationService().addProjectToOrganization(organizationId, projectId);

	List<ProjectLanguage> projectLanguages = addCommand.getProjectLanguages();

	Set<String> projectLanguageIds = new HashSet<String>();

	if (projectLanguages != null) {
	    for (ProjectLanguage pl : projectLanguages) {
		projectLanguageIds.add(pl.getLanguage());
	    }

	    projectId = projectService.addOrUpdateProjectLanguages(projectId, projectLanguages);
	    projectService.addOrUpdateProjectLanguageDetail(projectId, projectLanguageIds);
	}

	AssignProjectUserLanguageCommand assignProjectUserLanguageCommand = addCommand.getUserProjectCommand();

	List<Long> userIds = new ArrayList<Long>();

	if (assignProjectUserLanguageCommand != null) {

	    for (UserLanguageCommand userLanguageCommand : assignProjectUserLanguageCommand.getUsers()) {
		UserInfo userInfo = userLanguageCommand.getUserInfo();

		Long userId = userLanguageCommand.getUserId();

		if (userInfo != null && userLanguageCommand.isGenericUser()) {
		    if (!ManualTaskHandlerUtils.validateUserInfo(userInfo)) {
			continue;
		    }

		    userId = getUserProfileService().createOrUpdateGenericUser(userId, userInfo, organizationId);
		    userLanguageCommand.setUserId(userId);
		}

		userIds.add(userId);
	    }

	    for (UserLanguageCommand userLanguageCommand : assignProjectUserLanguageCommand.getUsers()) {

		String roleName = userLanguageCommand.getRoleId();

		Boolean genericUser = Boolean.valueOf(userLanguageCommand.isGenericUser());

		if (genericUser) {
		    roleName = ManualTaskHandlerUtils.GENERIC_USER_ROLE;
		    userLanguageCommand.setUserLanguages(new ArrayList<String>(projectLanguageIds));
		}

		Long userId = userLanguageCommand.getUserId();

		ManualTaskHandlerUtils.updateUserProjectRole(getRoleService(), getUserProfileService(), projectId,
			userIds, userId, roleName, genericUser);

		getProjectService().addOrUpdateProjectUserLanguages(userLanguageCommand.getUserId(), projectId,
			userLanguageCommand.getUserLanguages(), userIds, genericUser);
	    }
	}

	return new TaskResponse(new Ticket(projectId));
    }

    @Override
    protected Boolean getSharePendingTerms(ProjectCommand command) {
	return ServiceUtils.isSharePendingTermsOnProject(command.getSharePendingTerms());
    }
}
