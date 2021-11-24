package org.gs4tr.termmanager.webmvc.rest;

import static java.util.Objects.isNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.eventlogging.spring.aop.EventLogger;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.ExportInfo;
import org.gs4tr.termmanager.model.dto.converter.ExportInfoConverter;
import org.gs4tr.termmanager.model.dto.converter.TermEntryExportSearchRequestConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.export.ExportDocumentFactory;
import org.gs4tr.termmanager.service.export.ExportFormatEnum;
import org.gs4tr.termmanager.service.impl.ExportAdapter;
import org.gs4tr.termmanager.service.logging.util.EventContextConstants;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.termmanager.service.solr.SolrServiceConfiguration;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.termmanager.webmvc.controllers.DownloadUtils;
import org.gs4tr.termmanager.webmvc.rest.utils.RestConstants;
import org.gs4tr.tm3.api.DateFilter;
import org.gs4tr.tm3.api.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//TODO: We should remove old web services in web service module where they belong.
//Also in web service module we can test them in a better way using Jetty.

@Controller
public class DetailedExportDocumentController extends BaseExportDocumentController {

    private static final String USER_AGENT_HEADER = "User-Agent"; //$NON-NLS-1$

    @Value("${index.batchSize:100}")

    private Integer _batchSize;

    private static Log _logger = LogFactory.getLog(DetailedExportDocumentController.class);

    @Autowired
    private SolrServiceConfiguration _solrConfig;

    @Value("${synonym.number:5}")
    private int _synonymNumber;

    @RequestMapping(value = "/rest/detailedExport", method = RequestMethod.GET)
    @LogEvent(action = TMGREventActionConstants.ACTION_DETAILED_EXPORT, actionCategory = TMGREventActionConstants.ACTION_TYPE_REST)
    public void doGet(@RequestParam(required = false) String afterDate,
	    @RequestParam(required = true) String projectTicket, @RequestParam(required = false) String sourceLocale,
	    @RequestParam(required = false) String targetLocale, @RequestParam(required = false) String exportFormat,
	    @RequestParam(required = true) String exportForbiddenTerms,
	    @RequestParam(required = false) String exportAllDescriptions,
	    @RequestParam(required = false) String blacklistTermsCount,
	    @RequestParam(value = "userId", required = true) String userId, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	EventLogger.addProperty(EventContextConstants.SOURCE_LANGUAGE, sourceLocale);
	EventLogger.addProperty(EventContextConstants.TARGET_LANGUAGE, targetLocale);

	Long projectId = decryptGenericId(projectTicket);
	EventLogger.addProperty(EventContextConstants.PROJECT_ID, projectId);

	TmProject project = getProjectService().load(projectId);
	EventLogger.addProperty(EventContextConstants.PROJECT_NAME, project.getProjectInfo().getName());
	EventLogger.addProperty(EventContextConstants.PROJECT_SHORT_CODE, project.getProjectInfo().getShortCode());

	Set<String> userLanguageIds = getProjectLanguageIds(projectId);

	// 24-May-2016: as per [Issue#TERII-4162]

	String sourceCode = makeLocaleCode(sourceLocale, userLanguageIds);
	String targetCode = makeLocaleCode(targetLocale, userLanguageIds);

	if (isNull(sourceCode) || isNull(targetCode)) {
	    throw new IllegalArgumentException(Messages.getString("DetailedExportDocumentController.11")); //$NON-NLS-1$
	}

	long dateModifiedFrom = StringUtils.isNotBlank(afterDate) ? Long.parseLong(afterDate) : 0;

	TermEntrySearchRequest searchRequest = createTermEntrySearchRequest(dateModifiedFrom, projectTicket, sourceCode,
		targetCode, exportAllDescriptions, exportForbiddenTerms, project);

	boolean fetchDeleted = dateModifiedFrom > 0;
	TmgrSearchFilter whiteListFilter = createSearchFilter(searchRequest, false, false, fetchDeleted);

	logExportEvent(sourceCode, targetCode, request, project, dateModifiedFrom, fetchDeleted);

	if (StringUtils.isBlank(exportFormat) || exportFormat.equalsIgnoreCase(RestConstants.CSV)
		|| exportFormat.equalsIgnoreCase(RestConstants.CSVSYNC)) {
	    detailedExportCVSSync(searchRequest, response, whiteListFilter);
	} else if (exportFormat.equalsIgnoreCase(RestConstants.JSON)) {
	    detailedExportJSON(searchRequest, response, whiteListFilter, blacklistTermsCount);
	} else {
	    throw new RuntimeException(Messages.getString("ExportDocumentResource.8")); //$NON-NLS-1$
	}

    }

