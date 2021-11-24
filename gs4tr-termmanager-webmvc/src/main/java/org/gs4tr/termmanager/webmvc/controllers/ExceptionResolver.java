package org.gs4tr.termmanager.webmvc.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.usermanager.service.impl.SessionServiceException;
import org.gs4tr.foundation.modules.usermanager.service.impl.UserDisabledException;
import org.gs4tr.foundation.modules.webmvc.controllers.MvcExceptionHandler;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.model.ErrorTypeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Component
public class ExceptionResolver implements MvcExceptionHandler {

    private static final Log LOGGER = LogFactory.getLog(ExceptionResolver.class);

    @Value("${file.maxUploadSize:536870912}")
    private Long _maxUploadSize;

    @Override
    public Object handleException(Exception ex) {
	ModelMapResponse modelMap = new ModelMapResponse();

	String message = ex.getMessage();
	if (ex instanceof SessionServiceException || ex instanceof UserDisabledException) {
	    LOGGER.error(ex.getMessage());
	} else if (ex instanceof AuthenticationCredentialsNotFoundException) {
	    message = MessageResolver.getMessage("JsonExceptionResolver.0"); //$NON-NLS-1$
	    LOGGER.error(message);
	} else if (ex instanceof UserException) {
	    UserException userException = (UserException) ex;
	    message = userException.getMessage();
	    modelMap.addObject(ControllerConstants.DESCRIPTION, userException.getDescription());
	    ErrorTypeEnum errorType = ControllerConstants.LOGIN_FAILED.equals(message) ? ErrorTypeEnum.ERROR
		    : ErrorTypeEnum.WARNING;
	    modelMap.addObject(ControllerConstants.TYPE, errorType);
	} else if (ex instanceof MaxUploadSizeExceededException) {
	    long maxUploadSize = getMaxUploadSize() / 1024 * 1024;
	    message = MessageResolver.getMessage("ExceptionResolver.0"); //$NON-NLS-1$
	    StringBuilder descriptionBuilder = new StringBuilder();
	    descriptionBuilder.append(MessageResolver.getMessage("ExceptionResolver.1")); //$NON-NLS-1$
	    descriptionBuilder.append(String.format(MessageResolver.getMessage("ExceptionResolver.2"), maxUploadSize)); //$NON-NLS-1$

	    modelMap.addObject(ControllerConstants.TYPE, ErrorTypeEnum.WARNING);
	    modelMap.addObject(ControllerConstants.DESCRIPTION, descriptionBuilder.toString());

	    LOGGER.warn(ex.getMessage(), ex);

	    modelMap.addObject(ControllerConstants.FILE_IS_TO_LARGE, Boolean.TRUE);
	} else {
	    message = MessageResolver.getMessage("JsonExceptionResolver.1"); //$NON-NLS-1$
	    LOGGER.error(ex.getMessage(), ex);
	}

	modelMap.addObject(ControllerConstants.SUCCESS, Boolean.FALSE);
	modelMap.addObject(ControllerConstants.REASONS, message);

	return modelMap;
    }

    private Long getMaxUploadSize() {
	return _maxUploadSize;
    }
}
