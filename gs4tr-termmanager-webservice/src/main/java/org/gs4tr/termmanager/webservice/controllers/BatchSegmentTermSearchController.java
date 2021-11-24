package org.gs4tr.termmanager.webservice.controllers;

import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.OK;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.termmanager.webservice.converters.TermEntryConverter;
import org.gs4tr.termmanager.webservice.exceptions.BrokenStreamException;
import org.gs4tr.termmanager.webservice.exceptions.MaximumPermittedSegmentsException;
import org.gs4tr.termmanager.webservice.model.request.BatchSegmentTermSearchCommand;
import org.gs4tr.termmanager.webservice.model.response.BaseResponse;
import org.gs4tr.termmanager.webservice.model.response.BatchSegmentSearchResponse;
import org.gs4tr.termmanager.webservice.model.response.BatchSegmentTermHit;
import org.gs4tr.termmanager.webservice.model.response.ErrorResponse;
import org.gs4tr.termmanager.webservice.model.response.SegmentTermHit;
import org.gs4tr.termmanager.webservice.streamers.SegmentHitStreamer;
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

@RequestMapping("/rest/v2/batchSegmentTermSearch")
@RestController
@Api(value = "Multiple Segments Term Search")
public class BatchSegmentTermSearchController extends BaseSegmentTermSearchController {

    private static final Log LOGGER = LogFactory.getLog(BatchSegmentTermSearchController.class);

    @ApiResponses(value = {
	    @ApiResponse(code = 200, message = "Successful operation.", response = BatchSegmentSearchResponse.class),
	    @ApiResponse(code = 400, message = "Missing required parameter/Invalid parameter.", response = ErrorResponse.class),
	    @ApiResponse(code = 500, message = "Internal server error.") })
    @ApiOperation(value = "Use this method to get term matches for each segment in batch list.", httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @LogEvent(action = TMGREventActionConstants.ACTION_SEGMENT_SEARCH, actionCategory = TMGREventActionConstants.ACTION_TYPE_REST_V2)
    public BaseResponse segmentTermSearch(HttpServletResponse httpServletResponse,
	    @RequestBody BatchSegmentTermSearchCommand searchCommand) throws Exception {
	httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
	long startTime = System.currentTimeMillis();

	List<String> targetLanguages = searchCommand.getTargetLanguages();
	String sourceLanguage = searchCommand.getSourceLanguage();
	String projectTicket = searchCommand.getProjectTicket();
	List<String> segments = searchCommand.getBatchSegment();

	if (segments == null) {
	    return new BatchSegmentSearchResponse(OK, true);
	}

	if (segments.size() > MAXIMUM_PERMITTED_SEGMENTS) {
	    throw new MaximumPermittedSegmentsException(Messages.getString("MaximumPermittedSegments"));
	}

	String currentUser = TmUserProfile.getCurrentUserName();

	validateRequestParameters(projectTicket, sourceLanguage, targetLanguages);
	Long projectId = TicketConverter.fromDtoToInternal(projectTicket, Long.class);
	TmProject project = V2Utils.getProject(projectId, getProjectService());
	putValuesToEventContext(projectId, sourceLanguage, targetLanguages, project);

	loggingRequestEvent(sourceLanguage, project, segments);

	SegmentHitStreamer segmentHitStreamer = null;

	try {
	    segmentHitStreamer = new SegmentHitStreamer(httpServletResponse.getOutputStream());
	    segmentHitStreamer.writeResponseHeader();

	    for (int i = 0; i < segments.size(); i++) {

		String segment = segments.get(i);
		Page<TermEntry> page = segmentSearch(projectId, searchCommand, segment);

		BatchSegmentTermHit batchHit = new BatchSegmentTermHit();
		batchHit.setSegment(segment);

		List<SegmentTermHit> hits = TermEntryConverter.convertToSegmentTermHits(page.getResults(),
			sourceLanguage, targetLanguages, currentUser, project);
		TermRecognitionUtils.highlightMatchedSegments(hits, segment, searchCommand.isFuzzy());

		batchHit.setSegmentTermHits(hits);

		segmentHitStreamer.writeResponseBody(batchHit, i, segments.size());
	    }

	    long endTime = System.currentTimeMillis();
	    segmentHitStreamer.writeFooterAndCloseOutputStream(startTime, endTime);
	} catch (Exception e) {
	    throw new BrokenStreamException(Messages.getString("IOError"), e);
	} finally {
	    if (segmentHitStreamer != null && segmentHitStreamer.getOutputStream() != null) {
		segmentHitStreamer.closeOutputStream();
	    }
	}

	return null;
    }

    private void loggingRequestEvent(String sourceLanguage, TmProject project, final List<String> segments) {
	String projectName = V2Utils.getProjectName(project);
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("SegmentTermSearchController.0"), //$NON-NLS-1$
		    TmUserProfile.getCurrentUserName(), projectName, sourceLanguage, segments.size()));
	}
    }
}
