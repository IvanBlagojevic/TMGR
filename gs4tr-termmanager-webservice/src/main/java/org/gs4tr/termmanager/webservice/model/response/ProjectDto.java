package org.gs4tr.termmanager.webservice.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Projects")
public class ProjectDto {

    private LanguageDto[] _languages;
    private String _organizationName;
    private ProjectInfoDto _projectInfo;
    private String _projectTicket;

    @ApiModelProperty(value = "The languages that are available on specified project.")
    public LanguageDto[] getLanguages() {
	return _languages;
    }

    @ApiModelProperty(value = "Organization's name.")
    public String getOrganizationName() {
	return _organizationName;
    }

    @ApiModelProperty(value = "Object with basic project info.")
    public ProjectInfoDto getProjectInfo() {
	return _projectInfo;
    }

    @ApiModelProperty(value = "Project unique identifier.")
    public String getProjectTicket() {
	return _projectTicket;
    }

    public void setLanguages(LanguageDto[] languages) {
	_languages = languages;
    }

    public void setOrganizationName(String organizationName) {
	_organizationName = organizationName;
    }

    public void setProjectInfo(ProjectInfoDto projectInfo) {
	_projectInfo = projectInfo;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }
}
