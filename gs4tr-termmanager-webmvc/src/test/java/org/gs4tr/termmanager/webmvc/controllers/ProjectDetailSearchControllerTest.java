package org.gs4tr.termmanager.webmvc.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Iterator;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.termmanager.model.search.command.UserProjectSearchRequest;
import org.gs4tr.termmanager.model.view.ProjectDetailView;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("controllers")
public class ProjectDetailSearchControllerTest extends AbstractControllerTest {

    @Test
    @TestCase("projectDetailSearch")
    public void projectDetailSearch() throws Exception {

	@SuppressWarnings("unchecked")
	PagedList<ProjectDetailView> pagedList = getModelObject("pagedList", PagedList.class);

	TaskPagedList<ProjectDetailView> taskPagedList = new TaskPagedList<ProjectDetailView>(pagedList);

	when(getProjectDetailService().search(any(UserProjectSearchRequest.class), any(PagedListInfo.class)))
		.thenReturn(taskPagedList);

	String jsonData = getJsonData("projectDetailSearch.json");

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.PROJECT_DETAIL_SEARCH);
	post.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertTrue(!result.startsWith("Application error"));

	verify(getProjectDetailService(), times(1)).search(any(UserProjectSearchRequest.class),
		any(PagedListInfo.class));

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());

	JsonNode itemsNode = resultNode.get("items");
	Assert.assertNotNull(itemsNode);

	Iterator<JsonNode> elements = itemsNode.elements();
	Assert.assertTrue(elements.hasNext());

	while (elements.hasNext()) {
	    JsonNode element = elements.next();
	    Assert.assertNotNull(element.findValue("name"));
	    Assert.assertNotNull(element.findValue("ticket"));
	    Assert.assertNotNull(element.findValue("activeSubmissionCount"));
	    Assert.assertNotNull(element.findValue("approvedTermCount"));
	    Assert.assertNotNull(element.findValue("projectTicket"));
	    /*
	     * TERII-3208 : Dashboard | Add Pending Approval and On Hold columns
	     */
	    Assert.assertNotNull(element.findValue("pendingApprovalTermCount"));
	    Assert.assertNotNull(element.findValue("onHoldTermCount"));

	    Assert.assertNotNull(element.findValue("completedSubmissionCount"));
	    Assert.assertNotNull(element.findValue("dateModified"));
	    Assert.assertNotNull(element.findValue("forbiddenTermCount"));
	    Assert.assertNotNull(element.findValue("languageCount"));
	    Assert.assertNotNull(element.findValue("shortCode"));
	    Assert.assertNotNull(element.findValue("termCount"));
	    Assert.assertNotNull(element.findValue("termEntryCount"));
	    Assert.assertNotNull(element.findValue("termInSubmissionCount"));
	}
    }

}