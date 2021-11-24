package org.gs4tr.termmanager.model.dto;

import org.gs4tr.termmanager.model.dto.converter.LanguageConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.Term;

public class BaseDtoTerm {

    private Long _creationDate;
    private Boolean _forbidden;
    private Language _language;
    private String _markerId;
    private Long _modificationDate;
    private String _status;
    private String _termName;
    private String _termTicket;

    public BaseDtoTerm(Term term) {
	setForbidden(term.isForbidden());
	setTermName(term.getName());
	setTermTicket(TicketConverter.fromInternalToDto(term.getUuId()));
	if (term.getDateCreated() != null && term.getDateCreated() > 0) {
	    setCreationDate(term.getDateCreated());
	}
	if (term.getDateModified() != null && term.getDateModified() > 0) {
	    setModificationDate(term.getDateModified());
	}
	setMarkerId(term.getUuId());
	setLanguage(LanguageConverter.fromInternalToDto(term.getLanguageId()));
	setStatus(term.getStatus());
    }

    public Long getCreationDate() {
	return _creationDate;
    }

    public Boolean getForbidden() {
	return _forbidden;
    }

    public Language getLanguage() {
	return _language;
    }

    public String getMarkerId() {
	return _markerId;
    }

    public Long getModificationDate() {
	return _modificationDate;
    }

    public String getStatus() {
	return _status;
    }

    public String getTermName() {
	return _termName;
    }

    public String getTermTicket() {
	return _termTicket;
    }

    public void setCreationDate(Long creationDate) {
	_creationDate = creationDate;
    }

    public void setForbidden(Boolean forbidden) {
	_forbidden = forbidden;
    }

    public void setLanguage(Language language) {
	_language = language;
    }

    public void setMarkerId(String markerId) {
	_markerId = markerId;
    }

    public void setModificationDate(Long modificationDate) {
	_modificationDate = modificationDate;
    }

    public void setStatus(String status) {
	_status = status;
    }

    public void setTermName(String termName) {
	_termName = termName;
    }

    public void setTermTicket(String termTicket) {
	_termTicket = termTicket;
    }
}
