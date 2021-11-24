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
import javax.persistence.UniqueConstraint;

import org.gs4tr.foundation.modules.entities.model.Identifiable;
import org.gs4tr.foundation.modules.security.model.Role;
import org.hibernate.annotations.Type;

@NamedQueries({
	@NamedQuery(name = "UserProjectRole.findSuperUsersByProjectAndLanguages", query = "select distinct userProjectRole.userProfile "
		+ "from UserProjectRole userProjectRole "
		+ "where userProjectRole.project.projectId = :projectId and userProjectRole.role = 'super_user' "
		+ "and userProjectRole.userProfile.userProfileId in "
		+ "(select pul.user.userProfileId from ProjectUserLanguage pul "
		+ "where pul.project.projectId = :projectId " + "and pul.language in (:languages))"),

	@NamedQuery(name = "UserProjectRole.getAllUserProjectRolesByUserAndProject", query = "select distinct userProjectRole "
		+ "from UserProjectRole userProjectRole join fetch userProjectRole.userProfile as userProfile "
		+ "join fetch userProjectRole.project as project join fetch userProjectRole.role as role "
		+ "where userProfile.userProfileId = :userId and project.projectId = :projectId"),

	@NamedQuery(name = "UserProjectRole.getUserProjectRolesByUser", query = "select userProjectRole "
		+ "from UserProjectRole userProjectRole join fetch userProjectRole.userProfile as userProfile "
		+ "join fetch userProjectRole.project as project join fetch userProjectRole.role as role "
		+ "where userProfile.userProfileId = :userId"),

	@NamedQuery(name = "UserProjectRole.getRolesByUserAndProject", query = "select userProjectRole.role as role "
		+ "from UserProjectRole userProjectRole " + "where userProjectRole.userProfile.userProfileId = :userId "
		+ "and userProjectRole.project.projectId = :projectId"),

	@NamedQuery(name = "UserProjectRole.getUsersByProject", query = "select userProjectRole.userProfile "
		+ "from UserProjectRole userProjectRole " + "where userProjectRole.project.projectId = :projectId"),

	@NamedQuery(name = "UserProjectRole.getGenericUsersByProject", query = "select genericUser "
		+ "from UserProjectRole userProjectRole " + "left join userProjectRole.userProfile as genericUser "
		+ "where genericUser.generic is true " + "and userProjectRole.project.projectId = :projectId "),

	@NamedQuery(name = "UserProjectRole.getProjectsByUser", query = "select distinct project "
		+ "from UserProjectRole userProjectRole "
		+ "where userProjectRole.userProfile.userProfileId = :userId"),

	@NamedQuery(name = "UserProjectRole.getUserProjectRoleByProject", query = "select distinct userProjectRole "
		+ "from UserProjectRole userProjectRole " + "left join fetch userProjectRole.userProfile "
		+ "where userProjectRole.project.projectId = :projectId"),

	@NamedQuery(name = "UserProjectRole.getUsersByProjectAndRole", query = "select distinct userProfile "
		+ "from UserProjectRole userProjectRole join userProjectRole.userProfile as userProfile "
		+ "join userProjectRole.project as project join userProjectRole.role as role "
		+ "where role.roleId = :roleId and project.projectId = :projectId"),

	@NamedQuery(name = "UserProjectRole.updateRoles", query = " update UserProjectRole userProjectRole "
		+ "set role = :newRole  " + "where role = :oldRole"),

	@NamedQuery(name = "UserProjectRole.getProjectRolesByUser", query = "select new Map(r as role, userProjectRole.project.projectId as projectId) "
		+ "from UserProjectRole userProjectRole  " + "left join userProjectRole.role as r "
		+ "where userProjectRole.userProfile.userProfileId = :userProfileId"),

	@NamedQuery(name = "UserProjectRole.getUsersByProjectForReport", query = "select distinct user "
		+ "from UserProjectRole userProjectRole " + "join userProjectRole.userProfile user "
		+ "join userProjectRole.project project " + "where project.projectInfo.enabled is true "
		+ "and user.generic is false " + "and user.userInfo.enabled is true "
		+ "and user.userInfo.emailNotification is true "),

	@NamedQuery(name = "UserProjectRole.getRoleIdsByUserAndProject", query = "select userProjectRole.role.roleId "
		+ "from UserProjectRole userProjectRole " + "where userProjectRole.userProfile.userProfileId = :userId "
		+ "and userProjectRole.project.projectId = :projectId") })
@Entity
@Table(name = "USER_PROJECT_ROLE", uniqueConstraints = {
	@UniqueConstraint(name = "IDX_USR_PRO_ROLE_UNIQUE", columnNames = { "USER_PROFILE_ID", "PROJECT_ID",
		"ROLE_ID" }) })
public class UserProjectRole implements Identifiable<Long> {
    private static final long serialVersionUID = -2116629940882378976L;

    private TmProject _project;

    private Role _role;

    private TmUserProfile _userProfile;

    private Long _userProjectRoleId;

    public UserProjectRole() {

    }

    public UserProjectRole(TmProject project, Role role, TmUserProfile userProfile) {

	_project = project;
	_role = role;
	_userProfile = userProfile;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (!super.equals(obj))
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	final UserProjectRole other = (UserProjectRole) obj;
	if (_project == null) {
	    return other._project == null;
	} else
	    return _project.equals(other._project);
    }

    @Override
    @Transient
    public Long getIdentifier() {
	return getUserProjectRoleId();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID", nullable = false)
    public TmProject getProject() {
	return _project;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    public Role getRole() {
	return _role;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_PROFILE_ID", nullable = false)
    public TmUserProfile getUserProfile() {
	return _userProfile;
    }

    @Id
    @Column(name = "USER_PROJECT_ROLE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "java.lang.Long")
    public Long getUserProjectRoleId() {
	return _userProjectRoleId;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	return prime * result + ((_project == null) ? 0 : _project.hashCode());
    }

    @Override
    public void setIdentifier(Long identifier) {
	setUserProjectRoleId(identifier);
    }

    public void setProject(TmProject project) {
	_project = project;
    }

    public void setRole(Role role) {
	_role = role;
    }

    public void setUserProfile(TmUserProfile userProfile) {
	_userProfile = userProfile;
    }

    public void setUserProjectRoleId(Long projectRoleId) {
	this._userProjectRoleId = projectRoleId;
    }

}
