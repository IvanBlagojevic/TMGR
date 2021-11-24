package org.gs4tr.termmanager.service.xls;

import java.io.InputStream;
import java.util.List;

import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;

public class BaseXls {

    private XlsConfiguration _configuration;

    private List<String> _headerColumns;

    private Workbook _workbook;

    private Worksheet _worksheet;

    public List<String> getHeaderColumns() {
	return _headerColumns;
    }

    public Workbook getWorkbook() {
	return _workbook;
    }

    public Worksheet getWorksheet() {
	return _worksheet;
    }

    protected XlsConfiguration getConfiguration() {
	return _configuration;
    }

    protected void initConfiguration(int minRow, int minColumn, int maxRow, int maxColumn, int lastRecord) {
	_configuration = new XlsConfiguration();
	_configuration.setMinRow(minRow);
	_configuration.setMinColumn(minColumn);
	_configuration.setMaxRow(maxRow);
	_configuration.setMaxColumn(maxColumn);
	_configuration.setRowIndex(lastRecord);
    }

    protected void initWorkbook(Class<?> clazz, InputStream stream) {
	AsposeFactory asposeFactory = AsposeFactory.getInstance(clazz);
	_workbook = asposeFactory.createWorkbookInstance(stream);
    }

    protected void initWorksheet() {
	WorksheetCollection worksheetCollection = getWorkbook().getWorksheets();
	_worksheet = worksheetCollection.get(0);
    }

    protected void setConfiguration(XlsConfiguration configuration) {
	_configuration = configuration;
    }

    protected void setHeaderColumns(List<String> headerColumns) {
	_headerColumns = headerColumns;
    }
}
