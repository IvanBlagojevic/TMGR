package org.gs4tr.termmanager.webservice.controllers;

import java.io.BufferedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.eventlogging.spring.aop.EventLogger;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.persistence.solr.query.IPageable;
import org.gs4tr.termmanager.persistence.solr.query.TmgrPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.export.ExportDocumentFactory;
import org.gs4tr.termmanager.service.export.ExportFormatEnum;
import org.gs4tr.termmanager.service.logging.util.EventContextConstants;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.termmanager.service.utils.TermEntryUtils;
import org.gs4tr.termmanager.webservice.exceptions.BrokenStreamException;
import org.gs4tr.termmanager.webservice.model.request.DetailedGlossaryExportCommand;
import org.gs4tr.termmanager.webservice.model.response.ErrorResponse;
import org.gs4tr.termmanager.webservice.utils.V2Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * This class provides a method to export multilingual glossary from specified
 * Term Manager project.
 *
 * @author TMGR_Backend
 */
@RequestMapping("/rest/v2/detailedExport")
@RestController
@Api(value = "Detailed Terminology Export")
public class DetailedGlossaryExportController {

    private static final String FILENAME_FORMAT_VALUE = "attachment; filename=\"%s\""; //$NON-NLS-1$

    private static final String FILENAME_HEADER_NAME = "Content-Disposition"; //$NON-NLS-1$

    private static final Log LOGGER = LogFactory.getLog(DetailedGlossaryExportController.class);

    private static final String TM_EXPORTED = "TM_Exported_"; //$NON-NLS-1$

    @Value("${index.batchSize:100}")
    private Integer _batchSize;

    @Autowired
    private ProjectService _projectService;

    @Value("${synonym.number:5}")
    private int _synonymNumber;

