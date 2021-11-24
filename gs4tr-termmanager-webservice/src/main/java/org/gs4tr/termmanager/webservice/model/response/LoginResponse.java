package org.gs4tr.termmanager.webservice.model.response;

import io.swagger.annotations.ApiModelProperty;

public class LoginResponse extends BaseResponse {

    private String _securityTicket;
    private String _version;

    public LoginResponse(int returnCode, boolean success, String securityTicket, String version) {
	setReturnCode(returnCode);
	setSuccess(success);
	_securityTicket = securityTicket;
	_version = version;
    }

    public LoginResponse(int returnCode, boolean success) {
	setReturnCode(returnCode);
	setSuccess(success);
    }

    public LoginResponse() {

    }

    @ApiModelProperty(value = "Security ticket which is used for all subsequent requests.")
    public String getSecurityTicket() {
	return _securityTicket;
    }

    @ApiModelProperty(value = "Term Manager version.")
    public String getVersion() {
	return _version;
    }

    public void setSecurityTicket(String securityTicket) {
	_securityTicket = securityTicket;
    }

    public void setVersion(String version) {
	_version = version;
    }
}
