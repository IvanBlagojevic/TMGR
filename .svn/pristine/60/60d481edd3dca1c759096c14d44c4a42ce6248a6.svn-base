package org.gs4tr.termmanager.webmvc.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("controllers")
public class GetUUIDControllerTest extends AbstractControllerTest {

    private static final String URL = "getUUIDs.ter";

    @Test
    public void getUUIDTest_1() throws Exception {

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL);
	post.param("numberOf", "1");

	ResultActions resultActions = _mockMvc.perform(post);

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

    @Test
    public void getUUIDTest_2() throws Exception {

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL);
	ResultActions resultActions = _mockMvc.perform(post);

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

    @Test
    public void getUUIDTest_3() throws Exception {

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL);
	post.param("numberOf", "1001");

	ResultActions resultActions = _mockMvc.perform(post);

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
