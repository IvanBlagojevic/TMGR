package org.gs4tr.termmanager.webservice.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ProjectInfo")
public class ProjectInfoDto {

    private boolean _isEnabled;
    private String _name;
    private String _shortCode;

    @ApiModelProperty(value = "The name of the project.")
    public String getName() {
	return _name;
    }

    @ApiModelProperty(value = "Project unique identifier.")
    public String getShortCode() {
	return _shortCode;
    }

    @ApiModelProperty(value = "Boolean flag. True if the project is enabled and false if the project is disabled.")
    public boolean isEnabled() {
	return _isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
	_isEnabled = isEnabled;
    }

    public void setName(String name) {
	_name = name;
    }

    public void setShortCode(String shortCode) {
	_shortCode = shortCode;
    }

}
