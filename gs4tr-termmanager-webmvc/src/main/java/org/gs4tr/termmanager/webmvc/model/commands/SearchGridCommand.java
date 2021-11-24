package org.gs4tr.termmanager.webmvc.model.commands;

import org.gs4tr.termmanager.service.model.command.JsonCommand;

public class SearchGridCommand extends JsonCommand {

    private Boolean _ascending;

    private Integer _index;

    private Integer _size;

    private String _sortProperty;

    public Boolean getAscending() {
	return _ascending;
    }

    public Integer getIndex() {
	return _index;
    }

    public Integer getSize() {
	return _size;
    }

    public String getSortProperty() {
	return _sortProperty;
    }

    public void setAscending(Boolean ascending) {
	_ascending = ascending;
    }

    public void setIndex(Integer index) {
	_index = index;
    }

    public void setSize(Integer size) {
	_size = size;
    }

    public void setSortProperty(String sortProperty) {
	_sortProperty = sortProperty;
    }
}
