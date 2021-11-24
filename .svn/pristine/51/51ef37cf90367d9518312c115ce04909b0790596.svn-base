package org.gs4tr.termmanager.webmvc.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.RepositoryTicket;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.foundation.modules.repository.RepositoryManager;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

public class ViewMultimediaControllerTest extends AbstractControllerTest {

    private static final String FILE_PATH = "src/test/resources/images/dog.jpg";
    private static final int IMAGE_SIZE = 11759;
    private static final String URL = "multimedia.ter";

    private InputStream _inputStream;

    private RepositoryItem _repositoryItem = new RepositoryItem();

    @Autowired
    private RepositoryManager _repositoryManager;

    public RepositoryManager getRepositoryManager() {
	return _repositoryManager;
    }

    @Before
    public void setUp() throws Exception {
	resetServices();
    }

    @After
    public void tearDown() throws Exception {
	if (getInputStream() != null) {
	    getInputStream().close();
	}
    }

    @Test
    public void viewMultimediaExceptionTest() throws Exception {
	resetServices();
	MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/" + URL);

	ResultActions resultActions = _mockMvc.perform(get);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();
	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertFalse(successNode.asBoolean());
    }

    @Test
    public void viewMultimediaTumbailFalseTest() throws Exception {
	resetServices();
	ResourceInfo resourceInfo = new ResourceInfo();
	resourceInfo.setMimeType("image");

	MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/" + URL);
	get.param("ticketId", "1");

	_inputStream = new FileInputStream(new File(FILE_PATH));
	getRepositoryItem().setResourceInfo(resourceInfo);
	getRepositoryItem().setInputStream(getInputStream());

	when(getRepositoryManager().read(any(RepositoryTicket.class))).thenReturn(getRepositoryItem());

	ResultActions resultActions = _mockMvc.perform(get);

	verify(getRepositoryManager(), times(1)).read(any(RepositoryTicket.class));

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();

	Assert.assertTrue(response.getContentLength() == IMAGE_SIZE);
    }

    private InputStream getInputStream() {
	return _inputStream;
    }

    private RepositoryItem getRepositoryItem() {
	return _repositoryItem;
    }

    private void resetServices() {
	reset(getRepositoryManager());
    }

}
