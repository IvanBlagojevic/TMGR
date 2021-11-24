package org.gs4tr.termmanager.model;

import java.util.Date;

public class ProjectUserDetailIO {

    private Long _activeSubmissionCount;

    private Long _completedSubmissionCount;

    private Date _dateModified;

    private Long _projectId;

    private Long _userId;

    public ProjectUserDetailIO() {
    }

    public ProjectUserDetailIO(long userId) {
	_userId = userId;
    }

    public Long getActiveSubmissionCount() {
	return _activeSubmissionCount;
    }

    public Long getCompletedSubmissionCount() {
	return _completedSubmissionCount;
    }

    public Date getDateModified() {
	return _dateModified;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public Long getUserId() {
	return _userId;
    }

    public void setActiveSubmissionCount(long activeSubmissionCount) {
	_activeSubmissionCount = activeSubmissionCount;
    }

    public void setCompletedSubmissionCount(long completedSubmissionCount) {
	_completedSubmissionCount = completedSubmissionCount;
    }

    public void setDateModified(Date dateModified) {
	_dateModified = dateModified;
    }

    public void setProjectId(long projectId) {
	_projectId = projectId;
    }

    public void setUserId(long userId) {
	_userId = userId;
    }
}
