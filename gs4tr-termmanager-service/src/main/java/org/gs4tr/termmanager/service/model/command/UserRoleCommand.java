package org.gs4tr.termmanager.service.model.command;

import java.util.List;

import org.gs4tr.foundation.modules.security.model.Role;

public class UserRoleCommand {

    private List<Role> _roles;

    private Long _userId;

    public List<Role> getRoles() {
	return _roles;
    }

    public Long getUserId() {
	return _userId;
    }

    public void setRoles(List<Role> roles) {
	_roles = roles;
    }

    public void setUserId(Long userId) {
	_userId = userId;
    }

}
