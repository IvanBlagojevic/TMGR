package org.gs4tr.termmanager.webservice.model.request;

import io.swagger.annotations.ApiModelProperty;

public class LoginCommand {

    private String _password;
    private String _username;

    @ApiModelProperty(value = "Account id.", required = true)
    public String getPassword() {
	return _password;
    }

    @ApiModelProperty(value = "Account password.", required = true)
    public String getUsername() {
	return _username;
    }

    public void setPassword(String password) {
	_password = password;
    }

    public void setUsername(String username) {
	_username = username;
    }

}
