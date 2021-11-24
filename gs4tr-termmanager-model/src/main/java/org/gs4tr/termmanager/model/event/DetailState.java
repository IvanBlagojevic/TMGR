package org.gs4tr.termmanager.model.event;

import java.util.Date;

public class DetailState {

    private long _activeSubmissionCount;

    private long _approvedTermCount;

    private long _completedSubmissionCount;

    private Date _dateModified;

    private long _forbiddenTermCount;

    private long _termCount;

    private long _termEntryCount;

    private long _termInSubmissionCount;

    public DetailState() {
	_activeSubmissionCount = 0;
	_approvedTermCount = 0;
	_completedSubmissionCount = 0;
	_forbiddenTermCount = 0;
	_termInSubmissionCount = 0;
	_termCount = 0;
	_termEntryCount = 0;
	_dateModified = new Date();
    }

    public void decrementActiveSubmissionCount() {
	setDateModified(new Date());
	long count = getActiveSubmissionCount();
	setActiveSubmissionCount(count - 1);
    }

    public void decrementApprovedTermCount() {
	setDateModified(new Date());
	long count = getApprovedTermCount();
	setApprovedTermCount(count - 1);
    }

    public void decrementForbiddenTermCount() {
	setDateModified(new Date());
	long count = getForbiddenTermCount();
	setForbiddenTermCount(count - 1);
    }

    public void decrementTermCount() {
	setDateModified(new Date());
	long count = getTermCount();
	setTermCount(count - 1);
    }

    public void decrementTermEntryCount() {
	setDateModified(new Date());
	long count = getTermEntryCount();
	setTermEntryCount(count - 1);
    }

    public void decrementTermInSubmissionCount() {
	setDateModified(new Date());
	long count = getTermInSubmissionCount();
	setTermInSubmissionCount(count - 1);
    }

    public long getActiveSubmissionCount() {
	return _activeSubmissionCount;
    }

    public long getApprovedTermCount() {
	return _approvedTermCount;
    }

    public long getCompletedSubmissionCount() {
	return _completedSubmissionCount;
    }

    public Date getDateModified() {
	return _dateModified;
    }

    public long getForbiddenTermCount() {
	return _forbiddenTermCount;
    }

    public long getTermCount() {
	return _termCount;
    }

    public long getTermEntryCount() {
	return _termEntryCount;
    }

    public long getTermInSubmissionCount() {
	return _termInSubmissionCount;
    }

    public void incrementActiveSubmissionCount() {
	setActiveSubmissionCount(getActiveSubmissionCount() + 1);
	setDateModified(new Date());
    }

    public void incrementApprovedTermCount() {
	setApprovedTermCount(getApprovedTermCount() + 1);
	setDateModified(new Date());
    }

    public void incrementApprovedTermCount(int count) {
	setApprovedTermCount(getApprovedTermCount() + count);
	setDateModified(new Date());
    }

    public void incrementCompletedSubmissionCount() {
	setCompletedSubmissionCount(getCompletedSubmissionCount() + 1);
	setDateModified(new Date());
    }

    public void incrementForbiddenTermCount() {
	setForbiddenTermCount(getForbiddenTermCount() + 1);
	setDateModified(new Date());
    }

    public void incrementForbiddenTermCount(int count) {
	setForbiddenTermCount(getForbiddenTermCount() + count);
	setDateModified(new Date());
    }

    public void incrementTermCount() {
	setTermCount(getTermCount() + 1);
	setDateModified(new Date());
    }

    public void incrementTermCount(int count) {
	setTermCount(getTermCount() + count);
	setDateModified(new Date());
    }

    public void incrementTermEntryCount() {
	setTermEntryCount(getTermEntryCount() + 1);
	setDateModified(new Date());
    }

    public void incrementTermInSubmissionCount() {
	setTermInSubmissionCount(getTermInSubmissionCount() + 1);
	setDateModified(new Date());
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
