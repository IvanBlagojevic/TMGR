package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.dto.Attribute;
import org.gs4tr.termmanager.model.dto.converter.AttributeConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.AssignProjectAttributesCommand;

public class DtoAssignProjectAttributesCommand implements DtoTaskHandlerCommand<AssignProjectAttributesCommand> {

    private Attribute[] _projectAttributes;

    private Ticket _projectTicket;

    @Override
    public AssignProjectAttributesCommand convertToInternalTaskHandlerCommand() {
	AssignProjectAttributesCommand assignProjectAttributesCommand = new AssignProjectAttributesCommand();

	assignProjectAttributesCommand
		.setProjectAttributes(AttributeConverter.fromDtoToInternal(getProjectAttributes()));
	assignProjectAttributesCommand.setProjectId(TicketConverter.fromDtoToInternal(getProjectTicket(), Long.class));

	return assignProjectAttributesCommand;
    }

    public Attribute[] getProjectAttributes() {
	return _projectAttributes;
    }

    public Ticket getProjectTicket() {
	return _projectTicket;
    }

    public void setProjectAttributes(Attribute[] projectAttributes) {
	_projectAttributes = projectAttributes;
    }

    public void setProjectTicket(Ticket projectTicket) {
	_projectTicket = projectTicket;
    }

}
