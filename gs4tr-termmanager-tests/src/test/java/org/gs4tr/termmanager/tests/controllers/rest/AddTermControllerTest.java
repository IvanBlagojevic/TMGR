package org.gs4tr.termmanager.tests.controllers.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class AddTermControllerTest extends BaseRestControllerTest {

    private static final String URL = "/rest/addTerm";

    @Test
    public void addTermPostTest() throws Exception {
	String userId = loginPost("pm", "test");

	MockHttpServletRequestBuilder post = post(URL);

	post.param("projectTicket", IdEncrypter.encryptGenericId(1));
	post.param("sourceTerm", "car");
	post.param("targetTerm", "auto");
	post.param("sourceLocale", "en-US");
	post.param("targetLocale", "de-DE");
	post.param("userId", userId);

	post.contentType(MediaType.APPLICATION_FORM_URLENCODED);
	post.accept(MediaType.APPLICATION_JSON);

	ResultActions resultActions = _mockMvc.perform(post);

	MockHttpServletResponse response = resultActions.andReturn().getResponse();

	Assert.assertEquals(200, response.getStatus());

	Assert.assertEquals(MediaType.APPLICATION_JSON_UTF8_VALUE, response.getContentType());

	String result = response.getContentAsString();
	Assert.assertNotNull(result);
    }
}