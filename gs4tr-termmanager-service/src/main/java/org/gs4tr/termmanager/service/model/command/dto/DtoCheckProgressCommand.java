package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.termmanager.service.model.command.CheckProgressCommand;

public class DtoCheckProgressCommand implements DtoTaskHandlerCommand<CheckProgressCommand> {

    private String _threadName;

    @Override
    public CheckProgressCommand convertToInternalTaskHandlerCommand() {
	CheckProgressCommand checkImportProgressCommand = new CheckProgressCommand();
	checkImportProgressCommand.setThreadName(getThreadName());
	return checkImportProgressCommand;
    }

    public String getThreadName() {
	return _threadName;
    }

    public void setThreadName(String threadName) {
	_threadName = threadName;
    }

}
