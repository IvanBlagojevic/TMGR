package org.gs4tr.termmanager.service.schedule;

import static org.gs4tr.foundation.modules.webmvc.logging.util.EventLoggingActionConstants.CATEGORY_UI_SECURITY;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.eventlogging.api.Event;
import org.gs4tr.eventlogging.api.service.EventLoggingService;
import org.gs4tr.foundation.modules.entities.model.Messages;
import org.gs4tr.foundation.modules.entities.model.SecurityLogHelper;
import org.gs4tr.foundation.modules.usermanager.model.SystemAuthenticationHolder;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.foundation.modules.webmvc.logging.util.EventLoggingConstants;
import org.gs4tr.termmanager.dao.provider.UserIdLoadProvider;
import org.gs4tr.termmanager.dao.provider.UserLoadProvider;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(value = "userProfileInactivityCheckHandler")
public class UserProfileInactivityCheckHandler {

    private static final String ACTION_STATUS_FORBIDDEN_DISABLED_USER_LOGING = "forbidden disabled user form login";

    private static final String ADMIN_USERNAME = "admin";

    private static final String AUTO_DISABLE_INACTIVE_USERS = "auto disabling inactive user";

    private static final String DISABLED_ACCOUNT = "Disabled account: %s due to inactivity";

    private static final long ONE_DAY = 86400000L;

    private static final String SYSTEM_USER_USERNAME = "system";

    private static final Log _logger = LogFactory.getLog(UserProfileInactivityCheckHandler.class);

    @Autowired
    private EventLoggingService _eventLoggingService;

    @Value("${includeSsoUsersToInactivityCheck:true}")
    private boolean _includeSsoUsersToInactivityCheck;

    @Autowired
    private SessionService _sessionService;

    @Value("${maxUnusedAccountTime:0}")
    private int _unusedAccountDaysLimit;

    @Autowired
    private UserProfileService _userProfileService;

    public void disableInactiveUsers() {
	if (SystemAuthenticationHolder.getSystemAuthentication() != null) {
	    getSessionService().authenticateSystemUser();
	}
	if (getUnusedAccountDaysLimit() > 0) {
	    _logger.info(Messages.getString("UserProfileInactivityCheckHandler.1"));
	    List<Long> inactiveUserProfileIds = getUserProfileIdsForDeactivation();
	    if (CollectionUtils.isNotEmpty(inactiveUserProfileIds)) {
		logDeactivationOfUsers(inactiveUserProfileIds);
		getUserProfileService().disableUserProfiles(inactiveUserProfileIds);
	    }
	    _logger.info(String.format(Messages.getString("UserProfileInactivityCheckHandler.2"),
		    inactiveUserProfileIds.size()));
	}
    }

    public EventLoggingService getEventLoggingService() {
	return _eventLoggingService;
    }

    public int getUnusedAccountDaysLimit() {
	return _unusedAccountDaysLimit;
    }

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    private SessionService getSessionService() {
	return _sessionService;
    }

    private Date getTresholdDate() {
	long currentDate = new Date().getTime();
	long tresholdTimeStamp = currentDate - Long.valueOf(getUnusedAccountDaysLimit()) * ONE_DAY;
	return new Date(tresholdTimeStamp);
    }

    private List<Long> getUserProfileIdsForDeactivation() {
	List<Long> userIds;

	if (includeSsoUsersToInactivityCheck()) {
	    userIds = getUserProfileService()
		    .findUserProfileIdsByCriteria(UserIdLoadProvider.create().dateLastLoginBefore(getTresholdDate())
			    .enabled(Boolean.TRUE).excludedUserNames(ADMIN_USERNAME, SYSTEM_USER_USERNAME));

	} else {
	    userIds = getUserProfileService().findUserProfileIdsByCriteria(
		    UserIdLoadProvider.create().dateLastLoginBefore(getTresholdDate()).enabled(Boolean.TRUE)
			    .excludedUserNames(ADMIN_USERNAME, SYSTEM_USER_USERNAME).isSsoUser(Boolean.FALSE));
	}
	return userIds;
    }

    private boolean includeSsoUsersToInactivityCheck() {
	return _includeSsoUsersToInactivityCheck;
    }

    private void logDeactivationOfUsers(List<Long> inactiveUserProfileIds) {
	List<TmUserProfile> userProfiles = getUserProfileService()
		.findUserProfilesByCriteria(UserLoadProvider.create().userId(inactiveUserProfileIds));
	for (TmUserProfile userProfile : userProfiles) {
	    SecurityLogHelper.logEvent(String.format(Messages.getString("UserProfileInactivityCheckHandler.3"),
		    userProfile.getUserInfo().getUserName()));

	    getEventLoggingService().log(new Event(AUTO_DISABLE_INACTIVE_USERS, CATEGORY_UI_SECURITY)
		    .addProperty(EventLoggingConstants.ACTION_STATUS, ACTION_STATUS_FORBIDDEN_DISABLED_USER_LOGING)
		    .addProperty(EventLoggingConstants.SECURITY_MESSAGE,
			    String.format(DISABLED_ACCOUNT, userProfile.getUserInfo().getUserName())));

	}
    }
}