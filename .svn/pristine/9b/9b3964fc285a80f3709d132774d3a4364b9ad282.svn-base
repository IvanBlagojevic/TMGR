package org.gs4tr.termmanager.webservice.controllers;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.eventlogging.spring.aop.EventLogger;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.termmanager.model.ProjectMetadata;
import org.gs4tr.termmanager.model.ProjectMetadataRequest;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.logging.util.EventContextConstants;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.termmanager.webservice.exceptions.BrokenStreamException;
import org.gs4tr.termmanager.webservice.model.request.GetProjectMetadataCommand;
import org.gs4tr.termmanager.webservice.model.response.BaseResponse;
import org.gs4tr.termmanager.webservice.model.response.ProjectMetadataResponse;
import org.gs4tr.termmanager.webservice.streamers.ProjectMetadataStreamer;
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

@RequestMapping("/rest/v2/getProjectMetadata")
@RestController
@Api(value = "Get Project Metadata")
public class GetProjectMetadataController {

    protected static final Log LOGGER = LogFactory.getLog(GetProjectMetadataController.class);

    @Autowired
    private ProjectService _projectService;

    @ApiResponses(value = {
	    @ApiResponse(code = 200, message = "Successful operation.", response = ProjectMetadataResponse.class),
	    @ApiResponse(code = 500, message = "Internal server error.") })
    @ApiOperation(value = "Search for enabled projects metadata.", httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @LogEvent(action = TMGREventActionConstants.ACTION_TYPE_GET_PROJECT_METADATA, actionCategory = TMGREventActionConstants.ACTION_TYPE_REST_V2)
    public BaseResponse getProjectMetadata(@RequestBody GetProjectMetadataCommand command, HttpServletResponse response)
	    throws Exception {
	response.setContentType(MediaType.APPLICATION_JSON_VALUE);
	long startTime = System.currentTimeMillis();

	addEventToContext(command);
	logEvent(command);

	ProjectMetadataStreamer streamer = null;

	try {
	    streamer = new ProjectMetadataStreamer(response.getOutputStream());

	    List<ProjectMetadata> projectMetadataList = generateProjectMetadata(command);

	    streamer.writeResponseHeader();

	    for (int i = 0; i < projectMetadataList.size(); i++) {
		streamer.writeResponseBody(projectMetadataList.get(i), i, projectMetadataList.size());
	    }

	    long endTime = System.currentTimeMillis();
	    streamer.writeFooterAndCloseOutputStream(startTime, endTime);

	} catch (Exception e) {
	    throw new BrokenStreamException(Messages.getString("IOError"), e);
	} finally {
	    if (streamer != null && streamer.getOutputStream() != null) {
		streamer.closeOutputStream();
	    }
	}

	return null;
    }

    private void addEventToContext(GetProjectMetadataCommand command) {
	EventLogger.addProperty(EventContextConstants.ORGANIZATION_NAME, command.getOrganizationName());
	EventLogger.addProperty(EventContextConstants.LANGUAGES, command.getLanguages());
	EventLogger.addProperty(EventContextConstants.PROJECT_NAME, command.getProjectName());
	EventLogger.addProperty(EventContextConstants.PROJECT_SHORT_CODE, command.getProjectShortcode());
	EventLogger.addProperty(EventContextConstants.ORGANIZATION_NAME, command.getOrganizationName());

    }

    private ProjectMetadataRequest createMetadataRequest(GetProjectMetadataCommand command) {
	return new ProjectMetadataRequest(command.getLanguages(), command.getOrganizationName(),
		command.getProjectName(), command.getProjectShortcode(), command.getUsername());
    }

    private List<ProjectMetadata> generateProjectMetadata(GetProjectMetadataCommand command) {
	return getProjectService().getProjectMetadata(createMetadataRequest(command));
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private void logEvent(GetProjectMetadataCommand command) {
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("GetProjectMetadataController.0"), //$NON-NLS-1$
		    TmUserProfile.getCurrentUserName(), command.getOrganizationName(), command.getProjectName(),
		    command.getProjectShortcode(), command.getLanguages(), command.getUsername()));
	}

    }

}
