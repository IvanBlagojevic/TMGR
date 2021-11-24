package org.gs4tr.termmanager.webmvc.rest;

import org.gs4tr.eventlogging.api.EventThreadContext;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.foundation.modules.webmvc.logging.util.builder.HttpRequestThreadContextBuilder;
import org.gs4tr.foundation.modules.webmvc.rest.AbstractRestController;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("wsv1LogoutController")
public class RestLogoutController extends AbstractRestController {

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, Authentication> _cacheGateway;

    @Autowired
    private SessionService _sessionService;

    @RequestMapping(value = "/rest/logout", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @LogEvent(action = TMGREventActionConstants.ACTION_LOGOUT, actionCategory = TMGREventActionConstants.ACTION_TYPE_REST)
    public String doGet(@RequestParam(value = "userId") String userId) {
	logHttpUsername();
	getSessionService().logout();
	removeAuthentication(userId);
	return userId;
    }

    private CacheGateway<String, Authentication> getCacheGateway() {
	return _cacheGateway;
    }

    private SessionService getSessionService() {
	return _sessionService;
    }

    private void logHttpUsername() {
	EventThreadContext.addProperty(HttpRequestThreadContextBuilder.HTTP_USER_KEY,
		SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private void removeAuthentication(final String userId) {
	getCacheGateway().remove(CacheName.WS_V1_USERS_SESSIONS, userId);
    }
}
