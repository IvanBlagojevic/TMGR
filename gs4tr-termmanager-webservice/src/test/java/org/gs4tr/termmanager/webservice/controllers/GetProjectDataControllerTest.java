package org.gs4tr.termmanager.webservice.controllers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.model.ProjectLanguage;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.webservice.model.request.BrowseProjectDataCommand;
import org.gs4tr.termmanager.webservice.utils.JsonValidatorUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("webservice")
public class GetProjectDataControllerTest extends AbstractWebServiceTest {

    private static final String PROJECT_BY_SHORTCODE_URL = "rest/v2/projectByShortcode";

    @Test
    @TestCase("getProjectData")
    public void getProjectDataFailed() throws Exception {
	BrowseProjectDataCommand browseCommand = getModelObject("invalidBrowseCommand", BrowseProjectDataCommand.class);
	browseCommand.setSecurityTicket(getSecurityTicket());

	when(getProjectService().findProjectByShortCode(anyString())).thenReturn(null);

	String requestContent = OBJECT_MAPPER.writeValueAsString(browseCommand);

	Request request = createJsonRequest(PROJECT_BY_SHORTCODE_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(400, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertFalse(responseData.get("success").asBoolean());
	assertNotNull(responseData.get("errorMessage").asText());
    }

    @Test
    @TestCase("getProjectData")
    @SuppressWarnings("unchecked")
    public void getProjectDataIncludeLanguagesTest() throws IOException {
	BrowseProjectDataCommand browseCommand = getModelObject("browseCommand", BrowseProjectDataCommand.class);
	browseCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("tmProject", TmProject.class);

	Set<ProjectLanguage> projectLanguages = getModelObject("projectLanguages", Set.class);

	when(getProjectService().findProjectByShortCode(anyString())).thenReturn(project);
	when(getProjectService().getProjectLanguages(anyLong())).thenReturn(new ArrayList<>(projectLanguages));

	String requestContent = OBJECT_MAPPER.writeValueAsString(browseCommand);

	Request request = createJsonRequest(PROJECT_BY_SHORTCODE_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertTrue(responseData.get("success").asBoolean());

	JsonNode jsonTime = responseData.get("time");

	assertTrue(StringUtils.isNotBlank(jsonTime.asText()));

	JsonNode jsonProjects = responseData.get("projects");

	assertNotNull(jsonProjects);

	for (JsonNode jsonProject : jsonProjects) {
	    JsonValidatorUtils.validateJsonProjectContent(jsonProject);
	}
    }

}
