package org.gs4tr.termmanager.model.view;

import org.gs4tr.termmanager.model.AbstractItemHolder;

public class SubmissionDetailView extends AbstractItemHolder {

    private static final long serialVersionUID = -8618303345476248760L;

    private long _dateCompleted;

    private long _dateModified;

    private long _dateSubmitted;

    private String _markerId;

    private String _projectName;

    private String _sourceLanguageId;

    private Long _submissionId;

    private String _submissionName;

    private String _targetLanguageIds;

    private long _termEntryCount;

    public long getDateCompleted() {
	return _dateCompleted;
    }

    public long getDateModified() {
	return _dateModified;
    }

    public long getDateSubmitted() {
	return _dateSubmitted;
    }

    @Override
    public Long getIdentifier() {
	return getSubmissionId();
    }

    public String getMarkerId() {
	return _markerId;
    }

    public String getProjectName() {
	return _projectName;
    }

    public String getSourceLanguageId() {
	return _sourceLanguageId;
    }

    public Long getSubmissionId() {
	return _submissionId;
    }

    public String getSubmissionName() {
	return _submissionName;
    }

    public String getTargetLanguageIds() {
	return _targetLanguageIds;
    }

    public long getTermEntryCount() {
	return _termEntryCount;
    }

    public void setDateCompleted(long dateCompleted) {
	_dateCompleted = dateCompleted;
    }

    public void setDateModified(long dateModified) {
	_dateModified = dateModified;
    }

    public void setDateSubmitted(long dateSubmitted) {
	_dateSubmitted = dateSubmitted;
    }

    public void setMarkerId(String markerId) {
	_markerId = markerId;
    }

    public void setProjectName(String projectName) {
	_projectName = projectName;
    }

    public void setSourceLanguageId(String sourceLanguageId) {
	_sourceLanguageId = sourceLanguageId;
    }

    public void setSubmissionId(Long submissionId) {
	_submissionId = submissionId;
    }

    public void setSubmissionName(String submissionName) {
	_submissionName = submissionName;
    }

    public void setTargetLanguageIds(String targetLanguageIds) {
	_targetLanguageIds = targetLanguageIds;
    }

    public void setTermEntryCount(long termEntryCount) {
	_termEntryCount = termEntryCount;
    }
}
