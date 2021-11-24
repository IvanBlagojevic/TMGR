package org.gs4tr.termmanager.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.gs4tr.foundation.modules.entities.model.Identifiable;
import org.gs4tr.foundation.modules.security.model.Role;
import org.hibernate.annotations.Type;

@NamedQueries({
	@NamedQuery(name = "PowerUserProjectRole.findByUserId", query = "select powerUserProjectRole "
		+ "from TmPowerUserProjectRole as powerUserProjectRole "
		+ "where powerUserProjectRole.userProfile.userProfileId = :userId"),
	@NamedQuery(name = "PowerUserProjectRole.updateRole", query = " update TmPowerUserProjectRole powerUserProjectRole "
		+ "set powerUserProjectRole.role.roleId = :newRole  "
		+ "where powerUserProjectRole.userProfile.userProfileId = :userId") })
@Entity
@Table(name = "POWER_USER_PROJECT_ROLE")
public class TmPowerUserProjectRole implements Identifiable<Long> {
    private static final long serialVersionUID = 9162807809362420106L;

    private Long _powerUserProjectRoleId;

    private Role _role;

    private TmUserProfile _userProfile;

    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (o == null || getClass() != o.getClass())
	    return false;
	TmPowerUserProjectRole that = (TmPowerUserProjectRole) o;
	return Objects.equals(_userProfile, that._userProfile);
    }

    @Override
    @Transient
    public Long getIdentifier() {
	return getPowerUserProjectRoleId();
    }

    @Id
    @Column(name = "POWER_USER_PROJECT_ROLE_ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "java.lang.Long")
    public Long getPowerUserProjectRoleId() {
	return _powerUserProjectRoleId;
    }

    @OneToOne
    @JoinColumn(name = "ROLE_ID", nullable = false)
    public Role getRole() {
	return _role;
    }

    @OneToOne
    @JoinColumn(name = "USER_PROFILE_ID", nullable = false, updatable = false, unique = true)
    public TmUserProfile getUserProfile() {
	return _userProfile;
    }

    @Override
    public int hashCode() {
	return Objects.hash(_userProfile);
    }

    @Override
    public void setIdentifier(Long identifier) {
	setPowerUserProjectRoleId(identifier);
    }

    public void setPowerUserProjectRoleId(Long powerUserProjectRoleId) {
	_powerUserProjectRoleId = powerUserProjectRoleId;
    }

    public void setRole(Role role) {
	_role = role;
    }

    public void setUserProfile(TmUserProfile userProfile) {
	_userProfile = userProfile;
    }
}
