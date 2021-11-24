package org.gs4tr.termmanager.model;

public enum ExportTermTypeEnum {

    REGULAR("Regular"), BLACKLISTED("Blacklisted"), NONE("Regular & Blacklisted");

    public static ExportTermTypeEnum getInstance(Boolean exportForbidden) {
	if (exportForbidden == null) {
	    return NONE;
	} else if (exportForbidden) {
	    return NONE;
	} else {
	    return REGULAR;
	}
    }

    private String _typeName;

    private ExportTermTypeEnum(String typeName) {
	_typeName = typeName;
    }

    public String getTypeName() {
	return _typeName;
    }

}
