package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.webmvc.controllers.DownloadUtils;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@TestSuite("downloadResourceController")
public class DownloadResourceControllerTest extends AbstractMvcTest {

    public static final String DEFAULT_MIME_TYPE = "text/html;charset=utf-8";

    private static final String DOWNLOAD_TICKET = "downloadTicket";

    private static final String SEND_WITHOUT_DISPOSITION_HEADER = "sendWithoutDispositionHeader";

    private static final String TEMP_FOLDER_PATH = System.getProperty("java.io.tmpdir")
	    .concat(System.getProperty("file.separator"));

    private File _tempFile;

    @After
    public void cleanUp() throws IOException {
	// Delete all test files
	if (_tempFile.exists()) {
	    FileUtils.forceDelete(_tempFile);
	}
    }

    @Test
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void deleteTempTest() throws IOException {

	Map<String, String> parameters = new HashMap<String, String>();

	parameters.put(DOWNLOAD_TICKET, "tempFile");

	parameters.put(SEND_WITHOUT_DISPOSITION_HEADER, String.valueOf(false));

	Request request = createPostRequest("deleteTemp.ter", parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));

	assertFalse(_tempFile.exists());
    }

    @Test
    @TestCase("downloadResource")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void downloadResourceTest() throws IOException {

	RepositoryItem repositoryItem = getModelObject("repositoryItem", RepositoryItem.class);

	repositoryItem.setInputStream(new FileInputStream(_tempFile));

	when(getTermEntryService().downloadResource(anyLong())).thenReturn(repositoryItem);

	String encryptGenericId = IdEncrypter.encryptGenericId(1);

	Map<String, String> parameters = new HashMap<String, String>();

	parameters.put(DOWNLOAD_TICKET, encryptGenericId);

	parameters.put(SEND_WITHOUT_DISPOSITION_HEADER, String.valueOf(false));

	Request request = createPostRequest("download.ter", parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	verify(getTermEntryService()).downloadResource(eq(1L));

	assertEquals(200, response.getStatus());

	assertEquals("file content", response.getContent());

	assertEquals(DownloadUtils.DOWNLOAD_MIME_TYPE, response.get(CONTENT_TYPE_KEY));

	assertEquals(DownloadUtils.CACHE_CONTROL_VALUE, response.get(DownloadUtils.CACHE_CONTROL));

	String fileName = response.get(DownloadUtils.FILENAME_HEADER_NAME);

	assertTrue(StringUtils.equalsIgnoreCase("tempFile", fileName.split("\"")[1]));

	assertEquals(response.get(DownloadUtils.CONTENT_ENCODING_TRANSFER_HEADER_NAME), "binary");
    }

    @Test
    @TestCase("downloadTempFile")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void downloadTempFileTest() throws IOException {

	Map<String, String> parameters = new HashMap<String, String>();

	parameters.put(DOWNLOAD_TICKET, "tempFile");

	parameters.put(SEND_WITHOUT_DISPOSITION_HEADER, String.valueOf(true));

	Request request = createPostRequest("downloadTemp.ter", parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	assertEquals("file content", response.getContent());

	assertTrue(StringUtils.equalsIgnoreCase(DownloadUtils.DEFAULT_MIME_TYPE, response.get(CONTENT_TYPE_KEY)));

	assertEquals(DownloadUtils.CACHE_CONTROL_VALUE, response.get(DownloadUtils.CACHE_CONTROL));

	String fileName = response.get(DownloadUtils.FILENAME_HEADER_NAME);

	assertTrue(StringUtils.equalsIgnoreCase("tempFile", fileName.split("\"")[1]));

	assertEquals("binary", response.get(DownloadUtils.CONTENT_ENCODING_TRANSFER_HEADER_NAME));
    }

    @Before
    public void setUp() throws IOException {
	_tempFile = new File(TEMP_FOLDER_PATH + "tempFile");
	FileUtils.writeStringToFile(_tempFile, "file content");
    }
}
