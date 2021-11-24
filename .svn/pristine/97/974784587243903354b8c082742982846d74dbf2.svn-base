package org.gs4tr.termmanager.model.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.gs4tr.termmanager.model.glossary.Description;

public class TermEntrySearchRequest extends AbstractSearchRequest {

    private boolean _ascending;

    private String _descriptionType;

    private Set<Description> _descriptions;

    private Boolean _exportAllDescriptions = Boolean.FALSE;

    private Boolean _forbidden;

    private long _forbiddenTermCount = 0;

    private Boolean _includeSource = Boolean.TRUE;

    private boolean _isFirstTermEntry;

    private List<String> _languagesToExport;

    private Set<Description> _notes;

    private boolean _sharePendingTerms;

    private String _sortProperty;

    private String _sourceLocale;

    private long _startTime;

    private List<String> _targetLocales;

    private List<String> _termAttributes;

    private List<String> _termEntriesAttributes;

    private List<String> _termNotes;

    public void addLanguageToExport(String... languageIds) {
	if (languageIds == null) {
	    return;
	}

	if (_languagesToExport == null) {
	    _languagesToExport = new ArrayList<>();
	}

	Collections.addAll(_languagesToExport, languageIds);
    }

    public String getDescriptionType() {
	return _descriptionType;
    }

    public Set<Description> getDescriptions() {
	return _descriptions;
    }

    public Boolean getExportAllDescriptions() {
	return _exportAllDescriptions;
    }

    public Boolean getForbidden() {
	return _forbidden;
    }

    public long getForbiddenTermCount() {
	return _forbiddenTermCount;
    }

    public List<String> getLanguagesToExport() {
	return _languagesToExport;
    }

    public Set<Description> getNotes() {
	return _notes;
    }

    public String getSortProperty() {
	return _sortProperty;
    }

    public String getSourceLocale() {
	return _sourceLocale;
    }

    public long getStartTime() {
	return _startTime;
    }

    public List<String> getTargetLocales() {
	return _targetLocales;
    }

    public List<String> getTermAttributes() {
	return _termAttributes;
    }

    public List<String> getTermEntriesAttributes() {
	return _termEntriesAttributes;
    }

    public List<String> getTermNotes() {
	return _termNotes;
    }

    public boolean isAscending() {
	return _ascending;
    }

    public boolean isFirstTermEntry() {
	return _isFirstTermEntry;
    }

    public Boolean isIncludeSource() {
	return _includeSource;
    }

    public boolean isSharePendingTerms() {
	return _sharePendingTerms;
    }

    public void setAscending(boolean ascending) {
	_ascending = ascending;
    }

    public void setDescriptionType(String descriptionType) {
	_descriptionType = descriptionType;
    }

    public void setDescriptions(Set<Description> descriptions) {
	_descriptions = descriptions;
    }

    public void setExportAllDescriptions(Boolean exportAllDescriptions) {
	_exportAllDescriptions = exportAllDescriptions;
    }

    public void setFirstTermEntry(boolean isFirstTermEntry) {
	_isFirstTermEntry = isFirstTermEntry;
    }

    public void setForbidden(Boolean forbidden) {
	_forbidden = forbidden;
    }

    public void setForbiddenTermCount(long forbiddenTermCount) {
	_forbiddenTermCount = forbiddenTermCount;
    }

    public void setIncludeSource(Boolean includeSource) {
	_includeSource = includeSource;
    }

    public void setLanguagesToExport(List<String> languagesToExport) {
	_languagesToExport = languagesToExport;
    }

    public void setNotes(Set<Description> notes) {
	_notes = notes;
    }

    public void setSharePendingTerms(boolean sharePendingTerms) {
	_sharePendingTerms = sharePendingTerms;
    }

    public void setSortProperty(String sortProperty) {
	_sortProperty = sortProperty;
    }

    public void setSourceLocale(String sourceLocale) {
	_sourceLocale = sourceLocale;
    }

    public void setStartTime(long startTime) {
	_startTime = startTime;
    }

    public void setTargetLocales(List<String> targetLocales) {
	_targetLocales = targetLocales;
    }

    public void setTermAttributes(List<String> termAttributes) {
	_termAttributes = termAttributes;
    }

    public void setTermEntriesAttributes(List<String> termEntriesAttributes) {
	_termEntriesAttributes = termEntriesAttributes;
    }

    public void setTermNotes(List<String> termNotes) {
	_termNotes = termNotes;
    }
}