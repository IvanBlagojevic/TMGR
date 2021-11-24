package org.gs4tr.termmanager.service.manualtask;

import java.util.List;

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;

public class EnableOrganizationTaskHandler extends AbstractManualTaskHandler {

    @Autowired
    private OrganizationService _organizationService;

    public OrganizationService getOrganizationService() {
	return _organizationService;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.SINGLE_SELECT;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {

	Long organizationId = parentIds[0];
	if (organizationId == null) {
	    throw new RuntimeException(Messages.getString("EnableOrganizationTaskHandler.0")); //$NON-NLS-1$
	}
	TmOrganization organization = getOrganizationService().findById(organizationId);
	if (organization == null) {
	    throw new RuntimeException(Messages.getString("EnableOrganizationTaskHandler.1")); //$NON-NLS-1$
	}
	if (organization.getOrganizationInfo().isEnabled()) {
	    getOrganizationService().enableOrganizationUsers(organization.getOrganizationId(), false);
	} else {
	    getOrganizationService().enableOrganizationUsers(organization.getOrganizationId(), true);
	}

	return new TaskResponse(null);
    }
}