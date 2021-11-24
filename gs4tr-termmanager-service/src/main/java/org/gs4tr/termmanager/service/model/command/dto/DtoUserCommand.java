package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Objects;

import org.gs4tr.termmanager.service.model.command.UserCommand;

public class DtoUserCommand implements DtoTaskHandlerCommand<UserCommand> {

    private boolean _createLdapUser;

    private DtoUserInfoCommand _userInfo;

    public UserCommand convertToInternalTaskHandlerCommand() {

	UserCommand userCommand = new UserCommand();

	DtoUserInfoCommand userInfo = getUserInfo();
	if (Objects.nonNull(userInfo)) {
	    userCommand.setUserInfo(userInfo.convertToInternalTaskHandlerCommand());
	}

	userCommand.setCreateLdapUser(isCreateLdapUser());

	return userCommand;
    }

    public DtoUserInfoCommand getUserInfo() {
	return _userInfo;
    }

    public boolean isCreateLdapUser() {
	return _createLdapUser;
    }

    public void setCreateLdapUser(boolean createLdapUser) {
	_createLdapUser = createLdapUser;
    }

    public void setUserInfo(DtoUserInfoCommand userInfo) {
	_userInfo = userInfo;
    }
}
