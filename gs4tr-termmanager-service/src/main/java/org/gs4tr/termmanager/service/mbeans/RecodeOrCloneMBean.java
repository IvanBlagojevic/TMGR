package org.gs4tr.termmanager.service.mbeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.service.solr.restore.IRestoreProcessorV2;
import org.gs4tr.termmanager.service.solr.restore.RecodeOrCloneTermsProcessor;
import org.gs4tr.termmanager.service.solr.restore.model.RecodeOrCloneCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@ManagedResource(objectName = "tmgr.management:name=RecodeOrClone")
@Component(value = "recodeOrCloneMBean")
public class RecodeOrCloneMBean {

    private static Log LOGGER = LogFactory.getLog(RecodeOrCloneMBean.class);

    private ExecutorService _executorService = Executors.newFixedThreadPool(1);

    @Autowired
    private IRestoreProcessorV2 _iRestoreProcessorV2;

    private AtomicBoolean _recodeOrCloneAlreadyInProgress = new AtomicBoolean(Boolean.FALSE);

    @Autowired
    private RecodeOrCloneTermsProcessor _recodeOrCloneTermsProcessor;

    @ManagedOperation(description = "Recode or clone")
    @ManagedOperationParameters({
	    @ManagedOperationParameter(name = "recodeCommandsString", description = "String with recode commands"),
	    @ManagedOperationParameter(name = "cloneCommandsString", description = "String with clone commands") })
    public void recodeOrClone(String recodeCommandsString, String cloneCommandsString) throws Exception {

	validateAndSetAlreadyInProgress();

	try {
	    performOperation(recodeCommandsString, cloneCommandsString);
	} finally {
	    setRecodeOrCloneOperationToFinished();
	}

    }

    private Callable<Exception> getCallableRecodeOrClone(List<RecodeOrCloneCommand> recodeCommands,
	    List<RecodeOrCloneCommand> cloneCommands, CountDownLatch cdl) {
	return () -> {
	    try {
		getiRestoreProcessorV2().restoreRecodeOrClone(recodeCommands, cloneCommands);
	    } catch (Exception e) {
		return e;
	    } finally {
		cdl.countDown();
	    }
	    return null;
	};
    }

    private RecodeOrCloneTermsProcessor getRecodeOrCloneTermsProcessor() {
	return _recodeOrCloneTermsProcessor;
    }

    private IRestoreProcessorV2 getiRestoreProcessorV2() {
	return _iRestoreProcessorV2;
    }

    private void performOperation(String recodeCommandsString, String cloneCommandsString) throws Exception {

	List<RecodeOrCloneCommand> recodeCommands = new ArrayList<>();
	List<RecodeOrCloneCommand> cloneCommands = new ArrayList<>();

	try {
	    getRecodeOrCloneTermsProcessor().initAndValidateCommands(recodeCommands, cloneCommands,
		    recodeCommandsString, cloneCommandsString);
	} catch (Exception e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new Exception(e.getMessage(), e);
	}

	CountDownLatch cdl = new CountDownLatch(1);

	Callable<Exception> recodeOrCloneRunnable = getCallableRecodeOrClone(recodeCommands, cloneCommands, cdl);

	Future<Exception> future = _executorService.submit(recodeOrCloneRunnable);
	cdl.await();

	Exception e = future.get();
	if (Objects.nonNull(e)) {
	    LOGGER.error(e.getMessage(), e);
	    throw new Exception(e);
	}
    }

    private void setRecodeOrCloneOperationToFinished() {
	_recodeOrCloneAlreadyInProgress.set(Boolean.FALSE);
    }

    private void validateAndSetAlreadyInProgress() throws Exception {
	boolean isAlreadyInProgress = _recodeOrCloneAlreadyInProgress.getAndSet(Boolean.TRUE);
	if (isAlreadyInProgress) {
	    throw new Exception("Recode or Clone action is already in progress!");
	}
    }
}
