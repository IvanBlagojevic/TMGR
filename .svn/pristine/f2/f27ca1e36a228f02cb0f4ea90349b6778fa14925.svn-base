package org.gs4tr.termmanager.webmvc.configuration;

public class FieldInfo {

    private final Boolean _hidden;

    private final String _name;

    private final Boolean _sortable;

    private final String _sortProperty;

    private final Boolean _systemHidden;

    private final String _titleKey;

    private final Integer _width;

    public FieldInfo(String name, Integer width, Boolean hidden, String titleKey, Boolean systemHidden,
	    Boolean sortable, String sortProperty) {
	_name = name;
	_width = width;
	_hidden = hidden;
	_titleKey = titleKey;
	_systemHidden = systemHidden;
	_sortable = sortable;
	_sortProperty = sortProperty;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	FieldInfo other = (FieldInfo) obj;
	if (_name == null) {
	    if (other._name != null)
		return false;
	} else if (!_name.equals(other._name))
	    return false;
	return true;
    }

    public Boolean getHidden() {
	return _hidden;
    }

    public String getName() {
	return _name;
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

    public String getTitleKey() {
	return _titleKey;
    }

    public Integer getWidth() {
	return _width;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_name == null) ? 0 : _name.hashCode());
	return result;
    }
}
