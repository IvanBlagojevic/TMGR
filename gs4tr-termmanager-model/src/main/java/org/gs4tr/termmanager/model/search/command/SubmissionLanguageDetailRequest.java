package org.gs4tr.termmanager.model.search.command;

import java.util.Set;

public class SubmissionLanguageDetailRequest {
    private Set<String> _languageIds;

    private Long _submissionId;

    private boolean _submitterView = false;

    public Set<String> getLanguageIds() {
	return _languageIds;
    }

    public Long getSubmissionId() {
	return _submissionId;
    }

    public boolean isSubmitterView() {
	return _submitterView;
    }

    public void setLanguageIds(Set<String> languageIds) {
	_languageIds = languageIds;
    }

    public void setSubmissionId(Long submissionId) {
	_submissionId = submissionId;
    }

    public void setSubmitterView(boolean submitterView) {
	_submitterView = submitterView;
    }

}
