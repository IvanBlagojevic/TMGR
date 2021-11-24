package org.gs4tr.termmanager.webservice.controllers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.webservice.model.request.SegmentTermSearchCommand;
import org.gs4tr.termmanager.webservice.utils.JsonValidatorUtils;
import org.gs4tr.tm3.api.Page;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("webservice")
public class SegmentTermSearchControllerTest extends AbstractWebServiceTest {

    private static final String SEGMENT_TERM_SEARCH_URL = "rest/v2/segmentTermSearch";

    @Test
    @TestCase("segmentTermSearch")
    @SuppressWarnings("unchecked")
    public void highlightSegmentTermSearchResults() throws IOException {

	SegmentTermSearchCommand command = getModelObject("highlightTermSearchCommand", SegmentTermSearchCommand.class);
	command.setSecurityTicket(getSecurityTicket());
	assertEquals(command.getSegment(), "I like to walk. Walking is my favorite hobby.");

	List<TermEntry> termEntries = getModelObject("termEntries2", List.class);
	TmProject project = getModelObject("tmProject", TmProject.class);
	Page<TermEntry> page = new Page<>(termEntries.size(), 0, 100, termEntries);

	when(getTermEntyService().segmentTMSearch(any(TmgrSearchFilter.class))).thenReturn(page);
	when(getProjectService().load(anyLong())).thenReturn(project);

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	Request request = createJsonRequest(SEGMENT_TERM_SEARCH_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);
	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	JsonNode segmentHits = responseData.get("termHits");
	assertNotNull(segmentHits);
	assertEquals(segmentHits.size(), 1);
	assertEquals(segmentHits.get(0).get("termHit").asText(), "walk");

	JsonNode ranges = segmentHits.get(0).get("ranges");
	assertNotNull(ranges);
	assertEquals(ranges.size(), 2);

	assertEquals(ranges.get(0).get("fuzzy").asText(), "false");
	assertEquals(ranges.get(0).get("start").asInt(), 10);
	assertEquals(ranges.get(0).get("length").asInt(), 4);

	assertEquals(ranges.get(1).get("fuzzy").asText(), "true");
	assertEquals(ranges.get(1).get("start").asInt(), 16);
	assertEquals(ranges.get(1).get("length").asInt(), 7);
    }

    @Test
    @TestCase("segmentTermSearch")
    public void segmentTermSearchTestFailed() throws IOException {
	Class<SegmentTermSearchCommand> clazz = SegmentTermSearchCommand.class;

	SegmentTermSearchCommand command = getModelObject("invalidTermSearchCommand", clazz);
	command.setSecurityTicket(getSecurityTicket());

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	Request request = createJsonRequest(SEGMENT_TERM_SEARCH_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(400, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertFalse(responseData.get("success").asBoolean());
	assertNull(responseData.get("segmentTermHits"));
    }

    @Test
    @TestCase("segmentTermSearch")
    @SuppressWarnings("unchecked")
    public void segmentTermSearchTest_01() throws IOException {
	Class<SegmentTermSearchCommand> clazz = SegmentTermSearchCommand.class;

	SegmentTermSearchCommand command = getModelObject("termSearchCommand", clazz);
	command.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("tmProject", TmProject.class);
	List<TermEntry> termEntries = getModelObject("termEntries", List.class);
	Page<TermEntry> page = new Page<>(termEntries.size(), 0, 100, termEntries);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getTermEntyService().segmentTMSearch(any(TmgrSearchFilter.class))).thenReturn(page);

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	Request request = createJsonRequest(SEGMENT_TERM_SEARCH_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());
	assertTrue(responseData.get("success").asBoolean());

	JsonNode segmentTermHits = responseData.get("termHits");
	segmentTermHits.forEach(JsonValidatorUtils::validateSegmentTermHitContent);

	assertEquals(1, segmentTermHits.size());
	assertNotNull(responseData.get("time"));
    }

    @Test
    @TestCase("segmentTermSearch")
    @SuppressWarnings("unchecked")
    public void segmentTermSearchTest_02() throws IOException {
	Class<SegmentTermSearchCommand> clazz = SegmentTermSearchCommand.class;
	SegmentTermSearchCommand command = getModelObject("termSearchCommand", clazz);

	command.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("tmProject", TmProject.class);
	List<TermEntry> termEntries = getModelObject("termEntries1", List.class);
	Page<TermEntry> page = new Page<>(termEntries.size(), 0, 100, termEntries);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getTermEntyService().segmentTMSearch(any(TmgrSearchFilter.class))).thenReturn(page);

	String requestContent = OBJECT_MAPPER.writeValueAsString(command);

	Request request = createJsonRequest(SEGMENT_TERM_SEARCH_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());
	assertTrue(responseData.get("success").asBoolean());

	JsonNode segmentTermHits = responseData.get("termHits");
	segmentTermHits.forEach(JsonValidatorUtils::validateSegmentTermHitContent);

	assertEquals(3, segmentTermHits.size());
	assertNotNull(responseData.get("time"));
    }
}
