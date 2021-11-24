package org.gs4tr.termmanager.webmvc.model.search;

import java.util.List;

public class ComboCheckBox extends Control {

    private ComboCheckBoxValue _value;

    private List<List<String>> _values;

    public ComboCheckBox(SearchCriteria searchCriteria) {
	super(searchCriteria);
    }

    public ComboCheckBoxValue getValue() {
	return _value;
    }

    public List<List<String>> getValues() {
	return _values;
    }

    public void setValue(ComboCheckBoxValue value) {
	_value = value;
    }

    public void setValues(List<List<String>> values) {
	_values = values;
    }

}
