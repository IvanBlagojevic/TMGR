package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.search.UserProfileSearchRequest;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.junit.Test;

public class UserProfileServiceSearchTest extends AbstractSearchTests {

    @Test
    public void testSearch_01() throws Exception {
	UserProfileSearchRequest userProfileSearchRequest = new UserProfileSearchRequest();
	userProfileSearchRequest.setUsername("pm");

	TaskPagedList<TmUserProfile> userList = (TaskPagedList<TmUserProfile>) getUserProfileService()
		.search(userProfileSearchRequest, new PagedListInfo());

	assertNotNull(userList);

	TmUserProfile[] elements = userList.getElements();

	assertEquals(1, elements.length);

	assertAREPTaskNames(userList, EntityTypeHolder.USER);
    }

    @Test
    public void testSearch_02() {
	UserProfileSearchRequest userProfileSearchRequest = new UserProfileSearchRequest();
	userProfileSearchRequest.setUsername("no exist");

	TaskPagedList<TmUserProfile> userList = (TaskPagedList<TmUserProfile>) getUserProfileService()
		.search(userProfileSearchRequest, new PagedListInfo());

	assertNotNull(userList);

	TmUserProfile[] elements = userList.getElements();

	assertEquals(0, elements.length);
    }
}
