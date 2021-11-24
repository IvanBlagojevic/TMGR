package org.gs4tr.termmanager.webmvc.model.commands;

import java.util.List;

public class SearchValue {

    private String _name;

    private List<String> _values;

    public String getName() {
	return _name;
    }

    public List<String> getValues() {
	return _values;
    }

    public void setName(String name) {
	_name = name;
    }

    public void setValues(List<String> values) {
	_values = values;
    }
}
