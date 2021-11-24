package org.gs4tr.termmanager.model;

import java.io.Serializable;

import org.gs4tr.foundation.modules.entities.model.Identifiable;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;

public abstract class AbstractItemHolder implements Identifiable<Serializable> {

    private static final long serialVersionUID = 3421188320266329557L;

    private String _assignee;

    private Long _availableTasks;

    private boolean _canceled = false;

    private boolean _inFinalReview = false;

    private boolean _inTranslation;

    private Long _projectId;

    private boolean _sourceInTranslation;

    private ItemStatusType _status;

    private Long _submissionId;

    private String _submitter;

    public String getAssignee() {
	return _assignee;
    }

    public Long getAvailableTasks() {
	return _availableTasks;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public ItemStatusType getStatus() {
	return _status;
    }

    public Long getSubmissionId() {
	return _submissionId;
    }

    public String getSubmitter() {
	return _submitter;
    }

    public boolean isCanceled() {
	return _canceled;
    }

    public boolean isInFinalReview() {
	return _inFinalReview;
    }

    public boolean isInTranslation() {
	return _inTranslation;
    }

    public boolean isSourceInTranslation() {
	return _sourceInTranslation;
    }

    public void setAssignee(String assignee) {
	_assignee = assignee;
    }

    public void setAvailableTasks(Long availableTasks) {
	_availableTasks = availableTasks;
    }

    public void setCanceled(boolean canceled) {
	_canceled = canceled;
    }

    @Override
    public void setIdentifier(Serializable identifier) {
    }

    public void setInFinalReview(boolean inFinalReview) {
	_inFinalReview = inFinalReview;
    }

    public void setInTranslation(boolean inTranslation) {
	_inTranslation = inTranslation;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }

    public void setSourceInTranslation(boolean sourceInTranslation) {
	_sourceInTranslation = sourceInTranslation;
    }

    public void setStatus(ItemStatusType status) {
	_status = status;
    }

    public void setSubmissionId(Long submissionId) {
	_submissionId = submissionId;
    }

    public void setSubmitter(String submitter) {
	_submitter = submitter;
    }
}
