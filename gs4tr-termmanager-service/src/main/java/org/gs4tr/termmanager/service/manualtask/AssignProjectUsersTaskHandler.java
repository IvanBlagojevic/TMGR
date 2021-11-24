package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.webmvc.filters.model.RefreshUserContext;
import org.gs4tr.termmanager.model.Language;
import org.gs4tr.termmanager.model.ProjectLanguage;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.ProjectUserLanguageModel;
import org.gs4tr.termmanager.model.dto.UserLanguageModel;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.CacheGatewaySessionUpdaterService;
import org.gs4tr.termmanager.service.OrganizationService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.RoleService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.model.command.AssignProjectUserLanguageCommand;
import org.gs4tr.termmanager.service.model.command.UserLanguageCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoAssignProjectUserLanguageCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class AssignProjectUsersTaskHandler extends AbstractManualTaskHandler {

    private static final String PROJECT_ROLES_NAME_KEY = "projectRoles"; //$NON-NLS-1$

    @Autowired
    private CacheGatewaySessionUpdaterService _cacheGatewaySessionUpdaterService;

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

	return DtoAssignProjectUserLanguageCommand.class;
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

	AssignProjectUserLanguageCommand internalCommand = (AssignProjectUserLanguageCommand) command;

	Boolean showGenericUsers = Boolean.valueOf(internalCommand.isShowGenericUsers());

	Long projectId = parentIds[0];

	TmProject project = getProjectService().findById(projectId);

	TmOrganization organization = getOrganizationService().findById(project.getOrganization().getOrganizationId());

	List<String> projectLanguages = getProjectService().getProjectLanguageCodes(projectId);

	List<TmUserProfile> users = new ArrayList<>();

	if (!showGenericUsers) {
	    List<TmUserProfile> orgUsers = getUserProfileService().findUsersByOrganizationFetchLanguages(organization,
		    showGenericUsers);
	    if (CollectionUtils.isNotEmpty(orgUsers)) {
		users.addAll(orgUsers);
	    }
	} else {
	    List<TmUserProfile> genericUsers = getUserProfileService().findGenericUserByProjectId(projectId);
	    if (CollectionUtils.isNotEmpty(genericUsers)) {
		users.addAll(genericUsers);
	    }
	}

	TaskModel model = new TaskModel();

	List<Role> allProjectRoles = getRoleService().findAllContextRoles();
	List<String> allProjectRoleIds = new ArrayList<>();
	for (Role allProjectRole : allProjectRoles) {
	    if (allProjectRole.isGeneric()) {
		continue;
	    }

	    allProjectRoleIds.add(allProjectRole.getRoleId());
	}

	model.addObject(PROJECT_ROLES_NAME_KEY, allProjectRoleIds);

	List<UserLanguageModel> userLanguageModels = new ArrayList<>();
	for (TmUserProfile user : users) {
	    if (user.isOpe()) {
		continue;
	    }
	    Long userProfileId = user.getUserProfileId();
	    List<String> projectUserLanguageRoles = getProjectService().getRoleIdsByUserAndProject(projectId,
		    userProfileId);

	    List<String> projectUserLanguages = getProjectService().getProjectUserLanguageCodes(projectId,
		    user.getUserProfileId());

	    String roleId = null;

	    if (CollectionUtils.isNotEmpty(projectUserLanguageRoles)) {
		roleId = projectUserLanguageRoles.get(0);
	    }

	    UserLanguageModel userLanguageModel = new UserLanguageModel();

	    List<ProjectUserLanguageModel> languages = new ArrayList<>();

	    for (String language : projectLanguages) {
		ProjectUserLanguageModel projectUserLanguageModel = new ProjectUserLanguageModel();
		projectUserLanguageModel.setLocale(language);
		projectUserLanguageModel.setValue(Language.valueOf(language).getDisplayName());
		projectUserLanguageModel.setSelected(projectUserLanguages.contains(language));

		languages.add(projectUserLanguageModel);
	    }

	    userLanguageModel.setAllLanguages(languages);
	    userLanguageModel.setUserName(user.getUserName());
	    userLanguageModel.setRole(roleId);
	    userLanguageModel.setUserTicket(TicketConverter.fromInternalToDto(userProfileId));

	    userLanguageModels.add(userLanguageModel);

	}

	model.addObject("allUsers", userLanguageModels);

	return new TaskModel[] { model };
    }

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    @RefreshUserContext
    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> items) {

	AssignProjectUserLanguageCommand assignProjectUserLanguageCommand = (AssignProjectUserLanguageCommand) command;

	List<Long> userIds = new ArrayList<Long>();

	Long projectId = assignProjectUserLanguageCommand.getProjectId();

	Long organizationId = getProjectService().getOrganizationIdByProject(projectId);

	Set<String> projectLanguageIds = new HashSet<String>();

	List<ProjectLanguage> projectLanguages = getProjectService().getProjectLanguages(projectId);

	for (ProjectLanguage projectLanguage : projectLanguages) {
	    projectLanguageIds.add(projectLanguage.getLanguage());
	}

	for (UserLanguageCommand userLanguageCommand : assignProjectUserLanguageCommand.getUsers()) {
	    UserInfo userInfo = userLanguageCommand.getUserInfo();

	    Long userId = userLanguageCommand.getUserId();

	    if (userInfo != null && userLanguageCommand.isGenericUser()) {
		userId = getUserProfileService().createOrUpdateGenericUser(userId, userInfo, organizationId);
		userLanguageCommand.setUserId(userId);
	    }

	    userIds.add(userId);
	}

	for (UserLanguageCommand userLanguageCommand : assignProjectUserLanguageCommand.getUsers()) {

	    String roleName = userLanguageCommand.getRoleId();

	    boolean genericUser = userLanguageCommand.isGenericUser();

	    if (genericUser) {
		roleName = ManualTaskHandlerUtils.GENERIC_USER_ROLE;
		userLanguageCommand.setUserLanguages(new ArrayList<String>(projectLanguageIds));
	    }

	    Long userId = userLanguageCommand.getUserId();

	    ManualTaskHandlerUtils.updateUserProjectRole(getRoleService(), getUserProfileService(), projectId, userIds,
		    userId, roleName, genericUser);

	    getProjectService().addOrUpdateProjectUserLanguages(userId, projectId,
		    userLanguageCommand.getUserLanguages(), userIds, genericUser);
	}

	getCacheGatewaySessionUpdaterService().removeOnEditProjectUser(userIds, projectId,
		assignProjectUserLanguageCommand);

	return new TaskResponse(new Ticket(projectId));
    }

    private CacheGatewaySessionUpdaterService getCacheGatewaySessionUpdaterService() {
	return _cacheGatewaySessionUpdaterService;
    }

    private OrganizationService getOrganizationService() {
	return _organizationService;
    }
}
