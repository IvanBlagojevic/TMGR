package org.gs4tr.termmanager.webmvc.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.foundation.modules.usermanager.service.impl.SessionServiceException;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.webmvc.controllers.AbstractControllerTest;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class RestLoginControllerTest extends AbstractControllerTest {

    private static final String DEFAULT_PASSWORD = "password";

    private static final String DEFAULT_USERNAME = "sdulin";

    private static final String LOGIN_URL = "/rest/login";

    private static final String PASSWORD_KEY = "password";

    private static final String USERNAME_KEY = "username";

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, Authentication> _cacheGateway;

    @Autowired
    private SessionService _sessionService;

    @SuppressWarnings("unchecked")
    @Before
    public void cleanUp() {
	reset(getCacheGateway());
	reset(getSessionService());
    }

    @Test
    @TestCase("restLoginController")
    public void loginGetNegativeTest() throws Exception {
	MockHttpServletRequestBuilder request = get(LOGIN_URL);

	ResultActions resultActions = _mockMvc.perform(request);
	MvcResult mvcResult = resultActions.andReturn();
	MockHttpServletResponse response = mvcResult.getResponse();

	assertEquals(500, response.getStatus());
    }

    @Test
    @TestCase("restLoginController")
    public void loginGetTest() throws Exception {
	MockHttpServletRequestBuilder request = get(LOGIN_URL);

	addRequestParam(request);

	ResultActions resultActions = _mockMvc.perform(request);
	MvcResult mvcResult = resultActions.andReturn();
	MockHttpServletResponse response = mvcResult.getResponse();

	assertEquals(200, response.getStatus());

	verify(getSessionService()).login(DEFAULT_USERNAME, DEFAULT_PASSWORD);
	verify(getCacheGateway()).put(any(CacheName.class), anyString(), any(Authentication.class));

	String responseContent = response.getContentAsString();

	assertTrue(StringUtils.isNotBlank(responseContent));
	assertEquals(MediaType.APPLICATION_JSON_UTF8_VALUE, response.getContentType());
    }

    /*
     * TERII-4152 : PD - Connection with TMGR | Wrong message after connection with
     * invalid user or password.
     */
    @Test
    public void loginGetWhenUsernameAndPasswordIsInvalid() throws Exception {
	String invalidUsername = "random";
	String invalidPassword = "value";

	String message = "Login failed";

	when(getSessionService().login(invalidUsername, invalidPassword))
		.thenThrow(new SessionServiceException(new Throwable(message)));

	MockHttpServletRequestBuilder request = get(LOGIN_URL);
	request.param(USERNAME_KEY, invalidUsername);
	request.param(PASSWORD_KEY, invalidPassword);

	ResultActions resultActions = _mockMvc.perform(request);

	MvcResult mvcResult = resultActions.andReturn();
	MockHttpServletResponse response = mvcResult.getResponse();

	assertEquals(500, response.getStatus());
	assertEquals(MediaType.TEXT_PLAIN.toString(), response.getContentType());

	String responseContent = response.getContentAsString();

	assertTrue(StringUtils.isNotBlank(responseContent));
	assertEquals(message.toLowerCase(), responseContent.toLowerCase());
    }

    @Test
    @TestCase("restLoginController")
    public void loginPostNegativeTest() throws Exception {
	MockHttpServletRequestBuilder request = post(LOGIN_URL);

	ResultActions resultActions = _mockMvc.perform(request);

	MvcResult mvcResult = resultActions.andReturn();
	MockHttpServletResponse response = mvcResult.getResponse();

	assertEquals(500, response.getStatus());
    }

    @Test
    @TestCase("restLoginController")
    public void loginPostTest() throws Exception {
	MockHttpServletRequestBuilder request = post(LOGIN_URL);

	addRequestParam(request);

	ResultActions resultActions = _mockMvc.perform(request);

	MvcResult mvcResult = resultActions.andReturn();
	MockHttpServletResponse response = mvcResult.getResponse();

	assertEquals(200, response.getStatus());

	verify(getSessionService()).login(DEFAULT_USERNAME, DEFAULT_PASSWORD);
	verify(getCacheGateway()).put(any(CacheName.class), anyString(), any(Authentication.class));

	String responseContent = response.getContentAsString();

	assertTrue(StringUtils.isNotBlank(responseContent));
	assertEquals(MediaType.APPLICATION_JSON_UTF8_VALUE, response.getContentType());
    }

    private void addRequestParam(MockHttpServletRequestBuilder request) {
	request.param(USERNAME_KEY, DEFAULT_USERNAME);
	request.param(PASSWORD_KEY, DEFAULT_PASSWORD);
    }

    private CacheGateway<String, Authentication> getCacheGateway() {
	return _cacheGateway;
    }

    private SessionService getSessionService() {
	return _sessionService;
    }
}
