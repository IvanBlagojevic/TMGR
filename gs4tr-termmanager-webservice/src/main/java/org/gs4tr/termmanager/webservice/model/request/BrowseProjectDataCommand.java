package org.gs4tr.termmanager.webservice.model.request;

import io.swagger.annotations.ApiModelProperty;

public class BrowseProjectDataCommand extends BaseCommand {

    private boolean _fetchLanguages;

    private String _projectShortcode;

    @ApiModelProperty(value = "Project unique identifier.", required = true)
    public String getProjectShortcode() {
	return _projectShortcode;
    }

    @ApiModelProperty(value = "Defines whether the project languages should be included in a response or not. Allowed values are true or false. This parameter is optional, default is false.", example = "false")
    public boolean isFetchLanguages() {
	return _fetchLanguages;
    }

    public void setFetchLanguages(boolean fetchLanguages) {
	_fetchLanguages = fetchLanguages;
    }

    public void setProjectShortcode(String projectShortcode) {
	_projectShortcode = projectShortcode;
    }
}
