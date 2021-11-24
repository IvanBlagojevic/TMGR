package org.gs4tr.termmanager.webmvc.rest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.dto.ExportTermModel;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webmvc.controllers.AbstractControllerTest;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("rest")
public class AddTermControllerTest extends AbstractControllerTest {

    private static final String ADD_TERM_URL = "/rest/addTerm";

    /* Need to throw exception for not supported language */
    @Test
    @TestCase("addTermController")
    public void addTermControllerInvalideLanguagesTest() throws Exception {
	String userId = loginPost("pm", "test");

	MockHttpServletRequestBuilder post = post(ADD_TERM_URL);

	post.param("projectTicket", IdEncrypter.encryptGenericId(1));
	post.param("sourceTerm", "car");
	post.param("targetTerm", "auto");
	post.param("sourceLocale", "ar-AR");
	post.param("targetLocale", "de-DE");
	post.param("userId", userId);

	post.contentType(MediaType.APPLICATION_FORM_URLENCODED);
	post.accept(MediaType.APPLICATION_JSON);

	ResultActions resultActions = _mockMvc.perform(post);
	MockHttpServletResponse response = resultActions.andReturn().getResponse();

	Assert.assertEquals(500, response.getStatus());
	Assert.assertTrue(StringUtils.isNotBlank(response.getContentAsString()));
    }

    @Test
    @TestCase("addTermController")
    public void addTermControllerTest() throws Exception {
	String userId = loginPost("pm", "test");

	MockHttpServletRequestBuilder post = post(ADD_TERM_URL);

	post.param("projectTicket", IdEncrypter.encryptGenericId(1));
	post.param("sourceTerm", "car");
	post.param("targetTerm", "auto");
	post.param("sourceLocale", "en-US");
	post.param("targetLocale", "de-DE");
	post.param("userId", userId);

	post.contentType(MediaType.APPLICATION_FORM_URLENCODED);
	post.accept(MediaType.APPLICATION_JSON);

	ResultActions resultActions = _mockMvc.perform(post);

	verify(getProjectService(), times(1)).load(any(Long.class));
	verify(getTermEntryService(), times(1)).addTermWS(any(Long.class), any(String.class), any(String.class),
		any(String.class), any(String.class), any(String.class), any(String.class));

	MockHttpServletResponse response = resultActions.andReturn().getResponse();

	Assert.assertEquals(200, response.getStatus());

	Assert.assertEquals(MediaType.APPLICATION_JSON_UTF8_VALUE, response.getContentType());

	String result = response.getContentAsString();
	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	validateResult(resultNode);
    }

    @Before
    public void setUp() throws Exception {
	MockitoAnnotations.initMocks(this);
	mockObjects();
    }

    private void mockObjects() {
	TmProject project = getModelObject("tmProject", TmProject.class);
	ExportTermModel exportTermModel = getModelObject("exportTermModel", ExportTermModel.class);

	when(getProjectService().load(any(Long.class))).thenReturn(project);
	when(getTermEntryService().addTermWS(any(Long.class), any(String.class), any(String.class), any(String.class),
		any(String.class), any(String.class), any(String.class))).thenReturn(exportTermModel);
    }

    private void validateResult(JsonNode resultNode) {
	Assert.assertNotNull(resultNode.findValue("target"));
	Assert.assertNotNull(resultNode.findValue("creationUser"));
	Assert.assertNotNull(resultNode.findValue("modificationDate"));
	Assert.assertNotNull(resultNode.findValue("modificationUser"));
	Assert.assertNotNull(resultNode.findValue("operation"));
	Assert.assertNotNull(resultNode.findValue("sourceAttributes"));
	Assert.assertNotNull(resultNode.findValue("suggestions"));
	Assert.assertNotNull(resultNode.findValue("targetAttributes"));
	Assert.assertNotNull(resultNode.findValue("source"));
	Assert.assertNotNull(resultNode.findValue("forbidden"));
	Assert.assertNotNull(resultNode.findValue("ticket"));
	Assert.assertNotNull(resultNode.findValue("creationDate"));
    }
}
