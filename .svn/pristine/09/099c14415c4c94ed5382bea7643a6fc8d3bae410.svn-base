package org.gs4tr.termmanager.service.xls;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation3.io.CsvReader.ReturnCode;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Row;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;

public class XlsReader extends BaseXls {

    private final boolean _headerCaseSensitive;

    private final boolean _valuesCaseSensitive;

    public XlsReader(InputStream inputStream, boolean headerCaseSensitive, boolean valuesCaseSensitive) {
	_headerCaseSensitive = headerCaseSensitive;
	_valuesCaseSensitive = valuesCaseSensitive;

	initWorkbook(XlsReader.class, inputStream);
	initWorksheet();
	initHeader();
    }

    public XlsReader(InputStream inputStream, XlsConfiguration config, boolean headerCaseSensitive,
	    boolean valuesCaseSensitive) {
	_headerCaseSensitive = headerCaseSensitive;
	_valuesCaseSensitive = valuesCaseSensitive;

	initWorkbook(XlsReader.class, inputStream);
	initWorksheet();
	setConfiguration(config);
	initHeader(config);
    }

    public Cell getCell(Worksheet sheet, int row, int column) {
	return sheet.getCells().get(row, column);
    }

    public Worksheet getWorksheetByName(String sheetName) {
	WorksheetCollection worksheets = getWorkbook().getWorksheets();
	return worksheets.get(sheetName);
    }

    public boolean isHeaderCaseSensitive() {
	return _headerCaseSensitive;
    }

    public boolean isValuesCaseSensitive() {
	return _valuesCaseSensitive;
    }

    public ReturnCode readNextRecord(List<String> fields) throws HiddenColumnException {
	XlsConfiguration config = getConfiguration();
	config.incrementRowIndex();
	return read(fields, config.getRowIndex(), isValuesCaseSensitive());
    }

    private void clearIfAllEmpty(List<String> listRecords) {
	for (String item : listRecords) {
	    if (StringUtils.isNotEmpty(item)) {
		return;
	    }
	}
	listRecords.clear();
    }

    private void initHeader() {
	Cells cells = getWorksheet().getCells();

	initConfiguration(cells.getMinRow(), cells.getMinColumn(), cells.getMaxRow(), cells.getMaxColumn(),
		cells.getMinRow());

	List<String> headerColumns = new LinkedList<>();

	read(headerColumns, getConfiguration().getMinRow(), isHeaderCaseSensitive());
	setHeaderColumns(headerColumns);
    }

    private void initHeader(XlsConfiguration config) {
	List<String> headerColumns = new LinkedList<>();

	read(headerColumns, config.getMinRow(), isHeaderCaseSensitive());
	setHeaderColumns(headerColumns);
    }

    private ReturnCode read(List<String> listRecords, int rowOrdinal, boolean caseSensitive)
	    throws HiddenColumnException {

	XlsConfiguration config = getConfiguration();

	listRecords.clear();

	Cells cells = getWorksheet().getCells();

	Row row = cells.getRows().get(rowOrdinal);

	for (int i = config.getMinColumn(); i < config.getMaxColumn() + 1; i++) {

	    Cell cell = row.get(i);

	    int column = cell.getColumn();
	    int rowNum = cell.getRow();

	    if (cells.getColumns().get(column).isHidden()) {
		throw new HiddenColumnException(
			String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.14"), column + 1));
	    }

	    if (row.isHidden()) {
		throw new HiddenColumnException(
			String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.15"), rowNum + 1));

	    }

	    String cellValue = cell.getStringValue();

	    if (cellValue == null) {
		cellValue = StringUtils.EMPTY;
	    }

	    cellValue = XlsReaderHelper.replaceBrakingLineCharacters(cellValue);
	    cellValue = XlsReaderHelper.removeMultipleWhiteSpace(cellValue);

	    if (!caseSensitive) {
		cellValue = cellValue.toLowerCase();
	    }

	    cellValue = cellValue.trim();

	    listRecords.add(cellValue);
	}

	clearIfAllEmpty(listRecords);

	if (rowOrdinal < config.getMaxRow()) {
	    return ReturnCode.LINE_END;
	} else {
	    return ReturnCode.FILE_END;
	}
    }
}
