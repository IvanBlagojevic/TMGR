package org.gs4tr.termmanager.service.model.command;

public class PasswordCommand {

    private String _newPassword;

    private String _oldPassword;

    public PasswordCommand(String oldPassword, String newPassword) {
	super();
	_oldPassword = oldPassword;
	_newPassword = newPassword;
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
