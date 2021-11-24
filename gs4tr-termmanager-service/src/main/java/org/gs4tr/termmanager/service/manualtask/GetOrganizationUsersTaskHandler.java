package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.Language;
import org.gs4tr.termmanager.model.dto.converter.LanguageConverter;
import org.gs4tr.termmanager.service.OrganizationService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.model.command.GetOrganizationUsersCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoGetOrganizationUsersCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class GetOrganizationUsersTaskHandler extends AbstractManualTaskHandler {

    private static final String USER_KEY = "users";

    private static final String USER_LANGUAGES_KEY = "languages";

    private static final String USER_NAME_KEY = "userName";

    private static final String USER_TICKET_KEY = "userTicket";

    @Autowired
    public OrganizationService _organizationService;

    @Autowired
    public UserProfileService _userProfileService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoGetOrganizationUsersCommand.class;
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

	Long organizationId = parentIds[0];

	TmOrganization organization = getOrganizationService().findById(organizationId);

	GetOrganizationUsersCommand getOrganizationUsersCommand = (GetOrganizationUsersCommand) command;

	boolean showGenericUsers = getOrganizationUsersCommand.isShowGenericUsers();

	List<TmUserProfile> organizationUsers = getUserProfileService()
		.findUsersByOrganizationFetchLanguages(organization, showGenericUsers);

	TaskModel taskModel = new TaskModel();

	List<Object> userData = new ArrayList<Object>();

	Language[] projectLanguages = LanguageConverter
		.fromInternalToDto(getOrganizationUsersCommand.getProjectLanguages());

	for (TmUserProfile user : organizationUsers) {
	    Map<String, Object> userMap = new LinkedHashMap<String, Object>();

	    Long userId = user.getUserProfileId();

	    userMap.put(USER_NAME_KEY, user.getUserInfo().getUserName());

	    userMap.put(USER_TICKET_KEY, new Ticket(userId));

	    userMap.put(USER_LANGUAGES_KEY, projectLanguages);

	    userData.add(userMap);
	}

	taskModel.addObject(USER_KEY, userData);

	return new TaskModel[] { taskModel };
    }

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	return null;
    }
}
