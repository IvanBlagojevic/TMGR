package org.gs4tr.termmanager.persistence.solr.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation3.solr.model.SolrConstants;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;
import org.gs4tr.tm3.api.DateFilter;
import org.gs4tr.tm3.api.glossary.GlossaryConcordanceQuery;
import org.gs4tr.tm3.api.glossary.GlossarySearchFilter;

public class TmgrSearchFilter implements ISearchFilter {

    private static final String PREFIX = "_";

    private static final String ASSIGNEE_STRING_BASIC_SORT = PREFIX.concat(SolrParentDocFields.DYN_ASSIGNEE_SORT);

    private static final String ATTRIBUTES = PREFIX.concat(SolrParentDocFields.ATTRIBUTE_MULTI_STORE);

    private static final String DATE_COMPLETED_LONG_SORT = PREFIX.concat(SolrParentDocFields.DYN_DATE_COMPLETED_SORT);

    private static final String DATE_CREATED_LONG_SORT = PREFIX.concat(SolrParentDocFields.DYN_DATE_CREATED_SORT);

    private static final String DATE_CREATED_LONG_STORE = PREFIX.concat(SolrParentDocFields.DYN_DATE_CREATED_STORE);

    private static final String DATE_MODIFIED_LONG_SORT = PREFIX.concat(SolrParentDocFields.DYN_DATE_MODIFIED_SORT);

    private static final String DATE_MODIFIED_LONG_STORE = PREFIX.concat(SolrParentDocFields.DYN_DATE_MODIFIED_STORE);

    private static final String DATE_SUBMITTED_LONG_SORT = PREFIX.concat(SolrParentDocFields.DYN_DATE_SUBMITTED_SORT);

    private static final String DISABLED = PREFIX.concat(SolrParentDocFields.DYN_DISABLED_STORE);

    private static final String FORBIDDEN = PREFIX.concat(SolrParentDocFields.DYN_FORBIDDEN_STORE);

    private static final String ID_STRING_BASIC_SORT = PREFIX.concat(SolrParentDocFields.DYN_TERM_ID_SORT);

    private static final String ID_STRING_STORE = PREFIX.concat(SolrParentDocFields.DYN_TERM_ID_STORE);

    private static final String IN_TRANSLATION_AS_SOURCE = PREFIX.concat(SolrParentDocFields.DYN_IN_TRANSLATION_AS_SOURCE_STORE);

    private static final String NOTES = PREFIX.concat(SolrParentDocFields.DYN_NOTE_MULTI_STORE);

    private static final String COMMENT_STRING_STORE_MULTI = PREFIX.concat(SolrParentDocFields.DYN_COMMENT_MULTI_STORE);

    private static final String DELETED_TERMS = PREFIX.concat(SolrParentDocFields.DYN_DELETED_TERMS);

    private static final String STATUS_STRING_BASIC_SORT = PREFIX.concat(SolrParentDocFields.DYN_STATUS_SORT);

    private static final String STATUS_STRING_STORE = PREFIX.concat(SolrParentDocFields.DYN_STATUS_STORE);

    private static final String TEMP_TERM_NAME_STRING_STORE_SORT = PREFIX
	    .concat(SolrParentDocFields.DYN_TEMP_TERM_NAME_SORT_SUFFIX_NEW);

    private static final String TERM_NAME_STRING_STORE = PREFIX.concat(SolrParentDocFields.DYN_TERM_NAME_STORE);

    private static final String TERM_NAME_STRING_STORE_SORT = PREFIX.concat(SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW);

    private static final String USER_CREATED_STRING_BASIC_SORT = PREFIX.concat(SolrParentDocFields.DYN_USER_CREATED_SORT);

    private static final String USER_CREATED_STRING_STORE = PREFIX.concat(SolrParentDocFields.DYN_USER_CREATED_STORE);

    private static final String USER_MODIFIED_STRING_BASIC_SORT = PREFIX.concat(SolrParentDocFields.DYN_USER_MODIFIED_SORT);

    private static final String USER_MODIFIED_STRING_STORE = PREFIX.concat(SolrParentDocFields.DYN_USER_MODIFIED_STORE);

    private List<String> _assignees;

    private Set<String> _attributeTypeFilter;

    private Boolean _commited;

    private DateFilter _dateCompletedFilter;

