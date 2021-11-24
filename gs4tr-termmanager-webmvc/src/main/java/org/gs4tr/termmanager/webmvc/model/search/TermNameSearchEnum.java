package org.gs4tr.termmanager.webmvc.model.search;

public enum TermNameSearchEnum {

    TERM ("term", ControlType.TEXT),

    OPTIONS ("options", ControlType.LIST);

    private final String _command;

    private ControlType _controlType;

    private TermNameSearchEnum (String command, ControlType controlType) {

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
