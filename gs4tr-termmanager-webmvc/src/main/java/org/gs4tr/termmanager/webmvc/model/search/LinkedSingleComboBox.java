package org.gs4tr.termmanager.webmvc.model.search;

import java.util.List;

public class LinkedSingleComboBox extends Control {

    private boolean _required = true;

    private String _value;

    List<LinkedComboItem> _values;

    public LinkedSingleComboBox(SearchCriteria searchCriteria) {
	super(searchCriteria);
    }

    public String getValue() {
	return _value;
    }

    public List<LinkedComboItem> getValues() {
	return _values;
    }

    public boolean isRequired() {
	return _required;
    }

    public void setRequired(boolean required) {
	_required = required;
    }

    public void setValue(String value) {
	_value = value;
    }

    public void setValues(List<LinkedComboItem> values) {
	_values = values;
    }
}
