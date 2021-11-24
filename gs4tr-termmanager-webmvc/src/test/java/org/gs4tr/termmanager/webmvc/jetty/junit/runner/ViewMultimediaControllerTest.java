package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.RepositoryTicket;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.repository.RepositoryManager;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation.modules.webmvc.test.annotations.ClientBean;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.webmvc.controllers.DownloadUtils;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

@TestSuite("viewMultimediaController")
public class ViewMultimediaControllerTest extends AbstractMvcTest {

    private static final String IMAGE_FILE_PATH = "src/test/resources/images/15-beach-sea-photography.jpg";

    private static final String URL = "multimedia.ter";

    @Captor
    private ArgumentCaptor<RepositoryTicket> _captor;

    @ClientBean
    private RepositoryManager _repositoryManager;

    private InputStream _stream;

    public RepositoryManager getRepositoryManager() {
	return _repositoryManager;
    }

    @Before
    public void setUp() {
	reset(getRepositoryManager());
	MockitoAnnotations.initMocks(this);
    }

    public void tearDown() throws IOException {
	_stream.close();
    }

    @Test
    @TestCase("viewMultimedia")
    @TestUser(roleName = RoleNameEnum.SYSTEM_POWER_USER)
    public void viewMultimediaCompletedTest() throws Exception {

	RepositoryItem repositoryItem = getModelObject("repositoryItem", RepositoryItem.class);

	_stream = new FileInputStream(new File(IMAGE_FILE_PATH));
	repositoryItem.setInputStream(_stream);

	when(getRepositoryManager().read(any(RepositoryTicket.class))).thenReturn(repositoryItem);

	Map<String, String> parameters = new HashMap<String, String>();
	parameters.put("download", String.valueOf(false));
	String encryptGenericId = IdEncrypter.encryptGenericId(1);
	parameters.put("ticketId", encryptGenericId);

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(getRepositoryManager()).read(_captor.capture());
	RepositoryTicket repositoryTicket = _captor.getValue();
	assertEquals(encryptGenericId, repositoryTicket.getTicket());

	assertEquals(200, response.getStatus());

	assertEquals("image/jpeg", response.get(CONTENT_TYPE_KEY));

	assertEquals(DownloadUtils.CACHE_CONTROL_VALUE, response.get(DownloadUtils.CACHE_CONTROL));

	String fileName = response.get(DownloadUtils.FILENAME_HEADER_NAME);

	assertTrue(StringUtils.equalsIgnoreCase("15-beach-sea-photography.jpg", fileName.split("\"")[1]));
	assertEquals(response.get(DownloadUtils.CONTENT_ENCODING_TRANSFER_HEADER_NAME), "binary");

	tearDown();
    }

    @Test
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void viewMultimediaFailedTest() throws Exception {

	Map<String, String> parameters = new HashMap<String, String>();
	parameters.put("download", String.valueOf(true));

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(getRepositoryManager(), never()).read(any(RepositoryTicket.class));

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(false));

	responseContent.assertProperty("reasons", MessageResolver.getMessage("ViewMultimediaController.0"));
	responseContent.assertProperty("desciption", MessageResolver.getMessage("DownloadResourceController.0"));
    }
}
