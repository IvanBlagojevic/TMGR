package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.OrganizationInfo;
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
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class EditOrganizationTaskHandler extends AbstractManualTaskHandler {

    private static final String ORGANIZATION_NAME_KEY = "organizationName"; //$NON-NLS-1$

    private static final String ORGANIZATION_TICKET = "ticket"; //$NON-NLS-1$

    private static final String ORGANIZATIONS = "organizations"; //$NON-NLS-1$

    private static final String PARENT_ORGANIZATION_TICKET_KEY = "parentOrganizationTicket"; //$NON-NLS-1$

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
	return SelectStyleEnum.SINGLE_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {

	Long id = parentIds[0];

	List<TmOrganization> allOrganizations = getOrganizationService().findAllWithDependants();

	TmOrganization organization = ServiceUtils.findOrganizationById(allOrganizations, id);

	OrganizationInfo organizationInfo = organization.getOrganizationInfo();
	if (!organizationInfo.isEnabled()) {
	    String message = MessageResolver.getMessage("EditOrganizationTaskHandler.0");//$NON-NLS-1$
	    throw new UserException(message, MessageResolver.getMessage("EditOrganizationTaskHandler.3")); //$NON-NLS-1$
	}

	TaskModel newTaskModel = new TaskModel(null, new Ticket(id));// organization.getTicket()

	newTaskModel.addObject(ORGANIZATION_NAME_KEY, organizationInfo.getName());

	Ticket parentOrganizationTicket = null;

	TmOrganization parentOrganization = organization.getParentOrganization();

	if (parentOrganization != null) {

	    parentOrganizationTicket = new Ticket(parentOrganization.getOrganizationId());

	    // allOrganizations.remove(parentOrganization);
	}

	allOrganizations.remove(organization);

	removeChildOrganizations(organization, allOrganizations);

	newTaskModel.addObject(PARENT_ORGANIZATION_TICKET_KEY, parentOrganizationTicket);

	List<Object> organizationsList = new ArrayList<Object>();

	for (TmOrganization allOrganization : allOrganizations) {
	    OrganizationInfo allOrganizationInfo = allOrganization.getOrganizationInfo();
	    if (allOrganizationInfo.isEnabled()) {
		Map<String, Object> organizationMap = new LinkedHashMap<String, Object>();

		organizationMap.put(ORGANIZATION_NAME_KEY, allOrganizationInfo.getName());

		organizationMap.put(ORGANIZATION_TICKET, new Ticket(allOrganization.getOrganizationId()));

		organizationsList.add(organizationMap);
	    }

	}

	newTaskModel.addObject(ORGANIZATIONS, organizationsList);

	return new TaskModel[] { newTaskModel };
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> items) {

	OrganizationCommand editCommand = (OrganizationCommand) command;

	String organizationName = editCommand.getOrganizationInfo().getName();

	if (organizationName.length() > 50) {
	    String message = MessageResolver.getMessage("EditOrganizationTaskHandler.1");//$NON-NLS-1$
	    throw new UserException(message, MessageResolver.getMessage("EditOrganizationTaskHandler.4")); //$NON-NLS-1$
	}

	TmOrganization existingOrganization = getOrganizationService().findByName(organizationName);
	TmOrganization oldOrganization = getOrganizationService().findById(editCommand.getOrganizationId());
	if (existingOrganization != null
		&& !existingOrganization.getOrganizationId().equals(editCommand.getOrganizationId())) {
	    throw new UserException(MessageResolver.getMessage("EditOrganizationTaskHandler.5"), //$NON-NLS-1$
		    String.format(MessageResolver.getMessage("EditOrganizationTaskHandler.7"), //$NON-NLS-1$
			    organizationName));
	}

	editCommand.getOrganizationInfo().setEnabled(oldOrganization.getOrganizationInfo().isEnabled());

	Long organizationId = getOrganizationService().updateOrganization(editCommand.getOrganizationId(),
		editCommand.getParentOrganizationId(), editCommand.getOrganizationInfo());

	return new TaskResponse(new Ticket(organizationId));

    }

    private void removeChildOrganizations(TmOrganization organization, List<TmOrganization> allOrganizations) {

	Set<TmOrganization> childOrganizations = organization.getChildOrganizations();

	if (CollectionUtils.isEmpty(childOrganizations)) {
	    return;
	}

	allOrganizations.removeAll(childOrganizations);

	for (TmOrganization childOrganization : childOrganizations) {
	    removeChildOrganizations(childOrganization, allOrganizations);
	}
    }

}
