package org.gs4tr.termmanager.model.dto;

public class UserProfile {

    private Integer _availableTasks;

    private boolean _generic = false;

    private String _organizationName;

    private Role[] _systemRoles;

    private Task[] _tasks;

    private String _ticket;

    private UserInfo _userInfo;

    public Integer getAvailableTasks() {
	return _availableTasks;
    }

    public String getOrganizationName() {
	return _organizationName;
    }

    public Role[] getSystemRoles() {
	return _systemRoles;
    }

    public Task[] getTasks() {
	return _tasks;
    }

    public String getTicket() {
	return _ticket;
    }

    public UserInfo getUserInfo() {
	return _userInfo;
    }

    public boolean isGeneric() {
	return _generic;
    }

    public void setAvailableTasks(Integer availableTasks) {
	_availableTasks = availableTasks;
    }

    public void setGeneric(boolean generic) {
	_generic = generic;
    }

    public void setOrganizationName(String organizationName) {
	_organizationName = organizationName;
    }

    public void setSystemRoles(Role[] roles) {
	_systemRoles = roles;
    }

    public void setTasks(Task[] tasks) {
	_tasks = tasks;
    }

    public void setTicket(String ticket) {
	_ticket = ticket;
    }

    public void setUserInfo(UserInfo userInfo) {
	_userInfo = userInfo;
    }
}
