package org.gs4tr.termmanager.service.xls.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.gs4tr.termmanager.model.xls.report.CellTextStyle;
import org.gs4tr.termmanager.model.xls.report.ImportReportConstants;
import org.gs4tr.termmanager.model.xls.report.ReportColor;
import org.gs4tr.termmanager.model.xls.report.XlsReportCell;
import org.gs4tr.termmanager.service.xls.XlsConfiguration;
import org.gs4tr.termmanager.service.xls.XlsReader;
import org.junit.Test;

import com.aspose.cells.Cell;
import com.aspose.cells.Color;
import com.aspose.cells.Font;
import com.aspose.cells.FontSetting;
import com.aspose.cells.Style;
import com.aspose.cells.Worksheet;

import junit.framework.Assert;

public class XlsImportReportWriterTest {

    @Test
    public void testAddSheet() throws Exception {
	File xlsFile = new File("target/testAddSheet.xlsx");

	IXlsImportReportWriter writer = new XlsImportReportWriter();

	String sheetName1 = "Serbian";
	writer.addSheet(sheetName1, 0);

	String sheetName2 = "French";
	writer.addSheet(sheetName2, 1);

	// save report
	writer.saveReport(xlsFile);

	XlsConfiguration config = new XlsConfiguration();

	InputStream stream = new FileInputStream(xlsFile);
	XlsReader reader = new XlsReader(stream, config, true, true);

	Worksheet sheet1 = reader.getWorksheetByName(sheetName1);
	Assert.assertNotNull(sheet1);

	Assert.assertEquals(sheetName1, sheet1.getName());

	Worksheet sheet2 = reader.getWorksheetByName(sheetName2);
	Assert.assertNotNull(sheet2);

	Assert.assertEquals(sheetName2, sheet2.getName());
    }

    @Test
    public void testAddSheetWithSameName() throws Exception {
	File xlsFile = new File("target/testAddSheet.xlsx");

	IXlsImportReportWriter writer = new XlsImportReportWriter();

	String sheetName = "Serbian";
	writer.addSheet(sheetName, 0);

	writer.addSheet(sheetName, 1);

	// save report
	writer.saveReport(xlsFile);

	XlsConfiguration config = new XlsConfiguration();

	InputStream stream = new FileInputStream(xlsFile);
	XlsReader reader = new XlsReader(stream, config, true, true);

	Worksheet sheet1 = reader.getWorksheetByName(sheetName);
	Assert.assertNotNull(sheet1);

	Assert.assertEquals(sheetName, sheet1.getName());

	Worksheet sheet2 = reader.getWorksheetByName(sheetName + 1);
	Assert.assertNotNull(sheet2);

	Assert.assertEquals(sheetName + 1, sheet2.getName());
    }

    @Test
    public void testWriteCellText() throws Exception {
	File xlsFile = new File("target/testWriteCellText.xlsx");

	IXlsImportReportWriter writer = new XlsImportReportWriter();

	String sheetName = "French";
	Worksheet sheet = writer.addSheet(sheetName, 0);
	Assert.assertNotNull(sheet);
	Assert.assertEquals(sheetName, sheet.getName());

	String text = "TestTest";

	CellTextStyle red = new CellTextStyle(ReportColor.RED, 0, 4, true);
	CellTextStyle green = new CellTextStyle(ReportColor.GREEN, 4, 4, true);

	XlsReportCell cellStyle = new XlsReportCell(0, 0, false, ReportColor.BLACK, 11, ReportColor.WHITE, text);
	cellStyle.setTextStyle(Arrays.asList(red, green));
	writer.writeCellText(sheet, cellStyle);

	// save report
	writer.saveReport(xlsFile);

	XlsConfiguration config = new XlsConfiguration();

	InputStream stream = new FileInputStream(xlsFile);
	XlsReader reader = new XlsReader(stream, config, true, true);

	sheet = reader.getWorksheetByName(sheetName);
	Assert.assertNotNull(sheet);

	Cell cell = reader.getCell(sheet, 0, 0);
	Assert.assertNotNull(cell);
	Assert.assertEquals(text, cell.getStringValue());

	Style style = cell.getStyle();
	Assert.assertNotNull(style);
	Assert.assertEquals(Color.getWhite(), style.getForegroundColor());

	// first part of the text
	FontSetting chars = cell.characters(0, 4);
	Assert.assertNotNull(chars);

	Font font = chars.getFont();
	Assert.assertNotNull(font);

	Assert.assertEquals(Color.getRed(), font.getColor());
	Assert.assertEquals(11, font.getSize());
	Assert.assertEquals(true, font.isStrikeout());

	// second part of the text
	chars = cell.characters(4, 4);
	Assert.assertNotNull(chars);

	font = chars.getFont();
	Assert.assertNotNull(font);

	Assert.assertEquals(Color.getGreen(), font.getColor());
	Assert.assertEquals(11, font.getSize());
	Assert.assertEquals(true, font.isStrikeout());
    }

