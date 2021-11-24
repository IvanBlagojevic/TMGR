package org.gs4tr.termmanager.model.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("descriptions")
public class Description {

    public static final String ATTRIBUTE = org.gs4tr.termmanager.model.glossary.Description.ATTRIBUTE;

    public static final String NOTE = org.gs4tr.termmanager.model.glossary.Description.NOTE;

    private String _baseType;

    private boolean _excluded;

    private String _markerId;

    @XStreamAlias("type")
    private String _type;

    @XStreamAlias("value")
    private String _value;

    @ApiModelProperty(value = "Description type, which can be \"ATTRIBUTE\" or \"NOTE\".")
    public String getBaseType() {
	return _baseType;
    }

    @ApiModelProperty(value = "Description id.")
    public String getMarkerId() {
	return _markerId;
    }

    @ApiModelProperty(value = "Name of the attribute or note. For example \"definition\", \"characteristics\", \"partOfSpeech\" etc.")
    public String getType() {
	return _type;
    }

    @ApiModelProperty(value = "Value of the attribute or note.")
    public String getValue() {
	return _value;
    }

    @ApiModelProperty(value = "Excluded description flag. If true, all terms that contain description will be excluded from search result.")
    public boolean isExcluded() {
	return _excluded;
    }

    public void setBaseType(String baseType) {
	_baseType = baseType;
    }

    public void setExcluded(boolean excluded) {
	_excluded = excluded;
    }

    public void setMarkerId(String markerId) {
	_markerId = markerId;
    }

    public void setType(String type) {
	_type = type;
    }

    public void setValue(String value) {
	_value = value;
    }

    @Override
    public String toString() {
	return "Description [_baseType=" + _baseType + ", _type=" + _type + ", _value=" + _value + "]";
    }
}