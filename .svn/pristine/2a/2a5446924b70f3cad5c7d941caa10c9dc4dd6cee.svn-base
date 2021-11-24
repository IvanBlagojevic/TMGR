package org.gs4tr.termmanager.webservice.controllers;

import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.OK;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.webservice.converters.ProjectConverter;
import org.gs4tr.termmanager.webservice.model.request.BrowseUserProjectsCommand;
import org.gs4tr.termmanager.webservice.model.response.BaseResponse;
import org.gs4tr.termmanager.webservice.model.response.ErrorResponse;
import org.gs4tr.termmanager.webservice.model.response.ProjectDto;
import org.gs4tr.termmanager.webservice.model.response.UserProjectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This class provides a method to browse user projects information.
 *
 * @author TMGR_Backend
 */
@RequestMapping("/rest/v2/userProjects")
@RestController
@Api(tags = "Get User Projects")
public class GetUserProjectsDataController {

    private static final String PROJECTS = "projects"; //$NON-NLS-1$

    @Autowired
    private ProjectService _projectService;

    @ApiResponses(value = {
	    @ApiResponse(code = 200, message = "Successful operation.", response = UserProjectResponse.class),
	    @ApiResponse(code = 400, message = "Missing required parameter/Invalid parameter.", response = ErrorResponse.class),
	    @ApiResponse(code = 500, message = "Internal server error.") })
    @ApiOperation(value = "Use this method to get all available projects/glossaries for user that is currently logged.", response = UserProjectResponse.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse getUserProjects(@RequestBody BrowseUserProjectsCommand command) {

	TmUserProfile user = TmUserProfile.getCurrentUserProfile();

	List<TmProject> userProjects = getProjectService().getUserProjects(user.getUserProfileId(),
		TmOrganization.class);

	Map<Long, Set<String>> projectUserLanguages = null;
	if (command.isFetchLanguages()) {
	    projectUserLanguages = user.getProjectUserLanguages();
	}

	ProjectDto[] userProjectsDto = ProjectConverter.convertProjectsToDto(userProjects, projectUserLanguages);

	return new UserProjectResponse(OK, true, userProjectsDto);
    }

    private ProjectService getProjectService() {
	return _projectService;
    }
}
