package org.gs4tr.termmanager.service.xls.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.xls.report.CellTextStyle;
import org.gs4tr.termmanager.model.xls.report.ImportReportConstants;
import org.gs4tr.termmanager.model.xls.report.ReportColor;
import org.gs4tr.termmanager.model.xls.report.XlsReportCell;
import org.gs4tr.termmanager.service.xls.BaseXls;
import org.gs4tr.termmanager.service.xls.XlsConfiguration;
import org.gs4tr.termmanager.service.xls.XlsHelper;
import org.gs4tr.termmanager.service.xls.XlsWriter;
import org.springframework.util.StringUtils;

import com.aspose.cells.BackgroundType;
import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Color;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.Font;
import com.aspose.cells.Style;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;

/**
 * This is an implementation of import summary report writer. This writer is
 * responsible for creation of import report in XLS file format that can be
 * download by the import user which can review it.
 * 
 * @since 5.0
 */
public class XlsImportReportWriter extends BaseXls implements IXlsImportReportWriter {

    private static final String HEADER_TEXT_1 = "Following data summarizes term and attribute changes as a result of an import.";

    private static final String HEADER_TEXT_2 = "Note that this summary is only for review and should not be imported back into Term Manager.";

    private static final String HEADER_TOP_TEXT = "Term Manager Import Summary";

    /**
     * Default constructor responsible for creating writer instance with default
     * configuration.
     */
    public XlsImportReportWriter() {
	initWorkbook(XlsWriter.class, null);
	initWorksheet();
	XlsConfiguration config = new XlsConfiguration();
	config.setRowIndex(ImportReportConstants.HEADER_ROW_INDEX);
	config.setMinRow(ImportReportConstants.HEADER_ROW_INDEX);
	config.setMaxColumn(ImportReportConstants.DEFAULT_MAX_COLUMN);
	setConfiguration(config);
    }

    /**
     * Creates new work sheet with specified name.
     */
    @Override
    public Worksheet addSheet(String sheetName, int index) {
	WorksheetCollection worksheets = getWorkbook().getWorksheets();

	if (index == 0) {
	    Worksheet sheet = worksheets.get(index);
	    sheet.setName(sheetName);
	    return sheet;
	}

	Worksheet sheet = worksheets.get(sheetName);
	if (Objects.isNull(sheet)) {
	    sheet = worksheets.add(sheetName);
	} else {
	    sheet = worksheets.add(sheetName + index);
	}

	return sheet;
    }

