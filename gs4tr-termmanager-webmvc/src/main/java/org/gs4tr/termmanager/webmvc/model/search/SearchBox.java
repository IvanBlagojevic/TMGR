package org.gs4tr.termmanager.webmvc.model.search;

public class SearchBox {

    private String _name;

    public SearchBox(String name) {
	_name = name;
    }

    public SearchBox() {
    }

    public String getName() {
	return _name;
    }

    public void setName(String name) {
	_name = name;
    }
}
