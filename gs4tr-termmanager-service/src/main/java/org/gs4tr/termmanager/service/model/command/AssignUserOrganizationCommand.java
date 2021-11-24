package org.gs4tr.termmanager.service.model.command;

public class AssignUserOrganizationCommand {

    private Long _organizationId;

    private Long _userId;

    public Long getOrganizationId() {
	return _organizationId;
    }

    public Long getUserId() {
	return _userId;
    }

    public void setOrganizationId(Long organizationId) {
	_organizationId = organizationId;
    }

    public void setUserId(Long userId) {
	_userId = userId;
    }

}
