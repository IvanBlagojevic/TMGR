package org.gs4tr.termmanager.model;

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
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

@NamedQueries({
	@NamedQuery(name = "Statistics.getStatisticsByProjectAndLanguage", query = "select st " + "from Statistics st "
		+ "inner join fetch st.projectUserLanguage as pul "
		+ "where st.projectUserLanguage.project.projectId = :projectId "
		+ "and st.projectUserLanguage.language = :language"),

	@NamedQuery(name = "Statistics.updateStatistics", query = "update Statistics as st set "
		+ "st.addedApproved = (st.addedApproved + :newAddedApproved),"
		+ "st.approved = (st.approved + :newApproved),"
		+ "st.addedPending = (st.addedPending + :newAddedPending)," + "st.demoted = (st.demoted + :newPending),"
		+ "st.addedOnHold = (st.addedOnHold + :newAddedOnHold)," + "st.onHold = (st.onHold + :newOnHold),"
		+ "st.addedBlacklisted = (st.addedBlacklisted + :newAddedBlacklisted),"
		+ "st.blacklisted = (st.blacklisted + :newBlacklisted)," + "st.deleted = (st.deleted + :newDeleted), "
		+ "st.updated = (st.updated + :newUpdated) "
		+ "where st.projectUserLanguage in (select pul from ProjectUserLanguage pul "
		+ "where pul.project.projectId = :projectId and pul.language = :language)"),

	@NamedQuery(name = "Statistics.decrementStatistics", query = "update Statistics as st set "
		+ "st.addedApproved = (st.addedApproved - :newAddedApproved),"
		+ "st.approved = (st.approved - :newApproved),"
		+ "st.addedPending = (st.addedPending - :newAddedPending)," + "st.demoted = (st.demoted - :newPending),"
		+ "st.addedOnHold = (st.addedOnHold - :newAddedOnHold)," + "st.onHold = (st.onHold - :newOnHold),"
		+ "st.addedBlacklisted = (st.addedBlacklisted - :newAddedBlacklisted),"
		+ "st.blacklisted = (st.blacklisted - :newBlacklisted)," + "st.deleted = (st.deleted - :newDeleted), "
		+ "st.updated = (st.updated - :newUpdated) " + "where st.statisticsId = :statisticsId"),

	@NamedQuery(name = "Statistics.getStatisticsByProjectAndLanguages", query = "select st " + "from Statistics st "
		+ "where st.projectUserLanguage.project.projectId = :projectId "
		+ "and st.projectUserLanguage.language in (:languages)"),

	@NamedQuery(name = "Statistics.getStatisticsByUserId", query = "select st " + "from Statistics st "
		+ "join st.projectUserLanguage as pul " + "join pul.project as project " + "join pul.user as user "
		+ "where user.userProfileId = :userId " + "and project.projectInfo.enabled is true "
		+ "and st.reportType = :reportType"),

	@NamedQuery(name = "Statistics.getStatisticsByProjectId", query = "select st " + "from Statistics st "
		+ "inner join fetch st.projectUserLanguage as pul "
		+ "where st.projectUserLanguage.project.projectId = :projectId"),

	@NamedQuery(name = "Statistics.clearUserStatistics", query = "update Statistics st "
		+ "set st.addedApproved = 0, st.addedPending = 0, st.approved = 0, st.deleted = 0,"
		+ " st.demoted = 0, st.updated = 0, st.addedOnHold = 0, st.onHold = 0, st.addedBlacklisted = 0, st.blacklisted = 0"
		+ "where st.statisticsId in (:statisticsIds) ") })
@Entity
@Table(name = "STATISTICS")
public class Statistics {

    private long _addedApproved = 0;

    private long _addedBlacklisted = 0;

    private long _addedOnHold = 0;

    private long _addedPending = 0;

    private long _approved = 0;

    private long _blacklisted = 0;

    private long _deleted = 0;

    private long _demoted = 0;

    private String _languageId;

    private long _onHold = 0;

    private ProjectUserLanguage _projectUserLanguage;

