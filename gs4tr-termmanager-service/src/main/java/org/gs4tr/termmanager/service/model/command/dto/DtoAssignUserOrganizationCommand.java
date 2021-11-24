package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.AssignUserOrganizationCommand;

public class DtoAssignUserOrganizationCommand implements DtoTaskHandlerCommand<AssignUserOrganizationCommand> {

    private Ticket _organizationTicket;

    private Ticket _userTicket;

    @Override
    public AssignUserOrganizationCommand convertToInternalTaskHandlerCommand() {

	AssignUserOrganizationCommand assignUserVendorCommand = new AssignUserOrganizationCommand();

	assignUserVendorCommand
		.setOrganizationId(TicketConverter.fromDtoToInternal(getOrganizationTicket(), Long.class));
	assignUserVendorCommand.setUserId(TicketConverter.fromDtoToInternal(getUserTicket(), Long.class));
	return assignUserVendorCommand;

    }

    public Ticket getOrganizationTicket() {
	return _organizationTicket;
    }

    public Ticket getUserTicket() {
	return _userTicket;
    }

    public void setOrganizationTicket(Ticket organizationTicket) {
	_organizationTicket = organizationTicket;
    }

    public void setUserTicket(Ticket userTicket) {
	_userTicket = userTicket;
    }

}
