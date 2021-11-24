package org.gs4tr.termmanager.webmvc.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Iterator;
import java.util.List;

import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("controllers")
public class ProjectDefaultStatusControllerTest extends AbstractControllerTest {

    private static final String PROJECT_COMBO_BOX = "projectComboBox";

    private static final String URL_VARIABLES = "projectDefaultTermStatus.ter";

    @Test
    @TestCase("projectDefaultStatus")
    public void browseStatusForTwoProjectsTest() throws Exception {
	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL_VARIABLES);

	String[] projectTickets = { IdEncrypter.encryptGenericId(0), IdEncrypter.encryptGenericId(1) };

	post.param(PROJECT_COMBO_BOX, projectTickets);

	ResultActions resultActions = _mockMvc.perform(post);
	resultActions.andExpect(status().isOk());

	verify(getProjectService(), times(1)).findProjectByIds(anyListOf(Long.class));

	MockHttpServletResponse response = resultActions.andReturn().getResponse();

	String result = response.getContentAsString();
	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);

	JsonNode projectsInfos = resultNode.get("projectsInfos");

	assertNotNull(projectsInfos);
	assertEquals(2, projectsInfos.size());

	Iterator<JsonNode> iterator = projectsInfos.iterator();
	while (iterator.hasNext()) {
	    JsonNode projectInfoModel = iterator.next();
	    validateProjectInfoModel(projectInfoModel);
	}
    }

    @Test
    @TestCase("projectDefaultStatus")
    public void browseStatusOnPendingApprovalProjectTest() throws Exception {
	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL_VARIABLES);

	String[] projectTickets = { IdEncrypter.encryptGenericId(1) };

	post.param(PROJECT_COMBO_BOX, projectTickets);

	ResultActions resultActions = _mockMvc.perform(post);
	resultActions.andExpect(status().isOk());

	verify(getProjectService(), times(1)).findProjectByIds(anyListOf(Long.class));

	MockHttpServletResponse response = resultActions.andReturn().getResponse();

	String result = response.getContentAsString();
	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);

	JsonNode projectsInfos = resultNode.get("projectsInfos");

	assertNotNull(projectsInfos);
	assertEquals(1, projectsInfos.size());

	Iterator<JsonNode> iterator = projectsInfos.iterator();
	while (iterator.hasNext()) {
	    JsonNode projectInfoModel = iterator.next();
	    validateProjectInfoModel(projectInfoModel);
	}
    }

    @Test
    @TestCase("projectDefaultStatus")
    public void browseStatusUserContainProjectDefaultPolicyTest() throws Exception {

	/* Set project default status to Approved */
	List<Long> projectIds = getModelObject("projectIds", List.class);
	List<TmProject> projects = getModelObject("projects", List.class);
	projects.get(0).setDefaultTermStatus(ItemStatusTypeHolder.PROCESSED);
	when(getProjectService().findProjectByIds(projectIds)).thenReturn(projects);

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL_VARIABLES);

	String[] projectTickets = { IdEncrypter.encryptGenericId(1) };

	post.param(PROJECT_COMBO_BOX, projectTickets);

	ResultActions resultActions = _mockMvc.perform(post);
	resultActions.andExpect(status().isOk());

	verify(getProjectService(), times(1)).findProjectByIds(anyListOf(Long.class));

	MockHttpServletResponse response = resultActions.andReturn().getResponse();

	String result = response.getContentAsString();
	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);

	JsonNode projectsInfos = resultNode.get("projectsInfos");

	assertNotNull(projectsInfos);
	assertEquals(1, projectsInfos.size());

	Iterator<JsonNode> iterator = projectsInfos.iterator();

	JsonNode projectInfoModel = iterator.next();

	assertEquals(ItemStatusTypeHolder.PROCESSED.toString(), projectInfoModel.get("defaultTermStatus").asText());
    }

    @Before
    public void setUp() throws Exception {
	reset(getProjectService());
	mockObjects();
    }

    @SuppressWarnings("unchecked")
    private void mockObjects() {
	List<Long> projectIds = getModelObject("projectIds", List.class);
	List<Long> projectIds1 = getModelObject("projectIds1", List.class);
	List<TmProject> projects = getModelObject("projects", List.class);
	List<TmProject> projects1 = getModelObject("projects1", List.class);

	when(getProjectService().findProjectByIds(projectIds)).thenReturn(projects);
	when(getProjectService().findProjectByIds(projectIds1)).thenReturn(projects1);
    }

    private void validateDefaultTermStatus(JsonNode projectInfoModel) {
	JsonNode projectTicket = projectInfoModel.get("projectTicket");
	Long projectId = TicketConverter.fromDtoToInternal(projectTicket.asText(), Long.class);
	if (projectId.equals(1L)) {
	    assertEquals(ItemStatusTypeHolder.ON_HOLD.toString(), projectInfoModel.get("defaultTermStatus").asText());
	} else {
	    assertEquals(ItemStatusTypeHolder.ON_HOLD.toString(), projectInfoModel.get("defaultTermStatus").asText());
	}
    }

    private void validateProjectInfoModel(JsonNode projectInfoModel) {
	assertNotNull(projectInfoModel);
	assertNotNull(projectInfoModel.get("actionsAvailable"));
	assertNotNull(projectInfoModel.get("deleteAvailable"));
	assertNotNull(projectInfoModel.get("addEditApproved"));
	assertNotNull(projectInfoModel.get("addEditPending"));
	assertNotNull(projectInfoModel.get("defaultTermStatus"));
	assertNotNull(projectInfoModel.get("projectTicket"));
	validateDefaultTermStatus(projectInfoModel);
    }
}
