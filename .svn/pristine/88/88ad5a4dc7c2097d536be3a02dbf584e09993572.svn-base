package org.gs4tr.termmanager.webmvc.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Iterator;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.security.model.search.RoleSearchRequest;
import org.gs4tr.termmanager.model.pagedlist.TmTaskPagedList;
import org.gs4tr.termmanager.service.RoleService;
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
public class RoleSearchControllerTest extends AbstractControllerTest {

    @Autowired
    private RoleService _roleService;

    public RoleService getRoleService() {
	return _roleService;
    }

    @Test
    @TestCase("roleSearch")
    public void roleSearchTest() throws Exception {

	Role role1 = getModelObject("role1", Role.class);
	Role role2 = getModelObject("role2", Role.class);
	Role role3 = getModelObject("role3", Role.class);
	Role[] organizations = { role1, role2, role3 };

	PagedList<Role> pagedList = new PagedList<Role>();
	pagedList.setElements(organizations);
	pagedList.setPagedListInfo(new PagedListInfo());
	pagedList.setTotalCount(2L);

	TaskPagedList<Role> taskPagedList = new TmTaskPagedList<Role>(pagedList);

	when(getRoleService().search(any(RoleSearchRequest.class), any(PagedListInfo.class))).thenReturn(taskPagedList);

	String jsonData = getJsonData("roleSearch.json");

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.ROLE_SEARCH);
	post.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertTrue(!result.startsWith("Application error"));

	verify(getRoleService(), times(1)).search(any(RoleSearchRequest.class), any(PagedListInfo.class));

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
	    Assert.assertNotNull(element.findValue("roleId"));
	    Assert.assertNotNull(element.findValue("tasks"));
	    Assert.assertNotNull(element.findValue("roleType"));

	    JsonNode roleTypeNode = element.get("roleType");
	    Assert.assertNotNull(roleTypeNode);
	    Assert.assertNotNull(roleTypeNode.findValue("value"));

	    JsonNode policiesNode = element.get("policies");
	    Assert.assertNotNull(policiesNode);

	    Iterator<JsonNode> policies = policiesNode.elements();
	    Assert.assertTrue(policies.hasNext());

	    while (policies.hasNext()) {
		JsonNode policy = policies.next();
		Assert.assertNotNull(policy.findValue("category"));
		Assert.assertNotNull(policy.findValue("policyId"));
		Assert.assertNotNull(policy.findValue("policyType"));

		JsonNode policyTypeNode = policy.get("policyType");
		Assert.assertNotNull(policyTypeNode);
		Assert.assertNotNull(policyTypeNode.findValue("value"));
	    }
	}
    }

    public void setRoleService(RoleService roleService) {
	_roleService = roleService;
    }

    @Before
    public void setUp() throws Exception {
	reset(getRoleService());
    }

}
