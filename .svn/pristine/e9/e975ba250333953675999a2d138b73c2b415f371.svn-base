package org.gs4tr.termmanager.webmvc.rest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.webmvc.controllers.AbstractControllerTest;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@TestSuite("rest")
public class FindUserProjectsControllerTest extends AbstractControllerTest {

    private static final String URL = "/rest/userProjects"; //$NON-NLS-1$

    @Test
    @TestCase("findUserProjects")
    public void findUserProjectTest() throws Exception {
	@SuppressWarnings("unchecked")
	List<TmProject> userProjects = getModelObject("userProjects", List.class);

	when(getProjectService().getUserProjects(any(Long.class), any(Class.class))).thenReturn(userProjects);

	String userId = loginGet("pm", "test");

	MockHttpServletRequestBuilder get = get(URL);
	get.param("userId", userId);

	ResultActions resultActions = _mockMvc.perform(get);

	verify(getProjectService(), times(1)).getUserProjects(any(Long.class), any(Class.class));

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	Assert.assertEquals("application/xml;charset=UTF-8", response.getContentType());
	String result = response.getContentAsString();
	Assert.assertNotNull(result);

	SAXBuilder saxBuilder = new SAXBuilder();
	try {
	    org.jdom.Document doc = saxBuilder.build(new StringReader(result));
	    Element rootElement = doc.getRootElement();
	    Assert.assertNotNull(rootElement);

	    @SuppressWarnings("unchecked")
	    List<Element> projects = rootElement.getChildren();

	    for (Element project : projects) {
		Assert.assertNotNull(project.getChild("projectInfo"));
		Assert.assertNotNull(project.getChild("ticket"));
	    }

	} catch (JDOMException e) {
	    Assert.fail();
	} catch (IOException e) {
	    Assert.fail();
	}

    }

    @Before
    public void setUp() throws Exception {
    }
}
