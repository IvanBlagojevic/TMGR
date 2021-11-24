package org.gs4tr.termmanager.webservice.controllers;

import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.OK;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.eventlogging.spring.aop.EventLogger;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.logging.util.EventContextConstants;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.termmanager.webservice.converters.TermEntryConverter;
import org.gs4tr.termmanager.webservice.model.request.ConcordanceSearchCommand;
import org.gs4tr.termmanager.webservice.model.response.BaseResponse;
import org.gs4tr.termmanager.webservice.model.response.ErrorResponse;
import org.gs4tr.termmanager.webservice.model.response.SearchTermEntryResponse;
import org.gs4tr.termmanager.webservice.model.response.TermEntryHit;
import org.gs4tr.termmanager.webservice.utils.V2Utils;
import org.gs4tr.tm3.api.Page;
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
 * This class provide option to browse terms in specified Term Manager project.
 *
 * @author TMGR_Backend
 */
@RequestMapping("/rest/v2/searchTermEntries")
@RestController
@Api(value = "Search Term Entries")
public class SearchTermEntriesController extends AbstractTermSearchController {

    private static final Log LOGGER = LogFactory.getLog(SearchTermEntriesController.class);

    @ApiResponses(value = {
	    @ApiResponse(code = 200, message = "Successful operation.", response = SearchTermEntryResponse.class),
	    @ApiResponse(code = 400, message = "Missing required parameter/Invalid parameter.", response = ErrorResponse.class),
	    @ApiResponse(code = 500, message = "Internal server error.") })
    @ApiOperation(value = "Use this method to search for term entries with filter parameters in specified project/glossary.", httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @LogEvent(action = TMGREventActionConstants.ACTION_TERM_SEARCH, actionCategory = TMGREventActionConstants.ACTION_TYPE_REST_V2)
    public BaseResponse searchTermEntries(@RequestBody ConcordanceSearchCommand command) throws Exception {

	// Project ticket and source language locale are mandatory.
	Long projectId = null;
	EventLogger.addProperty(EventContextConstants.PROJECT_ID, projectId);

	projectId = validateAndGetProjectId(command);
	validateLocales(command);

	String source = command.getSourceLocale();
	EventLogger.addProperty(EventContextConstants.SOURCE_LANGUAGE, source);

	List<String> targets = command.getTargetLocales();
	EventLogger.addProperty(EventContextConstants.TARGET_LANGUAGES, targets);

	TmProject project = V2Utils.getProject(projectId, getProjectService());
	EventLogger.addProperty(EventContextConstants.PROJECT_NAME, project.getProjectInfo().getName());
	EventLogger.addProperty(EventContextConstants.PROJECT_SHORT_CODE, project.getProjectInfo().getShortCode());

	logRequestEvent(source, project, targets);

	Page<TermEntry> page = search(command, projectId);

	List<TermEntryHit> hits = TermEntryConverter.convertToTermEntryHits(page.getResults(), source, targets);

	return new SearchTermEntryResponse(OK, true, page.getLength(), page.getOffset(), page.getTotalResults(), hits);
    }

    private void logRequestEvent(String sourceLocale, TmProject project, List<String> targetLocales) {
	if (LOGGER.isDebugEnabled()) {
	    String projectName = V2Utils.getProjectName(project);
	    LOGGER.debug(String.format(Messages.getString("SearchTermEntriesController.4"), //$NON-NLS-1$
		    TmUserProfile.getCurrentUserName(), projectName, sourceLocale, targetLocales));
	}
    }
}
