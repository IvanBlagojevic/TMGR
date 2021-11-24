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
import org.gs4tr.foundation.modules.entities.model.search.OrganizationSearchRequest;
import org.gs4tr.termmanager.model.TmOrganization;
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
public class OrganizationSearchControllerTest extends AbstractControllerTest {

    @Test
    @TestCase("organizationSearch")
    public void organizationSearchTest() throws Exception {
	TmOrganization tmOrganization1 = getModelObject("tmOrganization1", TmOrganization.class);
	TmOrganization tmOrganization2 = getModelObject("tmOrganization2", TmOrganization.class);
	TmOrganization[] organizations = { tmOrganization1, tmOrganization2 };

	PagedList<TmOrganization> pagedList = new PagedList<TmOrganization>();
	pagedList.setElements(organizations);
	pagedList.setPagedListInfo(new PagedListInfo());
	pagedList.setTotalCount(2L);

	TaskPagedList<TmOrganization> taskPagedList = new TmTaskPagedList<TmOrganization>(pagedList);

	when(getOrganizationService().search(any(OrganizationSearchRequest.class), any(PagedListInfo.class)))
		.thenReturn(taskPagedList);

	String jsonData = getJsonData("organizationSearch.json");

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.ORGANIZATION_SEARCH);
	post.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertTrue(!result.startsWith("Application error"));

	verify(getOrganizationService(), times(1)).search(any(OrganizationSearchRequest.class),
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
	    Assert.assertNotNull(element.findValue("ticket"));
	    Assert.assertNotNull(element.findValue("availableTasks"));
	    Assert.assertNotNull(element.findValue("parentOrganization"));
	    Assert.assertNotNull(element.findValue("tasks"));

	    JsonNode organizationInfo = element.get("organizationInfo");
	    Assert.assertNotNull(organizationInfo.findValue("name"));
	    Assert.assertNotNull(organizationInfo.findValue("theme"));
	    Assert.assertNotNull(organizationInfo.findValue("enabled"));
	    Assert.assertNotNull(organizationInfo.findValue("ticket"));
	    Assert.assertNotNull(organizationInfo.findValue("domain"));
	    Assert.assertNotNull(organizationInfo.findValue("currencyCode"));

	}
    }
}
