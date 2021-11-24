package org.gs4tr.termmanager.service.xls.report;

import java.io.File;
import java.util.List;

import org.gs4tr.termmanager.model.xls.report.XlsReportCell;

import com.aspose.cells.Worksheet;

public interface IXlsImportReportWriter {

    Worksheet addSheet(String sheetName, int index);

    void saveReport(File xlsFile);

    void writeCellText(Worksheet sheet, XlsReportCell cellStyle);

    void writeHeader(Worksheet sheet, List<String> header) throws Exception;

    void writeRow(Worksheet sheet, List<XlsReportCell> row);

    void writeTopHeader(Worksheet sheet) throws Exception;
}
