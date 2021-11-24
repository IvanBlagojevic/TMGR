package org.gs4tr.termmanager.service.xls.report.builder.terminology;

import java.util.Objects;

import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.xls.report.ReportColor;

public class ReportDescription {

    private String _baseType;

    private ReportColor _color;

    private String _newValue;

    private String _oldValue;

    private String _type;

    public ReportDescription(Description description) {
	_type = description.getType();
	_oldValue = Objects.nonNull(description.getValue()) ? description.getValue().trim() : description.getValue();
	_newValue = _oldValue;
	_baseType = description.getBaseType();
	_color = ReportColor.BLACK;
    }

    public ReportDescription(Description oldDescription, Description newDescription) {
	this(oldDescription);
	_newValue = Objects.nonNull(newDescription.getValue()) ? newDescription.getValue().trim()
		: newDescription.getValue();
	_color = ReportColor.RED_GREEN;
    }

    public ReportDescription(Description description, ReportColor color) {
	this(description);
	_color = color;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ReportDescription other = (ReportDescription) obj;
	if (_oldValue == null) {
	    if (other._oldValue != null)
		return false;
	} else if (!_oldValue.equals(other._oldValue))
	    return false;
	if (_type == null) {
	    if (other._type != null)
		return false;
	} else if (!_type.equals(other._type))
	    return false;
	return true;
    }

    public ReportColor getColor() {
	return _color;
    }

    public String getNewValue() {
	return _newValue;
    }

    public String getOldValue() {
	return _oldValue;
    }

    public String getType() {
	return _type;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_oldValue == null) ? 0 : _oldValue.hashCode());
	result = prime * result + ((_type == null) ? 0 : _type.hashCode());
	return result;
    }

    public boolean isNote() {
	return Description.NOTE.equals(_baseType);
    }

    @Override
    public String toString() {
	return "ReportDescription [_type=" + _type + ", _oldValue=" + _oldValue + ", _newValue=" + _newValue + "]";
    }
}
