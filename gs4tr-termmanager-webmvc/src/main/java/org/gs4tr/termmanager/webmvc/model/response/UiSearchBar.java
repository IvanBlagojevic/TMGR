package org.gs4tr.termmanager.webmvc.model.response;

public class UiSearchBar {

    private String _name;

    private Object _value;

    public String getName() {
	return _name;
    }

    public Object getValue() {
	return _value;
    }

    public void setName(String name) {
	_name = name;
    }

    public void setValue(Object value) {
	_value = value;
    }
}