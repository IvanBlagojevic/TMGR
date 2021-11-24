package org.gs4tr.termmanager.service.model.command;

import java.util.List;

public class AssignProjectUserLanguageCommand {

    private Long _projectId;

    private boolean _showGenericUsers = false;

    private List<UserLanguageCommand> _users;

    public Long getProjectId() {
	return _projectId;
    }

    public List<UserLanguageCommand> getUsers() {
	return _users;
    }

    public boolean isShowGenericUsers() {
	return _showGenericUsers;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }

    public void setShowGenericUsers(boolean showGenericUsers) {
	_showGenericUsers = showGenericUsers;
    }

    public void setUsers(List<UserLanguageCommand> users) {
	_users = users;
    }
}
