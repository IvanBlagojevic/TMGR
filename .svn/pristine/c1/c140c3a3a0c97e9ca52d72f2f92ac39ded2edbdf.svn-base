package org.gs4tr.termmanager.webmvc.rest;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.eventlogging.api.EventThreadContext;
import org.gs4tr.eventlogging.spring.aop.EventLogger;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.foundation.modules.usermanager.service.impl.AbstractUserProfileServiceImpl;
import org.gs4tr.foundation.modules.webmvc.logging.util.builder.HttpRequestThreadContextBuilder;
import org.gs4tr.foundation.modules.webmvc.rest.AbstractRestController;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/rest/login")
@RestController("wsv1LoginController")
public class RestLoginController extends AbstractRestController {

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, Authentication> _cacheGateway;

    @Autowired
    private CacheManager _cacheManager;

    @Autowired
    private SessionService _sessionService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @LogEvent(action = TMGREventActionConstants.ACTION_LOGIN, actionCategory = TMGREventActionConstants.ACTION_TYPE_REST)
    public String doGet(@RequestParam String username, @RequestParam String password) {

	EventThreadContext.addProperty(HttpRequestThreadContextBuilder.HTTP_USER_KEY, username);

	String sessionKey = doLogin(username, password);
	if (sessionKey == null) {
	    throw new RuntimeException(String.format(Messages.getString("RestLoginController.0"), username));
	}

	EventThreadContext.addProperty(HttpRequestThreadContextBuilder.HTTP_SESSION_ID_KEY, sessionKey);

	return sessionKey;
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @LogEvent(action = TMGREventActionConstants.ACTION_LOGIN, actionCategory = TMGREventActionConstants.ACTION_TYPE_REST)
    public String doPost(@RequestParam String username, @RequestParam String password) {

	EventLogger.addProperty(HttpRequestThreadContextBuilder.HTTP_USER_KEY, username);

	String sessionKey = doLogin(username, password);
	if (sessionKey == null) {
	    throw new RuntimeException(String.format(Messages.getString("RestLoginController.0"), username));
	}

	EventLogger.addProperty(HttpRequestThreadContextBuilder.HTTP_SESSION_ID_KEY, sessionKey);

	return sessionKey;
    }

    private String doLogin(String username, String password) {
	if (paramsNotBlank(username, password)) {
	    /* Issue TERII-5912 */
	    getCacheManager().getCache(AbstractUserProfileServiceImpl.USER_PROFILE_CACHE).evict(username);

	    getSessionService().login(username, password);
	    String sessionKey = UUID.randomUUID().toString();
	    Authentication authentication = getCurrentAuthentication();
	    getHzCacheGateway().put(CacheName.WS_V1_USERS_SESSIONS, sessionKey, authentication);
	    return sessionKey;
	}
	return null;
    }

    private CacheManager getCacheManager() {
	return _cacheManager;
    }

    private Authentication getCurrentAuthentication() {
	return SecurityContextHolder.getContext().getAuthentication();
    }

    private CacheGateway<String, Authentication> getHzCacheGateway() {
	return _cacheGateway;
    }

    private SessionService getSessionService() {
	return _sessionService;
    }

    private boolean paramsNotBlank(String username, String password) {
	return StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password);
    }
}