package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.termmanager.service.model.command.ApproveCommand;

public class DtoApproveCommand implements DtoTaskHandlerCommand<ApproveCommand> {

    private boolean _approveTerm = false;

    @Override
    public ApproveCommand convertToInternalTaskHandlerCommand() {
	ApproveCommand command = new ApproveCommand();
	command.setApproveTerm(isApproveTerm());
	return command;
    }

    public boolean isApproveTerm() {
	return _approveTerm;
    }

    public void setApproveTerm(boolean approveTerm) {
	_approveTerm = approveTerm;
    }

}
