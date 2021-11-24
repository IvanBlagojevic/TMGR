package org.gs4tr.termmanager.webmvc.model.search;

import java.util.List;

public class StringComboBox extends Control {

    private String _value;

    private List<List<String>> _values;

    public StringComboBox(SearchCriteria searchCriteria) {
	super(searchCriteria);
    }

    public String getValue() {
	return _value;
    }

    public List<List<String>> getValues() {
	return _values;
    }

    public void setValue(String value) {
	_value = value;
    }

    public void setValues(List<List<String>> values) {
	_values = values;
    }
}
