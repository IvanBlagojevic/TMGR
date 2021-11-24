package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.dto.OrganizationInfo;
import org.gs4tr.termmanager.model.dto.converter.OrganizationInfoConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.OrganizationCommand;

public class DtoOrganizationCommand implements DtoTaskHandlerCommand<OrganizationCommand> {

    private String _name;

    private OrganizationInfo _organizationInfo;

    private Ticket _ticket;

    private Ticket _parentTicket;

    public OrganizationCommand convertToInternalTaskHandlerCommand() {

	OrganizationCommand organizationCommand = new OrganizationCommand();

	organizationCommand.setOrganizationId(TicketConverter.fromDtoToInternal(getTicket(), Long.class));

	organizationCommand.setParentOrganizationId(TicketConverter.fromDtoToInternal(getParentTicket(), Long.class));

	organizationCommand.setOrganizationInfo(OrganizationInfoConverter.fromDtoToInternal(getOrganizationInfo()));

	organizationCommand.setName(getName());

	return organizationCommand;
    }

    public String getName() {
	return _name;
    }

    public OrganizationInfo getOrganizationInfo() {
	return _organizationInfo;
    }

    public Ticket getParentTicket() {
	return _parentTicket;
    }

    public Ticket getTicket() {
	return _ticket;
    }

    public void setName(String name) {
	_name = name;
    }

    public void setOrganizationInfo(OrganizationInfo organizationInfo) {
	_organizationInfo = organizationInfo;
    }

    public void setParentTicket(Ticket parentTicket) {
	_parentTicket = parentTicket;
    }

    public void setTicket(Ticket ticket) {
	_ticket = ticket;
    }
}