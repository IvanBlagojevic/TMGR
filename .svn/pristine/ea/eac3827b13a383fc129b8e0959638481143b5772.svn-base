package org.gs4tr.termmanager.webservice.model.response;

import io.swagger.annotations.ApiModelProperty;

public class UserProjectResponse extends BaseResponse {

    ProjectDto[] _projects;

    public UserProjectResponse(int returnCode, boolean success, ProjectDto[] projectDto) {
	setReturnCode(returnCode);
	setSuccess(success);
	_projects = projectDto;
    }

    public UserProjectResponse() {
    }

    @ApiModelProperty(value = "Object that contains information about project(s).")
    public ProjectDto[] getProjects() {
	return _projects;
    }

    public void setProjects(ProjectDto[] projectDto) {
	_projects = projectDto;
    }
}
