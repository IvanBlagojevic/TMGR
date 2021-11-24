package org.gs4tr.termmanager.service.model.command;

import java.util.Date;
import java.util.List;

import org.gs4tr.termmanager.model.glossary.Description;

public class ExportCommand {

    private List<String> _creationUsers;

    private Date _dateCreatedFrom;

    private Date _dateCreatedTo;

    private Date _dateModifiedFrom;

    private Date _dateModifiedTo;

    private String _exportFormat;

    private List<String> _hideBlanks;

    private boolean _includeSource;

    private List<String> _modificationUsers;

    private Long _projectId;

    private ExportSearchFilter _searchFilter;

    private String _sourceLocale;

    private List<String> _targetLocales;

    private List<String> _targetStatuses;

    private List<Description> _termAttributesFilter;

    private List<String> _termAttributeTypes;

    private List<Description> _termEntryAttributesFilter;

    private List<String> _termEntryAttributeTypes;

    private List<Description> _termNotesFilter;

    private List<String> _termNoteTypes;

    public List<String> getCreationUsers() {
	return _creationUsers;
    }

    public Date getDateCreatedFrom() {
	return _dateCreatedFrom;
    }

    public Date getDateCreatedTo() {
	return _dateCreatedTo;
    }

    public Date getDateModifiedFrom() {
	return _dateModifiedFrom;
    }

    public Date getDateModifiedTo() {
	return _dateModifiedTo;
    }

    public String getExportFormat() {
	return _exportFormat;
    }

    public List<String> getHideBlanks() {
	return _hideBlanks;
    }

    public List<String> getModificationUsers() {
	return _modificationUsers;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public ExportSearchFilter getSearchFilter() {
	return _searchFilter;
    }

    public String getSourceLocale() {
	return _sourceLocale;
    }

    public List<String> getTargetLocales() {
	return _targetLocales;
    }

    public List<String> getTargetStatuses() {
	return _targetStatuses;
    }

    public List<Description> getTermAttributesFilter() {
	return _termAttributesFilter;
    }

    public List<String> getTermAttributeTypes() {
	return _termAttributeTypes;
    }

    public List<Description> getTermEntryAttributesFilter() {
	return _termEntryAttributesFilter;
    }

    public List<String> getTermEntryAttributeTypes() {
	return _termEntryAttributeTypes;
    }

    public List<Description> getTermNotesFilter() {
	return _termNotesFilter;
    }

    public List<String> getTermNoteTypes() {
	return _termNoteTypes;
    }

    public boolean isIncludeSource() {
	return _includeSource;
    }

    public void setCreationUsers(List<String> creationUsers) {
	_creationUsers = creationUsers;
    }

    public void setDateCreatedFrom(Date dateCreatedFrom) {
	_dateCreatedFrom = dateCreatedFrom;
    }

    public void setDateCreatedTo(Date dateCreatedTo) {
	_dateCreatedTo = dateCreatedTo;
    }

    public void setDateModifiedFrom(Date dateModifiedFrom) {
	_dateModifiedFrom = dateModifiedFrom;
    }

    public void setDateModifiedTo(Date dateModifiedTo) {
	_dateModifiedTo = dateModifiedTo;
    }

    public void setExportFormat(String exportFormat) {
	_exportFormat = exportFormat;
    }

    public void setHideBlanks(List<String> hideBlanks) {
	_hideBlanks = hideBlanks;
    }

    public void setIncludeSource(boolean includeSource) {
	_includeSource = includeSource;
    }

    public void setModificationUsers(List<String> modificationUsers) {
	_modificationUsers = modificationUsers;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }

    public void setSearchFilter(ExportSearchFilter searchFilter) {
	_searchFilter = searchFilter;
    }

    public void setSourceLocale(String sourceLocale) {
	_sourceLocale = sourceLocale;
    }

    public void setTargetLocales(List<String> targetLocales) {
	_targetLocales = targetLocales;
    }

    public void setTargetStatuses(List<String> targetStatuses) {
	_targetStatuses = targetStatuses;
    }

    public void setTermAttributesFilter(List<Description> termAttributesFilter) {
	_termAttributesFilter = termAttributesFilter;
    }

    public void setTermAttributeTypes(List<String> termAttributeTypes) {
	_termAttributeTypes = termAttributeTypes;
    }

    public void setTermEntryAttributesFilter(List<Description> termEntryAttributesFilter) {
	_termEntryAttributesFilter = termEntryAttributesFilter;
    }

    public void setTermEntryAttributeTypes(List<String> termEntryAttributeTypes) {
	_termEntryAttributeTypes = termEntryAttributeTypes;
    }

    public void setTermNotesFilter(List<Description> termNotesFilter) {
	_termNotesFilter = termNotesFilter;
    }

    public void setTermNoteTypes(List<String> termNoteTypes) {
	_termNoteTypes = termNoteTypes;
    }
}
