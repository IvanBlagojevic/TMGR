package org.gs4tr.termmanager.webservice.controllers;

import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.OK;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.solr.restore.IRestoreProcessorV2;
import org.gs4tr.termmanager.service.solr.restore.RecodeOrCloneTermsProcessor;
import org.gs4tr.termmanager.service.solr.restore.model.RecodeOrCloneCommand;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.termmanager.webservice.exceptions.UnauthorizedAccessException;
import org.gs4tr.termmanager.webservice.model.request.RecodeOrCloneTermsCommand;
import org.gs4tr.termmanager.webservice.model.response.BaseResponse;
import org.gs4tr.termmanager.webservice.model.response.RecodeOrCloneResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/rest/v2/recodeOrClone")
@RestController("recodeOrCloneController")
public class RecodeOrCloneController {

    private static Log LOGGER = LogFactory.getLog(RecodeOrCloneController.class);

    private ExecutorService _executorService = Executors.newFixedThreadPool(1);

    @Autowired
    private IRestoreProcessorV2 _iRestoreProcessorV2;

    private AtomicBoolean _recodeOrCloneAlreadyInProgress = new AtomicBoolean(Boolean.FALSE);

    @Autowired
    private RecodeOrCloneTermsProcessor _recodeOrCloneTermsProcessor;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse recodeOrClone(@RequestBody RecodeOrCloneTermsCommand command) throws Exception {

	try {
	    validateAndSetAlreadyInProgress();
	} catch (Exception e) {
	    throw new UnauthorizedAccessException(e.getMessage(), e);
	}

	/*
	 * *****************************************************************************
	 * Note: recodeOrCloneAlreadyInProgress should be set to false only in runnable
	 * or in catch block
	 * *****************************************************************************
	 */
	try {

	    validateUser();

	    String recodeCommandsString = command.getRecodeCommands();
	    String cloneCommandsString = command.getCloneCommands();

	    List<RecodeOrCloneCommand> recodeCommands = new ArrayList<>();
	    List<RecodeOrCloneCommand> cloneCommands = new ArrayList<>();

	    getRecodeOrCloneTermsProcessor().initAndValidateCommands(recodeCommands, cloneCommands,
		    recodeCommandsString, cloneCommandsString);

	    performOperation(recodeCommands, cloneCommands);
	} catch (UnauthorizedAccessException uae) {
	    setRecodeOrCloneOperationToFinished();
	    throw new UnauthorizedAccessException(uae.getMessage(), uae);
	} catch (Exception e) {
	    setRecodeOrCloneOperationToFinished();
	    throw new IllegalArgumentException(e.getMessage(), e);
	}

	return new RecodeOrCloneResponse(OK, true, Messages.getString("RecodeOrCloneController.1"));
    }

    private RecodeOrCloneTermsProcessor getRecodeOrCloneTermsProcessor() {
	return _recodeOrCloneTermsProcessor;
    }

    private Runnable getRunnableRecodeOrClone(List<RecodeOrCloneCommand> recodeCommands,
	    List<RecodeOrCloneCommand> cloneCommands) {
	return () -> {
	    try {
		getiRestoreProcessorV2().restoreRecodeOrClone(recodeCommands, cloneCommands);
	    } catch (Exception e) {
		LOGGER.error(e.getMessage(), e);
	    } finally {

	    }
	};
    }

    private IRestoreProcessorV2 getiRestoreProcessorV2() {
	return _iRestoreProcessorV2;
    }

    private void performOperation(List<RecodeOrCloneCommand> recodeCommands, List<RecodeOrCloneCommand> cloneCommands) {

	Runnable recodeOrCloneRunnable = getRunnableRecodeOrClone(recodeCommands, cloneCommands);

	_executorService.execute(recodeOrCloneRunnable);
    }

    private void setRecodeOrCloneOperationToFinished() {
	_recodeOrCloneAlreadyInProgress.set(Boolean.FALSE);
    }

    private void validateAndSetAlreadyInProgress() {
	boolean isAlreadyInProgress = _recodeOrCloneAlreadyInProgress.getAndSet(Boolean.TRUE);
	if (isAlreadyInProgress) {
	    throw new RuntimeException(Messages.getString("RecodeOrCloneController.2"));
	}
    }

    private void validateUser() throws UnauthorizedAccessException {

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	if (Objects.isNull(userProfile)) {
	    throw new UnauthorizedAccessException(Messages.getString("RecodeOrCloneController.3"));
	}

	boolean isPowerUser = userProfile.isPowerUser();
	boolean isAdmin = ServiceUtils.isAdminUser(userProfile);

	if (!isPowerUser && !isAdmin) {
	    throw new UnauthorizedAccessException(Messages.getString("RecodeOrCloneController.4"));
	}
    }

}
