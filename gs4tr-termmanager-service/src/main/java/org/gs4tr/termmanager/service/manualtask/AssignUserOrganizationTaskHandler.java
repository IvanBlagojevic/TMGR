package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.OrganizationService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.model.command.AssignUserOrganizationCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoAssignUserOrganizationCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class AssignUserOrganizationTaskHandler extends AbstractManualTaskHandler {

    private static final String SELECTED_KEY = "selected";

    private static final String TICKET_KEY = "ticket";

    private static final String ORGANIZATIONS_KEY = "organizations";

    private static final String ORGANIZATION_NAME_KEY = "organizationName";

    @Autowired
    private OrganizationService _organizationService;

    @Autowired
    private UserProfileService _userProfileService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoAssignUserOrganizationCommand.class;
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

	Long id = parentIds[0];

	TaskModel model = new TaskModel();
	TmUserProfile userProfile = getUserProfileService().load(id);

	List<TmOrganization> organizations = getOrganizationService().findAllOrganizations();
	CollectionUtils.filter(organizations, new Predicate() {
	    @Override
	    public boolean evaluate(Object item) {
		TmOrganization organization = (TmOrganization) item;
		return organization.getOrganizationInfo().isEnabled();
	    }
	});

	TmOrganization userOrganization = userProfile.getOrganization();
	if (userOrganization != null) {
	    Long userOrganizationId = userOrganization.getOrganizationId();
	    model.addObject(SELECTED_KEY, TicketConverter.fromInternalToDto(userOrganizationId));
	}

	List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

	for (TmOrganization organization : organizations) {

	    Map<String, Object> map = new HashMap<String, Object>();

	    map.put(ORGANIZATION_NAME_KEY, organization.getOrganizationInfo().getName());
	    map.put(TICKET_KEY, new Ticket(organization.getOrganizationId()));

	    mapList.add(map);

	}

	model.addObject(ORGANIZATIONS_KEY, mapList);

	return new TaskModel[] { model };
    }

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    public Boolean isVisible() {
	return Boolean.FALSE;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {

	AssignUserOrganizationCommand addCommand = (AssignUserOrganizationCommand) command;
	Long userId = getOrganizationService().assignOrganizationToUser(addCommand.getOrganizationId(),
		addCommand.getUserId());

	return new TaskResponse(new Ticket(userId));

    }
}
