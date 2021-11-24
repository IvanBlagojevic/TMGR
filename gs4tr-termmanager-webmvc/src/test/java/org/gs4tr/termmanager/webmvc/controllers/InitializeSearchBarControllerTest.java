package org.gs4tr.termmanager.webmvc.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

public class InitializeSearchBarControllerTest extends AbstractControllerTest {

    private static final boolean GRID_FEATURE = false;

    private static final String URL = "initializeSearchBar.ter";

    @Test
    public void initializeSearchBarTest() throws Exception {
	MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/" + URL);

	ResultActions resultActions = _mockMvc.perform(get);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());

	JsonNode searchBarNode = resultNode.get("searchBar");
	Assert.assertNotNull(searchBarNode);

	if (GRID_FEATURE) {
	    validateGridSearchBar(searchBarNode);

	} else {
	    validateSearchBarNode(searchBarNode);
	}

    }

    private void validateGridSearchBar(JsonNode searchBarNode) {
	Assert.assertNotNull(searchBarNode.findValue("USER_LAST_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("EMAIL_ADDRESS"));
	Assert.assertNotNull(searchBarNode.findValue("SUBMISSION_USERS"));
	Assert.assertNotNull(searchBarNode.findValue("DATE_COMPLETED_RANGE"));
	Assert.assertNotNull(searchBarNode.findValue("TL_TERM_STATUSES"));
	Assert.assertNotNull(searchBarNode.findValue("TL_ENTITY_TYPE"));
	Assert.assertNotNull(searchBarNode.findValue("TL_TERM_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("TERM_STATUSES"));
	Assert.assertNotNull(searchBarNode.findValue("ENTITY_TYPE"));
	Assert.assertNotNull(searchBarNode.findValue("TERM_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("SUBMISSION_STATUSES"));
	Assert.assertNotNull(searchBarNode.findValue("SUBMISSION_TERM_LANGUAGE"));
	Assert.assertNotNull(searchBarNode.findValue("LANGUAGE_SET_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("TL_DATE_MODIFIED_RANGE"));
	Assert.assertNotNull(searchBarNode.findValue("DATE_MODIFIED_RANGE"));
	Assert.assertNotNull(searchBarNode.findValue("SUBMISSION_PROJECT_LIST"));
	Assert.assertNotNull(searchBarNode.findValue("TL_DATE_CREATED_RANGE"));
	Assert.assertNotNull(searchBarNode.findValue("DATE_CREATED_RANGE"));
	Assert.assertNotNull(searchBarNode.findValue("USER_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("ORGANIZATION_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("TL_LANGUAGE_DIRECTION"));
	Assert.assertNotNull(searchBarNode.findValue("TL_PROJECT_LIST"));
	Assert.assertNotNull(searchBarNode.findValue("PROJECT_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("USER_FIRST_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("ROLE_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("SUBMISSION_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("LANGUAGE_DIRECTION_SUBMISSION"));
    }

    private void validateSearchBarNode(JsonNode searchBarNode) {
	Assert.assertNotNull(searchBarNode.findValue("USER_LAST_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("EMAIL_ADDRESS"));
	Assert.assertNotNull(searchBarNode.findValue("SUBMISSION_USERS"));
	Assert.assertNotNull(searchBarNode.findValue("DATE_COMPLETED_RANGE"));
	Assert.assertNotNull(searchBarNode.findValue("TERM_STATUSES"));
	Assert.assertNotNull(searchBarNode.findValue("ENTITY_TYPE"));
	Assert.assertNotNull(searchBarNode.findValue("TERM_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("SUBMISSION_STATUSES"));
	Assert.assertNotNull(searchBarNode.findValue("SUBMISSION_TERM_LANGUAGE"));
	Assert.assertNotNull(searchBarNode.findValue("LANGUAGE_SET_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("DATE_MODIFIED_RANGE"));
	Assert.assertNotNull(searchBarNode.findValue("SUBMISSION_PROJECT_LIST"));
	Assert.assertNotNull(searchBarNode.findValue("DATE_CREATED_RANGE"));
	Assert.assertNotNull(searchBarNode.findValue("USER_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("ORGANIZATION_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("LANGUAGE_DIRECTION"));
	Assert.assertNotNull(searchBarNode.findValue("PROJECT_LIST"));
	Assert.assertNotNull(searchBarNode.findValue("PROJECT_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("USER_FIRST_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("ROLE_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("SUBMISSION_NAME"));
	Assert.assertNotNull(searchBarNode.findValue("LANGUAGE_DIRECTION_SUBMISSION"));
    }

}
