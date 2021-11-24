package org.gs4tr.termmanager.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Transient;

@Embeddable
public class Attribute implements Serializable {

    private static final long serialVersionUID = 8942799816660647089L;

    private AttributeLevelEnum _attributeLevel;

    private BaseTypeEnum _baseTypeEnum;

    private String _comboValues;

    private InputFieldTypeEnum _inputFieldTypeEnum;

    private Boolean _multiplicity;

    private String _name;

    private Boolean _readOnly;

    private String _renameValue;

    private Boolean _required;

    private Boolean _synchronizable = Boolean.FALSE;

    private TermEntryAttributeTypeEnum _termEntryAttributeType = TermEntryAttributeTypeEnum.TEXT;

    private String _value;

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Attribute other = (Attribute) obj;
	if (_attributeLevel != other._attributeLevel)
	    return false;
	if (_baseTypeEnum != other._baseTypeEnum)
	    return false;
	if (_comboValues == null) {
	    if (other._comboValues != null)
		return false;
	} else if (!_comboValues.equals(other._comboValues))
	    return false;
	if (_inputFieldTypeEnum != other._inputFieldTypeEnum)
	    return false;
	if (_multiplicity == null) {
	    if (other._multiplicity != null)
		return false;
	} else if (!_multiplicity.equals(other._multiplicity))
	    return false;
	if (_name == null) {
	    if (other._name != null)
		return false;
	} else if (!_name.equals(other._name))
	    return false;
	if (_readOnly == null) {
	    if (other._readOnly != null)
		return false;
	} else if (!_readOnly.equals(other._readOnly))
	    return false;
	if (_required == null) {
	    if (other._required != null)
		return false;
	} else if (!_required.equals(other._required))
	    return false;
	if (_synchronizable == null) {
	    if (other._synchronizable != null)
		return false;
	} else if (!_synchronizable.equals(other._synchronizable))
	    return false;
	return _termEntryAttributeType == other._termEntryAttributeType;
    }

    public boolean equalsCustom(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Attribute other = (Attribute) obj;
	if (_attributeLevel != other._attributeLevel)
	    return false;
	if (_name == null) {
	    if (other._name != null)
		return false;
	} else if (!_name.equals(other._name))
	    return false;
	return _termEntryAttributeType == other._termEntryAttributeType;
    }

    @Column(name = "LEVEL")
    @Enumerated(EnumType.ORDINAL)
    public AttributeLevelEnum getAttributeLevel() {
	return _attributeLevel;
    }

    @Column(updatable = false, name = "BASE_TYPE", nullable = false, length = 20)
    @Enumerated(EnumType.ORDINAL)
    public BaseTypeEnum getBaseTypeEnum() {
	return _baseTypeEnum;
    }

    @Column(name = "COMBO_VALUES")
    @Lob
    public String getComboValues() {
	return _comboValues;
    }

    @Column(name = "FIELD_TYPE")
    public InputFieldTypeEnum getInputFieldTypeEnum() {
	return _inputFieldTypeEnum;
    }

    @Column(name = "MULTIPLICITY")
    public Boolean getMultiplicity() {
	return _multiplicity;
    }

    @Column(name = "ATTRIBUTE_NAME", nullable = false, updatable = false, length = 40)
    public String getName() {
	return _name;
    }

    @Column(name = "READ_ONLY")
    public Boolean getReadOnly() {
	return _readOnly;
    }

    @Transient
    public String getRenameValue() {
	return _renameValue;
    }

    @Column(name = "REQUIRED")
    public Boolean getRequired() {
	return _required;
    }

    @Column(name = "SYNCHRONIZABLE")
    public Boolean getSynchronizable() {
	return _synchronizable;
    }

    @Column(name = "TYPE")
    public TermEntryAttributeTypeEnum getTermEntryAttributeType() {
	return _termEntryAttributeType;
    }

    @Column(name = "ATTRIBUTE_VALUE")
    @Lob
    public String getValue() {
	return _value;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_attributeLevel == null) ? 0 : _attributeLevel.hashCode());
	result = prime * result + ((_baseTypeEnum == null) ? 0 : _baseTypeEnum.hashCode());
	result = prime * result + ((_comboValues == null) ? 0 : _comboValues.hashCode());
	result = prime * result + ((_inputFieldTypeEnum == null) ? 0 : _inputFieldTypeEnum.hashCode());
	result = prime * result + ((_multiplicity == null) ? 0 : _multiplicity.hashCode());
	result = prime * result + ((_name == null) ? 0 : _name.hashCode());
	result = prime * result + ((_readOnly == null) ? 0 : _readOnly.hashCode());
	result = prime * result + ((_required == null) ? 0 : _required.hashCode());
	result = prime * result + ((_synchronizable == null) ? 0 : _synchronizable.hashCode());
	return prime * result + ((_termEntryAttributeType == null) ? 0 : _termEntryAttributeType.hashCode());
    }

    @Transient
    public boolean isTextualAttribute() {
	return TermEntryAttributeTypeEnum.isTextualAttribute(getTermEntryAttributeType());
    }

    public void setAttributeLevel(AttributeLevelEnum attributeLevel) {
	_attributeLevel = attributeLevel;
    }

    public void setBaseTypeEnum(BaseTypeEnum baseTypeEnum) {
	_baseTypeEnum = baseTypeEnum;
    }

    public void setComboValues(String comboValues) {
	_comboValues = comboValues;
    }

    public void setInputFieldTypeEnum(InputFieldTypeEnum inputFieldTypeEnum) {
	_inputFieldTypeEnum = inputFieldTypeEnum;
    }

    public void setMultiplicity(Boolean multiplicity) {
	_multiplicity = multiplicity;
    }

    public void setName(String name) {
	_name = name;
    }

    public void setReadOnly(Boolean readOnly) {
	_readOnly = readOnly;
    }

    public void setRenameValue(String renameValue) {
	_renameValue = renameValue;
    }

    public void setRequired(Boolean required) {
	_required = required;
    }

    public void setSynchronizable(Boolean synchronizable) {
	_synchronizable = synchronizable;
    }

    public void setTermEntryAttributeType(TermEntryAttributeTypeEnum termEntryAttributeType) {
	if (termEntryAttributeType != null) {
	    _termEntryAttributeType = termEntryAttributeType;
	}
    }

    public void setValue(String value) {
	_value = value;
    }
}
