package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.DateRange;
import org.gs4tr.termmanager.model.dto.converter.DateRangeConverter;
import org.gs4tr.termmanager.model.dto.converter.DescriptionConverter;
import org.gs4tr.termmanager.service.impl.ExportAdapter;
import org.gs4tr.termmanager.service.impl.ExportDocumentStatusInfo;
import org.gs4tr.termmanager.service.model.command.CheckProgressCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoCheckProgressCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class CheckExportProgressTaskHandler extends AbstractManualTaskHandler {

    private static final String ATTRIBUTES_INCLUDED = "attributesIncluded";

    private static final String CREATION_DATE_RANGE = "creationDateRange";

    private static final String DESCRIPTIONS_FILTER = "descriptionsFilter";

    private static final String EXPORT_FORMAT = "exportFormat";

    private static final String EXPORT_PROCESS_CANCELED = "exportProcessCanceled";

    private static final String EXPORT_PROCESS_FINISHED = "exportProcessFinished"; //$NON-NLS-1$

    private static final String EXPORT_TERM_TYPE = "exportTermType";

    private static final String GENERAL = "general";

    private static final String LANGUAGES = "languages";

    private static final String LANGUAGE_CRITERIA = "languageCriteria";

    private static final String MODIFICATION_DATE_RANGE = "modificationDateRange";

    private static final String PROCESSING_PROGRESS_PERCENTAGE = "processingProgressPercentage"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "projectName";

    private static final String SOURCE_LANGUAGE = "sourceLanguage";

    private static final String TARGET_LANGUAGES = "targetLanguages";

    private static final String TERMS = "terms";

    private static final String TERM_ENTRIES = "termEntries";

    private static final String TOTAL_EXPORT_TIME = "totalExportTime";

    private static final String TOTAL_TERMS_EXPORTED = "totalTermsExported";

    private static final String TOTAL_TERM_ENTRIES_EXPORTED = "totalTermEntriesExported";

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, ExportAdapter> _cacheGateway;

    public CacheGateway<String, ExportAdapter> getCacheGateway() {
	return _cacheGateway;
    }

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoCheckProgressCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {
	String threadName = ((CheckProgressCommand) command).getThreadName();

	ExportDocumentStatusInfo statusInfo = getCacheGateway().get(CacheName.EXPORT_PROGRESS_STATUS, threadName);

	TaskModel taskModel = new TaskModel();
	if (statusInfo == null) {
	    throw new UserException(Messages.getString("CheckExportProgressTaskHandler.2")); //$NON-NLS-1$
	}

	if (statusInfo.getExportInfo() != null && statusInfo.getExportInfo().isProcessingCanceled()) {
	    getCacheGateway().remove(CacheName.EXPORT_PROGRESS_STATUS, threadName);
	    taskModel.addObject(EXPORT_PROCESS_FINISHED, Boolean.TRUE);
	    taskModel.addObject(EXPORT_PROCESS_CANCELED, Boolean.TRUE);
	    taskModel.addObject(PROCESSING_PROGRESS_PERCENTAGE, statusInfo.getProcessingProgress());
	    return new TaskModel[] { taskModel };
	} else if (statusInfo.isProcessingFinished()) {
	    Map<String, Object> generalMap = new HashMap<>();
	    Map<String, Object> termEntriesMap = new HashMap<>();
	    Map<String, Object> termsMap = new HashMap<>();

	    ExportInfo exportInfo = statusInfo.getExportInfo();
	    generalMap.put(PROJECT_NAME, exportInfo.getProjectName());
	    generalMap.put(EXPORT_FORMAT, exportInfo.getExportFormat());
	    generalMap.put(EXPORT_TERM_TYPE, exportInfo.getExportTermType());
	    DateRange creationDateRange = exportInfo.getCreationDateRange();
	    DateRange modificationDateRange = exportInfo.getModificationDateRange();
	    generalMap.put(CREATION_DATE_RANGE, DateRangeConverter.fromInternalToDto(creationDateRange));
	    generalMap.put(MODIFICATION_DATE_RANGE, DateRangeConverter.fromInternalToDto(modificationDateRange));
	    generalMap.put(LANGUAGE_CRITERIA, exportInfo.getLanguageCriteriaEnum().getTypeName());
	    generalMap.put(SOURCE_LANGUAGE, exportInfo.getSourceLanguage());
	    generalMap.put(TARGET_LANGUAGES, exportInfo.getTargetLanguages());
	    generalMap.put(ATTRIBUTES_INCLUDED, exportInfo.getDescriptionsToExport());

	    Set<String> languagesToExport = exportInfo.getLanguagesToExport();
	    List<String> languages = new ArrayList<>();
	    if (CollectionUtils.isNotEmpty(languagesToExport)) {
		languages.addAll(languagesToExport);
		languages.sort(String::compareTo);
	    }
	    generalMap.put(LANGUAGES, languages);
	    generalMap.put(DESCRIPTIONS_FILTER,
		    DescriptionConverter.fromInternalToDto(exportInfo.getDescriptionFilter()));

	    termEntriesMap.put(TOTAL_TERM_ENTRIES_EXPORTED, exportInfo.getTotalTermEntriesExported());

	    termsMap.put(TOTAL_TERMS_EXPORTED, exportInfo.getTotalTermsExported());

	    taskModel.addObject(EXPORT_PROCESS_FINISHED, Boolean.TRUE);
	    taskModel.addObject(EXPORT_PROCESS_CANCELED, Boolean.FALSE);
	    taskModel.addObject(TOTAL_EXPORT_TIME, statusInfo.getTotalExportTimeFormatted());
	    taskModel.addObject(GENERAL, generalMap);
	    taskModel.addObject(TERM_ENTRIES, termEntriesMap);
	    taskModel.addObject(TERMS, termsMap);
	} else {
	    taskModel.addObject(EXPORT_PROCESS_FINISHED, Boolean.FALSE);
	    taskModel.addObject(EXPORT_PROCESS_CANCELED, Boolean.FALSE);
	    taskModel.addObject(PROCESSING_PROGRESS_PERCENTAGE, statusInfo.getProcessingProgress());
	}

	return new TaskModel[] { taskModel };
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	return null;
    }
}
