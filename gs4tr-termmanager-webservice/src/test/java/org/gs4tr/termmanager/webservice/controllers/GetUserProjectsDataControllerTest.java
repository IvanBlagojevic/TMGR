package org.gs4tr.termmanager.webservice.controllers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.webservice.model.request.BrowseUserProjectsCommand;
import org.gs4tr.termmanager.webservice.utils.JsonValidatorUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("webservice")
public class GetUserProjectsDataControllerTest extends AbstractWebServiceTest {

    private static final String LANGUAGES = "languages"; //$NON-NLS-1$

    private static final Long USER_ID = 1L;

    static final String USER_PROJECTS = "rest/v2/userProjects"; //$NON-NLS-1$

    @Test
    @TestCase("getUserProjectsData")
    @SuppressWarnings("unchecked")
    public void getUserProjectsExcludeProjectLanguages() throws IOException {
	BrowseUserProjectsCommand command = getModelObject("browseUserProjectsCommand2",
		BrowseUserProjectsCommand.class);

	command.setSecurityTicket(getSecurityTicket());

	List<TmProject> userProjects = getModelObject("userProjects2", List.class);

	when(getProjectService().getUserProjects(USER_ID, TmOrganization.class)).thenReturn(userProjects);

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	Request request = createJsonRequest(USER_PROJECTS, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertTrue(responseData.get("success").asBoolean());

	JsonNode jsonTime = responseData.get("time");

	assertTrue(StringUtils.isNotBlank(jsonTime.asText()));

	JsonNode jsonProjects = responseData.get("projects");

	assertNotNull(jsonProjects);
	assertEquals(2, jsonProjects.size());

	for (JsonNode jsonProject : jsonProjects) {
	    JsonValidatorUtils.validateJsonProjectContent(jsonProject);
	    validateProjectLanguagesExcludedFromResponse(jsonProject);
	}
    }

    @Test
    @TestCase("getUserProjectsData")
    @SuppressWarnings("unchecked")
    public void getUserProjectsIncludeProjectLanguages() throws IOException {
	BrowseUserProjectsCommand command = getModelObject("browseUserProjectsCommand1",
		BrowseUserProjectsCommand.class);

	command.setSecurityTicket(getSecurityTicket());

	List<TmProject> userProjects = getModelObject("userProjects1", List.class);

	Class<?>[] clazz = { TmOrganization.class };

	when(getProjectService().getUserProjects(USER_ID, clazz)).thenReturn(userProjects);

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	Request request = createJsonRequest(USER_PROJECTS, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertTrue(responseData.get("success").asBoolean());

	JsonNode jsonTime = responseData.get("time");

	assertTrue(StringUtils.isNotBlank(jsonTime.asText()));

	JsonNode jsonProjects = responseData.get("projects");

	assertNotNull(jsonProjects);
	assertEquals(1, jsonProjects.size());

	for (JsonNode jsonProject : jsonProjects) {
	    JsonValidatorUtils.validateJsonProjectContent(jsonProject);
	    JsonValidatorUtils.validateProjectLanguagesIncludedInResponse(jsonProject);
	}
    }

    private void validateProjectLanguagesExcludedFromResponse(JsonNode project) {
	assertEquals(0, project.get(LANGUAGES).size());
    }
}
