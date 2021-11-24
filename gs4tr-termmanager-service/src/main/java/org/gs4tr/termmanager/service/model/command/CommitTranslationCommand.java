package org.gs4tr.termmanager.service.model.command;

import java.util.List;

public class CommitTranslationCommand extends BaseDashboardCommand {

    private Long _submissionId;

    private String _targetLanguage;

    private List<String> _targetTermIds;

    public Long getSubmissionId() {
	return _submissionId;
    }

    public String getTargetLanguage() {
	return _targetLanguage;
    }

    public List<String> getTargetTermIds() {
	return _targetTermIds;
    }

    public void setSubmissionId(Long submissionId) {
	_submissionId = submissionId;
    }

    public void setTargetLanguage(String targetLanguage) {
	_targetLanguage = targetLanguage;
    }

    public void setTargetTermIds(List<String> targetTermIds) {
	_targetTermIds = targetTermIds;
    }
}