    public Integer getBatchSize() {
	return _batchSize;
    }

    public void setBatchSize(Integer batchSize) {
	_batchSize = batchSize;
    }

    private TmgrSearchFilter createSearchFilter(TermEntrySearchRequest searchRequest, boolean forbiddenSearch,
	    boolean includeDateModified, boolean fetchDeleted) {
	TmgrSearchFilter filter = new TmgrSearchFilter();
	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(searchRequest.getProjectId());
	filter.setProjectIds(projectIds);

	if (searchRequest.getDateModifiedFrom() != null) {
	    DateFilter dateModified = new DateFilter();
	    dateModified.setStartDate(searchRequest.getDateModifiedFrom());

	    if (!forbiddenSearch) {
		// termEntry dateModified
		filter.setParentDateModifiedFilter(dateModified);
		filter.setSyncSearch(true);
	    } else {
		filter.setDateModifiedFilter(dateModified);
	    }
	}

	filter.setSourceLanguage(searchRequest.getSourceLocale());

	filter.setTargetLanguages(searchRequest.getTargetLocales());

	filter.setExcludeDisabled(forbiddenSearch);

	String targetLocale = searchRequest.getTargetLocales().get(0);

	filter.addLanguageResultField(true, fetchDeleted, getSynonymNumber(), searchRequest.getSourceLocale(),
		targetLocale);

	filter.setPageable(new TmgrPageRequest());

	if (forbiddenSearch) {
	    filter.setSourceLanguage(targetLocale);
	    filter.setTargetLanguages(null);
	    if (!includeDateModified) {
		filter.setDateModifiedFilter(null);
	    }
	    List<String> statuses = new ArrayList<String>();
	    statuses.add(ItemStatusTypeHolder.BLACKLISTED.getName());
	    filter.setStatuses(statuses);

	    filter.setResultFields(null);
	    filter.setFetchDeleted(false);
	    filter.addLanguageResultField(true, getSynonymNumber(), targetLocale);
	}

	TmgrPageRequest page = new TmgrPageRequest(0, getBatchSize(), null);
	filter.setPageable(page);

	return filter;
    }

    private TermEntrySearchRequest createTermEntrySearchRequest(long dateModidiedFrom, String projectTicket,
	    String sourceCode, String targetCode, String exportAllDescriptions, String exportForbiddenTerms,
	    TmProject project) {

	org.gs4tr.termmanager.model.dto.TermEntrySearchRequest dtoTermEntrySearchRequest = new org.gs4tr.termmanager.model.dto.TermEntrySearchRequest();

	dtoTermEntrySearchRequest.setProjectTicket(new Ticket(projectTicket));

	dtoTermEntrySearchRequest.setDateModifiedFrom(new Date(dateModidiedFrom));

	if (StringUtils.isNotBlank(sourceCode)) {
	    dtoTermEntrySearchRequest.setSourceLocale(sourceCode);
	}

	if (StringUtils.isNotBlank(targetCode)) {
	    dtoTermEntrySearchRequest.setTargetLocales(new String[] { targetCode });
	}

	TermEntrySearchRequest termEntrySearchRequest = TermEntryExportSearchRequestConverter
		.fromDtoToInternal(dtoTermEntrySearchRequest);

	if (StringUtils.isNotBlank(exportAllDescriptions)) {
	    termEntrySearchRequest.setExportAllDescriptions(Boolean.valueOf(exportAllDescriptions));
	}

	if (StringUtils.isNotBlank(exportForbiddenTerms)) {
	    termEntrySearchRequest.setForbidden(Boolean.valueOf(exportForbiddenTerms));
	}

	termEntrySearchRequest
		.setSharePendingTerms(ServiceUtils.isSharePendingTermsOnProject(project.getSharePendingTerms()));

	return termEntrySearchRequest;
    }

