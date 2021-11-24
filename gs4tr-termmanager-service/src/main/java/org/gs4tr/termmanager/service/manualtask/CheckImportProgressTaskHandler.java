package org.gs4tr.termmanager.service.manualtask;

import static java.util.stream.Collectors.toList;
import static org.gs4tr.termmanager.cache.model.CacheName.IMPORT_PROGRESS_STATUS;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.ProjectTerminologyCounts;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.BatchImportSummary;
import org.gs4tr.termmanager.model.dto.BatchImportSummary.TermCounts;
import org.gs4tr.termmanager.model.dto.Language;
import org.gs4tr.termmanager.model.dto.converter.ImportSummaryConverter;
import org.gs4tr.termmanager.service.impl.ImportProgressInfo;
import org.gs4tr.termmanager.service.model.command.CheckImportProgressCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoCheckImportProgressCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.project.terminology.counts.ProjectTerminologyCountsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class CheckImportProgressTaskHandler extends AbstractManualTaskHandler {

    private static final String IMPORT_CANCELED = "importCanceled";
    private static final String IMPORT_PERCENTAGE = "importPercentage";
    private static final String IMPORT_SUMMARY = "importSummary";
    private static final Log LOG = LogFactory.getLog(CheckImportProgressTaskHandler.class);

    @Autowired
    @Qualifier("guavaCacheGateway")
    private CacheGateway<String, ImportProgressInfo> _cacheGateway;

    @Autowired
    private ProjectTerminologyCountsProvider _terminologyCountsProvider;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoCheckImportProgressCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {
	Set<String> threadNames = ((CheckImportProgressCommand) command).getImportThreadNames();

	Map<String, ImportProgressInfo> importInfos = getCacheGateway().getAll(IMPORT_PROGRESS_STATUS, threadNames);

	final TaskModel taskModel = new TaskModel();

	int batchImportPercentage = getBatchImportPercentage(importInfos);

	if (isImportCanceled(importInfos)) {
	    taskModel.addObject(IMPORT_CANCELED, Boolean.TRUE);

	} else if (batchImportPercentage == 100) {
	    BatchImportSummary summary = getBatchImportSummary(importInfos);
	    taskModel.addObject(IMPORT_SUMMARY, summary);
	    LogHelper.info(LOG, String.format(Messages.getString("CheckImportProgressTaskHandler.2"),
		    importInfos.size(), summary.getImportTime())); // $NON-NLS-1$
	}

	taskModel.addObject(IMPORT_PERCENTAGE, batchImportPercentage);

	return new TaskModel[] { taskModel };
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> items) {
	throw new UnsupportedOperationException();
    }

    private int getBatchImportPercentage(Map<String, ImportProgressInfo> importInfos) {
	int totalPercentage = importInfos.values().stream().mapToInt(ImportProgressInfo::getPercentage).sum();
	return totalPercentage / importInfos.size();
    }

    private BatchImportSummary getBatchImportSummary(final Map<String, ImportProgressInfo> importInfos) {
	BatchImportSummary result = ImportSummaryConverter.fromImportSummariesToBatch(getImportSummaries(importInfos));

	ImportSummary summary = importInfos.values().iterator().next().getImportSummary();
	ProjectTerminologyCounts preTotal = summary.getPreImportCounts();
	ProjectTerminologyCounts postTotal = getTerminologyCountsProvider()
		.getProjectTerminologyCounts(preTotal.getProjectId(), preTotal.getLanguages());
	result.getTermEntries().addPostTotal(postTotal.getNumberOfTermEntries());

	Map<String, Long> numberOfTerms = postTotal.getNumberOfTermsByLanguage();
	Iterator<TermCounts> termsCounts = result.getTerms().iterator();
	while (termsCounts.hasNext()) {
	    TermCounts termCount = termsCounts.next();
	    Language language = termCount.getLanguage();
	    long count = numberOfTerms.get(language.getLocale()).longValue();
	    termCount.addPostTotal(count);
	}

	return result;
    }

    private CacheGateway<String, ImportProgressInfo> getCacheGateway() {
	return _cacheGateway;
    }

    private List<ImportSummary> getImportSummaries(Map<String, ImportProgressInfo> importInfos) {
	return importInfos.values().stream().map(ImportProgressInfo::getImportSummary).collect(toList());
    }

    private ProjectTerminologyCountsProvider getTerminologyCountsProvider() {
	return _terminologyCountsProvider;
    }

    private boolean isImportCanceled(Map<String, ImportProgressInfo> importInfos) {
	return importInfos.values().stream().anyMatch(ImportProgressInfo::isCanceled);
    }
}
