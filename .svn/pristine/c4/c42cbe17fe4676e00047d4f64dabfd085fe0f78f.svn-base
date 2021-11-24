package org.gs4tr.termmanager.service.manualtask;

import java.util.List;

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.model.command.PasswordCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoPasswordCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class ChangeUserPasswordTaskHandler extends AbstractManualTaskHandler {

    @Autowired
    private UserProfileService _userProfileService;

    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoPasswordCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.SINGLE_SELECT;
    }

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    @Override
    public TaskResponse processTasks(Long[] projectIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {

	PasswordCommand passwordCommand = (PasswordCommand) command;

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();
	UserInfo userInfo = userProfile.getUserInfo();
	String userName = userInfo.getUserName();

	String newPassword = passwordCommand.getNewPassword();
	String oldPassword = passwordCommand.getOldPassword();

	if (ManualTaskHandlerUtils.supportsLdap()
		&& ManualTaskHandlerUtils.getLdapUserHandler().findUser(userName) != null) {

	    ManualTaskHandlerUtils.updateLdapPassword(userName, oldPassword, newPassword, userInfo);
	} else {
	    getUserProfileService().changeCurrentUserPassword(oldPassword, newPassword);
	}

	return new TaskResponse(null);
    }
}