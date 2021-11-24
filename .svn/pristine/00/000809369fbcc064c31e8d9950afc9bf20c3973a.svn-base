package org.gs4tr.termmanager.service.model.command.dto;

import static java.util.Arrays.asList;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.service.model.command.ExportSearchFilter;

public class DtoExportSearchFilter implements DtoTaskHandlerCommand<ExportSearchFilter> {

    private boolean _ascending;
    private DtoEntityType _entityType;
    private String _sortProperty;
    private DtoTermNameAndSearchType _termNameAndSearchType;

    @SuppressWarnings("unchecked")
    @Override
    public ExportSearchFilter convertToInternalTaskHandlerCommand() {
	ExportSearchFilter command = new ExportSearchFilter();
	command.setAscending(isAscending());
	command.setSortProperty(getSortProperty());

	DtoEntityType dtoEntityType = getEntityType();
	if (dtoEntityType != null) {
	    ExportSearchFilter.EntityType entityType = new ExportSearchFilter.EntityType();
	    String[] dtoValue1 = dtoEntityType.getValue1();
	    if (!ArrayUtils.isEmpty(dtoValue1)) {
		entityType.setValue1(asList(dtoValue1));
	    }
	    String[] dtoValue2 = dtoEntityType.getValue2();
	    if (!ArrayUtils.isEmpty(dtoValue2)) {
		entityType.setValue2(asList(dtoValue2));
	    }
	    command.setEntityType(entityType);
	}

	DtoTermNameAndSearchType dtoTermNameAndSearchType = getTermNameAndSearchType();
	if (dtoTermNameAndSearchType != null) {
	    ExportSearchFilter.TermNameAndSearchType termNameAndSearchType = new ExportSearchFilter.TermNameAndSearchType();

	    if (StringUtils.isNotEmpty(dtoTermNameAndSearchType.getValue2())) {
		termNameAndSearchType.setValue2(dtoTermNameAndSearchType.getValue2());
	    }

	    if (StringUtils.isNotEmpty(dtoTermNameAndSearchType.getValue1())) {
		termNameAndSearchType.setValue1(dtoTermNameAndSearchType.getValue1());
		command.setTermNameAndSearchType(termNameAndSearchType);
	    }
	}

	return command;
    }

    public DtoEntityType getEntityType() {
	return _entityType;
    }

    public String getSortProperty() {
	return _sortProperty;
    }

    public DtoTermNameAndSearchType getTermNameAndSearchType() {
	return _termNameAndSearchType;
    }

    public boolean isAscending() {
	return _ascending;
    }

    public void setAscending(boolean ascending) {
	_ascending = ascending;
    }

    public void setEntityType(DtoEntityType entityType) {
	_entityType = entityType;
    }

    public void setSortProperty(String sortProperty) {
	_sortProperty = sortProperty;
    }

    public void setTermNameAndSearchType(DtoTermNameAndSearchType termNameAndSearchType) {
	_termNameAndSearchType = termNameAndSearchType;
    }

    public static class DtoEntityType {

	private String[] _value1;

	private String[] _value2;

	/** TERM, ATTRIBUTES */
	public String[] getValue1() {
	    return _value1;
	}

	/** SOURCE, TARGET */
	public String[] getValue2() {
	    return _value2;
	}

	public void setValue1(String[] value1) {
	    _value1 = value1;
	}

	public void setValue2(String[] value2) {
	    _value2 = value2;
	}
    }

    public static class DtoTermNameAndSearchType {

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
}
