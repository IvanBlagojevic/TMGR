package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static junit.framework.Assert.assertEquals;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;

import org.eclipse.jetty.http.HttpTester;
import org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Test;

public class HealthCheckControllerTest extends AbstractMvcTest {

    private static final String URL = "health.ter";

    @Test
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void testCheckHealth() {
	HttpTester.Request request = RequestHelper.createGetRequest(URL, null, getSessionParameters());

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());
        assertEquals("OK", response.getContent());
    }
}
