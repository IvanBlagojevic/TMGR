package org.gs4tr.termmanager.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.security.model.RoleTypeEnum;
import org.gs4tr.termmanager.dao.PowerUserProjectRoleDAO;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmPowerUserProjectRole;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.PowerUserProjectRoleService;
import org.gs4tr.termmanager.service.RoleService;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.AssignUserRoleCommand;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("assign_user_role")
public class AssignUserRoleTaskHandlerTest extends AbstractSpringServiceTests {

    private static final String PROJECT_ROLES_KEY = "projectRoles";

    private static final String SYSTEM_ROLES_KEY = "systemRoles";

    @Autowired
    private PowerUserProjectRoleDAO _powerUserProjectRoleDAO;

    @Autowired
    private PowerUserProjectRoleService _powerUserProjectRoleService;

    @Autowired
    private RoleService _roleService;

    /*
     * Test if power user will have assigned project role. This Action is called on
     * edit user
     */
    @Test
    public void assignProjectRoleToPowerUserTest() {

	Long projectId = 1L;
	Long userId = 3L;

	Role termTranslatorRole = getProjectRoleByUserAndProjectId(projectId, userId);
	assertNotNull(termTranslatorRole);

	Long userProfileId = 10L;
	TmUserProfile userProfile = getUserProfileService().findById(userProfileId);
	assertNotNull(userProfile);

	Set<Role> userSystemRolesBefore = userProfile.getSystemRoles();

	AssignUserRoleCommand command = new AssignUserRoleCommand();

	/* System roles will not be edited */
	command.setSystemRoles(null);
	command.setUserId(userProfileId);
	command.setProjectRole(termTranslatorRole);

	ManualTaskHandler taskHandler = getHandler("assign role");
	TaskResponse taskResponse = taskHandler.processTasks(new Long[] { userProfileId }, null, command, null);
	assertNotNull(taskResponse);

	/* Check if user has assigned project roles */
	TmPowerUserProjectRole savedPowerUserProjectRole = getPowerUserProjectRoleService().findByUserId(userProfileId);
	assertNotNull(savedPowerUserProjectRole);
	Assert.assertEquals(savedPowerUserProjectRole.getRole(), termTranslatorRole);
	Assert.assertEquals(savedPowerUserProjectRole.getUserProfile(), userProfile);

	/* User system roles should not be changed */
	TmUserProfile userProfileAfter = getUserProfileService().findById(userProfileId);
	Assert.assertEquals(userSystemRolesBefore, userProfileAfter.getSystemRoles());
    }

    /*
     * Test if power user will have assigned system and project roles on add user
     * action. This action is called on add user
     */
    @Test
    public void assignRolesToPowerUserTest() {

	Long projectId = 1L;
	Long userId = 3L;

	Role termTranslatorRole = getProjectRoleByUserAndProjectId(projectId, userId);
	assertNotNull(termTranslatorRole);

	Long userProfileId = 10L;
	TmUserProfile userProfile = getUserProfileService().findById(userProfileId);
	assertNotNull(userProfile);

	AssignUserRoleCommand command = new AssignUserRoleCommand();

	Role systemRole = getRoleService().findById("pro role");
	Set<Role> systemRoles = new HashSet<>(Arrays.asList(systemRole));

	command.setSystemRoles(systemRoles);
	command.setUserId(userProfileId);
	command.setProjectRole(termTranslatorRole);

	ManualTaskHandler taskHandler = getHandler("assign role");
	TaskResponse taskResponse = taskHandler.processTasks(new Long[] { userProfileId }, null, command, null);
	assertNotNull(taskResponse);

	/* Check if user has assigned project roles */
	TmPowerUserProjectRole savedPowerUserProjectRole = getPowerUserProjectRoleService().findByUserId(userProfileId);
	assertNotNull(savedPowerUserProjectRole);
	Assert.assertEquals(savedPowerUserProjectRole.getRole(), termTranslatorRole);
	Assert.assertEquals(savedPowerUserProjectRole.getUserProfile(), userProfile);

	/* Check if user has assigned system roles */
	TmUserProfile userProfileAfter = getUserProfileService().findById(userProfileId);
	Assert.assertEquals(systemRoles, userProfileAfter.getSystemRoles());
    }

