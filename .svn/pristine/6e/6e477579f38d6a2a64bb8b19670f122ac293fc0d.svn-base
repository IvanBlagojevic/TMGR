package org.gs4tr.termmanager.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.gs4tr.foundation.modules.entities.model.Identifiable;

@NamedQueries({
	@NamedQuery(name = "ProjectUserDetail.findByProjectId", query = "select detail "
		+ "from ProjectUserDetail detail " + "where detail.projectDetail.project.projectId = :projectId"),

	@NamedQuery(name = "ProjectUserDetail.incrementalUpdate", query = "update ProjectUserDetail as pud set "
		+ "pud.activeSubmissionCount = (pud.activeSubmissionCount + :activeSubmissionCount), "
		+ "pud.completedSubmissionCount = (pud.completedSubmissionCount + :completedSubmissionCount), "
		+ "pud.dateModified = case when  pud.dateModified < :dateModified then :dateModified else pud.dateModified end "
		+ "where pud.user.userProfileId = :userId "
		+ "and pud.projectDetail = (select pd from ProjectDetail as pd where pd.project.projectId = :projectId)"),

	@NamedQuery(name = "ProjectUserDetail.findByUserAndProjectId", query = "select detail "
		+ "from ProjectUserDetail detail " + "where detail.projectDetail.project.projectId = :projectId "
		+ "and detail.user.userProfileId = :userId"),

	@NamedQuery(name = "ProjectUserDetail.findByUsersAndProjectId", query = "select detail "
		+ "from ProjectUserDetail detail " + "where detail.projectDetail.project.projectId = :projectId "
		+ "and detail.user.userProfileId in (:userIds)") })
@Entity
@Table(name = "PROJECT_USER_DETAIL")
public class ProjectUserDetail implements Serializable, Identifiable<Long> {

    private static final long serialVersionUID = 1256700614069634825L;

    private long _activeSubmissionCount;

    private long _completedSubmissionCount;

    private Date _dateModified;

    private ProjectDetail _projectDetail;

    private Long _projectUserDetailId;

    private long _termEntryCount;

    private TmUserProfile _user;

    public ProjectUserDetail() {
	_dateModified = new Date();
	_activeSubmissionCount = 0;
	_completedSubmissionCount = 0;
	_termEntryCount = 0;
    }

    public ProjectUserDetail(TmUserProfile user, ProjectDetail projectDetail) {
	_projectDetail = projectDetail;
	_user = user;
	_dateModified = new Date();
	_activeSubmissionCount = 0;
	_completedSubmissionCount = 0;
	_termEntryCount = 0;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ProjectUserDetail other = (ProjectUserDetail) obj;
	if (_projectDetail == null) {
	    if (other._projectDetail != null)
		return false;
	} else if (!_projectDetail.getProjectDetailId().equals(other._projectDetail.getProjectDetailId()))
	    return false;
	if (_user == null) {
	    return other._user == null;
	} else
	    return _user.getUserProfileId().equals(other._user.getUserProfileId());
    }

    @Column(name = "ACTIVE_SUBMISSION_COUNT", nullable = false)
    public long getActiveSubmissionCount() {
	return _activeSubmissionCount;
    }

    @Column(name = "COMPLETED_SUBMISSION_COUNT", nullable = false)
    public long getCompletedSubmissionCount() {
	return _completedSubmissionCount;
    }

    @Column(name = "DATE_MODIFIED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDateModified() {
	return _dateModified;
    }

    @Override
    @Transient
    public Long getIdentifier() {
	return getProjectUserDetailId();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_DETAIL_ID", nullable = false, updatable = false)
    public ProjectDetail getProjectDetail() {
	return _projectDetail;
    }

    @Id
    @Column(name = "PROJECT_USER_DETAIL_ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getProjectUserDetailId() {
	return _projectUserDetailId;
    }

    @Column(name = "TERMENTRY_COUNT", nullable = false)
    public long getTermEntryCount() {
	return _termEntryCount;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_PROFILE_ID", nullable = false, updatable = false)
    public TmUserProfile getUser() {
	return _user;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_projectDetail == null) ? 0 : _projectDetail.hashCode());
	return prime * result + ((_user == null) ? 0 : _user.hashCode());
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

    @Override
    public void setIdentifier(Long identifier) {
	setProjectUserDetailId(identifier);
    }

    public void setProjectDetail(ProjectDetail projectDetail) {
	_projectDetail = projectDetail;
    }

    public void setProjectUserDetailId(Long projectUserDetailId) {
	_projectUserDetailId = projectUserDetailId;
    }

    public void setTermEntryCount(long termEntryCount) {
	_termEntryCount = termEntryCount;
    }

    public void setUser(TmUserProfile user) {
	_user = user;
    }
}
