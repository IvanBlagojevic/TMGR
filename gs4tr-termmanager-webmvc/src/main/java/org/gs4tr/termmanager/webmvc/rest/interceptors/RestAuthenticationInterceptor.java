package org.gs4tr.termmanager.webmvc.rest.interceptors;

import static org.gs4tr.termmanager.cache.model.CacheName.WS_V1_USERS_SESSIONS;

import java.lang.annotation.Annotation;
import java.util.Objects;

import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.gs4tr.eventlogging.api.EventThreadContext;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.foundation.modules.webmvc.logging.util.builder.HttpRequestThreadContextBuilder;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@Aspect
public class RestAuthenticationInterceptor {

    private static final String SESSION_KEY = "userId"; //$NON-NLS-1$

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, Authentication> _cacheGateway;

    @Autowired
    private SessionService _sessionService;

    @Around("org.gs4tr.termmanager.webmvc.rest.interceptors.RestAuthenticationPointcuts.restMethodsExceptLogin()")
    public Object authenticate(ProceedingJoinPoint pjp) throws Throwable {

	String sessionKey = extractSessionKey(pjp);

	Authentication authentication = getAuthentication(sessionKey);
	if (Objects.isNull(authentication)) {
	    throw new RuntimeException(Messages.getString("RestAuthenticationInterceptor.1")); //$NON-NLS-1$
	}

	EventThreadContext.addProperty(HttpRequestThreadContextBuilder.HTTP_USER_KEY, authentication.getName());
	EventThreadContext.addProperty(HttpRequestThreadContextBuilder.HTTP_SESSION_ID_KEY, sessionKey);

	getSessionService().registerAuthentication(authentication);

	return pjp.proceed();
    }

    private String extractSessionKey(ProceedingJoinPoint pjp) {
	Annotation[][] parameterAnnotations = getParameterAnnotations(pjp);

	for (int i = 0, j = parameterAnnotations.length; i < j; i++) {
	    Annotation[] annotations = parameterAnnotations[i];
	    for (Annotation annotation : annotations) {
		if (annotation instanceof RequestParam) {
		    RequestParam requestParam = (RequestParam) annotation;
		    if (SESSION_KEY.equals(requestParam.value())) {
			return (String) pjp.getArgs()[i];
		    }
		}
	    }
	}
	return null;
    }

    private Authentication getAuthentication(String sessionKey) {
	return getCacheGateway().get(WS_V1_USERS_SESSIONS, sessionKey);
    }

    private CacheGateway<String, Authentication> getCacheGateway() {
	return _cacheGateway;
    }

    private Annotation[][] getParameterAnnotations(ProceedingJoinPoint pjp) {
	StaticPart staticPart = pjp.getStaticPart();
	MethodSignature signature = (MethodSignature) staticPart.getSignature();
	return signature.getMethod().getParameterAnnotations();
    }

    private SessionService getSessionService() {
	return _sessionService;
    }
}