    @Test
    public void assignSystemRolesToOrganizationUserTest() {

	Long userProfileId = 3L;
	TmUserProfile userProfile = getUserProfileService().findById(userProfileId);
	assertNotNull(userProfile);
	assertFalse(userProfile.isPowerUser());

	AssignUserRoleCommand command = new AssignUserRoleCommand();

	Role systemRole = getRoleService().findById("pro role");
	Set<Role> systemRoles = new HashSet<>(Arrays.asList(systemRole));

	command.setSystemRoles(systemRoles);
	command.setUserId(userProfileId);
	command.setProjectRole(null);

	ManualTaskHandler taskHandler = getHandler("assign role");
	TaskResponse taskResponse = taskHandler.processTasks(new Long[] { userProfileId }, null, command, null);
	assertNotNull(taskResponse);

	/* User should not have project roles */
	TmPowerUserProjectRole savedPowerUserProjectRole = getPowerUserProjectRoleService().findByUserId(userProfileId);
	assertNull(savedPowerUserProjectRole);

	/* Check if user has assigned system roles */
	TmUserProfile userProfileAfter = getUserProfileService().findById(userProfileId);
	Assert.assertEquals(systemRoles, userProfileAfter.getSystemRoles());
    }

    /*
     * Test if power user will have assigned system roles. This action is called on
     * edit user.
     */
    @Test
    public void assignSystemRolesToPowerUserTest() {

	Long userProfileId = 10L;
	TmUserProfile userProfile = getUserProfileService().findById(userProfileId);
	assertNotNull(userProfile);

	AssignUserRoleCommand command = new AssignUserRoleCommand();

	Role systemRole = getRoleService().findById("pro role");
	Set<Role> systemRoles = new HashSet<>(Arrays.asList(systemRole));

	command.setSystemRoles(systemRoles);
	command.setUserId(userProfileId);
	command.setProjectRole(null);

	ManualTaskHandler taskHandler = getHandler("assign role");
	TaskResponse taskResponse = taskHandler.processTasks(new Long[] { userProfileId }, null, command, null);
	assertNotNull(taskResponse);

	/* User should not have project roles */
	TmPowerUserProjectRole savedPowerUserProjectRole = getPowerUserProjectRoleService().findByUserId(userProfileId);
	assertNull(savedPowerUserProjectRole);

	/* Check if user has assigned system roles */
	TmUserProfile userProfileAfter = getUserProfileService().findById(userProfileId);
	Assert.assertEquals(systemRoles, userProfileAfter.getSystemRoles());
    }

    public PowerUserProjectRoleDAO getPowerUserProjectRoleDAO() {
	return _powerUserProjectRoleDAO;
    }

    public PowerUserProjectRoleService getPowerUserProjectRoleService() {
	return _powerUserProjectRoleService;
    }

