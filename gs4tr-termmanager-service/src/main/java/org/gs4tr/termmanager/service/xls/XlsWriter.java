package org.gs4tr.termmanager.service.xls;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Font;
import com.aspose.cells.Style;

public class XlsWriter extends BaseXls {

    public XlsWriter() {
	this(null);
    }

    public XlsWriter(InputStream stream) {
	initWorkbook(XlsWriter.class, stream);
	initWorksheet();
	setConfiguration(new XlsConfiguration());
    }

    public void addHeaderCell(String cellValue) {
	if (StringUtils.isEmpty(cellValue)) {
	    return;
	}

	XlsConfiguration config = getConfiguration();

	Cells cells = getWorksheet().getCells();
	Cell cell = cells.get(0, config.getMaxColumn());
	cell.setValue(cellValue);
	makeBoldCell(cell);

	config.incrementMaxColumn();
	getHeaderColumns().add(cellValue);
    }

    public void save(OutputStream out, XlsType type) throws Exception {
	getWorkbook().save(out, type.getType());
    }

    public void write(List<String> row, boolean isHeader) {
	if (CollectionUtils.isEmpty(row)) {
	    return;
	}

	XlsConfiguration config = getConfiguration();

	int rowIndex = config.getRowIndex();

	if (isHeader) {
	    setHeaderColumns(row);
	    config.setMaxColumn(row.size());
	}

	Cells cells = getWorksheet().getCells();

	for (int i = 0; i < row.size(); i++) {
	    Cell cell = cells.get(rowIndex, i);
	    cell.setValue(row.get(i));
	    if (isHeader) {
		makeBoldCell(cell);
	    }
	}

	config.incrementRowIndex();
    }

    private void makeBoldCell(Cell cell) {
	Style style = cell.getStyle();

	Font font = style.getFont();
	font.setBold(true);
	font.setSize(11);

	cell.setStyle(style);
    }
}