    @Test
    public void testWriteFirstRow() throws Exception {
	File xlsFile = new File("target/testWriteFirstRow.xlsx");

	IXlsImportReportWriter writer = new XlsImportReportWriter();

	String sheetName = "French";
	Worksheet sheet = writer.addSheet(sheetName, 0);
	Assert.assertNotNull(sheet);
	Assert.assertEquals(sheetName, sheet.getName());

	List<String> header = createFrenchHeader();
	writer.writeHeader(sheet, header);

	writer.writeTopHeader(sheet);

	// write first row
	List<XlsReportCell> row = createFirstRow();

	writer.writeRow(sheet, row);

	// save report
	writer.saveReport(xlsFile);

	XlsConfiguration config = new XlsConfiguration();
	config.setMinRow(ImportReportConstants.FIRST_ROW_INDEX);
	config.setRowIndex(ImportReportConstants.FIRST_ROW_INDEX);
	config.setMaxColumn(header.size() - 1);

	InputStream stream = new FileInputStream(xlsFile);
	XlsReader reader = new XlsReader(stream, config, true, true);

	List<String> headerActual = reader.getHeaderColumns();
	Assert.assertTrue(header.containsAll(headerActual));

	String sheetNameActual = reader.getWorksheet().getName();
	Assert.assertEquals(sheetName, sheetNameActual);

	List<String> fields = new ArrayList<>();
	reader.readNextRecord(fields);

	Assert.assertTrue(!fields.isEmpty());
	Assert.assertEquals(headerActual.size(), fields.size());

	Assert.assertEquals("fr synonym", fields.get(fields.size() - 1));
    }

    @Test
    public void testWriteHeaders() throws Exception {
	File xlsFile = new File("target/testWriteHeaders.xlsx");

	IXlsImportReportWriter writer = new XlsImportReportWriter();

	String sheetName = "French";
	Worksheet sheet = writer.addSheet(sheetName, 0);

	List<String> header = createFrenchHeader();
	writer.writeHeader(sheet, header);

	writer.writeTopHeader(sheet);

	writer.saveReport(xlsFile);

	XlsConfiguration config = new XlsConfiguration();
	config.setMinRow(ImportReportConstants.FIRST_ROW_INDEX);
	config.setRowIndex(ImportReportConstants.FIRST_ROW_INDEX);

	InputStream stream = new FileInputStream(xlsFile);
	XlsReader reader = new XlsReader(stream, config, true, true);

	List<String> headerActual = reader.getHeaderColumns();
	Assert.assertTrue(header.containsAll(headerActual));

	String sheetNameActual = reader.getWorksheet().getName();
	Assert.assertEquals(sheetName, sheetNameActual);
    }

    @Test
    public void testWriteSecondRowWithUpdatedTerms() throws Exception {
	File xlsFile = new File("target/testWriteSecondRowWithUpdatedTerms.xlsx");

	IXlsImportReportWriter writer = new XlsImportReportWriter();

	String sheetName = "French";
	Worksheet sheet = writer.addSheet(sheetName, 0);

	List<String> header = createFrenchHeader();
	writer.writeHeader(sheet, header);

	writer.writeTopHeader(sheet);

	// write first row
	List<XlsReportCell> row = createFirstRow();
	writer.writeRow(sheet, row);

	// write second row
	row = createSecondRow();
	writer.writeRow(sheet, row);

	// save report
	writer.saveReport(xlsFile);

	XlsConfiguration config = new XlsConfiguration();
	config.setMinRow(ImportReportConstants.FIRST_ROW_INDEX);
	config.setRowIndex(ImportReportConstants.FIRST_ROW_INDEX);
	config.setMaxColumn(header.size() - 1);

	InputStream stream = new FileInputStream(xlsFile);
	XlsReader reader = new XlsReader(stream, config, true, true);

	List<String> headerActual = reader.getHeaderColumns();
	Assert.assertTrue(header.containsAll(headerActual));

	String sheetNameActual = reader.getWorksheet().getName();
	Assert.assertEquals(sheetName, sheetNameActual);

	List<String> fields = new ArrayList<>();
	reader.readNextRecord(fields);

	Assert.assertTrue(!fields.isEmpty());
	Assert.assertEquals(headerActual.size(), fields.size());

	Assert.assertEquals("fr synonym", fields.get(fields.size() - 1));

	fields = new ArrayList<>();
	reader.readNextRecord(fields);

	Assert.assertTrue(!fields.isEmpty());
	Assert.assertEquals(headerActual.size(), fields.size());

	Assert.assertEquals("fr 1 synonym fr 12 synonym", fields.get(fields.size() - 1));
    }

