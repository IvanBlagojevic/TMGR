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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.Identifiable;

@NamedQueries({

	@NamedQuery(name = "ProjectLanguageDetail.incrementUpdateProjectLanguageDetail", query = "update ProjectLanguageDetail as pld set "
		+ "pld.approvedTermCount = (pld.approvedTermCount + :approvedTermCount), "
		+ "pld.forbiddenTermCount = (pld.forbiddenTermCount + :forbiddenTermCount), "
		+ "pld.pendingApprovalCount = (pld.pendingApprovalCount + :pendingApprovalCount), "
		+ "pld.onHoldTermCount = (pld.onHoldTermCount + :onHoldTermCount), "
		+ "pld.termCount = (pld.termCount + :termCount), "
		+ "pld.activeSubmissionCount = (pld.activeSubmissionCount + :activeSubmissionCount), "
		+ "pld.completedSubmissionCount = (pld.completedSubmissionCount + :completedSubmissionCount), "
		+ "pld.termInSubmissionCount = (pld.termInSubmissionCount + :termInSubmissionCount), "
		+ "pld.termEntryCount = (pld.termEntryCount + :termEntryCount), "
		+ "pld.dateModified = case when  pld.dateModified < :dateModified then :dateModified else pld.dateModified end "
		+ "where pld.languageId = :languageId "
		+ "and pld.projectDetail = (select pd from ProjectDetail as pd where pd.project.projectId = :projectId)"),

	@NamedQuery(name = "ProjectLanguageDetail.updateProjectLanguageDetail", query = "update ProjectLanguageDetail as pld set "
		+ "pld.approvedTermCount =  :approvedTermCount, " + "pld.forbiddenTermCount = :forbiddenTermCount, "
		+ "pld.pendingApprovalCount = :pendingApprovalCount, " + "pld.onHoldTermCount = :onHoldTermCount, "
		+ "pld.termCount = :termCount, " + "pld.activeSubmissionCount = :activeSubmissionCount, "
		+ "pld.completedSubmissionCount = :completedSubmissionCount, "
		+ "pld.termInSubmissionCount =  :termInSubmissionCount, " + "pld.termEntryCount = :termEntryCount, "
		+ "pld.dateModified = case when  pld.dateModified < :dateModified then :dateModified else pld.dateModified end "
		+ "where pld.languageId = :languageId "
		+ "and pld.projectDetail = (select pd from ProjectDetail as pd where pd.project.projectId = :projectId)"),

	@NamedQuery(name = "ProjectLanguageDetail.findByProjectId", query = "select detail "
		+ "from ProjectLanguageDetail detail " + "where detail.projectDetail.project.projectId = :projectId"),

	@NamedQuery(name = "ProjectLanguageDetail.updateDateModified", query = "update ProjectLanguageDetail pld "
		+ "set pld.dateModified = case when pld.dateModified < :dateModified then :dateModified else pld.dateModified end "
		+ "where pld.languageId in (:languageIds) "
		+ "and pld.projectDetail = (select pd from ProjectDetail as pd where pd.project.projectId = :projectId)"),

	@NamedQuery(name = "ProjectLanguageDetail.findByProjectAndLanguageId", query = "select detail "
		+ "from ProjectLanguageDetail detail " + "where detail.projectDetail.project.projectId = :projectId "
		+ "and detail.languageId = :languageId "),

	@NamedQuery(name = "ProjectLanguageDetail.recodeProjectLanguageDetail", query = "update ProjectLanguageDetail pld set "
		+ "pld.languageId = :languageTo "
		+ "where pld.languageId = :languageFrom and pld.projectDetail = (select pd from ProjectDetail as pd where pd.project.projectId = :projectId)") })
@Entity
@Table(name = "PROJECT_LANGUAGE_DETAIL")
public class ProjectLanguageDetail extends Detail implements Serializable, Identifiable<Long> {

    private static final long serialVersionUID = 3919624027050226566L;

    private Boolean _disabled = Boolean.FALSE;

    private String _languageId;

    private ProjectDetail _projectDetail;

    private Long _projectLanguageDetailId;

    private Set<ProjectLanguageUserDetail> _userDetails;

    public ProjectLanguageDetail() {
	super();
    }

    public ProjectLanguageDetail(String languageId, ProjectDetail projectDetail) {
	super();
	_languageId = languageId;
	_projectDetail = projectDetail;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ProjectLanguageDetail other = (ProjectLanguageDetail) obj;
	if (_languageId == null) {
	    if (other._languageId != null)
		return false;
	} else if (!_languageId.equals(other._languageId))
	    return false;
	if (_projectDetail == null) {
	    return other._projectDetail == null;
	} else
	    return _projectDetail.equals(other._projectDetail);
    }

    @Override
    @Transient
    public Long getIdentifier() {
	return getProjectLanguageDetailId();
    }

    @Column(name = "LANGUAGE_ID", nullable = false, updatable = false, length = 10)
    public String getLanguageId() {
	return _languageId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_DETAIL_ID", nullable = false, updatable = false)
    public ProjectDetail getProjectDetail() {
	return _projectDetail;
    }

    @Id
    @Column(name = "PROJECT_LANGUAGE_DETAIL_ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getProjectLanguageDetailId() {
	return _projectLanguageDetailId;
    }

    @Transient
    public ProjectLanguageUserDetail getProjectUserDetail(Long userId) {
	Set<ProjectLanguageUserDetail> userDetails = getUserDetails();
	if (CollectionUtils.isNotEmpty(userDetails)) {
	    for (ProjectLanguageUserDetail userDetail : userDetails) {
		if (userDetail.getUser().getUserProfileId().equals(userId)) {
		    return userDetail;
		}
	    }
	}
	return null;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "languageDetail")
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    public Set<ProjectLanguageUserDetail> getUserDetails() {
	return _userDetails;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_languageId == null) ? 0 : _languageId.hashCode());
	return prime * result + ((_projectDetail == null) ? 0 : _projectDetail.hashCode());
    }

    @Column(name = "DISABLED", nullable = false, updatable = true)
    public Boolean isDisabled() {
	return _disabled;
    }

    public void setDisabled(Boolean disabled) {
	_disabled = disabled;
    }

    @Override
    public void setIdentifier(Long identifier) {
	setProjectLanguageDetailId(identifier);
    }

    public void setLanguageId(String languageId) {
	_languageId = languageId;
    }

    public void setProjectDetail(ProjectDetail projectDetail) {
	_projectDetail = projectDetail;
    }

    public void setProjectLanguageDetailId(Long projectLanguageDetailId) {
	_projectLanguageDetailId = projectLanguageDetailId;
    }

    public void setUserDetails(Set<ProjectLanguageUserDetail> userDetails) {
	_userDetails = userDetails;
    }
}
