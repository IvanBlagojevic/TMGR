package org.gs4tr.termmanager.service.solr.restore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.termmanager.service.solr.restore.model.RebuildException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component("restoreFromBackupInitializer")
public class RestoreFromBackupInitializer implements InitializingBean {

    private static final Log LOGGER = LogFactory.getLog(RestoreFromBackupInitializer.class);

    @Autowired
    private ICleanUpProcessorV2 _cleanUpProcessorV2;

    @Value("${clear.disabled.terminology.from.backup:false}")
    private boolean _clearTerminology;

    @Value("${index.restoreFromBackup:false}")
    private boolean _restoreFromBackup;

    @Autowired
    private RestoreInfoProcessor _restoreInfoProcessor;

    @Autowired
    @Qualifier(value = "restoreProcessorV2")
    private IRestoreProcessorV2 _restoreProcessorV2;

    @Autowired
    private SessionService _sessionService;

    @Override
    public void afterPropertiesSet() throws RebuildException, InterruptedException {
	if (getRestoreInfoProcessor().isRebuildIndexEnabled() || isRestoreFromBackup()) {
	    rebuildIndex();
	}
    }

    @Autowired
    private ICleanUpProcessorV2 getCleanUpProcessorV2() {
	return _cleanUpProcessorV2;
    }

    private RestoreInfoProcessor getRestoreInfoProcessor() {
	return _restoreInfoProcessor;
    }

    private IRestoreProcessorV2 getRestoreProcessorV2() {
	return _restoreProcessorV2;
    }

    private SessionService getSessionService() {
	return _sessionService;
    }

    private boolean isClearTerminology() {
	return _clearTerminology;
    }

    private boolean isRestoreFromBackup() {
	return _restoreFromBackup;
    }

    private void rebuildIndex() {
	Thread thread = new Thread(restore());
	thread.start();
    }

    private Runnable restore() {
	return () -> {
	    try {
		if (isClearTerminology()) {
		    getCleanUpProcessorV2().cleanup();
		}
		getRestoreProcessorV2().restore();
		getRestoreInfoProcessor().disableRebuildIndex();
	    } catch (Exception exception) {
		throw new RebuildException(exception.getMessage(), exception);
	    }
	};
    }
}