    private DateFilter _dateCreatedFilter;

    private DateFilter _dateModifiedFilter;

    private DateFilter _dateSubmittedFilter;

    private List<DescriptionFilter> _descriptionFilters;

    private boolean _excludeDisabled = true;

    private boolean _fetchDeleted = false;

    private Map<String, String> _filterProperties;

    private List<String> _hideBlanksLanguages;

    private boolean _importSearch = false;

    private Set<String> _noteTypeFilter;

    private boolean _onlyMissingTranslation = false;

    private IPageable _pageable;

    private DateFilter _parentDateModifiedFilter;

    private List<Long> _projectIds;

    private Set<String> _resultFields;

    private List<String> _routes;

    private String _shortCode;

    private String _sourceLanguage;

    private List<String> _statuses;

    private Long _submissionId;

    private String _submitter;

    private boolean _submitterView = true;

    private boolean _syncSearch = false;

    private String _targetLanguage;

    private List<String> _targetLanguages;

    private boolean _tempTermSearch = false;

    private TextFilter _textFilter;

    private Long _userLatestChange;

    private List<String> _usersCreated;

    private List<String> _usersModified;

    public TmgrSearchFilter() {
	_pageable = new TmgrPageRequest();
    }

    public TmgrSearchFilter(GlossaryConcordanceQuery query) {
	GlossarySearchFilter filter = query.getFilter();
	if (filter != null) {
	    setDateCreatedFilter(filter.getCreationDate());
	    setDateModifiedFilter(filter.getModificationDate());
	    addUsersCreatedFilter(filter.getCreationUser());
	    addUsersModifiedFilter(filter.getModificationUser());
	}

	setTextFilter(new TextFilter(query.getQuery(), query.isExactMatch(), query.isCaseSensitive()));

	_pageable = new TmgrPageRequest(query.getOffset(), query.getMaxResults(), null);
    }

    public TmgrSearchFilter(IPageable pageable) {
	_pageable = pageable;
    }

    public void addAttributeType(String... attributeType) {
	if (attributeType == null) {
	    return;
	}

	if (_attributeTypeFilter == null) {
	    _attributeTypeFilter = new HashSet<String>();
	}

	for (int i = 0; i < attributeType.length; i++) {
	    _attributeTypeFilter.add(attributeType[i]);
	}
    }

    public void addDescriptionFilter(DescriptionFilter filter) {

	if (_descriptionFilters == null) {
	    _descriptionFilters = new ArrayList<>();
	}

	_descriptionFilters.add(filter);

    }

    @Override
    public void addFilterProperty(String key, String value) {
	if (_filterProperties == null) {
	    _filterProperties = new LinkedHashMap<>();
	}

	String filterValue = _filterProperties.get(key);
	if (filterValue == null) {
	    _filterProperties.put(key, value);
	} else {
	    filterValue = filterValue.concat(StringConstants.COMMA).concat(value);
	    _filterProperties.put(key, filterValue);
	}
    }

    public void addHideBlanksLanguages(String... languageId) {
	if (languageId == null) {
	    return;
	}

	if (_hideBlanksLanguages == null) {
	    _hideBlanksLanguages = new ArrayList<>();
	}

	for (int i = 0; i < languageId.length; i++) {
	    _hideBlanksLanguages.add(languageId[i]);
	}
    }

