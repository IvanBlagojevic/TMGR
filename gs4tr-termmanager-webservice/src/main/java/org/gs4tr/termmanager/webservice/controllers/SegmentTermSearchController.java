package org.gs4tr.termmanager.webservice.controllers;

import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.OK;

import java.util.List;

import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.termmanager.webservice.converters.TermEntryConverter;
import org.gs4tr.termmanager.webservice.model.request.SegmentTermSearchCommand;
import org.gs4tr.termmanager.webservice.model.response.BaseResponse;
import org.gs4tr.termmanager.webservice.model.response.ErrorResponse;
import org.gs4tr.termmanager.webservice.model.response.SegmentSearchResponse;
import org.gs4tr.termmanager.webservice.model.response.SegmentTermHit;
import org.gs4tr.termmanager.webservice.utils.TermRecognitionUtils;
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
 * This class provides a method to search segment terms in specified Term
 * Manager project.
 *
 * @author TMGR_Backend
 */
@RequestMapping("/rest/v2/segmentTermSearch")
@RestController
@Api(value = "Segment Term Search")
public class SegmentTermSearchController extends BaseSegmentTermSearchController {

    @ApiResponses(value = {
	    @ApiResponse(code = 200, message = "Successful operation.", response = SegmentSearchResponse.class),
	    @ApiResponse(code = 400, message = "Missing required parameter/Invalid parameter.", response = ErrorResponse.class),
	    @ApiResponse(code = 500, message = "Internal server error.") })
    @ApiOperation(value = "Use this method to get term matches for specified segment.", nickname = "segmentTermSearch", httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @LogEvent(action = TMGREventActionConstants.ACTION_SEGMENT_SEARCH, actionCategory = TMGREventActionConstants.ACTION_TYPE_REST_V2)
    public BaseResponse segmentTermSearch(@RequestBody SegmentTermSearchCommand searchCommand) {

	List<String> targetLanguages = searchCommand.getTargetLanguages();

	String sourceLanguage = searchCommand.getSourceLanguage();

	String projectTicket = searchCommand.getProjectTicket();

	String currentUser = TmUserProfile.getCurrentUserName();

	validateRequestParameters(projectTicket, sourceLanguage, targetLanguages);
	Long projectId = TicketConverter.fromDtoToInternal(projectTicket, Long.class);
	TmProject project = V2Utils.getProject(projectId, getProjectService());
	putValuesToEventContext(projectId, sourceLanguage, targetLanguages, project);

	loggingRequestEvent(sourceLanguage, project, searchCommand.getSegment());

	Page<TermEntry> page = segmentSearch(projectId, searchCommand, searchCommand.getSegment());

	List<SegmentTermHit> hits = TermEntryConverter.convertToSegmentTermHits(page.getResults(), sourceLanguage,
		targetLanguages, currentUser, project);

	TermRecognitionUtils.highlightMatchedSegments(hits, searchCommand.getSegment(), searchCommand.isFuzzy());

	return new SegmentSearchResponse(OK, true, hits);
    }

    private void loggingRequestEvent(String sourceLanguage, TmProject project, final String segment) {
	if (LOGGER.isDebugEnabled()) {
	    String projectName = V2Utils.getProjectName(project);
	    LOGGER.debug(String.format(Messages.getString("SegmentTermSearchController.0"), //$NON-NLS-1$
		    TmUserProfile.getCurrentUserName(), projectName, sourceLanguage, segment));
	}
    }
}
