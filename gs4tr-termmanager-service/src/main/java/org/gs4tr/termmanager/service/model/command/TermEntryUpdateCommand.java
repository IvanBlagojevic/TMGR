package org.gs4tr.termmanager.service.model.command;

import java.util.List;

import org.gs4tr.termmanager.model.UpdateCommand;

public class TermEntryUpdateCommand {

    private List<UpdateCommand> _termEntryUpdateCommands;

    public List<UpdateCommand> getTermEntryUpdateCommands() {
	return _termEntryUpdateCommands;
    }

    public void setTermEntryUpdateCommands(List<UpdateCommand> termEntryUpdateCommands) {
	_termEntryUpdateCommands = termEntryUpdateCommands;
    }

}
