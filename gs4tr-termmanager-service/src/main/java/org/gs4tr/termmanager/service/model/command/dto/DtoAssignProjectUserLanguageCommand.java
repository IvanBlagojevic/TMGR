package org.gs4tr.termmanager.service.model.command.dto;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.termmanager.model.dto.UserInfo;
import org.gs4tr.termmanager.model.dto.converter.LanguageConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.dto.converter.UserInfoConverter;
import org.gs4tr.termmanager.service.model.command.AssignProjectUserLanguageCommand;
import org.gs4tr.termmanager.service.model.command.UserLanguageCommand;

public class DtoAssignProjectUserLanguageCommand implements DtoTaskHandlerCommand<AssignProjectUserLanguageCommand> {

    private Ticket _projectTicket;

    private DtoLanguageCommand[] _users;

    private boolean _showGenericUsers = false;

    @Override
    public AssignProjectUserLanguageCommand convertToInternalTaskHandlerCommand() {

	AssignProjectUserLanguageCommand assignProjectUserLanguageCommand = new AssignProjectUserLanguageCommand();

	List<UserLanguageCommand> userLanguageCommands = new ArrayList<UserLanguageCommand>();

	DtoLanguageCommand[] users = getUsers();
	if (users != null) {
	    for (DtoLanguageCommand userLanguage : users) {
		UserLanguageCommand command = new UserLanguageCommand();

		List<String> languages = LanguageConverter.fromDtoToLanguageCode(userLanguage.getUserLanguages());

		command.setUserLanguages(languages);
		command.setRoleId(TicketConverter.fromDtoToInternal(userLanguage.getRoleId(), String.class));
		command.setUserId(TicketConverter.fromDtoToInternal(userLanguage.getUserTicket(), Long.class));

		boolean genericUser = userLanguage.isGenericUser();
		command.setGenericUser(genericUser);

		UserInfo userInfo = userLanguage.getUserInfo();
		if (genericUser && userInfo != null) {
		    userInfo.setEmailNotification(Boolean.FALSE);
		    userInfo.setUserType(UserTypeEnum.ORGANIZATION.name());

		    command.setUserInfo(UserInfoConverter.fromDtoToInternal(userInfo));
		}

		userLanguageCommands.add(command);
	    }
	}

	assignProjectUserLanguageCommand.setUsers(userLanguageCommands);

	assignProjectUserLanguageCommand
		.setProjectId(TicketConverter.fromDtoToInternal(getProjectTicket(), Long.class));

	assignProjectUserLanguageCommand.setShowGenericUsers(isShowGenericUsers());

	return assignProjectUserLanguageCommand;
    }

    public boolean isShowGenericUsers() {
	return _showGenericUsers;
    }

    public void setShowGenericUsers(boolean showGenericUsers) {
	_showGenericUsers = showGenericUsers;
    }

    public Ticket getProjectTicket() {
	return _projectTicket;
    }

    public DtoLanguageCommand[] getUsers() {
	return _users;
    }

    public void setProjectTicket(Ticket projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setUsers(DtoLanguageCommand[] users) {
	_users = users;
    }

}