    @Override
    public void saveReport(File xlsFile) {
	try (OutputStream out = new FileOutputStream(xlsFile)) {
	    getWorkbook().save(out, FileFormatType.XLSX);
	    out.flush();
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    /**
     * Writes text into the cell with particular style.
     */
    @Override
    public void writeCellText(Worksheet sheet, XlsReportCell cellStyle) {
	Cells cells = sheet.getCells();

	Cell cell = cells.get(cellStyle.getRow(), cellStyle.getColumn());

	/*
	 * If cell value already exists, add delimiter and new value. This the case with
	 * multiple attributes and notes.
	 */
	String cellValue = StringUtils.isEmpty(cell.getStringValue()) ? cellStyle.getText()
		: cell.getStringValue().concat(XlsHelper.DELIMITER).concat(cellStyle.getText());
	cell.setValue(cellValue);

	Style style = cell.getStyle();

	// if textStyle is not empty, particular style will be applied on the
	// part of the text defined by range
	List<CellTextStyle> textStyle = cellStyle.getTextStyle();
	if (CollectionUtils.isNotEmpty(textStyle)) {
	    for (CellTextStyle cellTextStyle : textStyle) {
		int startIndex = cellTextStyle.getStartIndex();
		int lenght = cellTextStyle.getLength();
		Font font = cell.characters(startIndex, lenght).getFont();
		font.setName(cellStyle.getFontName());
		font.setSize(cellStyle.getFontSize());
		font.setColor(resolveColor(cellTextStyle.getColor()));
		font.setStrikeout(cellTextStyle.isStrikeout());
	    }
	} else if (cellValue.contains(XlsHelper.DELIMITER)) {
	    int startIndex = cellValue.indexOf(cellStyle.getText());
	    int length = cellValue.length();
	    Font font = cell.characters(startIndex, length).getFont();
	    font.setName(cellStyle.getFontName());
	    font.setSize(cellStyle.getFontSize());
	    font.setColor(resolveColor(cellStyle.getFontColor()));
	} else {
	    Font font = style.getFont();
	    font.setName(cellStyle.getFontName());
	    font.setSize(cellStyle.getFontSize());
	    font.setColor(resolveColor(cellStyle.getFontColor()));
	    font.setBold(cellStyle.isBold());
	    font.setStrikeout(cellStyle.isStrikeout());
	}

	style.setForegroundColor(resolveColor(cellStyle.getForegroundColor()));
	style.setPattern(BackgroundType.SOLID);

	cell.setStyle(style);
    }

    /**
     * Writes header based on the same pattern as for import of XLS file.
     * 
     * @throws Exception
     */
    @Override
    public void writeHeader(Worksheet sheet, List<String> header) throws Exception {
	XlsConfiguration config = getConfiguration();
	int rowIndex = ImportReportConstants.HEADER_ROW_INDEX;
	int columnIndex = 0;

	setHeaderColumns(header);

	for (String value : header) {
	    XlsReportCell cellStyle = new XlsReportCell(rowIndex, columnIndex, true, ReportColor.BLACK, 11,
		    ReportColor.WHITE, value);
	    writeCellText(sheet, cellStyle);
	    columnIndex++;
	}

	config.incrementRowIndex();
	config.setMaxColumn(header.size() - 1);

	sheet.autoFitRow(ImportReportConstants.HEADER_ROW_INDEX);
    }

    /**
     * Writes entire row with specific style for each cell.
     */
    @Override
    public void writeRow(Worksheet sheet, List<XlsReportCell> row) {
	if (CollectionUtils.isEmpty(row)) {
	    return;
	}

	for (XlsReportCell cellStyle : row) {
	    writeCellText(sheet, cellStyle);
	}
    }

    /**
     * Writes top header with basic information.
     * 
     * @throws Exception
     */
    @Override
    public void writeTopHeader(Worksheet sheet) throws Exception {
	int maxColumn = getHeaderColumns().size() + 5;
	// Merge first 3 rows
	XlsReportCell cellStyle = new XlsReportCell();
	cellStyle.setRow(0);
	cellStyle.setMergeRows(3);
	cellStyle.setMergeColumns(maxColumn);
	mergeCells(sheet, cellStyle);

	// Write text into A1 cell
	cellStyle = new XlsReportCell(0, 0, true, ReportColor.BLUE, 16, ReportColor.SKY_BLUE, HEADER_TOP_TEXT);
	writeCellText(sheet, cellStyle);

	// Merge cells in 4th row
	cellStyle = new XlsReportCell();
	cellStyle.setRow(3);
	cellStyle.setMergeRows(1);
	cellStyle.setMergeColumns(maxColumn);
	mergeCells(sheet, cellStyle);

	// Write text into A4 cell
	cellStyle = new XlsReportCell(3, 0, false, ReportColor.BLACK, 10, ReportColor.SKY_BLUE, HEADER_TEXT_1);
	writeCellText(sheet, cellStyle);

	// Merge cells in 5th row
	cellStyle = new XlsReportCell();
	cellStyle.setRow(4);
	cellStyle.setMergeRows(1);
	cellStyle.setMergeColumns(maxColumn);
	mergeCells(sheet, cellStyle);

	// Write text into A5 cell
	cellStyle = new XlsReportCell(4, 0, false, ReportColor.BLACK, 10, ReportColor.SKY_BLUE, HEADER_TEXT_2);
	writeCellText(sheet, cellStyle);
    }

    private void mergeCells(Worksheet sheet, XlsReportCell cellStyle) {
	sheet.getCells().merge(cellStyle.getRow(), cellStyle.getColumn(), cellStyle.getMergeRows(),
		cellStyle.getMergeColumns());
    }

    private Color resolveColor(ReportColor reportColor) {
	Color color = Color.getBlack();
	if (ReportColor.WHITE == reportColor) {
	    color = Color.getWhite();
	} else if (ReportColor.BLUE == reportColor) {
	    color = Color.getBlue();
	} else if (ReportColor.SKY_BLUE == reportColor) {
	    color = Color.getSkyBlue();
	} else if (ReportColor.RED == reportColor) {
	    color = Color.getRed();
	} else if (ReportColor.GREEN == reportColor) {
	    color = Color.getGreen();
	}
	return color;
    }
}
