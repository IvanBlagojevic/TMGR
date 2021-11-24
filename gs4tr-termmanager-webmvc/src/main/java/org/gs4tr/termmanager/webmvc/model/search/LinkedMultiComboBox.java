package org.gs4tr.termmanager.webmvc.model.search;

import java.util.List;

public class LinkedMultiComboBox extends Control {

    private boolean _required = true;

    private DoubleMultiComboBoxDefaultValue _value;

    private List<LinkedComboItem> _values1;

    private List<LinkedComboItem> _values2;

    public LinkedMultiComboBox(SearchCriteria searchCriteria) {
	super(searchCriteria);
    }

    public DoubleMultiComboBoxDefaultValue getValue() {
	return _value;
    }

    /** first combo box values */
    public List<LinkedComboItem> getValues1() {
	return _values1;
    }

    /** second combo box values */
    public List<LinkedComboItem> getValues2() {
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

    public void setValues1(List<LinkedComboItem> values1) {
	_values1 = values1;
    }

    public void setValues2(List<LinkedComboItem> values2) {
	_values2 = values2;
    }
}
