package org.gs4tr.termmanager.tests.controllers.mvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.AbstractSpringServiceTests;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("controllers")
public class UserProfileSearchControllerTest extends AbstractSpringServiceTests {

    @Test
    @TestCase("userProfileSearch")
    public void userProfileSearchTest() throws Exception {

	String jsonData = getJsonData("userProfileSearch.json");

	String currentUserUsername = TmUserProfile.getCurrentUserName();

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/userProfileSearch.ter");
	post.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(post);

	String result = resultActions.andReturn().getResponse().getContentAsString();
	assertTrue(StringUtils.isNotEmpty(result));

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	assertNotNull(resultNode);

	JsonNode itemsNode = resultNode.get("items");
	assertNotNull(itemsNode);

	Iterator<JsonNode> elements = itemsNode.elements();

	while (elements.hasNext()) {

	    JsonNode element = elements.next();
	    String username = element.findValue("userInfo").get("userName").asText();

	    if (currentUserUsername.equals(username)) {
		continue;
	    }

	    JsonNode dateLastLogin = element.findValue("userInfo").get("dateLastLogin");
	    assertEquals(dateLastLogin.toString(), "null");

	}

    }

}
