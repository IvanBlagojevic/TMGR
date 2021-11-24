package org.gs4tr.termmanager.service.manualtask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.BatchMessage;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.view.ProjectReport;
import org.gs4tr.termmanager.service.ProjectDetailService;
import org.gs4tr.termmanager.service.batch.BatchJob;
import org.gs4tr.termmanager.service.batch.executor.BatchJobExecutor;
import org.gs4tr.termmanager.service.batch.register.BatchJobRegister;
import org.gs4tr.termmanager.service.export.CsvLineBuilder;
import org.gs4tr.termmanager.service.export.ExportFormatEnum;
import org.gs4tr.termmanager.service.model.command.GenerateReportCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoGenerateReportCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.notification.listeners.ReportExportNotificationListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class GenerateReportManualTaskHandler extends AbstractManualTaskHandler {

    public static final String REPORT_URL = "reportURL";

    private static final Log LOG = LogFactory.getLog(GenerateReportManualTaskHandler.class);

    @Autowired
    private BatchJobExecutor _batchJobExecutor;

    @Autowired
    private BatchJobRegister<String> _batchJobRegister;

    @Autowired
    private ProjectDetailService _projectDetailService;

    @Autowired
    private ReportExportNotificationListener _reportExportNotificationListener;

    @Value("${tableau.url.group.by.language:\"\"}")
    private String _reportURL_groupByLanguage;

    @Value("${tableau.url.group.by.project:\"\"}")
    private String _reportURL_groupByProject;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoGenerateReportCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {

	List<String> reportUrls = new ArrayList<>(2);

	String reportURL_groupByProject = getReportURL_groupByProject();
	if (StringUtils.isNotEmpty(reportURL_groupByProject)) {
	    reportUrls.add(reportURL_groupByProject);
	}
	String reportURL_groupByLanguage = getReportURL_groupByLanguage();
	if (StringUtils.isNotEmpty(reportURL_groupByLanguage)) {
	    reportUrls.add(reportURL_groupByLanguage);
	}

	TaskModel model = new TaskModel();
	model.addObject(REPORT_URL, reportUrls.toArray(new String[reportUrls.size()]));

	return new TaskModel[] { model };
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	GenerateReportCommand reportCommand = (GenerateReportCommand) command;

	final boolean groupByLanguage = reportCommand.isGroupByLanguage();

	final String username = TmUserProfile.getCurrentUserName();
	final String fileName = createTempFileName();

	BatchJob batchJob = createBatchJob(groupByLanguage, fileName);

	BatchMessage message = createBatchMessage(username, BatchJobName.REPORT_EXPORT, fileName);
	getBatchJobRegister().registerBatchJob(username, BatchJobName.REPORT_EXPORT);
	getBatchJobExecutor().execute(batchJob, message, getReportExportNotificationListener());

	return createTaskResponse(fileName);
    }

    private void appendHeader(StringBuilder builder, boolean groupByLanguage) {
	String language = groupByLanguage ? "LANGUAGE_ID" : "LANGUAGE_COUNT";
	String[] columns = { "PROJECT_NAME", language, "TERM_COUNT", "LAST_MODIFIED_DATE" };
	String header = CsvLineBuilder.buildLine(columns);
	builder.append(header);
	builder.append(StringConstants.NEW_LINE);
    }

    private void appendReport(StringBuilder builder, ProjectReport report, boolean groupByLanguage) {
	CsvLineBuilder csvBuilder = new CsvLineBuilder();
	csvBuilder.addField(report.getProjectName());
	String language = groupByLanguage ? report.getLanguageId() : String.valueOf(report.getLanguageCount());
	csvBuilder.addField(language);
	csvBuilder.addField(String.valueOf(report.getTermCount()));
	csvBuilder.addField(report.getMaxModifiedDate());

	builder.append(csvBuilder);
	builder.append(StringConstants.NEW_LINE);
    }

    private BatchJob createBatchJob(final boolean groupByLanguage, final String fileName) {
	return (NotifyingMessageListener<BatchMessage> listener, BatchMessage message) -> {
	    StringBuilder builder = new StringBuilder();
	    appendHeader(builder, groupByLanguage);

	    List<ProjectReport> reports = getProjectDetailService().getAllProjectsReport(groupByLanguage);

	    if (CollectionUtils.isNotEmpty(reports)) {
		for (ProjectReport report : reports) {
		    appendReport(builder, report, groupByLanguage);
		}
	    }
	    createFile(fileName, builder);
	    listener.notify(message);
	};
    }

    private BatchMessage createBatchMessage(String sessionId, BatchJobName batchProcessName, String resourceTicket) {
	BatchMessage message = new BatchMessage();

	Map<String, Object> propertiesMap = message.getPropertiesMap();

	propertiesMap.put(BatchMessage.SESSION_ID_KEY, sessionId);
	propertiesMap.put(BatchMessage.BATCH_PROCESS, batchProcessName);
	propertiesMap.put(BatchMessage.ITEMS_TO_PROCESS_KEY, resourceTicket);

	return message;
    }

    private void createFile(final String fileName, StringBuilder builder) {
	File tempFile = new File(StringConstants.TEMP_DIR, FilenameUtils.getName(fileName));
	try (OutputStream output = new FileOutputStream(tempFile)) {
	    output.write(builder.toString().getBytes(StandardCharsets.UTF_8));
	    output.flush();
	} catch (IOException e) {
	    LOG.error(e.getMessage(), e);
	}
    }

    private TaskResponse createTaskResponse(final String fileName) {
	TaskResponse taskResponse = new TaskResponse(new Ticket(fileName));
	taskResponse.addObject(ManualTaskHandlerUtils.START_PINGING, Boolean.TRUE);
	return taskResponse;
    }

    private String createTempFileName() {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

	StringBuilder builder = new StringBuilder();
	builder.append(sdf.format(new Date()));
	builder.append(StringConstants.DOT);
	builder.append(ExportFormatEnum.CSVEXPORT.getFileFormat());

	return builder.toString();
    }

    private BatchJobExecutor getBatchJobExecutor() {
	return _batchJobExecutor;
    }

    private BatchJobRegister<String> getBatchJobRegister() {
	return _batchJobRegister;
    }

    private ProjectDetailService getProjectDetailService() {
	return _projectDetailService;
    }

    private ReportExportNotificationListener getReportExportNotificationListener() {
	return _reportExportNotificationListener;
    }

    private String getReportURL_groupByLanguage() {
	return _reportURL_groupByLanguage;
    }

    private String getReportURL_groupByProject() {
	return _reportURL_groupByProject;
    }
}
