package org.gs4tr.termmanager.model;

public enum LastOperationEnum {
    CREATED("C"), DELETED("D"), UPDATED("U");
    private final String _shortLabel;

    LastOperationEnum(String shortLabel) {
	_shortLabel = shortLabel;
    }

    public String getShortLabel() {
	return _shortLabel;
    }

}
