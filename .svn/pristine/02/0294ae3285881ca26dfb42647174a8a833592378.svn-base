package org.gs4tr.termmanager.webservice.controllers;

import static org.gs4tr.termmanager.cache.model.CacheName.WS_V2_USERS_SESSIONS;
import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.OK;

import java.util.Objects;
import java.util.UUID;

import org.gs4tr.eventlogging.api.EventThreadContext;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.foundation.modules.spring.utils.SpringProfileUtils;
import org.gs4tr.foundation.modules.usermanager.oauth.TptOAuthUserManagerClient;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.foundation.modules.usermanager.service.impl.AbstractUserProfileServiceImpl;
import org.gs4tr.foundation.modules.usermanager.service.impl.SessionServiceException;
import org.gs4tr.foundation.modules.webmvc.logging.util.builder.HttpRequestThreadContextBuilder;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.model.Version;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.termmanager.webservice.model.request.LoginCommand;
import org.gs4tr.termmanager.webservice.model.response.BaseResponse;
import org.gs4tr.termmanager.webservice.model.response.ErrorResponse;
import org.gs4tr.termmanager.webservice.model.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This class provide post login method to login onto Term Manager before
 * invoking other actions in order to provide credentials and have them checked
 * by server.
 *
 * @author TMGR_Backend
 */
@RequestMapping("/rest/v2/login")
@RestController("wsLoginController")
@Api(value = "Login")
public class LoginController {

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, Authentication> _cacheGateway;

    @Autowired
    private CacheManager _cacheManager;

    @Autowired(required = false)

    private TptOAuthUserManagerClient _oAuthClient;

    @Autowired
    private SessionService _sessionService;

    @Autowired
    private UserProfileService _userProfileService;

    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful operation.", response = LoginResponse.class),
	    @ApiResponse(code = 400, message = "Missing required parameter/Invalid parameter.", response = ErrorResponse.class),
	    @ApiResponse(code = 500, message = "Internal server error.") })
    @ApiOperation(value = "Use this method to login to Term Manager before invoking other actions.", httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @LogEvent(action = TMGREventActionConstants.ACTION_LOGIN, actionCategory = TMGREventActionConstants.ACTION_TYPE_REST_V2)
    public BaseResponse loginPost(@RequestBody LoginCommand loginCommand) {

	String username = loginCommand.getUsername();

	EventThreadContext.addProperty(HttpRequestThreadContextBuilder.HTTP_USER_KEY, username);

	String password = loginCommand.getPassword();

	/* Issue TERII-5912 */
	getCacheManager().getCache(AbstractUserProfileServiceImpl.USER_PROFILE_CACHE).evict(username);

	login(username, password);

	return new LoginResponse(OK, true, storeAuthentication(), Version.getVersion());
    }

    private CacheGateway<String, Authentication> getCacheGateway() {
	return _cacheGateway;
    }

    private CacheManager getCacheManager() {
	return _cacheManager;
    }

    private Authentication getCurrentAuthentication() {
	return SecurityContextHolder.getContext().getAuthentication();
    }

    private SessionService getSessionService() {
	return _sessionService;
    }

    private UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    private TptOAuthUserManagerClient getoAuthClient() {
	return _oAuthClient;
    }

    private boolean isSSO(String username, String password) {

	if (Objects.isNull(getoAuthClient())) {
	    return false;
	}

	return SpringProfileUtils.checkIfSpringProfileIsActive(SpringProfileUtils.OAUTH_AUTHENTICATION_PROFILE)
		&& getoAuthClient().authenticate(username, password);
    }

    private void login(String username, String password) throws SessionServiceException {
	if (isSSO(username, password)) {
	    UserDetails userDetails = null;
	    try {
		userDetails = getUserProfileService().loadUserByEmail(username);
	    } catch (Exception e) {
		new SessionServiceException(new RuntimeException(Messages.getString("LoginFailed")));
	    }
	    getSessionService().registerAuthentication(
		    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
	} else {
	    getSessionService().login(username, password);
	}
    }

    private String storeAuthentication() {
	String securityTicket = UUID.randomUUID().toString();
	Authentication auth = getCurrentAuthentication();
	getCacheGateway().put(WS_V2_USERS_SESSIONS, securityTicket, auth);
	return securityTicket;
    }
}