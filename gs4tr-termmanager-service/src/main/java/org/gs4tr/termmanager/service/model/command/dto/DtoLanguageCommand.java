package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.dto.Language;
import org.gs4tr.termmanager.model.dto.UserInfo;

public class DtoLanguageCommand {

    private boolean _genericUser = false;

    private String _roleId;

    private UserInfo _userInfo;

    private Language[] _userLanguages;

    private Ticket _userTicket;

    public String getRoleId() {
	return _roleId;
    }

    public UserInfo getUserInfo() {
	return _userInfo;
    }

    public Language[] getUserLanguages() {
	return _userLanguages;
    }

    public Ticket getUserTicket() {
	return _userTicket;
    }

    public boolean isGenericUser() {
	return _genericUser;
    }

    public void setGenericUser(boolean genericUser) {
	_genericUser = genericUser;
    }

    public void setRoleId(String roleId) {
	_roleId = roleId;
    }

    public void setUserInfo(UserInfo userInfo) {
	_userInfo = userInfo;
    }

    public void setUserLanguages(Language[] languages) {
	_userLanguages = languages;
    }

    public void setUserTicket(Ticket userTicket) {
	_userTicket = userTicket;
    }

}
