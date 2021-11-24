package org.gs4tr.termmanager.model.dto;

import java.io.Serializable;

public class DtoSubmissionDetailView implements Serializable {

    private static final long serialVersionUID = 6246315937852947026L;

    private String _assignees;

    private Long _availableTasks;

    private boolean _canceled = false;

    private long _dateCompleted;

    private long _dateModified;

    private long _dateSubmitted;

    private String _markerId;

    private String _projectName;

    private String _projectTicket;

    private String _sourceLanguageId;

    private String _status;

    private long _submissionId;

    private String _submissionName;

    private String _submitter;

    private String _targetLanguageIds;

    private long _termEntryCount;

    private String _ticket;

    public String getAssignees() {
	return _assignees;
    }

    public Long getAvailableTasks() {
	return _availableTasks;
    }

    public long getDateCompleted() {
	return _dateCompleted;
    }

    public long getDateModified() {
	return _dateModified;
    }

    public long getDateSubmitted() {
	return _dateSubmitted;
    }

    public String getMarkerId() {
	return _markerId;
    }

    public String getProjectName() {
	return _projectName;
    }

    public String getProjectTicket() {
	return _projectTicket;
    }

    public String getSourceLanguageId() {
	return _sourceLanguageId;
    }

    public String getStatus() {
	return _status;
    }

    public long getSubmissionId() {
	return _submissionId;
    }

    public String getSubmissionName() {
	return _submissionName;
    }

    public String getSubmitter() {
	return _submitter;
    }

    public String getTargetLanguageIds() {
	return _targetLanguageIds;
    }

    public long getTermEntryCount() {
	return _termEntryCount;
    }

    public String getTicket() {
	return _ticket;
    }

    public boolean isCanceled() {
	return _canceled;
    }

    public void setAssignees(String assignees) {
	_assignees = assignees;
    }

    public void setAvailableTasks(Long availableTasks) {
	_availableTasks = availableTasks;
    }

    public void setCanceled(boolean canceled) {
	_canceled = canceled;
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

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setSourceLanguageId(String sourceLanguageId) {
	_sourceLanguageId = sourceLanguageId;
    }

    public void setStatus(String status) {
	_status = status;
    }

    public void setSubmissionId(long submissionId) {
	_submissionId = submissionId;
    }

    public void setSubmissionName(String submissionName) {
	_submissionName = submissionName;
    }

    public void setSubmitter(String submitter) {
	_submitter = submitter;
    }

    public void setTargetLanguageIds(String targetLanguageIds) {
	_targetLanguageIds = targetLanguageIds;
    }

    public void setTermEntryCount(long termEntryCount) {
	_termEntryCount = termEntryCount;
    }

    public void setTicket(String ticket) {
	_ticket = ticket;
    }
}
