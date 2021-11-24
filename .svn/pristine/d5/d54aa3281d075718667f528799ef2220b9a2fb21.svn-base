package org.gs4tr.termmanager.service.model.command.dto;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.termmanager.model.dto.converter.RoleConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.AssignUserProjectRoleCommand;
import org.gs4tr.termmanager.service.model.command.UserRoleCommand;

public class DtoAssignUserProjectRoleCommand implements DtoTaskHandlerCommand<AssignUserProjectRoleCommand> {

    private Ticket _projectTicket;

    private DtoUserRoleCommand[] _userRoles;

    @Override
    public AssignUserProjectRoleCommand convertToInternalTaskHandlerCommand() {
	AssignUserProjectRoleCommand userProjectRoleCommand = new AssignUserProjectRoleCommand();

	userProjectRoleCommand.setProjectId(TicketConverter.fromDtoToInternal(getProjectTicket(), Long.class));

	List<UserRoleCommand> userRoleCommands = new ArrayList<UserRoleCommand>();
	for (DtoUserRoleCommand dtoCommand : getUserRoles()) {
	    UserRoleCommand newCommand = new UserRoleCommand();
	    List<Role> modelRoles = new ArrayList<Role>();
	    for (org.gs4tr.termmanager.model.dto.Role role : dtoCommand.getRoles()) {
		modelRoles.add(RoleConverter.fromDtoToInternal(role));
	    }
	    newCommand.setUserId(TicketConverter.fromDtoToInternal(dtoCommand.getUserTicket(), Long.class));
	    newCommand.setRoles(modelRoles);

	    userRoleCommands.add(newCommand);
	}
	userProjectRoleCommand.setUserRoles(userRoleCommands);

	return userProjectRoleCommand;
    }

    public Ticket getProjectTicket() {
	return _projectTicket;
    }

    public DtoUserRoleCommand[] getUserRoles() {
	return _userRoles;
    }

    public void setProjectTicket(Ticket projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setUserRoles(DtoUserRoleCommand[] userRoles) {
	_userRoles = userRoles;
    }

}
