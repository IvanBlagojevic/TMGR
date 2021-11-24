package org.gs4tr.termmanager.service.concurrency;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.ThreadBoundContextVariables;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service("serviceThreadPoolHandler")
public class ServiceThreadPoolHandler implements InitializingBean {

    private static final int KEEP_ALIVE = 1; // hours

    private static final Log LOGGER = LogFactory.getLog(ServiceThreadPoolHandler.class);

    private static final int NCPU = Runtime.getRuntime().availableProcessors();

    private static final String SERVICE_THREAD_NAME_PREFIX = "Service "; //$NON-NLS-1$

    private static final BlockingQueue<Runnable> SYNCHRONOUS_QUEUE = new LinkedBlockingQueue<>();

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ServiceThreadPool(NCPU, NCPU + 1, KEEP_ALIVE,
	    TimeUnit.HOURS, SYNCHRONOUS_QUEUE, runnable -> {
		Thread thread = new Thread(runnable);
		thread.setDaemon(true);

		String threadName = SERVICE_THREAD_NAME_PREFIX.concat(thread.getName());
		thread.setName(threadName);

		thread.setContextClassLoader(ServiceThreadPoolHandler.class.getClassLoader());

		return thread;
	    });

    public static void execute(RunnableCallback runnableCallback, Authentication authentication) {
	execute(createRunnable(runnableCallback, authentication, null));
    }

    public static int getActiveThreadsCount() {
	return THREAD_POOL_EXECUTOR.getActiveCount();
    }

    public static void shutDown() {
	THREAD_POOL_EXECUTOR.shutdown();
    }

    private static SessionService SESSION_SERVICE;

    @Autowired
    private SessionService _sessionService;

    @Override
    public void afterPropertiesSet() throws Exception {
	SESSION_SERVICE = getSessionService();
    }

    private static Map<String, String> copyVariables(Map<String, String> variables) {
	final Map<String, String> variablesCopy;
	if (variables != null && !variables.isEmpty()) {
	    variablesCopy = new HashMap<>(variables);
	} else {
	    variablesCopy = null;
	}
	return variablesCopy;
    }

    private static Runnable createRunnable(final RunnableCallback runnableCallback, final Authentication authentication,
	    Map<String, String> variables) {
	final Map<String, String> variablesCopy = copyVariables(variables);

	return () -> {
	    LogHelper.debug(LOGGER, Messages.getString("ServiceThreadPoolHandler.1"), //$NON-NLS-1$
		    runnableCallback.getRunnableOperation());

	    if (authentication != null) {

		UserDetails details = ((UserDetails) authentication.getPrincipal());

		LogHelper.debug(LOGGER, Messages.getString("ServiceThreadPoolHandler.2"), //$NON-NLS-1$
			details.getUsername());

		SESSION_SERVICE.registerAuthentication(authentication);
	    }

	    if ((variablesCopy != null)) {
		LogHelper.debug(LOGGER, Messages.getString("ServiceThreadPoolHandler.3")); //$NON-NLS-1$
		ThreadBoundContextVariables.setVariables(variablesCopy);
	    }

	    runnableCallback.execute();
	};
    }

    private static void execute(Runnable runnable) {
	LogHelper.debug(LOGGER, Messages.getString("ServiceThreadPoolHandler.4"), //$NON-NLS-1$
		THREAD_POOL_EXECUTOR.getActiveCount());
	THREAD_POOL_EXECUTOR.execute(runnable);
    }

    private SessionService getSessionService() {
	return _sessionService;
    }
}
