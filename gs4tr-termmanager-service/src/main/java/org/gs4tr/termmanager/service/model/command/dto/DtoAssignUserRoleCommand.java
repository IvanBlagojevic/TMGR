package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.dto.Role;
import org.gs4tr.termmanager.model.dto.converter.RoleConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.AssignUserRoleCommand;

public class DtoAssignUserRoleCommand implements DtoTaskHandlerCommand<AssignUserRoleCommand> {

    private org.gs4tr.termmanager.model.dto.Role _projectRole;

    private DtoProjectRoleCommand[] _projectRoles;

    private String _roleTypeInfo;

    private org.gs4tr.termmanager.model.dto.Role[] _systemRoles;

    private Ticket _userTicket;

    @Override
    public AssignUserRoleCommand convertToInternalTaskHandlerCommand() {
	AssignUserRoleCommand assignUserRoleCommand = new AssignUserRoleCommand();

	assignUserRoleCommand.setUserId(TicketConverter.fromDtoToInternal(getUserTicket(), Long.class));

	assignUserRoleCommand.setSystemRoles(RoleConverter.fromDtoToInternal(getSystemRoles()));

	assignUserRoleCommand.setProjectRole(RoleConverter.fromDtoToInternal(getProjectRole()));

	assignUserRoleCommand.setRoleTypeInfo(getRoleTypeInfo());

	return assignUserRoleCommand;
    }

    public DtoProjectRoleCommand[] getProjectRoles() {
	return _projectRoles;
    }

    public org.gs4tr.termmanager.model.dto.Role[] getSystemRoles() {
	return _systemRoles;
    }

    public Ticket getUserTicket() {
	return _userTicket;
    }

    public void setProjectRole(Role projectRole) {
	_projectRole = projectRole;
    }

    public void setProjectRoles(DtoProjectRoleCommand[] projectRoles) {
	_projectRoles = projectRoles;
    }

    public void setRoleTypeInfo(String roleTypeInfo) {
	_roleTypeInfo = roleTypeInfo;
    }

    public void setSystemRoles(org.gs4tr.termmanager.model.dto.Role[] systemRoles) {
	_systemRoles = systemRoles;
    }

    public void setUserTicket(Ticket userTicket) {
	_userTicket = userTicket;
    }

    private Role getProjectRole() {
	return _projectRole;
    }

    private String getRoleTypeInfo() {
	return _roleTypeInfo;
    }

}
