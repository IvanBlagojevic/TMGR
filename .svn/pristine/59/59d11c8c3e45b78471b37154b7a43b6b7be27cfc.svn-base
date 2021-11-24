package org.gs4tr.termmanager.glossaryV2.logevent;

import org.gs4tr.eventlogging.api.EventThreadContext;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.springframework.stereotype.Component;

@Component
public class LogEventExecutor {

    private static final String ACTION = "action";

    private static final String ACTION_CATEGORY = "actionCategory";

    private static final String ACTION_CATEGORY_PREFIX = "external/";

    public LogEventExecutor() {

    }

    @LogEvent(action = "", actionCategory = "")
    public Object logEvent(LoggingCallback callback, String action, String actionCategory) throws OperationsException {

	EventThreadContext.addProperty(ACTION, action);
	EventThreadContext.addProperty(ACTION_CATEGORY, ACTION_CATEGORY_PREFIX.concat(actionCategory));

	return callback.log();
    }

    @LogEvent(action = "", actionCategory = "")
    public void logEvent(LoggingVoidCallback callback, String action, String actionCategory)
	    throws OperationsException {

	EventThreadContext.addProperty(ACTION, action);
	EventThreadContext.addProperty(ACTION_CATEGORY, ACTION_CATEGORY_PREFIX.concat(actionCategory));

	callback.log();
    }

    public interface LoggingCallback {
	Object log() throws OperationsException;
    }

    public interface LoggingVoidCallback {
	void log() throws OperationsException;
    }

}
