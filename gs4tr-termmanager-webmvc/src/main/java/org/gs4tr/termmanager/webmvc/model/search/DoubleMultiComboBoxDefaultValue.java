package org.gs4tr.termmanager.webmvc.model.search;

import java.util.List;

public class DoubleMultiComboBoxDefaultValue {

    private List<String> _value1;

    private List<String> _value2;

    /** first combo box default value */
    public List<String> getValue1() {
	return _value1;
    }

    /** second combo box default value */
    public List<String> getValue2() {
	return _value2;
    }

    public void setValue1(List<String> value1) {
	_value1 = value1;
    }

    public void setValue2(List<String> value2) {
	_value2 = value2;
    }
}
