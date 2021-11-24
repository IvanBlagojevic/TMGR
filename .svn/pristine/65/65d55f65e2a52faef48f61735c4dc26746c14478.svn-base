package org.gs4tr.termmanager.webservice.interceptors;

import static org.gs4tr.termmanager.cache.model.CacheName.WS_V2_USERS_SESSIONS;
import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.UNAUTHORIZED_ACCESS;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.gs4tr.eventlogging.api.EventThreadContext;
import org.gs4tr.foundation.modules.spring.utils.SpringProfileUtils;
import org.gs4tr.foundation.modules.usermanager.oauth.TptOAuthUserManagerClient;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.foundation.modules.webmvc.logging.util.builder.HttpRequestThreadContextBuilder;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.webservice.model.request.BaseCommand;
import org.gs4tr.termmanager.webservice.model.response.BaseResponse;
import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class WsAuthenticationInterceptor {

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, Authentication> _cacheGateway;

    @Autowired(required = false)
    private TptOAuthUserManagerClient _oAuthClient;

    @Autowired
    private SessionService _sessionService;

    @Autowired
    private UserProfileService _userProfileService;

    @Around("org.gs4tr.termmanager.webservice.interceptors.WsSharedPointcuts.wsMethodsWithoutLoginController()")
    public Object authenticate(ProceedingJoinPoint pjp) throws Throwable {

	String securityTicket = extractSecurityToken(pjp);
	EventThreadContext.addProperty(HttpRequestThreadContextBuilder.HTTP_SESSION_ID_KEY, securityTicket);

	boolean returnResponseCode = resolveReturnResponseCode(pjp);
	if (StringUtils.isEmpty(securityTicket)) {
	    return signalError(returnResponseCode);
	}

	Authentication authentication = getAuthentication(securityTicket, pjp);
	if (authentication == null) {
	    EventThreadContext.addProperty(HttpRequestThreadContextBuilder.HTTP_USER_KEY, null);
	    return signalError(returnResponseCode);
	}

	getSessionService().registerAuthentication(authentication);
	EventThreadContext.addProperty(HttpRequestThreadContextBuilder.HTTP_USER_KEY, authentication.getName());

	return pjp.proceed();
    }

    private String[] extractCredentials(ProceedingJoinPoint pjp) {
	String[] ssoArgs = new String[2];
	for (Object argument : pjp.getArgs()) {
	    if (BaseCommand.class.isAssignableFrom(argument.getClass())) {
		BaseCommand command = (BaseCommand) argument;
		String username = command.getSsoUsername();
		String password = command.getSsoPassword();

		ssoArgs[0] = username;
		ssoArgs[1] = password;
	    }
	}

	return ssoArgs;
    }

    private String extractSecurityToken(ProceedingJoinPoint pjp) {
	for (Object argument : pjp.getArgs()) {
	    if (BaseCommand.class.isAssignableFrom(argument.getClass())) {
		BaseCommand command = (BaseCommand) argument;
		/*
		 * If it's OAUTH profile enabled, we will use ssoUsername as security ticket.
		 * (TERII-5554)
		 */
		String securityTicket = StringUtils.isNotEmpty(command.getSecurityTicket())
			? command.getSecurityTicket()
			: command.getSsoUsername();
		return securityTicket;
	    }
	}
	return null;
    }

    private Authentication getAuthentication(String securityTicket, ProceedingJoinPoint pjp) {
	Authentication authentication = getCacheGateway().get(WS_V2_USERS_SESSIONS, securityTicket);

	if (Objects.isNull(authentication)
		&& SpringProfileUtils.checkIfSpringProfileIsActive(SpringProfileUtils.OAUTH_AUTHENTICATION_PROFILE)) {

	    String[] ssoArgs = extractCredentials(pjp);
	    String username = ssoArgs[0];
	    String password = ssoArgs[1];

	    if (getoAuthClient().authenticate(username, password)) {
		UserDetails userDetails = getUserProfileService().loadUserByEmail(username);
		authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
			userDetails.getAuthorities());
		getCacheGateway().put(WS_V2_USERS_SESSIONS, securityTicket, authentication);
	    }
	}

	return authentication;
    }

    private CacheGateway<String, Authentication> getCacheGateway() {
	return _cacheGateway;
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

    private boolean resolveReturnResponseCode(ProceedingJoinPoint pjp) {
	boolean result = false;
	Signature signature = pjp.getSignature();
	if (signature instanceof MethodSignature) {
	    Class<?> returnClass = ((MethodSignature) signature).getReturnType();
	    result = BaseResponse.class.isAssignableFrom(returnClass);
	}
	return result;
    }

    private BaseResponse signalError(boolean returnResponseCode) {
	Validate.isTrue(returnResponseCode, Messages.getString("WsAuthenticationInterceptor.1")); //$NON-NLS-1$
	BaseResponse response = new BaseResponse();
	response.setReturnCode(UNAUTHORIZED_ACCESS);
	response.setSuccess(false);
	return response;
    }
}
