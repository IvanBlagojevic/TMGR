package org.gs4tr.termmanager.webmvc.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.service.PluginsService;
import org.gs4tr.termmanager.service.manualtask.AddProjectTaskHandler;
import org.gs4tr.termmanager.service.manualtask.AddRoleTaskHandler;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandlerUtils;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestCommand;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("controllers")
public class TaskControllerTest extends AbstractControllerTest {

    private static final String TASK_POST_URL = "taskPost.ter";
    private static final String TASK_URL = "task.ter";

    @Mock
    private AddProjectTaskHandler _addProjectTaskHandler;

    @Mock
    private AddRoleTaskHandler _addRoleTaskHandler;

    @Mock
    private Authentication _authentication;

    @Autowired
    private PluginsService _pluginsService;

    @Mock
    private SecurityContext _securityContext;

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("taskController")
    public void addOrganizationPostTest() throws Exception {

	Ticket ticket = new Ticket("some role");
	TaskResponse taskResponse = new TaskResponse(ticket);

	when(getPluginsService().getUserTaskHandler(any(String.class))).thenReturn(getAddRoleTaskHandler());
	when(getAddRoleTaskHandler().getCommandClass()).then(new Answer<Class<TestCommand>>() {

	    @Override
	    public Class<TestCommand> answer(InvocationOnMock invocation) throws Throwable {
		return TestCommand.class;
	    }
	});
	when(getAddRoleTaskHandler().processTasks(any(Long[].class), any(Long[].class), any(Object.class),
		any(List.class))).thenReturn(taskResponse);

	String jsonTaskData = getJsonData("addRoleTask.json");
	String jsonData = getJsonData("addRole.json");

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + TASK_URL);
	post.param("jsonTaskData", jsonTaskData);
	post.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	verify(getPluginsService(), times(1)).getUserTaskHandler(any(String.class));

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());

	JsonNode taskResponseNode = resultNode.get("taskResponse");
	Assert.assertNotNull(taskResponseNode);

	Assert.assertNotNull(taskResponseNode.findValue("repositoryItem"));
	Assert.assertNotNull(taskResponseNode.findValue("model"));
	Assert.assertNotNull(taskResponseNode.findValue("responseTicket"));

	JsonNode responseTicketNode = taskResponseNode.get("responseTicket");
	Assert.assertNotNull(responseTicketNode);
	Assert.assertNotNull(responseTicketNode.findValue("ticketId"));
    }

    @Test
    @TestCase("taskController")
    public void addProjectTaskGetTest() throws Exception {
	TmOrganization tmOrganization1 = getModelObject("tmOrganization1", TmOrganization.class);
	TmOrganization tmOrganization2 = getModelObject("tmOrganization2", TmOrganization.class);
	List<TmOrganization> organizations = new ArrayList<TmOrganization>();
	organizations.add(tmOrganization1);
	organizations.add(tmOrganization2);

	@SuppressWarnings("unchecked")
	List<String> allProjectRoleIds = getModelObject("allProjectRoleIds", List.class);

	List<Map<String, String>> termStatuses = ManualTaskHandlerUtils.createTermStatusModel();

	TaskModel model = new TaskModel();
	model.addObject("organizationName", organizations);
	model.addObject("projectRoles", allProjectRoleIds);
	model.addObject(ManualTaskHandlerUtils.TERM_STATUSES, termStatuses);
	model.addObject(ManualTaskHandlerUtils.DEFAULT_TERM_STATUS, ItemStatusTypeHolder.PROCESSED.getName());

	TaskModel[] models = { model };

	when(getPluginsService().getUserTaskHandler(any(String.class))).thenReturn(getAddProjectTaskHandler());
	when(getAddProjectTaskHandler().getTaskInfos(any(Long[].class), any(String.class), any(Object.class)))
		.thenReturn(models);

	String jsonData = getJsonData("addProject.json");

	MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/" + TASK_URL);
	get.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(get);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	verify(getPluginsService(), times(1)).getUserTaskHandler(any(String.class));
	verify(getAddProjectTaskHandler(), times(1)).getTaskInfos(any(Long[].class), any(String.class),
		any(Object.class));

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());

	JsonNode taskInfoNode = resultNode.get("taskInfos");
	Assert.assertNotNull(taskInfoNode);

	Iterator<JsonNode> elements = taskInfoNode.elements();
	Assert.assertTrue(elements.hasNext());

	while (elements.hasNext()) {
	    JsonNode element = elements.next();
	    Assert.assertNotNull(element.findValue("model"));
	    Assert.assertNotNull(element.findValue("repositoryItem"));
	    Assert.assertNotNull(element.findValue("parentEntityTicket"));
	    Assert.assertNotNull(element.findValue("taskTicket"));

	    JsonNode modelNode = element.get("model");
	    Assert.assertNotNull(modelNode.findValue("defaultTermStatus"));

	    JsonNode termStatusesNode = modelNode.get("termStatuses");
	    checkTermStatuses(termStatusesNode);

	    JsonNode organizationNode = modelNode.get("organizationName");
	    checkOrganizationsNode(organizationNode);

	    JsonNode projectRolesNode = modelNode.get("projectRoles");
	    Assert.assertNotNull(projectRolesNode);

	    Iterator<JsonNode> projectRoles = projectRolesNode.elements();
	    Assert.assertTrue(projectRoles.hasNext());

	    while (projectRoles.hasNext()) {
		JsonNode node = projectRoles.next();
		Assert.assertNotNull(node);
	    }
	}

    }

    @Test
    @TestCase("taskController")
    public void addProjectTaskPostTest() throws Exception {
	when(getPluginsService().getUserTaskHandler(any(String.class))).thenReturn(getAddProjectTaskHandler());

	String jsonData = getJsonData("addProject.json");

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + TASK_POST_URL);
	post.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	verify(getPluginsService(), times(1)).getUserTaskHandler(any(String.class));

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());
    }

    public AddProjectTaskHandler getAddProjectTaskHandler() {
	return _addProjectTaskHandler;
    }

    public AddRoleTaskHandler getAddRoleTaskHandler() {
	return _addRoleTaskHandler;
    }

    public Authentication getAuthentication() {
	return _authentication;
    }

    public PluginsService getPluginsService() {
	return _pluginsService;
    }

    public SecurityContext getSecurityContext() {
	return _securityContext;
    }

    /*
     * TERII-5118 Target languages combo box doesn't refresh after import
     */
    @SuppressWarnings("unchecked")
    @Test
    @TestCase("taskController")
    public void importTbxSumoLogicProxyTest() throws Exception {

	AspectJProxyFactory factory = new AspectJProxyFactory(new ImportTbxDocumentTaskHandlerDummy());

	/* Create Import document task handler proxy wrapper */
	ManualTaskHandler importTbxDocumentTaskHandler = factory.getProxy();

	when(getPluginsService().getUserTaskHandler(anyString())).thenReturn(importTbxDocumentTaskHandler);
	when(getAddRoleTaskHandler().getCommandClass()).then(new Answer<Class<TestCommand>>() {

	    @Override
	    public Class<TestCommand> answer(InvocationOnMock invocation) throws Throwable {
		return TestCommand.class;
	    }
	});

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + TASK_URL);

	// when(getAuthentication().getPrincipal()).thenReturn(new
	// DefaultUserDetails<>(new TmUserProfile()));

	when(getAuthentication().isAuthenticated()).thenReturn(true);

	when(getSecurityContext().getAuthentication()).thenReturn(getAuthentication());

	// when(getUserProfileService().loadUserByUsername(anyString()))
	// .thenReturn(new DefaultUserDetails<>(new TmUserProfile()));

	MockHttpSession session = new MockHttpSession();
	session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, getSecurityContext());

	post.session(session);

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	verify(getPluginsService(), times(1)).getUserTaskHandler(any(String.class));

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);

    }

    public void setAddProjectTaskHandler(AddProjectTaskHandler addProjectTaskHandler) {
	_addProjectTaskHandler = addProjectTaskHandler;
    }

    public void setAddRoleTaskHandler(AddRoleTaskHandler addRoleTaskHandler) {
	_addRoleTaskHandler = addRoleTaskHandler;
    }

    public void setPluginsService(PluginsService pluginsService) {
	_pluginsService = pluginsService;
    }

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);
	reset(getPluginsService());
	reset(getOrganizationService());
	reset(getAddProjectTaskHandler());
	reset(getAuthentication());
	reset(getSecurityContext());
    }

    private void checkOrganizationsNode(JsonNode node) {
	Assert.assertNotNull(node);

	Iterator<JsonNode> elements = node.elements();
	Assert.assertTrue(elements.hasNext());

	while (elements.hasNext()) {
	    JsonNode element = elements.next();
	    Assert.assertNotNull(element.findValue("users"));
	    Assert.assertNotNull(element.findValue("projects"));
	    Assert.assertNotNull(element.findValue("identifier"));
	    Assert.assertNotNull(element.findValue("organizationInfo"));
	    Assert.assertNotNull(element.findValue("childOrganizations"));
	    Assert.assertNotNull(element.findValue("organizationId"));
	    Assert.assertNotNull(element.findValue("parentOrganization"));

	    JsonNode organizationInfo = element.get("organizationInfo");
	    Assert.assertNotNull(organizationInfo.findValue("name"));
	    Assert.assertNotNull(organizationInfo.findValue("theme"));
	    Assert.assertNotNull(organizationInfo.findValue("domain"));
	    Assert.assertNotNull(organizationInfo.findValue("currencyCode"));
	    Assert.assertNotNull(organizationInfo.findValue("enabled"));

	}
    }

    private void checkTermStatuses(JsonNode node) {
	Assert.assertNotNull(node);

	Iterator<JsonNode> statuses = node.elements();
	Assert.assertTrue(statuses.hasNext());

	while (statuses.hasNext()) {
	    JsonNode status = statuses.next();
	    Assert.assertNotNull(status.findValue("name"));
	    Assert.assertNotNull(status.findValue("value"));
	}
    }

}
