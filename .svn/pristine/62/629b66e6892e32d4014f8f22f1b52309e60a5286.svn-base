package org.gs4tr.termmanager.service.model.command.dto;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.termmanager.model.dto.UpdateCommand;
import org.gs4tr.termmanager.service.model.command.TermEntryUpdateCommand;

public class DtoTermEntryUpdateCommand implements DtoTaskHandlerCommand<TermEntryUpdateCommand> {

    private UpdateCommand[] _termEntryUpdateCommands;

    @Override
    public TermEntryUpdateCommand convertToInternalTaskHandlerCommand() {

	TermEntryUpdateCommand termEntryUpdateCommand = new TermEntryUpdateCommand();

	List<org.gs4tr.termmanager.model.UpdateCommand> termEntryUpdateCommands = new ArrayList<org.gs4tr.termmanager.model.UpdateCommand>();

	for (UpdateCommand dtoUpdateCommand : getTermEntryUpdateCommands()) {

	    org.gs4tr.termmanager.model.UpdateCommand updateCommand = new org.gs4tr.termmanager.model.UpdateCommand();

	    updateCommand.setCommand(dtoUpdateCommand.getCommand());
	    updateCommand.setItemType(dtoUpdateCommand.getType());
	    updateCommand.setSubType(dtoUpdateCommand.getSubType());
	    updateCommand.setMarkerId(dtoUpdateCommand.getMarkerId());

	    updateCommand.setParentMarkerId(dtoUpdateCommand.getParentMarkerId());
	    updateCommand.setValue(dtoUpdateCommand.getValue());

	    termEntryUpdateCommands.add(updateCommand);
	}

	termEntryUpdateCommand.setTermEntryUpdateCommands(termEntryUpdateCommands);

	return termEntryUpdateCommand;
    }

    public UpdateCommand[] getTermEntryUpdateCommands() {
	return _termEntryUpdateCommands;
    }

    public void setTermEntryUpdateCommands(UpdateCommand[] termEntryUpdateCommands) {
	_termEntryUpdateCommands = termEntryUpdateCommands;
    }

}
