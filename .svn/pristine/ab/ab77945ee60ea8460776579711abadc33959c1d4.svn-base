package org.gs4tr.termmanager.webmvc.rest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.webmvc.controllers.AbstractControllerTest;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@TestSuite("rest")
public class FindProjectByShortcodeControllerTest extends AbstractControllerTest {

    private static final String PROJECT_SHORT_CODE = "MYF000001"; //$NON-NLS-1$

    private static final String URL = "/rest/projectByShortcode"; //$NON-NLS-1$

    @Test
    @TestCase("findProjectByShortCode")
    public void findProjectNullTest() throws Exception {
	when(getProjectService().findProjectByShortCode(any(String.class))).thenReturn(null);

	String userId = loginGet("pm", "test");

	MockHttpServletRequestBuilder get = get(URL);
	get.param("projectShortcode", PROJECT_SHORT_CODE);
	get.param("userId", userId);
	get.param("fetchLanguages", "false");

	ResultActions resultActions = _mockMvc.perform(get);

	verify(getProjectService(), times(1)).findProjectByShortCode(any(String.class));

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	Assert.assertEquals("application/xml;charset=UTF-8", response.getContentType());

	String result = response.getContentAsString();
	Assert.assertNotNull(result);

    }

    @Test
    @TestCase("findProjectByShortCode")
    public void findWithLanguagesTest() throws Exception {
	TmProject project = getModelObject("tmProject1", TmProject.class);

	when(getProjectService().findProjectByShortCode(any(String.class))).thenReturn(project);

	String userId = loginGet("pm", "test");

	MockHttpServletRequestBuilder get = get(URL);
	get.param("projectShortcode", PROJECT_SHORT_CODE);
	get.param("userId", userId);
	get.param("fetchLanguages", "true");

	ResultActions resultActions = _mockMvc.perform(get);

	verify(getProjectService(), times(1)).findProjectByShortCode(any(String.class));

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	Assert.assertEquals("application/xml;charset=UTF-8", response.getContentType());

	String result = response.getContentAsString();
	Assert.assertNotNull(result);
    }

    @Test
    @TestCase("findProjectByShortCode")
    public void findWithoutLanguagesTest() throws Exception {
	TmProject project = getModelObject("tmProject2", TmProject.class);

	when(getProjectService().findProjectByShortCode(any(String.class))).thenReturn(project);

	String userId = loginGet("pm", "test");

	MockHttpServletRequestBuilder get = get(URL);
	get.param("projectShortcode", PROJECT_SHORT_CODE);
	get.param("userId", userId);
	get.param("fetchLanguages", "false");

	ResultActions resultActions = _mockMvc.perform(get);

	verify(getProjectService(), times(1)).findProjectByShortCode(any(String.class));

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	Assert.assertEquals("application/xml;charset=UTF-8", response.getContentType());

	String result = response.getContentAsString();
	Assert.assertNotNull(result);
    }

}
