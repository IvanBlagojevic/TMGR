package org.gs4tr.termmanager.dao.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.search.UserProfileSearchRequest;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.junit.Test;

public class UserSearchDAOHibernateIntegrationTest extends AbstractSpringDAOIntegrationTest {

    public static final String UNEXISTENT_EN_US_1 = "jno";

    public static final String UNEXISTENT_EN_US_2 = "en_us";

    @Test
    public void testSearch1() {
	UserProfileSearchRequest userProfileSearchRequest = new UserProfileSearchRequest();
	userProfileSearchRequest.setUsername("bob_jones");

	PagedList<TmUserProfile> userList = getUserSearchDAO().getEntityPagedList(userProfileSearchRequest, null);

	assertNotNull(userList);

	TmUserProfile[] elements = userList.getElements();

	assertEquals(elements.length, 1);
    }

    @Test
    public void testSearch2() {
	UserProfileSearchRequest userProfileSearchRequest = new UserProfileSearchRequest();
	userProfileSearchRequest.setFirstname("no exist");

	PagedList<TmUserProfile> userList = getUserSearchDAO().getEntityPagedList(userProfileSearchRequest, null);

	assertNotNull(userList);

	TmUserProfile[] elements = userList.getElements();

	assertEquals(elements.length, 0);
    }

    @Test
    public void testSearch3() {
	UserProfileSearchRequest userProfileSearchRequest = new UserProfileSearchRequest();

	PagedList<TmUserProfile> userList = getUserSearchDAO().getEntityPagedList(userProfileSearchRequest, null);

	assertNotNull(userList);

	TmUserProfile[] elements = userList.getElements();

	assertEquals(elements.length, 2);
    }

    @Test
    public void testSearch4() {
	UserProfileSearchRequest userProfileSearchRequest = new UserProfileSearchRequest();
	userProfileSearchRequest.setFirstname("John");

	PagedList<TmUserProfile> userList = getUserSearchDAO().getEntityPagedList(userProfileSearchRequest, null);

	assertNotNull(userList);

	TmUserProfile[] elements = userList.getElements();

	assertEquals(elements.length, 1);
    }

    @Test
    public void testSearch5() {
	UserProfileSearchRequest userProfileSearchRequest = new UserProfileSearchRequest();
	userProfileSearchRequest.setLastname("");

	PagedList<TmUserProfile> userList = getUserSearchDAO().getEntityPagedList(userProfileSearchRequest, null);

	assertNotNull(userList);

	TmUserProfile[] elements = userList.getElements();

	assertEquals(2, elements.length);
    }

    @Test
    public void testSearch6() {
	UserProfileSearchRequest userProfileSearchRequest = new UserProfileSearchRequest();
	userProfileSearchRequest.setFirstname("Bob");
	userProfileSearchRequest.setLastname("Jones");

	PagedList<TmUserProfile> userList = getUserSearchDAO().getEntityPagedList(userProfileSearchRequest, null);

	assertNotNull(userList);

	TmUserProfile[] elements = userList.getElements();

	assertEquals(elements.length, 1);
    }

}
