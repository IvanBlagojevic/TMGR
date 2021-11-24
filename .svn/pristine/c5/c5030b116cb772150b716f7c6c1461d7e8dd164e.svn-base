package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Arrays;
import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.dto.UpdateCommand;
import org.gs4tr.termmanager.model.dto.converter.UpdateCommandConverter;
import org.gs4tr.termmanager.service.model.command.AssignTermEntryAttributesCommand;

public class DtoAssignTermEntryAttributesCommand implements DtoTaskHandlerCommand<AssignTermEntryAttributesCommand> {

    private DtoAttributeTypeFileCommand[] _attributeTypeFileCommands;

    private DtoFileEditCommandCommand[] _fileEditCommands;

    private String[] _resourceIdsForRemoval;

    private Ticket _termEntryTicket;

    private UpdateCommand[] _updateCommands;

    @Override
    public AssignTermEntryAttributesCommand convertToInternalTaskHandlerCommand() {
	AssignTermEntryAttributesCommand assignTermEntryAttributesCommand = new AssignTermEntryAttributesCommand();

	assignTermEntryAttributesCommand
		.setUpdateCommands(UpdateCommandConverter.fromDtoToInternal(getUpdateCommands()));
	assignTermEntryAttributesCommand.setTermEntryId(getTermEntryTicket().getTicketId());

	Map<String, String> attributeTypeFileNameMap = assignTermEntryAttributesCommand.getAttributeTypeFileNameMap();
	DtoAttributeTypeFileCommand[] attributeTypeFileCommands = getAttributeTypeFileCommands();
	if (attributeTypeFileCommands != null) {
	    for (DtoAttributeTypeFileCommand attributeTypeFile : attributeTypeFileCommands) {
		attributeTypeFileNameMap.put(attributeTypeFile.getName(), attributeTypeFile.getAttributeType());
	    }
	}

	DtoFileEditCommandCommand[] fileEditCommands = getFileEditCommands();
	if (fileEditCommands != null) {
	    for (DtoFileEditCommandCommand command : fileEditCommands) {
		assignTermEntryAttributesCommand.getResourceTicketForEditingMap().put(command.getName(),
			command.getTicketForRemoval());

	    }
	}

	if (getResourceIdsForRemoval() != null) {
	    assignTermEntryAttributesCommand.setResourceIdsForRemoval(Arrays.asList(getResourceIdsForRemoval()));
	}

	return assignTermEntryAttributesCommand;
    }

    public DtoAttributeTypeFileCommand[] getAttributeTypeFileCommands() {
	return _attributeTypeFileCommands;
    }

    public DtoFileEditCommandCommand[] getFileEditCommands() {
	return _fileEditCommands;
    }

    public String[] getResourceIdsForRemoval() {
	return _resourceIdsForRemoval;
    }

    public Ticket getTermEntryTicket() {
	return _termEntryTicket;
    }

    public UpdateCommand[] getUpdateCommands() {
	return _updateCommands;
    }

    public void setAttributeTypeFileCommands(DtoAttributeTypeFileCommand[] attributeTypeFileCommands) {
	_attributeTypeFileCommands = attributeTypeFileCommands;
    }

    public void setFileEditCommands(DtoFileEditCommandCommand[] fileEditCommands) {
	_fileEditCommands = fileEditCommands;
    }

    public void setResourceIdsForRemoval(String[] resourceIdsForRemoval) {
	_resourceIdsForRemoval = resourceIdsForRemoval;
    }

    public void setTermEntryTicket(Ticket termEntryTicket) {
	_termEntryTicket = termEntryTicket;
    }

    public void setUpdateCommands(UpdateCommand[] updateCommands) {
	_updateCommands = updateCommands;
    }

}
