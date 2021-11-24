package org.gs4tr.termmanager.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.UniqueConstraint;

import org.gs4tr.foundation.modules.entities.model.Identifiable;
import org.hibernate.annotations.Type;

@NamedQueries({ @NamedQuery(name = "ProjectUserLanguage.findByUserAndProject", query = "select projectUserLanguage "
	+ "from ProjectUserLanguage projectUserLanguage " + "where projectUserLanguage.user.userProfileId = :userId "
	+ "and projectUserLanguage.project.projectId = :projectId"),
	@NamedQuery(name = "ProjectUserLanguage.findProjectsByUser", query = "select distinct projectUserLanguage.project "
		+ "from ProjectUserLanguage projectUserLanguage "
		+ "where projectUserLanguage.user.userProfileId = :userId "
		+ "and projectUserLanguage.project.projectInfo.enabled = true"),
	@NamedQuery(name = "ProjectUserLanguage.findUsersByProject", query = "select projectUserLanguage.user "
		+ "from ProjectUserLanguage projectUserLanguage "
		+ "where projectUserLanguage.project.projectId = :projectId"),
	@NamedQuery(name = "ProjectUserLanguage.findProjectUserLanguagesByUser", query = "select projectUserLanguage "
		+ "from ProjectUserLanguage projectUserLanguage " + "left join projectUserLanguage.project as project "
		+ "where projectUserLanguage.user.userProfileId = :userId "
		+ "and project.projectInfo.enabled is true "),
	@NamedQuery(name = "ProjectUserLanguage.findProjectUserLanguagesByProject", query = "select distinct projectUserLanguage "
		+ "from ProjectUserLanguage projectUserLanguage " + "join fetch projectUserLanguage.project as project "
		+ "join fetch projectUserLanguage.user as user "
		+ "where projectUserLanguage.project.projectId = :projectId"),
	@NamedQuery(name = "ProjectUserLanguage.findProjectsByUserOrdered", query = "select distinct project "
		+ "from ProjectUserLanguage projectUserLanguage " + "inner join projectUserLanguage.project project "
		+ "where projectUserLanguage.user.userProfileId = :userId " + "order by project.projectId "),
	@NamedQuery(name = "ProjectUserLanguage.findByProjectIds", query = "select projectUserLanguage "
		+ "from ProjectUserLanguage projectUserLanguage "
		+ "where projectUserLanguage.project.projectId in (:projectIds) "
		+ "and projectUserLanguage.user.generic is true"),
	@NamedQuery(name = "ProjectUserLanguage.findUsersByProjectAndLanguageIds", query = "select distinct projectUserLanguage.user "
		+ "from ProjectUserLanguage projectUserLanguage "
		+ "where projectUserLanguage.project.projectId = :projectId and projectUserLanguage.language in (:languageIds) "),
	@NamedQuery(name = "ProjectUserLanguage.getUserLanguagesMap", query = "select new Map(pul.user.userProfileId as userProfileId, pul.language as languageId)"
		+ "from ProjectUserLanguage pul where pul.project.projectId = :projectId  "),
	@NamedQuery(name = "ProjectUserLanguage.recodeProjectUserLanguage", query = "update ProjectUserLanguage pul set "
		+ "pul.language = :languageTo "
		+ "where pul.project.projectId = :projectId and pul.language = :languageFrom") })
@Entity
@Table(name = "PROJECT_USER_LANGUAGE", uniqueConstraints = {
	@UniqueConstraint(name = "IDX_PRO_USR_LANG_UNIQUE", columnNames = { "PROJECT_ID", "USER_PROFILE_ID",
		"LANGUAGE_ID" }) })
public class ProjectUserLanguage implements Serializable, Identifiable<Long> {

    private static final long serialVersionUID = 841919860687902116L;

    private String _language;

    private TmProject _project;

    private Long _projectUserLanguageId;

    private Set<Statistics> _statistics;

    private TmUserProfile _user;

    public ProjectUserLanguage() {
    }

    public ProjectUserLanguage(ProjectUserLanguage projectUserLanguage) {
	setLanguage(projectUserLanguage.getLanguage());
	setProject(projectUserLanguage.getProject());
	setUser(projectUserLanguage.getUser());
	setStatistics(projectUserLanguage.getStatistics());
    }

    public ProjectUserLanguage(TmProject project, TmUserProfile user, String language) {
	super();
	_project = project;
	_language = language;
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
	ProjectUserLanguage other = (ProjectUserLanguage) obj;
	if (_language == null) {
	    if (other._language != null)
		return false;
	} else if (!_language.equals(other._language))
	    return false;
	if (_project == null) {
	    if (other._project != null)
		return false;
	} else if (!_project.equals(other._project))
	    return false;
	if (_user == null) {
	    return other._user == null;
	} else
	    return _user.equals(other._user);
    }

    @Override
    @Transient
    public Long getIdentifier() {
	return getProjectUserLanguageId();
    }

    @Column(name = "LANGUAGE_ID", nullable = false, updatable = false, length = 10)
    public String getLanguage() {
	return _language;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID", nullable = false, updatable = false)
    public TmProject getProject() {
	return _project;
    }

    @Id
    @Column(name = "PROJECT_USER_LANGUAGE_ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "java.lang.Long")
    public Long getProjectUserLanguageId() {
	return _projectUserLanguageId;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "projectUserLanguage", cascade = CascadeType.ALL)
    public Set<Statistics> getStatistics() {
	return _statistics;
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
	result = prime * result + ((_language == null) ? 0 : _language.hashCode());
	result = prime * result + ((_project == null) ? 0 : _project.hashCode());
	return prime * result + ((_user == null) ? 0 : _user.hashCode());
    }

    @Override
    public void setIdentifier(Long identifier) {
	setProjectUserLanguageId(identifier);
    }

    public void setLanguage(String language) {
	_language = language;
    }

    public void setProject(TmProject project) {
	_project = project;
    }

    public void setProjectUserLanguageId(Long projectUserLanguageId) {
	_projectUserLanguageId = projectUserLanguageId;
    }

    public void setStatistics(Set<Statistics> statistics) {
	_statistics = statistics;
    }

    public void setUser(TmUserProfile user) {
	_user = user;
    }
}
