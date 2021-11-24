package org.gs4tr.termmanager.webmvc.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Iterator;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.search.ProjectSearchRequest;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.pagedlist.TmTaskPagedList;
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
public class ProjectSearchControllerTest extends AbstractControllerTest {

    @Test
    @TestCase("projectSearch")
    public void projectSearchTest() throws Exception {
	TmProject tmProject1 = getModelObject("tmProject1", TmProject.class);
	TmProject tmProject2 = getModelObject("tmProject1", TmProject.class);
	TmProject[] organizations = { tmProject1, tmProject2 };

	PagedList<TmProject> pagedList = new PagedList<TmProject>();
	pagedList.setElements(organizations);
	pagedList.setPagedListInfo(new PagedListInfo());
	pagedList.setTotalCount(2L);

	TaskPagedList<TmProject> taskPagedList = new TmTaskPagedList<TmProject>(pagedList);

	when(getUserProfileService().findById(anyLong())).thenReturn(TmUserProfile.getCurrentUserProfile());

	when(getProjectService().search(any(ProjectSearchRequest.class), any(PagedListInfo.class)))
		.thenReturn(taskPagedList);

	String jsonData = getJsonData("projectSearch.json");

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.PROJECT_SEARCH);
	post.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertTrue(!result.startsWith("Application error"));

	verify(getProjectService(), times(1)).search(any(ProjectSearchRequest.class), any(PagedListInfo.class));

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
	    Assert.assertNotNull(element.findValue("readOnly"));
	    Assert.assertNotNull(element.findValue("ticket"));
	    Assert.assertNotNull(element.findValue("languages"));
	    Assert.assertNotNull(element.findValue("projectInfo"));

	    JsonNode projectInfoNode = element.get("projectInfo");
	    Assert.assertNotNull(projectInfoNode);
	    Assert.assertNotNull(projectInfoNode.findValue("name"));
	    Assert.assertNotNull(projectInfoNode.findValue("enabled"));
	    Assert.assertNotNull(projectInfoNode.findValue("shortCode"));
	    Assert.assertNotNull(projectInfoNode.findValue("clientIdentifier"));

	    Assert.assertNotNull(element.findValue("organizationName"));
	}
    }
}
