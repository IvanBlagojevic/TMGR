package org.gs4tr.termmanager.webservice.model.request;

import io.swagger.annotations.ApiModelProperty;

public class BrowseUserProjectsCommand extends BaseCommand {

    @ApiModelProperty(value = "Boolean flag. If true, project languages will be included in a response.", example = "false")
    private boolean _fetchLanguages;

    public boolean isFetchLanguages() {
	return _fetchLanguages;
    }

    public void setFetchLanguages(boolean fetchLanguages) {
	_fetchLanguages = fetchLanguages;
    }
}
