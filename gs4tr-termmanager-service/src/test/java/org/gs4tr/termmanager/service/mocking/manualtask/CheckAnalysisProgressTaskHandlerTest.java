package org.gs4tr.termmanager.service.mocking.manualtask;

import static org.gs4tr.termmanager.cache.model.CacheName.ANALYSIS_PROGRESS_STATUS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.service.file.analysis.model.Alert;
import org.gs4tr.termmanager.service.file.analysis.model.AlertSubject;
import org.gs4tr.termmanager.service.file.analysis.model.AlertType;
import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisAlerts;
import org.gs4tr.termmanager.service.file.analysis.response.FilesAnalysisResponse;
import org.gs4tr.termmanager.service.manualtask.CheckAnalysisProgressTaskHandler;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.model.command.CheckAnalysisProgressCommand;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@TestSuite("manualtask")
public class CheckAnalysisProgressTaskHandlerTest extends AbstractManualtaskTest {

    private static final String TASK_NAME = "check analysis progress";

    @Autowired
    @Qualifier("guavaCacheGateway")
    private CacheGateway<String, FilesAnalysisResponse> _cacheGateway;

    @Autowired
    private CheckAnalysisProgressTaskHandler _taskHandler;

    @Test
    @TestCase("checkAnalysisProgress")
    public void checkAnalysisProgressWhenAnalysisCompletedExceptionallyTest() throws Exception {
	CheckAnalysisProgressCommand command = getModelObject("command", CheckAnalysisProgressCommand.class);

	FilesAnalysisResponse response = getModelObject("responseCompletedExceptionally", FilesAnalysisResponse.class);
	when(getCacheGateway().get(ANALYSIS_PROGRESS_STATUS, command.getProcessingId())).thenReturn(response);

	Map<String, Object> model = getTaskHandler().getTaskInfos(new Long[] { 1L }, TASK_NAME, command)[0].getModel();

	verify(getCacheGateway()).get(eq(ANALYSIS_PROGRESS_STATUS), eq(command.getProcessingId()));

	assertEquals(1, model.size());
	assertTrue((boolean) model.get(CheckAnalysisProgressTaskHandler.COMPLETED_EXCEPTIONALY));
    }

    @Test
    @TestCase("checkAnalysisProgress")
    public void checkAnalysisProgressWhenAnalysisCompletedSuccessfullyTest() throws Exception {
	CheckAnalysisProgressCommand command = getModelObject("command", CheckAnalysisProgressCommand.class);

	FilesAnalysisResponse response = getModelObject("responseCompletedSuccessfully", FilesAnalysisResponse.class);
	when(getCacheGateway().get(ANALYSIS_PROGRESS_STATUS, command.getProcessingId())).thenReturn(response);

	Map<String, Object> model = getTaskHandler().getTaskInfos(new Long[] { 1L }, TASK_NAME, command)[0].getModel();

	verify(getCacheGateway()).get(eq(ANALYSIS_PROGRESS_STATUS), eq(command.getProcessingId()));

	assertEquals(7, model.size());
	assertTrue((boolean) model.get(CheckAnalysisProgressTaskHandler.COMPLETED));

	assertNotNull(model.get(CheckAnalysisProgressTaskHandler.FILE_ANALYSIS_ALERTS));
	assertNotNull(model.get(CheckAnalysisProgressTaskHandler.IMPORT_LANGUAGES_REPORTS));
	assertNotNull(model.get(CheckAnalysisProgressTaskHandler.IMPORT_ATTRIBUTES_REPORTS));
	assertNotNull(model.get(CheckAnalysisProgressTaskHandler.EXISTING_PROJECT_ATTRIBUTES));
	assertNotNull(model.get(CheckAnalysisProgressTaskHandler.IMPORT_TYPE_REPORTS));
	assertNotNull(model.get(CheckAnalysisProgressTaskHandler.OVERWRITE_BY_TERM_ENTRY_ID));
    }

    @Test
    @TestCase("checkAnalysisProgress")
    public void checkAnalysisProgressWhenAnalysisNotCompletedTest() throws Exception {
	CheckAnalysisProgressCommand command = getModelObject("command", CheckAnalysisProgressCommand.class);

	FilesAnalysisResponse response = new FilesAnalysisResponse();
	when(getCacheGateway().get(ANALYSIS_PROGRESS_STATUS, command.getProcessingId())).thenReturn(response);

	Map<String, Object> model = getTaskHandler().getTaskInfos(new Long[] { 1L }, TASK_NAME, command)[0].getModel();

	verify(getCacheGateway()).get(eq(ANALYSIS_PROGRESS_STATUS), eq(command.getProcessingId()));

	assertEquals(1, model.size());
	assertFalse((boolean) model.get(CheckAnalysisProgressTaskHandler.COMPLETED));
    }

    @Test
    @TestCase("checkAnalysisProgress")
    public void checkAnalysisProgressWhenResponseContainsAlerts() {

	CheckAnalysisProgressCommand command = getModelObject("command", CheckAnalysisProgressCommand.class);
	FilesAnalysisResponse response = createFileAnalysisResponse();

	when(getCacheGateway().get(ANALYSIS_PROGRESS_STATUS, command.getProcessingId())).thenReturn(response);

	TaskModel[] taksModel = getTaskHandler().getTaskInfos(null, TASK_NAME, command);

	Map<String, Object> model = taksModel[0].getModel();
	assertNotNull(model);
	assertEquals(7, model.size());

	Object alert = model.get(CheckAnalysisProgressTaskHandler.FILE_ANALYSIS_ALERTS);
	List<FileAnalysisAlerts> alerts = (List<FileAnalysisAlerts>) alert;
	assertEquals(alerts.size(), 1);

	List<Alert> alertList = alerts.get(0).getAlerts();
	assertNotNull(alertList);
	assertEquals(1, alertList.size());
	assertNotNull(alertList.get(0).getMessage());

    }

    private FilesAnalysisResponse createFileAnalysisResponse() {
	FilesAnalysisResponse response = getModelObject("responseWithAlerts", FilesAnalysisResponse.class);

	Alert alert = new Alert(AlertSubject.HEADER_CHECK, AlertType.ERROR, "Invalid language code");

	FileAnalysisAlerts fileAnalysisAlert = new FileAnalysisAlerts("import.xls");
	fileAnalysisAlert.getAlerts().add(alert);

	response.getFileAnalysisAlerts().add(fileAnalysisAlert);

	return response;
    }

    private CacheGateway<String, FilesAnalysisResponse> getCacheGateway() {
	return _cacheGateway;
    }

    private CheckAnalysisProgressTaskHandler getTaskHandler() {
	return _taskHandler;
    }
}
