package org.gs4tr.termmanager.tests.controllers.mvc;

import java.util.Iterator;

import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.AbstractSpringServiceTests;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

public class ProjectLanguageDetailSearchControllerTest extends AbstractSpringServiceTests {

    @Test
    public void searchTest() throws Exception {

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/projectLanguageDetailView.ter");
	post.param("ticket", IdEncrypter.encryptGenericId(1));

	ResultActions resultActions = _mockMvc.perform(post);

	String result = resultActions.andReturn().getResponse().getContentAsString();

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	JsonNode itemsNode = resultNode.get("items");
	Assert.assertNotNull(itemsNode);

	Iterator<JsonNode> elements = itemsNode.elements();
	Assert.assertTrue(elements.hasNext());

	while (elements.hasNext()) {
	    JsonNode element = elements.next();
	    Assert.assertNotNull(element.findValue("language"));
	}
    }
}