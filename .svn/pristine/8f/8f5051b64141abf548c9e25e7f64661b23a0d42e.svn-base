package org.gs4tr.termmanager.tests;

import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Test;

@TestSuite("get_project_user_languages")
public class GetProjectUserLanguagesTaskHandlerTest extends AbstractSpringServiceTests {

    @Test
    @TestCase("get_task_infos")
    public void testGetTaskInfos() throws Exception {
	String taskName = "get project user languages";

	ManualTaskHandler taskHandler = getHandler(taskName);

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { 1L }, taskName, null);

	String result = JsonUtils.writeValueAsString(taskInfos);

	assertJSONResponse(result, "getProjectUserLanguagesValidation.json");
    }
}
