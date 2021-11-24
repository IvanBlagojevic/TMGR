package org.gs4tr.termmanager.model.xls;

import org.gs4tr.termmanager.model.glossary.Description;

public class XlsDescription {

    public String _baseType = Description.ATTRIBUTE;

    private String _type;

    private String _value;

    public XlsDescription(String type, String value) {
	_type = type;
	_value = value;
    }

    public XlsDescription(String type, String value, String baseType) {
	this(type, value);
	_baseType = baseType;
    }

    public String getBaseType() {
	return _baseType;
    }

    public String getType() {
	return _type;
    }

    public String getValue() {
	return _value;
    }

    public void setType(String type) {
	_type = type;
    }

    public void setValue(String value) {
	_value = value;
    }
}
