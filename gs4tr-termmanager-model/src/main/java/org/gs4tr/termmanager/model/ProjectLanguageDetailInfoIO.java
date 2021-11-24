package org.gs4tr.termmanager.model;

import java.util.Date;

public class ProjectLanguageDetailInfoIO {

    private Long _activeSubmissionCount;

    private Long _approvedTermCount;

    private Long _completedSubmissionCount;

    private Date _dateModified;

    private Long _forbiddenTermCount;

    private String _languageId;

    private Long _onHoldTermCount;

    private Long _pendingTermCount;

    private Long _projectId;

    private Long _termCount;

    private Long _termEntryCount;

    private Long _termInSubmissionCount;

    public ProjectLanguageDetailInfoIO() {
    }

    public ProjectLanguageDetailInfoIO(String languageId) {
	_languageId = languageId;
    }

    public Long getActiveSubmissionCount() {
	return _activeSubmissionCount;
    }

    public Long getApprovedTermCount() {
	return _approvedTermCount;
    }

    public Long getCompletedSubmissionCount() {
	return _completedSubmissionCount;
    }

    public Date getDateModified() {
	return _dateModified;
    }

    public Long getForbiddenTermCount() {
	return _forbiddenTermCount;
    }

    public String getLanguageId() {
	return _languageId;
    }

    public Long getOnHoldTermCount() {
	return _onHoldTermCount;
    }

    public Long getPendingTermCount() {
	return _pendingTermCount;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public Long getTermCount() {
	return _termCount;
    }

    public Long getTermEntryCount() {
	return _termEntryCount;
    }

    public Long getTermInSubmissionCount() {
	return _termInSubmissionCount;
    }

    public void setActiveSubmissionCount(long activeSubmissionCount) {
	_activeSubmissionCount = activeSubmissionCount;
    }

    public void setApprovedTermCount(long approvedTermCount) {
	_approvedTermCount = approvedTermCount;
    }

    public void setCompletedSubmissionCount(long completedSubmissionCount) {
	_completedSubmissionCount = completedSubmissionCount;
    }

    public void setDateModified(Date dateModified) {
	_dateModified = dateModified;
    }

    public void setForbiddenTermCount(long forbiddenTermCount) {
	_forbiddenTermCount = forbiddenTermCount;
    }

    public void setLanguageId(String languageId) {
	_languageId = languageId;
    }

    public void setOnHoldTermCount(long onHoldTermCount) {
	_onHoldTermCount = onHoldTermCount;
    }

    public void setPendingTermCount(long pendingTermCount) {
	_pendingTermCount = pendingTermCount;
    }

    public void setProjectId(long projectId) {
	_projectId = projectId;
    }

    public void setTermCount(long termCount) {
	_termCount = termCount;
    }

    public void setTermEntryCount(long termEntryCount) {
	_termEntryCount = termEntryCount;
    }

    public void setTermInSubmissionCount(long termInSubmissionCount) {
	_termInSubmissionCount = termInSubmissionCount;
    }

}
