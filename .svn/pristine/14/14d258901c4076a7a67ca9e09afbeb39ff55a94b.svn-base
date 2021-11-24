package org.gs4tr.termmanager.service.manualtask;

import java.util.List;

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.TaskPriority;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.SystemTask;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;

@SystemTask(priority = TaskPriority.LEVEL_SIX)
public class UnlockUserAccountTaskHandler extends AbstractManualTaskHandler {

    @Autowired
    private UserProfileService _userProfileService;

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.SINGLE_SELECT;
    }

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	Long userId = parentIds[0];
	UserProfileService userProfileService = getUserProfileService();

	TmUserProfile userProfile = userProfileService.load(userId);
	UserInfo userInfo = userProfile.getUserInfo();

	if (!userInfo.isAccountNonLocked()) {
	    userProfileService.unlockUserAccount(userId);
	} else {
	    String message = String.format(MessageResolver.getMessage("UnlockUserAccountTaskHandler.0"), //$NON-NLS-1$
		    userInfo.getUserName());
	    throw new UserException(message, message);
	}

	return new TaskResponse(null);
    }

}
