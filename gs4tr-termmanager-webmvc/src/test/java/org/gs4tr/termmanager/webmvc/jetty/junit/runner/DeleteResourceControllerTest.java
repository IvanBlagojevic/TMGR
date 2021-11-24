package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.model.commands.DeleteResourceCommand;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

@TestSuite("deleteResourceController")
public class DeleteResourceControllerTest extends AbstractMvcTest {

    private static final String URL = "deleteResource.ter";

    @Test
    @TestCase("deleteResourceCompleted")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void deleteResourceCompletedTest() throws JsonProcessingException {

	String[] resourceIds = new String[] { IdEncrypter.encryptGenericId(2) };

	Map<String, String> parameters = new HashMap<String, String>();

	String encryptGenericId = IdEncrypter.encryptGenericId(1);

	parameters.put("termEntryTicket", UUID.randomUUID().toString());

	parameters.put("resourceIds", String.valueOf(resourceIds));

	parameters.put("projectTicket", encryptGenericId);

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	Assert.assertEquals(200, response.getStatus());

	verify(getTermEntryService()).deleteTermEntryResourceTracks(anyString(), anyListOf(String.class), anyLong());

	Assert.assertNotNull(response.getContent());
    }

    @Test
    @TestCase("deleteResourceFailed")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void deleteResourceFailedTest() throws JsonProcessingException {

	DeleteResourceCommand command = getModelObject("command", DeleteResourceCommand.class);

	Map<String, String> parameters = new HashMap<String, String>();
	parameters.put(JSON_DATA_KEY, OBJECT_MAPPER.writeValueAsString(command));

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	Assert.assertEquals(200, response.getStatus());

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(false));

	responseContent.assertProperty("reasons", MessageResolver.getMessage("JsonExceptionResolver.1"));
    }
}
