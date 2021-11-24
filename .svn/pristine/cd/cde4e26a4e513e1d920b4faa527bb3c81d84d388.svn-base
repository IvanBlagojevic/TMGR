package org.gs4tr.termmanager.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.Identifiable;

@NamedQueries({

	@NamedQuery(name = "ProjectDetail.incrementUpdateProjectDetail", query = "update ProjectDetail as pd set "
		+ "pd.approvedTermCount = (pd.approvedTermCount + :approvedTermCount), "
		+ "pd.forbiddenTermCount = (pd.forbiddenTermCount + :forbiddenTermCount), "
		+ "pd.pendingApprovalCount = (pd.pendingApprovalCount + :pendingApprovalCount), "
		+ "pd.onHoldTermCount = (pd.onHoldTermCount + :onHoldTermCount), "
		+ "pd.termCount = (pd.termCount + :termCount), "
		+ "pd.termEntryCount = (pd.termEntryCount + :termEntryCount), "
		+ "pd.activeSubmissionCount = (pd.activeSubmissionCount + :activeSubmissionCount), "
		+ "pd.completedSubmissionCount = (pd.completedSubmissionCount + :completedSubmissionCount), "
		+ "pd.termInSubmissionCount = (pd.termInSubmissionCount + :termInSubmissionCount), "
		+ "pd.dateModified = case when  pd.dateModified < :dateModified then :dateModified else pd.dateModified end "
		+ "where pd.project.projectId = :projectId"),

	@NamedQuery(name = "ProjectDetail.updateProjectDetail", query = "update ProjectDetail as pd set "
		+ "pd.approvedTermCount =  :approvedTermCount, " + "pd.forbiddenTermCount = :forbiddenTermCount, "
		+ "pd.pendingApprovalCount =  :pendingApprovalCount, " + "pd.onHoldTermCount =  :onHoldTermCount, "
		+ "pd.termCount =  :termCount, " + "pd.termEntryCount =  :termEntryCount, "
		+ "pd.activeSubmissionCount =  :activeSubmissionCount, "
		+ "pd.completedSubmissionCount = :completedSubmissionCount, "
		+ "pd.termInSubmissionCount =  :termInSubmissionCount, "
		+ "pd.dateModified = case when  pd.dateModified < :dateModified then :dateModified else pd.dateModified end "
		+ "where pd.project.projectId = :projectId"),

	@NamedQuery(name = "ProjectDetail.findByProjectId", query = "select detail " + "from ProjectDetail detail "
		+ "where detail.project.projectId = :projectId"),

	@NamedQuery(name = "ProjectDetail.updateDateModifiedByProjectId", query = "update ProjectDetail detail "
		+ "set detail.dateModified = case when detail.dateModified < :dateModified then :dateModified else detail.dateModified end "
		+ "where detail.project.projectId = :projectId") })
@Entity
@Table(name = "PROJECT_DETAIL")
public class ProjectDetail extends Detail implements Serializable, Identifiable<Long> {

    private static final long serialVersionUID = -1092084875325876495L;

    private long _languageCount;

    private Set<ProjectLanguageDetail> _languageDetails;

    private TmProject _project;

    private Long _projectDetailId;

    private Set<ProjectUserDetail> _userDetails;

    public ProjectDetail() {
	super();
	_languageCount = 0;
    }

    public ProjectDetail(TmProject project) {
	super();
	_languageCount = 0;
	_project = project;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ProjectDetail other = (ProjectDetail) obj;
	if (_project == null) {
	    return other._project == null;
	} else
	    return _project.equals(other._project);
    }

    @Override
    @Transient
    public Long getIdentifier() {
	return getProjectDetailId();
    }

    @Column(name = "LANGUAGE_COUNT", nullable = false)
    public long getLanguageCount() {
	return _languageCount;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "projectDetail")
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    public Set<ProjectLanguageDetail> getLanguageDetails() {
	return _languageDetails;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID", nullable = false, updatable = false)
    public TmProject getProject() {
	return _project;
    }

    @Id
    @Column(name = "PROJECT_DETAIL_ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getProjectDetailId() {
	return _projectDetailId;
    }

    @Transient
    public ProjectUserDetail getProjectUserDetail(Long userId) {
	Set<ProjectUserDetail> userDetails = getUserDetails();
	if (CollectionUtils.isNotEmpty(userDetails)) {
	    for (ProjectUserDetail userDetail : userDetails) {
		if (userDetail.getUser().getUserProfileId().equals(userId)) {
		    return userDetail;
		}
	    }
	}
	return null;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "projectDetail")
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    public Set<ProjectUserDetail> getUserDetails() {
	return _userDetails;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	return prime * result + ((_project == null) ? 0 : _project.hashCode());
    }

    @Override
    public void setIdentifier(Long identifier) {
	setProjectDetailId(identifier);
    }

    public void setLanguageCount(long languageCount) {
	_languageCount = languageCount;
    }

    public void setLanguageDetails(Set<ProjectLanguageDetail> languageDetails) {
	_languageDetails = languageDetails;
    }

    public void setProject(TmProject project) {
	_project = project;
    }

    public void setProjectDetailId(Long projectDetailId) {
	_projectDetailId = projectDetailId;
    }

    public void setUserDetails(Set<ProjectUserDetail> userDetails) {
	_userDetails = userDetails;
    }
}
