package org.gs4tr.termmanager.service.model.command;

import java.util.Set;

import org.gs4tr.foundation.modules.security.model.Role;

public class AssignUserRoleCommand {

    private Role _projectRole;

    private String _roleTypeInfo;

    private Set<Role> _systemRoles;

    private Long _userId;

    public Role getProjectRole() {
	return _projectRole;
    }

    public String getRoleTypeInfo() {
	return _roleTypeInfo;
    }

    public Set<Role> getSystemRoles() {
	return _systemRoles;
    }

    public Long getUserId() {
	return _userId;
    }

    public void setProjectRole(Role projectRole) {
	_projectRole = projectRole;
    }

    public void setRoleTypeInfo(String roleTypeInfo) {
	_roleTypeInfo = roleTypeInfo;
    }

    public void setSystemRoles(Set<Role> systemRoles) {
	_systemRoles = systemRoles;
    }

    public void setUserId(Long userId) {
	_userId = userId;
    }

}
