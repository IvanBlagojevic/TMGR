package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.dto.DateRange;
import org.gs4tr.termmanager.model.dto.Description;
import org.gs4tr.termmanager.model.dto.converter.DescriptionConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.ExportCommand;

public class DtoExportCommand implements DtoTaskHandlerCommand<ExportCommand> {

    private DateRange _creationDate;

    private String[] _creationUsers;

    private String _exportFormat;

    private String[] _hideBlanks;

    private boolean _includeSource = true;

    private DateRange _modificationDate;

    private String[] _modificationUsers;

    private Ticket _projectTicket;

    private DtoExportSearchFilter _searchFilter;

    private String _sourceLocale;

    private String[] _targetLocales;

    private String[] _targetStatuses;

    private Description[] _termAttributesFilter;

    private String[] _termAttributeTypes;

    private Description[] _termEntryAttributesFilter;

    private String[] _termEntryAttributeTypes;

    private Description[] _termNotesFilter;

    private String[] _termNoteTypes;

    @Override
    public ExportCommand convertToInternalTaskHandlerCommand() {

	ExportCommand command = new ExportCommand();

	DateRange creationDate = getCreationDate();
	if (creationDate != null) {
	    Long fromDate = creationDate.getFromDate();
	    command.setDateCreatedFrom(fromDate != null ? new Date(fromDate) : null);
	    Long toDate = creationDate.getToDate();
	    command.setDateCreatedTo(toDate != null ? new Date(toDate) : null);
	}

	DateRange modificationDate = getModificationDate();
	if (modificationDate != null) {
	    Long fromDate = modificationDate.getFromDate();
	    command.setDateModifiedFrom(fromDate != null ? new Date(fromDate) : null);
	    Long toDate = modificationDate.getToDate();
	    command.setDateModifiedTo(toDate != null ? new Date(toDate) : null);
	}

	command.setExportFormat(getExportFormat());

	command.setProjectId(TicketConverter.fromDtoToInternal(getProjectTicket(), Long.class));

	command.setSourceLocale(getSourceLocale());

	String[] targetLocales = getTargetLocales();
	if (ArrayUtils.isNotEmpty(targetLocales)) {
	    command.setTargetLocales(Arrays.asList(targetLocales));
	}

	String[] hideBlanks = getHideBlanks();
	if (ArrayUtils.isNotEmpty(hideBlanks)) {
	    command.setHideBlanks(Arrays.asList(hideBlanks));
	}

	Description[] termEntryAttributesFilter = getTermEntryAttributesFilter();
	if (ArrayUtils.isNotEmpty(termEntryAttributesFilter)) {
	    command.setTermEntryAttributesFilter(DescriptionConverter.fromDtoToInternal(termEntryAttributesFilter));
	}

	Description[] termAttributesFilter = getTermAttributesFilter();
	if (ArrayUtils.isNotEmpty(termAttributesFilter)) {
	    command.setTermAttributesFilter(DescriptionConverter.fromDtoToInternal(termAttributesFilter));
	}

	Description[] termNotesFilter = getTermNotesFilter();
	if (ArrayUtils.isNotEmpty(termNotesFilter)) {
	    command.setTermNotesFilter(DescriptionConverter.fromDtoToInternal(termNotesFilter));
	}

	String[] termEntryAttributeTypes = getTermEntryAttributeTypes();
	if (ArrayUtils.isNotEmpty(termEntryAttributeTypes)) {
	    command.setTermEntryAttributeTypes(Arrays.asList(termEntryAttributeTypes));
	}

	String[] termAttributeTypes = getTermAttributeTypes();
	if (ArrayUtils.isNotEmpty(termAttributeTypes)) {
	    command.setTermAttributeTypes(Arrays.asList(termAttributeTypes));
	}

	String[] termNoteTypes = getTermNoteTypes();
	if (ArrayUtils.isNotEmpty(termNoteTypes)) {
	    command.setTermNoteTypes(Arrays.asList(termNoteTypes));
	}

	String[] statuses = getTargetStatuses();
	if (ArrayUtils.isNotEmpty(statuses)) {
	    command.setTargetStatuses(Arrays.asList(statuses));
	}

	command.setIncludeSource(isIncludeSource());

	DtoExportSearchFilter searchFilter = getSearchFilter();
	if (searchFilter != null) {
	    command.setSearchFilter(searchFilter.convertToInternalTaskHandlerCommand());
	}

	String[] creationUsers = getCreationUsers();
	if (ArrayUtils.isNotEmpty(creationUsers)) {
	    command.setCreationUsers(Arrays.asList(creationUsers));
	}

	String[] modificationUsers = getModificationUsers();
	if (ArrayUtils.isNotEmpty(modificationUsers)) {
	    command.setModificationUsers(Arrays.asList(modificationUsers));
	}

	return command;
    }

