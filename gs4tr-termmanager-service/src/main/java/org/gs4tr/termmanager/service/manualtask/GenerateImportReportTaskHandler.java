package org.gs4tr.termmanager.service.manualtask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.Validate;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
import org.gs4tr.foundation.modules.entities.model.ProjectInfo;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.BatchMessage;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.xls.report.XlsReport;
import org.gs4tr.termmanager.model.xls.report.XlsReportRow;
import org.gs4tr.termmanager.model.xls.report.XlsReportSheet;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.batch.BatchJob;
import org.gs4tr.termmanager.service.batch.executor.BatchJobExecutor;
import org.gs4tr.termmanager.service.batch.register.BatchJobRegister;
import org.gs4tr.termmanager.service.export.ExportFormatEnum;
import org.gs4tr.termmanager.service.model.command.ImportReportCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoImportReportCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.notification.listeners.ImportReportNotificationListener;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.termmanager.service.xls.report.XlsImportReportWriter;
import org.gs4tr.termmanager.service.xls.report.processor.ImportSummaryReportProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.aspose.cells.Worksheet;

public class GenerateImportReportTaskHandler extends AbstractManualTaskHandler {

    private static final String ACTION = "Summary"; //$NON-NLS-1$

    @Autowired
    private BatchJobExecutor _batchJobExecutor;

    @Autowired
    private BatchJobRegister<String> _batchJobRegister;

    @Autowired
    private ImportReportNotificationListener _importReportNotificationListener;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private ImportSummaryReportProcessor _reportProcessor;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoImportReportCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	ImportReportCommand cmd = (ImportReportCommand) command;

	Long projectId = cmd.getProjectId();
	Validate.notNull(projectId, Messages.getString("GenerateImportReportTaskHandler.1")); //$NON-NLS-1$

	Set<String> importThreadNames = cmd.getImportThreadNames();

	Validate.notEmpty(importThreadNames, Messages.getString("GenerateImportReportTaskHandler.0")); //$NON-NLS-1$

	XlsReport report = getReportProcessor().generateImportSummaryReport(importThreadNames);

	String fileName = createTempFileName(projectId);

	BatchJob batchJob = createBatchJob(importThreadNames, fileName, report);

	String username = TmUserProfile.getCurrentUserName();

	BatchMessage message = createBatchMessage(username, fileName, report);

	getBatchJobRegister().registerBatchJob(username, BatchJobName.IMPORT_REPORT);

	getBatchJobExecutor().execute(batchJob, message, getImportReportNotificationListener());

	return createTaskResponse(fileName);
    }

    private BatchJob createBatchJob(final Set<String> importThreadNames, String fileName, final XlsReport report) {
	return (NotifyingMessageListener<BatchMessage> listener, BatchMessage message) -> {
	    createReportFile(importThreadNames, fileName, report);

	    listener.notify(message);
	};
    }

    private BatchMessage createBatchMessage(String sessionId, String key, XlsReport report) {
	BatchMessage message = new BatchMessage();

	Map<String, Object> propertiesMap = message.getPropertiesMap();

	propertiesMap.put(BatchMessage.SESSION_ID_KEY, sessionId);
	propertiesMap.put(BatchMessage.BATCH_PROCESS, BatchJobName.IMPORT_REPORT);
	propertiesMap.put(BatchMessage.ITEMS_TO_PROCESS_KEY, key);
	propertiesMap.put(BatchMessage.DISPLAY_FAILED_NOTIFICATION, Boolean.FALSE);

	if (report.isEmpty()) {
	    propertiesMap.put(BatchMessage.FAIL_ITEMS_KEY, key);
	    propertiesMap.put(BatchMessage.DISPLAY_FAILED_NOTIFICATION, Boolean.TRUE);
	    propertiesMap.put(BatchMessage.EXCEPTION_MESSAGE, Messages.getString("unable.to.generate.report"));
	    propertiesMap.put(BatchMessage.EXCEPTION_DETAILS,
		    Messages.getString("there.is.no.any.information.in.the.report.file"));
	}

	return message;
    }

    private void createReportFile(final Set<String> importThreadNames, String fileName, final XlsReport report) {
	if (report.isEmpty()) {
	    getReportProcessor().discardImportProgressInfos(importThreadNames);
	    return;
	}

	List<XlsReportSheet> sheets = report.getSheets();

	XlsImportReportWriter writer = new XlsImportReportWriter();

	int index = 0;
	for (XlsReportSheet reportSheet : sheets) {
	    String sheetName = Locale.get(reportSheet.getTargetLanguageId()).getDisplayName();

	    Worksheet xlsSheet = writer.addSheet(sheetName, index);
	    index++;

	    try {
		writer.writeHeader(xlsSheet, reportSheet.getHeader());
		writer.writeTopHeader(xlsSheet);
		List<XlsReportRow> rows = reportSheet.getRows();
		for (XlsReportRow r : rows) {
		    writer.writeRow(xlsSheet, r.getCells());
		}
	    } catch (Exception e) {
		throw new RuntimeException(e.getMessage(), e);
	    }
	}

	File reportFile = new File(StringConstants.TEMP_DIR, FilenameUtils.getName(fileName));
	writer.saveReport(reportFile);

	getReportProcessor().discardImportProgressInfos(importThreadNames);
    }

    private TaskResponse createTaskResponse(final String fileName) {
	TaskResponse taskResponse = new TaskResponse(new Ticket(fileName));
	taskResponse.addObject(ManualTaskHandlerUtils.START_PINGING, Boolean.TRUE);
	return taskResponse;
    }

    private String createTempFileName(Long projectId) {
	TmProject project = getProjectService().load(projectId);

	ProjectInfo projectInfo = project.getProjectInfo();
	String projectName = projectInfo.getName();
	String shortCode = projectInfo.getShortCode();

	SimpleDateFormat sdf = new SimpleDateFormat(ServiceUtils.DATE_FORMAT);

	StringBuilder builder = new StringBuilder();
	builder.append(projectName);
	builder.append(StringConstants.UNDERSCORE);
	builder.append(shortCode);
	builder.append(StringConstants.UNDERSCORE);
	builder.append(ACTION);
	builder.append(StringConstants.UNDERSCORE);
	builder.append(sdf.format(new Date()));
	builder.append(StringConstants.DOT);
	builder.append(ExportFormatEnum.XLSX.getFileFormat());

	return builder.toString();
    }

    private BatchJobExecutor getBatchJobExecutor() {
	return _batchJobExecutor;
    }

    private BatchJobRegister<String> getBatchJobRegister() {
	return _batchJobRegister;
    }

    private ImportReportNotificationListener getImportReportNotificationListener() {
	return _importReportNotificationListener;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private ImportSummaryReportProcessor getReportProcessor() {
	return _reportProcessor;
    }
}
