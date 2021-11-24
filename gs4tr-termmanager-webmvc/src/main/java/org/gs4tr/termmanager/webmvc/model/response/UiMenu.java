package org.gs4tr.termmanager.webmvc.model.response;

import java.util.List;

public class UiMenu {

    private List<UiMenuItem> _adminMenu;

    private List<UiMenuItem> _userMenu;

    public List<UiMenuItem> getAdminMenu() {
	return _adminMenu;
    }

    public List<UiMenuItem> getUserMenu() {
	return _userMenu;
    }

    public void setAdminMenu(List<UiMenuItem> adminMenu) {
	_adminMenu = adminMenu;
    }

    public void setUserMenu(List<UiMenuItem> userMenu) {
	_userMenu = userMenu;
    }
}
