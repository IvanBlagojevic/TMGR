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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.http.HttpTester;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.webservice.model.request.BatchSegmentTermSearchCommand;
import org.gs4tr.termmanager.webservice.utils.JsonValidatorUtils;
import org.gs4tr.tm3.api.Page;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import edu.emory.mathcs.backport.java.util.Arrays;

@TestSuite("webservice")
public class BatchSegmentTermSearchControllerTest extends AbstractWebServiceTest {
    private static final String BATCH_SEGMENT_TERM_SEARCH_URL = "rest/v2/batchSegmentTermSearch";

    @Test
    @TestCase("batchSegmentTermSearch")
    public void segmentTermSearchTestFailed() throws IOException {
	Class<BatchSegmentTermSearchCommand> clazz = BatchSegmentTermSearchCommand.class;
	BatchSegmentTermSearchCommand invalidCommnad = getModelObject("invalidSearchCommand", clazz);

	String[] segment = { "This is simple sentence." };

	invalidCommnad.setBatchSegment(Arrays.asList(segment));

	invalidCommnad.setSecurityTicket(getSecurityTicket());

	String requestContent = OBJECT_MAPPER.writeValueAsString(invalidCommnad);

	HttpTester.Request request = createJsonRequest(BATCH_SEGMENT_TERM_SEARCH_URL, requestContent);

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(400, response.getStatus());
	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertEquals("Target languages can't be empty or null.", responseData.get("errorMessage").asText());
	assertFalse(responseData.get("success").asBoolean());
	assertNull(responseData.get("segmentTermHits"));
    }

    @Test
    @TestCase("batchSegmentTermSearch")
    @SuppressWarnings("unchecked")
    public void testResponseForMoreThan_1000_Segments() throws IOException {
	Class<BatchSegmentTermSearchCommand> clazz = BatchSegmentTermSearchCommand.class;
	BatchSegmentTermSearchCommand searchCommnad = getModelObject("searchCommand", clazz);

	searchCommnad.setSecurityTicket(getSecurityTicket());

	addSegmentsToCommand(searchCommnad, 1001);

	TmProject project = getModelObject("tmProject", TmProject.class);
	List<TermEntry> termEntries = getModelObject("termEntries", List.class);
	Page<TermEntry> page = new Page<>(termEntries.size(), 0, 100, termEntries);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getTermEntyService().segmentTMSearch(any(TmgrSearchFilter.class))).thenReturn(page);

	String requestContent = OBJECT_MAPPER.writeValueAsString(searchCommnad);

