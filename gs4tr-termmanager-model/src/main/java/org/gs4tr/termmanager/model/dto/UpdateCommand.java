package org.gs4tr.termmanager.model.dto;

import java.io.Serializable;

public class UpdateCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    private String _assignee;
    private String _command;
    private String _languageId;
    private String _markerId;
    private String _parentMarkerId;
    private String _status;
    private String _subType;
    private String _type;
    private String _value;

    public String getAssignee() {
	return _assignee;
    }

    public String getCommand() {
	return _command;
    }

    public String getLanguageId() {
	return _languageId;
    }

    public String getMarkerId() {
	return _markerId;
    }

    public String getParentMarkerId() {
	return _parentMarkerId;
    }

    public String getStatus() {
	return _status;
    }

    public String getSubType() {
	return _subType;
    }

    public String getType() {
	return _type;
    }

    public String getValue() {
	return _value;
    }

    public void setAssignee(String assignee) {
	_assignee = assignee;
    }

    public void setCommand(String command) {
	_command = command;
    }

    public void setLanguageId(String languageId) {
	_languageId = languageId;
    }

    public void setMarkerId(String markerId) {
	_markerId = markerId;
    }

    public void setParentMarkerId(String parentMarkerId) {
	_parentMarkerId = parentMarkerId;
    }

    public void setStatus(String status) {
	_status = status;
    }

    public void setSubType(String subType) {
	_subType = subType;
    }

    public void setType(String type) {
	_type = type;
    }

    public void setValue(String value) {
	this._value = value;
    }
}
