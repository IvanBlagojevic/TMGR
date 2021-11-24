package org.gs4tr.termmanager.webservice.model.response;

import io.swagger.annotations.ApiModelProperty;

public class ErrorResponse extends BaseResponse {

    @ApiModelProperty(value = "Message which contains information about cause of error.")
    private String _errorMessage;

    public String getErrorMessage() {
	return _errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
	_errorMessage = errorMessage;
    }
}
