package org.gs4tr.termmanager.service.manualtask;

import java.util.List;

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;

public class ClearUserMetadataTaskHandler extends AbstractManualTaskHandler {

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

	getUserProfileService().clearUserMetadata(TmUserProfile.getCurrentUserName());

	return new TaskResponse(null);
    }

    public void setUserProfileService(UserProfileService userProfileService) {
	_userProfileService = userProfileService;
    }

}
