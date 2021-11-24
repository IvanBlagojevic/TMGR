package org.gs4tr.termmanager.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.gs4tr.foundation.modules.entities.model.ProjectsHolder;
import org.gs4tr.foundation.modules.entities.model.UserProfilesHolder;
import org.gs4tr.foundation.modules.organization.model.BaseOrganization;

@NamedQueries({
	@NamedQuery(name = "Organization.findOrganizationById", query = "select organization "
		+ "from TmOrganization organization " + "where organization.organizationId = :entityId"),
	@NamedQuery(name = "Organization.findChildOrganizationsById", query = "select organization "
		+ "from TmOrganization organization " + "join fetch organization.parentOrganization parentOrganization "
		+ "left join fetch organization.users " + "left join fetch organization.projects "
		+ "where parentOrganization.organizationId = :entityId"),
	@NamedQuery(name = "Organization.findOrganizationProjectsById", query = "select distinct project from TmProject project "
		+ "left join fetch project.projectLanguages as projectLanguages "
		+ "left join fetch project.organization as organization "
		+ "where project.organization.organizationId = :entityId"),
	@NamedQuery(name = "Organization.enableOrganizationUsers", query = "update from TmUserProfile userprofile "
		+ "set userprofile.userInfo.enabled = :enabled "
		+ "where userprofile.organization.organizationId = :entityId"),
	@NamedQuery(name = "Organization.findByName", query = "select organization "
		+ "from TmOrganization organization " + "where organization.organizationInfo.name = :name"),

	@NamedQuery(name = "Organization.findOrganizationIdByUserId", query = "select organization.organizationId "
		+ "from TmOrganization organization " + "join organization.users as user "
		+ "where user.userProfileId = :userId"),
	@NamedQuery(name = "Organization.findOrganizationNameByUserId", query = "select organization.organizationInfo.name "
		+ "from TmOrganization organization " + "join organization.users as user "
		+ "where user.userProfileId = :userId") })
@Entity
@Table(name = "ORGANIZATION")
public class TmOrganization extends BaseOrganization<TmOrganization>
	implements ProjectsHolder<TmProject>, UserProfilesHolder<TmUserProfile> {

    private static final long serialVersionUID = -8851256975634546531L;

    private Set<TmProject> _projects;

    private Set<TmUserProfile> _users;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    public Set<TmProject> getProjects() {
	return _projects;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID", updatable = false)
    public Set<TmUserProfile> getUsers() {
	return _users;
    }

    public void setProjects(Set<TmProject> projects) {
	_projects = projects;
    }

    public void setUsers(Set<TmUserProfile> users) {
	_users = users;
    }
}
