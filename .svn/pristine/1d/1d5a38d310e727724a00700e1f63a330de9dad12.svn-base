package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation.modules.webmvc.test.annotations.ClientBean;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UserCustomSearch;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

@TestSuite("saveCustomFilterController")
public class SaveCustomFilterControllerTest extends AbstractMvcTest {

    private static final String JSON_SEARCH_KEY = "jsonSearch";

    private static final String URL = "saveCustomFilter.ter";

    @Captor
    private ArgumentCaptor<String> _captor;

    @ClientBean
    private UserProfileService _userProfileService;

    @Test
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void saveCustomFilterCompletedTest() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	String originalFolder = ItemFolderEnum.SUBMISSIONDETAILS.name();

	UserProfileService userProfileService = getUserProfileService();
	when(userProfileService.addOrUpdateCustomSearchFolder(any(TmUserProfile.class), anyString(), anyString(),
		anyString(), anyString(), anyBoolean())).thenReturn(Long.valueOf(1));

	Map<String, String> parameters = new HashMap<String, String>();
	parameters.put(JSON_DATA_KEY, getJsonData("saveCustomFilterCompletedJsonData.json"));
	parameters.put(JSON_SEARCH_KEY, getJsonData("saveCustomFilterCompletedJsonSearch.json"));

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(userProfileService).getCustomSearchFolder(eq(getCurrentUserProfile()), _captor.capture());
	verify(getUserProfileService()).addOrUpdateCustomSearchFolder(eq(getCurrentUserProfile()), _captor.capture(),
		_captor.capture(), _captor.capture(), _captor.capture(), eq(Boolean.FALSE));

	List<String> params = _captor.getAllValues();
	assertTrue(params.contains("Workflow_Filter"));
	assertTrue(params.contains(originalFolder));
	assertTrue(params.contains("submissionDetailView.ter"));

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));

	JSONValidator menuConfig = responseContent.getObject("menuConfig");

	menuConfig.assertProperty("url", "submissionDetailView.ter").assertProperty("id", "Workflow_Filter")
		.assertProperty("parent", originalFolder).assertProperty("systemHidden", String.valueOf(false));

	JSONValidator detailsUrl = menuConfig.getObject("detailsUrl");
	JSONValidator detailUrl = detailsUrl.getObjectFromArray("url", "submissionLanguageDetailView.ter");

	detailUrl.assertProperty("name", "SUBMISSIONLANGUAGEDETAILS");

	assertNotNull(menuConfig.getObject("jsonSearch"));

	String searchCriterias = menuConfig.getObject("searchCriterias").toString();
	assertTrue(searchCriterias.contains("SUBMISSION_NAME"));
	assertTrue(searchCriterias.contains("SUBMISSION_PROJECT_LIST"));
	assertTrue(searchCriterias.contains("LANGUAGE_DIRECTION_SUBMISSION"));
	assertTrue(searchCriterias.contains("SUBMISSION_STATUSES"));
	assertTrue(searchCriterias.contains("DATE_CREATED_RANGE"));
	assertTrue(searchCriterias.contains("DATE_MODIFIED_RANGE"));
	assertTrue(searchCriterias.contains("DATE_COMPLETED_RANGE"));
    }

    @Test
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void saveCustomFilterFailedTest() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	Map<String, String> parameters = new HashMap<>();
	parameters.put("folder", ItemFolderEnum.PROJECTDETAILS.name());
	parameters.put("customFolder", ItemFolderEnum.TERM_LIST.name());
	parameters.put("index", String.valueOf(0));
	parameters.put("ascending", String.valueOf(true));
	parameters.put("index", String.valueOf(50));

	parameters.put(JSON_DATA_KEY, getJsonData("saveCustomFilterFailed.json"));

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(getUserProfileService(), never()).addOrUpdateCustomSearchFolder(any(TmUserProfile.class), anyString(),
		anyString(), anyString(), anyString(), anyBoolean());

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(false));

	responseContent.assertProperty("reasons", MessageResolver.getMessage("SaveCustomFilterController.0"));
    }

    @Test
    @TestUser(roleName = RoleNameEnum.SYSTEM_POWER_USER)
    public void saveCustomFilterFailedTest_1() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	UserProfileService userProfileService = getUserProfileService();
	when(userProfileService.getCustomSearchFolder(any(TmUserProfile.class), any(String.class)))
		.thenReturn(new UserCustomSearch());

	Map<String, String> parameters = new HashMap<String, String>();
	parameters.put(JSON_DATA_KEY, getJsonData("saveCustomFilterFailedJsonData.json"));
	parameters.put(JSON_SEARCH_KEY, getJsonData("saveCustomFilterFailedJsonSearch.json"));

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(userProfileService).getCustomSearchFolder(eq(getCurrentUserProfile()), _captor.capture());

	assertEquals("term_list_filter", _captor.getValue());

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(false));

	responseContent.assertProperty("reasons", MessageResolver.getMessage("SaveCustomFilterController.0"));
    }

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);
    }

    UserProfileService getUserProfileService() {
	return _userProfileService;
    }
}
