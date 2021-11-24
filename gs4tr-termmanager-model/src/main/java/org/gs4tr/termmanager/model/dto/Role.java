package org.gs4tr.termmanager.model.dto;

public class Role {

    private Policy[] _policies;

    private String _roleId;

    private RoleTypeEnum _roleType;

    private Task[] _tasks;

    private String _ticket;

    public Policy[] getPolicies() {
	return _policies;
    }

    public String getRoleId() {
	return _roleId;
    }

    public RoleTypeEnum getRoleType() {
	return _roleType;
    }

    public Task[] getTasks() {
	return _tasks;
    }

    public String getTicket() {
	return _ticket;
    }

    public void setPolicies(Policy[] policies) {
	_policies = policies;
    }

    public void setRoleId(String roleId) {
	_roleId = roleId;
    }

    public void setRoleType(RoleTypeEnum roleType) {
	_roleType = roleType;
    }

    public void setTasks(Task[] tasks) {
	_tasks = tasks;
    }

    public void setTicket(String ticket) {
	_ticket = ticket;
    }

}
