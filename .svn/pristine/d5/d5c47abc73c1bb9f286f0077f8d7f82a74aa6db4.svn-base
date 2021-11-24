package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static junit.framework.Assert.assertEquals;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createGetRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Mockito.when;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Test;

public class ProjectWriteConfigurationControllerTest extends AbstractMvcTest {

    private static final String URL = "projectWriteConfiguration.ter";

    @Test
    @TestUser(roleName = RoleNameEnum.SYSTEM_POWER_USER)
    public void projectWriteConfigurationTest() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	Request request = createGetRequest(URL, null, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));

	JSONValidator projectAddEditConfiguration = responseContent.getObject("projectAddEditConfiguration");
	projectAddEditConfiguration.assertProperty(IdEncrypter.encryptGenericId(1), String.valueOf(true));
    }
}
