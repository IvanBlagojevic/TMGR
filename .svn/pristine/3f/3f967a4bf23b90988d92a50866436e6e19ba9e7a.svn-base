package org.gs4tr.termmanager.webservice.controllers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.usermanager.model.AbstractUserProfile;
import org.gs4tr.foundation.modules.usermanager.service.security.DefaultUserDetails;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.webservice.model.request.LoginCommand;
import org.gs4tr.termmanager.webservice.model.response.ReturnCode;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite(value = "webservice")
public class LoginControllerTest extends AbstractWebServiceTest {

    static final String LOGIN_URL = "rest/v2/login";

    @Test
    @TestCase("login")
    public void testLoginFailedInvalidCredentials() throws IOException {
	LoginCommand command = getModelObject("loginFailedCommand", LoginCommand.class);

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	Request request = createJsonRequest(LOGIN_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(400, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertFalse(responseData.get("success").asBoolean());

	assertEquals(ReturnCode.INVALID_CREDENTIALS, responseData.get(ReturnCode.RETURN_CODE).asInt());
    }

    @Test
    @TestCase("login")
    public void testLoginSucceed() throws IOException {
	LoginCommand command = getModelObject("loginCommand", LoginCommand.class);
	TmUserProfile userProfile = getModelObject("userProfile", TmUserProfile.class);

	final String userName = command.getUsername();

	when(getUserProfileService().loadUserByUsername(userName))
		.thenReturn(new DefaultUserDetails<AbstractUserProfile>(userProfile));

	String content = OBJECT_MAPPER.writeValueAsString(command);

	Request request = createJsonRequest(LOGIN_URL, content);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertTrue(responseData.get("success").asBoolean());

	assertEquals(ReturnCode.OK, responseData.get(ReturnCode.RETURN_CODE).asInt());

	assertTrue(StringUtils.isNotBlank(responseData.get("version").asText()));

	JsonNode jsonTime = responseData.get("time");

	assertTrue(StringUtils.isNotBlank(jsonTime.asText()));

	JsonNode jsonSecurityTicket = responseData.get(SECURITY_TICKET_KEY);

	assertTrue(StringUtils.isNotBlank(jsonSecurityTicket.asText()));
    }
}
