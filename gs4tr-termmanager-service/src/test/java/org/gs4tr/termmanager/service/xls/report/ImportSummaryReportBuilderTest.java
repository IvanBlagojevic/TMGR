package org.gs4tr.termmanager.service.xls.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.xls.report.CellTextStyle;
import org.gs4tr.termmanager.model.xls.report.ImportReportConstants;
import org.gs4tr.termmanager.model.xls.report.ReportColor;
import org.gs4tr.termmanager.model.xls.report.XlsReport;
import org.gs4tr.termmanager.model.xls.report.XlsReportCell;
import org.gs4tr.termmanager.model.xls.report.XlsReportRow;
import org.gs4tr.termmanager.model.xls.report.XlsReportSheet;
import org.gs4tr.termmanager.service.xls.XlsHelper;
import org.gs4tr.termmanager.service.xls.report.builder.ImportSummaryReportBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ImportSummaryReportBuilderTest {

    private static final String STATUS = ItemStatusTypeHolder.PROCESSED.getName();

    private static final List<String> TARGETS = Arrays.asList("sr", "fr", "ru");

    private static final String USER = "userneme";

    @Test
    public void testAddedReport() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_full();
	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	String newType = "custom";

	List<String> incomingAttributes = new ArrayList<>();
	incomingAttributes.add("definition");
	incomingAttributes.add(newType);

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry, incomingAttributes,
		sourceLanguageId, Arrays.asList("en", "sr", "fr"), report, true);

	// add new termEntry attribute
	Description teDesc = new Description("custom", "new term entry att");
	entry.addDescription(teDesc);

	// add new en term
	Term enNewTerm = new Term("en", "en new term", false, STATUS, USER);
	enNewTerm.setUuId(UUID.randomUUID().toString());
	enNewTerm.addDescription(new Description("custom", "en new term att"));
	entry.addTerm(enNewTerm);

	// add new sr term
	Term srNewTerm = new Term("sr", "sr new term", false, STATUS, USER);
	srNewTerm.setUuId(UUID.randomUUID().toString());
	srNewTerm.addDescription(new Description("custom", "sr new term att"));
	entry.addTerm(srNewTerm);

	// add new fr term
	Term frNewTerm = new Term("fr", "fr new term", false, STATUS, USER);
	frNewTerm.setUuId(UUID.randomUUID().toString());
	frNewTerm.addDescription(new Description("custom", "fr new term att"));
	entry.addTerm(frNewTerm);

	builder.appendReport();

	assertReport(report, 2, 1, 19, 19);
    }

    @Test
    public void testDeletedReport() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_full();
	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";
	String targetLanguageId = "sr";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr"), report,
		true);

	Set<Term> srTerms = entry.getLanguageTerms().get(targetLanguageId);

	Assert.assertEquals(2, srTerms.size());

	Iterator<Term> srIterator = srTerms.iterator();

	Term srTerm1 = srIterator.next();
	srTerm1.setDisabled(true);

	builder.appendReport();

	assertReport(report, 1, 1, 12, 12);
    }

    @Test
    public void testMixedChangesReport() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_full();
	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	String newType = "entry";

	List<String> incomingAttributes = new ArrayList<>();
	incomingAttributes.add("definition");
	incomingAttributes.add(newType);

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry, incomingAttributes,
		sourceLanguageId, Arrays.asList("en", "sr", "fr", "ru"), report, true);

	entry.addDescription(new Description(newType, "description"));

	Term srNewTerm = new Term("sr", "sr new term 1", false, STATUS, USER);

	entry.addTerm(srNewTerm);

	Set<Term> frTerms = entry.getLanguageTerms().get("fr");

	Iterator<Term> frTermsIterator = frTerms.iterator();

	Term fr1 = frTermsIterator.next();
	fr1.setDisabled(true);

	Term fr2 = frTermsIterator.next();
	fr2.setName("updated fr term");

	Term ruTerm = new Term("ru", "russian term", false, STATUS, USER);
	ruTerm.addDescription(new Description(Description.NOTE, "partOfSpeach", "ru term note"));
	entry.addTerm(ruTerm);

	builder.appendReport();

	List<XlsReportSheet> sheets = report.getSheets();
	Assert.assertEquals(3, sheets.size());

	for (XlsReportSheet sheet : sheets) {
	    String targetLanguageId = sheet.getTargetLanguageId();
	    int expecdedNumOfColumns = 11;
	    if (targetLanguageId.equals("sr")) {
		expecdedNumOfColumns = 15;
	    } else if (targetLanguageId.equals("fr")) {
		expecdedNumOfColumns = 13;
	    }

	    Assert.assertEquals(expecdedNumOfColumns, sheet.getHeader().size());

	    String sheetTargetLanguageId = targetLanguageId;
	    Assert.assertTrue(TARGETS.contains(sheetTargetLanguageId));

	    List<XlsReportRow> rows = sheet.getRows();
	    Assert.assertEquals(1, rows.size());

	    Assert.assertEquals(ImportReportConstants.HEADER_ROW_INDEX + 1 + rows.size(), sheet.getRowIndex().get());

	    for (XlsReportRow row : rows) {
		List<XlsReportCell> cells = row.getCells();
		Assert.assertEquals(expecdedNumOfColumns, cells.size());

		int cellIndex = 0;
		for (XlsReportCell cell : cells) {
		    Assert.assertEquals(cellIndex, cell.getColumn());
		    cellIndex++;
		}
	    }
	}
    }

    @Test
    public void testMultipleRowsReport() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_full();
	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	List<String> importLocales = Arrays.asList("en", "sr", "fr");

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, importLocales, report, true);

	Term srNewTerm = new Term("sr", "sr new term 1", false, STATUS, USER);
	srNewTerm.setUuId(UUID.randomUUID().toString());
	srNewTerm.addDescription(new Description("custom", "sr new term 1 att"));
	entry.addTerm(srNewTerm);

	// add first sr row
	builder.appendReport();

	entry = createTermEntry_full();
	clone = new TermEntry(entry);

	builder = new ImportSummaryReportBuilder(clone, entry, getDescriptionTypes(entry.getDescriptions()),
		sourceLanguageId, importLocales, report, true);

	srNewTerm = new Term("sr", "sr new term 2", false, STATUS, USER);
	srNewTerm.setUuId(UUID.randomUUID().toString());
	srNewTerm.addDescription(new Description("custom", "sr new term 2 att"));
	entry.addTerm(srNewTerm);

	// add second sr row
	builder.appendReport();

	entry = createTermEntry_full();
	clone = new TermEntry(entry);

	builder = new ImportSummaryReportBuilder(clone, entry, getDescriptionTypes(entry.getDescriptions()),
		sourceLanguageId, importLocales, report, true);

	Term frNewTerm = new Term("fr", "fr new term 1", false, STATUS, USER);
	frNewTerm.setUuId(UUID.randomUUID().toString());
	frNewTerm.addDescription(new Description("custom", "fr new term 1 att"));
	entry.addTerm(frNewTerm);

	// add first fr row
	builder.appendReport();

	entry = createTermEntry_full();
	clone = new TermEntry(entry);

	builder = new ImportSummaryReportBuilder(clone, entry, getDescriptionTypes(entry.getDescriptions()),
		sourceLanguageId, importLocales, report, true);

	frNewTerm = new Term("fr", "fr new term 2", false, STATUS, USER);
	frNewTerm.setUuId(UUID.randomUUID().toString());
	frNewTerm.addDescription(new Description("custom", "fr new term 2 att"));
	entry.addTerm(frNewTerm);

	// add second fr row
	builder.appendReport();

	assertReport(report, 2, 2, 15, 15);
    }

    /* TERII-4696 */
    @Test
    public void testSourceTermAttributeMutliValueIssue() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_sourceTerm();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en"), report, true);

	Set<Term> enTerms = entry.getLanguageTerms().get(sourceLanguageId);
	Assert.assertNotNull(enTerms);

	Term enTerm = enTerms.iterator().next();
	enTerm.addDescription(new Description("context", "en new context value"));

	builder.appendReport();

	List<XlsReportSheet> sheets = report.getSheets();
	Assert.assertTrue(CollectionUtils.isNotEmpty(sheets));

	XlsReportSheet sheet = sheets.get(0);
	Assert.assertEquals(sourceLanguageId, sheet.getTargetLanguageId());

	List<XlsReportRow> rows = sheet.getRows();
	Assert.assertEquals(1, rows.size());

	XlsReportRow row = rows.get(0);

	List<XlsReportCell> cells = row.getCells();
	Assert.assertTrue(CollectionUtils.isNotEmpty(cells));

	XlsReportCell cell = cells.get(3);
	Assert.assertNotNull(cell);
	Assert.assertEquals(ReportColor.BLACK, cell.getFontColor());
	Assert.assertEquals("en context value", cell.getText());

	cell = cells.get(4);
	Assert.assertNotNull(cell);
	Assert.assertEquals(ReportColor.GREEN, cell.getFontColor());
	Assert.assertEquals("en new context value", cell.getText());
    }

    @Test
    public void testTermDescriptionsReport_add() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_termDescriptions();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr"), report,
		true);

	Set<Term> srTerms = entry.getLanguageTerms().get("sr");
	Assert.assertTrue(!srTerms.isEmpty());

	Term srTerm = srTerms.iterator().next();
	Set<Description> descs = srTerm.getDescriptions();
	Assert.assertTrue(CollectionUtils.isNotEmpty(descs));
	Assert.assertEquals(2, descs.size());

	// add Serbian term description
	srTerm.addDescription(new Description("custom", "sr custom value"));

	builder.appendReport();

	assertReport(report, 1, 1, 8, 8);

	for (XlsReportSheet sheet : report.getSheets()) {
	    for (XlsReportRow row : sheet.getRows()) {
		for (XlsReportCell cell : row.getCells()) {
		    ReportColor color = cell.getFontColor();
		    if (cell.getColumn() == 0) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 1) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 2) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 3) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 4) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 5) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 6) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 6) {
			Assert.assertEquals(ReportColor.GREEN, color);
		    }
		}
	    }
	}
    }

    @Test
    public void testTermDescriptionsReport_add_delete() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_termDescriptions();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr"), report,
		true);

	Set<Term> srTerms = entry.getLanguageTerms().get("sr");
	Assert.assertTrue(!srTerms.isEmpty());

	Term srTerm = srTerms.iterator().next();
	Set<Description> descs = srTerm.getDescriptions();
	Assert.assertTrue(CollectionUtils.isNotEmpty(descs));
	Assert.assertEquals(2, descs.size());

	// delete one Serbian term description
	Iterator<Description> iterator = descs.iterator();
	iterator.next();
	iterator.remove();

	// add Serbian term description
	srTerm.addDescription(new Description("custom", "sr custom value"));

	builder.appendReport();

	assertReport(report, 1, 1, 8, 8);

	for (XlsReportSheet sheet : report.getSheets()) {
	    for (XlsReportRow row : sheet.getRows()) {
		for (XlsReportCell cell : row.getCells()) {
		    ReportColor color = cell.getFontColor();
		    if (cell.getColumn() == 0) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 1) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 2) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 3) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 4) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 5) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 6) {
			Assert.assertEquals(ReportColor.GREEN, color);
		    } else if (cell.getColumn() == 6) {
			Assert.assertEquals(ReportColor.RED, color);
		    }
		}
	    }
	}
    }

    @Test
    public void testTermDescriptionsReport_add_update() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_termDescriptions();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr"), report,
		true);

	Set<Term> srTerms = entry.getLanguageTerms().get("sr");
	Assert.assertTrue(!srTerms.isEmpty());

	Term srTerm = srTerms.iterator().next();
	Set<Description> descs = srTerm.getDescriptions();
	Assert.assertTrue(CollectionUtils.isNotEmpty(descs));
	Assert.assertEquals(2, descs.size());

	// update one Serbian term description
	Iterator<Description> iterator = descs.iterator();
	Description next = iterator.next();
	String newValue = "updated description value";
	String totalValue = next.getValue().concat(StringConstants.SPACE).concat(newValue);

	Set<Description> updatedSrDescs = new HashSet<>();
	updatedSrDescs.add(new Description(next.getType(), newValue));
	updatedSrDescs.add(iterator.next());
	updatedSrDescs.add(new Description("partOfSpeach", "new value"));

	srTerm.setDescriptions(updatedSrDescs);

	builder.appendReport();

	assertReport(report, 1, 1, 8, 8);

	for (XlsReportSheet sheet : report.getSheets()) {
	    for (XlsReportRow row : sheet.getRows()) {
		for (XlsReportCell cell : row.getCells()) {
		    ReportColor color = cell.getFontColor();
		    if (cell.getColumn() == 0) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 1) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 2) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 3) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 4) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 5) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 6) {
			Assert.assertEquals(totalValue, cell.getText());

			List<CellTextStyle> textStyle = cell.getTextStyle();

			Assert.assertEquals(2, textStyle.size());
			Assert.assertNotNull(textStyle);
			Assert.assertEquals(ReportColor.RED, textStyle.get(0).getColor());
			Assert.assertEquals(ReportColor.GREEN, textStyle.get(1).getColor());
		    } else if (cell.getColumn() == 6) {
			Assert.assertEquals(ReportColor.GREEN, color);
		    }
		}
	    }
	}
    }

    @Test
    public void testTermDescriptionsReport_delete() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_termDescriptions();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr"), report,
		true);

	Set<Term> srTerms = entry.getLanguageTerms().get("sr");
	Assert.assertTrue(!srTerms.isEmpty());

	Term srTerm = srTerms.iterator().next();
	Set<Description> descs = srTerm.getDescriptions();
	Assert.assertTrue(CollectionUtils.isNotEmpty(descs));
	Assert.assertEquals(2, descs.size());

	// delete one Serbian term description
	Iterator<Description> iterator = descs.iterator();
	iterator.next();
	iterator.remove();

	builder.appendReport();

	assertReport(report, 1, 1, 7, 7);

	for (XlsReportSheet sheet : report.getSheets()) {
	    for (XlsReportRow row : sheet.getRows()) {
		for (XlsReportCell cell : row.getCells()) {
		    ReportColor color = cell.getFontColor();
		    if (cell.getColumn() == 0) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 1) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 2) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 3) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 4) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 5) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 6) {
			Assert.assertEquals(ReportColor.RED, color);
		    }
		}
	    }
	}
    }

    @Test
    public void testTermDescriptionsReport_delete_update() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_termDescriptions();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr"), report,
		true);

	Set<Term> srTerms = entry.getLanguageTerms().get("sr");
	Assert.assertTrue(!srTerms.isEmpty());

	Term srTerm = srTerms.iterator().next();
	Set<Description> descs = srTerm.getDescriptions();
	Assert.assertTrue(CollectionUtils.isNotEmpty(descs));
	Assert.assertEquals(2, descs.size());

	// delete one and update one Serbian term description same as import
	// overwrite
	Iterator<Description> iterator = descs.iterator();
	Description next = iterator.next();
	String newValue = "updated description value";
	String totalValue = next.getValue().concat(StringConstants.SPACE).concat(newValue);

	Set<Description> updatedSrDescs = new HashSet<>();
	updatedSrDescs.add(new Description(next.getType(), newValue));

	srTerm.setDescriptions(updatedSrDescs);

	builder.appendReport();

	assertReport(report, 1, 1, 7, 7);

	for (XlsReportSheet sheet : report.getSheets()) {
	    for (XlsReportRow row : sheet.getRows()) {
		for (XlsReportCell cell : row.getCells()) {
		    ReportColor color = cell.getFontColor();
		    if (cell.getColumn() == 0) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 1) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 2) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 3) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 4) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 5) {
			Assert.assertEquals(totalValue, cell.getText());

			List<CellTextStyle> textStyle = cell.getTextStyle();

			Assert.assertEquals(2, textStyle.size());
			Assert.assertNotNull(textStyle);
			Assert.assertEquals(ReportColor.RED, textStyle.get(0).getColor());
			Assert.assertEquals(ReportColor.GREEN, textStyle.get(1).getColor());
		    } else if (cell.getColumn() == 6) {
			Assert.assertEquals(ReportColor.RED, color);
		    }
		}
	    }
	}
    }

    @Test
    public void testTermDescriptionsReport_update() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_termDescriptions();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr"), report,
		true);

	Set<Term> srTerms = entry.getLanguageTerms().get("sr");
	Assert.assertTrue(!srTerms.isEmpty());

	Term srTerm = srTerms.iterator().next();
	Set<Description> descs = srTerm.getDescriptions();
	Assert.assertTrue(CollectionUtils.isNotEmpty(descs));
	Assert.assertEquals(2, descs.size());

	// update one Serbian term description same as import overwrite
	Iterator<Description> iterator = descs.iterator();
	Set<Description> updatedSrDescs = new HashSet<>();

	Description next = iterator.next();
	String newValue = "updated description value";
	String totalValue = next.getValue().concat(StringConstants.SPACE).concat(newValue);

	updatedSrDescs.add(new Description(next.getType(), newValue));
	updatedSrDescs.add(iterator.next());

	srTerm.setDescriptions(updatedSrDescs);

	// append report
	builder.appendReport();

	// assert
	assertReport(report, 1, 1, 7, 7);

	for (XlsReportSheet sheet : report.getSheets()) {
	    for (XlsReportRow row : sheet.getRows()) {
		for (XlsReportCell cell : row.getCells()) {
		    ReportColor color = cell.getFontColor();
		    if (cell.getColumn() == 0) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 1) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 2) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 3) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 4) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 5) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 6) {
			Assert.assertEquals(totalValue, cell.getText());

			List<CellTextStyle> textStyle = cell.getTextStyle();

			Assert.assertEquals(2, textStyle.size());
			Assert.assertNotNull(textStyle);
			Assert.assertEquals(ReportColor.RED, textStyle.get(0).getColor());
			Assert.assertEquals(ReportColor.GREEN, textStyle.get(1).getColor());
		    }
		}
	    }
	}
    }

    @Test
    public void testTermEntryAttributesReport_add() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_full();
	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	String newType = "custom 1";

	List<String> incomingAttributes = new ArrayList<>();
	incomingAttributes.add("definition");
	incomingAttributes.add(newType);

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry, incomingAttributes,
		sourceLanguageId, Arrays.asList("en", "sr", "fr"), report, true);

	Set<Description> termEntryAttributes = entry.getDescriptions();
	Assert.assertEquals(1, termEntryAttributes.size());

	Description newTermEntryAttribute = new Description(newType, "new custom term entry attribute");
	termEntryAttributes.add(newTermEntryAttribute);

	builder.appendReport();

	assertReport(report, 2, 1, 13, 13);

	for (XlsReportSheet sheet : report.getSheets()) {
	    for (XlsReportRow row : sheet.getRows()) {
		for (XlsReportCell cell : row.getCells()) {
		    ReportColor color = cell.getFontColor();
		    if (cell.getColumn() == 0) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 1) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 2) {
			Assert.assertEquals(ReportColor.GREEN, color);
		    } else if (cell.getColumn() == 3) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 4) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 5) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 6) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 7) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 8) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 9) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 10) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 11) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 12) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    }
		}
	    }
	}
    }

    @Test
    public void testTermEntryAttributesReport_add_delete() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_attributes();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	String newType = "new custom type";

	List<String> incomingAttributes = new ArrayList<>();
	incomingAttributes.add("context");
	incomingAttributes.add(newType);
	incomingAttributes.add("definition");

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry, incomingAttributes,
		sourceLanguageId, Arrays.asList("en", "sr"), report, true);

	Set<Description> termEntryAttributes = entry.getDescriptions();
	Assert.assertEquals(2, termEntryAttributes.size());

	Iterator<Description> iterator = termEntryAttributes.iterator();
	iterator.next();
	iterator.remove();

	termEntryAttributes.add(new Description(newType, "new value"));

	builder.appendReport();

	assertReport(report, 1, 1, 8, 8);

	for (XlsReportSheet sheet : report.getSheets()) {
	    for (XlsReportRow row : sheet.getRows()) {
		for (XlsReportCell cell : row.getCells()) {
		    ReportColor color = cell.getFontColor();
		    if (cell.getColumn() == 0) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 1) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 2) {
			Assert.assertEquals(ReportColor.GREEN, color);
		    } else if (cell.getColumn() == 3) {
			Assert.assertEquals(ReportColor.RED, color);
		    } else if (cell.getColumn() == 4) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 5) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 6) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 7) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    }
		}
	    }
	}
    }

    @Test
    public void testTermEntryAttributesReport_add_multiplicity() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_attributes();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr"), report,
		true);

	Set<Description> termEntryAttributes = entry.getDescriptions();
	Assert.assertEquals(2, termEntryAttributes.size());

	termEntryAttributes.add(new Description("definition", "new value"));

	builder.appendReport();

	assertReportMultiplicity(report, 1, 1, 8, 7);
    }

    @Test
    public void testTermEntryAttributesReport_add_update() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_attributes();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	String newType = "new custom type";

	List<String> incomingAttributes = new ArrayList<>();
	incomingAttributes.add(newType);
	incomingAttributes.add("definition");
	incomingAttributes.add("context");

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry, incomingAttributes,
		sourceLanguageId, Arrays.asList("en", "sr"), report, true);

	Set<Description> termEntryAttributes = entry.getDescriptions();
	Assert.assertEquals(2, termEntryAttributes.size());

	Iterator<Description> iterator = termEntryAttributes.iterator();

	Description desc1 = iterator.next();
	String oldValue = desc1.getValue();
	String newValue = "changed attribute value";
	String totalValue = oldValue.concat(StringConstants.SPACE).concat(newValue);

	Set<Description> updatedTermEntryAttributes = new HashSet<>();
	updatedTermEntryAttributes.add(iterator.next());
	updatedTermEntryAttributes.add(new Description(desc1.getType(), newValue));
	updatedTermEntryAttributes.add(new Description(newType, "new value"));

	entry.setDescriptions(updatedTermEntryAttributes);

	builder.appendReport();

	assertReport(report, 1, 1, 8, 8);

	for (XlsReportSheet sheet : report.getSheets()) {
	    for (XlsReportRow row : sheet.getRows()) {
		for (XlsReportCell cell : row.getCells()) {
		    ReportColor color = cell.getFontColor();
		    if (cell.getColumn() == 0) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 1) {
			Assert.assertEquals(ReportColor.GREEN, color);
		    } else if (cell.getColumn() == 2) {
			Assert.assertEquals(totalValue, cell.getText());

			List<CellTextStyle> textStyle = cell.getTextStyle();

			Assert.assertEquals(2, textStyle.size());
			Assert.assertNotNull(textStyle);
			Assert.assertEquals(ReportColor.RED, textStyle.get(0).getColor());
			Assert.assertEquals(ReportColor.GREEN, textStyle.get(1).getColor());
		    } else if (cell.getColumn() == 3) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 4) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 5) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 6) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 7) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    }
		}
	    }
	}
    }

    @Test
    public void testTermEntryAttributesReport_delete() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_attributes();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr"), report,
		true);

	Set<Description> termEntryAttributes = entry.getDescriptions();
	Assert.assertEquals(2, termEntryAttributes.size());

	Set<Description> updatedAttributes = new HashSet<>();
	updatedAttributes.add(termEntryAttributes.iterator().next());

	entry.setDescriptions(updatedAttributes);

	builder.appendReport();

	assertReport(report, 1, 1, 7, 7);

	for (XlsReportSheet sheet : report.getSheets()) {
	    for (XlsReportRow row : sheet.getRows()) {
		for (XlsReportCell cell : row.getCells()) {
		    ReportColor color = cell.getFontColor();
		    if (cell.getColumn() == 0) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 1) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 2) {
			Assert.assertEquals(ReportColor.RED, color);
		    } else if (cell.getColumn() == 3) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 4) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 5) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 6) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 7) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    }
		}
	    }
	}
    }

    @Test
    public void testTermEntryAttributesReport_update() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_full();
	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr", "fr"), report,
		true);

	Set<Description> termEntryAttributes = entry.getDescriptions();
	Assert.assertEquals(1, termEntryAttributes.size());

	Iterator<Description> teAttIterator = termEntryAttributes.iterator();

	Description teAtt = teAttIterator.next();
	String newValue = "changed attribute value";
	String totalValue = teAtt.getValue().concat(StringConstants.SPACE).concat(newValue);

	Set<Description> updatedDescs = new HashSet<>();
	updatedDescs.add(new Description(teAtt.getType(), newValue));

	entry.setDescriptions(updatedDescs);

	builder.appendReport();

	// 12
	assertReport(report, 2, 1, 12, 12);

	for (XlsReportSheet sheet : report.getSheets()) {
	    for (XlsReportRow row : sheet.getRows()) {
		for (XlsReportCell cell : row.getCells()) {
		    ReportColor color = cell.getFontColor();
		    if (cell.getColumn() == 0) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 1) {
			Assert.assertEquals(totalValue, cell.getText());

			List<CellTextStyle> textStyle = cell.getTextStyle();

			Assert.assertEquals(2, textStyle.size());
			Assert.assertNotNull(textStyle);
			Assert.assertEquals(ReportColor.RED, textStyle.get(0).getColor());
			Assert.assertEquals(ReportColor.GREEN, textStyle.get(1).getColor());
		    } else if (cell.getColumn() == 2) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 3) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 4) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 5) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 6) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 7) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 8) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 9) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 10) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 11) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    }
		}
	    }
	}
    }

    @Test
    public void testTermEntryAttributesReport_update_deleted() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_attributes();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr"), report,
		true);

	Set<Description> termEntryAttributes = entry.getDescriptions();
	Assert.assertEquals(2, termEntryAttributes.size());

	Iterator<Description> teAttIterator = termEntryAttributes.iterator();

	Description teAtt = teAttIterator.next();
	String newValue = "changed attribute value";
	String totalValue = teAtt.getValue().concat(StringConstants.SPACE).concat(newValue);

	Set<Description> updatedDescs = new HashSet<>();
	updatedDescs.add(new Description(teAtt.getType(), newValue));

	entry.setDescriptions(updatedDescs);

	builder.appendReport();

	assertReport(report, 1, 1, 7, 7);

	for (XlsReportSheet sheet : report.getSheets()) {
	    for (XlsReportRow row : sheet.getRows()) {
		for (XlsReportCell cell : row.getCells()) {
		    ReportColor color = cell.getFontColor();
		    if (cell.getColumn() == 0) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 1) {
			Assert.assertEquals(totalValue, cell.getText());

			List<CellTextStyle> textStyle = cell.getTextStyle();

			Assert.assertEquals(2, textStyle.size());
			Assert.assertNotNull(textStyle);
			Assert.assertEquals(ReportColor.RED, textStyle.get(0).getColor());
			Assert.assertEquals(ReportColor.GREEN, textStyle.get(1).getColor());
		    } else if (cell.getColumn() == 2) {
			Assert.assertEquals(ReportColor.RED, color);
		    } else if (cell.getColumn() == 3) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 4) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 5) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 6) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    }
		}
	    }
	}
    }

    /* TERII-4685 */
    @Test
    public void testTermEntryColumnAppearsAfterLanguageColumns() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_terms();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	List<String> types = Arrays.asList("definition", "context", "newType");

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry, types, sourceLanguageId,
		Arrays.asList("en", "sr"), report, true);

	// add new English term
	String newTermName = "English term";
	entry.addTerm(new Term(sourceLanguageId, newTermName, false, STATUS, USER));

	builder.appendReport();

	// new row
	entry = createTermEntry_attributes();

	clone = new TermEntry(entry);

	builder = new ImportSummaryReportBuilder(clone, entry, types, sourceLanguageId, Arrays.asList("en", "sr", "ru"),
		report, true);

	entry.addDescription(new Description("newType", "newValue"));

	builder.appendReport();

	List<XlsReportSheet> sheets = report.getSheets();
	Assert.assertTrue(CollectionUtils.isNotEmpty(sheets));

	XlsReportSheet sheet = sheets.get(0);

	List<String> header = sheet.getHeader();
	Assert.assertTrue(CollectionUtils.isNotEmpty(header));

	Assert.assertEquals(XlsHelper.TERM_ENTRY_ID, header.get(0));
	Assert.assertEquals(types.get(0), header.get(1));
	Assert.assertEquals(types.get(1), header.get(2));
	Assert.assertEquals(types.get(2), header.get(3));
	Assert.assertEquals(sourceLanguageId, header.get(4));
    }

    @Test
    public void testTermsReport_add() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_terms();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr", "ru"), report,
		true);

	// add new Russian term
	String newTermName = "russian term";
	entry.addTerm(new Term("ru", newTermName, false, STATUS, USER));

	builder.appendReport();

	assertReport(report, 1, 1, 5, 5);

	for (XlsReportSheet sheet : report.getSheets()) {
	    for (XlsReportRow row : sheet.getRows()) {
		for (XlsReportCell cell : row.getCells()) {
		    ReportColor color = cell.getFontColor();
		    if (cell.getColumn() == 0) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 1) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 2) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 3) {
			Assert.assertEquals(ReportColor.GREEN, color);
		    } else if (cell.getColumn() == 4) {
			Assert.assertEquals(ReportColor.GREEN, color);
		    }
		}
	    }
	}
    }

    @Test
    public void testTermsReport_add_delete() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_terms();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr", "ru"), report,
		true);

	// add new Russian term
	entry.addTerm(new Term("ru", "russian term", false, STATUS, STATUS));

	// delete Serbian term
	Set<Term> srTerms = entry.getLanguageTerms().get("sr");
	Assert.assertTrue(!srTerms.isEmpty());

	srTerms.iterator().next().setDisabled(true);

	builder.appendReport();

	assertReport(report, 2, 1, 5, 5);

	for (XlsReportSheet sheet : report.getSheets()) {
	    if ("ru".equals(sheet.getTargetLanguageId())) {
		for (XlsReportRow row : sheet.getRows()) {
		    for (XlsReportCell cell : row.getCells()) {
			ReportColor color = cell.getFontColor();
			if (cell.getColumn() == 0) {
			    Assert.assertEquals(ReportColor.BLACK, color);
			} else if (cell.getColumn() == 1) {
			    Assert.assertEquals(ReportColor.BLACK, color);
			} else if (cell.getColumn() == 2) {
			    Assert.assertEquals(ReportColor.BLACK, color);
			} else if (cell.getColumn() == 3) {
			    Assert.assertEquals(ReportColor.GREEN, color);
			} else if (cell.getColumn() == 4) {
			    Assert.assertEquals(ReportColor.GREEN, color);
			}
		    }
		}
	    } else {
		for (XlsReportRow row : sheet.getRows()) {
		    for (XlsReportCell cell : row.getCells()) {
			ReportColor color = cell.getFontColor();
			if (cell.getColumn() == 0) {
			    Assert.assertEquals(ReportColor.BLACK, color);
			} else if (cell.getColumn() == 1) {
			    Assert.assertEquals(ReportColor.BLACK, color);
			} else if (cell.getColumn() == 2) {
			    Assert.assertEquals(ReportColor.BLACK, color);
			} else if (cell.getColumn() == 3) {
			    Assert.assertEquals(ReportColor.RED, color);
			} else if (cell.getColumn() == 4) {
			    Assert.assertEquals(ReportColor.RED, color);
			}
		    }
		}
	    }
	}
    }

    @Test
    public void testTermsReport_add_update() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_terms();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr", "ru"), report,
		true);

	// add new Russian term
	entry.addTerm(new Term("ru", "russian term", false, STATUS, STATUS));

	Set<Term> srTerms = entry.getLanguageTerms().get("sr");
	Assert.assertTrue(!srTerms.isEmpty());
	Assert.assertEquals(1, srTerms.size());

	// update Serbian term
	Iterator<Term> iterator = srTerms.iterator();

	Term srTerm = iterator.next();

	String newName = "updated term name";
	String totalName = srTerm.getName().concat(StringConstants.SPACE).concat(newName);

	srTerm.setDisabled(true);

	Set<Term> updatedSrTerms = new HashSet<>();
	updatedSrTerms.add(new Term(srTerm.getLanguageId(), newName, false, STATUS, USER));
	updatedSrTerms.add(new Term(srTerm));

	entry.getLanguageTerms().put("sr", updatedSrTerms);

	builder.appendReport();

	assertReport(report, 2, 1, 5, 5);

	for (XlsReportSheet sheet : report.getSheets()) {
	    if ("ru".equals(sheet.getTargetLanguageId())) {
		for (XlsReportRow row : sheet.getRows()) {
		    for (XlsReportCell cell : row.getCells()) {
			ReportColor color = cell.getFontColor();
			if (cell.getColumn() == 0) {
			    Assert.assertEquals(ReportColor.BLACK, color);
			} else if (cell.getColumn() == 1) {
			    Assert.assertEquals(ReportColor.BLACK, color);
			} else if (cell.getColumn() == 2) {
			    Assert.assertEquals(ReportColor.BLACK, color);
			} else if (cell.getColumn() == 3) {
			    Assert.assertEquals(ReportColor.GREEN, color);
			} else if (cell.getColumn() == 4) {
			    Assert.assertEquals(ReportColor.GREEN, color);
			}
		    }
		}
	    } else {
		for (XlsReportRow row : sheet.getRows()) {
		    for (XlsReportCell cell : row.getCells()) {
			ReportColor color = cell.getFontColor();
			if (cell.getColumn() == 0) {
			    Assert.assertEquals(ReportColor.BLACK, color);
			} else if (cell.getColumn() == 1) {
			    Assert.assertEquals(ReportColor.BLACK, color);
			} else if (cell.getColumn() == 2) {
			    Assert.assertEquals(ReportColor.BLACK, color);
			} else if (cell.getColumn() == 3) {
			    Assert.assertEquals(totalName, cell.getText());

			    List<CellTextStyle> textStyle = cell.getTextStyle();

			    Assert.assertEquals(2, textStyle.size());
			    Assert.assertNotNull(textStyle);
			    Assert.assertEquals(ReportColor.RED, textStyle.get(0).getColor());
			    Assert.assertEquals(ReportColor.GREEN, textStyle.get(1).getColor());
			} else if (cell.getColumn() == 4) {
			    Assert.assertEquals(ReportColor.BLACK, color);
			}
		    }
		}
	    }
	}
    }

    @Test
    public void testTermsReport_delete() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_terms();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr"), report,
		true);

	Set<Term> srTerms = entry.getLanguageTerms().get("sr");
	Assert.assertTrue(!srTerms.isEmpty());

	// delete Serbian term
	srTerms.iterator().next().setDisabled(true);

	builder.appendReport();

	assertReport(report, 1, 1, 5, 5);

	for (XlsReportSheet sheet : report.getSheets()) {
	    for (XlsReportRow row : sheet.getRows()) {
		for (XlsReportCell cell : row.getCells()) {
		    ReportColor color = cell.getFontColor();
		    if (cell.getColumn() == 0) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 1) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 2) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 3) {
			Assert.assertEquals(ReportColor.RED, color);
		    } else if (cell.getColumn() == 4) {
			Assert.assertEquals(ReportColor.RED, color);
		    }
		}
	    }
	}
    }

    @Test
    public void testTermsReport_delete_source() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_full();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr"), report,
		true);

	Set<Term> enTerms = entry.getLanguageTerms().get("en");
	Assert.assertTrue(!enTerms.isEmpty());

	// delete English term
	enTerms.iterator().next().setDisabled(true);

	builder.appendReport();

	assertReport(report, 1, 1, 12, 12);

	for (XlsReportSheet sheet : report.getSheets()) {
	    for (XlsReportRow row : sheet.getRows()) {
		for (XlsReportCell cell : row.getCells()) {
		    ReportColor color = cell.getFontColor();
		    if (cell.getColumn() == 0) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 1) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 2) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 3) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 4) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 5) {
			Assert.assertEquals(ReportColor.RED, color);
		    } else if (cell.getColumn() == 6) {
			Assert.assertEquals(ReportColor.RED, color);
		    } else if (cell.getColumn() == 7) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 8) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 9) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 10) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 11) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    }
		}
	    }
	}
    }

    @Test
    public void testTermsReport_delete_update() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_terms();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr"), report,
		true);

	Set<Term> srTerms = entry.getLanguageTerms().get("sr");
	Assert.assertTrue(!srTerms.isEmpty());

	// delete Serbian term
	Iterator<Term> srIterator = srTerms.iterator();

	Term srTerm = srIterator.next();
	srTerm.setDisabled(true);

	Set<Term> enTerms = entry.getLanguageTerms().get("en");
	Assert.assertTrue(!enTerms.isEmpty());

	// update English term
	Iterator<Term> enIterator = enTerms.iterator();

	Term enTerm = enIterator.next();
	enTerm.setDisabled(true);

	String newName = "updated en term name";
	String totalName = enTerm.getName().concat(StringConstants.SPACE).concat(newName);

	Term updEnTerm = new Term("en", newName, false, STATUS, USER);
	Term oldEnTerm = new Term(enTerm);

	enTerms.clear();

	entry.addTerm(updEnTerm);
	entry.addTerm(oldEnTerm);

	builder.appendReport();

	assertReport(report, 1, 1, 5, 5);

	for (XlsReportSheet sheet : report.getSheets()) {
	    for (XlsReportRow row : sheet.getRows()) {
		for (XlsReportCell cell : row.getCells()) {
		    ReportColor color = cell.getFontColor();
		    if (cell.getColumn() == 0) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 1) {
			Assert.assertEquals(totalName, cell.getText());

			List<CellTextStyle> textStyle = cell.getTextStyle();

			Assert.assertEquals(2, textStyle.size());
			Assert.assertNotNull(textStyle);
			Assert.assertEquals(ReportColor.RED, textStyle.get(0).getColor());
			Assert.assertEquals(ReportColor.GREEN, textStyle.get(1).getColor());
		    } else if (cell.getColumn() == 2) {
			Assert.assertEquals(ReportColor.BLACK, color);
		    } else if (cell.getColumn() == 3) {
			Assert.assertEquals(ReportColor.RED, color);
		    } else if (cell.getColumn() == 4) {
			Assert.assertEquals(ReportColor.RED, color);
		    }
		}
	    }
	}
    }

    @Test
    public void testTermsReport_update() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_terms();

	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr"), report,
		true);

	Set<Term> srTerms = entry.getLanguageTerms().get("sr");
	Assert.assertTrue(!srTerms.isEmpty());

	srTerms.iterator().next().setName("updated term name");

	builder.appendReport();

	assertReport(report, 1, 1, 5, 5);
    }

    @Test
    public void testUpdatedRerport() throws Exception {
	XlsReport report = new XlsReport();

	TermEntry entry = createTermEntry_full();
	TermEntry clone = new TermEntry(entry);

	String sourceLanguageId = "en";
	String targetLanguageId = "sr";

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(clone, entry,
		getDescriptionTypes(entry.getDescriptions()), sourceLanguageId, Arrays.asList("en", "sr"), report,
		true);

	Set<Term> srTerms = entry.getLanguageTerms().get(targetLanguageId);

	Assert.assertEquals(2, srTerms.size());

	Iterator<Term> srIterator = srTerms.iterator();

	Term srTerm1 = srIterator.next();

	srTerm1.setName("sr updated term 1");

	Term srTerm2 = srIterator.next();

	srTerm2.setName("sr updated term 2");

	builder.appendReport();

	assertReport(report, 1, 1, 12, 12);
    }

    private void assertReport(XlsReport report, int expectedNumOfSheets, int expectedNumOfRows,
	    int expectedNumOfColumns, int expectedNumOfHeaderColumns) {
	List<XlsReportSheet> sheets = report.getSheets();
	Assert.assertEquals(expectedNumOfSheets, sheets.size());

	for (XlsReportSheet sheet : sheets) {
	    Assert.assertEquals(expectedNumOfHeaderColumns, sheet.getHeader().size());

	    String sheetTargetLanguageId = sheet.getTargetLanguageId();
	    Assert.assertTrue(TARGETS.contains(sheetTargetLanguageId));

	    List<XlsReportRow> rows = sheet.getRows();
	    Assert.assertEquals(expectedNumOfRows, rows.size());

	    Assert.assertEquals(ImportReportConstants.HEADER_ROW_INDEX + 1 + rows.size(), sheet.getRowIndex().get());

	    for (XlsReportRow row : rows) {
		List<XlsReportCell> cells = row.getCells();
		Assert.assertEquals(expectedNumOfColumns, cells.size());

		int cellIndex = 0;
		for (XlsReportCell cell : cells) {
		    Assert.assertEquals(cellIndex, cell.getColumn());
		    cellIndex++;
		}
	    }
	}
    }

    private void assertReportMultiplicity(XlsReport report, int expectedNumOfSheets, int expectedNumOfRows,
	    int expectedNumOfColumns, int expectedNumOfHeaderColumns) {
	List<XlsReportSheet> sheets = report.getSheets();
	Assert.assertEquals(expectedNumOfSheets, sheets.size());

	for (XlsReportSheet sheet : sheets) {
	    Assert.assertEquals(expectedNumOfHeaderColumns, sheet.getHeader().size());

	    String sheetTargetLanguageId = sheet.getTargetLanguageId();
	    Assert.assertTrue(TARGETS.contains(sheetTargetLanguageId));

	    List<XlsReportRow> rows = sheet.getRows();
	    Assert.assertEquals(expectedNumOfRows, rows.size());

	    Assert.assertEquals(ImportReportConstants.HEADER_ROW_INDEX + 1 + rows.size(), sheet.getRowIndex().get());

	    for (XlsReportRow row : rows) {
		List<XlsReportCell> cells = row.getCells();
		Assert.assertEquals(expectedNumOfColumns, cells.size());
	    }
	}
    }

    // 7
    // te_id, te_att1, te_att2, en_name, en_status, sr_name, sr_status
    private TermEntry createTermEntry_attributes() {
	TermEntry entry = new TermEntry(1L, "TES000001", "username");
	entry.setUuId(UUID.randomUUID().toString());
	entry.addDescription(new Description("definition", "term entry definition 1"));
	entry.addDescription(new Description("context", "term entry definition 2"));

	// en
	Term enMainTerm = new Term("en", "en main term", false, STATUS, USER);
	enMainTerm.setUuId(UUID.randomUUID().toString());
	entry.addTerm(enMainTerm);

	// sr
	Term srMainTerm = new Term("sr", "sr main term", false, STATUS, USER);
	srMainTerm.setUuId(UUID.randomUUID().toString());
	entry.addTerm(srMainTerm);

	return entry;
    }

    // 12
    // te - 2, en - 5, sr - 5, fr - 5
    private TermEntry createTermEntry_full() {
	TermEntry entry = new TermEntry(1L, "TES000001", "username");
	entry.setUuId(UUID.randomUUID().toString());
	entry.addDescription(new Description("definition", "term entry definition"));

	// en
	Term enMainTerm = new Term("en", "en main term", false, STATUS, USER);
	enMainTerm.setUuId(UUID.randomUUID().toString());
	enMainTerm.addDescription(new Description("context", "en main term context"));
	entry.addTerm(enMainTerm);

	Term enSynonymTerm = new Term("en", "en synonym term", false, STATUS, USER);
	enSynonymTerm.setUuId(UUID.randomUUID().toString());
	entry.addTerm(enSynonymTerm);

	// sr
	Term srMainTerm = new Term("sr", "sr main term", false, STATUS, USER);
	srMainTerm.setUuId(UUID.randomUUID().toString());
	srMainTerm.addDescription(new Description("context", "sr main term context"));
	entry.addTerm(srMainTerm);

	Term srSynonymTerm = new Term("sr", "sr synonym term", false, STATUS, USER);
	srSynonymTerm.setUuId(UUID.randomUUID().toString());
	entry.addTerm(srSynonymTerm);

	// fr
	Term frMainTerm = new Term("fr", "fr main term", false, STATUS, USER);
	frMainTerm.setUuId(UUID.randomUUID().toString());
	frMainTerm.addDescription(new Description("context", "fr main term context"));
	entry.addTerm(frMainTerm);

	Term frSynonymTerm = new Term("fr", "fr synonym term", false, STATUS, USER);
	frSynonymTerm.setUuId(UUID.randomUUID().toString());
	entry.addTerm(frSynonymTerm);

	return entry;
    }

    // 4
    // te_id, en_name, en_status, en att
    private TermEntry createTermEntry_sourceTerm() {
	TermEntry entry = new TermEntry(1L, "TES000001", "username");
	entry.setUuId(UUID.randomUUID().toString());

	// en
	Term enMainTerm = new Term("en", "en main term", false, STATUS, USER);
	enMainTerm.setUuId(UUID.randomUUID().toString());
	enMainTerm.addDescription(new Description("context", "en context value"));
	entry.addTerm(enMainTerm);

	return entry;
    }

    // 7
    // te_id, en_name, en_status, sr_name, sr_status, sr_desc1, sr_desc2
    private TermEntry createTermEntry_termDescriptions() {
	TermEntry entry = new TermEntry(1L, "TES000001", "username");
	entry.setUuId(UUID.randomUUID().toString());

	// en
	Term enMainTerm = new Term("en", "en main term", false, STATUS, USER);
	enMainTerm.setUuId(UUID.randomUUID().toString());
	entry.addTerm(enMainTerm);

	// sr
	Term srMainTerm = new Term("sr", "sr main term", false, STATUS, USER);
	srMainTerm.setUuId(UUID.randomUUID().toString());
	srMainTerm.addDescription(new Description("context", "sr main term context"));
	srMainTerm.addDescription(new Description("definition", "sr main term definition"));
	entry.addTerm(srMainTerm);

	return entry;
    }

    // 5
    // te_id, en_name, en_status, sr_name, sr_status
    private TermEntry createTermEntry_terms() {
	TermEntry entry = new TermEntry(1L, "TES000001", "username");
	entry.setUuId(UUID.randomUUID().toString());

	// en
	Term enMainTerm = new Term("en", "en main term", false, STATUS, USER);
	enMainTerm.setUuId(UUID.randomUUID().toString());
	entry.addTerm(enMainTerm);

	// sr
	Term srMainTerm = new Term("sr", "sr main term", false, STATUS, USER);
	srMainTerm.setUuId(UUID.randomUUID().toString());
	entry.addTerm(srMainTerm);

	return entry;
    }

    private List<String> getDescriptionTypes(Set<Description> descriptions) {
	return Objects.nonNull(descriptions)
		? descriptions.stream().map(Description::getType).collect(Collectors.toList()) : null;
    }
}
