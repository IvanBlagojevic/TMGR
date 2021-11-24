package org.gs4tr.termmanager.webservice.model.response;

import io.swagger.annotations.ApiModelProperty;

public class BaseResponse {

    private int _returnCode;
    private boolean _success;
    private long _time;

    @ApiModelProperty(value = "If user successfully logged/logged out, this value will be set to zero. Otherwise, it may have different value.")
    public int getReturnCode() {
	return _returnCode;
    }

    @ApiModelProperty(value = "Determines whether the request was successful or not.", example = "true")
    public boolean getSuccess() {
	return _success;
    }

    @ApiModelProperty(value = "Request/Response execution time in milliseconds.")
    public long getTime() {
	return _time;
    }

    public void setReturnCode(int returnCode) {
	_returnCode = returnCode;
    }

    public void setSuccess(boolean success) {
	_success = success;
    }

    public void setTime(long time) {
	_time = time;
    }
}
