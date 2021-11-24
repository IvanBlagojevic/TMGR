package org.gs4tr.termmanager.service.model.command;

import java.util.List;

public class CancelTranslationCommand {

    private String _commentText;

    private List<Long> _submissionIds;

    private List<String> termIds;

    public String getCommentText() {
	return _commentText;
    }

    public List<Long> getSubmissionIds() {
	return _submissionIds;
    }

    public List<String> getTermIds() {
	return termIds;
    }

    public void setCommentText(String commentText) {
	_commentText = commentText;
    }

    public void setSubmissionId(List<Long> submissionIds) {
	_submissionIds = submissionIds;
    }

    public void setTermIds(List<String> termIds) {
	this.termIds = termIds;
    }
}
