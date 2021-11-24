package org.gs4tr.termmanager.webmvc.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Iterator;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.search.UserProfileSearchRequest;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.pagedlist.TmTaskPagedList;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("controllers")
public class UserProfileSearchControllerTest extends AbstractControllerTest {

    @Test
    @TestCase("userProfileSearch")
    public void userProfileSearchTest() throws Exception {
	TmUserProfile userProfile1 = getModelObject("tmUserProfile1", TmUserProfile.class);
	TmUserProfile userProfile2 = getModelObject("tmUserProfile2", TmUserProfile.class);
	TmUserProfile[] organizations = { userProfile1, userProfile2 };

	PagedList<TmUserProfile> pagedList = new PagedList<TmUserProfile>();
	pagedList.setElements(organizations);
	pagedList.setPagedListInfo(new PagedListInfo());
	pagedList.setTotalCount(2L);

	TaskPagedList<TmUserProfile> taskPagedList = new TmTaskPagedList<TmUserProfile>(pagedList);

	when(getUserProfileService().search(any(UserProfileSearchRequest.class), any(PagedListInfo.class)))
		.thenReturn(taskPagedList);

	String jsonData = getJsonData("userProfileSearch.json");

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.USER_PROFILE_SEARCH);
	post.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertTrue(!result.startsWith("Application error"));

	verify(getUserProfileService(), times(1)).search(any(UserProfileSearchRequest.class), any(PagedListInfo.class));

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());

	JsonNode itemsNode = resultNode.get("items");
	Assert.assertNotNull(itemsNode);

	Iterator<JsonNode> elements = itemsNode.elements();
	Assert.assertTrue(elements.hasNext());

	while (elements.hasNext()) {
	    JsonNode element = elements.next();
	    Assert.assertNotNull(element.findValue("generic"));
	    Assert.assertNotNull(element.findValue("systemRoles"));
	    Assert.assertNotNull(element.findValue("organizationName"));
	    Assert.assertNotNull(element.findValue("ticket"));
	    Assert.assertNotNull(element.findValue("availableTasks"));
	    Assert.assertNotNull(element.findValue("tasks"));
	    Assert.assertNotNull(element.findValue("userInfo"));

	    JsonNode userInfoNode = element.get("userInfo");
	    Assert.assertNotNull(userInfoNode);
	    Assert.assertNotNull(userInfoNode.findValue("unsuccessfulAuthCount"));
	    Assert.assertNotNull(userInfoNode.findValue("dateLastLogin"));
	    Assert.assertNotNull(userInfoNode.findValue("userName"));
	    Assert.assertNotNull(userInfoNode.findValue("userType"));
	    Assert.assertNotNull(userInfoNode.findValue("emailAddress"));
	    Assert.assertNotNull(userInfoNode.findValue("emailNotification"));
	    Assert.assertNotNull(userInfoNode.findValue("firstName"));
	    Assert.assertNotNull(userInfoNode.findValue("lastName"));
	    Assert.assertNotNull(userInfoNode.findValue("accountNonExpired"));
	    Assert.assertNotNull(userInfoNode.findValue("credentialsNonExpired"));
	    Assert.assertNotNull(userInfoNode.findValue("enabled"));
	    Assert.assertNotNull(userInfoNode.findValue("password"));
	    Assert.assertNotNull(userInfoNode.findValue("timeZone"));
	    Assert.assertNotNull(userInfoNode.findValue("accountLocked"));
	    Assert.assertNotNull(userInfoNode.findValue("autoClaimMultipleTasks"));
	    Assert.assertNotNull(userInfoNode.findValue("claimMultipleJobTasks"));

	}
    }

}