    public void addLanguageResultField(boolean includeTermEntryFields, boolean fetchDeleted, int synonymNumber,
	    String... languageIds) {
	if (languageIds == null) {
	    return;
	}

	setFetchDeleted(fetchDeleted);

	List<String> fields = new ArrayList<>();

	if (includeTermEntryFields) {
	    addTermEntryResultField();
	} else {
	    // Because of solr routing, projectId is mandatory returned field
	    fields.add(SolrParentDocFields.PROJECT_ID_INDEX_STORE);
	}

	for (String languageId : languageIds) {
	    fields.add(languageId.concat(TERM_NAME_STRING_STORE_SORT));
	    fields.add(languageId.concat(TEMP_TERM_NAME_STRING_STORE_SORT));
	    fields.add(languageId.concat(ID_STRING_BASIC_SORT));
	    fields.add(languageId.concat(USER_CREATED_STRING_BASIC_SORT));
	    fields.add(languageId.concat(USER_MODIFIED_STRING_BASIC_SORT));
	    fields.add(languageId.concat(DATE_CREATED_LONG_SORT));
	    fields.add(languageId.concat(DATE_MODIFIED_LONG_SORT));
	    fields.add(languageId.concat(STATUS_STRING_BASIC_SORT));
	    fields.add(languageId.concat(ASSIGNEE_STRING_BASIC_SORT));
	    fields.add(languageId.concat(IN_TRANSLATION_AS_SOURCE));
	    fields.add(languageId.concat(FORBIDDEN));
	    fields.add(languageId.concat(DISABLED));
	    fields.add(languageId.concat(DELETED_TERMS));

	    // Attributes and Notes are needed for import
	    fields.add(languageId.concat(ATTRIBUTES));
	    fields.add(languageId.concat(NOTES));

	    fields.add(languageId.concat(DATE_SUBMITTED_LONG_SORT));
	    fields.add(languageId.concat(DATE_COMPLETED_LONG_SORT));
	    fields.add(languageId.concat(COMMENT_STRING_STORE_MULTI));

	    for (int i = 1; i <= synonymNumber; i++) {
		String synonym = languageId.concat(Integer.toString(i));

		fields.add(synonym.concat(TERM_NAME_STRING_STORE));
		fields.add(synonym.concat(USER_CREATED_STRING_STORE));
		fields.add(synonym.concat(USER_MODIFIED_STRING_STORE));
		fields.add(synonym.concat(DATE_CREATED_LONG_STORE));
		fields.add(synonym.concat(DATE_MODIFIED_LONG_STORE));
		fields.add(synonym.concat(STATUS_STRING_STORE));
		fields.add(synonym.concat(ID_STRING_STORE));
		fields.add(synonym.concat(IN_TRANSLATION_AS_SOURCE));
		fields.add(synonym.concat(FORBIDDEN));
		fields.add(synonym.concat(DISABLED));
		fields.add(synonym.concat(DELETED_TERMS));

		// Attributes and Notes are needed for import
		fields.add(synonym.concat(ATTRIBUTES));
		fields.add(synonym.concat(NOTES));
	    }
	}

	addResultField(fields.toArray(new String[fields.size()]));

    }

    public void addLanguageResultField(boolean includeTermEntryFields, int synonymNumber, String... languageIds) {
	addLanguageResultField(includeTermEntryFields, isFetchDeleted(), synonymNumber, languageIds);
    }

    public void addNoteType(String... noteType) {
	if (noteType == null) {
	    return;
	}

	if (_noteTypeFilter == null) {
	    _noteTypeFilter = new HashSet<>();
	}

	for (int i = 0; i < noteType.length; i++) {
	    _noteTypeFilter.add(noteType[i]);
	}
    }

    public void addProjectId(Long projectId) {
	if (projectId != null) {
	    if (_projectIds == null) {
		_projectIds = new ArrayList<>();
	    }

	    _projectIds.add(projectId);
	    addFilterProperty(SolrParentDocFields.PROJECT_ID_INDEX_STORE, String.valueOf(projectId));
	}
    }

    @Override
    public void addResultField(String... field) {
	if (field == null) {
	    return;
	}

	if (_resultFields == null) {
	    _resultFields = new HashSet<>();
	}

	for (int i = 0; i < field.length; i++) {
	    _resultFields.add(field[i]);
	}
    }

    public void addTermEntryResultField() {
	List<String> fields = new ArrayList<String>();
	fields.add(SolrConstants.ID_FIELD);
	fields.add(SolrParentDocFields.LANGUAGE_ID_INDEX_STORE_MULTI);
	fields.add(SolrParentDocFields.DATE_CREATED_INDEX_STORE);
	fields.add(SolrParentDocFields.DATE_MODIFIED_INDEX_STORE);
	fields.add(SolrParentDocFields.USER_CREATED_INDEX);
	fields.add(SolrParentDocFields.USER_MODIFIED_SORT);
	fields.add(SolrParentDocFields.PROJECT_ID_INDEX_STORE);
	fields.add(SolrParentDocFields.SHORTCODE_INDEX_STORE);
	fields.add(SolrParentDocFields.SUBMISSION_ID_INDEX_STORE);
	fields.add(SolrParentDocFields.SUBMISSION_NAME_STORE);
	fields.add(SolrParentDocFields.SUBMITTER_SORT);
	fields.add(SolrParentDocFields.PARENT_ID_STORE);
	fields.add(SolrParentDocFields.PROJECT_NAME_INDEX_STORE);
	fields.add(SolrParentDocFields.ATTRIBUTE_MULTI_STORE);

	addResultField(fields.toArray(new String[fields.size()]));
    }

