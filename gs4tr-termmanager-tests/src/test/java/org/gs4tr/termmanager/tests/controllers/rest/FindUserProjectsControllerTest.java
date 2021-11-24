package org.gs4tr.termmanager.tests.controllers.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class FindUserProjectsControllerTest extends BaseRestControllerTest {

    private static final String URL = "/rest/userProjects";

    @Test
    public void findUserProjectsGetTest() throws Exception {
	String userId = loginGet("pm", "test");

	MockHttpServletRequestBuilder get = get(URL);
	get.param("userId", userId);

	ResultActions resultActions = _mockMvc.perform(get);

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	Assert.assertEquals("application/xml;charset=UTF-8", response.getContentType());
	String result = response.getContentAsString();
	Assert.assertNotNull(result);
    }

}
