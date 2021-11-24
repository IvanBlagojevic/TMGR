package org.gs4tr.termmanager.tests.controllers.mvc;

import java.util.Iterator;

import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.AbstractSolrGlossaryTest;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("bilingual_translation")
public class BilingualTranslationViewSearchControllerTest extends AbstractSolrGlossaryTest {

    @Test
    @TestCase("search")
    public void searchTest() throws Exception {
	Long submissionID = 1L;
	String submissionTicket = IdEncrypter.encryptGenericId(submissionID);
	String source = "en-US";
	String target = "de-DE";

	String jsonData = getJsonData("bilingualTranslation.json",
		new String[] { "$submissionTicket", submissionTicket },
		new String[] { "$submissionTermLanguage", target }, new String[] { "$sourceLanguage", source });

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.MULTILINGUAL_SEARCH);
	post.param("jsonData", jsonData);

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
	    Assert.assertNotNull(element.findValue("targetTerms"));
	}
    }
}