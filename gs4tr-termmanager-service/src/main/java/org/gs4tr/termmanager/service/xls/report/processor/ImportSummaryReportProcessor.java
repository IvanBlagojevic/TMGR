package org.gs4tr.termmanager.service.xls.report.processor;

import java.util.Set;

import org.gs4tr.termmanager.model.xls.report.XlsReport;

public interface ImportSummaryReportProcessor {

    void discardImportProgressInfos(Set<String> keys);

    XlsReport generateImportSummaryReport(Set<String> keys);
}
