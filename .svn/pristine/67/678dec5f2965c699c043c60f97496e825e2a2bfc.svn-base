package org.gs4tr.termmanager.service.schadule;

import java.util.Date;

import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.AbstractSpringServiceTests;
import org.gs4tr.termmanager.service.schedule.UserProfileInactivityCheckHandler;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/*TERII-5907 | Admin dashboard status is not updated when user is auto disabled for inactive users*/
public class UserProfileInactivityCheckHandlerTest extends AbstractSpringServiceTests {

    @Autowired
    private UserProfileInactivityCheckHandler _userProfileInactivityCheckHandler;

    @Test
    public void disableInactiveUserTest() {

	Long powerId = 8L;

	TmUserProfile powerUser = getUserProfileService().findById(powerId);
	powerUser.getUserInfo().setDateLastLogin(new Date(0L));
	getUserProfileService().update(powerUser);

	// Disable inactive users
	getUserProfileInactivityCheckHandler().disableInactiveUsers();

	TmUserProfile regularUserAfter = getUserProfileService().findById(powerId);

	// Power user should be disabled
	Assert.assertFalse(regularUserAfter.getUserInfo().isEnabled());
    }

    @Test
    public void disableInactiveUsersNoUsersForDisable() {
	Long powerId = 8L;

	// Disable users
	getUserProfileInactivityCheckHandler().disableInactiveUsers();

	TmUserProfile regularUserAfter = getUserProfileService().findById(powerId);

	// User should not be disabled because of last login date
	Assert.assertTrue(regularUserAfter.getUserInfo().isEnabled());
    }

    @Test
    @Ignore
    public void disableInactiveUsersTest() {

	Long adminId = 1L;
	Long systemId = 2L;
	Long powerId = 8L;

	TmUserProfile admin = getUserProfileService().findById(adminId);
	admin.getUserInfo().setDateLastLogin(new Date(0L));
	getUserProfileService().update(admin);

	TmUserProfile systemUser = getUserProfileService().findById(systemId);
	systemUser.getUserInfo().setDateLastLogin(new Date(0L));
	getUserProfileService().update(systemUser);

	TmUserProfile regularUser = getUserProfileService().findById(powerId);
	regularUser.getUserInfo().setDateLastLogin(new Date(0L));
	getUserProfileService().update(regularUser);

	// Disable users
	getUserProfileInactivityCheckHandler().disableInactiveUsers();

	TmUserProfile adminAfter = getUserProfileService().findById(adminId);
	TmUserProfile systemUserAfter = getUserProfileService().findById(systemId);
	TmUserProfile regularUserAfter = getUserProfileService().findById(powerId);

	// Admin should not be disabled
	Assert.assertTrue(adminAfter.getUserInfo().isEnabled());

	// System user should not be disabled
	Assert.assertTrue(systemUserAfter.getUserInfo().isEnabled());

	// Regular user should be disabled
	Assert.assertFalse(regularUserAfter.getUserInfo().isEnabled());
    }

    private UserProfileInactivityCheckHandler getUserProfileInactivityCheckHandler() {
	return _userProfileInactivityCheckHandler;
    }
}
