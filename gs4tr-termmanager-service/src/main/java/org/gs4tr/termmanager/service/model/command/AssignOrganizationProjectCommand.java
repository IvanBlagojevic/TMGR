package org.gs4tr.termmanager.service.model.command;

public class AssignOrganizationProjectCommand {

    private Long _organizationId;

    private Long[] _projectIds;

    public Long getOrganizationId() {
	return _organizationId;
    }

    public Long[] getProjectIds() {
	return _projectIds;
    }

    public void setOrganizationId(Long organizationId) {
	_organizationId = organizationId;
    }

    public void setProjectIds(Long[] projectIds) {
	_projectIds = projectIds;
    }

}
