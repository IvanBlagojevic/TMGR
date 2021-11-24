package org.gs4tr.termmanager.tests;

import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.service.TermEntryExporter;
import org.gs4tr.termmanager.service.export.ExportFormatEnum;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.CheckProgressCommand;
import org.gs4tr.termmanager.service.model.command.DownloadExportedDocumentCommand;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("export_tbx_document")
public class ExportDocumentTaskHandlerTest extends AbstractSolrGlossaryTest {

    private static final String TASK_NAME = "export tbx";

    @Autowired
    private TermEntryExporter _termEntryExporter;

    @Test
    @TestCase("process_tasks")
    public void testCancelExport() throws TimeoutException, InterruptedException {
	String taskName = TASK_NAME;

	ManualTaskHandler taskHandler = getHandler(taskName);
	ManualTaskHandler checkExportProgresTaskHandler = getHandler("check export progress");

	String projectTicket = IdEncrypter.encryptGenericId(1L);

	Object command = getTaskHandlerCommand(taskHandler, "exportFromHome.json",
		new String[] { "$projectTicket", projectTicket });

	TaskResponse response = taskHandler.processTasks(null, null, command, null);
	String threadName = (String) response.getModel().get("threadName");

	// Set cancel export (Thread with this name should be canceled)
	_termEntryExporter.requestStopExport(threadName);

	Object commandGetTaskInfos = getTaskHandlerCommand(checkExportProgresTaskHandler, "checkExportProgress.json",
		new String[] { "$threadName", threadName });

	long startTime = System.currentTimeMillis();

	boolean isExportCanceled = false;

	long timeout = 100000;

	while (!isExportCanceled) {
	    if (System.currentTimeMillis() - startTime > timeout) {
		LOG.error("Timeout has occurred while waiting for cancel export.");
		throw new TimeoutException("Timeout has occurred while waiting for cancel export.");
	    }
	    TaskModel[] taskModels = checkExportProgresTaskHandler.getTaskInfos(null, null, commandGetTaskInfos);
	    Assert.assertTrue(taskModels != null);
	    Assert.assertTrue(taskModels.length == 1);
	    Assert.assertTrue(taskModels[0].getModel().get("exportProcessCanceled") != null);
	    Assert.assertTrue(taskModels[0].getModel().get("exportProcessFinished") != null);
	    if ((boolean) taskModels[0].getModel().get("exportProcessFinished")) {
		isExportCanceled = (boolean) taskModels[0].getModel().get("exportProcessCanceled");
	    }
	    Thread.sleep(100);
	}
    }

    @Test
    @TestCase("process_tasks")
    public void testExportFromHome() throws Exception {
	String taskName = TASK_NAME;
	ManualTaskHandler taskHandler = getHandler(taskName);

	String projectTicket = IdEncrypter.encryptGenericId(1L);

	Object command = getTaskHandlerCommand(taskHandler, "exportFromHome.json",
		new String[] { "$projectTicket", projectTicket });

	TaskResponse response = taskHandler.processTasks(null, null, command, null);

	waitServiceThreadPoolThreads();

	checkExportAndDownload(response);
    }

    @Test
    @TestCase("get_task_infos")
    public void testGetTaskInfos() throws Exception {
	String taskName = TASK_NAME;
	ManualTaskHandler taskHandler = getHandler(taskName);

	TaskModel[] taskInfos = taskHandler.getTaskInfos(null, taskName, null);

	String result = JsonUtils.writeValueAsString(taskInfos);

	assertJSONResponse(result, "exportTbxValidation.json");
    }

    @Test
    @TestCase("process_tasks")
    public void testProcessTasks() throws Exception {
	String taskName = TASK_NAME;
	ManualTaskHandler taskHandler = getHandler(taskName);

	String projectTicket = IdEncrypter.encryptGenericId(1L);

	Object command = getTaskHandlerCommand(taskHandler, "exportXlsx.json",
		new String[] { "$projectTicket", projectTicket });

	TaskResponse response = taskHandler.processTasks(null, null, command, null);

	waitServiceThreadPoolThreads();

	checkExportAndDownload(response);
    }

    private void checkExportAndDownload(TaskResponse response) {
	Assert.assertNotNull(response);
	Map<String, Object> model = response.getModel();
	Assert.assertNotNull(model);

	String threadName = model.get("threadName").toString();
	Assert.assertNotNull(threadName);

	CheckProgressCommand checkCommand = new CheckProgressCommand();
	checkCommand.setThreadName(threadName);

	ManualTaskHandler checkExportTaskHandler = getHandler("check export progress");
	TaskModel[] checkTaskInfos = checkExportTaskHandler.getTaskInfos(null, null, checkCommand);

	TaskModel checkExportInfo = checkTaskInfos[0];
	boolean exportProcessFinished = Boolean
		.valueOf(checkExportInfo.getModel().get("exportProcessFinished").toString());

	int count = 1000;
	while (count != 0 && !exportProcessFinished) {
	    checkTaskInfos = checkExportTaskHandler.getTaskInfos(null, null, checkCommand);
	    checkExportInfo = checkTaskInfos[0];
	    exportProcessFinished = Boolean.valueOf(checkExportInfo.getModel().get("exportProcessFinished").toString());
	    count--;
	}

	@SuppressWarnings("unchecked")
	Map<String, Object> termEntriesMap = (Map<String, Object>) checkExportInfo.getModel().get("termEntries");
	Assert.assertNotNull(termEntriesMap);
	Assert.assertTrue(Integer.valueOf(termEntriesMap.get("totalTermEntriesExported").toString()) > 0);

	DownloadExportedDocumentCommand downloadCommand = new DownloadExportedDocumentCommand();
	downloadCommand.setThreadName(threadName);
	downloadCommand.setXslName(ExportFormatEnum.TBX.name());

	ManualTaskHandler downloadExportTaskHandler = getHandler("download exported document");
	TaskResponse downloadResponse = downloadExportTaskHandler.processTasks(null, null, downloadCommand, null);
	RepositoryItem repositoryItem = downloadResponse.getRepositoryItem();
	Assert.assertNotNull(repositoryItem);
	Assert.assertNotNull(repositoryItem.getInputStream());
    }
}
