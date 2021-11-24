package org.gs4tr.termmanager.webservice.controllers;

import static org.apache.commons.lang.Validate.notNull;
import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.OK;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.webservice.converters.ProjectConverter;
import org.gs4tr.termmanager.webservice.model.request.BrowseProjectDataCommand;
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
 * This class provides a method to browse project information.
 *
 * @author TMGR_Backend
 */
@RequestMapping("/rest/v2/projectByShortcode")
@RestController
@Api(value = "Get Project By ShortCode")
public class GetProjectDataController {

    @Autowired
    private ProjectService _projectService;

    @ApiResponses(value = {
	    @ApiResponse(code = 200, message = "Successful operation.", response = UserProjectResponse.class),
	    @ApiResponse(code = 400, message = "Missing required parameter/Invalid parameter.", response = ErrorResponse.class),
	    @ApiResponse(code = 500, message = "Internal server error.") })
    @ApiOperation(value = "Use this method to get project/glossary information based on project shortcode.", httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse browseProjectDataByShortcode(@RequestBody BrowseProjectDataCommand browseCommand) {

	String shortcode = browseCommand.getProjectShortcode();

	TmProject project = getProjectService().findProjectByShortCode(shortcode);
	notNull(project, String.format(Messages.getString("can.t.find.project.with.shortcode.s"), shortcode));

	final boolean fetchLanguages = browseCommand.isFetchLanguages();

	List<TmProject> projects = Arrays.asList(project);

	TmUserProfile user = TmUserProfile.getCurrentUserProfile();

	Map<Long, Set<String>> projectUserLanguages = null;

	if (project != null && fetchLanguages) {
	    projectUserLanguages = user.getProjectUserLanguages();
	}

	ProjectDto[] projectDto = ProjectConverter.convertProjectsToDto(projects, projectUserLanguages);

	return new UserProjectResponse(OK, true, projectDto);
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

}
