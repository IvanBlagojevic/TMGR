package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.OrganizationService;
import org.gs4tr.termmanager.service.model.command.OrganizationCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoOrganizationCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class AddOrganizationTaskHandler extends AbstractManualTaskHandler {

    private static final String ORGANIZATION_NAME = "organizationName"; //$NON-NLS-1$

    private static final String ORGANIZATION_TICKET = "ticket"; //$NON-NLS-1$

    private static final String ORGANIZATIONS = "organizations"; //$NON-NLS-1$

    @Autowired
    private OrganizationService _organizationService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoOrganizationCommand.class;
    }

    public OrganizationService getOrganizationService() {
	return _organizationService;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {

	TaskModel newTaskModel = new TaskModel();

	List<TmOrganization> organizations = getOrganizationService().findAllOrganizations();

	List<Object> organizationsList = new ArrayList<Object>();

	for (TmOrganization organization : organizations) {
	    if (organization.getOrganizationInfo().isEnabled()) {
		Map<String, Object> organizationMap = new LinkedHashMap<String, Object>();

		organizationMap.put(ORGANIZATION_NAME, organization.getOrganizationInfo().getName());

		organizationMap.put(ORGANIZATION_TICKET, new Ticket(organization.getOrganizationId()));

		organizationsList.add(organizationMap);
	    }
	}

	newTaskModel.addObject(ORGANIZATIONS, organizationsList);

	return new TaskModel[] { newTaskModel };
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> items) {

	OrganizationService organizationService = getOrganizationService();

	OrganizationCommand addCommand = (OrganizationCommand) command;
	String organizationName = addCommand.getOrganizationInfo().getName();

	if (organizationName.length() > 50) {
	    throw new UserException(MessageResolver.getMessage("AddOrganizationTaskHandler.0"), //$NON-NLS-1$
		    MessageResolver.getMessage("AddOrganizationTaskHandler.2")); //$NON-NLS-1$
	}

	TmOrganization org = organizationService.findByName(organizationName);
	if (org != null) {
	    String description = String.format(MessageResolver.getMessage("AddOrganizationTaskHandler.3"), //$NON-NLS-1$
		    organizationName);
	    throw new UserException(MessageResolver.getMessage("AddOrganizationTaskHandler.4"), //$NON-NLS-1$
		    description);
	}

	Long organizationId = organizationService.createOrganization(addCommand.getOrganizationInfo(),
		addCommand.getParentOrganizationId());

	return new TaskResponse(new Ticket(organizationId));

    }
}