    public void addUsersCreatedFilter(String... users) {
	if (users == null) {
	    return;
	}

	if (_usersCreated == null) {
	    _usersCreated = new ArrayList<>();
	}

	for (int i = 0; i < users.length; i++) {
	    _usersCreated.add(users[i]);
	}
    }

    public void addUsersModifiedFilter(String... users) {
	if (users == null) {
	    return;
	}

	if (_usersModified == null) {
	    _usersModified = new ArrayList<>();
	}

	for (int i = 0; i < users.length; i++) {
	    _usersModified.add(users[i]);
	}
    }

    public List<String> getAssignees() {
	return _assignees;
    }

    public Set<String> getAttributeTypeFilter() {
	return _attributeTypeFilter;
    }

    public Boolean getCommited() {
	return _commited;
    }

    public DateFilter getDateCompletedFilter() {
	return _dateCompletedFilter;
    }

    public DateFilter getDateCreatedFilter() {
	return _dateCreatedFilter;
    }

    public DateFilter getDateModifiedFilter() {
	return _dateModifiedFilter;
    }

    public DateFilter getDateSubmittedFilter() {
	return _dateSubmittedFilter;
    }

    public List<DescriptionFilter> getDescriptionFilters() {
	return _descriptionFilters;
    }

    @Override
    public Map<String, String> getFilterProperties() {
	return _filterProperties;
    }

    public List<String> getHideBlanksLanguages() {
	return _hideBlanksLanguages;
    }

    public Set<String> getNoteTypeFilter() {
	return _noteTypeFilter;
    }

    @Override
    public IPageable getPageable() {
	return _pageable;
    }

    public DateFilter getParentDateModifiedFilter() {
	return _parentDateModifiedFilter;
    }

    public List<Long> getProjectIds() {
	return _projectIds;
    }

    @Override
    public Set<String> getResultFields() {
	return _resultFields;
    }

    public List<String> getRoutes() {
	return _routes;
    }

    public String getShortCode() {
	return _shortCode;
    }

    public String getSourceLanguage() {
	return _sourceLanguage;
    }

    public List<String> getStatuses() {
	return _statuses;
    }

    public Long getSubmissionId() {
	return _submissionId;
    }

    public String getSubmitter() {
	return _submitter;
    }

    public String getTargetLanguage() {
	return _targetLanguage;
    }

    public List<String> getTargetLanguages() {
	return _targetLanguages;
    }

    public TextFilter getTextFilter() {
	return _textFilter;
    }

    public Long getUserLatestChange() {
	return _userLatestChange;
    }

    public List<String> getUsersCreated() {
	return _usersCreated;
    }

    public List<String> getUsersModified() {
	return _usersModified;
    }

    public boolean isExcludeDisabled() {
	return _excludeDisabled;
    }

    public boolean isFetchDeleted() {
	return _fetchDeleted;
    }

    public boolean isHideBlanks() {
	return (CollectionUtils.isNotEmpty(getHideBlanksLanguages()));
    }

    public boolean isImportSearch() {
	return _importSearch;
    }

    public boolean isMultiLingual() {
	return (StringUtils.isNotEmpty(getSourceLanguage()) && CollectionUtils.isNotEmpty(getTargetLanguages()));
    }

    public boolean isOnlyMissingTranslation() {
	return _onlyMissingTranslation;
    }

    public boolean isSourceSearch() {
	return (StringUtils.isNotEmpty(getSourceLanguage()) && CollectionUtils.isEmpty(getTargetLanguages()));
    }

    public boolean isSubmitterView() {
	return _submitterView;
    }

    public boolean isSyncSearch() {
	return _syncSearch;
    }

    public boolean isTargetSearch() {
	return (StringUtils.isEmpty(getSourceLanguage()) && CollectionUtils.isNotEmpty(getTargetLanguages()));
    }

    public boolean isTempTermSearch() {
	return _tempTermSearch;
    }

    public void setAssignees(List<String> assignees) {
	_assignees = assignees;
    }

