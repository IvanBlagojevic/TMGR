package org.gs4tr.termmanager.model.xls.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class XlsReport implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5819831259922915711L;

    private List<XlsReportSheet> _sheets;

    public XlsReport() {
	_sheets = new ArrayList<>();
    }

    public void addSheet(XlsReportSheet sheet) {
	getSheets().add(sheet);
    }

    public List<XlsReportSheet> getSheets() {
	return _sheets;
    }

    public boolean isEmpty() {
	return _sheets.isEmpty();
    }

    @Override
    public String toString() {
	return "XlsReport [_sheets=" + _sheets + "]";
    }
}
