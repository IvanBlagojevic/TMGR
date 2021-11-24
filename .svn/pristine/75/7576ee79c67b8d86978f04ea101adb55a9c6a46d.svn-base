package org.gs4tr.termmanager.model.dto;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.dto.converter.DtoTermCommentConverter;
import org.gs4tr.termmanager.model.dto.converter.LanguageConverter;

public class TargetDtoTermTranslation {

    private String _assignee;

    private Boolean _canceled = Boolean.FALSE;

    private DtoTermComment[] _comments;

    private Boolean _commited = Boolean.TRUE;

    private Long _completedDate;

    private Long _creationDate;

    private Boolean _forbidden;

    private Boolean _inTranslation = Boolean.FALSE;

    private Language _language;

    private String _markerId;

    private Long _modificationDate;

    private Boolean _sourceInTranslation = Boolean.FALSE;

    private String _status;

    private Long _submissionDate;

    private String _submitter;

    private String _termName;

    private String _termTicket;

    private String _userModified;

    public TargetDtoTermTranslation(org.gs4tr.termmanager.model.glossary.Term term, boolean showAutoSaved) {
	setCommited(term.getCommited());
	setAssignee(term.getAssignee());
	setCanceled(term.getCanceled());
	setLanguage(LanguageConverter.fromInternalToDto(term.getLanguageId()));
	setMarkerId(term.getUuId());
	setModificationDate(term.getDateModified());
	setCreationDate(term.getDateCreated());
	setCompletedDate(term.getDateCompleted());

	setStatus(term.getStatus());
	setSubmissionDate(term.getDateSubmitted());
	setSubmitter(term.getSubmitter());
	String text = showAutoSaved && !StringUtils.isBlank(term.getTempText()) ? term.getTempText() : term.getName();
	setTermName(text);
	setTermTicket(term.getUuId());
	setComments(DtoTermCommentConverter.fromInternalToDto(term.getComments()));
	boolean termInTranslation = ItemStatusTypeHolder.isTermInTranslation(term);
	boolean inTranslationAsSource = term.getInTranslationAsSource() != null ? term.getInTranslationAsSource()
		: false;

	setInTranslation(termInTranslation || inTranslationAsSource);
	setSourceInTranslation(inTranslationAsSource);
	setUserModified(term.getUserModified());
    }

    public String getAssignee() {
	return _assignee;
    }

    public DtoTermComment[] getComments() {
	return _comments;
    }

    public Long getCompletedDate() {
	return _completedDate;
    }

    public Long getCreationDate() {
	return _creationDate;
    }

    public Boolean getForbidden() {
	return _forbidden;
    }

    public Boolean getInTranslation() {
	return _inTranslation;
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

    public Boolean getSourceInTranslation() {
	return _sourceInTranslation;
    }

    public String getStatus() {
	return _status;
    }

    public Long getSubmissionDate() {
	return _submissionDate;
    }

    public String getSubmitter() {
	return _submitter;
    }

    public String getTermName() {
	return _termName;
    }

    public String getTermTicket() {
	return _termTicket;
    }

    public String getUserModified() {
	return _userModified;
    }

    public Boolean isCanceled() {
	return _canceled;
    }

    public Boolean isCommited() {
	return _commited;
    }

    public void setAssignee(String assignee) {
	_assignee = assignee;
    }

    public void setCanceled(Boolean canceled) {
	_canceled = canceled;
    }

    public void setComments(DtoTermComment[] comments) {
	_comments = comments;
    }

    public void setCommited(Boolean commited) {
	_commited = commited;
    }

    public void setCompletedDate(Long completedDate) {
	_completedDate = completedDate;
    }

    public void setCreationDate(Long creationDate) {
	_creationDate = creationDate;
    }

    public void setForbidden(Boolean forbidden) {
	_forbidden = forbidden;
    }

    public void setInTranslation(Boolean inTranslation) {
	_inTranslation = inTranslation;
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

    public void setSourceInTranslation(Boolean sourceInTranslation) {
	_sourceInTranslation = sourceInTranslation;
    }

    public void setStatus(String status) {
	_status = status;
    }

    public void setSubmissionDate(Long submissionDate) {
	_submissionDate = submissionDate;
    }

    public void setSubmitter(String submitter) {
	_submitter = submitter;
    }

    public void setTermName(String termName) {
	_termName = termName;
    }

    public void setTermTicket(String termTicket) {
	_termTicket = termTicket;
    }

    public void setUserModified(String userModified) {
	_userModified = userModified;
    }
}
