package org.gs4tr.termmanager.service.model.command.dto;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.termmanager.service.model.command.MergeCommands;
import org.gs4tr.termmanager.service.model.command.MultipleTermEntriesMergeCommand;
import org.gs4tr.termmanager.service.model.command.SaveDashboardCommand;

public class DtoMergeCommands implements DtoTaskHandlerCommand<MergeCommands> {

    private DtoMultipleTermEntriesMergeCommand _multipleTermEntriesMergeCommand;

    @Override
    public MergeCommands convertToInternalTaskHandlerCommand() {

	MergeCommands mergeCommands = new MergeCommands();

	DtoMultipleTermEntriesMergeCommand multipleTermEntriesMergeCommand = getMultipleTermEntriesMergeCommand();
	MultipleTermEntriesMergeCommand command;

	if (multipleTermEntriesMergeCommand != null) {
	    command = multipleTermEntriesMergeCommand.convertToInternalTaskHandlerCommand();
	    mergeCommands.setMultipleTermEntriesMergeCommand(command);
	}

	return mergeCommands;
    }

    public DtoMultipleTermEntriesMergeCommand getMultipleTermEntriesMergeCommand() {
	return _multipleTermEntriesMergeCommand;
    }

    public void setMultipleTermEntriesMergeCommand(DtoMultipleTermEntriesMergeCommand multipleTermEntriesMergeCommand) {
	_multipleTermEntriesMergeCommand = multipleTermEntriesMergeCommand;
    }


}