    @Autowired
    private TermEntryService _termEntryService;

    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful operation."),
	    @ApiResponse(code = 400, message = "Missing required parameter/Invalid parameter.", response = ErrorResponse.class),
	    @ApiResponse(code = 500, message = "Internal server error.") })
    @ApiOperation(value = "Use this method to export multilingual glossary from specified project/glossary.", httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @LogEvent(action = TMGREventActionConstants.ACTION_DETAILED_EXPORT, actionCategory = TMGREventActionConstants.ACTION_TYPE_REST_V2)
    public void exportGlossary(@RequestBody DetailedGlossaryExportCommand exportCommand, HttpServletResponse response)
	    throws Exception {

	// Project ticket and source language locale are mandatory.
	String projectTicket = exportCommand.getProjectTicket();
	Validate.notEmpty(projectTicket, Messages.getString("ProjectTicketError"));

	String sourceLocale = exportCommand.getSourceLocale();
	EventLogger.addProperty(EventContextConstants.SOURCE_LANGUAGE, sourceLocale);
	Validate.notEmpty(sourceLocale, Messages.getString("TermLanguageError"));

	Long projectId = TicketConverter.fromDtoToInternal(projectTicket, Long.class);
	EventLogger.addProperty(EventContextConstants.PROJECT_ID, projectId);

	ExportFormatEnum exportFormat = getExportFormat(exportCommand);
	EventLogger.addProperty(EventContextConstants.EXPORT_FORMAT, exportFormat.getExportName());

	final boolean isForbidden = exportCommand.isForbidden();

	List<String> targetLocales = exportCommand.getTargetLocales();
	EventLogger.addProperty(EventContextConstants.TARGET_LANGUAGES, targetLocales);

	// TODO Check this. loggingRequestEvent() also call
	TmProject project = getProjectService().load(projectId);
	Validate.notNull(project, Messages.getString("ProjectError"));

	EventLogger.addProperty(EventContextConstants.PROJECT_NAME, project.getProjectInfo().getName());
	EventLogger.addProperty(EventContextConstants.PROJECT_SHORT_CODE, project.getProjectInfo().getShortCode());

	loggingRequestEvent(sourceLocale, targetLocales, projectId, exportFormat, isForbidden);

	TermEntrySearchRequest searchRequest = createSearchRequest(sourceLocale, isForbidden, projectId,
		project.getSharePendingTerms());
	TmgrSearchFilter filter = createSearchFilter(projectId, sourceLocale, targetLocales, isForbidden);

	response.setHeader(FILENAME_HEADER_NAME, getHeader(exportFormat));
	response.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
	response.setContentType(getContentType(exportFormat));

	addTargetLocaleCodes(targetLocales, searchRequest, filter);

	ExportDocumentFactory exporter = ExportDocumentFactory.getInstance(exportFormat);

	try {
	    startExport(exporter, response, searchRequest, filter);
	} catch (Exception e) {
	    throw new BrokenStreamException(Messages.getString("IOError"), e);
	} finally {
	    exporter.close();
	}
    }

    public Integer getBatchSize() {
	return _batchSize;
    }

    private void addTargetLocaleCodes(List<String> targetLocales, TermEntrySearchRequest searchRequest,
	    TmgrSearchFilter filter) {
	if (CollectionUtils.isNotEmpty(targetLocales)) {
	    List<String> targetLocaleCodes = createTargetLanguagesForExport(targetLocales, searchRequest);
	    filter.setTargetLanguages(targetLocaleCodes);
	    searchRequest.setTargetLocales(targetLocaleCodes);
	}
    }

    // Create TmgrSearchFilter and set parameters from exportCommand
    private TmgrSearchFilter createSearchFilter(Long projectId, String sourceLocale, List<String> targetLocales,
	    boolean isForbidden) {
	TmgrSearchFilter filter = new TmgrSearchFilter();

	List<Long> projectIds = new ArrayList<>();
	projectIds.add(projectId);
	filter.setProjectIds(projectIds);

	filter.setSourceLanguage(sourceLocale);

	List<String> languageResultFields = new ArrayList<>();
	if (CollectionUtils.isNotEmpty(targetLocales)) {
	    languageResultFields.addAll(targetLocales);
	}
	languageResultFields.add(sourceLocale);
	filter.addLanguageResultField(true, getSynonymNumber(),
		languageResultFields.toArray(new String[languageResultFields.size()]));

	filter.setStatuses(getStatuses(isForbidden));

	filter.setPageable(new TmgrPageRequest(0, getBatchSize(), null));

	return filter;
    }

    // Create TermEntrySearchRequest and set parameters from exportCommand
    private TermEntrySearchRequest createSearchRequest(String sourceLocale, boolean isForbidden, Long projectId,
	    Boolean sharePendingTerms) {
	TermEntrySearchRequest searchRequest = new TermEntrySearchRequest();

	searchRequest.setProjectId(projectId);

	Locale locale = Locale.makeLocale(sourceLocale);
	String localeCode = locale.getCode();

	searchRequest.setSourceLocale(localeCode);
	searchRequest.addLanguageToExport(localeCode);
	searchRequest.setForbidden(isForbidden);
	searchRequest.setFirstTermEntry(true);
	searchRequest.setStartTime(System.currentTimeMillis());

	searchRequest.setStatuses(getExportableStatuses(isForbidden, sharePendingTerms));

	List<Attribute> attributes = getProjectService().getAttributesByProjectId(projectId);
	searchRequest.setTermAttributes(TermEntryUtils.filterSynchronizableAttributes(attributes));

	return searchRequest;
    }

    private List<String> createTargetLanguagesForExport(List<String> targetLocales,
	    TermEntrySearchRequest searchRequest) {
	List<String> targetLocaleCodes = new ArrayList<>();
	for (String targetLanguage : targetLocales) {
	    Locale targetLocale = Locale.makeLocale(targetLanguage);
	    String targetLocaleCode = targetLocale.getCode();
	    targetLocaleCodes.add(targetLocale.getCode());
	    searchRequest.addLanguageToExport(targetLocaleCode);
	}
	return targetLocaleCodes;
    }

    private void exportDocument(List<TermEntry> termEntries, TermEntrySearchRequest searchRequest,
	    ExportDocumentFactory exporter) {
	getTermEntryService().exportDocumentWS(termEntries, searchRequest, null, exporter);
    }

    private String fileNameResolver(String exportFormat) {
	String fileName = TM_EXPORTED + Calendar.getInstance().getTimeInMillis();

	String jsonExport = ExportFormatEnum.JSON_V2.getExportName();

	String tbxExport = ExportFormatEnum.TBX.getExportName();

	if (exportFormat.equalsIgnoreCase(jsonExport)) {
	    fileName = fileName.concat(jsonExport);
	} else if (exportFormat.equalsIgnoreCase(tbxExport)) {
	    fileName = fileName.concat(tbxExport);
	}

	return fileName;
    }

    private String formatedFileName(ExportFormatEnum exportFormat) {
	return fileNameResolver(exportFormat.toString());
    }

    private String getContentType(ExportFormatEnum exportFormat) {
	return ExportFormatEnum.JSON_V2 == exportFormat ? MediaType.APPLICATION_JSON_VALUE
		: MediaType.APPLICATION_XML_VALUE;
    }

    private ExportFormatEnum getExportFormat(DetailedGlossaryExportCommand exportCommand) {
	ExportFormatEnum fileType = exportCommand.getFileType();
	if (ExportFormatEnum.JSON == fileType) {
	    return ExportFormatEnum.JSON_V2;
	} else if (ExportFormatEnum.TBX == fileType) {
	    return ExportFormatEnum.TBX;
	} else {
	    throw new RuntimeException(
		    String.format(Messages.getString("DetailedGlossaryExportController.2"), fileType)); //$NON-NLS-1$
	}
    }

    private List<String> getExportableStatuses(boolean isForbidden, Boolean sharePendingTerms) {
	final List<String> exportableStatuses = new ArrayList<>(4);

	ItemStatusType processed = ItemStatusTypeHolder.PROCESSED;
	ItemStatusType blacklisted = ItemStatusTypeHolder.BLACKLISTED;
	ItemStatusType pending = ItemStatusTypeHolder.WAITING;

	exportableStatuses.add(blacklisted.getName());

	if (!isForbidden) {
	    exportableStatuses.add(processed.getName());
	    if (ServiceUtils.isSharePendingTermsOnProject(sharePendingTerms)) {
		exportableStatuses.add(pending.getName());
	    }
	}

	return exportableStatuses;
    }

    private String getHeader(ExportFormatEnum exportFormat) {
	return String.format(FILENAME_FORMAT_VALUE, formatedFileName(exportFormat));
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private List<String> getStatuses(boolean searchForbidden) {
	ItemStatusType blacklisted = ItemStatusTypeHolder.BLACKLISTED;
	ItemStatusType processed = ItemStatusTypeHolder.PROCESSED;
	ItemStatusType pending = ItemStatusTypeHolder.WAITING;
	ItemStatusType onHold = ItemStatusTypeHolder.ON_HOLD;

	return searchForbidden ? Arrays.asList(blacklisted.getName())
		: Arrays.asList(processed.getName(), pending.getName(), onHold.getName(), blacklisted.getName());
    }

    private int getSynonymNumber() {
	return _synonymNumber;
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    /***
     * Is debug logging currently enabled? Call this method to prevent having to
     * perform expensive operations (for example, loading <code>TmProject</code> )
     * because we want to do that only in debug mode.
     *
     * @param sourceLocale
     * @param projectId
     * @param exportFormat
     * @param isForbidden
     */
    private void loggingRequestEvent(String sourceLocale, List<String> targetLocales, Long projectId,
	    ExportFormatEnum exportFormat, final boolean isForbidden) {
	if (LOGGER.isDebugEnabled()) {
	    String projectName = V2Utils.getProjectName(projectId, getProjectService());
	    String exportName = exportFormat.getExportName();
	    LOGGER.debug(String.format(Messages.getString("DetailedGlossaryExportController.0"), //$NON-NLS-1$
		    TmUserProfile.getCurrentUserName(), projectName, sourceLocale, targetLocales, isForbidden,
		    exportName));
	}
    }

    private void startExport(ExportDocumentFactory exporter, HttpServletResponse response,
	    TermEntrySearchRequest searchRequest, TmgrSearchFilter filter) throws Exception {
	exporter.open(new BufferedOutputStream(response.getOutputStream()), searchRequest, null);

	List<TermEntry> termentries = getTermEntryService().searchTermEntries(filter).getResults();
	while (CollectionUtils.isNotEmpty(termentries)) {
	    exportDocument(termentries, searchRequest, exporter);
	    IPageable pageable = filter.getPageable();
	    pageable = pageable.next();
	    filter.setPageable(pageable);

	    termentries = getTermEntryService().searchTermEntries(filter).getResults();
	}
    }
}
