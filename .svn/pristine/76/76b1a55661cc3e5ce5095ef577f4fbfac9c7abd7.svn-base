package org.gs4tr.termmanager.webservice.controllers;

import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.INTERNAL_SERVER_ERROR;
import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.INVALID_CREDENTIALS;
import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.INVALID_PARAMETERS;
import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.PROCEEDING_REQUEST_ERROR;
import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.PROJECT_LOCKED;
import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.UNAUTHORIZED_ACCESS;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.eventlogging.core.executor.builder.ExceptionEventDataBuilder;
import org.gs4tr.eventlogging.spring.aop.EventLogger;
import org.gs4tr.foundation.modules.usermanager.service.impl.SessionServiceException;
import org.gs4tr.termmanager.webservice.exceptions.BrokenStreamException;
import org.gs4tr.termmanager.webservice.exceptions.MaximumPermittedSegmentsException;
import org.gs4tr.termmanager.webservice.exceptions.ProjectLockedException;
import org.gs4tr.termmanager.webservice.exceptions.UnauthorizedAccessException;
import org.gs4tr.termmanager.webservice.model.response.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice(basePackages = "org.gs4tr.termmanager.webservice.controllers")
public class RestV2ErrorHandler {

    private static final Log LOG = LogFactory.getLog(RestV2ErrorHandler.class);

    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<ErrorResponse> handleClassCastException(Exception e) {
	log(e);
	return error(Messages.getString("ExportFormatError"), INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(Exception e) {
	log(e);
	return error(e.getMessage(), INVALID_PARAMETERS, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BrokenStreamException.class)
    public ResponseEntity<ErrorResponse> handleIOException(Exception e) {
	log(e);
	return error(e.getMessage(), PROCEEDING_REQUEST_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(Exception e) {
	log(e);
	return error(e.getMessage(), INVALID_PARAMETERS, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaximumPermittedSegmentsException.class)
    public ResponseEntity<ErrorResponse> handleMaximumPermittedSegmentsException(Exception e) {
	log(e);
	return error(e.getMessage(), INVALID_PARAMETERS, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(Exception e) {
	log(e);
	return error(e.getMessage(), INVALID_PARAMETERS, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SessionServiceException.class)
    public ResponseEntity<ErrorResponse> handleSessionServiceException(Exception e) {
	log(e);
	return error(Messages.getString("LoginFailed"), INVALID_CREDENTIALS, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAccessException(Exception e) {
	log(e);
	return error(e.getMessage(), UNAUTHORIZED_ACCESS, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProjectLockedException.class)
    public ResponseEntity<ErrorResponse> handleUserException(Exception e) {
	log(e);
	return error(e.getMessage(), PROJECT_LOCKED, HttpStatus.LOCKED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> resolveException(Exception e) {
	log(e);
	return error(e.getMessage(), UNAUTHORIZED_ACCESS, HttpStatus.BAD_REQUEST);
    }

    private ErrorResponse createErrorResponse(String message, int returnCode) {
	ErrorResponse response = new ErrorResponse();
	response.setReturnCode(returnCode);
	response.setErrorMessage(message);
	return response;
    }

    private ResponseEntity<ErrorResponse> error(String message, int returnCode, HttpStatus status) {
	ErrorResponse response = createErrorResponse(message, returnCode);

	HttpHeaders header = new HttpHeaders();
	header.setContentType(MediaType.APPLICATION_JSON);

	return new ResponseEntity<>(response, header, status);
    }

    private void log(Exception e) {
	// Exceptions log
	LOG.error(e.getMessage(), e);
	Map<String, Object> exceptionMap = new ExceptionEventDataBuilder(e).build();
	exceptionMap.entrySet().stream().forEach(ex -> EventLogger.addProperty(ex.getKey(), ex.getValue()));
    }
}
