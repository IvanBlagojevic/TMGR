package org.gs4tr.termmanager.webmvc.model.search;

import java.util.List;

public class DoubleMultiComboBox extends Control {

    private boolean _required = false;

    private DoubleMultiComboBoxDefaultValue _value;

    private List<SimpleComboItem> _values1;

    private List<SimpleComboItem> _values2;

    public DoubleMultiComboBox(SearchCriteria searchCriteria) {
	super(searchCriteria);
    }

    public DoubleMultiComboBoxDefaultValue getValue() {
	return _value;
    }

    /** first combo box values */
    public List<SimpleComboItem> getValues1() {
	return _values1;
    }

    /** second combo box values */
    public List<SimpleComboItem> getValues2() {
	return _values2;
    }

    public boolean isRequired() {
	return _required;
    }

    public void setRequired(boolean required) {
	_required = required;
    }

    public void setValue(DoubleMultiComboBoxDefaultValue value) {
	_value = value;
    }

    public void setValues1(List<SimpleComboItem> values1) {
	_values1 = values1;
    }

    public void setValues2(List<SimpleComboItem> values2) {
	_values2 = values2;
    }
}
