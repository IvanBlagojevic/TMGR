package org.gs4tr.termmanager.webservice.controllers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpTester;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.model.ProjectMetadata;
import org.gs4tr.termmanager.model.ProjectMetadataRequest;
import org.gs4tr.termmanager.webservice.model.request.GetProjectMetadataCommand;
import org.gs4tr.termmanager.webservice.utils.JsonValidatorUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("webservice")
public class GetProjectMetadataControllerTest extends AbstractWebServiceTest {

    private static final String GET_PROJECT_METADATA_URL = "rest/v2/getProjectMetadata";

    @Test
    @TestCase("getProjectMetadata")
    @SuppressWarnings("unchecked")
    public void getProjectMetadataFailsInvaliProjectShortcodeTest() throws IOException {

	GetProjectMetadataCommand command = getModelObject("invalidProjectMetadataCommand",
		GetProjectMetadataCommand.class);
	command.setSecurityTicket(getSecurityTicket());

	when(getProjectService().getProjectMetadata(any(ProjectMetadataRequest.class)))
		.thenReturn(Collections.emptyList());

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	HttpTester.Request request = createJsonRequest(GET_PROJECT_METADATA_URL, requestContent);

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertTrue(responseData.get("success").asBoolean());

	JsonNode jsonTime = responseData.get("time");

	assertTrue(StringUtils.isNotBlank(jsonTime.asText()));

	JsonNode projectMetadata = responseData.get("projectMetadata");

	assertEquals(0, projectMetadata.size());

    }

    @Test
    @TestCase("getProjectMetadata")
    @SuppressWarnings("unchecked")
    public void getProjectMetadataSuccessTest() throws IOException {

	GetProjectMetadataCommand command = getModelObject("projectMetadataCommand", GetProjectMetadataCommand.class);
	command.setSecurityTicket(getSecurityTicket());

	List<ProjectMetadata> metadataList = getModelObject("projectMetadataList", List.class);

	when(getProjectService().getProjectMetadata(any(ProjectMetadataRequest.class))).thenReturn(metadataList);

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	HttpTester.Request request = createJsonRequest(GET_PROJECT_METADATA_URL, requestContent);

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertTrue(responseData.get("success").asBoolean());

	JsonNode jsonTime = responseData.get("time");

	assertTrue(StringUtils.isNotBlank(jsonTime.asText()));

	JsonNode projectMetadata = responseData.get("projectMetadata");

	assertNotNull(projectMetadata);

	for (JsonNode jsonProjectMetadata : projectMetadata) {
	    JsonValidatorUtils.validateJsonProjectMetadataContent(jsonProjectMetadata);
	}

    }

}
