package org.gs4tr.termmanager.service.model.command.dto;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.termmanager.service.model.command.SaveDashboardCommand;
import org.gs4tr.termmanager.service.model.command.SaveDashboardCommands;

public class DtoSaveDashboardCommands implements DtoTaskHandlerCommand<SaveDashboardCommands> {

    private DtoSaveDashboardCommand[] _saveDashboardCommands;

    @Override
    public SaveDashboardCommands convertToInternalTaskHandlerCommand() {
	List<SaveDashboardCommand> saveDashboardCommandList = new ArrayList<SaveDashboardCommand>();

	DtoSaveDashboardCommand[] dtoSaveDashboardCommandArray = getSaveDashboardCommands();

	if (dtoSaveDashboardCommandArray != null) {
	    for (DtoSaveDashboardCommand dtoSaveDashboardCommand : dtoSaveDashboardCommandArray) {
		SaveDashboardCommand saveDashboardCommand = dtoSaveDashboardCommand
			.convertToInternalTaskHandlerCommand();
		saveDashboardCommandList.add(saveDashboardCommand);
	    }
	}

	SaveDashboardCommands saveDashboardCommands = new SaveDashboardCommands();
	saveDashboardCommands.setSaveDashboardCommands(saveDashboardCommandList);

	return saveDashboardCommands;
    }

    public DtoSaveDashboardCommand[] getSaveDashboardCommands() {
	return _saveDashboardCommands;
    }

    public void setSaveDashboardCommands(DtoSaveDashboardCommand[] saveDashboardCommands) {
	_saveDashboardCommands = saveDashboardCommands;
    }
}
