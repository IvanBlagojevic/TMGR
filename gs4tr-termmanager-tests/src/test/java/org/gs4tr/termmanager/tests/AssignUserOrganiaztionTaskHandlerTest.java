package org.gs4tr.termmanager.tests;

import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;

@TestSuite("assign_organization")
public class AssignUserOrganiaztionTaskHandlerTest extends AbstractSpringServiceTests {

    @Test
    @TestCase("get_task_infos")
    public void addOrganizationGetTest() throws Exception {
	ManualTaskHandler taskHandler = getHandler("assign organization");

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(6) }, null, null);

	String result = JsonUtils.writeValueAsString(taskInfos);

	Assert.assertNotNull(result);
    }

}
