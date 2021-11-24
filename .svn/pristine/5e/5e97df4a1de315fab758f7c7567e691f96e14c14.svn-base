package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.AssignOrganizationProjectCommand;

public class DtoAssignOrganizationProjectCommand implements DtoTaskHandlerCommand<AssignOrganizationProjectCommand> {

    private Ticket _organizationTicket;

    private Ticket[] _projectTickets;

    @Override
    public AssignOrganizationProjectCommand convertToInternalTaskHandlerCommand() {
	AssignOrganizationProjectCommand assignOrganizationProjectCommand = new AssignOrganizationProjectCommand();

	assignOrganizationProjectCommand
		.setOrganizationId(TicketConverter.fromDtoToInternal(getOrganizationTicket(), Long.class));

	assignOrganizationProjectCommand
		.setProjectIds(TicketConverter.fromDtoToInternal(getProjectTickets(), Long.class));

	return assignOrganizationProjectCommand;

    }

    public Ticket getOrganizationTicket() {
	return _organizationTicket;
    }

    public Ticket[] getProjectTickets() {
	return _projectTickets;
    }

    public void setOrganizationTicket(Ticket organizationTicket) {
	_organizationTicket = organizationTicket;
    }

    public void setProjectTickets(Ticket[] projectTickets) {
	_projectTickets = projectTickets;
    }

}
