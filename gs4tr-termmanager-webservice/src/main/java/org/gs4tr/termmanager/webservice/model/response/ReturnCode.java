package org.gs4tr.termmanager.webservice.model.response;

import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;

/**
 * Instead of throwing an error, return normal response with error code.
 * 
 */
public class ReturnCode {

    public static final int INTERNAL_SERVER_ERROR = 6;

    public static final int INVALID_CREDENTIALS = 1;

    public static final int INVALID_PARAMETERS = 5;

    public static final int OK = 0;

    public static final int PROCEEDING_REQUEST_ERROR = 4;

    public static final int PROJECT_LOCKED = 3;

    public static final String RETURN_CODE = "returnCode"; //$NON-NLS-1$

    public static final int UNAUTHORIZED_ACCESS = 2;

    public static ModelMapResponse error(int code) {
	ModelMapResponse response = new ModelMapResponse();
	response.addObject(RETURN_CODE, code);
	response.addObject(ModelMapResponse.SUCCESS, Boolean.FALSE);
	return response;
    }

    public static ModelMapResponse error(int code, String message) {
	ModelMapResponse response = error(code);
	response.addObject("errorMessage", message);
	return response;
    }
}