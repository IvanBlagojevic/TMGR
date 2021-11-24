package org.gs4tr.termmanager.model.event;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.mutable.MutableInt;

public class SubmissionDetailInfo {

    public static class SubmissionStateWrapper {
	private MutableInt _canceledCount;
	private MutableInt _completedCount;
	private MutableInt _inFinalReviewCount;
	private MutableInt _inTranslationCount;
	private MutableInt _totalCount;

	public SubmissionStateWrapper() {
	    _inTranslationCount = new MutableInt(0);
	    _canceledCount = new MutableInt(0);
	    _completedCount = new MutableInt(0);
	    _inFinalReviewCount = new MutableInt(0);
	    _totalCount = new MutableInt(0);
	}

	public MutableInt getCanceledCount() {
	    return _canceledCount;
	}

	public MutableInt getCompletedCount() {
	    return _completedCount;
	}

	public MutableInt getInFinalReviewCount() {
	    return _inFinalReviewCount;
	}

	public MutableInt getInTranslationCount() {
	    return _inTranslationCount;
	}

	public MutableInt getTotalCount() {
	    return _totalCount;
	}
    }

    private String _sourceLanguageId;

    private Long _submissionId;

    private Map<String, SubmissionStateWrapper> _submissionStatesWrapper;

    private MutableInt _termEntryCount;

    public SubmissionDetailInfo(Long submissionId, String sourceLanguageId) {
	_submissionId = submissionId;
	_sourceLanguageId = sourceLanguageId;
	_submissionStatesWrapper = new HashMap<String, SubmissionStateWrapper>();
	_termEntryCount = new MutableInt(0);
    }

    public void decrementCompletedCount(String languageId) {
	SubmissionStateWrapper stateWrapper = initSubmissionStateWrapper(languageId);
	MutableInt completedCount = stateWrapper.getCompletedCount();
	completedCount.decrement();
    }

    public void decrementInFinalReviewCount(String languageId) {
	SubmissionStateWrapper stateWrapper = initSubmissionStateWrapper(languageId);
	MutableInt inFinalReviewCount = stateWrapper.getInFinalReviewCount();
	inFinalReviewCount.decrement();
    }

    public void decrementInTranslationCount(String languageId) {
	SubmissionStateWrapper stateWrapper = initSubmissionStateWrapper(languageId);
	MutableInt inTranslationCount = stateWrapper.getInTranslationCount();
	inTranslationCount.decrement();
    }

    public String getSourceLanguageId() {
	return _sourceLanguageId;
    }

    public Long getSubmissionId() {
	return _submissionId;
    }

    public Map<String, SubmissionStateWrapper> getSubmissionStatesWrapper() {
	return _submissionStatesWrapper;
    }

    public MutableInt getTermEntryCount() {
	return _termEntryCount;
    }

    public void incrementCanceledCount(String languageId) {
	SubmissionStateWrapper stateWrapper = initSubmissionStateWrapper(languageId);
	MutableInt canceledCount = stateWrapper.getCanceledCount();
	canceledCount.increment();
    }

    public void incrementCompletedCount(String languageId) {
	SubmissionStateWrapper stateWrapper = initSubmissionStateWrapper(languageId);
	MutableInt completedCount = stateWrapper.getCompletedCount();
	completedCount.increment();
    }

    public void incrementInFinalReviewCount(String languageId) {
	SubmissionStateWrapper stateWrapper = initSubmissionStateWrapper(languageId);
	MutableInt inFinalReviewCount = stateWrapper.getInFinalReviewCount();
	inFinalReviewCount.increment();
    }

    public void incrementInTranslationCount(String langugeId) {
	SubmissionStateWrapper stateWrapper = initSubmissionStateWrapper(langugeId);
	MutableInt inTranslationCount = stateWrapper.getInTranslationCount();
	inTranslationCount.increment();
    }

    public void incrementTermEntryCount() {
	getTermEntryCount().increment();
    }

    public void incrementTotalCount(String languageId) {
	SubmissionStateWrapper stateWrapper = initSubmissionStateWrapper(languageId);
	MutableInt totalCount = stateWrapper.getTotalCount();
	totalCount.increment();
    }

    public SubmissionStateWrapper initSubmissionStateWrapper(String langugeId) {
	SubmissionStateWrapper stateWrapper = getSubmissionStatesWrapper().get(langugeId);
	if (stateWrapper == null) {
	    stateWrapper = new SubmissionStateWrapper();
	    getSubmissionStatesWrapper().put(langugeId, stateWrapper);
	}
	return stateWrapper;
    }
}
