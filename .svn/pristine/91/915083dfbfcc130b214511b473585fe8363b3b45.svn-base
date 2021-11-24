package org.gs4tr.termmanager.service.xls.report;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.xls.report.ReportColor;
import org.gs4tr.termmanager.model.xls.report.XlsReport;
import org.gs4tr.termmanager.model.xls.report.XlsReportCell;
import org.gs4tr.termmanager.model.xls.report.XlsReportRow;
import org.gs4tr.termmanager.model.xls.report.XlsReportSheet;
import org.gs4tr.termmanager.service.AbstractSpringHazelcastTest;
import org.gs4tr.termmanager.service.impl.ImportProgressInfo;
import org.gs4tr.termmanager.service.xls.report.processor.ImportSummaryReportProcessor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-import-report.xml" }, loader = TestEnvironmentAwareContextLoader.class)
public class ImportSummaryReportProcessorTest extends AbstractSpringHazelcastTest {

    private static final int CELL_NUM = 10;

    private static final int ROW_NUM = 100;

    @Autowired
    @Qualifier("guavaCacheGateway")
    private CacheGateway<String, ImportProgressInfo> _cacheGateway;

    @Autowired
    private ImportSummaryReportProcessor _reportProcessor;

    @After
    public void clear() {
	getCacheGateway().removeAll(CacheName.IMPORT_PROGRESS_STATUS);
    }

    @Before
    public void setup() {
	getCacheGateway().removeAll(CacheName.IMPORT_PROGRESS_STATUS);
    }

    /* Test generate import summary report - regular case. */
    @Test
    public void testGenerateImportSummaryReport_case1() {
	ImportSummaryReportProcessor reportProcessor = getReportProcessor();

	String key = UUID.randomUUID().toString();

	Set<String> keys = new HashSet<>();
	keys.add(key);

	ImportProgressInfo importInfo = new ImportProgressInfo(1);
	writeReport(importInfo.getImportSummary().getReport());

	getCacheGateway().put(CacheName.IMPORT_PROGRESS_STATUS, key, importInfo);

	XlsReport finalReport = reportProcessor.generateImportSummaryReport(keys);

	// validate
	Assert.assertNotNull(finalReport);
	List<XlsReportSheet> sheets = finalReport.getSheets();
	sheets.stream().forEach(s -> Assert.assertTrue(!s.getRows().isEmpty()));
	sheets.stream().forEach(s -> Assert.assertEquals(ROW_NUM, s.getRows().size()));

	for (XlsReportSheet sheet : sheets) {
	    List<XlsReportRow> rows = sheet.getRows();
	    rows.stream().forEach(r -> Assert.assertEquals(CELL_NUM, r.getCells().size()));
	}

	reportProcessor.discardImportProgressInfos(keys);

	finalReport = reportProcessor.generateImportSummaryReport(keys);

	Assert.assertNotNull(finalReport);
	sheets = finalReport.getSheets();
	Assert.assertTrue(sheets.isEmpty());
    }

    /*
     * Test generate import summary report - without putting report into the
     * cache.
     */
    @Test
    public void testGenerateImportSummaryReport_case2() {
	ImportSummaryReportProcessor reportProcessor = getReportProcessor();

	String key = UUID.randomUUID().toString();

	Set<String> keys = new HashSet<>();
	keys.add(key);

	XlsReport finalReport = reportProcessor.generateImportSummaryReport(keys);

	// validate
	Assert.assertNotNull(finalReport);
	List<XlsReportSheet> sheets = finalReport.getSheets();
	sheets.stream().forEach(s -> Assert.assertTrue(s.getRows().isEmpty()));
    }

    private CacheGateway<String, ImportProgressInfo> getCacheGateway() {
	return _cacheGateway;
    }

    private ImportSummaryReportProcessor getReportProcessor() {
	return _reportProcessor;
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
