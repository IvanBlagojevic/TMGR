package org.gs4tr.termmanager.tests.controllers.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Calendar;

import javax.servlet.http.HttpServletResponse;

import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class DetailedExportDocumentControllerTest extends BaseRestControllerTest {

    private static final String URL = "/rest/detailedExport";

    @Test
    public void exportDetailedDocumentGetTest() throws Exception {
	Calendar calendar = Calendar.getInstance();
	calendar.set(2011, 5, 23);
	String projectTicket = TicketConverter.fromInternalToDto(1L);
	String userId = loginGet("pm", "test");

	MockHttpServletRequestBuilder get = get(URL);

	get.param("projectTicket", projectTicket);
	get.param("sourceLocale", "en-US");
	get.param("targetLocale", "de-DE");
	get.param("descriptionType", "context");
	get.param("exportForbiddenTerms", "false");
	get.param("afterDate", "0");
	get.param("userId", userId);

	ResultActions result = _mockMvc.perform(get);
	MockHttpServletResponse response = result.andReturn().getResponse();

	Assert.assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, response.getContentType());
	Assert.assertNotNull(response.getContentAsString());
    }

    @Test
    public void wfExportDetailedDocumentGetTest() throws Exception {
	Calendar calendar = Calendar.getInstance();
	calendar.set(2011, 5, 23);
	Long timeInMilliseconds = 0L;
	String projectTicket = TicketConverter.fromInternalToDto(1L);
	String userId = loginGet("pm", "test");

	MockHttpServletRequestBuilder get = get(URL);
	get.param("afterDate", String.valueOf(timeInMilliseconds));
	get.param("projectTicket", projectTicket);
	get.param("sourceLocale", "en-US");
	get.param("targetLocale", "de-DE");
	get.param("descriptionType", "context");
	get.param("exportAllDescriptions", "true");
	get.param("exportFormat", "JSON");
	get.param("exportForbiddenTerms", "false");
	get.param("blacklistTermsCount", "0");
	get.param("userId", userId);

	ResultActions result = _mockMvc.perform(get);
	MockHttpServletResponse response = result.andReturn().getResponse();

	Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());

	Assert.assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
    }
}