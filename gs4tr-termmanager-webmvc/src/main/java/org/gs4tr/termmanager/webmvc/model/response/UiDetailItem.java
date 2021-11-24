package org.gs4tr.termmanager.webmvc.model.response;

public class UiDetailItem {

    private String _name;

    private String _url;

    public UiDetailItem(String name, String url) {
	_name = name;
	_url = url;
    }

    public String getName() {
	return _name;
    }

    public String getUrl() {
	return _url;
    }

    public void setName(String name) {
	_name = name;
    }

    public void setUrl(String url) {
	_url = url;
    }
}