    public void setAttributeTypeFilter(Set<String> attributeTypeFilter) {
	_attributeTypeFilter = attributeTypeFilter;
    }

    public void setCommited(Boolean commited) {
	_commited = commited;
    }

    public void setDateCompletedFilter(DateFilter dateCompletedFilter) {
	_dateCompletedFilter = dateCompletedFilter;
    }

    public void setDateCreatedFilter(DateFilter dateCreatedFilter) {
	_dateCreatedFilter = dateCreatedFilter;
    }

    public void setDateModifiedFilter(DateFilter dateModifiedFilter) {
	_dateModifiedFilter = dateModifiedFilter;
    }

    public void setDateSubmittedFilter(DateFilter dateSubmittedFilter) {
	_dateSubmittedFilter = dateSubmittedFilter;
    }

    public void setDescriptionFilters(List<DescriptionFilter> descriptionFilters) {
	_descriptionFilters = descriptionFilters;
    }

    public void setExcludeDisabled(boolean excludeDisabled) {
	_excludeDisabled = excludeDisabled;
    }

    public void setFetchDeleted(boolean fetchDeleted) {
	_fetchDeleted = fetchDeleted;
    }

    public void setFilterProperties(Map<String, String> filterProperties) {
	_filterProperties = filterProperties;
    }

    public void setHideBlanksLanguages(List<String> hideBlanksLanguages) {
	_hideBlanksLanguages = hideBlanksLanguages;
    }

    public void setImportSearch(boolean importSearch) {
	_importSearch = importSearch;
    }

    public void setNoteTypeFilter(Set<String> noteTypeFilter) {
	_noteTypeFilter = noteTypeFilter;
    }

    public void setOnlyMissingTranslation(boolean onlyMissingTranslation) {
	_onlyMissingTranslation = onlyMissingTranslation;
    }

    @Override
    public void setPageable(IPageable pageable) {
	_pageable = pageable;
    }

    public void setParentDateModifiedFilter(DateFilter parentDateModifiedFilter) {
	_parentDateModifiedFilter = parentDateModifiedFilter;
    }

    public void setProjectIds(List<Long> projectIds) {
	if (projectIds != null && !projectIds.isEmpty()) {
	    _projectIds = projectIds;
	    for (Long projectId : projectIds) {
		addFilterProperty(SolrParentDocFields.PROJECT_ID_INDEX_STORE, String.valueOf(projectId));
	    }
	}
    }

    public void setResultFields(Set<String> resultFields) {
	_resultFields = resultFields;
    }

    public void setRoutes(List<String> routes) {
	_routes = routes;
    }

    public void setShortCode(String shortCode) {
	if (shortCode != null) {
	    _shortCode = shortCode;
	    addFilterProperty(SolrParentDocFields.SHORTCODE_INDEX_STORE, shortCode);
	}
    }

    public void setSourceLanguage(String sourceLanguage) {
	_sourceLanguage = sourceLanguage;
    }

    public void setStatuses(List<String> statuses) {
	_statuses = statuses;
    }

    public void setSubmissionId(Long submissionId) {
	_submissionId = submissionId;
	addFilterProperty(SolrParentDocFields.SUBMISSION_ID_INDEX_STORE, String.valueOf(submissionId));
    }

    public void setSubmitter(String submitter) {
	_submitter = submitter;
    }

    public void setSubmitterView(boolean submitterView) {
	_submitterView = submitterView;
    }

    public void setSyncSearch(boolean syncSearch) {
	_syncSearch = syncSearch;
    }

    public void setTargetLanguage(String targetLanguage) {
	_targetLanguage = targetLanguage;
    }

    public void setTargetLanguages(List<String> targetLanguages) {
	_targetLanguages = targetLanguages;
    }

    public void setTempTermSearch(boolean tempTermSearch) {
	_tempTermSearch = tempTermSearch;
    }

    public void setTextFilter(TextFilter textFilter) {
	_textFilter = textFilter;
    }

    public void setUserLatestChange(Long userLatestChange) {
	_userLatestChange = userLatestChange;
    }

    public void setUsersCreated(List<String> usersCreated) {
	_usersCreated = usersCreated;
    }

    public void setUsersModified(List<String> usersModified) {
	_usersModified = usersModified;
    }
}
