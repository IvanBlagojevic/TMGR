package org.gs4tr.termmanager.webmvc.model.search;

import java.util.List;

public class LinkedComboBox extends Control {

    private boolean _required = true;

    private LinkedComboBoxDefaultValue _value;

    List<LinkedComboItem> _values;

    public LinkedComboBox(SearchCriteria searchCriteria) {
	super(searchCriteria);
    }

    public LinkedComboBoxDefaultValue getValue() {
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

    public void setValue(LinkedComboBoxDefaultValue value) {
	_value = value;
    }

    public void setValues(List<LinkedComboItem> values) {
	_values = values;
    }
}
