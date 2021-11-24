package org.gs4tr.termmanager.service.model.command;

public class UserCommand {

    private boolean _createLdapUser;
    
    private UserInfoCommand _userInfoCommand;

    public UserInfoCommand getUserInfoCommand() {
	return _userInfoCommand;
    }

    public boolean isCreateLdapUser() {
	return _createLdapUser;
    }

    public void setCreateLdapUser(boolean createLdapUser) {
	_createLdapUser = createLdapUser;
    }

    public void setUserInfo(UserInfoCommand userInfoCommand) {
	_userInfoCommand = userInfoCommand;
    }
}
