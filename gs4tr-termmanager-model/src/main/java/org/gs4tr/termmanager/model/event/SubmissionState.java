package org.gs4tr.termmanager.model.event;

import java.util.Date;

public class SubmissionState {

    private long _canceledCount;

    private long _completedCount;

    private Date _dateModified;

    private long _inFinalReviewCount;

    private long _inTranslationCount;

    private long _totalCount;

    public SubmissionState() {
	_inTranslationCount = 0;
	_canceledCount = 0;
	_completedCount = 0;
	_inFinalReviewCount = 0;
	_totalCount = 0;
	_dateModified = new Date();
    }

    public void decrementCompletedCount() {
	long count = getCompletedCount();
	setCompletedCount(count - 1);
	setDateModified(new Date());
    }

    public void decrementInFinalReviewCount() {
	long count = getInFinalReviewCount();
	setInFinalReviewCount(count - 1);
	setDateModified(new Date());
    }

    public void decrementInTranslationCount() {
	long count = getInTranslationCount();
	setInTranslationCount(count - 1);
	setDateModified(new Date());
    }

    public long getCanceledCount() {
	return _canceledCount;
    }

    public long getCompletedCount() {
	return _completedCount;
    }

    public Date getDateModified() {
	return _dateModified;
    }

    public long getInFinalReviewCount() {
	return _inFinalReviewCount;
    }

    public long getInTranslationCount() {
	return _inTranslationCount;
    }

    public long getTotalCount() {
	return _totalCount;
    }

    public void incrementCanceledCount() {
	setCanceledCount(getCanceledCount() + 1);
	setDateModified(new Date());
    }

    public void incrementCompletedCount() {
	setCompletedCount(getCompletedCount() + 1);
	setDateModified(new Date());
    }

    public void incrementInFinalReviewCount() {
	setInFinalReviewCount(getInFinalReviewCount() + 1);
	setDateModified(new Date());
    }

    public void incrementInTranslationCount() {
	setInTranslationCount(getInTranslationCount() + 1);
	setDateModified(new Date());
    }

    public void incrementTotalCount() {
	setTotalCount(getTotalCount() + 1);
	setDateModified(new Date());
    }

    public void setCanceledCount(long canceledCount) {
	_canceledCount = canceledCount;
    }

    public void setCompletedCount(long completedCount) {
	_completedCount = completedCount;
    }

    public void setDateModified(Date dateModified) {
	_dateModified = dateModified;
    }

    public void setInFinalReviewCount(long inFinalReviewCount) {
	_inFinalReviewCount = inFinalReviewCount;
    }

    public void setInTranslationCount(long inTranslationCount) {
	_inTranslationCount = inTranslationCount;
    }

    public void setTotalCount(long totalCount) {
	_totalCount = totalCount;
    }
}
