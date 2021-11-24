package org.gs4tr.termmanager.webmvc.controllers;

public class GridConfig {
    private String _dataIndex;

    private String _header;

    private Boolean _hidden;

    private Boolean _sortable;

    private String _sortProperty;

    private Boolean _systemHidden;

    private Integer _width;

    public String getDataIndex() {
	return _dataIndex;
    }

    public String getHeader() {
	return _header;
    }

    public Boolean getHidden() {
	return _hidden;
    }

    public Boolean getSortable() {
	return _sortable;
    }

    public String getSortProperty() {
	return _sortProperty;
    }

    public Boolean getSystemHidden() {
	return _systemHidden;
    }

    public Integer getWidth() {
	return _width;
    }

    public void setSortable(Boolean sortable) {
	_sortable = sortable;
    }

    public void setSortProperty(String sortProperty) {
	_sortProperty = sortProperty;
    }

    public void setSystemHidden(Boolean systemHidden) {
	_systemHidden = systemHidden;
    }

    protected void setDataIndex(String dataIndex) {
	_dataIndex = dataIndex;
    }

    protected void setHeader(String header) {
	_header = header;
    }

    protected void setHidden(Boolean hidden) {
	_hidden = hidden;
    }

    protected void setWidth(Integer width) {
	_width = width;
    }

}
