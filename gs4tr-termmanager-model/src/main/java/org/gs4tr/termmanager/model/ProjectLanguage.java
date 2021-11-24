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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.gs4tr.foundation.modules.entities.model.Identifiable;

@NamedQueries({
	@NamedQuery(name = "ProjectLanguage.findByProjectId", query = "select projectLanguages "
		+ "from ProjectLanguage projectLanguages " + "where projectLanguages.project.projectId = :projectId"),

	@NamedQuery(name = "ProjectLanguage.getLanguageIdsByProjectId", query = "select projectLanguage.language "
		+ "from ProjectLanguage projectLanguage " + "where projectLanguage.project.projectId = :projectId"),

	@NamedQuery(name = "ProjectLanguage.getProjectLanguagesMap", query = "select new Map(p.projectId as projectId, pl.language as languageId) "
		+ "from ProjectLanguage as pl " + "join pl.project as p " + "where p.projectInfo.enabled is true"),

	@NamedQuery(name = "ProjectLanguage.findByProjectIds", query = "select projectLanguages "
		+ "from ProjectLanguage projectLanguages "
		+ "where projectLanguages.project.projectId in (:projectIds)") })
@Entity
@Table(name = "PROJECT_LANGUAGE", uniqueConstraints = {
	@UniqueConstraint(name = "IDX_PRO_LANG_WFDEF", columnNames = { "PROJECT_ID", "LANGUAGE_ID" }) })
public class ProjectLanguage implements Serializable, Identifiable<Long> {
    private static final long serialVersionUID = 2707318517107357176L;

    private Boolean _default = Boolean.FALSE;

    private String _language;

    private TmProject _project;

    private Long _projectLanguageId;

    public ProjectLanguage() {

    }

    public ProjectLanguage(ProjectLanguage projectLanguage) {
	setLanguage(projectLanguage.getLanguage());
	setProject(projectLanguage.getProject());
    }

    public ProjectLanguage(TmProject project, String language) {
	_project = project;
	_language = language;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ProjectLanguage other = (ProjectLanguage) obj;
	if (_default == null) {
	    if (other._default != null)
		return false;
	} else if (!_default.equals(other._default))
	    return false;
	if (_language == null) {
	    return other._language == null;
	} else
	    return _language.equals(other._language);
    }

    @Override
    @Transient
    public Long getIdentifier() {
	return getProjectLanguageId();
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
    @Column(name = "PROJECT_LANG_ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getProjectLanguageId() {
	return _projectLanguageId;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_default == null) ? 0 : _default.hashCode());
	return prime * result + ((_language == null) ? 0 : _language.hashCode());
    }

    @Column(name = "IS_DEFAULT")
    public Boolean isDefault() {
	return _default;
    }

    public void setDefault(Boolean def) {
	_default = def;
    }

    @Override
    public void setIdentifier(Long identifier) {
	setProjectLanguageId(identifier);
    }

    public void setLanguage(String language) {
	_language = language;
    }

    public void setProject(TmProject project) {
	_project = project;
    }

    public void setProjectLanguageId(Long projectLanguageId) {
	_projectLanguageId = projectLanguageId;
    }
}
