package org.gs4tr.termmanager.webmvc.rest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;
import java.util.Locale;

import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.webmvc.controllers.AbstractControllerTest;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.gs4tr.termmanager.webmvc.rest.utils.RestConstants;
import org.gs4tr.tm3.api.Page;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@TestSuite("rest")
public class ExportDocumentControllerTest extends AbstractControllerTest {

    private static final String URL = "/rest/export"; //$NON-NLS-1$

    // Currently create file on ROOT need to check that and than remove Ignore
    // annotation
    @Ignore
    @Test
    @TestCase("export")
    public void exportDocumentCSVSYNCTest_01() throws Exception {
	mockObjects();

	String projectTicket = TicketConverter.fromInternalToDto(1L);
	String userId = loginGet("pm", "test");

	MockHttpServletRequestBuilder get = get(URL);
	get.param("projectTicket", projectTicket);
	get.param("exportFormat", RestConstants.CSVSYNC);
	get.param("userId", userId);
	get.param("sourceLocale", "en-US");
	get.param("targetLocale", "de-DE");
	get.param("generateStatistics", "true");

	@SuppressWarnings("unchecked")
	List<TermEntry> termentries = getModelObject("termEntryList", List.class);
	Page<TermEntry> page = new Page<TermEntry>(1, 0, 1, termentries);

	when(getTermEntryService().searchTermEntries(any(TmgrSearchFilter.class))).thenReturn(page)
		.thenReturn(new Page<TermEntry>(0, 0, 0, null));

	ResultActions result = _mockMvc.perform(get);

	verifyMockings();
	verify(getTermEntryService(), times(2)).searchTermEntries(any(TmgrSearchFilter.class));

	MockHttpServletResponse response = result.andReturn().getResponse();

	Locale source = new Locale("en", "US");

	Assert.assertEquals(source, response.getLocale());
	Assert.assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, response.getContentType());
	Assert.assertEquals("ISO-8859-1", response.getCharacterEncoding());
	Assert.assertNotNull(response.getContentAsString());
	Assert.assertEquals(200, response.getStatus());
    }

    @Test
    @TestCase("export")
    public void exportDocumentCSVTest_01() throws Exception {
	mockObjects();

	String projectTicket = TicketConverter.fromInternalToDto(1L);
	String userId = loginGet("pm", "test");

	MockHttpServletRequestBuilder get = get(URL);
	get.param("projectTicket", projectTicket);
	get.param("exportFormat", RestConstants.CSV);
	get.param("userId", userId);
	get.param("sourceLocale", "en-US");
	get.param("targetLocale", "de-DE");
	get.param("generateStatistics", "true");

	@SuppressWarnings("unchecked")
	List<TermEntry> termentries = getModelObject("termEntryList", List.class);
	Page<TermEntry> page = new Page<TermEntry>(1, 0, 1, termentries);

	when(getTermEntryService().searchTermEntries(any(TmgrSearchFilter.class))).thenReturn(page)
		.thenReturn(new Page<TermEntry>(0, 0, 0, null));

	ResultActions result = _mockMvc.perform(get);

	verifyMockings();
	verify(getTermEntryService(), times(2)).searchTermEntries(any(TmgrSearchFilter.class));

	MockHttpServletResponse response = result.andReturn().getResponse();

	Locale source = new Locale("en", "US");

	Assert.assertEquals(source, response.getLocale());
	Assert.assertEquals(MediaType.TEXT_PLAIN_VALUE, response.getContentType());
	Assert.assertEquals("UTF-8", response.getCharacterEncoding());
	Assert.assertNotNull(response.getContentAsString());
	Assert.assertEquals(200, response.getStatus());
    }

    @Test
    @TestCase("export")
    public void exportDocumentJSONTest_01() throws Exception {
	mockObjects();

	String projectTicket = TicketConverter.fromInternalToDto(1L);
	String userId = loginGet("pm", "test");

	MockHttpServletRequestBuilder get = get(URL);
	get.param("projectTicket", projectTicket);
	get.param("exportFormat", RestConstants.JSON);
	get.param("userId", userId);
	get.param("sourceLocale", "en-US");
	get.param("targetLocale", "de-DE");
	get.param("generateStatistics", "true");

	@SuppressWarnings("unchecked")
	List<TermEntry> termentries = getModelObject("termEntryList", List.class);
	Page<TermEntry> page = new Page<TermEntry>(1, 0, 1, termentries);

	when(getTermEntryService().searchTermEntries(any(TmgrSearchFilter.class))).thenReturn(page)
		.thenReturn(new Page<TermEntry>(0, 0, 0, null));

	ResultActions result = _mockMvc.perform(get);

	verifyMockings();
	verify(getTermEntryService(), times(2)).searchTermEntries(any(TmgrSearchFilter.class));

	MockHttpServletResponse response = result.andReturn().getResponse();

	Locale source = new Locale("en", "US");

	Assert.assertEquals(source, response.getLocale());
	Assert.assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
	Assert.assertEquals("UTF-8", response.getCharacterEncoding());
	Assert.assertNotNull(response.getContentAsString());
	Assert.assertEquals(200, response.getStatus());
    }

    @Test
    @TestCase("export")
    public void exportDocumentTABTest_01() throws Exception {
	mockObjects();

	String projectTicket = TicketConverter.fromInternalToDto(1L);
	String userId = loginGet("pm", "test");

	MockHttpServletRequestBuilder get = get(URL);
	get.param("projectTicket", projectTicket);
	get.param("exportFormat", RestConstants.TAB);
	get.param("userId", userId);
	get.param("sourceLocale", "en-US");
	get.param("targetLocale", "de-DE");
	get.param("generateStatistics", "true");

	@SuppressWarnings("unchecked")
	List<TermEntry> termentries = getModelObject("termEntryList", List.class);
	Page<TermEntry> page = new Page<TermEntry>(1, 0, 1, termentries);

	when(getTermEntryService().searchTermEntries(any(TmgrSearchFilter.class))).thenReturn(page)
		.thenReturn(new Page<TermEntry>(0, 0, 0, null));

	ResultActions result = _mockMvc.perform(get);

	verifyMockings();
	verify(getTermEntryService(), times(2)).searchTermEntries(any(TmgrSearchFilter.class));

	MockHttpServletResponse response = result.andReturn().getResponse();

	Locale source = new Locale("en", "US");

	Assert.assertEquals(source, response.getLocale());
	Assert.assertEquals(MediaType.TEXT_PLAIN_VALUE, response.getContentType());
	Assert.assertEquals("UTF-8", response.getCharacterEncoding());
	Assert.assertNotNull(response.getContentAsString());
	Assert.assertEquals(200, response.getStatus());
    }

    @Test
    @TestCase("export")
    public void exportDocumentTBXTest_01() throws Exception {
	mockObjects();

	String projectTicket = TicketConverter.fromInternalToDto(1L);
	String userId = loginGet("pm", "test");

	MockHttpServletRequestBuilder get = get(URL);
	get.param("projectTicket", projectTicket);
	get.param("exportFormat", RestConstants.TBX);
	get.param("userId", userId);
	get.param("sourceLocale", "en-US");
	get.param("targetLocale", "de-DE");
	get.param("generateStatistics", "true");

	@SuppressWarnings("unchecked")
	List<TermEntry> termentries = getModelObject("termEntryList", List.class);
	Page<TermEntry> page = new Page<TermEntry>(1, 0, 1, termentries);

	when(getTermEntryService().searchTermEntries(any(TmgrSearchFilter.class))).thenReturn(page)
		.thenReturn(new Page<TermEntry>(0, 0, 0, null));

	ResultActions result = _mockMvc.perform(get);

	verifyMockings();
	verify(getTermEntryService(), times(2)).searchTermEntries(any(TmgrSearchFilter.class));

	MockHttpServletResponse response = result.andReturn().getResponse();

	Locale source = new Locale("en", "US");

	Assert.assertEquals(source, response.getLocale());
	Assert.assertEquals(MediaType.APPLICATION_XML_VALUE, response.getContentType());
	Assert.assertEquals("UTF-8", response.getCharacterEncoding());
	Assert.assertNotNull(response.getContentAsString());
	Assert.assertEquals(200, response.getStatus());
    }

    @Before
    public void setUp() throws Exception {
    }

    private void mockObjects() {
	TmProject tmProject = getModelObject("tmProject", TmProject.class);
	when(getProjectService().load(any(Long.class))).thenReturn(tmProject);

    }

    private void verifyMockings() {
	verify(getProjectService(), times(1)).load(any(Long.class));
    }

}
