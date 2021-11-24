package org.gs4tr.termmanager.webservice.controllers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.gs4tr.termmanager.webservice.controllers.GetUserProjectsDataControllerTest.USER_PROJECTS;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.webservice.model.request.BaseCommand;
import org.gs4tr.termmanager.webservice.model.request.BrowseUserProjectsCommand;
import org.gs4tr.termmanager.webservice.model.response.ReturnCode;
import org.junit.Test;
import org.mockito.internal.verification.Times;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("webservice")
public class LogoutControllerTest extends AbstractWebServiceTest {

    private static final String LOGOUT_URL = "rest/v2/logout";

    @Test
    @TestCase("logout")
    public void logoutTest() throws IOException {
	BaseCommand command = getModelObject("command", BaseCommand.class);

	command.setSecurityTicket(getSecurityTicket());

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	Request request = createJsonRequest(LOGOUT_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertEquals(ReturnCode.OK, responseData.get(ReturnCode.RETURN_CODE).asInt());

	assertEquals(true, responseData.get("success").asBoolean());

	JsonNode jsonTime = responseData.get("time");

	assertTrue(StringUtils.isNotBlank(jsonTime.asText()));

	verify(getSessionService(), new Times(1)).logout();

	BrowseUserProjectsCommand browseUserProjects = new BrowseUserProjectsCommand();

	browseUserProjects.setSecurityTicket(getSecurityTicket());

	String jsonBrowseUserProjectsCommand = OBJECT_MAPPER.writeValueAsString(browseUserProjects);

	Request browseUserProjectsRequest = createJsonRequest(USER_PROJECTS, jsonBrowseUserProjectsCommand);

	Response browseResponse = sendRecieve(getLocalConnector(), browseUserProjectsRequest);

	JsonNode browseData = OBJECT_MAPPER.readTree(browseResponse.getContent());

	assertEquals(false, browseData.get("success").asBoolean());
    }
}
