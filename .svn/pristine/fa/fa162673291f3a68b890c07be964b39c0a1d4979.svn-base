package org.gs4tr.termmanager.model.dto;

import java.util.Date;

public class ProjectDetailCountsIO {

    private Long _activeSubmissionCount;

    private Long _approvedTermCount;

    private Long _completedSubmissionCount;

    private Date _dateModified;

    private Long _forbiddenTermCount;

    private Long _onHoldTermCount;

    private Long _pendingTermCount;

    private Long _projectId;

    private Long _termEntryCount;

    private Long _termInSubmissionCount;

    private Long _totalCount;

    public ProjectDetailCountsIO() {
    }

    public ProjectDetailCountsIO(long projectId) {
	_projectId = projectId;
	_approvedTermCount = 0L;
	_forbiddenTermCount = 0L;
	_onHoldTermCount = 0L;
	_pendingTermCount = 0L;
	_termEntryCount = 0L;
	_termInSubmissionCount = 0L;
	_totalCount = 0L;
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

    public Long getOnHoldTermCount() {
	return _onHoldTermCount;
    }

    public Long getPendingTermCount() {
	return _pendingTermCount;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public Long getTermEntryCount() {
	return _termEntryCount;
    }

    public Long getTermInSubmissionCount() {
	return _termInSubmissionCount;
    }

    public Long getTotalCount() {
	return _totalCount;
    }

    public void incrementApprovedCount(long approvedCount) {
	_approvedTermCount += approvedCount;
    }

    public void incrementForbiddenCount(long forbiddenCount) {
	_forbiddenTermCount += forbiddenCount;
    }

    public void incrementOnHoldCount(long onHoldTermCount) {
	_onHoldTermCount += onHoldTermCount;
    }

    public void incrementPendingTermCount(long pendingCount) {
	_pendingTermCount += pendingCount;
    }

    public void incrementTermInSubmissionCount(long termInSubmissionCount) {
	_termInSubmissionCount += termInSubmissionCount;
    }

    public void incrementTotalTermCount(long totalCount) {
	_totalCount += totalCount;
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

    public void setOnHoldTermCount(long onHoldTermCount) {
	_onHoldTermCount = onHoldTermCount;
    }

    public void setPendingTermCount(long pendingTermCount) {
	_pendingTermCount = pendingTermCount;
    }

    public void setProjectId(long projectId) {
	_projectId = projectId;
    }

    public void setTermEntryCount(long termEntryCount) {
	_termEntryCount = termEntryCount;
    }

    public void setTermInSubmissionCount(long termInSubmissionCount) {
	_termInSubmissionCount = termInSubmissionCount;
    }

    public void setTotalCount() {
	_totalCount = getApprovedTermCount() + getForbiddenTermCount() + getPendingTermCount() + getOnHoldTermCount()
		+ getTermInSubmissionCount();
    }

    public void setTotalCount(long totalCount) {
	_totalCount = totalCount;
    }

}
