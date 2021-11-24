package org.gs4tr.termmanager.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public abstract class Detail implements Serializable {

    private static final long serialVersionUID = -2311404066972058368L;

    private long _activeSubmissionCount;

    private long _approvedTermCount;

    private long _completedSubmissionCount;

    private Date _dateModified;

    private long _forbiddenTermCount;

    private long _onHoldTermCount;

    private long _pendingApprovalCount;

    private long _termCount;

    private long _termEntryCount;

    private long _termInSubmissionCount;

    public Detail() {
	_activeSubmissionCount = 0;
	_pendingApprovalCount = 0;
	_approvedTermCount = 0;
	_onHoldTermCount = 0;
	_completedSubmissionCount = 0;
	_dateModified = new Date();
	_forbiddenTermCount = 0;
	_termCount = 0;
	_termEntryCount = 0;
	_termInSubmissionCount = 0;
    }

    @Column(name = "ACTIVE_SUBMISSION_COUNT", nullable = false)
    public long getActiveSubmissionCount() {
	return _activeSubmissionCount;
    }

    @Column(name = "APPROVE_TERM_COUNT", nullable = false)
    public long getApprovedTermCount() {
	return _approvedTermCount;
    }

    @Column(name = "COMPLETED_SUBMISSION_COUNT", nullable = false)
    public long getCompletedSubmissionCount() {
	return _completedSubmissionCount;
    }

    @Column(name = "DATE_MODIFIED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDateModified() {
	return _dateModified;
    }

    @Column(name = "FORBIDDEN_TERM_COUNT", nullable = false)
    public long getForbiddenTermCount() {
	return _forbiddenTermCount;
    }

    @Column(name = "ON_HOLD_TERM_COUNT", nullable = false)
    public long getOnHoldTermCount() {
	return _onHoldTermCount;
    }

    @Column(name = "PENDING_APPROVAL_TERM_COUNT", nullable = false)
    public long getPendingApprovalCount() {
	return _pendingApprovalCount;
    }

    @Column(name = "TERM_COUNT", nullable = false)
    public long getTermCount() {
	return _termCount;
    }

    @Column(name = "TERMENTRY_COUNT", nullable = false)
    public long getTermEntryCount() {
	return _termEntryCount;
    }

    @Column(name = "TERM_IN_SUBMISSION_COUNT", nullable = false)
    public long getTermInSubmissionCount() {
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

    public void setOnHoldTermCount(long onHoldTermCount) {
	_onHoldTermCount = onHoldTermCount;
    }

    public void setPendingApprovalCount(long pendingApprovalCount) {
	_pendingApprovalCount = pendingApprovalCount;
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
