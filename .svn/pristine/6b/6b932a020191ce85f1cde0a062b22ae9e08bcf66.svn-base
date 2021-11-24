package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static junit.framework.Assert.assertEquals;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.http.HttpTester;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.webmvc.test.annotations.ClientBean;
import org.gs4tr.termmanager.service.ImportTermService;
import org.gs4tr.termmanager.webmvc.model.commands.CancelImportRequestCommand;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

@TestSuite("cancelImportController")
public class CancelImportControllerTest extends AbstractMvcTest {

    private static final String URL = "cancelImportRequest.ter";

    @ClientBean
    private ImportTermService _importTermService;

    @Test
    @TestCase("cancelImport")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void cancelImportRequestSuccess() throws JsonProcessingException {

	CancelImportRequestCommand command = getModelObject("command", CancelImportRequestCommand.class);
	assertEquals(command.getThreadNames().size(), 3);

	Set<String> threadNames = command.getThreadNames();

	Map<String, String> parameters = new HashMap<>();
	parameters.put(JSON_DATA_KEY, OBJECT_MAPPER.writeValueAsString(command));

	HttpTester.Request request = createPostRequest(URL, parameters, getSessionParameters());

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);

	verify(getImportTermService()).cancelImportRequest(eq(threadNames));

	assertEquals(200, response.getStatus());

    }

    private ImportTermService getImportTermService() {
	return _importTermService;
    }

}
