package org.gs4tr.termmanager.model.glossary;

import java.io.Serializable;
import java.util.UUID;

public class Description implements Serializable {

    public static final String ATTRIBUTE = "ATTRIBUTE";

    public static final String NOTE = "NOTE";

    private static final long serialVersionUID = -7236082344223712561L;

    private String _baseType = ATTRIBUTE;

    private String _tempValue;

    private String _type;

    private String _uuid;

    private String _value;

    public Description() {
    }

    public Description(Description description) {
	_baseType = description.getBaseType();
	_type = description.getType();
	_value = description.getValue();
	_uuid = description.getUuid();
    }

    public Description(String type, String value) {
	_baseType = ATTRIBUTE;
	_type = type;
	_value = value;
	_uuid = UUID.randomUUID().toString();
    }

    public Description(String baseType, String type, String value) {
	this(type, value);
	_baseType = baseType;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Description other = (Description) obj;
	if (_baseType == null) {
	    if (other._baseType != null)
		return false;
	} else if (!_baseType.equals(other._baseType))
	    return false;
	if (_type == null) {
	    if (other._type != null)
		return false;
	} else if (!_type.equals(other._type))
	    return false;
	if (_value == null) {
	    if (other._value != null)
		return false;
	} else if (!_value.equals(other._value))
	    return false;
	return true;
    }

    public String getBaseType() {
	return _baseType;
    }

    public String getTempValue() {
	return _tempValue;
    }

    public String getType() {
	return _type;
    }

    public String getUuid() {
	return _uuid;
    }

    public String getValue() {
	return _value;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_baseType == null) ? 0 : _baseType.hashCode());
	result = prime * result + ((_type == null) ? 0 : _type.hashCode());
	result = prime * result + ((_value == null) ? 0 : _value.hashCode());
	return result;
    }

    public void setBaseType(String baseType) {
	_baseType = baseType;
    }

    public void setTempValue(String tempValue) {
	_tempValue = tempValue;
    }

    public void setType(String type) {
	_type = type;
    }

    public void setUuid(String uuid) {
	_uuid = uuid;
    }

    public void setValue(String value) {
	_value = value;
    }

    @Override
    public String toString() {
	return "Description [_baseType=" + _baseType + ", _type=" + _type + ", _value=" + _value + "]";
    }
}
