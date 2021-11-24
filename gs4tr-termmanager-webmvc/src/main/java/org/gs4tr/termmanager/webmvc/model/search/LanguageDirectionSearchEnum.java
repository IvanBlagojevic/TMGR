package org.gs4tr.termmanager.webmvc.model.search;

public enum LanguageDirectionSearchEnum {

    SOURCE("source", ControlType.LIST),

    TARGET("target", ControlType.MULTI);

    private final String _command;

    private ControlType _controlType;

    private LanguageDirectionSearchEnum(String command, ControlType controlType) {

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
