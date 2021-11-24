package org.gs4tr.termmanager.webmvc.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.gs4tr.termmanager.model.TmUserProfile;
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
public class RemoveCustomFilterControllerTest extends AbstractControllerTest {

    private static final String URL = "removeCustomFilter.ter";

    @Test
    @TestCase("removeCustomFilter")
    public void exceptionFolderNotDeletedTest() throws Exception {

	when(getUserProfileService().removeCustomSearchFolder(any(TmUserProfile.class), any(String.class)))
		.thenReturn(false);

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL);
	post.param("customFolder", "Some filter");

	ResultActions resultActions = _mockMvc.perform(post);

	verify(getUserProfileService(), times(1)).removeCustomSearchFolder(any(TmUserProfile.class), any(String.class));

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertFalse(successNode.asBoolean());

	JsonNode reason = resultNode.get("reasons");
	Assert.assertNotNull(reason);

    }

    @Test
    @TestCase("removeCustomFilter")
    public void exceptionTryAgainTest() throws Exception {

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL);
	post.param("customFolder", "BILINGUAL");

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertFalse(successNode.asBoolean());

	JsonNode reason = resultNode.get("reasons");
	Assert.assertNotNull(reason);

    }

    @Test
    @TestCase("removeCustomFilter")
    public void removeFilterTest() throws Exception {

	when(getUserProfileService().removeCustomSearchFolder(any(TmUserProfile.class), any(String.class)))
		.thenReturn(true);

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL);
	post.param("customFolder", "Some filter");

	ResultActions resultActions = _mockMvc.perform(post);

	verify(getUserProfileService(), times(1)).removeCustomSearchFolder(any(TmUserProfile.class), any(String.class));

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());
    }
}
