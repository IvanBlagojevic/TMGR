package org.gs4tr.termmanager.webmvc.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

public class ProjectWriteConfigurationControllerTest extends AbstractControllerTest {

    private static final String URL = "projectWriteConfiguration.ter";

    @Test
    public void projectWriteConfigurationTest() throws Exception {
	MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/" + URL);

	ResultActions resultActions = _mockMvc.perform(get);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());

	JsonNode projectAddEditConfigurationNode = resultNode.get("projectAddEditConfiguration");
	Assert.assertNotNull(projectAddEditConfigurationNode);

    }

}