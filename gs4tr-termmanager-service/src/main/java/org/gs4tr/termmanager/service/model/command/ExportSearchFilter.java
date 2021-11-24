package org.gs4tr.termmanager.service.model.command;

import java.util.List;

public class ExportSearchFilter {

    public static class EntityType {

	private List<String> _value1;

	private List<String> _value2;

	/** TERM, ATTRIBUTES */
	public List<String> getValue1() {
	    return _value1;
	}

	/** SOURCE, TARGET */
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

    public static class TermNameAndSearchType {

	private String _value1;

	private String _value2;

	/** term text */
	public String getValue1() {
	    return _value1;
	}

	/** search type */
	public String getValue2() {
	    return _value2;
	}

	public void setValue1(String value1) {
	    _value1 = value1;
	}

	public void setValue2(String value2) {
	    _value2 = value2;
	}
    }

    private boolean _ascending = true;

    private EntityType _entityType;

    private String _sortProperty;

    private TermNameAndSearchType _termNameAndSearchType;

    public EntityType getEntityType() {
	return _entityType;
    }

    public String getSortProperty() {
	return _sortProperty;
    }

    public TermNameAndSearchType getTermNameAndSearchType() {
	return _termNameAndSearchType;
    }

    public boolean isAscending() {
	return _ascending;
    }

    public void setAscending(boolean ascending) {
	_ascending = ascending;
    }

    public void setEntityType(EntityType entityType) {
	_entityType = entityType;
    }

    public void setSortProperty(String sortProperty) {
	_sortProperty = sortProperty;
    }

    public void setTermNameAndSearchType(TermNameAndSearchType termNameAndSearchType) {
	_termNameAndSearchType = termNameAndSearchType;
    }
}
