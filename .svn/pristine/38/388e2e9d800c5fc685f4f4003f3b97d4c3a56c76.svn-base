package org.gs4tr.termmanager.service.xls.report;

import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.xls.report.ReportColor;
import org.gs4tr.termmanager.model.xls.report.XlsReport;
import org.gs4tr.termmanager.model.xls.report.XlsReportCell;
import org.gs4tr.termmanager.model.xls.report.XlsReportRow;
import org.gs4tr.termmanager.model.xls.report.XlsReportSheet;
import org.gs4tr.termmanager.service.AbstractSpringHazelcastTest;
import org.gs4tr.termmanager.service.impl.ImportProgressInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import junit.framework.Assert;

@Ignore
public class ImportSummaryReportPocTest extends AbstractSpringHazelcastTest {

    private static final int CELL_NUM = 10;

    private static final int LIMIT = 100;

    private static final Log LOGGER = LogFactory.getLog(ImportSummaryReportPocTest.class);

    private static final int ROW_NUM = 100;

    private static final int THREAD_NUMBERS = 100;

    @Autowired
    private CacheGateway<String, ImportProgressInfo> _cacheGateway;

    private ExecutorService _executor;

    @After
    public void cleanUp() {
	if (Objects.nonNull(getExecutor()) && !getExecutor().isShutdown()) {
	    shutdownAndAwaitTermination();
	}
	getCacheGateway().removeAll(CacheName.IMPORT_PROGRESS_STATUS);
    }

    @Before
    public void setUp() {
	_executor = Executors.newFixedThreadPool(THREAD_NUMBERS);
	getCacheGateway().removeAll(CacheName.IMPORT_PROGRESS_STATUS);
    }

    @Test
    public void testParallelImport() {
	Set<String> keys = new HashSet<>();

	// run parallel import
	IntStream.rangeClosed(1, LIMIT).forEach(i -> {
	    ImportProgressInfo importInfo = new ImportProgressInfo(1);

	    getExecutor().execute(importSimulation(importInfo.getImportSummary().getReport()));

	    String key = UUID.randomUUID().toString();
	    keys.add(key);

	    getCacheGateway().put(CacheName.IMPORT_PROGRESS_STATUS, key, importInfo);
	});

	// wait parallel import to finish
	shutdownAndAwaitTermination();

	XlsReport finalReport = createFinalReport(keys);

	// validate
	List<XlsReportSheet> sheets = finalReport.getSheets();
	Assert.assertEquals(LIMIT, sheets.size());
	for (XlsReportSheet sheet : sheets) {
	    Assert.assertTrue(!sheet.getHeader().isEmpty());
	    Assert.assertNotNull(sheet.getSourceLanguageId());
	    Assert.assertNotNull(sheet.getTargetLanguageId());

	    List<XlsReportRow> rows = sheet.getRows();
	    Assert.assertTrue(!rows.isEmpty());
	    Assert.assertEquals(ROW_NUM, rows.size());
	    rows.stream().forEach(r -> Assert.assertEquals(CELL_NUM, r.getCells().size()));
	}
    }

    private void awaitTermination() throws InterruptedException {
	if (!getExecutor().awaitTermination(30, TimeUnit.SECONDS)) {
	    getExecutor().shutdownNow();
	    if (!getExecutor().awaitTermination(30, TimeUnit.SECONDS)) {
		LOGGER.error("Executor did not terminate");
	    }
	}
    }

    private XlsReport createFinalReport(Set<String> keys) {
	XlsReport finalReport = new XlsReport();

	Map<String, ImportProgressInfo> infos = getCacheGateway().getAll(CacheName.IMPORT_PROGRESS_STATUS, keys);
	List<XlsReport> reports = infos.values().stream().map(i -> i.getImportSummary().getReport()).collect(toList());
	if (CollectionUtils.isEmpty(reports)) {
	    return finalReport;
	}

	reports.stream().forEach(r -> finalReport.getSheets().addAll(r.getSheets()));
	return finalReport;
    }

    private CacheGateway<String, ImportProgressInfo> getCacheGateway() {
	return _cacheGateway;
    }

    private ExecutorService getExecutor() {
	return _executor;
    }

    private Runnable importSimulation(XlsReport report) {
	return () -> {
	    writeReport(report);
	};
    }

    private void shutdownAndAwaitTermination() {
	getExecutor().shutdown();
	try {
	    awaitTermination();
	} catch (Exception e) {
	    getExecutor().shutdownNow();
	    Thread.currentThread().interrupt();
	}
    }

    private XlsReport writeReport(XlsReport report) {
	XlsReportSheet sheet = new XlsReportSheet("sr", "en");
	for (int i = 0; i < 10; i++) {
	    sheet.addToHeader(UUID.randomUUID().toString());
	}

	for (int j = 0; j < ROW_NUM; j++) {
	    int rowIndex = sheet.getRowIndex().get();
	    int columnIndex = 0;

	    XlsReportRow row = new XlsReportRow();

	    for (int i = 0; i < CELL_NUM; i++) {
		String text = "text_" + rowIndex + "_" + columnIndex;
		XlsReportCell cell = new XlsReportCell(rowIndex, columnIndex, false, ReportColor.BLACK, 15,
			ReportColor.WHITE, text);

		row.addCell(cell);
		columnIndex++;
	    }

	    sheet.addRow(row);
	    sheet.incrementRowIndex();
	}
	report.addSheet(sheet);
	return report;
    }
}
