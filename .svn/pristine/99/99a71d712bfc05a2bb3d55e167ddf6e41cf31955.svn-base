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
public class SearchGridControllerTest extends AbstractControllerTest {

    @Test
    @TestCase("projectDetailSearch")
    public void searchGridTest() throws Exception {

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

	JsonNode gridConfig = resultNode.get("gridConfig");
	checkGridConfig(gridConfig);

	JsonNode gridContetnInfo = resultNode.get("gridContentInfo");
	checkGridContentInfo(gridContetnInfo);

	JsonNode readerColumns = resultNode.get("readerColumns");
	checkReaderColumns(readerColumns);

	JsonNode allProjectsSearch = resultNode.get("allProjectsSearch");
	Assert.assertNotNull(allProjectsSearch);

	JsonNode tasks = resultNode.get("tasks");
	Assert.assertNotNull(tasks);

	JsonNode gridConfigFromMetadata = resultNode.get("gridConfigFromMetadata");
	Assert.assertNotNull(gridConfigFromMetadata);

	JsonNode readerConfig = resultNode.get("readerConfig");
	checkReaderConfig(readerConfig);

    }

    private void checkGridConfig(JsonNode node) {
	Assert.assertNotNull(node);

	Iterator<JsonNode> gridConfigElements = node.elements();
	Assert.assertTrue(gridConfigElements.hasNext());

	while (gridConfigElements.hasNext()) {
	    JsonNode element = gridConfigElements.next();
	    Assert.assertNotNull(element.findValue("dataIndex"));
	    Assert.assertNotNull(element.findValue("width"));
	    Assert.assertNotNull(element.findValue("hidden"));
	    Assert.assertNotNull(element.findValue("hidden"));
	    Assert.assertNotNull(element.findValue("systemHidden"));
	    Assert.assertNotNull(element.findValue("sortable"));
	    Assert.assertNotNull(element.findValue("sortProperty"));
	    Assert.assertNotNull(element.findValue("header"));
	}
    }

    private void checkGridContentInfo(JsonNode node) {
	Assert.assertNotNull(node);

	Assert.assertNotNull(node.findValue("totalCount"));
	Assert.assertNotNull(node.findValue("hasNext"));
	Assert.assertNotNull(node.findValue("hasPrevious"));
	Assert.assertNotNull(node.findValue("totalPageCount"));

	Assert.assertNotNull(node.findValue("totalCountPerTarget"));
	Assert.assertNotNull(node.findValue("totalCountPerSource"));

	JsonNode gridConfig = node.get("pagedListInfo");
	Assert.assertNotNull(gridConfig);
	Assert.assertNotNull(gridConfig.findValue("size"));
	Assert.assertNotNull(gridConfig.findValue("index"));
	Assert.assertNotNull(gridConfig.findValue("sortProperty"));
	Assert.assertNotNull(gridConfig.findValue("sortDirection"));
	Assert.assertNotNull(gridConfig.findValue("ascending"));
	Assert.assertNotNull(gridConfig.findValue("indexesSize"));

	JsonNode pageIndexes = node.get("pageIndexes");
	Assert.assertNotNull(pageIndexes);

    }

    private void checkReaderColumns(JsonNode node) {
	Assert.assertNotNull(node);

	Iterator<JsonNode> gridConfigElements = node.elements();
	Assert.assertTrue(gridConfigElements.hasNext());

	while (gridConfigElements.hasNext()) {
	    JsonNode element = gridConfigElements.next();
	    Assert.assertNotNull(element.findValue("name"));
	    Assert.assertNotNull(element.findValue("mapping"));
	}

    }

    private void checkReaderConfig(JsonNode node) {
	Assert.assertNotNull(node);

	Assert.assertNotNull(node.findValue("id"));
	Assert.assertNotNull(node.findValue("totalProperty"));
	Assert.assertNotNull(node.findValue("root"));

    }

}
