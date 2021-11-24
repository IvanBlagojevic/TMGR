package org.gs4tr.termmanager.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.gs4tr.foundation.modules.entities.model.Identifiable;

@Entity
@Table(name = "PROJECT_LANGUAGE_USER_DETAIL")
public class ProjectLanguageUserDetail implements Serializable, Identifiable<Long> {

    private static final long serialVersionUID = 2743988381452267630L;

    private long _activeSubmissionCount;

    private long _completedSubmissionCount;

    private ProjectLanguageDetail _languageDetail;

    private Long _projectLanguageUserDetailId;

    private TmUserProfile _user;

    public ProjectLanguageUserDetail() {
	_activeSubmissionCount = 0;
	_completedSubmissionCount = 0;
    }

    public ProjectLanguageUserDetail(ProjectLanguageDetail languageDetail, TmUserProfile user) {
	_activeSubmissionCount = 0;
	_completedSubmissionCount = 0;
	_languageDetail = languageDetail;
	_user = user;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ProjectLanguageUserDetail other = (ProjectLanguageUserDetail) obj;
	if (_languageDetail == null) {
	    if (other._languageDetail != null)
		return false;
	} else if (!_languageDetail.equals(other._languageDetail))
	    return false;
	if (_user == null) {
	    return other._user == null;
	} else
	    return _user.equals(other._user);
    }

    @Column(name = "ACTIVE_SUBMISSION_COUNT", nullable = false)
    public long getActiveSubmissionCount() {
	return _activeSubmissionCount;
    }

    @Column(name = "COMPLETED_SUBMISSION_COUNT", nullable = false)
    public long getCompletedSubmissionCount() {
	return _completedSubmissionCount;
    }

    @Transient
    @Override
    public Long getIdentifier() {
	return getProjectLanguageUserDetailId();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_LANGUAGE_DETAIL_ID", nullable = false, updatable = false)
    public ProjectLanguageDetail getLanguageDetail() {
	return _languageDetail;
    }

    @Id
    @Column(name = "PROJECT_LANGUAGE_USER_DETAIL_ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getProjectLanguageUserDetailId() {
	return _projectLanguageUserDetailId;
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
	result = prime * result + ((_languageDetail == null) ? 0 : _languageDetail.hashCode());
	return prime * result + ((_user == null) ? 0 : _user.hashCode());
    }

    public void setActiveSubmissionCount(long activeSubmissionCount) {
	_activeSubmissionCount = activeSubmissionCount;
    }

    public void setCompletedSubmissionCount(long completedSubmissionCount) {
	_completedSubmissionCount = completedSubmissionCount;
    }

    @Override
    public void setIdentifier(Long id) {
	setProjectLanguageUserDetailId(id);
    }

    public void setLanguageDetail(ProjectLanguageDetail languageDetail) {
	_languageDetail = languageDetail;
    }

    public void setProjectLanguageUserDetailId(Long projectLanguageUserDetailId) {
	_projectLanguageUserDetailId = projectLanguageUserDetailId;
    }

    public void setUser(TmUserProfile user) {
	_user = user;
    }
}
