package org.gs4tr.termmanager.tests;

import junit.framework.Assert;

import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.service.OrganizationService;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.OrganizationCommand;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("add_organization")
public class AddOrganizationTaskHandlerTest extends AbstractSpringServiceTests {

    @Autowired
    private OrganizationService _organizationService;

    @Test
    @TestCase("get_task_infos")
    public void addOrganizationGetTest() throws Exception {
	ManualTaskHandler taskHandler = getHandler("add organization");

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(1) }, null, null);

	String result = JsonUtils.writeValueAsString(taskInfos);

	assertJSONResponse(result, "addOrganizationValidation.json");
    }

    @Test
    @TestCase("process_tasks")
    public void addOrganizationPostTest() {
	ManualTaskHandler taskHandler = getHandler("add organization");

	OrganizationCommand command = (OrganizationCommand) getTaskHandlerCommand(taskHandler, "addOrganization.json");

	taskHandler.processTasks(null, null, command, null);

	TmOrganization org = getOrganizationService().findByName("testOrg");
	Assert.assertNotNull(org);

    }

    public OrganizationService getOrganizationService() {
	return _organizationService;
    }

}