    @Test
    public void getProjectRolesTaskInfosTest() {
	Long userProfileId = 12L;

	TmUserProfile userProfile = getUserProfileService().findById(userProfileId);
	assertNotNull(userProfile);

	Role termTranslatorRole = getProjectRoleByUserAndProjectId(1L, 3L);
	assertNotNull(termTranslatorRole);

	AssignUserRoleCommand command = new AssignUserRoleCommand();
	command.setSystemRoles(userProfile.getSystemRoles());
	command.setUserId(userProfileId);
	command.setProjectRole(termTranslatorRole);
	command.setRoleTypeInfo(PROJECT_ROLES_KEY);

	ManualTaskHandler taskHandler = getHandler("assign role");
	TaskResponse taskResponse = taskHandler.processTasks(new Long[] { userProfileId }, null, command, null);
	assertNotNull(taskResponse);

	TmPowerUserProjectRole savedPowerUserProjectRole = getPowerUserProjectRoleService().findByUserId(12L);
	assertNotNull(savedPowerUserProjectRole);

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(userProfileId) }, null, command);

	TaskModel taskInfo = taskInfos[0];
	ArrayList<Map<String, Object>> projectRoleList = (ArrayList<Map<String, Object>>) taskInfo.getModel()
		.get(PROJECT_ROLES_KEY);
	assertNotNull(projectRoleList);
	Assert.assertEquals(6, projectRoleList.size());

	for (Map<String, Object> map : projectRoleList) {
	    if (map.get("role").equals("term_translator")) {
		Assert.assertEquals(map.get("assigned"), true);
	    } else
		Assert.assertEquals(map.get("assigned"), false);
	}

	ArrayList<Map<String, Object>> systemRoleList = (ArrayList<Map<String, Object>>) taskInfo.getModel()
		.get(SYSTEM_ROLES_KEY);
	Assert.assertNull(systemRoleList);

    }

    public RoleService getRoleService() {
	return _roleService;
    }

    @Test
    public void getSystemRolesTaskInfosTest() {
	Long userProfileId = 6L;

	TmUserProfile userProfile = getUserProfileService().findById(userProfileId);
	assertNotNull(userProfile);

	AssignUserRoleCommand command = new AssignUserRoleCommand();
	command.setSystemRoles(userProfile.getSystemRoles());
	command.setUserId(userProfileId);
	command.setRoleTypeInfo(SYSTEM_ROLES_KEY);

	ManualTaskHandler taskHandler = getHandler("assign role");

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(userProfileId) }, null, command);

	TaskModel taskInfo = taskInfos[0];
	ArrayList<Map<String, Object>> systemRoleList = (ArrayList<Map<String, Object>>) taskInfo.getModel()
		.get(SYSTEM_ROLES_KEY);
	assertNotNull(systemRoleList);
	Assert.assertEquals(systemRoleList.size(), 8);

	for (Map<String, Object> map : systemRoleList) {
	    if (map.get("role").equals("pm")) {
		Assert.assertEquals(map.get("assigned"), true);
	    } else
		Assert.assertEquals(map.get("assigned"), false);
	}

	ArrayList<Map<String, Object>> projectRoleList = (ArrayList<Map<String, Object>>) taskInfo.getModel()
		.get(PROJECT_ROLES_KEY);
	Assert.assertNull(projectRoleList);

    }

    @Test
    public void projectRoleRoundTripTest() {
	Long userProfileId = 12L;

	TmUserProfile userProfile = getUserProfileService().findById(userProfileId);
	assertNotNull(userProfile);

	Role termTranslatorRole = getProjectRoleByUserAndProjectId(1L, 3L);
	assertNotNull(termTranslatorRole);

	AssignUserRoleCommand command = new AssignUserRoleCommand();
	command.setSystemRoles(userProfile.getSystemRoles());
	command.setUserId(userProfileId);
	command.setProjectRole(termTranslatorRole);

	ManualTaskHandler taskHandler = getHandler("assign role");
	TaskResponse taskResponse = taskHandler.processTasks(new Long[] { userProfileId }, null, command, null);
	assertNotNull(taskResponse);

	TmPowerUserProjectRole savedPowerUserProjectRole = getPowerUserProjectRoleService().findByUserId(12L);
	assertNotNull(savedPowerUserProjectRole);
	Assert.assertEquals(savedPowerUserProjectRole.getRole(), termTranslatorRole);
	Assert.assertEquals(savedPowerUserProjectRole.getUserProfile(), userProfile);

	Role proRole = getProjectRoleByUserAndProjectId(1L, 6L);

	command.setProjectRole(proRole);

	taskHandler.processTasks(new Long[] { userProfileId }, null, command, null);

	TmPowerUserProjectRole updatedPowerUserProjectRole = getPowerUserProjectRoleService().findByUserId(12L);
	assertNotNull(updatedPowerUserProjectRole);
	Assert.assertEquals(updatedPowerUserProjectRole.getRole(), proRole);
	Assert.assertEquals(savedPowerUserProjectRole.getUserProfile(), userProfile);

    }

    private Role getProjectRoleByUserAndProjectId(Long projectId, Long userId) {
	List<Role> rolesByUserAndProject = getProjectService().getRolesByUserAndProject(projectId, userId);

	for (Role role : rolesByUserAndProject) {
	    if (role.getRoleType().equals(RoleTypeEnum.CONTEXT))
		return role;
	}
	return null;
    }

}
