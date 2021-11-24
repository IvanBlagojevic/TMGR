package org.gs4tr.termmanager.tests.controllers.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class SearchTermsControllerTest extends BaseRestControllerTest {

    @Test
    public void searchTermsGetTest() throws Exception {

	String projectTicket = TicketConverter.fromInternalToDto(1L);
	String term = "house";
	String userId = loginGet("pm", "test");

	MockHttpServletRequestBuilder get = get("/rest/searchTerms/en_US/fr_FR");
	get.servletPath("/rest");
	get.param("projectTicket", projectTicket);
	get.param("term", term);
	get.param("index", "0");
	get.param("indexesSize", "2");
	get.param("size", "5");
	get.param("sortDirection", "ascending");
	get.param("sortProperty", "sortPropVal");
	get.param("userId", userId);

	ResultActions resultActions = _mockMvc.perform(get);

	String result = resultActions.andReturn().getResponse().getContentAsString();

	Assert.assertNotNull(result);
    }
}