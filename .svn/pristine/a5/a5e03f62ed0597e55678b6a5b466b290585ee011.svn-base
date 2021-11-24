package org.gs4tr.termmanager.model.glossary.backup.submission;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.gs4tr.termmanager.model.glossary.backup.DbBaseTerm;

@Entity
@Table(name = "SUBMISSION_TERM")
public class DbSubmissionTerm extends DbBaseTerm implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8912372457830476517L;

    private String _assignee;

    private Boolean _canceled;

    private Set<DbComment> _comments;

    private Boolean _commited = Boolean.TRUE;

    private Date _dateCompleted;

    private Date _dateSubmitted;

    private Set<DbSubmissionTermDescription> _descriptions;

    // link between term and term in submission
    private String _parentUuId;

    private DbPriority _priority;

    private Boolean _reviewRequired;

    private Long _submissionId;

    private String _submissionName;

    private String _submitter;

    private String _tempText;

    public DbSubmissionTerm() {
	super();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (!super.equals(obj))
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	DbSubmissionTerm other = (DbSubmissionTerm) obj;
	if (_submissionId == null) {
	    if (other._submissionId != null)
		return false;
	} else if (!_submissionId.equals(other._submissionId))
	    return false;
	return true;
    }

    @Column(name = "ASSIGNEE", nullable = true, length = 128)
    public String getAssignee() {
	return _assignee;
    }

    @Column(name = "CANCELED", nullable = false)
    public Boolean getCanceled() {
	return _canceled;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "TERM_ID")
    public Set<DbComment> getComments() {
	return _comments;
    }

    @Column(name = "COMMITED", nullable = false)
    public Boolean getCommited() {
	return _commited;
    }

    @Column(name = "DATE_COMPLETED", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDateCompleted() {
	return _dateCompleted;
    }

    @Column(name = "DATE_SUBMITTED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDateSubmitted() {
	return _dateSubmitted;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "TERM_ID")
    public Set<DbSubmissionTermDescription> getDescriptions() {
	return _descriptions;
    }

    @Column(name = "PARENT_ID", nullable = false, length = 128)
    public String getParentUuId() {
	return _parentUuId;
    }

    @Embedded
    public DbPriority getPriority() {
	return _priority;
    }

    @Column(name = "REVIEW_REQUIRED", nullable = false)
    public Boolean getReviewRequired() {
	return _reviewRequired;
    }

    @Column(name = "SUBMISSION_ID", length = 10, nullable = true, updatable = true)
    public Long getSubmissionId() {
	return _submissionId;
    }

    @Column(name = "SUBMISSION_NAME", nullable = true, length = 128)
    public String getSubmissionName() {
	return _submissionName;
    }

    @Column(name = "SUBMITTER", nullable = true, length = 128)
    public String getSubmitter() {
	return _submitter;
    }

    @Column(name = "TEMP_TEXT")
    @Lob
    public String getTempText() {
	return _tempText;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	result = prime * result + ((_submissionId == null) ? 0 : _submissionId.hashCode());
	return result;
    }

    public void setAssignee(String assignee) {
	_assignee = assignee;
    }

    public void setCanceled(Boolean canceled) {
	_canceled = canceled;
    }

    public void setComments(Set<DbComment> comments) {
	_comments = comments;
    }

    public void setCommited(Boolean commited) {
	_commited = commited;
    }

    public void setDateCompleted(Date dateCompleted) {
	_dateCompleted = dateCompleted;
    }

    public void setDateSubmitted(Date dateSubmitted) {
	_dateSubmitted = dateSubmitted;
    }

    public void setDescriptions(Set<DbSubmissionTermDescription> descriptions) {
	_descriptions = descriptions;
    }

    public void setParentUuId(String parentUuId) {
	_parentUuId = parentUuId;
    }

    public void setPriority(DbPriority priority) {
	_priority = priority;
    }

    public void setReviewRequired(Boolean reviewRequired) {
	_reviewRequired = reviewRequired;
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

    public void setTempText(String tempText) {
	_tempText = tempText;
    }

    @Override
    public String toString() {
	return "DbSubmissionTerm [_assignee=" + _assignee + ", _canceled=" + _canceled + ", _comments=" + _comments
		+ ", _commited=" + _commited + ", _dateCompleted=" + _dateCompleted + ", _dateSubmitted="
		+ _dateSubmitted + ", _descriptions=" + _descriptions + ", _parentUuId=" + _parentUuId + ", _priority="
		+ _priority + ", _reviewRequired=" + _reviewRequired + ", _submissionId=" + _submissionId
		+ ", _submissionName=" + _submissionName + ", _submitter=" + _submitter + ", _tempText=" + _tempText
		+ ", toString()=" + super.toString() + "]";
    }
}