    private List<XlsReportCell> createFirstRow() {
	int rowIndex = ImportReportConstants.FIRST_ROW_INDEX + 1;

	List<XlsReportCell> row = new ArrayList<>();

	XlsReportCell termEntryIdCell = new XlsReportCell(rowIndex, 0, false, ReportColor.BLACK, 10, ReportColor.WHITE,
		UUID.randomUUID().toString());
	row.add(termEntryIdCell);

	XlsReportCell enCell = new XlsReportCell(rowIndex, 1, false, ReportColor.BLACK, 10, ReportColor.WHITE,
		"enTerm");
	row.add(enCell);

	XlsReportCell enStatus = new XlsReportCell(rowIndex, 2, false, ReportColor.BLACK, 10, ReportColor.WHITE,
		"Approved");
	row.add(enStatus);

	XlsReportCell en1Cell = new XlsReportCell(rowIndex, 4, false, ReportColor.BLACK, 10, ReportColor.WHITE,
		"en synonym");
	en1Cell.setStrikeout(true);
	row.add(en1Cell);

	XlsReportCell en1Status = new XlsReportCell(rowIndex, 5, false, ReportColor.RED, 10, ReportColor.WHITE,
		"Approved");
	en1Status.setStrikeout(true);
	row.add(en1Status);

	XlsReportCell frCell = new XlsReportCell(rowIndex, 7, false, ReportColor.GREEN, 10, ReportColor.WHITE,
		"frTerm");
	row.add(frCell);

	XlsReportCell frStatus = new XlsReportCell(rowIndex, 8, false, ReportColor.BLACK, 10, ReportColor.WHITE,
		"Approved");
	row.add(frStatus);

	XlsReportCell fr1Cell = new XlsReportCell(rowIndex, 10, false, ReportColor.GREEN, 10, ReportColor.WHITE,
		"fr synonym");
	row.add(fr1Cell);
	return row;
    }

    private List<String> createFrenchHeader() {
	List<String> header = new ArrayList<>();
	header.add("TermEntryID");
	header.add("en");
	header.add("en:Status");
	header.add("en:clientApproved");
	header.add("en1");
	header.add("en1:Status");
	header.add("en1:clientApproved");
	header.add("fr");
	header.add("fr:Status");
	header.add("fr:clientApproved");
	header.add("fr1");
	return header;
    }

    private List<XlsReportCell> createSecondRow() {
	int rowIndex = ImportReportConstants.FIRST_ROW_INDEX + 2;

	List<XlsReportCell> row = new ArrayList<>();

	XlsReportCell termEntryIdCell = new XlsReportCell(rowIndex, 0, false, ReportColor.BLACK, 10, ReportColor.WHITE,
		UUID.randomUUID().toString());
	row.add(termEntryIdCell);

	XlsReportCell enCell = new XlsReportCell(rowIndex, 1, false, ReportColor.BLACK, 10, ReportColor.WHITE,
		"en 1 Term");
	row.add(enCell);

	XlsReportCell enStatus = new XlsReportCell(rowIndex, 2, false, ReportColor.BLACK, 10, ReportColor.WHITE,
		"Pending Approval");
	row.add(enStatus);

	XlsReportCell en1Cell = new XlsReportCell(rowIndex, 4, false, ReportColor.RED, 10, ReportColor.WHITE,
		"en 1 synonym");
	en1Cell.setStrikeout(true);
	row.add(en1Cell);

	XlsReportCell en1Status = new XlsReportCell(rowIndex, 5, false, ReportColor.RED, 10, ReportColor.WHITE,
		"Approved");
	en1Status.setStrikeout(true);
	row.add(en1Status);

	XlsReportCell frCell = new XlsReportCell(rowIndex, 7, false, ReportColor.GREEN, 10, ReportColor.WHITE,
		"fr 1 Term");
	row.add(frCell);

	XlsReportCell frStatus = new XlsReportCell(rowIndex, 8, false, ReportColor.BLACK, 10, ReportColor.WHITE,
		"Approved");
	row.add(frStatus);

	CellTextStyle textDeleted = new CellTextStyle(ReportColor.RED, 0, 13, true);
	CellTextStyle textAdded = new CellTextStyle(ReportColor.GREEN, 13, 13, true);

	XlsReportCell fr1Cell = new XlsReportCell(rowIndex, 10, false, ReportColor.GREEN, 10, ReportColor.WHITE,
		"fr 1 synonym fr 12 synonym");
	fr1Cell.setTextStyle(Arrays.asList(textDeleted, textAdded));
	row.add(fr1Cell);

	return row;
    }
}
