package org.gs4tr.termmanager.webmvc.model.search;

public enum EntitySearch {

    INCLUDE("include", ControlType.MULTI),

    SEARCH_IN("searchIn", ControlType.MULTI);

    private final String _command;

    private ControlType _controlType;

    private EntitySearch(String command, ControlType controlType) {

	_command = command;
	_controlType = controlType;

    }

    public String getCommand() {
	return _command;
    }

    public ControlType getControlType() {
	return _controlType;
    }
}
