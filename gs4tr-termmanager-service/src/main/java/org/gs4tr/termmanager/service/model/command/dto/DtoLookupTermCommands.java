package org.gs4tr.termmanager.service.model.command.dto;

import java.util.List;

import org.gs4tr.termmanager.service.model.command.LookupTermCommand;
import org.gs4tr.termmanager.service.model.command.LookupTermCommands;

public class DtoLookupTermCommands implements DtoTaskHandlerCommand<LookupTermCommands> {

    private DtoLookupTermCommand[] _projectUnits;

    @Override
    public LookupTermCommands convertToInternalTaskHandlerCommand() {
	LookupTermCommands cmds = new LookupTermCommands();

	List<LookupTermCommand> internalList = cmds.getCommands();

	if (_projectUnits != null && _projectUnits.length > 0) {
	    for (DtoLookupTermCommand dtoCommand : _projectUnits) {
		internalList.add(dtoCommand.convertToInternalTaskHandlerCommand());
	    }
	}

	return cmds;
    }

    public DtoLookupTermCommand[] getProjectUnits() {
	return _projectUnits;
    }

    public void setProjectUnits(DtoLookupTermCommand[] projectUnits) {
	_projectUnits = projectUnits;
    }
}