    private Long decryptGenericId(final String projectTicket) {
	Validate.notEmpty(projectTicket, Messages.getString("ExportDocumentResource.5")); //$NON-NLS-1$
	return TicketConverter.fromDtoToInternal(projectTicket, Long.class);
    }

    private void detailedExportCVSSync(org.gs4tr.termmanager.model.search.TermEntrySearchRequest termEntrySearchRequest,
	    HttpServletResponse response, TmgrSearchFilter filter) throws Exception {
	String fileName = fileNameResolver(ExportFormatEnum.CSVSYNC);

	response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
	response.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
	response.setHeader(DownloadUtils.FILENAME_HEADER_NAME,
		String.format(DownloadUtils.FILENAME_FORMAT_VALUE, fileName));

	TermEntryService termEntryService = getTermEntryService();

	ExportDocumentFactory factory = ExportDocumentFactory.getInstance(ExportFormatEnum.CSVSYNC);

	try {
	    factory.open(response.getOutputStream(), termEntrySearchRequest, null);
	    List<TermEntry> termentries = termEntryService.searchTermEntries(filter).getResults();

	    while (CollectionUtils.isNotEmpty(termentries)) {
		doDetailedExportDocument(termentries, termEntrySearchRequest, factory);
		filter.setPageable(filter.getPageable().next());
		termentries = termEntryService.searchTermEntries(filter).getResults();
		response.getOutputStream().flush();
	    }

	    factory.close();
	} catch (IOException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    private void detailedExportJSON(org.gs4tr.termmanager.model.search.TermEntrySearchRequest searchRequest,
	    HttpServletResponse response, TmgrSearchFilter whiteListFilter, String blacklistTermsCount)
	    throws Exception {

	ExportDocumentFactory exportDocumentFactory = ExportDocumentFactory.getInstance(ExportFormatEnum.JSON);
	String fileName = fileNameResolver(ExportFormatEnum.JSON);

	response.setContentType(MediaType.APPLICATION_JSON_VALUE);
	response.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
	response.setHeader(DownloadUtils.FILENAME_HEADER_NAME,
		String.format(DownloadUtils.FILENAME_FORMAT_VALUE, fileName));

	try {
	    addSyncAttributes(searchRequest);

	    TermEntryService termEntryService = getTermEntryService();

	    // blackList term count is needed before exportDocumentFactory.open
	    TmgrSearchFilter blackListFilter = createSearchFilter(searchRequest, true, false, false);
	    long totalBlackListSize = termEntryService.getNumberOfTerms(blackListFilter,
		    getSolrConfig().getRegularCollection());
	    searchRequest.setForbiddenTermCount(totalBlackListSize);

	    exportDocumentFactory.open(response.getOutputStream(), searchRequest, null);

	    List<TermEntry> whiteListPage = termEntryService.searchTermEntries(whiteListFilter).getResults();

	    String targetLanguageId = searchRequest.getTargetLocales().get(0);

	    while (CollectionUtils.isNotEmpty(whiteListPage)) {
		doDetailedExportDocument(whiteListPage, searchRequest, exportDocumentFactory);
		whiteListFilter.setPageable(whiteListFilter.getPageable().next());
		whiteListPage = termEntryService.searchTermEntries(whiteListFilter).getResults();
		response.getOutputStream().flush();
	    }

	    boolean requestBlacklist = StringUtils.isNotBlank(blacklistTermsCount);
	    if (requestBlacklist) {
		int requestBlackListCount = Long.valueOf(blacklistTermsCount).intValue();
		if (totalBlackListSize == requestBlackListCount) {
		    TmgrSearchFilter modifiedBlackListFilter = createSearchFilter(searchRequest, true, true, false);
		    Page<TermEntry> modifiedBlackListPage = termEntryService.searchTermEntries(modifiedBlackListFilter);
		    requestBlacklist = modifiedBlackListPage.getTotalResults() > 0;
		}

		if (requestBlacklist) {
		    List<TermEntry> blackListPage = termEntryService.searchTermEntries(blackListFilter).getResults();

		    while (CollectionUtils.isNotEmpty(blackListPage)) {
			List<Term> blacklistedTerms = findBlacklistedTerm(blackListPage, targetLanguageId);
			if (CollectionUtils.isNotEmpty(blacklistedTerms)) {
			    termEntryService.exportForbiddenTerms(blacklistedTerms, exportDocumentFactory);
			}

			blackListFilter.setPageable(blackListFilter.getPageable().next());
			blackListPage = termEntryService.searchTermEntries(blackListFilter).getResults();
			response.getOutputStream().flush();
		    }
		}
	    }

	    exportDocumentFactory.close();
	} catch (IOException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    private ExportInfo doDetailedExportDocument(List<TermEntry> termEntries,
	    TermEntrySearchRequest termEntryExportSearchRequest, ExportDocumentFactory exportDocumentFactory) {
	ExportInfo exportInfoDto = ExportInfoConverter.fromInternalToDto(getTermEntryService().exportDocumentWS(
		termEntries, termEntryExportSearchRequest, new ExportAdapter(), exportDocumentFactory));
	return exportInfoDto;
    }

    private String fileNameResolver(ExportFormatEnum exportFormat) {
	Long time = Calendar.getInstance().getTimeInMillis();

	if (exportFormat.name().equalsIgnoreCase(RestConstants.JSON)) {
	    return TM_EXPORTED.concat(time.toString()).concat(RestConstants.JSON_EXTENSION);
	} else {
	    return TM_EXPORTED.concat(time.toString()).concat(RestConstants.CSV_EXTENSION);
	}
    }

    private List<Term> findBlacklistedTerm(List<TermEntry> termentries, String languageId) {
	List<Term> terms = new ArrayList<Term>();

	if (CollectionUtils.isEmpty(termentries)) {
	    return terms;
	}

	for (TermEntry termEntry : termentries) {
	    for (Term term : termEntry.ggetTerms()) {
		if (term.getLanguageId().equals(languageId)
			&& term.getStatus().equals(ItemStatusTypeHolder.BLACKLISTED.getName())) {
		    terms.add(term);
		}
	    }
	}
	return terms;
    }

    private Set<String> getProjectLanguageIds(final Long projectId) {
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();
	Set<String> userLanguageIds = userProfile.getProjectUserLanguages().get(projectId);
	Validate.notEmpty(userLanguageIds, String.format(Messages.getString("DetailedExportDocumentController.5"), // $NON-NLS-1$
		userProfile.getUserName()));
	return userLanguageIds;
    }

    private SolrServiceConfiguration getSolrConfig() {
	return _solrConfig;
    }

    private int getSynonymNumber() {
	return _synonymNumber;
    }

    private void logExportEvent(String sourceCode, String targetCode, HttpServletRequest request, TmProject project,
	    long dateModifiedFrom, boolean fetchHistory) {
	if (_logger.isDebugEnabled()) {

	    String projectName = project.getProjectInfo().getName();

	    if (fetchHistory) {
		Date date = new Date(dateModifiedFrom);
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z"); //$NON-NLS-1$
		String dateText = dateFormat.format(date);
		LogHelper.debug(_logger,
			"Performing [detailedExport] action WITH HISTORY from remote address: [%s], remote host: [%s], user agent: [%s], date modified from: [%s]", //$NON-NLS-1$
			request.getRemoteAddr(), request.getRemoteHost(), request.getHeader(USER_AGENT_HEADER),
			dateText);
	    } else {
		LogHelper.debug(_logger,
			"Performing [detailedExport] action WITHOUT HISTORY from remote address: [%s], remote host: [%s], user agent: [%s]", //$NON-NLS-1$
			request.getRemoteAddr(), request.getRemoteHost(), request.getHeader(USER_AGENT_HEADER));

	    }

	    LogHelper.debug(_logger, String.format(Messages.getString("DetailedExportDocumentController.7"), //$NON-NLS-1$
		    TmUserProfile.getCurrentUserName(), projectName, sourceCode, targetCode));
	}
    }

    private String makeLocaleCode(String locale, Set<String> userLanguageIds) {
	String localeCode = null;
	if (StringUtils.isNotEmpty(locale)) {
	    localeCode = Locale.makeLocale(locale).getCode();

	    if (!userLanguageIds.contains(localeCode)) {
		throw new UserException(String.format(Messages.getString("DetailedExportDocumentController.14"), //$NON-NLS-1$
			localeCode));
	    }
	}
	return localeCode;
    }

}