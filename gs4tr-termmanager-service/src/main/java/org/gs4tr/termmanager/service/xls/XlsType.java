package org.gs4tr.termmanager.service.xls;

import com.aspose.cells.FileFormatType;

public enum XlsType {

    XLS("xls", FileFormatType.EXCEL_97_TO_2003), XLSX("xlsx", FileFormatType.XLSX);

    private String _displayName;

    private int _type;

    private XlsType(String displayName, int type) {
	_displayName = displayName;
	_type = type;
    }

    public String getDisplayName() {
	return _displayName;
    }

    public int getType() {
	return _type;
    }
}
