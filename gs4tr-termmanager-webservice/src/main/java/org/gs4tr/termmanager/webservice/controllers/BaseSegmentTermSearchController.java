package org.gs4tr.termmanager.webservice.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.eventlogging.spring.aop.EventLogger;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TextFilter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.logging.util.EventContextConstants;
import org.gs4tr.termmanager.webservice.model.request.TermSearchCommand;
import org.gs4tr.tm3.api.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by emisia on 5/17/17.
 */
public class BaseSegmentTermSearchController {

    protected static final Log LOGGER = LogFactory.getLog(BaseSegmentTermSearchController.class);

    protected static final int MAXIMUM_PERMITTED_SEGMENTS = 1000;

    @Autowired
    private ProjectService _projectService;

    @Value("${synonym.number:5}")
    private int _synonymNumber;

    @Autowired
    private TermEntryService _termEntryService;

    private int getSynonymNumber() {
	return _synonymNumber;
    }

    protected TmgrSearchFilter createSearchFilter(Long projectId, TermSearchCommand searchCommand, String segment) {

	List<String> languageIds = new ArrayList<>();
	languageIds.add(searchCommand.getSourceLanguage());

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setSourceLanguage(searchCommand.getSourceLanguage());

	List<String> targetLocales = new ArrayList<>();
	for (String language : searchCommand.getTargetLanguages()) {
	    Locale targetLocale = Locale.makeLocale(language);
	    targetLocales.add(targetLocale.getCode());
	}

	languageIds.addAll(targetLocales);

	List<String> statuses = new ArrayList<>();

	statuses.add(ItemStatusTypeHolder.WAITING.getName());
	statuses.add(ItemStatusTypeHolder.PROCESSED.getName());
	statuses.add(ItemStatusTypeHolder.ON_HOLD.getName());
	if (searchCommand.isSearchForbidden()) {
	    statuses.add(ItemStatusTypeHolder.BLACKLISTED.getName());
	}

	filter.setStatuses(statuses);

	filter.setPageable(new TmgrPageRequest(0, searchCommand.getMaxNumFound(), null));

	TextFilter textFilter = new TextFilter(segment, !searchCommand.isFuzzy());
	textFilter.setFuzzyMatch(searchCommand.isFuzzy());
	textFilter.setSegmentSearch(true);
	filter.setTextFilter(textFilter);

	List<Long> projectIds = new ArrayList<>();
	projectIds.add(projectId);

	filter.setProjectIds(projectIds);
	filter.addLanguageResultField(true, getSynonymNumber(), languageIds.toArray(new String[languageIds.size()]));

	return filter;
    }

    protected ProjectService getProjectService() {
	return _projectService;
    }

    protected TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    protected void putValuesToEventContext(Long projectId, String sourceLanguage, List<String> targetLanguages,
	    TmProject project) {
	EventLogger.addProperty(EventContextConstants.PROJECT_ID, projectId);
	EventLogger.addProperty(EventContextConstants.SOURCE_LANGUAGE, sourceLanguage);
	EventLogger.addProperty(EventContextConstants.TARGET_LANGUAGES, targetLanguages);
	EventLogger.addProperty(EventContextConstants.PROJECT_NAME, project.getProjectInfo().getName());
	EventLogger.addProperty(EventContextConstants.PROJECT_SHORT_CODE, project.getProjectInfo().getShortCode());
    }

    protected Page<TermEntry> segmentSearch(Long projectId, TermSearchCommand searchCommand, String segment) {
	TmgrSearchFilter filter = createSearchFilter(projectId, searchCommand, segment);
	return getTermEntryService().segmentTMSearch(filter);
    }

    protected void validateRequestParameters(String projectTicket, String sourceLanguage,
	    List<String> targetLanguages) {
	Validate.notEmpty(projectTicket, Messages.getString("ProjectTicketError"));
	Validate.notEmpty(sourceLanguage, Messages.getString("SourceLanguageError"));
	Validate.notEmpty(targetLanguages, Messages.getString("TargetLanguageError"));
    }
}
