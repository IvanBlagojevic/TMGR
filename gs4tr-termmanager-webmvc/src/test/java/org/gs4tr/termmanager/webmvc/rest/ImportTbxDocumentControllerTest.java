package org.gs4tr.termmanager.webmvc.rest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.ImportTypeEnum;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.ImportTermService;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;
import org.gs4tr.termmanager.webmvc.controllers.AbstractControllerTest;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@TestSuite("rest")
public class ImportTbxDocumentControllerTest extends AbstractControllerTest {

    private static final String FILE_LOCATION = "src/test/resources/testfiles/en_fr_FR.tbx"; //$NON-NLS-1$

    private static final String URL = "/rest/import"; //$NON-NLS-1$

    @Autowired
    private ImportTermService _importTermService;

    @Test
    @TestCase("importTbx")
    public void importTbxTest() throws Exception {
	String projectTicket = TicketConverter.fromInternalToDto(1L);
	String userId = loginPost("pm", "test");

	File file = new File(FILE_LOCATION);

	FileSystemResource fileResource = new FileSystemResource(file);

	MockHttpServletRequestBuilder post = post(URL);
	post.contentType(MediaType.APPLICATION_OCTET_STREAM);
	post.content(IOUtils.toByteArray(fileResource.getInputStream()));
	post.param("projectTicket", projectTicket);
	post.param("syncLang", "fr-FR");
	post.param("userId", userId);

	ResultActions resultActions = _mockMvc.perform(post);

	ArgumentCaptor<ImportOptionsModel> argument = ArgumentCaptor.forClass(ImportOptionsModel.class);

	verify(getProjectService(), times(2)).load(any(Long.class));
	verify(getImportTermService(), times(1)).importDocumentWS(argument.capture(), any(InputStream.class),
		any(Long.class), any(ImportTypeEnum.class), any(SyncOption.class));

	assertEquals(10, argument.getValue().getSynonymNumber());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();

	Assert.assertEquals("application/xml;charset=UTF-8", response.getContentType());

	String responseString = response.getContentAsString();
	Assert.assertTrue(StringUtils.isNotBlank(responseString));

	SAXBuilder saxBuilder = new SAXBuilder();
	try {
	    org.jdom.Document doc = saxBuilder.build(new StringReader(responseString));
	    Element rootElement = doc.getRootElement();
	    Assert.assertNotNull(rootElement);

	    validateRootElement(rootElement);

	    // error needs to be null if everything is OK
	    Element errorMessage = rootElement.getChild("errorMessages");
	    Assert.assertNull(errorMessage);

	} catch (JDOMException e) {
	    Assert.fail();
	} catch (IOException e) {
	    Assert.fail();
	}
    }

    @Test
    @TestCase("importTbx")
    public void importTbxWithoutPolicyTest() throws Exception {
	UserProfileContext.clearContext();
	TmUserProfile userProfile = mock(TmUserProfile.class);
	UserProfileContext.setCurrentUserProfile(userProfile);

	when(userProfile.containsContextPolicies(any(Long.class), any(String[].class))).thenReturn(false);
	when(userProfile.getUserInfo()).thenReturn(new UserInfo());

	String projectTicket = TicketConverter.fromInternalToDto(1L);
	String userId = loginPost("pm", "test");

	File file = new File(FILE_LOCATION);

	FileSystemResource fileResource = new FileSystemResource(file);

	MockHttpServletRequestBuilder post = post(URL);
	post.contentType(MediaType.APPLICATION_OCTET_STREAM);
	post.content(IOUtils.toByteArray(fileResource.getInputStream()));
	post.param("projectTicket", projectTicket);
	post.param("syncLang", "fr-FR");
	post.param("userId", userId);

	ResultActions resultActions = _mockMvc.perform(post);

	verify(getProjectService(), times(1)).load(any(Long.class));
	verify(userProfile, times(1)).containsContextPolicies(any(Long.class), any(String[].class));

	MockHttpServletResponse response = resultActions.andReturn().getResponse();

	Assert.assertEquals("application/xml;charset=UTF-8", response.getContentType());

	String responseString = response.getContentAsString();
	Assert.assertTrue(StringUtils.isNotBlank(responseString));

	SAXBuilder saxBuilder = new SAXBuilder();
	try {
	    org.jdom.Document doc = saxBuilder.build(new StringReader(responseString));
	    Element rootElement = doc.getRootElement();
	    Assert.assertNotNull(rootElement);

	    validateRootElement(rootElement);

	    Element errorMessage = rootElement.getChild("errorMessages");
	    Assert.assertNotNull(errorMessage);
	    Assert.assertTrue(StringUtils.isNotBlank(errorMessage.getValue()));

	} catch (JDOMException e) {
	    Assert.fail();
	} catch (IOException e) {
	    Assert.fail();
	}
    }

    @Before
    public void setUp() throws Exception {
	MockitoAnnotations.initMocks(this);
	mockObjects();
    }

    private ImportTermService getImportTermService() {
	return _importTermService;
    }

    private void mockObjects() throws Exception {
	TmProject project = getModelObject("tmProject", TmProject.class);

	when(getProjectService().load(any(Long.class))).thenReturn(project);
	when(getImportTermService().importDocumentWS(any(ImportOptionsModel.class), any(InputStream.class),
		any(Long.class), any(ImportTypeEnum.class), any(SyncOption.class))).thenReturn(new ImportSummary());
    }

    private void validateRootElement(Element rootElement) {
	Assert.assertNotNull(rootElement.getChild("totalTermsDuplicated"));
	Assert.assertNotNull(rootElement.getChild("totalImported"));
	Assert.assertNotNull(rootElement.getChild("totalSkipped"));
	Assert.assertNotNull(rootElement.getChild("totalTermsAttributesImported"));
	Assert.assertNotNull(rootElement.getChild("totalTermEntryAttributesImported"));
	Assert.assertNotNull(rootElement.getChild("totalTermEntryErrors"));
	Assert.assertNotNull(rootElement.getChild("totalTermsImported"));
    }
}
