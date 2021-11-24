package org.gs4tr.termmanager.webservice.model.request;

import io.swagger.annotations.ApiModelProperty;

public class BaseCommand {

    private String _securityTicket;

    private String _ssoPassword;

    private String _ssoUsername;

    @ApiModelProperty(value = "Security ticket which was previously obtained when connecting to Term Manager.", required = true)
    public String getSecurityTicket() {
	return _securityTicket;
    }

    @ApiModelProperty(value = "This parameter is SSO user password. Hence it is used only if Term Manager is configured for SSO authentication.")
    public String getSsoPassword() {
	return _ssoPassword;
    }

    @ApiModelProperty(value = "This parameter is SSO user email. Hence it is used only if Term Manager is configured for SSO authentication.")
    public String getSsoUsername() {
	return _ssoUsername;
    }

    public void setSecurityTicket(String securityTicket) {
	_securityTicket = securityTicket;
    }

    public void setSsoPassword(String ssoPassword) {
	_ssoPassword = ssoPassword;
    }

    public void setSsoUsername(String ssoUsername) {
	_ssoUsername = ssoUsername;
    }
}
