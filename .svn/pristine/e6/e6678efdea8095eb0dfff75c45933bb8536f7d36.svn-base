package org.gs4tr.termmanager.webmvc.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.search.command.ProjectLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.ProjectLanguageDetailView;
import org.gs4tr.termmanager.service.ProjectLanguageDetailService;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("controllers")
public class ProjectLanguageDetailSearchControllerTest extends AbstractControllerTest {

    @Autowired
    private ProjectLanguageDetailService _projectLanguageDetailService;

    public ProjectLanguageDetailService getProjectLanguageDetailService() {
	return _projectLanguageDetailService;
    }

    @Test
    @TestCase("projectLanguageDetailSearch")
    public void projectLanguageDetailSearch() throws Exception {

	ProjectLanguageDetailView detailView1 = getModelObject("view1", ProjectLanguageDetailView.class);
	ProjectLanguageDetailView detailView2 = getModelObject("view2", ProjectLanguageDetailView.class);

	ProjectLanguageDetailView[] array = { detailView1, detailView2 };

	PagedList<ProjectLanguageDetailView> pagedList = new PagedList<ProjectLanguageDetailView>();
	pagedList.setElements(array);
	pagedList.setTotalCount(2L);
	pagedList.setPagedListInfo(new PagedListInfo());

	TaskPagedList<ProjectLanguageDetailView> taskPagedList = new TaskPagedList<ProjectLanguageDetailView>(
		pagedList);

	when(getProjectLanguageDetailService().search(any(ProjectLanguageDetailRequest.class),
		any(PagedListInfo.class))).thenReturn(taskPagedList);

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders
		.post("/" + UrlConstants.PROJECT_LANGUAGE_DETAIL_SEARCH);
	post.param("ticket", IdEncrypter.encryptGenericId(1));

	ResultActions resultActions = _mockMvc.perform(post);

	verify(getProjectLanguageDetailService(), times(1)).search(any(ProjectLanguageDetailRequest.class),
		any(PagedListInfo.class));

	resultActions.andExpect(status().isOk());

	String result = resultActions.andReturn().getResponse().getContentAsString();

	Assert.assertTrue(StringUtils.isNotBlank(result));
	Assert.assertTrue(!result.startsWith("Application error"));

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

	    JsonNode languageNode = element.get("language");
	    Assert.assertNotNull(languageNode);

	    Iterator<JsonNode> langElements = languageNode.elements();
	    Assert.assertTrue(langElements.hasNext());

	    while (langElements.hasNext()) {
		JsonNode langElement = langElements.next();
		Assert.assertNotNull(langElement);
	    }

	    Assert.assertNotNull(itemsNode.findValue("approvedTermCount"));

	    /*
	     * TERII-3208 : Dashboard | Add Pending Approval and On Hold columns
	     */
	    Assert.assertNotNull(itemsNode.findValue("pendingApprovalTermCount"));
	    Assert.assertNotNull(itemsNode.findValue("onHoldTermCount"));

	    Assert.assertNotNull(itemsNode.findValue("completedSubmissionCount"));
	    Assert.assertNotNull(itemsNode.findValue("dateModified"));
	    Assert.assertNotNull(itemsNode.findValue("forbiddenTermCount"));
	    Assert.assertNotNull(itemsNode.findValue("termCount"));
	    Assert.assertNotNull(itemsNode.findValue("termInSubmissionCount"));
	    Assert.assertNotNull(itemsNode.findValue("ticket"));
	    Assert.assertNotNull(itemsNode.findValue("activeSubmissionCount"));

	}

    }

    public void setProjectLanguageDetailService(ProjectLanguageDetailService projectLanguageDetailService) {
	_projectLanguageDetailService = projectLanguageDetailService;
    }

    @Before
    public void setUp() throws Exception {
	reset(getProjectLanguageDetailService());
    }

}
