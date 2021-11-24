package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.SortDirection;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation3.solr.model.concordance.ConcordanceType;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.model.search.TypeSearchEnum;
import org.gs4tr.termmanager.persistence.solr.query.Sort.Direction;
import org.gs4tr.termmanager.persistence.solr.query.TextFilter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.model.command.ExportCommand;
import org.gs4tr.termmanager.service.model.command.ExportSearchFilter;
import org.gs4tr.termmanager.service.model.command.ExportSearchFilter.TermNameAndSearchType;
import org.gs4tr.termmanager.service.model.command.dto.DtoExportCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.tm3.api.DateFilter;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractExportDocumentTaskHandler extends AbstractManualTaskHandler {

    @Value("${index.batchSize:100}")
    private Integer _batchSize;

    public TmgrSearchFilter createFilterFromRequest(TermEntrySearchRequest request, PagedListInfo pagedListInfo) {
	TmgrSearchFilter filter = new TmgrSearchFilter();

	filter.addProjectId(request.getProjectId());

	DateFilter dateCreatedFilter = new DateFilter();
	Date dateCreatedFrom = request.getDateCreatedFrom();

	if (dateCreatedFrom != null) {
	    dateCreatedFilter.setStartDate(dateCreatedFrom);
	}

	Date dateCreatedTo = request.getDateCreatedTo();
	if (dateCreatedTo != null) {
	    dateCreatedFilter.setEndDate(dateCreatedTo);
	}

	filter.setDateCreatedFilter(dateCreatedFilter);

	DateFilter dateModifiedFilter = new DateFilter();
	Date dateModifiedFrom = request.getDateModifiedFrom();
	if (dateModifiedFrom != null) {
	    dateModifiedFilter.setStartDate(dateModifiedFrom);
	}

	Date dateModifiedTo = request.getDateModifiedTo();
	if (dateModifiedTo != null) {
	    dateModifiedFilter.setEndDate(dateModifiedTo);
	}

	filter.setDateModifiedFilter(dateModifiedFilter);

	if (request.isSourceAndTargetSearch() || request.isMissingTranslation()) {
	    filter.setSourceLanguage(request.getSourceLocale());
	}

	List<String> targetLanguages = new ArrayList<String>();
	if (CollectionUtils.isNotEmpty(request.getTargetLocales())) {
	    targetLanguages.addAll(request.getTargetLocales());
	}

	if (CollectionUtils.isNotEmpty(targetLanguages)) {
	    if (request.isTargetTermSearch()) {
		filter.setTargetLanguages(targetLanguages);
	    }
	}

	List<String> languageToExport = request.getLanguagesToExport();
	if (CollectionUtils.isNotEmpty(languageToExport)) {
	    filter.addLanguageResultField(true, getSynonymNumber(),
		    languageToExport.toArray(new String[languageToExport.size()]));
	}

	List<String> hideBlanksLanguages = request.getHideBlanksLanguageIds();
	if (CollectionUtils.isNotEmpty(hideBlanksLanguages)) {
	    filter.setHideBlanksLanguages(hideBlanksLanguages);
	}

	List<String> creationUsers = request.getCreationUsers();
	if (CollectionUtils.isNotEmpty(creationUsers)) {
	    filter.addUsersCreatedFilter(creationUsers.toArray(new String[creationUsers.size()]));
	}

	List<String> modificationUsers = request.getModificationUsers();
	if (CollectionUtils.isNotEmpty(modificationUsers)) {
	    filter.addUsersModifiedFilter(modificationUsers.toArray(new String[modificationUsers.size()]));
	}

	if (request.isMissingTranslation()) {
	    filter.setOnlyMissingTranslation(true);
	}

	List<String> statuses = request.getStatuses();
	if (CollectionUtils.isNotEmpty(statuses)) {
	    filter.setStatuses(statuses);
	}

	Set<Description> attributes = request.getDescriptions();
	if (CollectionUtils.isNotEmpty(attributes)) {
	    for (Description attribute : attributes) {
		StringBuilder builder = new StringBuilder();
		builder.append(attribute.getType());
		builder.append(StringConstants.SPACE);
		builder.append(attribute.getValue());

		filter.addAttributeType(builder.toString());
	    }
	}

	Set<Description> notes = request.getNotes();
	if (CollectionUtils.isNotEmpty(notes)) {
	    for (Description note : notes) {
		StringBuilder builder = new StringBuilder();
		builder.append(note.getType());
		builder.append(StringConstants.SPACE);
		builder.append(note.getValue());

		filter.addNoteType(builder.toString());
	    }
	}

	String searchedText = request.getTerm();
	if (StringUtils.isNotEmpty(searchedText)) {
	    TextFilter textFilter = new TextFilter(searchedText);

	    if (StringUtils.isNotBlank(request.getSearchType())
		    && request.getSearchType().equals(ConcordanceType.EXACT.name())) {
		textFilter.setExactMatch(true);
	    }

	    String typeSearchName = request.getTypeSearch().name();
	    if (TypeSearchEnum.ALL.name().equals(typeSearchName)) {
		textFilter.setAllTextSearch(true);
	    } else if (TypeSearchEnum.ATTRIBUTES.name().equals(typeSearchName)) {
		textFilter.setAttributeTextSearch(true);
	    }

	    filter.setTextFilter(textFilter);
	}

	if (pagedListInfo != null) {
	    int index = pagedListInfo.getIndex();
	    int size = pagedListInfo.getSize();

	    String property = StringUtils.isEmpty(searchedText) ? pagedListInfo.getSortProperty() : null;
	    // sort property should be empty or null if quick search is applied
	    if (StringUtils.isEmpty(property)) {
		filter.setPageable(new TmgrPageRequest(index, size, null));
	    } else {
		Direction direction = pagedListInfo.getAscending() ? Direction.ASC : Direction.DESC;
		filter.setPageable(new TmgrPageRequest(index, size, direction, property));
	    }
	}

	return filter;
    }

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoExportCommand.class;
    }

    private Integer getBatchSize() {
	return _batchSize;
    }

    protected PagedListInfo createPageListInfo(TermEntrySearchRequest termEntrySearchRequest) {
	SortDirection sortDirection = termEntrySearchRequest.isAscending() ? SortDirection.ASCENDING
		: SortDirection.DESCENDING;
	PagedListInfo pageListInfo = new PagedListInfo();
	pageListInfo.setSortDirection(sortDirection);
	pageListInfo.setSortProperty(termEntrySearchRequest.getSortProperty());
	pageListInfo.setSize(getBatchSize());
	return pageListInfo;
    }

    protected TermEntrySearchRequest createSearchRequestFromSearchCommand(ExportCommand command) {

	TermEntrySearchRequest request = new TermEntrySearchRequest();
	request.setProjectId(command.getProjectId());

	request.setDateCreatedFrom(command.getDateCreatedFrom());
	request.setDateCreatedTo(command.getDateCreatedTo());

	request.setDateModifiedFrom(command.getDateModifiedFrom());
	request.setDateModifiedTo(command.getDateModifiedTo());

	List<String> languagesToExport = new LinkedList<String>();
	if (command.getSourceLocale() != null) {
	    languagesToExport.add(command.getSourceLocale());
	    request.setSourceLocale(command.getSourceLocale());
	}
	if (command.getTargetLocales() != null) {
	    languagesToExport.addAll(command.getTargetLocales());
	    request.setTargetLocales(command.getTargetLocales());
	    request.setTargetLanguagesList(command.getTargetLocales());
	}
	request.setLanguagesToExport(languagesToExport);

	request.setHideBlanksLanguageIds(command.getHideBlanks());

	request.setCreationUsers(command.getCreationUsers());
	request.setModificationUsers(command.getModificationUsers());

	// merged termEntry and term attributes
	HashSet<Description> attributesFilter = new HashSet<Description>();

	List<Description> termEntryAttributesFilter = command.getTermEntryAttributesFilter();
	if (CollectionUtils.isNotEmpty(termEntryAttributesFilter)) {
	    attributesFilter.addAll(termEntryAttributesFilter);
	}
	List<Description> termAttributesFilter = command.getTermAttributesFilter();
	if (CollectionUtils.isNotEmpty(termAttributesFilter)) {
	    attributesFilter.addAll(termAttributesFilter);
	}
	request.setDescriptions(attributesFilter);

	List<Description> termNotesFilter = command.getTermNotesFilter();
	if (CollectionUtils.isNotEmpty(termNotesFilter)) {
	    request.setNotes(new HashSet<Description>(termNotesFilter));
	}

	request.setTermEntriesAttributes(command.getTermEntryAttributeTypes());
	request.setTermAttributes(command.getTermAttributeTypes());
	request.setTermNotes(command.getTermNoteTypes());

	request.setStatuses(command.getTargetStatuses());

	request.setIncludeSource(command.isIncludeSource());

	ManualTaskHandlerUtils.resolveStatusSearch(request);

	ExportSearchFilter searchFilter = command.getSearchFilter();
	if (searchFilter != null) {
	    request.setAscending(searchFilter.isAscending());
	    request.setSortProperty(searchFilter.getSortProperty());

	    ExportSearchFilter.EntityType entityType = searchFilter.getEntityType();
	    if (Objects.nonNull(entityType)) {
		List<String> entityTypes = entityType.getValue1();
		List<String> languageLevel = entityType.getValue2();
		ManualTaskHandlerUtils.resolveSearchEntityTypes(entityTypes, languageLevel, request);
	    }

	    TermNameAndSearchType termNameAndSearchType = searchFilter.getTermNameAndSearchType();
	    if (termNameAndSearchType != null) {
		request.setTerm(termNameAndSearchType.getValue1());
		request.setSearchType(termNameAndSearchType.getValue2());
	    }
	}

	return request;
    }
}
