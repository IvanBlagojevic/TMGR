package org.gs4tr.termmanager.webmvc.model.search;

import java.util.ArrayList;
import java.util.List;

public class LinkedComboBoxDefaultValue {

    private String _value1;

    private List<String> _value2;

    public void addValue2(String value) {
	if (_value2 == null) {
	    _value2 = new ArrayList<String>();
	}

	_value2.add(value);
    }

    /** first combo box value */
    public String getValue1() {
	return _value1;
    }

    /** second combo box values */
    public List<String> getValue2() {
	return _value2;
    }

    public void setValue1(String value1) {
	_value1 = value1;
    }

    public void setValue2(List<String> value2) {
	_value2 = value2;
    }
}
