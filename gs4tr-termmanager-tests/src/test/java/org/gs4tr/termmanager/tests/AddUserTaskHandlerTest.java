package org.gs4tr.termmanager.tests;

import static org.junit.Assert.assertFalse;

import java.util.List;

import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;

@TestSuite("add_user")
public class AddUserTaskHandlerTest extends AbstractSpringServiceTests {

    public static final String ADD_USER_TASK_NAME = "add user";

    private static final List<String> ROLES = java.util.Arrays.asList("power_user", "translator_user", "super_user",
	    "term_translator", "pro role", "generic_user");

    @Test
    @TestCase("get_task_infos")
    public void addUserGetTest() {
	ManualTaskHandler taskHandler = getHandler(ADD_USER_TASK_NAME);

	taskHandler.getTaskInfos(null, null, null);

	assertFalse(false); // Not checking json data, just calling getTaskInfos
    }

    @Test
    @TestCase("process_tasks")
    public void addUserPostTest() {
	ManualTaskHandler taskHandler = getHandler(ADD_USER_TASK_NAME);
	String username = "betmen";
	Object command = getTaskHandlerCommand(taskHandler, "addUser.json", new String[] { "$userName", username });

	taskHandler.processTasks(null, null, command, null);

	// Is there such user?
	UserProfileService userProfileService = getUserProfileService();
	Long userProfileId = userProfileService.findUserProfileIdByUserName(username);
	TmUserProfile userProfile = userProfileService.findById(userProfileId);
	Assert.assertNotNull(userProfile);

	UserInfo userInfo = userProfile.getUserInfo();
	Assert.assertNotNull(userInfo);
	Assert.assertEquals(username, (userInfo.getUserName()));
    }

    @Test
    public void getAllProjectRolesTest() {
	ManualTaskHandler taskHandler = getHandler(ADD_USER_TASK_NAME);

	TaskModel[] taskInfos = taskHandler.getTaskInfos(null, ADD_USER_TASK_NAME, null);

	TaskModel taskModel = taskInfos[0];

	List<String> allProjectRoleIds = (List<String>) taskModel.getModel().get("allProjectRoles");

	Assert.assertNotNull(allProjectRoleIds);
	Assert.assertEquals(allProjectRoleIds.size(), 6);
	Assert.assertTrue(allProjectRoleIds.containsAll(ROLES));
    }
}
