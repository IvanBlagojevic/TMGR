package org.gs4tr.termmanager.webmvc.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

public class DeleteResourceControllerTest extends AbstractControllerTest {

    private static final String URL = "deleteResource.ter";

    /* Testing exception success should be FALSE */
    @Test
    public void deleteResourceExceptionTest() throws Exception {
	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL);
	String[] resources = { IdEncrypter.encryptGenericId(1), IdEncrypter.encryptGenericId(2) };
	post.param("resourceIds", resources);

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
    }

    @Test
    public void deleteResourceTest() throws Exception {
	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL);
	String[] resources = { IdEncrypter.encryptGenericId(1), IdEncrypter.encryptGenericId(2) };
	post.param("resourceIds", resources);
	post.param("termEntryTicket", IdEncrypter.encryptGenericId(1));

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);
	Assert.assertTrue(StringUtils.isNotBlank(result));
    }

    @Before
    public void setUp() throws Exception {
    }

}
