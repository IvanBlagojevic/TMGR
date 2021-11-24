package org.gs4tr.termmanager.webservice.controllers;

import static junit.framework.Assert.assertEquals;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jetty.http.HttpTester;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.webmvc.test.annotations.ClientBean;
import org.gs4tr.termmanager.service.solr.restore.IRestoreProcessorV2;
import org.gs4tr.termmanager.service.solr.restore.RecodeOrCloneTermsProcessor;
import org.gs4tr.termmanager.webservice.model.request.RecodeOrCloneTermsCommand;
import org.gs4tr.termmanager.webservice.model.response.ErrorResponse;
import org.gs4tr.termmanager.webservice.model.response.RecodeOrCloneResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.stubbing.Answer;

@TestSuite("webservice")
public class RecodeOrCloneControllerTest extends AbstractWebServiceTest {

    private static final String RECODE_OR_CLONE_TEST = "rest/v2/recodeOrClone";

    @ClientBean
    private IRestoreProcessorV2 _iRestoreProcessorV2;

    @ClientBean
    private RecodeOrCloneTermsProcessor _recodeOrCloneTermsProcessor;

    @Test
    @TestCase("recodeOrClone")
    public void recodeOrCloneCalledTwoTimesTest() throws IOException {

	loginUserWithSpecificRoleAndType("power_user", UserTypeEnum.POWER_USER);

	RecodeOrCloneTermsCommand command = getModelObject("recodeOrCloneCommand", RecodeOrCloneTermsCommand.class);
	command.setSecurityTicket(getSecurityTicket());

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	HttpTester.Request request = createJsonRequest(RECODE_OR_CLONE_TEST, requestContent);

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);
	assertEquals(200, response.getStatus());

	RecodeOrCloneResponse recodeOrCloneResponse = OBJECT_MAPPER.readValue(response.getContent(),
		RecodeOrCloneResponse.class);

	String responseMessage = recodeOrCloneResponse.getMessage();

	Assert.assertEquals(Messages.getString("RecodeOrCloneController.1"), responseMessage);

	HttpTester.Response response1 = sendRecieve(getLocalConnector(), request);
	assertEquals(200, response1.getStatus());

	RecodeOrCloneResponse recodeOrCloneResponseSecond = OBJECT_MAPPER.readValue(response1.getContent(),
		RecodeOrCloneResponse.class);

