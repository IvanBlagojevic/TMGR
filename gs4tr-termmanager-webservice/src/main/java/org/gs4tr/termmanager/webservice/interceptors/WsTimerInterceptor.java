package org.gs4tr.termmanager.webservice.interceptors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.webservice.model.response.BaseResponse;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class WsTimerInterceptor {

    private static final String TIME = "time"; //$NON-NLS-1$

    // TODO: We should do this in semaphore interceptor
    @Around("org.gs4tr.termmanager.webservice.interceptors.WsSharedPointcuts.wsMethods()")
    public Object setExecutionTime(ProceedingJoinPoint pjp) throws Throwable {

	final long start = System.currentTimeMillis();
	Object response = pjp.proceed();
	if (response instanceof ModelMapResponse) {
	    ((ModelMapResponse) response).addObject(TIME, System.currentTimeMillis() - start);
	} else if (response instanceof BaseResponse) {
	    ((BaseResponse) response).setTime(System.currentTimeMillis() - start);
	}

	return response;
    }

}
