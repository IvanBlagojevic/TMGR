package org.gs4tr.termmanager.webmvc.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Iterator;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.termmanager.model.search.command.SubmissionSearchRequest;
import org.gs4tr.termmanager.model.view.SubmissionDetailView;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("controllers")
public class SubmissionDetailSearchControllerTest extends AbstractControllerTest {

    @Test
    @TestCase("submissionDetailSearch")
    public void projectDetailSearchWithBlanksTest() throws Exception {
	String jsonData = getJsonData("submissionDetailSearchWithBlanks.json");

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.SUBMISSION_DETAIL_SEARCH);
	post.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	verify(getSubmissionDetailViewService(), atLeastOnce()).search(any(SubmissionSearchRequest.class),
		any(PagedListInfo.class));

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	validateResult(resultNode);
    }

    @Test
    @TestCase("submissionDetailSearch")
    public void projectDetailSearchWithDateTest() throws Exception {
	String jsonData = getJsonData("submissionDetailSearchWithDate.json");

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.SUBMISSION_DETAIL_SEARCH);
	post.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	verify(getSubmissionDetailViewService(), atLeastOnce()).search(any(SubmissionSearchRequest.class),
		any(PagedListInfo.class));

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	validateResult(resultNode);
    }

    @Test
    @TestCase("submissionDetailSearch")
    public void projectDetailSearchWithNullsTest() throws Exception {
	String jsonData = getJsonData("submissionDetailSearchWithNulls.json");

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.SUBMISSION_DETAIL_SEARCH);
	post.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	verify(getSubmissionDetailViewService(), atLeastOnce()).search(any(SubmissionSearchRequest.class),
		any(PagedListInfo.class));

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	validateResult(resultNode);
    }

    @Test
    @TestCase("submissionDetailSearch")
    public void projectDetailSearchWithoutDateTest() throws Exception {
	String jsonData = getJsonData("submissionDetailSearch.json");

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.SUBMISSION_DETAIL_SEARCH);
	post.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	verify(getSubmissionDetailViewService(), atLeastOnce()).search(any(SubmissionSearchRequest.class),
		any(PagedListInfo.class));

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	validateResult(resultNode);
    }

    @Before
    @TestCase("submissionDetailSearch")
    public void setUp() throws Exception {
	mockObjects();
    }

    @SuppressWarnings("unchecked")
    private void mockObjects() {
	PagedList<SubmissionDetailView> pagedList = getModelObject("pagedList", PagedList.class);
	TaskPagedList<SubmissionDetailView> taskPagedList = new TaskPagedList<SubmissionDetailView>(pagedList);
	when(getSubmissionDetailViewService().search(any(SubmissionSearchRequest.class), any(PagedListInfo.class)))
		.thenReturn(taskPagedList);
    }

    private void validateResult(JsonNode resultNode) {
	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());

	JsonNode itemsNode = resultNode.get("items");
	Assert.assertNotNull(itemsNode);

	Iterator<JsonNode> elements = itemsNode.elements();
	Assert.assertTrue(elements.hasNext());

	while (elements.hasNext()) {
	    JsonNode element = elements.next();
	    Assert.assertNotNull(element.findValue("canceled"));
	    Assert.assertNotNull(element.findValue("projectName"));
	    Assert.assertNotNull(element.findValue("submitter"));
	    Assert.assertNotNull(element.findValue("submissionName"));
	    Assert.assertNotNull(element.findValue("submissionId"));
	    Assert.assertNotNull(element.findValue("dateModified"));
	    Assert.assertNotNull(element.findValue("termEntryCount"));
	    Assert.assertNotNull(element.findValue("assignees"));
	    Assert.assertNotNull(element.findValue("dateSubmitted"));
	    Assert.assertNotNull(element.findValue("sourceLanguageId"));
	    Assert.assertNotNull(element.findValue("targetLanguageIds"));
	    Assert.assertNotNull(element.findValue("markerId"));
	    Assert.assertNotNull(element.findValue("dateCompleted"));
	    Assert.assertNotNull(element.findValue("availableTasks"));
	    Assert.assertNotNull(element.findValue("status"));
	    Assert.assertNotNull(element.findValue("projectTicket"));
	    Assert.assertNotNull(element.findValue("ticket"));

	}
    }
}