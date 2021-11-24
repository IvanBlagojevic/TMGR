package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.termmanager.service.model.command.PasswordCommand;

public class DtoPasswordCommand implements DtoTaskHandlerCommand<PasswordCommand> {

    private String _oldPassword;

    private String _newPassword;

    @Override
    public PasswordCommand convertToInternalTaskHandlerCommand() {

	PasswordCommand command = new PasswordCommand(getOldPassword(), getNewPassword());
	return command;
    }

    public String getNewPassword() {
	return _newPassword;
    }

    public String getOldPassword() {
	return _oldPassword;
    }

    public void setNewPassword(String newPassword) {
	_newPassword = newPassword;
    }

    public void setOldPassword(String oldPassword) {
	_oldPassword = oldPassword;
    }

}
