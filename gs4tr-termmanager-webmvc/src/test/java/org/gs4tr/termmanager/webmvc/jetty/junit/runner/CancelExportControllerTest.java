package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static junit.framework.Assert.assertEquals;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.eq;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.webmvc.test.annotations.ClientBean;
import org.gs4tr.termmanager.service.TermEntryExporter;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.model.commands.CancelExportCommand;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

@TestSuite("cancelExportController")
public class CancelExportControllerTest extends AbstractMvcTest {

    private static final String URL = "cancelExport.ter";

    @ClientBean
    private TermEntryExporter _termEntryExporter;

    @Test
    @TestCase("cancelExportCommand")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void cancelExportTest() throws JsonProcessingException {

	CancelExportCommand command = getModelObject("command", CancelExportCommand.class);

	Map<String, String> parameters = new HashMap<String, String>();
	parameters.put(JSON_DATA_KEY, OBJECT_MAPPER.writeValueAsString(command));

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(getTermEntryExporter()).requestStopExport(eq(command.getThreadName()));

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));
    }

    public TermEntryExporter getTermEntryExporter() {
	return _termEntryExporter;
    }

    @Before
    public void setUp() {
	reset(getTermEntryExporter());
    }
}
