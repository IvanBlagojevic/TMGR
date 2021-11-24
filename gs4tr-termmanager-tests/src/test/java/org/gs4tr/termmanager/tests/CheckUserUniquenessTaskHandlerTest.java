package org.gs4tr.termmanager.tests;

import junit.framework.Assert;

import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.PropertyCheckCommand;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Test;

@TestSuite("check_user_name")
public class CheckUserUniquenessTaskHandlerTest extends AbstractSpringServiceTests {
    private static final String CHECK_USER_NAME_TASK_NAME = "check user uniqueness";

    @Test
    @TestCase("get_task_infos")
    public void checkTmNameGetTest() throws Exception {

	Long userId = 6L;

	TmUserProfile userProfile = getUserProfileService().findById(userId);

	ManualTaskHandler taskHandler = getHandler(CHECK_USER_NAME_TASK_NAME);

	Object cmd = getTaskHandlerCommand(taskHandler, "checkUserNameTestInput.json",
		new String[] { "$userName", userProfile.getUserInfo().getUserName() });

	testCommand(cmd);

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] {}, CHECK_USER_NAME_TASK_NAME, cmd);

	String result = JsonUtils.writeValueAsString(taskInfos).toString();

	assertJSONResponse(result, "checkUserNameResponseRule.json");

	Boolean exists = (Boolean) taskInfos[0].getModel().get("exists");

	Assert.assertTrue(exists);

	String userName = "nonExisting";
	Object newCmd = getTaskHandlerCommand(taskHandler, "checkUserNameTestInput.json",
		new String[] { "$userName", userName });

	testCommand(newCmd);

	TaskModel[] newtaskInfos = taskHandler.getTaskInfos(new Long[] {}, CHECK_USER_NAME_TASK_NAME, newCmd);

	String newResult = JsonUtils.writeValueAsString(newtaskInfos).toString();

	assertJSONResponse(newResult, "checkUserNameResponseRule.json");

	Boolean newExists = (Boolean) newtaskInfos[0].getModel().get("exists");

	Assert.assertEquals(false, newExists.booleanValue());
    }

    private void testCommand(Object cmd) {
	PropertyCheckCommand propertyCheckCommand = (PropertyCheckCommand) cmd;

	Assert.assertNotNull(propertyCheckCommand);
	Assert.assertNotNull(propertyCheckCommand.getUsername());
    }
}