    private String _reportType;

    private Long _statisticsId;

    private long _updated = 0;

    public Statistics() {
    }

    public Statistics(String reportType) {
	_reportType = reportType;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Statistics other = (Statistics) obj;
	if (_projectUserLanguage == null) {
	    if (other._projectUserLanguage != null)
		return false;
	} else if (!_projectUserLanguage.equals(other._projectUserLanguage))
	    return false;
	if (_reportType == null) {
	    return other._reportType == null;
	} else
	    return _reportType.equals(other._reportType);
    }

    @Column(name = "ADDED_APPROVED_COUNT", nullable = false, updatable = true)
    public long getAddedApproved() {
	return _addedApproved;
    }

    @Column(name = "ADDED_BLACKLISTED_COUNT", nullable = false, updatable = true)
    public long getAddedBlacklisted() {
	return _addedBlacklisted;
    }

    @Column(name = "ADDED_ON_HOLD_COUNT", nullable = false, updatable = true)
    public long getAddedOnHold() {
	return _addedOnHold;
    }

    @Column(name = "ADDED_PENDING_COUNT", nullable = false, updatable = true)
    public long getAddedPending() {
	return _addedPending;
    }

    @Column(name = "APPROVED_COUNT", nullable = false, updatable = true)
    public long getApproved() {
	return _approved;
    }

    @Column(name = "BLACKLISTED_COUNT", nullable = false, updatable = true)
    public long getBlacklisted() {
	return _blacklisted;
    }

    @Column(name = "DELETED_COUNT", nullable = false, updatable = true)
    public long getDeleted() {
	return _deleted;
    }

    @Column(name = "DEMOTED_COUNT", nullable = false, updatable = true)
    public long getDemoted() {
	return _demoted;
    }

    @Transient
    public String getLanguageId() {
	return _languageId;
    }

    @Column(name = "ON_HOLD_COUNT", nullable = false, updatable = true)
    public long getOnHold() {
	return _onHold;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_USER_LANGUAGE_ID", nullable = false, updatable = false)
    public ProjectUserLanguage getProjectUserLanguage() {
	return _projectUserLanguage;
    }

    @Column(name = "REPORT_TYPE", nullable = false, updatable = false, length = 10)
    public String getReportType() {
	return _reportType;
    }

    @Id
    @Column(name = "STATISTICS_ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "java.lang.Long")
    public Long getStatisticsId() {
	return _statisticsId;
    }

    @Column(name = "UPDATED_COUNT", nullable = false, updatable = true)
    public long getUpdated() {
	return _updated;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_projectUserLanguage == null) ? 0 : _projectUserLanguage.hashCode());
	return prime * result + ((_reportType == null) ? 0 : _reportType.hashCode());
    }

    public void setAddedApproved(long addedApproved) {
	_addedApproved = addedApproved;
    }

    public void setAddedBlacklisted(long addedBlacklisted) {
	_addedBlacklisted = addedBlacklisted;
    }

    public void setAddedOnHold(long addedOnHold) {
	_addedOnHold = addedOnHold;
    }

    public void setAddedPending(long addedPending) {
	_addedPending = addedPending;
    }

    public void setApproved(long approved) {
	_approved = approved;
    }

    public void setBlacklisted(long blacklisted) {
	_blacklisted = blacklisted;
    }

    public void setDeleted(long deleted) {
	_deleted = deleted;
    }

    public void setDemoted(long demoted) {
	_demoted = demoted;
    }

    public void setLanguageId(String languageId) {
	_languageId = languageId;
    }

    public void setOnHold(long onHold) {
	_onHold = onHold;
    }

    public void setProjectUserLanguage(ProjectUserLanguage projectUserLanguage) {
	_projectUserLanguage = projectUserLanguage;
    }

    public void setReportType(String reportType) {
	_reportType = reportType;
    }

    public void setStatisticsId(Long statisticsId) {
	_statisticsId = statisticsId;
    }

    public void setUpdated(long updated) {
	_updated = updated;
    }
}