	HttpTester.Request request = createJsonRequest(BATCH_SEGMENT_TERM_SEARCH_URL, requestContent);

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(400, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());
	assertFalse(responseData.get("success").asBoolean());
	assertEquals(5, responseData.get("returnCode").asInt());
	assertEquals("You can't send more than 1000 segments.", responseData.get("errorMessage").asText());
    }

    /*
     * TERII-5172 | CLONE - LRP-TMGR_5.1.0_RC1| Terms are not highlighted
     */
    @Test
    @TestCase("batchSegmentTermSearch")
    @SuppressWarnings("unchecked")
    public void testResponseForNonExportableTerms() throws IOException {
	Class<BatchSegmentTermSearchCommand> clazz = BatchSegmentTermSearchCommand.class;
	BatchSegmentTermSearchCommand searchCommnad = getModelObject("searchCommand", clazz);

	int segmentNum = 15;

	searchCommnad.setSecurityTicket(getSecurityTicket());

	addSegmentsToCommand(searchCommnad, segmentNum);

	TmProject project = getModelObject("tmProject", TmProject.class);
	List<TermEntry> termEntries = getModelObject("termEntries1", List.class);

	Map<String, Set<Term>> languageTerms = termEntries.get(0).getLanguageTerms();
	Set<Term> terms = languageTerms.get("en-US");

	assertTrue(terms.size() > 1);

	Iterator<Term> termIterator = terms.iterator();

	/* Skip first term (First term should be exportable to reproduce this bug) */
	termIterator.next();

	/*
	 * Set term status to In Final Review. (Second term should not be exportable to
	 * reproduce this bug)
	 */
	termIterator.next().setStatus(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName());
	Page<TermEntry> page = new Page<>(termEntries.size(), 0, 100, termEntries);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getTermEntyService().segmentTMSearch(any(TmgrSearchFilter.class))).thenReturn(page);

	String requestContent = OBJECT_MAPPER.writeValueAsString(searchCommnad);

	HttpTester.Request request = createJsonRequest(BATCH_SEGMENT_TERM_SEARCH_URL, requestContent);

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());
	assertTrue(responseData.get("success").asBoolean());
	JsonNode batchSegmentTermHits = responseData.get("batchSegmentTermHits");
	assertEquals(segmentNum, batchSegmentTermHits.size());
	for (JsonNode batchSegmentTermHit : batchSegmentTermHits) {
	    JsonNode segmentTermHits = batchSegmentTermHit.get("segmentTermHits");
	    for (JsonNode segmentTermHit : segmentTermHits) {
		JsonValidatorUtils.validateSegmentTermHitContent(segmentTermHit);
	    }
	}
	assertNotNull(responseData.get("time"));
    }

    @Test
    @TestCase("batchSegmentTermSearch")
    @SuppressWarnings("unchecked")
    public void testResponseForRegularSegmentNumber() throws IOException {
	Class<BatchSegmentTermSearchCommand> clazz = BatchSegmentTermSearchCommand.class;
	BatchSegmentTermSearchCommand searchCommand = getModelObject("searchCommand", clazz);

	int segmentNum = 15;

	searchCommand.setSecurityTicket(getSecurityTicket());

	addSegmentsToCommand(searchCommand, segmentNum);

	TmProject project = getModelObject("tmProject", TmProject.class);
	List<TermEntry> termEntries = getModelObject("termEntries1", List.class);
	Page<TermEntry> page = new Page<>(termEntries.size(), 0, 100, termEntries);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getTermEntyService().segmentTMSearch(any(TmgrSearchFilter.class))).thenReturn(page);

	String requestContent = OBJECT_MAPPER.writeValueAsString(searchCommand);

	HttpTester.Request request = createJsonRequest(BATCH_SEGMENT_TERM_SEARCH_URL, requestContent);

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());
	assertTrue(responseData.get("success").asBoolean());
	JsonNode batchSegmentTermHits = responseData.get("batchSegmentTermHits");
	assertEquals(segmentNum, batchSegmentTermHits.size());
	for (JsonNode batchSegmentTermHit : batchSegmentTermHits) {
	    JsonNode segmentTermHits = batchSegmentTermHit.get("segmentTermHits");
	    for (JsonNode segmentTermHit : segmentTermHits) {
		JsonValidatorUtils.validateSegmentTermHitContent(segmentTermHit);
	    }
	}
	assertNotNull(responseData.get("time"));
    }

    @Test
    @TestCase("batchSegmentTermSearch")
    @SuppressWarnings("unchecked")
    public void testResponseWithoutBatchSegment() throws IOException {
	Class<BatchSegmentTermSearchCommand> clazz = BatchSegmentTermSearchCommand.class;
	BatchSegmentTermSearchCommand searchCommnad = getModelObject("invalidSearchCommand", clazz);

	searchCommnad.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("tmProject", TmProject.class);
	List<TermEntry> termEntries = getModelObject("termEntries", List.class);
	Page<TermEntry> page = new Page<>(termEntries.size(), 0, 100, termEntries);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getTermEntyService().segmentTMSearch(any(TmgrSearchFilter.class))).thenReturn(page);

	String requestContent = OBJECT_MAPPER.writeValueAsString(searchCommnad);

	HttpTester.Request request = createJsonRequest(BATCH_SEGMENT_TERM_SEARCH_URL, requestContent);

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());
	assertTrue(responseData.get("success").asBoolean());
	JsonNode batchSegmentTermHits = responseData.get("batchSegmentTermHits");
	assertNull(batchSegmentTermHits);
    }

    private void addSegmentsToCommand(BatchSegmentTermSearchCommand command, int segmentNum) {
	List<String> batchSegment = new ArrayList<>();
	for (int i = 0; i < segmentNum; i++) {
	    batchSegment.add("This is segment num " + (i + 1) + ".");
	}
	command.setBatchSegment(batchSegment);
    }

}
