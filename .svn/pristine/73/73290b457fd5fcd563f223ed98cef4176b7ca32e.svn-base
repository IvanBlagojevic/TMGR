package org.gs4tr.termmanager.tests.controllers.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.File;

import org.apache.commons.io.IOUtils;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class ImportTbxDocumentControllerTest extends BaseRestControllerTest {

    private static final String FILE_LOCATION = "src/test/resources/testfiles/en_fr_FR.tbx";

    private static final String URL = "/rest/import";

    @Test
    public void importTbxDocumentTest() throws Exception {
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

	MockHttpServletResponse response = _mockMvc.perform(post).andReturn().getResponse();
	Assert.assertEquals("application/xml;charset=UTF-8", response.getContentType());

	String responseString = response.getContentAsString();
	Assert.assertNotNull(responseString);
    }
}