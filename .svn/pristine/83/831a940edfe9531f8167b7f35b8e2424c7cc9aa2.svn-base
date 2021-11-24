package org.gs4tr.termmanager.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.UserCommand;
import org.gs4tr.termmanager.service.model.command.UserInfoCommand;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Test;

@TestSuite("edit_user")
public class EditUserTaskHandlerTest extends AbstractSpringServiceTests {

    public static final String EDIT_USER_TASK_NAME = "edit user";

    @Test
    @TestCase("edit_task_infos")
    public void editUserGetTest() {
	ManualTaskHandler taskHandler = getHandler(EDIT_USER_TASK_NAME);

	taskHandler.getTaskInfos(new Long[] { 3L }, EDIT_USER_TASK_NAME, null);

	assertFalse(false); // NOTE: not checking json data
    }

    @Test
    @TestCase("process_tasks")
    public void editUserPostTest() {
	// Get user profile
	Long userProfileId = 3L;
	UserProfileService userProfileService = getUserProfileService();
	TmUserProfile userProfile = userProfileService.findById(userProfileId);
	assertNotNull(userProfile);

	// Define new userName
	String newUserName = "jocker";

	// Get task
	ManualTaskHandler taskHandler = getHandler(EDIT_USER_TASK_NAME);
	Object command = getTaskHandlerCommand(taskHandler, "editUser.json", new String[] { "$userName", newUserName });

	// processTask
	TaskResponse response = taskHandler.processTasks(null, new Long[] { userProfileId }, command, null);
	assertNotNull(response);

	// Get edited user and see if it is changed
	TmUserProfile changedUserProfile = userProfileService.findById(userProfileId);
	assertNotNull(changedUserProfile);

	UserInfo changedUserInfo = changedUserProfile.getUserInfo();

	assertEquals(newUserName, (changedUserInfo.getFirstName()));
	assertEquals("superment", (changedUserInfo.getLastName()));
    }

    @Test
    @TestCase("process_tasks")
    public void usersPasswordWillChangeIfWeSendValidStringInEditDialogue() {

	Long userProfileId = 3L;
	UserProfileService userProfileService = getUserProfileService();
	TmUserProfile userProfile = userProfileService.findById(userProfileId);
	assertNotNull(userProfile);

	String newPassword = "Password1!";

	ManualTaskHandler taskHandler = getHandler(EDIT_USER_TASK_NAME);
	Object command = getTaskHandlerCommand(taskHandler, "editUser2.json",
		new String[] { "$password", newPassword });

	TaskResponse response = taskHandler.processTasks(null, new Long[] { userProfileId }, command, null);
	assertNotNull(response);

	String username = userProfile.getUserInfo().getUserName();
	getSessionService().login(username, newPassword);
    }

    @Test
    @TestCase("process_tasks")
    public void usersPasswordWillNotChangeIfWeSendEmptyStringInEditDialogue() {

	Long userProfileId = 3L;
	UserProfileService userProfileService = getUserProfileService();
	TmUserProfile userProfile = userProfileService.findById(userProfileId);
	assertNotNull(userProfile);

	String currentPassword = userProfile.getUserInfo().getPassword();
	assertNotNull(currentPassword);
	assertEquals("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3", currentPassword);

	String newEmail = "test@yahoo.com";

	ManualTaskHandler taskHandler = getHandler(EDIT_USER_TASK_NAME);
	Object command = getTaskHandlerCommand(taskHandler, "editUser1.json", new String[] { "$email", newEmail });

	UserCommand editCommand = (UserCommand) command;
	UserInfoCommand userInfoCommand = editCommand.getUserInfoCommand();
	assertEquals("", userInfoCommand.getPassword());

	TaskResponse response = taskHandler.processTasks(null, new Long[] { userProfileId }, command, null);
	assertNotNull(response);

	TmUserProfile changedUserProfile = userProfileService.findById(userProfileId);
	assertNotNull(changedUserProfile);

	String changedPassword = changedUserProfile.getUserInfo().getPassword();
	assertEquals(currentPassword, changedPassword);

	getSessionService().login(userProfile.getUserName(), "test");
    }
}
