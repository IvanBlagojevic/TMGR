package org.gs4tr.termmanager.service.manualtask;

import java.util.List;

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.CacheGatewaySessionUpdaterService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;

public class EnableUserTaskHandler extends AbstractManualTaskHandler {

    @Autowired
    private CacheGatewaySessionUpdaterService _cacheGatewaySessionUpdaterService;

    @Autowired
    private UserProfileService _userProfileService;

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.MULTI_SELECT;
    }

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {

	UserProfileService userProfileService = getUserProfileService();
	for (Long userId : parentIds) {
	    TmUserProfile userProfile = userProfileService.load(userId);

	    UserInfo userInfo = userProfile.getUserInfo();
	    if (userInfo.isEnabled()) {
		userProfile.getUserInfo().setEnabled(false);
	    } else {
		userProfile.getUserInfo().setEnabled(true);
	    }

	    userProfileService.switchUserProfile(userId, userProfile.getUserInfo());

	    if (!userInfo.isEnabled()) {
		getCacheGatewaySessionUpdaterService().removeOnDisableUser(userProfile);
	    }
	}
	return new TaskResponse(null);

    }

    private CacheGatewaySessionUpdaterService getCacheGatewaySessionUpdaterService() {
	return _cacheGatewaySessionUpdaterService;
    }

}
