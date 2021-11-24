package org.gs4tr.termmanager.webservice.model.request;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class GetProjectMetadataCommand extends BaseCommand {

    private List<String> _languages;

    private String _organizationName;

    private String _projectName;

    private String _projectShortcode;

    private String _username;

    @ApiModelProperty(value = "Collection of project languages.")
    public List<String> getLanguages() {
	return _languages;
    }

    @ApiModelProperty(value = "Organization name. Can be prefix, suffix or keyword.")
    public String getOrganizationName() {
	return _organizationName;
    }

    @ApiModelProperty(value = "Project name. Can be prefix, suffix or keyword.")
    public String getProjectName() {
	return _projectName;
    }

    @ApiModelProperty(value = "Project unique identifier.")
    public String getProjectShortcode() {
	return _projectShortcode;
    }

    @ApiModelProperty(value = "Generic user's username. Can be prefix, suffix or keyword.")
    public String getUsername() {
	return _username;
    }

    public void setLanguages(List<String> languages) {
	_languages = languages;
    }

    public void setOrganizationName(String organizationName) {
	_organizationName = organizationName;
    }

    public void setProjectName(String projectName) {
	_projectName = projectName;
    }

    public void setProjectShortcode(String projectShortcode) {
	_projectShortcode = projectShortcode;
    }

    public void setUsername(String username) {
	_username = username;
    }
}
