package org.gs4tr.termmanager.service.model.command;

import java.util.ArrayList;
import java.util.List;

public class LookupTermCommands {

    private List<LookupTermCommand> _commands;

    public LookupTermCommands() {
	_commands = new ArrayList<LookupTermCommand>();
    }

    public List<LookupTermCommand> getCommands() {
	return _commands;
    }

    public void setCommands(List<LookupTermCommand> commands) {
	_commands = commands;
    }
}
