package org.gs4tr.termmanager.tests;

import java.io.IOException;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;

@TestSuite("generate_report")
public class GenerateReportManualTaskHandlerTest extends AbstractSpringServiceTests {

    @Test
    @TestCase("process_tasks")
    public void processTasksTest() throws IOException {
	ManualTaskHandler taskHandler = getHandler("generate report");

	Object command = getTaskHandlerCommand(taskHandler, "generateReport.json");

	TaskResponse response = taskHandler.processTasks(null, null, command, null);
	Assert.assertNotNull(response);

	Ticket responseTicket = response.getResponseTicket();
	Assert.assertNotNull(responseTicket);

	String resourceTicket = responseTicket.getTicketId();
	Assert.assertNotNull(resourceTicket);
    }
}
