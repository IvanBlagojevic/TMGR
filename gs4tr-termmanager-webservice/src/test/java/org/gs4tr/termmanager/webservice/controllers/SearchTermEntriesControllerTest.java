package org.gs4tr.termmanager.webservice.controllers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.INVALID_PARAMETERS;
import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.RETURN_CODE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.DescriptionFilter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.webservice.model.request.ConcordanceSearchCommand;
import org.gs4tr.termmanager.webservice.utils.JsonValidatorUtils;
import org.gs4tr.tm3.api.Page;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.hazelcast.util.CollectionUtil;

@TestSuite("webservice")
public class SearchTermEntriesControllerTest extends AbstractWebServiceTest {
    private static final String SEARCH_TERM_ENTRIES_URL = "rest/v2/searchTermEntries";

    /*
     * TERII-5940 restV2 | searchTermEntries is not working
     */
    @Test
    @TestCase("concordanceSearch")
    @SuppressWarnings("unchecked")
    public void searchTermEntriesByStatusApproved() throws IOException {
	ConcordanceSearchCommand concordanceCommand = getModelObject("concordanceCommandStatus",
		ConcordanceSearchCommand.class);
	concordanceCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("tmProject", TmProject.class);
	List<TermEntry> termEntries = getModelObject("termEntries", List.class);
	Page<TermEntry> page = new Page<TermEntry>(1, 0, 1, termEntries);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getTermEntyService().searchTermEntries(any(TmgrSearchFilter.class))).thenReturn(page);

	String requestContent = OBJECT_MAPPER.writeValueAsString(concordanceCommand);

	Request request = createJsonRequest(SEARCH_TERM_ENTRIES_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);
	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());
	assertTrue(responseData.get("success").asBoolean());

	JsonNode hits = responseData.get("termEntryHits");
	hits.forEach(JsonValidatorUtils::validateTermEntryHitContent);

	assertEquals(1, hits.size());
	assertNotNull(responseData.get("time"));
	assertNotNull(responseData.get("itemsPerPage"));
	assertNotNull(responseData.get("pageIndex"));
	assertNotNull(responseData.get("totalResults"));
    }

    @Test
    @TestCase("concordanceSearch")
    public void searchTermEntriesFailedTest() throws IOException {
	ConcordanceSearchCommand command = getModelObject("invalidConcordanceCommand", ConcordanceSearchCommand.class);
	command.setSecurityTicket(getSecurityTicket());

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	Request request = createJsonRequest(SEARCH_TERM_ENTRIES_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(400, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());
	assertFalse(responseData.get("success").asBoolean());

	JsonNode returnCode = responseData.get(RETURN_CODE);
	assertEquals(INVALID_PARAMETERS, returnCode.asInt());

	assertNotNull(responseData.get("time"));
	assertEquals("Project ticket can't be empty or null.", responseData.get("errorMessage").asText());
    }

    @Test
    @TestCase("concordanceSearch")
    @SuppressWarnings("unchecked")
    public void searchTermEntriesTest_01() throws IOException {
	ConcordanceSearchCommand concordanceCommand = getModelObject("concordanceCommand",
		ConcordanceSearchCommand.class);
	concordanceCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("tmProject", TmProject.class);
	List<TermEntry> termEntries = getModelObject("termEntries", List.class);
	Page<TermEntry> page = new Page<TermEntry>(1, 0, 1, termEntries);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getTermEntyService().searchTermEntries(any(TmgrSearchFilter.class))).thenReturn(page);

	String requestContent = OBJECT_MAPPER.writeValueAsString(concordanceCommand);

	Request request = createJsonRequest(SEARCH_TERM_ENTRIES_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);
	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());
	assertTrue(responseData.get("success").asBoolean());

	JsonNode hits = responseData.get("termEntryHits");
	hits.forEach(JsonValidatorUtils::validateTermEntryHitContent);

	assertEquals(1, hits.size());
	assertNotNull(responseData.get("time"));
	assertNotNull(responseData.get("itemsPerPage"));
	assertNotNull(responseData.get("pageIndex"));
	assertNotNull(responseData.get("totalResults"));
    }

    @Test
    @TestCase("concordanceSearch")
    @SuppressWarnings("unchecked")
    public void searchTermEntriesTest_02() throws IOException {
	ConcordanceSearchCommand command = getModelObject("concordanceCommand", ConcordanceSearchCommand.class);
	command.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("tmProject", TmProject.class);
	List<TermEntry> termEntries = getModelObject("termEntries1", List.class);
	Page<TermEntry> page = new Page<TermEntry>(2, 0, 1, termEntries);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getTermEntyService().searchTermEntries(any(TmgrSearchFilter.class))).thenReturn(page);

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	Request request = createJsonRequest(SEARCH_TERM_ENTRIES_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());
	assertTrue(responseData.get("success").asBoolean());

	JsonNode hits = responseData.get("termEntryHits");
	hits.forEach(JsonValidatorUtils::validateTermEntryHitContent);

	assertEquals(2, hits.size());
	assertNotNull(responseData.get("time"));
	assertNotNull(responseData.get("itemsPerPage"));
	assertNotNull(responseData.get("pageIndex"));
	assertNotNull(responseData.get("totalResults"));
    }

    @Test
    @TestCase("concordanceSearch")
    public void searchTermEntriesWithDescriptionFilter() throws JsonProcessingException {
	ConcordanceSearchCommand command = getModelObject("concordanceCommandDescription",
		ConcordanceSearchCommand.class);
	command.setSecurityTicket(getSecurityTicket());
	assertEquals(1, command.getDescriptions().size());

	TmProject project = getModelObject("tmProject", TmProject.class);
	List<TermEntry> termEntries = getModelObject("termEntries2", List.class);
	Page<TermEntry> page = new Page<TermEntry>(1, 0, 1, termEntries);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getTermEntyService().searchTermEntries(any(TmgrSearchFilter.class))).thenReturn(page);

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	Request request = createJsonRequest(SEARCH_TERM_ENTRIES_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	ArgumentCaptor<TmgrSearchFilter> argument = ArgumentCaptor.forClass(TmgrSearchFilter.class);
	verify(getTermEntyService()).searchTermEntries(argument.capture());

	List<DescriptionFilter> descriptionFilters = argument.getValue().getDescriptionFilters();
	assertNotNull(descriptionFilters);
	assertTrue(CollectionUtil.isNotEmpty(descriptionFilters));
	assertEquals(1, descriptionFilters.size());

	assertEquals(200, response.getStatus());

    }
}
