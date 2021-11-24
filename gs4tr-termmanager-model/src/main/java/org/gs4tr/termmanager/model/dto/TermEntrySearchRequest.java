package org.gs4tr.termmanager.model.dto;

import java.util.Date;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.Ticket;

public class TermEntrySearchRequest {

    private Date _dateCreatedFrom;

    private Date _dateCreatedTo;

    private Date _dateModifiedFrom;

    private Date _dateModifiedTo;

    private Boolean _forbidden;

    private Boolean _includeSource;

    private Ticket _projectTicket;

    private String _sourceLocale;

    private String[] _targetLocales;

    private List<String> _termEntryIds;

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

    public Boolean getForbidden() {
	return _forbidden;
    }

    public Ticket getProjectTicket() {
	return _projectTicket;
    }

    public String getSourceLocale() {
	return _sourceLocale;
    }

    public String[] getTargetLocales() {
	return _targetLocales;
    }

    public List<String> getTermEntryIds() {
	return _termEntryIds;
    }

    public Boolean isIncludeSource() {
	return _includeSource;
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

    public void setForbidden(Boolean forbidden) {
	_forbidden = forbidden;
    }

    public void setIncludeSource(Boolean includeSource) {
	_includeSource = includeSource;
    }

    public void setProjectTicket(Ticket projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setSourceLocale(String sourceLocale) {
	_sourceLocale = sourceLocale;
    }

    public void setTargetLocales(String[] targetLocales) {
	_targetLocales = targetLocales;
    }

    public void setTermEntryIds(List<String> termEntryIds) {
	_termEntryIds = termEntryIds;
    }
}