    public DateRange getCreationDate() {
	return _creationDate;
    }

    public String[] getCreationUsers() {
	return _creationUsers;
    }

    public String getExportFormat() {
	return _exportFormat;
    }

    public String[] getHideBlanks() {
	return _hideBlanks;
    }

    public DateRange getModificationDate() {
	return _modificationDate;
    }

    public String[] getModificationUsers() {
	return _modificationUsers;
    }

    public Ticket getProjectTicket() {
	return _projectTicket;
    }

    public DtoExportSearchFilter getSearchFilter() {
	return _searchFilter;
    }

    public String getSourceLocale() {
	return _sourceLocale;
    }

    public String[] getTargetLocales() {
	return _targetLocales;
    }

    public String[] getTargetStatuses() {
	return _targetStatuses;
    }

    public Description[] getTermAttributesFilter() {
	return _termAttributesFilter;
    }

    public String[] getTermAttributeTypes() {
	return _termAttributeTypes;
    }

    public Description[] getTermEntryAttributesFilter() {
	return _termEntryAttributesFilter;
    }

    public String[] getTermEntryAttributeTypes() {
	return _termEntryAttributeTypes;
    }

    public Description[] getTermNotesFilter() {
	return _termNotesFilter;
    }

    public String[] getTermNoteTypes() {
	return _termNoteTypes;
    }

    public boolean isIncludeSource() {
	return _includeSource;
    }

    public void setCreationDate(DateRange creationDate) {
	_creationDate = creationDate;
    }

    public void setCreationUsers(String[] creationUsers) {
	_creationUsers = creationUsers;
    }

    public void setExportFormat(String exportFormat) {
	_exportFormat = exportFormat;
    }

    public void setHideBlanks(String[] hideBlanks) {
	_hideBlanks = hideBlanks;
    }

    public void setIncludeSource(boolean includeSource) {
	_includeSource = includeSource;
    }

    public void setModificationDate(DateRange modificationDate) {
	_modificationDate = modificationDate;
    }

    public void setModificationUsers(String[] modificationUsers) {
	_modificationUsers = modificationUsers;
    }

    public void setProjectTicket(Ticket projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setSearchFilter(DtoExportSearchFilter searchFilter) {
	_searchFilter = searchFilter;
    }

    public void setSourceLocale(String sourceLocale) {
	_sourceLocale = sourceLocale;
    }

    public void setTargetLocales(String[] targetLocales) {
	_targetLocales = targetLocales;
    }

    public void setTargetStatuses(String[] targetStatuses) {
	_targetStatuses = targetStatuses;
    }

    public void setTermAttributesFilter(Description[] termAttributesFilter) {
	_termAttributesFilter = termAttributesFilter;
    }

    public void setTermAttributeTypes(String[] termAttributeTypes) {
	_termAttributeTypes = termAttributeTypes;
    }

    public void setTermEntryAttributesFilter(Description[] termEntryAttributesFilter) {
	_termEntryAttributesFilter = termEntryAttributesFilter;
    }

    public void setTermEntryAttributeTypes(String[] termEntryAttributeTypes) {
	_termEntryAttributeTypes = termEntryAttributeTypes;
    }

    public void setTermNotesFilter(Description[] termNotesFilter) {
	_termNotesFilter = termNotesFilter;
    }

    public void setTermNoteTypes(String[] termNoteTypes) {
	_termNoteTypes = termNoteTypes;
    }
}
