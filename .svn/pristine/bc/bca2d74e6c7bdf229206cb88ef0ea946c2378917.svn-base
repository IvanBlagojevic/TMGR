package org.gs4tr.termmanager.service.manualtask;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.file.analysis.response.FilesAnalysisResponse;
import org.gs4tr.termmanager.service.model.command.CheckAnalysisProgressCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoCheckAnalysisProgressCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class CheckAnalysisProgressTaskHandler extends AbstractManualTaskHandler {

    public static final String COMPLETED = "completed"; //$NON-NLS-1$
    public static final String COMPLETED_EXCEPTIONALY = "completedExceptionally"; //$NON-NLS-1$
    public static final String EXISTING_PROJECT_ATTRIBUTES = "existingProjectAttributes"; //$NON-NLS-1$
    public static final String FILE_ANALYSIS_ALERTS = "fileAnalysisAlerts"; //$NON-NLS-1$
    public static final String IMPORT_ATTRIBUTES_REPORTS = "importAttributeReports"; //$NON-NLS-1$
    public static final String IMPORT_LANGUAGES_REPORTS = "importLanguageReports"; //$NON-NLS-1$
    public static final String IMPORT_TYPE_REPORTS = "importTypeReports"; //$NON-NLS-1$
    public static final Log LOG = LogFactory.getLog(CheckAnalysisProgressTaskHandler.class);
    public static final String OVERWRITE_BY_TERM_ENTRY_ID = "finalyOverwriteByTermEntryId"; //$NON-NLS-1$

    @Autowired
    @Qualifier("guavaCacheGateway")
    private CacheGateway<String, FilesAnalysisResponse> _cacheGateway;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoCheckAnalysisProgressCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {
	String processingId = ((CheckAnalysisProgressCommand) command).getProcessingId();
	Validate.notEmpty(processingId, Messages.getString("processingId.must.not.be.empty"));

	FilesAnalysisResponse response = getCacheGateway().get(CacheName.ANALYSIS_PROGRESS_STATUS, processingId);
	TaskModel taskModel = new TaskModel();
	if (response.isCompleted()) {
	    getCacheGateway().remove(CacheName.ANALYSIS_PROGRESS_STATUS, processingId);
	    if (response.isCompletedExceptionally()) {
		LogHelper.info(LOG, Messages.getString("an.error.was.encountered.while.analysing.the.file"));
		taskModel.addObject(COMPLETED_EXCEPTIONALY, Boolean.TRUE);
		return new TaskModel[] { taskModel };
	    }
	    populateTaskModel(taskModel, response);
	}

	taskModel.addObject(COMPLETED, response.isCompleted());
	return new TaskModel[] { taskModel };
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	throw new UnsupportedOperationException();
    }

    private CacheGateway<String, FilesAnalysisResponse> getCacheGateway() {
	return _cacheGateway;
    }

    private void populateTaskModel(TaskModel tm, FilesAnalysisResponse r) {
	tm.addObject(FILE_ANALYSIS_ALERTS, r.getFileAnalysisAlerts());
	tm.addObject(IMPORT_LANGUAGES_REPORTS, r.getImportLanguageReports());
	tm.addObject(IMPORT_ATTRIBUTES_REPORTS, r.getImportAttributeReports());
	tm.addObject(EXISTING_PROJECT_ATTRIBUTES, r.getAttributesByLevel());
	tm.addObject(IMPORT_TYPE_REPORTS, r.getImportTypeReports());
	tm.addObject(OVERWRITE_BY_TERM_ENTRY_ID, r.isOverwriteByTermEntryId());
    }
}
