package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.AssignUserProjectCommand;

public class DtoAssignUserProjectCommand implements DtoTaskHandlerCommand<AssignUserProjectCommand> {

    private Ticket[] _projectTickets;

    private Ticket _userTicket;

    @Override
    public AssignUserProjectCommand convertToInternalTaskHandlerCommand() {

	AssignUserProjectCommand assignUserProjectCommand = new AssignUserProjectCommand();

	assignUserProjectCommand.setProjectIds(TicketConverter.fromDtoToInternal(getProjectTickets(), Long.class));
	assignUserProjectCommand.setUserId(TicketConverter.fromDtoToInternal(getUserTicket(), Long.class));

	return assignUserProjectCommand;

    }

    public Ticket[] getProjectTickets() {
	return _projectTickets;
    }

    public Ticket getUserTicket() {
	return _userTicket;
    }

    public void setProjectTickets(Ticket[] projectTickets) {
	_projectTickets = projectTickets;
    }

    public void setUserTicket(Ticket userTicket) {
	_userTicket = userTicket;
    }

}
