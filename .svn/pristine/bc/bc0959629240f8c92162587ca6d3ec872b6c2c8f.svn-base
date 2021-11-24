package org.gs4tr.termmanager.webmvc.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.eventlogging.spring.aop.EventLogger;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.ExportInfo;
import org.gs4tr.termmanager.model.dto.converter.ExportInfoConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.persistence.solr.query.IPageable;
import org.gs4tr.termmanager.persistence.solr.query.TmgrPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.export.ExportDocumentFactory;
import org.gs4tr.termmanager.service.export.ExportFormatEnum;
import org.gs4tr.termmanager.service.impl.ExportAdapter;
import org.gs4tr.termmanager.service.logging.util.EventContextConstants;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.termmanager.webmvc.controllers.DownloadUtils;
import org.gs4tr.termmanager.webmvc.rest.utils.RestConstants;
import org.gs4tr.tm3.api.DateFilter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ExportDocumentController extends BaseExportDocumentController {

    private static final String DO_GET_METHOD_NAME = "doGet";
    private static Log _logger = LogFactory.getLog(ExportDocumentController.class);

    @RequestMapping(value = "/rest/export", method = RequestMethod.GET)
    @LogEvent(action = TMGREventActionConstants.ACTION_EXPORT, actionCategory = TMGREventActionConstants.ACTION_TYPE_REST)
    public void doGet(@RequestParam(required = false) String afterDate, @RequestParam String projectTicket,
	    @RequestParam(required = false) String sourceLocale, @RequestParam(required = false) String targetLocale,
	    @RequestParam String exportFormat, @RequestParam(required = false) String exportForbiddenTerms,
	    @RequestParam String generateStatistics, @RequestParam(required = false) String descriptionType,
	    @RequestParam(value = "userId") String userId, HttpServletResponse response) throws Exception {

	EventLogger.addProperty(EventContextConstants.SOURCE_LANGUAGE, sourceLocale);
	EventLogger.addProperty(EventContextConstants.TARGET_LANGUAGE, targetLocale);

	if (validInputParameters(projectTicket, sourceLocale, targetLocale, exportFormat)) {

	    // projectTicket = URLDecoder.decode(projectTicket,
	    // UTF_8_ENCODING);
	    Long projectId = TicketConverter.fromDtoToInternal(projectTicket, Long.class);
	    EventLogger.addProperty(EventContextConstants.PROJECT_ID, projectId);

	    TmProject project = getProjectService().load(projectId);
	    EventLogger.addProperty(EventContextConstants.PROJECT_NAME, project.getProjectInfo().getName());
	    EventLogger.addProperty(EventContextConstants.PROJECT_SHORT_CODE, project.getProjectInfo().getShortCode());

	    String projectName = project.getProjectInfo().getName();

	    LogHelper.debug(_logger, String.format(Messages.getString("ExportDocumentController.0"), //$NON-NLS-1$
		    TmUserProfile.getCurrentUserName(), projectName, sourceLocale, targetLocale, exportFormat));

	    TermEntrySearchRequest searchRequest = new TermEntrySearchRequest();

	    searchRequest.setProjectId(projectId);

	    TmgrSearchFilter filter = new TmgrSearchFilter();

	    List<Long> projectIds = new ArrayList<Long>();
	    projectIds.add(projectId);

	    filter.setProjectIds(projectIds);

	    if (StringUtils.isNotBlank(sourceLocale)) {
		String sourceLanguageId = Locale.makeLocale(sourceLocale).getCode();
		searchRequest.setSourceLocale(sourceLanguageId);
		searchRequest.addLanguageToExport(sourceLanguageId);
		filter.setSourceLanguage(sourceLocale);
	    }
	    if (StringUtils.isNotBlank(targetLocale)) {
		String targetLanguageId = Locale.makeLocale(targetLocale).getCode();
		List<String> targetLanguages = new ArrayList<String>();
		targetLanguages.add(targetLanguageId);

		searchRequest.setTargetLocales(targetLanguages);
		searchRequest.addLanguageToExport(targetLanguageId);

		filter.setTargetLanguages(targetLanguages);
	    }

	    long dateModidiedStart = StringUtils.isNotBlank(afterDate) ? Long.parseLong(afterDate) : 0;
	    searchRequest.setDateModifiedFrom(new Date(dateModidiedStart));
	    DateFilter dateModifiedFilter = new DateFilter();
	    dateModifiedFilter.setStartDate(new Date(dateModidiedStart));
	    filter.setDateModifiedFilter(dateModifiedFilter);

	    if (StringUtils.isNotBlank(exportForbiddenTerms)) {
		searchRequest.setForbidden(Boolean.valueOf(exportForbiddenTerms));

		if (Boolean.parseBoolean(exportForbiddenTerms)) {
		    searchRequest.setSharePendingTerms(project.getSharePendingTerms());
		}
	    }

	    if (StringUtils.isBlank(sourceLocale) && StringUtils.isBlank(targetLocale)) {
		Set<String> languageIds = TmUserProfile.getCurrentUserProfile().getProjectUserLanguages()
			.get(projectId);

		List<String> languagesToExport = new ArrayList<String>();
		languagesToExport.addAll(languageIds);

		searchRequest.setLanguagesToExport(languagesToExport);
		filter.setTargetLanguages(languagesToExport);
	    }

	    addSyncAttributes(searchRequest);

	    // CSVSYNC
	    if (exportFormat.equals(RestConstants.CSVSYNC)) {
		if (StringUtils.isNotBlank(descriptionType)) {
		    searchRequest.setDescriptionType(descriptionType);
		}
		exportOldCVSSync(filter, searchRequest, userId, response);
	    } else {
		ExportFormatEnum exportTypeEnum = resolveXmlTransformation(exportFormat);

		ExportDocumentFactory exportDocumentFactory = ExportDocumentFactory.getInstance(exportTypeEnum);
		String fileName = fileNameResolver(exportFormat);

		response.setContentType(getResponseContextTypeFromExportFormat(exportTypeEnum));
		response.setHeader(DownloadUtils.FILENAME_HEADER_NAME,
			String.format(DownloadUtils.FILENAME_FORMAT_VALUE, fileName));
		response.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));

		exportDocumentFactory.open(response.getOutputStream(), searchRequest, null);

		TmgrPageRequest pageRequest = new TmgrPageRequest();
		filter.setPageable(pageRequest);

		List<TermEntry> termentries = getTermEntryService().searchTermEntries(filter).getResults();

		while (CollectionUtils.isNotEmpty(termentries)) {
		    exportDocument(termentries, searchRequest, exportDocumentFactory, userId);
		    IPageable pageable = filter.getPageable();
		    pageable = pageable.next();
		    filter.setPageable(pageable);

		    termentries = getTermEntryService().searchTermEntries(filter).getResults();
		    try {
			response.getOutputStream().flush();
		    } catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		    }
		}
		exportDocumentFactory.close();
	    }
	}
    }

    public ExportInfo exportDocument(List<TermEntry> termentries, TermEntrySearchRequest termEntryExportSearchRequest,
	    ExportDocumentFactory exportDocumentFactory, String userId) {
	ExportInfo exportInfo = ExportInfoConverter.fromInternalToDto(getTermEntryService().exportDocumentWS(
		termentries, termEntryExportSearchRequest, new ExportAdapter(), exportDocumentFactory));
	return exportInfo;
    }

    private void exportOldCVSSync(TmgrSearchFilter filter,
	    org.gs4tr.termmanager.model.search.TermEntrySearchRequest termEntrySearchRequest, String userId,
	    HttpServletResponse response) throws Exception {

	File tempFile = new File(fileNameResolver("csv")); //$NON-NLS-1$

	ExportDocumentFactory factory = ExportDocumentFactory.getInstance(ExportFormatEnum.CSVSYNC);
	try {
	    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

	    OutputStream outputStream = new FileOutputStream(tempFile);
	    factory.open(outputStream, termEntrySearchRequest, null);

	    TmgrPageRequest pageRequest = new TmgrPageRequest();
	    filter.setPageable(pageRequest);

	    List<TermEntry> termentries = getTermEntryService().searchTermEntries(filter).getResults();
	    while (CollectionUtils.isNotEmpty(termentries)) {

		exportDocument(termentries, termEntrySearchRequest, factory, userId);
		IPageable pageable = filter.getPageable();
		pageable = pageable.next();
		filter.setPageable(pageable);

		termentries = getTermEntryService().searchTermEntries(filter).getResults();
		outputStream.flush();
	    }

	    FileInputStream tempFileInputStream = new FileInputStream(tempFile);
	    IOUtils.copy(tempFileInputStream, response.getOutputStream());

	} catch (IOException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    private String fileNameResolver(String exportFormat) {
	String fileName = TM_EXPORTED + Calendar.getInstance().getTimeInMillis();

	if (exportFormat.equalsIgnoreCase(RestConstants.CSV)) {
	    fileName += RestConstants.CSV_EXTENSION;
	} else if (exportFormat.equalsIgnoreCase(RestConstants.TBX)) {
	    fileName += RestConstants.TBX_EXTENSION;
	} else if (exportFormat.equalsIgnoreCase(RestConstants.TAB)) {
	    fileName += RestConstants.TAB_EXTENSION;
	} else {
	    fileName += RestConstants.CSV_EXTENSION;
	}
	return fileName;
    }

    private String getResponseContextTypeFromExportFormat(ExportFormatEnum exportFormat) {

	if (ExportFormatEnum.JSON == exportFormat) {
	    return MediaType.APPLICATION_JSON_VALUE;

	} else if (ExportFormatEnum.TBX == exportFormat) {
	    return MediaType.APPLICATION_XML_VALUE;

	} else if (ExportFormatEnum.CSVEXPORT == exportFormat || ExportFormatEnum.CSVSYNC == exportFormat
		|| ExportFormatEnum.TABSYNC == exportFormat) {
	    return MediaType.TEXT_PLAIN_VALUE;

	} else {
	    return null;
	}
    }

    private ExportFormatEnum resolveXmlTransformation(String formatName) {

	if (formatName.equalsIgnoreCase(RestConstants.CSV)) {
	    return ExportFormatEnum.CSVEXPORT;
	} else if (formatName.equalsIgnoreCase(RestConstants.CSVSYNC)) {
	    return ExportFormatEnum.CSVSYNC;
	} else if (formatName.equalsIgnoreCase(RestConstants.TBX)) {
	    return ExportFormatEnum.TBX;
	} else if (formatName.equalsIgnoreCase(RestConstants.JSON)) {
	    return ExportFormatEnum.JSON;
	} else if (formatName.equalsIgnoreCase(RestConstants.TAB)) {
	    return ExportFormatEnum.TABSYNC;
	}

	throw new RuntimeException(Messages.getString("ExportDocumentResource.2")); //$NON-NLS-1$
    }

    private boolean validInputParameters(String projectTicket, String sourceLocale, String targetLocale,
	    String exportFormat) {
	boolean result = true;

	if (StringUtils.isBlank(projectTicket)) {
	    result = false;
	}
	if (StringUtils.isBlank(sourceLocale) != StringUtils.isBlank(targetLocale)) {
	    result = false;
	}
	if (StringUtils.isBlank(exportFormat)) {
	    result = false;

	} else if (!exportFormat.equalsIgnoreCase(RestConstants.CSV)
		&& !exportFormat.equalsIgnoreCase(RestConstants.CSVSYNC)
		&& !exportFormat.equalsIgnoreCase(RestConstants.TBX)
		&& !exportFormat.equalsIgnoreCase(RestConstants.JSON)
		&& !exportFormat.equalsIgnoreCase(RestConstants.TAB)) {
	    result = false;
	}

	return result;
    }
}