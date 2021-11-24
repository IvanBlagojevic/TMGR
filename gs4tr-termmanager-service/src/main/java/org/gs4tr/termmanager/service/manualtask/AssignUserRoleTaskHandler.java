package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmPowerUserProjectRole;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.PowerUserProjectRoleService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.RoleService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.model.command.AssignUserRoleCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoAssignUserRoleCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class AssignUserRoleTaskHandler extends AbstractManualTaskHandler {

    private static final String ASSIGNED_KEY = "assigned";

    private static final String PROJECT_ROLES_KEY = "projectRoles";

    private static final String ROLE_KEY = "role";

    private static final String SYSTEM_ROLES_KEY = "systemRoles";

    @Autowired
    private PowerUserProjectRoleService _powerUserProjectRoleService;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private RoleService _roleService;

    @Autowired
    private UserProfileService _userProfileService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoAssignUserRoleCommand.class;
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

	AssignUserRoleCommand assignUserRoleCommand = (AssignUserRoleCommand) command;

	validateCommand(assignUserRoleCommand);

	String roleTypeInfo = assignUserRoleCommand.getRoleTypeInfo();

	Long userId = parentIds[0];

	TaskModel newRoleTaskModel = new TaskModel(null, null);

	if (roleTypeInfo.equals(SYSTEM_ROLES_KEY)) {

	    TmUserProfile userProfile = getUserProfileService().findById(userId);

	    List<Role> allSystemRoles = getRoleService().findAllSystemRoles();

	    Set<Role> systemRoles = userProfile.getSystemRoles();

	    ArrayList<Map<String, Object>> systemRoleList = new ArrayList<Map<String, Object>>();

	    for (Role role : allSystemRoles) {
		Map<String, Object> roleMap = new HashMap<String, Object>();
		if (systemRoles.contains(role)) {
		    roleMap.put(ASSIGNED_KEY, Boolean.TRUE);
		} else {
		    roleMap.put(ASSIGNED_KEY, Boolean.FALSE);
		}
		roleMap.put(ROLE_KEY, role.getRoleId());

		systemRoleList.add(roleMap);

	    }
	    newRoleTaskModel.addObject(SYSTEM_ROLES_KEY, systemRoleList);
	} else {

	    List<Role> allProjectRoles = getRoleService().findAllContextRoles();

	    ArrayList<Map<String, Object>> projectRoleList = new ArrayList<>();

	    TmPowerUserProjectRole powerUserProjectRole = getPowerUserProjectRoleService().findByUserId(userId);

	    for (Role role : allProjectRoles) {

		Map<String, Object> projectRoleMap = new HashMap<>();

		projectRoleMap.put(ROLE_KEY, role.getRoleId());
		projectRoleMap.put(ASSIGNED_KEY, isRoleAssignedToUser(powerUserProjectRole, role));

		projectRoleList.add(projectRoleMap);
	    }

	    newRoleTaskModel.addObject(PROJECT_ROLES_KEY, projectRoleList);
	}

	return new TaskModel[] { newRoleTaskModel };
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

	AssignUserRoleCommand assignCommand = (AssignUserRoleCommand) command;

	Long userId = assignCommand.getUserId();

	Set<Role> systemRoles = assignCommand.getSystemRoles();
	if (Objects.nonNull(systemRoles)) {
	    getUserProfileService().addOrUpdateSystemRoles(userId, systemRoles);
	}

	TmUserProfile tmUserProfile = getUserProfileService().findById(userId);

	if (tmUserProfile.isPowerUser()) {

	    Role projectRole = assignCommand.getProjectRole();

	    if (Objects.nonNull(projectRole)) {
		saveOrUpdatePowerUserProjectRole(tmUserProfile, projectRole.getRoleId());
	    }

	}
	return new TaskResponse(new Ticket(userId));
    }

    private PowerUserProjectRoleService getPowerUserProjectRoleService() {
	return _powerUserProjectRoleService;
    }

    private boolean isRoleAssignedToUser(TmPowerUserProjectRole powerUserProjectRole, Role projectRole) {
	return Objects.nonNull(powerUserProjectRole) && Objects.nonNull(powerUserProjectRole.getRole())
		&& powerUserProjectRole.getRole().equals(projectRole);
    }

    private boolean isSameUserRole(TmPowerUserProjectRole powerUserProjectRole, Role newRole) {
	return powerUserProjectRole.getRole().equals(newRole);
    }

    private void saveOrUpdatePowerUserProjectRole(TmUserProfile tmUserProfile, String roleId) {

	TmPowerUserProjectRole powerUserProjectRole = getPowerUserProjectRoleService()
		.findByUserId(tmUserProfile.getUserProfileId());

	Role role = getRoleService().findById(roleId);

	if (Objects.isNull(powerUserProjectRole)) {
	    powerUserProjectRole = new TmPowerUserProjectRole();
	    powerUserProjectRole.setRole(role);
	    powerUserProjectRole.setUserProfile(tmUserProfile);
	    getPowerUserProjectRoleService().save(powerUserProjectRole);
	} else if (!isSameUserRole(powerUserProjectRole, role)) {
	    getPowerUserProjectRoleService().updatePowerUserProjectRoles(role, tmUserProfile.getUserProfileId());
	}
    }

    private void validateCommand(AssignUserRoleCommand assignUserRoleCommand) {
	String roleTypeInfo = assignUserRoleCommand.getRoleTypeInfo();

	if (!roleTypeInfo.equals(PROJECT_ROLES_KEY) && !roleTypeInfo.equals(SYSTEM_ROLES_KEY)) {
	    throw new UserException(MessageResolver.getMessage("AssignUserRoleTaskHandler.3"));
	}
    }

}
