package org.gs4tr.termmanager.service.xls.report.processor;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.xls.report.XlsReport;
import org.gs4tr.termmanager.service.impl.ImportProgressInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ImportSummaryReportProcessorImpl implements ImportSummaryReportProcessor {

    private static final Log LOGGER = LogFactory.getLog(ImportSummaryReportProcessorImpl.class);

    @Autowired
    @Qualifier("guavaCacheGateway")
    private CacheGateway<String, ImportProgressInfo> _cacheGateway;

    @Override
    public void discardImportProgressInfos(Set<String> keys) {
	if (CollectionUtils.isEmpty(keys)) {
	    return;
	}

	for (String key : keys) {
	    getCacheGateway().remove(CacheName.IMPORT_PROGRESS_STATUS, key);
	}
    }

    @Override
    public XlsReport generateImportSummaryReport(Set<String> keys) {
	LogHelper.debug(LOGGER, String.format(Messages.getString("ImportSummaryReportProcessorImpl.1"), keys)); //$NON-NLS-1$
	return createFinalReport(keys);
    }

    private XlsReport createFinalReport(Set<String> keys) {
	XlsReport finalReport = new XlsReport();

	Map<String, ImportProgressInfo> infos = getCacheGateway().getAll(CacheName.IMPORT_PROGRESS_STATUS, keys);
	List<XlsReport> reports = getImportSummarieReports(infos);
	if (CollectionUtils.isEmpty(reports)) {
	    return finalReport;
	}

	reports.stream().forEach(r -> finalReport.getSheets().addAll(r.getSheets()));
	return finalReport;
    }

    private CacheGateway<String, ImportProgressInfo> getCacheGateway() {
	return _cacheGateway;
    }

    private List<XlsReport> getImportSummarieReports(Map<String, ImportProgressInfo> importInfos) {
	return importInfos.values().stream().map(i -> i.getImportSummary().getReport()).collect(toList());
    }
}
