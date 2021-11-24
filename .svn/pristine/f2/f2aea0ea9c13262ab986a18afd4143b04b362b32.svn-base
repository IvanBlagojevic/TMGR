package org.gs4tr.termmanager.model.xls.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class XlsReportRow implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8304131109139307928L;

    private List<XlsReportCell> _cells;

    public XlsReportRow() {
	_cells = new ArrayList<>();
    }

    public XlsReportRow(List<XlsReportCell> cells) {
	_cells = Objects.nonNull(cells) ? cells : new ArrayList<>();
    }

    public void addCell(XlsReportCell cell) {
	_cells.add(cell);
    }

    public List<XlsReportCell> getCells() {
	return _cells;
    }

    @Override
    public String toString() {
	return "XlsReportRow [_cells=" + _cells + "]";
    }
}
