package org.gs4tr.termmanager.webmvc.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.MultilingualTerm;
import org.gs4tr.termmanager.model.TermSearchRequest;
import org.gs4tr.termmanager.model.dto.converter.MultilingualTermConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.MultilingualViewModelService;
import org.gs4tr.termmanager.service.TermService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandlerUtils;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.model.commands.DateRange;
import org.gs4tr.termmanager.webmvc.model.commands.SearchCommand;
import org.gs4tr.termmanager.webmvc.model.commands.SearchItem;
import org.gs4tr.termmanager.webmvc.model.commands.SearchValue;
import org.gs4tr.termmanager.webmvc.model.search.DateSearchEnum;
import org.gs4tr.termmanager.webmvc.model.search.DoubleMultiComboBoxDefaultValue;
import org.gs4tr.termmanager.webmvc.model.search.EntitySearch;
import org.gs4tr.termmanager.webmvc.model.search.InputTextAndComboItem;
import org.gs4tr.termmanager.webmvc.model.search.LanguageDirectionSearchEnum;
import org.gs4tr.termmanager.webmvc.model.search.LinkedComboBoxDefaultValue;
import org.gs4tr.termmanager.webmvc.model.search.SearchCriteria;
import org.gs4tr.termmanager.webmvc.model.search.TermNameSearchEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = UrlConstants.MULTILINGUAL_SEARCH, method = RequestMethod.POST)
public class MultilingualViewSearchController extends
	AbstractSearchGridController<MultilingualTerm, org.gs4tr.termmanager.model.dto.MultilingualTerm, TermSearchRequest, SearchCommand> {

    @Autowired
    private MultilingualViewModelService _multilingualViewModelService;

    @Autowired
    private TermService _termService;

    @Autowired
    private UserProfileService _userProfileService;

    protected MultilingualViewSearchController() {
	super(EntityTypeHolder.TERM, MultilingualTerm.class, org.gs4tr.termmanager.model.dto.MultilingualTerm.class);
    }

    private void configureCreatedUserFilter(List<SearchItem> items, SearchCommand command) {

	SearchItem item = getSearchItemByCriteria(items, SearchCriteria.TL_CREATION_USERS.getControlName());

	if (isSearchItemValid(item)) {
	    List<SearchValue> values = item.getValues();
	    for (SearchValue value : values) {
		command.setCreationUsers(getEntityValues(value));
	    }
	}
    }

    private void configureDateCreatedRangeFilter(List<SearchItem> items, SearchCommand command) {

	SearchItem item = getSearchItemByCriteria(items, SearchCriteria.TL_DATE_CREATED_RANGE.getControlName());

	if (isSearchItemValid(item)) {
	    command.setDateCreatedRange(new DateRange());
	    List<SearchValue> values = item.getValues();
	    for (SearchValue value : values) {
		String date = getDateFromValue(value);
		resolveDateCreatedRange(value.getName(), date, command);
	    }
	}
    }

    private void configureDateModifiedRangeFilter(List<SearchItem> items, SearchCommand command) {
	SearchItem item = getSearchItemByCriteria(items, SearchCriteria.TL_DATE_MODIFIED_RANGE.getControlName());

	if (isSearchItemValid(item)) {
	    command.setDateModifiedRange(new DateRange());
	    List<SearchValue> values = item.getValues();
	    for (SearchValue value : values) {
		String date = getDateFromValue(value);
		resolveDateModifiedRange(value.getName(), date, command);
	    }
	}
    }

    private void configureEntityTypeFilter(List<SearchItem> items, SearchCommand command) {

	SearchItem item = getSearchItemByCriteria(items, SearchCriteria.TL_ENTITY_TYPE.getControlName());

	if (isSearchItemValid(item)) {

	    List<SearchValue> values = item.getValues();

	    List<String> entityTypes = new ArrayList<>();
	    List<String> languageLevel = new ArrayList<>();

	    for (SearchValue value : values) {
		resolveEntityType(value.getName(), value.getValues(), entityTypes, languageLevel);
	    }

	    DoubleMultiComboBoxDefaultValue doubleMultiComboBoxDefaultValue = new DoubleMultiComboBoxDefaultValue();

	    doubleMultiComboBoxDefaultValue.setValue1(entityTypes);
	    doubleMultiComboBoxDefaultValue.setValue2(languageLevel);

	    command.setEntityType(doubleMultiComboBoxDefaultValue);
	}
    }

    private void configureHideBlanksFilter(List<SearchItem> items, SearchCommand command) {

	SearchItem item = getSearchItemByCriteria(items, SearchCriteria.TL_HIDE_BLANKS.getControlName());

	if (isSearchItemValid(item)) {
	    List<SearchValue> values = item.getValues();
	    for (SearchValue value : values) {
		command.setHideBlanks(getEntityValues(value));
	    }
	}
    }

    private void configureLanguageDirectionFilter(List<SearchItem> items, SearchCommand command) {

	SearchItem item = getSearchItemByCriteria(items, SearchCriteria.TL_LANGUAGE_DIRECTION.getControlName());

	if (isSearchItemValid(item)) {
	    List<SearchValue> values = item.getValues();

	    command.setLanguageDirection(new LinkedComboBoxDefaultValue());
	    for (SearchValue value : values) {

		String name = value.getName();
		List<String> languageValues = value.getValues();

		if (isSource(name)) {
		    boolean isSourceEmpty = CollectionUtils.isEmpty(languageValues);
		    String source = isSourceEmpty ? ControllerUtils.getDefaultSourceLanguage() : languageValues.get(0);
		    command.getLanguageDirection().setValue1(source);
		} else if (isTarget(name, languageValues)) {
		    command.getLanguageDirection().setValue2(languageValues);
		}
	    }
	}
    }

    private void configureModificationUserFilter(List<SearchItem> items, SearchCommand command) {

	SearchItem item = getSearchItemByCriteria(items, SearchCriteria.TL_MODIFICATION_USERS.getControlName());

	if (isSearchItemValid(item)) {
	    List<SearchValue> values = item.getValues();
	    for (SearchValue value : values) {
		command.setModificationUsers(getEntityValues(value));
	    }
	}
    }

    private void configureProjectFilter(List<SearchItem> items, SearchCommand command) {
	SearchItem item = getSearchItemByCriteria(items, SearchCriteria.TL_PROJECT_LIST.getControlName());

	if (isSearchItemValid(item)) {
	    command.setProjectComboBox(new ArrayList<>());
	    List<SearchValue> values = item.getValues();
	    for (SearchValue value : values) {
		List<String> entityValues = getEntityValues(value);
		if (entityValues != null) {
		    command.getProjectComboBox().addAll(entityValues);
		}
	    }
	}
    }

    private void configureTermNameAndSearchTypeFilter(List<SearchItem> items, SearchCommand command) {

	SearchItem item = getSearchItemByCriteria(items, SearchCriteria.TL_TERM_NAME.getControlName());

	if (isSearchItemValid(item)) {
	    command.setTermNameAndSearchType(new InputTextAndComboItem());
	    List<SearchValue> values = item.getValues();
	    for (SearchValue value : values) {
		resolveTermNameAndSearchType(value.getName(), value.getValues(), command);
	    }
	}
    }

    private void configureTermStatusFilter(List<SearchItem> items, SearchCommand command) {
	SearchItem item = getSearchItemByCriteria(items, SearchCriteria.TL_TERM_STATUSES.getControlName());

	if (isSearchItemValid(item)) {
	    command.setStatuses(new ArrayList<>());
	    List<SearchValue> values = item.getValues();
	    for (SearchValue value : values) {
		List<String> entityValues = getEntityValues(value);
		if (entityValues != null) {
		    command.getStatuses().addAll(entityValues);
		}
	    }
	}
    }

    private String getDateFromValue(SearchValue value) {
	List<String> values = value.getValues();
	return CollectionUtils.isNotEmpty(values) ? values.get(0) : null;

    }

    private List<String> getEntityValues(SearchValue value) {
	List<String> values = value.getValues();
	return CollectionUtils.isNotEmpty(values) ? values : null;
    }

    private MultilingualViewModelService getMultilingualViewModelService() {
	return _multilingualViewModelService;
    }

    private SearchItem getSearchItemByCriteria(List<SearchItem> items, String searchCriteria) {
	return items.stream().filter(s -> s.getName().equals(searchCriteria)).findFirst().orElse(null);
    }

    private TermService getTermService() {
	return _termService;
    }

    private UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    private boolean isFromDate(String name, String date) {
	return StringUtils.isNotEmpty(date) && name.equals(DateSearchEnum.FROM_DATE.getCommand());

    }

    private boolean isInclude(String name, List<String> entityValues) {
	return CollectionUtils.isNotEmpty(entityValues) && name.equals(EntitySearch.INCLUDE.getCommand());
    }

    private boolean isOptions(String name, List<String> entityValues) {
	return CollectionUtils.isNotEmpty(entityValues) && name.equals(TermNameSearchEnum.OPTIONS.getCommand());
    }

    private boolean isSearchIn(String name, List<String> entityValues) {
	return CollectionUtils.isNotEmpty(entityValues) && name.equals(EntitySearch.SEARCH_IN.getCommand());
    }

    private boolean isSearchItemValid(SearchItem item) {
	return Objects.nonNull(item) && CollectionUtils.isNotEmpty(item.getValues());
    }

    private boolean isSource(String name) {
	return name.equals(LanguageDirectionSearchEnum.SOURCE.getCommand());
    }

    private boolean isTarget(String name, List<String> languageValues) {
	return CollectionUtils.isNotEmpty(languageValues)
		&& name.equals(LanguageDirectionSearchEnum.TARGET.getCommand());
    }

    private boolean isTerm(String name, List<String> entityValues) {
	return CollectionUtils.isNotEmpty(entityValues) && name.equals(TermNameSearchEnum.TERM.getCommand());
    }

    private boolean isToDate(String name, String date) {
	return StringUtils.isNotEmpty(date) && name.equals(DateSearchEnum.TO_DATE.getCommand());

    }

    private void resolveDateCreatedRange(String name, String date, SearchCommand command) {
	if (StringUtils.isEmpty(date)) {
	    return;
	}

	Long dateLong = Long.valueOf(date);
	if (isFromDate(name, date)) {
	    command.getDateCreatedRange().setFromDate(dateLong);
	} else if (isToDate(name, date)) {
	    command.getDateCreatedRange().setToDate(dateLong);
	}
    }

    private void resolveDateModifiedRange(String name, String date, SearchCommand command) {
	if (StringUtils.isEmpty(date)) {
	    return;
	}

	Long dateLong = Long.valueOf(date);
	if (isFromDate(name, date)) {
	    command.getDateModifiedRange().setFromDate(dateLong);
	} else if (isToDate(name, date)) {
	    command.getDateModifiedRange().setToDate(dateLong);
	}

    }

    private void resolveEntityType(String name, List<String> entityValues, List<String> entityTypes,
	    List<String> languageLevel) {

	if (isSearchIn(name, entityValues)) {
	    entityTypes.addAll(entityValues);
	} else if (isInclude(name, entityValues)) {
	    languageLevel.addAll(entityValues);
	}

    }

    private void resolveTermNameAndSearchType(String name, List<String> entityValues, SearchCommand command) {
	InputTextAndComboItem inputTextAndComboItem = command.getTermNameAndSearchType();
	if (isTerm(name, entityValues)) {
	    inputTextAndComboItem.setValue1(entityValues.get(0));
	} else if (isOptions(name, entityValues)) {
	    inputTextAndComboItem.setValue2(entityValues.get(0));
	}
    }

    @Override
    protected org.gs4tr.termmanager.model.dto.MultilingualTerm createDtoEntityFromEntity(
	    MultilingualTerm multilingualTerm) {
	org.gs4tr.termmanager.model.dto.MultilingualTerm dtoMultilingualTerm = MultilingualTermConverter
		.fromInternalToDto(multilingualTerm);
	return dtoMultilingualTerm;
    }

    @Override
    protected String createGridConfigKey(SearchCommand searchGridCommand) {
	ItemFolderEnum folder = searchGridCommand.getFolder();

	if (ItemFolderEnum.TERM_LIST == folder) {
	    return ItemFolderEnum.TERM_LIST.name().toLowerCase();
	}

	return ItemFolderEnum.SUBMISSIONTERMLIST.name().toLowerCase();
    }

    @Override
    protected GridContentInfo createGridContentInfo(PagedList<MultilingualTerm> targetPagedList) {
	TaskPagedList<MultilingualTerm> pagedList = (TaskPagedList<MultilingualTerm>) targetPagedList;

	GridContentInfo gridContent = new GridContentInfo();

	gridContent.setTotalCount(pagedList.getTotalCount());
	gridContent.setHasNext(pagedList.getHasNext());
	gridContent.setHasPrevious(pagedList.getHasPrevious());
	gridContent.setPageIndexes(pagedList.getPageIndexes());
	gridContent.setTotalPageCount(pagedList.getTotalPageCount());
	gridContent.setPagedListInfo(pagedList.getPagedListInfo());

	return gridContent;
    }

    @Override
    protected TermSearchRequest createSearchRequestFromSearchCommand(SearchCommand command) {
	TermSearchRequest request = new TermSearchRequest();

	request.setFolder(command.getFolder());

	String submissionTicket = command.getSubmissionTicket();
	if (StringUtils.isNotEmpty(submissionTicket)) {
	    request.setSubmissionId(TicketConverter.fromDtoToInternal(submissionTicket, Long.class));
	}

	List<Long> projectIds = TicketConverter.fromDtoToInternal(command.getProjectComboBox());
	request.setProjectIds(projectIds);

	LinkedComboBoxDefaultValue languageDirection = command.getLanguageDirection();

	if (languageDirection != null) {
	    String[] sourceAndTargetLanguage = null;
	    if (StringUtils.isBlank(languageDirection.getValue1())) {
		sourceAndTargetLanguage = ControllerUtils.getDefaultSourceAndTargetLanguage();
	    }

	    String source = languageDirection.getValue1();
	    if (StringUtils.isBlank(source)) {
		request.setSource(sourceAndTargetLanguage[0]);
	    } else {
		request.setSource(source);
	    }

	    List<String> targets = languageDirection.getValue2();
	    if (CollectionUtils.isNotEmpty(targets)) {
		request.setTargetLanguagesList(targets);
	    }
	}

	List<String> hideBlanks = command.getHideBlanks();
	if (CollectionUtils.isNotEmpty(hideBlanks)) {
	    request.setHideBlanksLanguageIds(hideBlanks);
	}

	if (command.isSearchUserLatestChanges()) {
	    request.setSearchUserLatestChanges(true);
	    return request;
	}

	DoubleMultiComboBoxDefaultValue entityType = command.getEntityType();
	if (entityType != null) {
	    ManualTaskHandlerUtils.resolveSearchEntityTypes(entityType.getValue1(), entityType.getValue2(), request);
	}

	request.setStatuses(command.getStatuses());
	ManualTaskHandlerUtils.resolveStatusSearch(request);

	InputTextAndComboItem termNameAndSearchType = command.getTermNameAndSearchType();
	if (termNameAndSearchType != null) {
	    String termText = termNameAndSearchType.getValue1();
	    request.setTerm(termText);
	    String searchType = termNameAndSearchType.getValue2();
	    request.setSearchType(searchType);
	}

	DateRange dateCreatedRange = command.getDateCreatedRange();
	if (dateCreatedRange != null) {
	    if (dateCreatedRange.getFromDate() != null) {
		request.setDateCreatedFrom(new Date(dateCreatedRange.getFromDate()));
	    }

	    if (dateCreatedRange.getToDate() != null) {
		request.setDateCreatedTo(new Date(dateCreatedRange.getToDate()));
	    }
	}

	DateRange dateModifiedRange = command.getDateModifiedRange();
	if (dateModifiedRange != null) {
	    if (dateModifiedRange.getFromDate() != null) {
		request.setDateModifiedFrom(new Date(dateModifiedRange.getFromDate()));
	    }

	    if (dateModifiedRange.getToDate() != null) {
		request.setDateModifiedTo(new Date(dateModifiedRange.getToDate()));
	    }
	}

	DateRange dateCompletedRange = command.getDateCompletedRange();
	if (dateCompletedRange != null) {
	    if (dateCompletedRange.getFromDate() != null) {
		request.setDateCompletedFrom(new Date(dateCompletedRange.getFromDate()));
	    }

	    if (dateCompletedRange.getToDate() != null) {
		request.setDateCompletedTo(new Date(dateCompletedRange.getToDate()));
	    }
	}

	List<String> creationUsers = command.getCreationUsers();
	if (CollectionUtils.isNotEmpty(creationUsers)) {
	    request.setCreationUsers(creationUsers);
	}

	List<String> modificationUsers = command.getModificationUsers();
	if (CollectionUtils.isNotEmpty(modificationUsers)) {
	    request.setModificationUsers(modificationUsers);
	}

	return request;
    }

    @Override
    protected boolean isAllProjectsSearch(SearchCommand command) {
	return (CollectionUtils.isEmpty(command.getProjectComboBox()));
    }

    @Override
    protected TaskPagedList<MultilingualTerm> search(TermSearchRequest searchRequest, SearchCommand command,
	    PagedListInfo pagedListInfo) {
	TaskPagedList<MultilingualTerm> result = getMultilingualViewModelService().search(searchRequest, pagedListInfo);

	if (!command.isSearchUserLatestChanges()) {
	    List<Long> projectIds = searchRequest.getProjectIds();
	    updateUserLatestChangedTerms(getUserProfileService(), getTermService(), projectIds);
	}

	return result;
    }

    @Override
    protected void setCommandFields(SearchCommand command) {
	List<SearchItem> filterSearch = command.getFilterSearch();
	if (filterSearch == null) {
	    return;
	}

	configureLanguageDirectionFilter(filterSearch, command);
	configureHideBlanksFilter(filterSearch, command);
	configureCreatedUserFilter(filterSearch, command);
	configureModificationUserFilter(filterSearch, command);
	configureDateCreatedRangeFilter(filterSearch, command);
	configureDateModifiedRangeFilter(filterSearch, command);
	configureEntityTypeFilter(filterSearch, command);
	configureProjectFilter(filterSearch, command);
	configureTermNameAndSearchTypeFilter(filterSearch, command);
	configureTermStatusFilter(filterSearch, command);
    }

}
