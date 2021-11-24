package org.gs4tr.termmanager.webmvc.rest;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.export.ExportDocumentFactory;
import org.gs4tr.termmanager.webmvc.controllers.AbstractControllerTest;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.gs4tr.termmanager.webmvc.rest.utils.RestConstants;
import org.gs4tr.tm3.api.Page;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@TestSuite("rest")
public class DetailedExportDocumentControllerTest extends AbstractControllerTest {

    private static final String AFTER_DATE_LONG = "1396338186000"; //$NON-NLS-1$

    private static final String CONTEXT = "context"; //$NON-NLS-1$

    private static final long PROJECT_ID = 1L;

    private static final String URL = "/rest/detailedExport"; //$NON-NLS-1$

    @Test
    @TestCase("detailedExport")
    public void detailedExportCSVTest() throws Exception {
	String projectTicket = TicketConverter.fromInternalToDto(1L);
	String userId = loginGet("pm", "test");

	MockHttpServletRequestBuilder get = get(URL);
	// required = true
	get.param("projectTicket", projectTicket);
	get.param("exportForbiddenTerms", "false");
	get.param("userId", userId);
	// required = false
	get.param("sourceLocale", "en-US");
	get.param("targetLocale", "de-DE");
	get.param("exportFormat", RestConstants.CSV);
	get.param("descriptionType", CONTEXT);
	get.param("exportAllDescriptions", "true");
	get.param("blacklistTermsCount", "0");
	get.param("afterDate", AFTER_DATE_LONG);

	@SuppressWarnings("unchecked")
	List<TermEntry> termentries = getModelObject("termentries", List.class);
	Page<TermEntry> page = new Page<TermEntry>(1, 0, 1, termentries);

	when(getTermEntryService().searchTermEntries(any(TmgrSearchFilter.class))).thenReturn(page)
		.thenReturn(new Page<TermEntry>(0, 0, 0, null));

	ResultActions result = _mockMvc.perform(get);

	verify(getProjectService()).load(any(Long.class));
	verify(getTermEntryService(), times(2)).searchTermEntries(any(TmgrSearchFilter.class));

	MockHttpServletResponse response = result.andReturn().getResponse();

	java.util.Locale source = new java.util.Locale("en", "US");
	Assert.assertEquals(source, response.getLocale());
	Assert.assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, response.getContentType());
	Assert.assertEquals("UTF-8", response.getCharacterEncoding());
	Assert.assertNotNull(response.getContentAsString());
	Assert.assertEquals(200, response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("detailedExport")
    public void detailedExportJSONTest() throws Exception {
	String projectTicket = TicketConverter.fromInternalToDto(1L);
	String userId = loginGet("pm", "test");

	MockHttpServletRequestBuilder get = get(URL);
	// required = true
	get.param("projectTicket", projectTicket);
	get.param("exportForbiddenTerms", "true");
	get.param("userId", userId);
	// required = false
	get.param("sourceLocale", "en-US");
	get.param("targetLocale", "de-DE");
	get.param("exportFormat", RestConstants.JSON);
	get.param("descriptionType", CONTEXT);
	get.param("exportAllDescriptions", "true");
	get.param("blacklistTermsCount", "0");
	get.param("afterDate", AFTER_DATE_LONG);

	List<TermEntry> termentries = getModelObject("termentries", List.class);
	Page<TermEntry> page = new Page<TermEntry>(1, 0, 1, termentries);

	List<TermEntry> blackListTermentries = getModelObject("blackListTermentries", List.class);
	Page<TermEntry> blackListPage = new Page<TermEntry>(1, 0, 1, blackListTermentries);

	when(getTermEntryService().searchTermEntries(any(TmgrSearchFilter.class))).thenReturn(page)
		.thenReturn(new Page<TermEntry>(0, 0, 0, null)).thenReturn(blackListPage)
		.thenReturn(new Page<TermEntry>(0, 0, 0, null));

	when(getTermEntryService().getNumberOfTerms(any(TmgrSearchFilter.class), any())).thenReturn(1L);

	ResultActions result = _mockMvc.perform(get);

	verify(getProjectService()).load(any(Long.class));
	verify(getTermEntryService(), times(4)).searchTermEntries(any(TmgrSearchFilter.class));
	verify(getTermEntryService(), times(1)).exportForbiddenTerms(anyListOf(Term.class),
		any(ExportDocumentFactory.class));

	MockHttpServletResponse response = result.andReturn().getResponse();

	java.util.Locale source = new java.util.Locale("en", "US");
	Assert.assertEquals(source, response.getLocale());
	Assert.assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
	Assert.assertEquals("UTF-8", response.getCharacterEncoding());
	Assert.assertNotNull(response.getContentAsString());
	Assert.assertEquals(200, response.getStatus());
    }

    /*
     * TERII-4162: Web Services - TMGR sends terms of non-existent language to
     * Wordfast.
     */
    @Test
    @TestCase("detailedExport")
    public void detailedExportJSONWhenSourceLocaleDoesnotExist() throws Exception {
	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);
	String userId = loginGet("pm", "test");

	MockHttpServletRequestBuilder request = get(URL);
	request.param("projectTicket", projectTicket);
	request.param("userId", userId);

	// User does not have 'en' assign on the project
	request.param("sourceLocale", Locale.ENGLISH.getCode());
	request.param("targetLocale", Locale.GERMANY.getCode());

	request.param("exportForbiddenTerms", String.valueOf(false));
	request.param("exportAllDescriptions", String.valueOf(true));

	request.param("exportFormat", RestConstants.JSON);
	request.param("afterDate", AFTER_DATE_LONG);
	request.param("descriptionType", CONTEXT);

	ResultActions result = _mockMvc.perform(request);
	// Because this request fails on validation
	verify(getTermEntryService(), times(0)).searchTermEntries(any(TmgrSearchFilter.class));

	MockHttpServletResponse response = result.andReturn().getResponse();
	Assert.assertEquals(500, response.getStatus());

	String responseContent = response.getContentAsString().replaceAll("\"", "");
	Assert.assertEquals(
		String.format(Messages.getString("DetailedExportDocumentController.14"), Locale.ENGLISH.getCode()),
		responseContent);
    }

    @Before
    public void mockTmProject() throws Exception {
	TmProject tmProject = getModelObject("tmProject", TmProject.class);
	when(getProjectService().load(any(Long.class))).thenReturn(tmProject);
    }
}
