package org.gs4tr.termmanager.tests;

import junit.framework.Assert;

import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.PropertyCheckCommand;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Test;

@TestSuite("check_project_name")
public class CheckProjectUniquenessTaskHandlerTest extends AbstractSpringServiceTests {
    private static final String CHECK_PROJECT_NAME_TASK_NAME = "check project name uniqueness";

    @Test
    @TestCase("get_task_infos")
    public void checkTmNameGetTest() throws Exception {

	Long projectId = 1L;

	TmProject project = getProjectService().findById(projectId);

	ManualTaskHandler taskHandler = getHandler(CHECK_PROJECT_NAME_TASK_NAME);

	Object cmd = getTaskHandlerCommand(taskHandler, "checkProjectNameTestInput.json",
		new String[] { "$projectName", project.getProjectInfo().getName() });

	testCommand(cmd);

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] {}, CHECK_PROJECT_NAME_TASK_NAME, cmd);

	String result = JsonUtils.writeValueAsString(taskInfos).toString();

	assertJSONResponse(result, "checkProjectNameResponseRule.json");

	Boolean exists = (Boolean) taskInfos[0].getModel().get("exists");

	Assert.assertTrue(exists);

	String projectName = "nonExisting";
	Object newCmd = getTaskHandlerCommand(taskHandler, "checkProjectNameTestInput.json",
		new String[] { "$projectName", projectName });

	testCommand(newCmd);

	TaskModel[] newtaskInfos = taskHandler.getTaskInfos(new Long[] {}, CHECK_PROJECT_NAME_TASK_NAME, newCmd);

	String newResult = JsonUtils.writeValueAsString(newtaskInfos).toString();

	assertJSONResponse(newResult, "checkProjectNameResponseRule.json");

	Boolean newExists = (Boolean) newtaskInfos[0].getModel().get("exists");

	Assert.assertEquals(false, newExists.booleanValue());
    }

    private void testCommand(Object cmd) {
	PropertyCheckCommand propertyCheckCommand = (PropertyCheckCommand) cmd;

	Assert.assertNotNull(propertyCheckCommand);
	Assert.assertNotNull(propertyCheckCommand.getProjectName());
    }
}
