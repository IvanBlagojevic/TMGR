package org.gs4tr.termmanager.dao.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.search.UserProfileSearchRequest;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.junit.Test;

public class UserProfileSearchDAOTest extends AbstractSpringDAOIntegrationTest {

    @Test
    public void findUserByEmailAddress() {

	String email = "foo@yahoo.com";

	UserProfileSearchRequest request = new UserProfileSearchRequest();
	request.setEmailAddress(email);

	PagedList<TmUserProfile> userProfiles = getUserSearchDAO().getEntityPagedList(request, new PagedListInfo());

	assertNotNull(userProfiles);
	assertEquals(userProfiles.getElements().length, 1);

	TmUserProfile[] users = userProfiles.getElements();
	TmUserProfile matchedProfile = users[0];
	assertNotNull(matchedProfile);
	assertEquals(matchedProfile.getUserInfo().getEmailAddress(), email);

    }

}
