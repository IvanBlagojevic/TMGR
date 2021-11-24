package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Before;
import org.junit.Test;

@TestSuite("folderViewUpdateController")
public class FolderViewUpdateControllerTest extends AbstractMvcTest {

    @Test
    @TestCase("folderViewUpdate")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void projectDetailFolderViewUpdateTest() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	String inputJson = getJsonData("termListMetadata.json");

	Map<String, String> parameters = new HashMap<String, String>();
	parameters.put("folder", ItemFolderEnum.PROJECTDETAILS.name());
	parameters.put("metadata", inputJson);

	Request request = createPostRequest("folderViewUpdate.ter", parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));

	verify(getUserProfileService()).addOrUpdateMetadata(ItemFolderEnum.PROJECTDETAILS.name().toLowerCase(),
		inputJson);
    }

    @Test
    @TestCase("folderViewUpdate")
    @TestUser(roleName = RoleNameEnum.ADMIN)
    public void projectFolderViewUpdateTest() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	String inputJson = getJsonData("projectFolderMetadata.json");

	Map<String, String> parameters = new HashMap<String, String>();
	parameters.put("folder", ItemFolderEnum.PROJECTS.name());
	parameters.put("metadata", inputJson);

	Request request = createPostRequest("folderViewUpdate.ter", parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));

	verify(getUserProfileService()).addOrUpdateMetadata(ItemFolderEnum.PROJECTS.name().toLowerCase(), inputJson);
    }

    @Before
    public void setUp() {
	reset(getUserProfileService());
    }

    @Test
    @TestCase("folderViewUpdate")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void termListFolderViewUpdateTest() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	String inputJson = getJsonData("termListMetadata.json");

	Map<String, String> parameters = new HashMap<String, String>();
	parameters.put("folder", ItemFolderEnum.TERM_LIST.name());
	parameters.put("metadata", inputJson);

	Request request = createPostRequest("folderViewUpdate.ter", parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));

	verify(getUserProfileService()).addOrUpdateMetadata(ItemFolderEnum.TERM_LIST.name().toLowerCase(), inputJson);
    }
}