	String responseMessageSecond = recodeOrCloneResponseSecond.getMessage();
	Assert.assertEquals(Messages.getString("RecodeOrCloneController.1"), responseMessageSecond);

    }

    /* Must be admin or power user to run RecodeOrClone operation */
    @Test
    @TestCase("recodeOrClone")
    public void recodeOrCloneInvalidUserTest() throws IOException {

	RecodeOrCloneTermsCommand command = getModelObject("recodeOrCloneCommand", RecodeOrCloneTermsCommand.class);
	command.setSecurityTicket(getSecurityTicket());

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	HttpTester.Request request = createJsonRequest(RECODE_OR_CLONE_TEST, requestContent);

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);
	assertEquals(400, response.getStatus());

	ErrorResponse recodeOrCloneResponse = OBJECT_MAPPER.readValue(response.getContent(), ErrorResponse.class);

	String responseErrorMessage = recodeOrCloneResponse.getErrorMessage();

	Assert.assertEquals(Messages.getString("RecodeOrCloneController.4"), responseErrorMessage);
    }

    @Test
    @TestCase("recodeOrClone")
    public void recodeOrCloneStartedWhileAlreadyExecutingTest() throws IOException, InterruptedException {

	loginUserWithSpecificRoleAndType("power_user", UserTypeEnum.POWER_USER);

	RecodeOrCloneTermsCommand command = getModelObject("recodeOrCloneCommand", RecodeOrCloneTermsCommand.class);
	command.setSecurityTicket(getSecurityTicket());

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	HttpTester.Request request = createJsonRequest(RECODE_OR_CLONE_TEST, requestContent);

	int sleepTime = 3000;

	/* Simulate RecodeOrCode action until second request is called */

	try {
	    doAnswer((Answer<Set<String>>) invocationOnMock -> {

		// Wait 3 sec for second controller call
		Thread.sleep(sleepTime);
		return new HashSet<>();
	    }).when(getiRestoreProcessorV2()).restoreRecodeOrClone(any(), any());
	} catch (Exception e) {
	    e.printStackTrace();
	}

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);
	assertEquals(200, response.getStatus());

	RecodeOrCloneResponse recodeOrCloneResponse = OBJECT_MAPPER.readValue(response.getContent(),
		RecodeOrCloneResponse.class);

	String responseMessage = recodeOrCloneResponse.getMessage();

	Assert.assertEquals(Messages.getString("RecodeOrCloneController.1"), responseMessage);

	HttpTester.Response response1 = sendRecieve(getLocalConnector(), request);
	assertEquals(400, response1.getStatus());

	ErrorResponse recodeOrCloneResponseSecond = OBJECT_MAPPER.readValue(response1.getContent(),
		ErrorResponse.class);

	String responseMessageSecond = recodeOrCloneResponseSecond.getErrorMessage();
	Assert.assertEquals(Messages.getString("RecodeOrCloneController.2"), responseMessageSecond);

	// Wait first recodeOrClone action to finish
	Thread.sleep(sleepTime);

    }

    @Test
    @TestCase("recodeOrClone")
    public void recodeOrCloneTest() throws IOException {

	loginUserWithSpecificRoleAndType("power_user", UserTypeEnum.POWER_USER);

	RecodeOrCloneTermsCommand command = getModelObject("recodeOrCloneCommand", RecodeOrCloneTermsCommand.class);
	command.setSecurityTicket(getSecurityTicket());

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	HttpTester.Request request = createJsonRequest(RECODE_OR_CLONE_TEST, requestContent);

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);
	assertEquals(200, response.getStatus());

	RecodeOrCloneResponse recodeOrCloneResponse = OBJECT_MAPPER.readValue(response.getContent(),
		RecodeOrCloneResponse.class);

	String responseMessage = recodeOrCloneResponse.getMessage();

	Assert.assertEquals(Messages.getString("RecodeOrCloneController.1"), responseMessage);
    }

    @Test
    @TestCase("recodeOrClone")
    public void recodeOrCloneTestInvalidCommand() throws IOException {

	loginUserWithSpecificRoleAndType("power_user", UserTypeEnum.POWER_USER);

	RecodeOrCloneTermsCommand command = getModelObject("recodeOrCloneCommand", RecodeOrCloneTermsCommand.class);
	command.setSecurityTicket(getSecurityTicket());

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	HttpTester.Request request = createJsonRequest(RECODE_OR_CLONE_TEST, requestContent);

	String invalidCommand = "Invalid command";

	doThrow(new RuntimeException(invalidCommand)).when(getRecodeOrCloneTermsProcessor())
		.initAndValidateCommands(any(), any(), any(), any());

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);
	assertEquals(400, response.getStatus());

	ErrorResponse recodeOrCloneResponseSecond = OBJECT_MAPPER.readValue(response.getContent(), ErrorResponse.class);

	String responseMessageSecond = recodeOrCloneResponseSecond.getErrorMessage();
	Assert.assertEquals(invalidCommand, responseMessageSecond);
    }

    @Override
    public void resetMocks() {
	reset(getTermEntyService());
	reset(getiRestoreProcessorV2());
	reset(getRecodeOrCloneTermsProcessor());
    }

    private RecodeOrCloneTermsProcessor getRecodeOrCloneTermsProcessor() {
	return _recodeOrCloneTermsProcessor;
    }

    private IRestoreProcessorV2 getiRestoreProcessorV2() {
	return _iRestoreProcessorV2;
    }

    private void loginUserWithSpecificRoleAndType(String roleId, UserTypeEnum userType) throws IOException {
	setUserRoleAndType(roleId, userType);
	loginBeforeTest();
    }

}
