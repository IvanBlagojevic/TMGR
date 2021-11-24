package org.gs4tr.termmanager.service.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.gs4tr.foundation.modules.entities.model.ThreadBoundContextVariables;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class ServiceThreadPool extends ThreadPoolExecutor {

    public ServiceThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
	    BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
	super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
	super.afterExecute(r, t);

	SecurityContextHolder.getContext().setAuthentication(null);
	UserProfileContext.clearContext();
	ThreadBoundContextVariables.clearVariables();
    }
}