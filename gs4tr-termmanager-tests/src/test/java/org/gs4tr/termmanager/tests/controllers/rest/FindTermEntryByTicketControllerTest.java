package org.gs4tr.termmanager.tests.controllers.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class FindTermEntryByTicketControllerTest extends BaseRestControllerTest {

    private static final String URL = "/rest/termEntry";

    @Test
    public void findTermEntryByTicketGetTest() throws Exception {

	String termEntryTicket = TicketConverter.fromInternalToDto(10L);
	String userId = loginGet("pm", "test");

	MockHttpServletRequestBuilder get = get(URL);
	get.servletPath("/rest");
	get.param("termEntryTicket", termEntryTicket);
	get.param("userId", userId);

	ResultActions resultActions = _mockMvc.perform(get);

	String result = resultActions.andReturn().getResponse().getContentAsString();

	Assert.assertNotNull(result);

	System.out.println(result);
    }
}