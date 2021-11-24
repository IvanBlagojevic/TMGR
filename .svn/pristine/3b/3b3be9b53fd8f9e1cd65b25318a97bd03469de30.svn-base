package org.gs4tr.termmanager.persistence.solr.query;

public class DescriptionFilter {

    private boolean _caseSensitive = false;

    private boolean _not = false;

    private String _type;

    private String _value;

    public DescriptionFilter(String type) {
	_type = type;
    }

    public DescriptionFilter(String type, String value) {
	_type = type;
	_value = value;
    }

    public DescriptionFilter(String type, String value, boolean not) {
	this(type, value);
	_not = not;
    }

    public String getType() {
	return _type;
    }

    public String getValue() {
	return _value;
    }

    public boolean isCaseSensitive() {
	return _caseSensitive;
    }

    public boolean isNot() {
	return _not;
    }

    public void setCaseSensitive(boolean caseSensitive) {
	_caseSensitive = caseSensitive;
    }

    public void setNot(boolean not) {
	_not = not;
    }

    public void setType(String type) {
	_type = type;
    }

    public void setValue(String value) {
	_value = value;
    }

}