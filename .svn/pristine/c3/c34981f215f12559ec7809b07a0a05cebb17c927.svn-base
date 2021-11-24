package org.gs4tr.termmanager.model.glossary.backup;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.gs4tr.termmanager.model.glossary.Action;

@MappedSuperclass
public class DbBaseTermEntry implements Serializable {

    private static final long serialVersionUID = 2307214914895136398L;

    private String _action = Action.NOT_AVAILABLE.name();

    private Date _dateCreated;

    private Date _dateModified;

    private Long _id;

    private Long _projectId;

    private String _projectName;

    private Integer _revisionId;

    private String _shortCode;

    private Long _submissionId;

    private String _submissionName;

    private String _submitter;

    private String _userCreated;

    private String _userModified;

    private String _uuId;

    public DbBaseTermEntry() {

    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	DbBaseTermEntry other = (DbBaseTermEntry) obj;
	if (_projectId == null) {
	    if (other._projectId != null)
		return false;
	} else if (!_projectId.equals(other._projectId))
	    return false;
	if (_submissionId == null) {
	    return other._submissionId == null;
	} else
	    return _submissionId.equals(other._submissionId);
    }

    @Column(name = "ACTION", nullable = false, length = 50)
    public String getAction() {
	return _action;
    }

    @Column(name = "DATE_CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDateCreated() {
	return _dateCreated;
    }

    @Column(name = "DATE_MODIFIED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDateModified() {
	return _dateModified;
    }

    @Id
    @Column(name = "TERMENTRY_ID", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
	return _id;
    }

    @Column(name = "PROJECT_ID", length = 10, nullable = false)
    public Long getProjectId() {
	return _projectId;
    }

    @Column(name = "PROJECT_NAME", length = 128)
    public String getProjectName() {
	return _projectName;
    }

    @Column(name = "REVISION_ID", length = 10, nullable = false)
    public Integer getRevisionId() {
	return _revisionId;
    }

    @Column(name = "SHORT_CODE", length = 20)
    public String getShortCode() {
	return _shortCode;
    }

    @Column(name = "SUBMISSION_ID", length = 10)
    public Long getSubmissionId() {
	return _submissionId;
    }

    @Column(name = "SUBMISSION_NAME", length = 128)
    public String getSubmissionName() {
	return _submissionName;
    }

    @Column(name = "SUBMITTER", length = 128)
    public String getSubmitter() {
	return _submitter;
    }

    @Column(name = "USER_CREATED", length = 128)
    public String getUserCreated() {
	return _userCreated;
    }

    @Column(name = "USER_MODIFIED", length = 128)
    public String getUserModified() {
	return _userModified;
    }

    @Column(name = "UUID", nullable = false, length = 128)
    public String getUuId() {
	return _uuId;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_projectId == null) ? 0 : _projectId.hashCode());
	return prime * result + ((_submissionId == null) ? 0 : _submissionId.hashCode());
    }

    public void setAction(String action) {
	_action = action;
    }

    public void setDateCreated(Date dateCreated) {
	_dateCreated = dateCreated;
    }

    public void setDateModified(Date dateModified) {
	_dateModified = dateModified;
    }

    public void setId(Long id) {
	_id = id;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }

    public void setProjectName(String projectName) {
	_projectName = projectName;
    }

    public void setRevisionId(Integer revisionId) {
	_revisionId = revisionId;
    }

    public void setShortCode(String shortCode) {
	_shortCode = shortCode;
    }

    public void setSubmissionId(Long submissionId) {
	_submissionId = submissionId;
    }

    public void setSubmissionName(String submissionName) {
	_submissionName = submissionName;
    }

    public void setSubmitter(String submitter) {
	_submitter = submitter;
    }

    public void setUserCreated(String userCreated) {
	_userCreated = userCreated;
    }

    public void setUserModified(String userModified) {
	_userModified = userModified;
    }

    public void setUuId(String uuId) {
	_uuId = uuId;
    }

    @Override
    public String toString() {
	return "DbBaseTermEntry [_action=" + _action + ", _dateCreated=" + _dateCreated + ", _dateModified="
		+ _dateModified + ", _id=" + _id + ", _projectId=" + _projectId + ", _projectName=" + _projectName
		+ ", _shortCode=" + _shortCode + ", _submissionId=" + _submissionId + ", _submissionName="
		+ _submissionName + ", _submitter=" + _submitter + ", _userCreated=" + _userCreated + ", _userModified="
		+ _userModified + ", _uuId=" + _uuId + "]";
    }
}
