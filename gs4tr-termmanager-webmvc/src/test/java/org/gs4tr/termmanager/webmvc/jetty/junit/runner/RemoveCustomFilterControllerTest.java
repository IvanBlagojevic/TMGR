package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static junit.framework.Assert.assertEquals;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

@TestSuite("removeCustomFilterController")
public class RemoveCustomFilterControllerTest extends AbstractMvcTest {

    private static final String URL = "removeCustomFilter.ter";

    @Captor
    private ArgumentCaptor<String> _captor;

    @Test
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void removeCustomFilterCompletedTest() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	when(getUserProfileService().removeCustomSearchFolder(any(), any())).thenReturn(Boolean.TRUE);

	String inputJson = getJsonData("removeCustomFilterCompleted.json");

	Map<String, String> parameters = new HashMap<String, String>();
	parameters.put("customFolder", "customFilter");
	parameters.put(JSON_DATA_KEY, OBJECT_MAPPER.writeValueAsString(inputJson));

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(getUserProfileService()).removeCustomSearchFolder(eq(getCurrentUserProfile()), _captor.capture());

	assertEquals("customFilter", _captor.getValue());

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));
    }

    @Test
    @TestUser(roleName = RoleNameEnum.SYSTEM_POWER_USER)
    public void removeCustomFilterFailedTest() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	String inputJson = getJsonData("removeCustomFilterFailed.json");

	Map<String, String> parameters = new HashMap<String, String>();
	parameters.put("customFolder", ItemFolderEnum.SECURITY.name());
	parameters.put(JSON_DATA_KEY, OBJECT_MAPPER.writeValueAsString(inputJson));

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(getUserProfileService(), never()).removeCustomSearchFolder(any(TmUserProfile.class), any(String.class));

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(false));

	responseContent.assertProperty("reasons", MessageResolver.getMessage("RemoveCustomFilterController.0"));
    }

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);
    }
}
