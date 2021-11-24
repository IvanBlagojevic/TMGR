package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.termmanager.service.model.command.GenerateReportCommand;

public class DtoGenerateReportCommand implements DtoTaskHandlerCommand<GenerateReportCommand> {

    private boolean _groupByLanguage = false;

    @Override
    public GenerateReportCommand convertToInternalTaskHandlerCommand() {
	GenerateReportCommand command = new GenerateReportCommand();
	command.setGroupByLanguage(isGroupByLanguage());
	return command;
    }

    public boolean isGroupByLanguage() {
	return _groupByLanguage;
    }

    public void setGroupByLanguage(boolean groupByLanguage) {
	_groupByLanguage = groupByLanguage;
    }
}
