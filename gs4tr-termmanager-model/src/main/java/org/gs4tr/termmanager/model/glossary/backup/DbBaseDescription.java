package org.gs4tr.termmanager.model.glossary.backup;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public class DbBaseDescription implements Serializable {

    public static final String ATTRIBUTE = "ATTRIBUTE";

    public static final String NOTE = "NOTE";

    /**
     * 
     */
    private static final long serialVersionUID = -3834921670189109788L;

    private String _baseType = ATTRIBUTE;

    private Long _id;

    private String _type;

    private String _uuid;

    private byte[] _value;

    public DbBaseDescription() {
	_uuid = UUID.randomUUID().toString();
    }

    public DbBaseDescription(String type, byte[] value) {
	this();
	_baseType = ATTRIBUTE;
	_type = type;
	_value = value;
    }

    public DbBaseDescription(String baseType, String type, byte[] value) {
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
	DbBaseDescription other = (DbBaseDescription) obj;
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
	    return other._value == null;
	} else
	    return Arrays.equals(_value, other._value);
    }

    @Column(name = "BASE_TYPE", nullable = false, updatable = true, length = 10)
    public String getBaseType() {
	return _baseType;
    }

    @Id
    @Column(name = "DESCRIPTION_ID", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
	return _id;
    }

    @Column(name = "TYPE", nullable = false, updatable = true, length = 100)
    public String getType() {
	return _type;
    }

    @Column(name = "UUID", nullable = false, length = 128)
    public String getUuid() {
	return _uuid;
    }

    @Column(name = "VALUE", nullable = false)
    @Lob
    public byte[] getValue() {
	return _value;
    }

    @Transient
    public String getValueAsString() {
	return _value != null ? new String(_value, StandardCharsets.UTF_8) : null;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_baseType == null) ? 0 : _baseType.hashCode());
	result = prime * result + ((_type == null) ? 0 : _type.hashCode());
	return prime * result + ((_value == null) ? 0 : Arrays.hashCode(_value));
    }

    public void setBaseType(String baseType) {
	_baseType = baseType;
    }

    public void setId(Long id) {
	_id = id;
    }

    public void setType(String type) {
	_type = type;
    }

    public void setUuid(String uuid) {
	_uuid = uuid;
    }

    public void setValue(byte[] value) {
	_value = value;
    }

    @Transient
    public void setValueAsBytes(String value) {
	_value = value != null ? value.getBytes(StandardCharsets.UTF_8) : null;
    }

    @Override
    public String toString() {
	return "DbBaseDescription [_baseType=" + _baseType + ", _id=" + _id + ", _type=" + _type + ", _uuid=" + _uuid
		+ ", _value=" + getValueAsString() + "]";
    }
}
