package org.gs4tr.termmanager.tests;

import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.SendConnectionCommand;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.junit.Assert;
import org.junit.Test;

@org.gs4tr.termmanager.tests.model.TestSuite("send_connection")
public class SendConnectionTaskHandlerTest extends AbstractSpringServiceTests {

    private static final String SEND_CONNECTION_TASK_NAME = "send connection string";

    @Test
    @TestCase("get_task_infos")
    public void sendTmConnectionGetTestWithoutSource() throws Exception {
	ManualTaskHandler taskHandler = getHandler(SEND_CONNECTION_TASK_NAME);

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { 1L, 2L }, SEND_CONNECTION_TASK_NAME, null);

	String result = JsonUtils.writeValueAsString(taskInfos);

	assertJSONResponse(result, "sendConnectionResponseRuleCase1.json");
    }

    @Test
    @TestCase("get_task_infos")
    public void sendTmConnectionGetTestWithSource() throws Exception {
	ManualTaskHandler taskHandler = getHandler(SEND_CONNECTION_TASK_NAME);

	SendConnectionCommand sendCommand = new SendConnectionCommand();
	sendCommand.setSourceLanguage("en-US");

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { 1L, 2L }, SEND_CONNECTION_TASK_NAME, sendCommand);

	String result = JsonUtils.writeValueAsString(taskInfos);

	assertJSONResponse(result, "sendConnectionResponseRuleCase2.json");
    }

    @Test
    @TestCase("process_tasks")
    public void sendTmConnectionPostTest() throws Exception {
	ManualTaskHandler taskHandler = getHandler(SEND_CONNECTION_TASK_NAME);

	Object cmd = getTaskHandlerCommand(taskHandler, "sendConnectionTestInput.json",
		new String[] { "$userTicket", IdEncrypter.encryptGenericId(1) });

	testCommand(cmd);
    }

    private void testCommand(Object cmd) {
	SendConnectionCommand sendTmConnectionCommand = (SendConnectionCommand) cmd;

	Assert.assertNotNull(sendTmConnectionCommand);
    }
}
