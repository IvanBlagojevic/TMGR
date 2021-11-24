package org.gs4tr.termmanager.webservice.controllers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.webmvc.test.annotations.ClientBean;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.export.ExportDocumentFactory;
import org.gs4tr.termmanager.service.impl.ExportNotificationCallback;
import org.gs4tr.termmanager.webservice.model.request.DetailedGlossaryExportCommand;
import org.gs4tr.termmanager.webservice.utils.JsonValidatorUtils;
import org.gs4tr.tm3.api.Page;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("webservice")
public class DetailedGlossaryExportControllerTest extends AbstractWebServiceTest {

    private static final String DETAILED_EXPORT_URL = "rest/v2/detailedExport";

    private static final String TIME_KEY = "time";

    @ClientBean
    private TermEntryService _termEntryService;

    @Test
    @TestCase("detailedGlossaryExport")
    public void detailedGlossaryExportFailed() throws Exception {

	DetailedGlossaryExportCommand invalidExportCommand = getModelObject("invalidExportCommand",
		DetailedGlossaryExportCommand.class);

	invalidExportCommand.setSecurityTicket(getSecurityTicket());

	String requestContent = OBJECT_MAPPER.writeValueAsString(invalidExportCommand);

	Request request = createJsonRequest(DETAILED_EXPORT_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	/*
	 * This is expected because the export command does not contain project ticket.
	 */
	assertEquals(400, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertEquals("Project ticket can't be empty or null.", responseData.get("errorMessage").asText());
	assertFalse(responseData.get("success").asBoolean());

    }

    @Test
    @TestCase("detailedGlossaryExport")
    public void detailedGlossaryExportFailedInvalidExportFormat() throws Exception {

	DetailedGlossaryExportCommand invalidExportCommand = getModelObject("invalidExportCommand1",
		DetailedGlossaryExportCommand.class);

	invalidExportCommand.setSecurityTicket(getSecurityTicket());

	String requestContent = OBJECT_MAPPER.writeValueAsString(invalidExportCommand);

	Request request = createJsonRequest(DETAILED_EXPORT_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(400, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertEquals("Invalid file format [CSVSYNC]. Supported formats are JSON and TBX.",
		responseData.get("errorMessage").asText());
	assertFalse(responseData.get("success").asBoolean());

    }

    /* Test case where search forbidden terms is excluded. */
    @Test
    @TestCase("detailedGlossaryExport")
    public void detailedGlossaryExportTest_01() throws IOException {

	DetailedGlossaryExportCommand exportCommand = getModelObject("exportCommandForbiddenExcluded",
		DetailedGlossaryExportCommand.class);

	exportCommand.setSecurityTicket(getSecurityTicket());

	List<TermEntry> termEntries = Arrays.asList(getModelObject("termEntry1", TermEntry.class));

	stubSearchTermEntriesMethod(termEntries);

	Answer<Void> answer = createAnswer(termEntries);

	doAnswer(answer).when(getTermEntryService()).exportDocumentWS(anyListOf(TermEntry.class),
		any(TermEntrySearchRequest.class), any(ExportNotificationCallback.class),
		any(ExportDocumentFactory.class));

	TmProject project = getModelObject("tmProject", TmProject.class);

	when(getProjectService().load(anyLong())).thenReturn(project);

	String requestContent = OBJECT_MAPPER.writeValueAsString(exportCommand);

	Request request = createJsonRequest(DETAILED_EXPORT_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	JsonNode jsonTermEntries = responseData.get("termEntries");

	for (JsonNode jsonTermEntry : jsonTermEntries) {
	    JsonValidatorUtils.validateJsonTermEntryContent(jsonTermEntry);
	}

	assertEquals(1, jsonTermEntries.size());
	assertNotNull(responseData.get(TIME_KEY));
    }

    /* Test case where search forbidden terms is included. */
    @Test
    @SuppressWarnings("unchecked")
    @TestCase("detailedGlossaryExport")
    public void detailedGlossaryExportTest_02() throws IOException {

	DetailedGlossaryExportCommand exportCommand = getModelObject("exportCommandForbiddenIncluded",
		DetailedGlossaryExportCommand.class);

	exportCommand.setSecurityTicket(getSecurityTicket());

	List<TermEntry> termEntries = getModelObject("termEntries", List.class);

	TmProject project = getModelObject("tmProject", TmProject.class);

	when(getProjectService().load(anyLong())).thenReturn(project);

	stubSearchTermEntriesMethod(termEntries);

	Answer<Void> answer = createAnswer(termEntries);

	doAnswer(answer).when(getTermEntryService()).exportDocumentWS(anyListOf(TermEntry.class),
		any(TermEntrySearchRequest.class), any(ExportNotificationCallback.class),
		any(ExportDocumentFactory.class));

	String requestContent = OBJECT_MAPPER.writeValueAsString(exportCommand);

	Request request = createJsonRequest(DETAILED_EXPORT_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	JsonNode jsonTermEntries = responseData.get("termEntries");
	for (JsonNode jsonTermEntry : jsonTermEntries) {
	    JsonValidatorUtils.validateJsonTermEntryContent(jsonTermEntry);
	}

	assertEquals(1, jsonTermEntries.size());
	assertNotNull(responseData.get(TIME_KEY));
    }

    public TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    private Answer<Void> createAnswer(final List<TermEntry> termEntries) {
	return new Answer<Void>() {
	    @Override
	    public Void answer(InvocationOnMock invocation) throws Throwable {
		Object[] arguments = invocation.getArguments();
		for (Object argument : arguments) {
		    if (isAssignableFrom(argument, ExportDocumentFactory.class)) {
			ExportDocumentFactory writer = (ExportDocumentFactory) argument;
			// Writes term entries to output stream
			for (TermEntry termEntry : termEntries) {
			    writer.write(termEntry, null, true, null);
			}
		    }
		}
		return null;
	    }

	    private boolean isAssignableFrom(Object argument, Class<? extends Object> clazz) {
		return Objects.nonNull(argument) && clazz.isAssignableFrom(argument.getClass());
	    }
	};
    }

    private void stubSearchTermEntriesMethod(List<TermEntry> termEntries) {
	when(getTermEntryService().searchTermEntries(any(TmgrSearchFilter.class)))
		.thenReturn(new Page<>(1, 0, 1, termEntries)).thenReturn(new Page<>(0, 0, 0, null));
    }
}
