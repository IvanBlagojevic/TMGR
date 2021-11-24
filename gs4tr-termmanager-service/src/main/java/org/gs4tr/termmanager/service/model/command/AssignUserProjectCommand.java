package org.gs4tr.termmanager.service.model.command;

public class AssignUserProjectCommand {

    private Long[] _projectIds;

    private Long _userId;

    public Long[] getProjectIds() {
	return _projectIds;
    }

    public Long getUserId() {
	return _userId;
    }

    public void setProjectIds(Long[] projectIds) {
	_projectIds = projectIds;
    }

    public void setUserId(Long userId) {
	_userId = userId;
    }

}
