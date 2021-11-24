package org.gs4tr.termmanager.webmvc.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UserCustomSearch;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.utils.JsonUtils;
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
public class SaveCustomFilterControllerTest extends AbstractControllerTest {

    private static final String URL = "saveCustomFilter.ter";

    /* Testing exception success should be FALSE */
    @Test
    @TestCase("saveCustomFilter")
    public void saveCustomFilterNameExistThrowExceptionTest() throws Exception {
	String jsonData = getJsonData("saveCustomFilterData.json");

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL);
	post.param("jsonData", jsonData);

	when(getUserProfileService().getCustomSearchFolder(any(TmUserProfile.class), any(String.class)))
		.thenReturn(new UserCustomSearch());

	ResultActions resultActions = _mockMvc.perform(post);

	verify(getUserProfileService(), times(1)).getCustomSearchFolder(any(TmUserProfile.class), any(String.class));

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	// Success == false exception is thrown
	Assert.assertFalse(successNode.asBoolean());

    }

    @Test
    @TestCase("saveCustomFilter")
    public void saveCustomFilterPowerUserTest() throws Exception {
	String jsonData = getJsonData("saveCustomFilterAdminData.json");
	List<ItemFolderEnum> adminFolders = new ArrayList<ItemFolderEnum>();
	adminFolders.add(ItemFolderEnum.ORGANIZATIONS);
	adminFolders.add(ItemFolderEnum.PROJECTDETAILS);
	adminFolders.add(ItemFolderEnum.PROJECTS);
	adminFolders.add(ItemFolderEnum.SUBMISSIONTERMLIST);

	TmUserProfile userProfile = (TmUserProfile) UserProfileContext.getCurrentUserProfile();
	userProfile.getUserInfo().setUserType(UserTypeEnum.POWER_USER);
	userProfile.setAdminFolders(adminFolders);
	UserProfileContext.setCurrentUserProfile(userProfile);

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL);
	post.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	validateResult(resultNode);

    }

    @Test
    @TestCase("saveCustomFilter")
    public void saveCustomFilterTest() throws Exception {
	String jsonData = getJsonData("saveCustomFilterData.json");

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL);
	post.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	validateResult(resultNode);

    }

    private void validateResult(JsonNode resultNode) {
	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());

	JsonNode menuConfigNode = resultNode.get("menuConfig");
	Assert.assertNotNull(menuConfigNode);

	Assert.assertNotNull(menuConfigNode.findValue("parent"));
	Assert.assertNotNull(menuConfigNode.findValue("id"));
	Assert.assertNotNull(menuConfigNode.findValue("systemHidden"));
	Assert.assertNotNull(menuConfigNode.findValue("url"));
	Assert.assertNotNull(menuConfigNode.findValue("jsonSearch"));
	Assert.assertNotNull(menuConfigNode.findValue("detailsUrl"));
	Assert.assertNotNull(menuConfigNode.findValue("searchCriterias"));

	JsonNode searchCriteriasNode = menuConfigNode.get("searchCriterias");
	Assert.assertNotNull(searchCriteriasNode);

	Iterator<JsonNode> elements = searchCriteriasNode.elements();
	Assert.assertTrue(elements.hasNext());

	while (elements.hasNext()) {
	    JsonNode element = elements.next();
	    Assert.assertNotNull(element);
	}
    }
}