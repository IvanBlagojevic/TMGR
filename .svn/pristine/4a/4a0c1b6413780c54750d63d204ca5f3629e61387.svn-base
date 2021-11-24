package org.gs4tr.termmanager.webservice.controllers;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.Validate.notEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.dto.Description;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.DescriptionFilter;
import org.gs4tr.termmanager.persistence.solr.query.Sort;
import org.gs4tr.termmanager.persistence.solr.query.Sort.Direction;
import org.gs4tr.termmanager.persistence.solr.query.TextFilter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.termmanager.solr.plugin.utils.SolrDocHelper;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;
import org.gs4tr.termmanager.webservice.model.request.ConcordanceSearchCommand;
import org.gs4tr.termmanager.webservice.model.request.WsDateFilter;
import org.gs4tr.termmanager.webservice.model.request.WsPageInfo;
import org.gs4tr.tm3.api.ConcordanceMatchLocation;
import org.gs4tr.tm3.api.DateFilter;
import org.gs4tr.tm3.api.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class AbstractTermSearchController {

    private static final String DEFAULT_SORT_PROPERTY_SUFFIX = SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW;

    private static final int MAX_ITEMS_PER_PAGE = 200;

    private static final Map<String, String> SORT_PROPERTY_MAP = new HashMap<String, String>();

    private static final List<ConcordanceMatchLocation> SOURCE_OR_SOURCE_AND_TARGET;

    private static final List<ConcordanceMatchLocation> TARGET_OR_SOURCE_AND_TARGET;

    static {
	SOURCE_OR_SOURCE_AND_TARGET = Arrays.asList(ConcordanceMatchLocation.SOURCE,
		ConcordanceMatchLocation.SOURCE_AND_TARGET);
	TARGET_OR_SOURCE_AND_TARGET = Arrays.asList(ConcordanceMatchLocation.TARGET,
		ConcordanceMatchLocation.SOURCE_AND_TARGET);

	SORT_PROPERTY_MAP.put("creationDate", SolrParentDocFields.DATE_CREATED_INDEX_STORE); //$NON-NLS-1$
	SORT_PROPERTY_MAP.put("modificationDate", SolrParentDocFields.DATE_MODIFIED_INDEX_STORE); //$NON-NLS-1$
    }

    @Autowired
    private ProjectService _projectService;

    @Value("${synonym.number:5}")
    private int _synonymNumber;

    @Autowired
    private TermEntryService _termEntryService;

    private void addLanguagesToFilter(ConcordanceSearchCommand command, TmgrSearchFilter filter) {
	ConcordanceMatchLocation location = command.getMatchLocation();

	if (isSourceOrSourceAndTarget(location)) {
	    filter.setSourceLanguage(command.getSourceLocale());
	}
	if (isTargetOrSourceAndTarget(location)) {
	    filter.setTargetLanguages(command.getTargetLocales());
	}
    }

    private List<String> convertCodesToLocales(List<String> languages) {
	if (CollectionUtils.isEmpty(languages)) {
	    return Collections.emptyList();
	}
	return languages.stream().map(Locale::makeLocale).map(language -> language.getCode()).collect(toList());
    }

    private DateFilter createDateFilter(WsDateFilter wsFilter) {
	DateFilter dateFilter = new DateFilter();
	Long dateStart = wsFilter.getDateStart();
	if (dateStart != null) {
	    dateFilter.setStartDate(new Date(dateStart));
	    dateFilter.setStartDateInclusive(true);
	}
	Long dateEnd = wsFilter.getDateEnd();
	if (dateEnd != null) {
	    dateFilter.setEndDate(new Date(dateEnd));
	    dateFilter.setEndDateInclusive(true);
	}
	return dateFilter;
    }

    private int getSynonymNumber() {
	return _synonymNumber;
    }

    private boolean isSourceOrSourceAndTarget(ConcordanceMatchLocation location) {
	return SOURCE_OR_SOURCE_AND_TARGET.contains(location);
    }

    private boolean isTargetOrSourceAndTarget(ConcordanceMatchLocation location) {
	return TARGET_OR_SOURCE_AND_TARGET.contains(location);
    }

    private String resolveSortProperty(ConcordanceSearchCommand command) {
	String sortProperty = null;

	WsPageInfo pageInfo = command.getPageInfo();
	if (pageInfo == null) {
	    return SolrDocHelper.createDynamicFieldName(command.getSourceLocale(), DEFAULT_SORT_PROPERTY_SUFFIX);
	}

	String sortCommand = pageInfo.getSortProperty();
	if (StringUtils.isNotEmpty(sortCommand)) {
	    sortProperty = SORT_PROPERTY_MAP.get(sortCommand);
	}

	if (sortProperty == null) {
	    sortProperty = SolrDocHelper.createDynamicFieldName(command.getSourceLocale(),
		    DEFAULT_SORT_PROPERTY_SUFFIX);
	}

	return sortProperty;
    }

    protected TmgrSearchFilter createFilterFromCommand(ConcordanceSearchCommand command, Long projectId) {

	final TmgrSearchFilter filter = new TmgrSearchFilter();

	List<String> languageIds = new ArrayList<String>();

	languageIds.add(command.getSourceLocale());
	languageIds.addAll(command.getTargetLocales());

	filter.addLanguageResultField(true, getSynonymNumber(), languageIds.toArray(new String[languageIds.size()]));

	addLanguagesToFilter(command, filter);

	WsDateFilter creationDateFilter = command.getCreationDateFilter();
	if (creationDateFilter != null) {
	    filter.setDateCreatedFilter(createDateFilter(creationDateFilter));
	}

	WsDateFilter modificationDateFilter = command.getModificationDateFilter();
	if (modificationDateFilter != null) {
	    filter.setDateModifiedFilter(createDateFilter(modificationDateFilter));
	}

	List<String> statuses = new ArrayList<>();

	String status = command.getStatus();

	String userCreated = command.getCreationUser();
	String userModified = command.getModificationUser();

	if (StringUtils.isNotEmpty(userCreated)) {
	    filter.setUsersCreated(Collections.singletonList(userCreated));
	}

	if (StringUtils.isNotEmpty(userModified)) {
	    filter.setUsersModified(Collections.singletonList(userModified));
	}

	if (StringUtils.isNotEmpty(status)) {
	    try {
		status = ServiceUtils.resolveStatusName(status);
	    } catch (Exception e) {
		throw new IllegalArgumentException(e.getMessage(), e);
	    }
	    statuses.add(status);
	} else {
	    statuses.add(ItemStatusTypeHolder.PROCESSED.getName());
	    statuses.add(ItemStatusTypeHolder.ON_HOLD.getName());
	    statuses.add(ItemStatusTypeHolder.WAITING.getName());
	}

	if (command.isSearchForbidden()) {
	    statuses.add(ItemStatusTypeHolder.BLACKLISTED.getName());
	}

	filter.setStatuses(statuses);

	filter.addProjectId(projectId);
	filter.setProjectIds(Arrays.asList(projectId));

	WsPageInfo pageInfo = command.getPageInfo();

	int size = pageInfo != null ? pageInfo.getMaxNumFound() : MAX_ITEMS_PER_PAGE;
	if (size > MAX_ITEMS_PER_PAGE) {
	    size = MAX_ITEMS_PER_PAGE;
	}

	int pageIndex = pageInfo != null ? pageInfo.getPageIndex() : 0;

	Sort sort = null;

	String termText = command.getTerm();
	if (StringUtils.isNotBlank(termText)) {
	    TextFilter textFilter = new TextFilter(termText, command.isMatchWholeWords(), command.isCaseSensitive());
	    textFilter.setAllTextSearch(command.isIncludeAttributesSearch());
	    filter.setTextFilter(textFilter);
	} else {
	    String sortProperty = resolveSortProperty(command);
	    Direction direction = pageInfo != null ? Direction.valueOf(pageInfo.getSortDirection()) : Direction.ASC;
	    sort = new Sort(direction, sortProperty);
	}

	Set<Description> descriptions = command.getDescriptions();

	if (CollectionUtils.isNotEmpty(descriptions)) {

	    for (Description description : descriptions) {
		DescriptionFilter descriptionFilter = new DescriptionFilter(description.getType(),
			description.getValue(), description.isExcluded());
		filter.addDescriptionFilter(descriptionFilter);
	    }

	}

	filter.setPageable(new TmgrPageRequest(pageIndex, size, sort));

	return filter;
    }

    protected ProjectService getProjectService() {
	return _projectService;
    }

    protected TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    protected Page<TermEntry> search(ConcordanceSearchCommand command, Long projectId) {
	TmgrSearchFilter filter = createFilterFromCommand(command, projectId);

	return getTermEntryService().searchTermEntries(filter);
    }

    protected Long validateAndGetProjectId(ConcordanceSearchCommand command) throws Exception {
	String projectTicket = command.getProjectTicket();
	notEmpty(projectTicket, Messages.getString("ProjectTicketError"));

	return TicketConverter.fromDtoToInternal(projectTicket, Long.class);
    }

    protected void validateLocales(ConcordanceSearchCommand command) throws Exception {
	// Project ticket and source language locale are mandatory.
	String sourceLocale = command.getSourceLocale();

	List<String> targetLocales = command.getTargetLocales();

	// Checks source and target locale codes.
	Validate.isTrue(Locale.checkLocale(sourceLocale),
		String.format(Messages.getString("AbstractTermSearchController.2"), sourceLocale)); //$NON-NLS-1$

	if (CollectionUtils.isNotEmpty(targetLocales)) {
	    Validate.isTrue(targetLocales.stream().allMatch(Locale::checkLocale),
		    String.format(Messages.getString("AbstractTermSearchController.3"), targetLocales)); //$NON-NLS-1$
	}
	// Use appropriate code/s from the list of all available locales.
	command.setSourceLocale(Locale.makeLocale(sourceLocale).getCode());
	command.setTargetLocales(convertCodesToLocales(targetLocales));
    }
}
