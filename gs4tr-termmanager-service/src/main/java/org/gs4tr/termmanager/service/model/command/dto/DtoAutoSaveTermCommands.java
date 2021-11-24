package org.gs4tr.termmanager.service.model.command.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.termmanager.service.model.command.AutoSaveTermCommand;
import org.gs4tr.termmanager.service.model.command.AutoSaveTermCommands;

public class DtoAutoSaveTermCommands implements DtoTaskHandlerCommand<AutoSaveTermCommands> {

    private DtoAutoSaveTermCommand[] _autoSaveCommands;

    @Override
    public AutoSaveTermCommands convertToInternalTaskHandlerCommand() {
	AutoSaveTermCommands internaLcommand = new AutoSaveTermCommands();

	DtoAutoSaveTermCommand[] autoSaveCommands = getAutoSaveCommands();
	if (ArrayUtils.isNotEmpty(autoSaveCommands)) {
	    List<AutoSaveTermCommand> commands = new ArrayList<AutoSaveTermCommand>();
	    for (DtoAutoSaveTermCommand autoSaveTermCommand : autoSaveCommands) {
		AutoSaveTermCommand command = autoSaveTermCommand.convertToInternalTaskHandlerCommand();
		commands.add(command);
	    }

	    internaLcommand.setAutoSaveTermCommands(commands);
	}

	return internaLcommand;
    }

    public DtoAutoSaveTermCommand[] getAutoSaveCommands() {
	return _autoSaveCommands;
    }

    public void setAutoSaveCommands(DtoAutoSaveTermCommand[] autoSaveCommands) {
	_autoSaveCommands = autoSaveCommands;
    }
}
