package org.gs4tr.termmanager.webmvc.model.commands;

import java.util.List;

public class SearchItem {

    private String _name;

    private List<SearchValue> _values;

    public String getName() {
	return _name;
    }

    public List<SearchValue> getValues() {
	return _values;
    }

    public void setName(String name) {
	_name = name;
    }

    public void setValues(List<SearchValue> values) {
	_values = values;
    }
}
