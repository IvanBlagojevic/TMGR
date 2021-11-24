package org.gs4tr.termmanager.webmvc.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.webmvc.controllers.AbstractControllerTest;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class RestLogoutControllerTest extends AbstractControllerTest {

    public static final String LOGOUT_URL = "/rest/logout";

    private static final String SESSION_KEY = "password";

    private static final String USER_ID = "userId";

    @Autowired
    private CacheGateway<String, Authentication> _cacheGateway;

    @Autowired
    private SessionService _sessionService;

    public CacheGateway<String, Authentication> getCacheGateway() {
	return _cacheGateway;
    }

    public SessionService getSessionService() {
	return _sessionService;
    }

    @Test
    @TestCase("restLogoutController")
    public void logoutTest() throws Exception {
	MockHttpServletRequestBuilder request = get(LOGOUT_URL);

	request.param(USER_ID, SESSION_KEY);

	ResultActions resultActions = _mockMvc.perform(request);

	MockHttpServletResponse response = getResponse(resultActions);

	assertEquals(200, response.getStatus());

	verify(getSessionService()).logout();
	verify(getCacheGateway()).remove(CacheName.WS_V1_USERS_SESSIONS, SESSION_KEY);

	String responseContent = response.getContentAsString();

	assertTrue(StringUtils.isNotBlank(responseContent));
    }

    private MockHttpServletResponse getResponse(ResultActions resultActions) {
	MvcResult mvcResult = resultActions.andReturn();
	return mvcResult.getResponse();
    }
}
