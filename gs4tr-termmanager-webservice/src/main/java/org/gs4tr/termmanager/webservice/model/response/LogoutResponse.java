package org.gs4tr.termmanager.webservice.model.response;

public class LogoutResponse extends BaseResponse {

    public LogoutResponse(int returnCode, boolean success) {
	setReturnCode(returnCode);
	setSuccess(success);
    }

    public LogoutResponse() {
    }

}
