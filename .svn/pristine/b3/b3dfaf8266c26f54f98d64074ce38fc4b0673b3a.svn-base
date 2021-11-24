package org.gs4tr.termmanager.webmvc.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Iterator;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.search.command.SubmissionLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.SubmissionLanguageDetailView;
import org.gs4tr.termmanager.service.SubmissionLanguageDetailViewService;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("controllers")
public class SubmissionLanguageDetailSearchControllerTest extends AbstractControllerTest {

    @Autowired
    private SubmissionLanguageDetailViewService _submissionLanguageDetailViewService;

    public SubmissionLanguageDetailViewService getSubmissionLanguageDetailViewService() {
	return _submissionLanguageDetailViewService;
    }

    public void setSubmissionLanguageDetailViewService(
	    SubmissionLanguageDetailViewService submissionLanguageDetailViewService) {
	_submissionLanguageDetailViewService = submissionLanguageDetailViewService;
    }

    @Before
    public void setUp() throws Exception {
	reset(getSubmissionDetailViewService());
	mockObjects();
    }

    @Test
    @TestCase("submissionLanguageDetailSearch")
    public void submissionLanguageDetailSearchTest() throws Exception {

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders
		.post("/" + UrlConstants.SUBMISSION_LANGUAGE_DETAIL_SEARCH);
	post.param("ticket", IdEncrypter.encryptGenericId(1));

	ResultActions resultActions = _mockMvc.perform(post);

	verify(getSubmissionLanguageDetailViewService(), atLeastOnce())
		.search(any(SubmissionLanguageDetailRequest.class), any(PagedListInfo.class));

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();
	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	validateResultsNode(resultNode);

    }

    @Test
    @TestCase("submissionLanguageDetailSearch")
    public void submissionLanguageDetailSearchWithoutTicketTest() throws Exception {
	MockHttpServletRequestBuilder post = MockMvcRequestBuilders
		.post("/" + UrlConstants.SUBMISSION_LANGUAGE_DETAIL_SEARCH);

	ResultActions resultActions = _mockMvc.perform(post);

	verify(getSubmissionLanguageDetailViewService(), atLeastOnce())
		.search(any(SubmissionLanguageDetailRequest.class), any(PagedListInfo.class));

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();
	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	validateResultsNode(resultNode);

    }

    private void mockObjects() {
	SubmissionLanguageDetailView detailView1 = getModelObject("view1", SubmissionLanguageDetailView.class);
	SubmissionLanguageDetailView detailView2 = getModelObject("view2", SubmissionLanguageDetailView.class);

	SubmissionLanguageDetailView[] array = { detailView1, detailView2 };

	PagedList<SubmissionLanguageDetailView> pagedList = new PagedList<SubmissionLanguageDetailView>();
	pagedList.setElements(array);
	pagedList.setTotalCount(2L);
	pagedList.setPagedListInfo(new PagedListInfo());

	TaskPagedList<SubmissionLanguageDetailView> taskPagedList = new TaskPagedList<SubmissionLanguageDetailView>(
		pagedList);

	when(getSubmissionLanguageDetailViewService().search(any(SubmissionLanguageDetailRequest.class),
		any(PagedListInfo.class))).thenReturn(taskPagedList);
    }

    private void validateResultsNode(JsonNode resultNode) {
	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());

	JsonNode itemsNode = resultNode.get("items");
	Assert.assertNotNull(itemsNode);

	Iterator<JsonNode> elements = itemsNode.elements();
	Assert.assertTrue(elements.hasNext());

	while (elements.hasNext()) {
	    JsonNode element = elements.next();
	    Assert.assertNotNull(element.findValue("dateCompleted"));
	    Assert.assertNotNull(element.findValue("dateSubmitted"));
	    Assert.assertNotNull(element.findValue("markerId"));
	    Assert.assertNotNull(element.findValue("languageId"));
	    Assert.assertNotNull(element.findValue("termCanceledCount"));
	    Assert.assertNotNull(element.findValue("termCompletedCount"));
	    Assert.assertNotNull(element.findValue("termInFinalReviewCount"));
	    Assert.assertNotNull(element.findValue("termInTranslationCount"));
	    Assert.assertNotNull(element.findValue("comments"));
	    Assert.assertNotNull(element.findValue("status"));
	    Assert.assertNotNull(element.findValue("status"));
	    Assert.assertNotNull(element.findValue("dateModified"));
	    Assert.assertNotNull(element.findValue("termCount"));
	    Assert.assertNotNull(element.findValue("assignee"));
	    Assert.assertNotNull(element.findValue("canceled"));

	    JsonNode commentsNode = element.get("comments");
	    Assert.assertNotNull(commentsNode);

	    Iterator<JsonNode> comments = commentsNode.elements();
	    Assert.assertTrue(comments.hasNext());

	    while (comments.hasNext()) {
		JsonNode comment = comments.next();
		Assert.assertNotNull(comment.findValue("text"));
		Assert.assertNotNull(comment.findValue("user"));
		Assert.assertNotNull(comment.findValue("markerId"));
		Assert.assertNotNull(comment.findValue("commentTicket"));
		Assert.assertNotNull(comment.findValue("commentId"));
	    }

	}
    }
}