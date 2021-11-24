package org.gs4tr.termmanager.persistence.solr.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.search.join.BlockJoinChildQParserPlugin;
import org.apache.solr.search.join.BlockJoinParentQParserPlugin;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation3.solr.model.SolrConstants;
import org.gs4tr.foundation3.solr.model.client.SearchRequest;
import org.gs4tr.foundation3.solr.model.concordance.ConcordanceType;
import org.gs4tr.foundation3.solr.plugin.ConcordanceQueryPlugin;
import org.gs4tr.foundation3.solr.plugin.SubdocumentSearchQueryPlugin;
import org.gs4tr.foundation3.solr.util.SolrQueryBuilder;
import org.gs4tr.termmanager.persistence.solr.query.DescriptionFilter;
import org.gs4tr.termmanager.persistence.solr.query.IPageable;
import org.gs4tr.termmanager.persistence.solr.query.ISearchFilter;
import org.gs4tr.termmanager.persistence.solr.query.Sort;
import org.gs4tr.termmanager.persistence.solr.query.TextFilter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.solr.plugin.utils.ChecksumHelper;
import org.gs4tr.termmanager.solr.plugin.utils.SolrChildDocFileds;
import org.gs4tr.termmanager.solr.plugin.utils.SolrDocHelper;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;
import org.gs4tr.tm3.api.DateFilter;

public class SolrQueryUtils {

    private static final String NOT_QUERIES = "not";

    private static final String OR_QUERIES = "or";

    private static final String PARENT_FILTER = "pf";

    private static final String SEPARATOR = "_"; //$NON-NLS-1$

    public static void addPage(SearchRequest request, TmgrSearchFilter filter) {
	IPageable pageable = filter.getPageable();
	request.setStart(pageable.getOffset());
	request.setRows(pageable.getPageSize());
    }

    public static void addSort(SearchRequest request, TmgrSearchFilter filter) {
	Sort sort = filter.getPageable().getSort();
	if (Objects.isNull(sort)) {
	    return;
	}

	List<Sort.Order> orders = sort.getOrders();
	if (Objects.isNull(orders)) {
	    return;
	}

	for (Sort.Order order : orders) {
	    SolrQuery.ORDER direction = order.isAscending() ? SolrQuery.ORDER.asc : SolrQuery.ORDER.desc;
	    request.addSort(order.getProperty(), direction);
	}

    }

    public static Iterable<String> convertProjectIds(List<Long> projectIds) {
	if (CollectionUtils.isEmpty(projectIds)) {
	    return new HashSet<>(0);
	}
	return projectIds.stream().map(projectId -> String.valueOf(projectId)).collect(Collectors.toSet());
    }

    public static SolrQuery createChildFilterQuery(SolrQuery query, TmgrSearchFilter filter) {
	if (query == null) {
	    query = new SolrQuery();
	}

	SolrQueryBuilder builder = makeBlockJoinParentSolrQueryBuilder();

	String queryString = createChildQuery(query, filter, builder);

	return StringUtils.isNotEmpty(queryString) ? query.addFilterQuery(queryString) : query;
    }

    public static SolrQuery createChildMainQuery(SolrQuery query, TmgrSearchFilter filter) {
	if (query == null) {
	    query = new SolrQuery();
	}

	SolrQueryBuilder builder = SolrQueryBuilder.newInstance();

	String queryString = createChildQuery(query, filter, builder);

	return StringUtils.isNotEmpty(queryString) ? query.setQuery(queryString) : query;
    }

    public static SolrQuery createExactMatchQuery(SolrQuery query, TmgrSearchFilter filter) {
	if (query == null) {
	    query = new SolrQuery();
	}

	SolrQueryBuilder builder = makeBlockJoinParentSolrQueryBuilder();

	String sourceLanguage = filter.getSourceLanguage();
	if (StringUtils.isNotEmpty(sourceLanguage)) {
	    builder.must(SolrChildDocFileds.LANGUAGE_ID_INDEX, sourceLanguage, true);
	}

	TextFilter textFilter = filter.getTextFilter();
	if (textFilter != null && StringUtils.isNotEmpty(textFilter.getText())) {
	    builder.must(SolrChildDocFileds.TERM_CHECKSUM, ChecksumHelper.makeChecksum(textFilter.getText()), true);
	}

	return query.setQuery(builder.buildQuery());
    }

    public static SearchRequest createFacetTermCountQuery(Long projectId, List<String> languageIds) {

	SolrQueryBuilder builder = SolrQueryBuilder.newInstance();

	builder.openShould().must(SolrChildDocFileds.TERM_PROJECT_ID_INDEX, String.valueOf(projectId), false)
		.must(SolrChildDocFileds.LANGUAGE_ID_INDEX, concatStringItemsQuery(languageIds, false), false).close();

	SearchRequest query = new SearchRequest();
	query.setStart(0);
	query.setRows(0);
	query.addFilterQuery(builder.buildQuery());

	return query;
    }

    public static SolrQuery createFindAllQuery(SolrQuery query) {
	if (query == null) {
	    query = new SolrQuery();
	}

	SolrQueryBuilder builder = SolrQueryBuilder.newInstance();

	builder.setQuery(SolrConstants.FIND_ALL_QUERY, false);

	query.setQuery(builder.buildQuery());

	return query;
    }

    public static SolrQuery createHideBlanksFilterQuery(SolrQuery query, TmgrSearchFilter filter) {
	if (query == null) {
	    query = new SolrQuery();
	}

	SolrQueryBuilder builder = makeSolrFilterQueryBuilder(false, 0);

	List<String> hideBlanksLanguages = filter.getHideBlanksLanguages();
	if (CollectionUtils.isNotEmpty(hideBlanksLanguages)) {
	    builder.openShould();
	    for (String language : hideBlanksLanguages) {
		builder.must(SolrParentDocFields.LANGUAGE_ID_INDEX_STORE_MULTI, language, true);
	    }
	    builder.close();
	}

	return query.addFilterQuery(builder.buildQuery());
    }

    public static SolrQuery createIdQuery(SolrQuery query, SolrQueryBuilder builder, String id) {
	if (query == null) {
	    query = new SolrQuery();
	}

	builder.must(SolrConstants.ID_FIELD, id, true);

	return query.setQuery(builder.buildQuery());
    }

    public static SolrQuery createIdsQuery(SolrQuery query, SolrQueryBuilder builder, Iterable<String> ids) {
	if (query == null) {
	    query = new SolrQuery();
	}

	builder.must(SolrConstants.ID_FIELD, concatStringItemsQuery(ids, false), false);

	return query.setQuery(builder.buildQuery());
    }

    public static SolrQuery createLanguageIdFilterQuery(SolrQuery query, String languageId) {
	if (Objects.isNull(query)) {
	    query = new SolrQuery();
	}

	SolrQueryBuilder filterBuilder = makeSolrFilterQueryBuilder(false, 0);

	filterBuilder.must(SolrParentDocFields.LANGUAGE_ID_INDEX_STORE_MULTI, languageId, true);

	return query.addFilterQuery(filterBuilder.buildQuery());
    }

    public static SolrQuery createMissingTranslationFilterQuery(SolrQuery query, TmgrSearchFilter filter) {
	if (query == null) {
	    query = new SolrQuery();
	}

	SolrQueryBuilder builder = makeSolrFilterQueryBuilder(false, 0);

	builder.must(SolrParentDocFields.LANGUAGE_ID_INDEX_STORE_MULTI, filter.getSourceLanguage(), true);

	List<String> targetLanguages = filter.getTargetLanguages();
	if (CollectionUtils.isNotEmpty(targetLanguages)) {
	    builder.openMustNot();
	    for (String targetLanguage : targetLanguages) {
		builder.must(SolrParentDocFields.LANGUAGE_ID_INDEX_STORE_MULTI, targetLanguage, true);
	    }
	    builder.close();
	}

	return query.addFilterQuery(builder.buildQuery());
    }

    public static SolrQuery createParentChildFilterQuery(SolrQuery query, ISearchFilter filter) {
	if (query == null) {
	    query = new SolrQuery();
	}

	Map<String, String> filterProperties = filter.getFilterProperties();
	if (filterProperties == null || filterProperties.isEmpty()) {
	    return query;
	}

	SolrQueryBuilder builder = makeBlockJoinChildSolrQueryBuilder();

	return query.addFilterQuery(makeFilterQuery(filterProperties, builder));
    }

    public static void createParentFilterQuery(SolrQuery query) {
	String parentFilterQuery = makeParentFilterQuery();
	query.addFilterQuery(parentFilterQuery);
    }

    public static SolrQuery createParentFilterQuery(SolrQuery query, TmgrSearchFilter filter) {
	if (query == null) {
	    query = new SolrQuery();
	}

	Map<String, String> filterProperties = filter.getFilterProperties();
	if (filterProperties == null || filterProperties.isEmpty()) {
	    return query;
	}

	SolrQueryBuilder builder = makeSolrFilterQueryBuilder(false, 0);

	if (filter.isSyncSearch()) {
	    DateFilter dateModified = filter.getParentDateModifiedFilter();
	    String startDateString = dateModified.getStartDate() != null
		    ? String.valueOf(dateModified.getStartDate().getTime())
		    : null;
	    String endDateString = dateModified.getEndDate() != null
		    ? String.valueOf(dateModified.getEndDate().getTime())
		    : null;
	    builder.must(SolrParentDocFields.DATE_MODIFIED_INDEX_STORE, startDateString,
		    dateModified.isStartDateInclusive(), endDateString, dateModified.isEndDateInclusive());
	}

	DateFilter dateCreated = filter.getDateCreatedFilter();
	if (dateCreated != null) {
	    String startDateString = dateCreated.getStartDate() != null
		    ? String.valueOf(dateCreated.getStartDate().getTime())
		    : null;
	    String endDateString = dateCreated.getEndDate() != null ? String.valueOf(dateCreated.getEndDate().getTime())
		    : null;
	    builder.must(SolrParentDocFields.DATE_CREATED_INDEX_STORE, startDateString,
		    dateCreated.isStartDateInclusive(), endDateString, dateCreated.isEndDateInclusive());
	}

	List<String> usersCreated = filter.getUsersCreated();
	if (CollectionUtils.isNotEmpty(usersCreated)) {
	    builder.openMust()
		    .should(SolrParentDocFields.USER_CREATED_INDEX, concatStringItemsQuery(usersCreated, false), false)
		    .close();
	}

	return query.addFilterQuery(makeFilterQuery(filterProperties, builder));
    }

    public static SearchRequest createProjectFacetQuery(List<Long> projectIds, List<String> languageIds) {

	SolrQueryBuilder builder = SolrQueryBuilder.newInstance();

	String concatenatedProjectIds = concatStringItemsQuery(convertProjectIds(projectIds), false);
	builder.openShould().must(SolrParentDocFields.PROJECT_ID_INDEX_STORE, concatenatedProjectIds, false);

	String concatenatedLanguageIds = concatStringItemsQuery(languageIds, false);
	builder.must(SolrParentDocFields.LANGUAGE_ID_INDEX_STORE_MULTI, concatenatedLanguageIds, false).close();

	SearchRequest query = new SearchRequest();
	query.setStart(0);
	query.setRows(0);
	query.addFilterQuery(builder.buildQuery());

	return query;
    }

    public static SolrQuery createProjectIdFilterQuery(SolrQuery query, Long projectId) {
	if (Objects.isNull(query)) {
	    query = new SolrQuery();
	}

	SolrQueryBuilder filterBuilder = makeSolrFilterQueryBuilder(false, 0);

	filterBuilder.must(SolrParentDocFields.PROJECT_ID_INDEX_STORE, String.valueOf(projectId), false);

	return query.addFilterQuery(filterBuilder.buildQuery());
    }

    public static SolrQuery createResultFields(SolrQuery query, ISearchFilter filter) {
	if (query == null) {
	    query = new SolrQuery();
	}

	Set<String> fields = filter.getResultFields();
	if (CollectionUtils.isNotEmpty(fields)) {
	    query.setFields(fields.toArray(new String[fields.size()]));
	}

	return query;
    }

    public static SolrQuery createResultFields(SolrQuery query, String... flParameters) {
	if (Objects.isNull(query)) {
	    query = new SolrQuery();
	}

	if (Objects.nonNull(flParameters)) {
	    query.setFields(flParameters);
	}

	return query;
    }

    public static SolrQuery createSearchQuery(SolrQuery query, TmgrSearchFilter filter) {
	if (query == null) {
	    query = new SolrQuery();
	}

	List<String> languageIds = new ArrayList<String>();

	if (filter.isMultiLingual()) {
	    languageIds.add(filter.getSourceLanguage());
	    languageIds.addAll(filter.getTargetLanguages());
	} else if (filter.isSourceSearch()) {
	    languageIds.add(filter.getSourceLanguage());
	} else if (filter.isTargetSearch()) {
	    languageIds.addAll(filter.getTargetLanguages());
	}

	if (languageIds.isEmpty()) {
	    languageIds.add(Locale.US.getCode());
	}

	TextFilter textFilter = filter.getTextFilter();

	boolean useAllQuery = true;
	if (textFilter != null) {
	    String termQueryString = createTextQuery(filter, textFilter, languageIds);
	    query.setQuery(termQueryString);
	    useAllQuery = false;
	}

	if (filter.getAttributeTypeFilter() != null || filter.getNoteTypeFilter() != null) {
	    String descriptionTypeQueryString = createDescriptionTypeQuery(filter, languageIds);
	    query.setQuery(descriptionTypeQueryString);
	    useAllQuery = false;
	}

	List<DescriptionFilter> descriptionFilters = filter.getDescriptionFilters();

	if (CollectionUtils.isNotEmpty(descriptionFilters)) {
	    String descriptionFilterQueryString = createDescriptionFilterQuery(descriptionFilters, languageIds);
	    query.setQuery(descriptionFilterQueryString);
	    useAllQuery = false;

	}

	return useAllQuery ? createFindAllQuery(query) : query;
    }

    public static SolrQueryBuilder makeBlockJoinChildSolrQueryBuilder() {
	SolrQueryBuilder builder = SolrQueryBuilder.newInstance(BlockJoinChildQParserPlugin.NAME);

	StringBuilder valueBuilder = new StringBuilder();
	valueBuilder.append(SolrParentDocFields.TYPE_INDEX);
	valueBuilder.append(StringConstants.COLON);
	valueBuilder.append(SolrParentDocFields.PARENT);

	builder.setParam("of", valueBuilder.toString());
	return builder;
    }

    public static SolrQueryBuilder makeBlockJoinParentSolrQueryBuilder() {
	SolrQueryBuilder builder = SolrQueryBuilder.newInstance(BlockJoinParentQParserPlugin.NAME);

	StringBuilder valueBuilder = new StringBuilder();
	valueBuilder.append(SolrParentDocFields.TYPE_INDEX);
	valueBuilder.append(StringConstants.COLON);
	valueBuilder.append(SolrParentDocFields.PARENT);

	builder.setParam("which", valueBuilder.toString());
	return builder;
    }

    public static String makeParentFilterQuery() {
	SolrQueryBuilder builder = makeSolrFilterQueryBuilder(false, 0);
	builder.must(SolrParentDocFields.TYPE_INDEX, SolrParentDocFields.PARENT, true);
	return builder.buildQuery();
    }

    private static void addFilterParams(SolrQueryBuilder builder, boolean cache, int cost) {
	if (!cache) {
	    builder.setParam(CommonParams.CACHE, String.valueOf(false));
	    if (cost > 0) {
		builder.setParam(CommonParams.COST, String.valueOf(cost));
	    }
	}
    }

    private static SolrQueryBuilder addParentParameter(SolrQueryBuilder builder) {
	StringBuilder valueBuilder = new StringBuilder();
	valueBuilder.append(SolrParentDocFields.TYPE_INDEX);
	valueBuilder.append(StringConstants.COLON);
	valueBuilder.append(SolrParentDocFields.PARENT);

	builder.setParam(PARENT_FILTER, valueBuilder.toString());

	return builder;
    }

    private static void addSubmissionFilterToSearchQuery(TmgrSearchFilter filter, SolrQueryBuilder builder) {
	List<String> assignees = filter.getAssignees();
	if (assignees != null) {
	    builder.must(SolrChildDocFileds.ASSIGNEE_INDEX, concatStringItemsQuery(assignees, false), false);
	}

	Boolean commited = filter.getCommited();
	if (commited != null) {
	    builder.must(SolrChildDocFileds.COMMITED_INDEX, commited.toString(), true);
	}

	DateFilter dateCompleted = filter.getDateCompletedFilter();
	if (dateCompleted != null) {
	    String startDateString = dateCompleted.getStartDate() != null
		    ? String.valueOf(dateCompleted.getStartDate().getTime())
		    : null;
	    String endDateString = dateCompleted.getEndDate() != null
		    ? String.valueOf(dateCompleted.getEndDate().getTime())
		    : null;
	    builder.must(SolrChildDocFileds.DATE_COMPLETED_INDEX, startDateString, dateCompleted.isStartDateInclusive(),
		    endDateString, dateCompleted.isEndDateInclusive());
	}

	DateFilter dateSubmitted = filter.getDateSubmittedFilter();
	if (dateSubmitted != null) {
	    String startDateString = dateSubmitted.getStartDate() != null
		    ? String.valueOf(dateSubmitted.getStartDate().getTime())
		    : null;
	    String endDateString = dateSubmitted.getEndDate() != null
		    ? String.valueOf(dateSubmitted.getEndDate().getTime())
		    : null;
	    builder.must(SolrChildDocFileds.DATE_SUBMITTED_INDEX, startDateString, dateSubmitted.isStartDateInclusive(),
		    endDateString, dateSubmitted.isEndDateInclusive());
	}

	String submitter = filter.getSubmitter();
	if (submitter != null) {
	    builder.must(SolrChildDocFileds.SUBMITTER_INDEX, submitter, true);
	}
    }

    private static void appendDescriptionField(String languageId, List<String> fields) {

	String attributeField = SolrDocHelper.createDynamicFieldName(SolrParentDocFields.ATTR_PREFIX, languageId,
		SolrChildDocFileds.NGRAM_INDEX_MULTI_SUFFIX);
	String noteField = SolrDocHelper.createDynamicFieldName(SolrChildDocFileds.NOTE_PREFIX, languageId,
		SolrChildDocFileds.NGRAM_INDEX_MULTI_SUFFIX);

	fields.add(attributeField);
	fields.add(noteField);

    }

    private static void appendNotQueries(List<SolrQueryBuilder> notQueries, SolrQueryBuilder builder) {
	if (CollectionUtils.isNotEmpty(notQueries)) {

	    if (StringUtils.isNotEmpty(Objects.toString(builder))) {
		builder.and();
	    }

	    Iterator<SolrQueryBuilder> iterator = notQueries.iterator();

	    while (iterator.hasNext()) {
		builder.shouldNested(iterator.next().buildQuery());
		if (iterator.hasNext()) {
		    builder.and();
		}
	    }
	}
    }

    private static void appendOrQueries(List<SolrQueryBuilder> orQueries, SolrQueryBuilder builder) {
	if (CollectionUtils.isNotEmpty(orQueries)) {

	    if (StringUtils.isNotEmpty(Objects.toString(builder))) {
		builder.and();
	    }

	    Iterator<SolrQueryBuilder> iterator = orQueries.iterator();

	    while (iterator.hasNext()) {
		builder.shouldNested(iterator.next().buildQuery());
		if (iterator.hasNext()) {
		    builder.or();
		}
	    }
	}
    }

    private static String concatStringItemsQuery(Iterable<String> items, boolean inserted) {
	StringBuilder builder = new StringBuilder();

	if (inserted) {
	    builder.append("(");
	}

	boolean isFirst = true;
	for (String item : items) {
	    if (isFirst) {
		isFirst = false;
	    } else {
		builder.append(" ");
	    }
	    builder.append(item);
	}

	if (inserted) {
	    builder.append(")");
	}

	return builder.toString();
    }

    private static String createChildQuery(SolrQuery query, TmgrSearchFilter filter, SolrQueryBuilder builder) {
	handleLanguagesQuery(filter, builder);

	Long userLatestChange = filter.getUserLatestChange();
	if (userLatestChange != null) {
	    builder.must(SolrChildDocFileds.USER_LATEST_CHANGE_INDEX, String.valueOf(userLatestChange), true);
	}

	List<String> statuses = filter.getStatuses();
	if (CollectionUtils.isNotEmpty(statuses)) {
	    if (filter.isOnlyMissingTranslation()) {
		builder.should(SolrChildDocFileds.STATUS_INDEX, concatStringItemsQuery(statuses, false), false);
	    } else {
		builder.must(SolrChildDocFileds.STATUS_INDEX, concatStringItemsQuery(statuses, false), false);
	    }
	}

	List<String> usersModified = filter.getUsersModified();
	if (CollectionUtils.isNotEmpty(usersModified)) {
	    builder.openMust()
		    .should(SolrChildDocFileds.USER_MODIFIED_INDEX, concatStringItemsQuery(usersModified, false), false)
		    .close();
	}

	DateFilter dateModified = filter.getDateModifiedFilter();
	/*
	 * if client app (eg WordFast) is syncing terminology, exclude dateModified
	 * filter.
	 */
	if (dateModified != null && !filter.isSyncSearch()) {
	    String startDateString = dateModified.getStartDate() != null
		    ? String.valueOf(dateModified.getStartDate().getTime())
		    : null;
	    String endDateString = dateModified.getEndDate() != null
		    ? String.valueOf(dateModified.getEndDate().getTime())
		    : null;
	    builder.must(SolrChildDocFileds.DATE_MODIFIED_INDEX, startDateString, dateModified.isStartDateInclusive(),
		    endDateString, dateModified.isEndDateInclusive());
	}

	addSubmissionFilterToSearchQuery(filter, builder);

	return builder.buildQuery();
    }

    private static SolrQueryBuilder createConcordanceTextQuery(TextFilter textFilter, List<String> languageIds,
	    boolean tempTermSearch) {
	ConcordanceType type = textFilter.isExactMatch() ? ConcordanceType.EXACT : ConcordanceType.DEFAULT;
	String prefix = tempTermSearch ? SolrChildDocFileds.TEMP_TERM_NAME_PREFIX : SolrChildDocFileds.TERM_NAME_PREFIX;
	String suffix = SolrChildDocFileds.NGRAM_INDEX_SUFFIX;
	boolean attributeTextSearch = textFilter.isAttributeTextSearch();
	boolean allTextSearch = textFilter.isAllTextSearch();
	List<String> fields = new ArrayList<String>();
	for (String languageId : languageIds) {
	    if (!attributeTextSearch) {
		String termNameField = SolrDocHelper.createDynamicFieldName(prefix, languageId, suffix);
		fields.add(termNameField);
	    }

	    if (allTextSearch || attributeTextSearch) {
		String attributeField = SolrDocHelper.createDynamicFieldName(SolrParentDocFields.ATTR_PREFIX,
			languageId, SolrChildDocFileds.NGRAM_INDEX_MULTI_SUFFIX);
		fields.add(attributeField);
		String noteField = SolrDocHelper.createDynamicFieldName(SolrChildDocFileds.NOTE_PREFIX, languageId,
			SolrChildDocFileds.NGRAM_INDEX_MULTI_SUFFIX);
		fields.add(noteField);
	    }
	}
	SolrQueryBuilder builder = makeConcordanceQueryBuilder(textFilter.getText(), type, textFilter.isCaseSensitive(),
		fields.toArray(new String[fields.size()]));
	return builder;
    }

    private static String createDescriptionFilterQuery(List<DescriptionFilter> descriptionFilters,
	    List<String> languageIds) {

	Map<String, List<SolrQueryBuilder>> descriptionQueries = new HashMap<>();
	descriptionQueries.put(OR_QUERIES, new ArrayList<>());
	descriptionQueries.put(NOT_QUERIES, new ArrayList<>());

	for (DescriptionFilter descriptionFilter : descriptionFilters) {

	    List<String> fields = new ArrayList<>();
	    String query = descriptionFilter.getType();
	    String value = descriptionFilter.getValue();

	    if (StringUtils.isNotEmpty(value)) {
		query = query + StringConstants.SPACE + value;
	    }

	    for (String languageId : languageIds) {
		appendDescriptionField(languageId, fields);
	    }

	    SolrQueryBuilder builder = makeConcordanceQueryBuilder(query, ConcordanceType.PHRASE,
		    descriptionFilter.isCaseSensitive(), fields.toArray(new String[fields.size()]));
	    addParentParameter(builder);

	    if (descriptionFilter.isNot()) {
		descriptionQueries.get(NOT_QUERIES).add(builder);
	    } else {
		descriptionQueries.get(OR_QUERIES).add(builder);
	    }
	}

	return createDescriptionQueryString(descriptionQueries);

    }

    private static String createDescriptionQueryString(Map<String, List<SolrQueryBuilder>> descriptionQueries) {

	SolrQueryBuilder builder = SolrQueryBuilder.newInstance();

	appendOrQueries(descriptionQueries.get(OR_QUERIES), builder);
	appendNotQueries(descriptionQueries.get(NOT_QUERIES), builder);

	return builder.buildQuery();
    }

    private static String createDescriptionTypeQuery(TmgrSearchFilter filter, List<String> languageIds) {
	ConcordanceType type = ConcordanceType.EXACT;

	List<String> fields = new ArrayList<String>();

	StringBuilder textBuilder = new StringBuilder();

	Set<String> attributeTypeFilter = filter.getAttributeTypeFilter();
	if (attributeTypeFilter != null) {
	    for (String languageId : languageIds) {
		fields.add(makeFullDynamicFieldName(SolrParentDocFields.ATTR_PREFIX,
			SolrChildDocFileds.NGRAM_INDEX_MULTI_SUFFIX, languageId));
	    }

	    for (String attType : attributeTypeFilter) {
		textBuilder.append(attType);
		textBuilder.append(StringConstants.SPACE);
	    }
	}

	Set<String> noteTypeFilter = filter.getNoteTypeFilter();
	if (noteTypeFilter != null) {
	    for (String languageId : languageIds) {
		fields.add(makeFullDynamicFieldName(SolrChildDocFileds.NOTE_PREFIX,
			SolrChildDocFileds.NGRAM_INDEX_MULTI_SUFFIX, languageId));
	    }
	    for (String noteType : noteTypeFilter) {
		textBuilder.append(noteType);
		textBuilder.append(StringConstants.SPACE);
	    }
	}

	SolrQueryBuilder builder = makeConcordanceQueryBuilder(textBuilder.toString(), type, true,
		fields.toArray(new String[fields.size()]));
	addParentParameter(builder);

	return builder.buildQuery();
    }

    private static SolrQueryBuilder createSegmentTextQuery(TextFilter textFilter, List<String> languageIds) {

	boolean fuzzy = textFilter.isFuzzyMatch();
	List<String> fields = new ArrayList<String>();
	String prefix = SolrChildDocFileds.TERM_NAME_PREFIX;
	String suffix = fuzzy ? SolrChildDocFileds.SUB_FUZZY_SUFFIX : SolrChildDocFileds.SUB_SUFFIX;

	for (String languageId : languageIds) {
	    String fieldName = SolrDocHelper.createDynamicFieldName(prefix, languageId, suffix);
	    fields.add(fieldName);
	}

	SolrQueryBuilder builder = makeSegmentSearchQueryBuilder(textFilter.getText(),
		fields.toArray(new String[fields.size()]));
	return builder;

    }

    private static String createTextQuery(TmgrSearchFilter filter, TextFilter textFilter, List<String> languageIds) {

	SolrQueryBuilder builder = null;
	if (textFilter.isSegmentSearch()) {
	    builder = createSegmentTextQuery(textFilter, languageIds);
	} else {
	    builder = createConcordanceTextQuery(textFilter, languageIds, filter.isTempTermSearch());
	}

	addParentParameter(builder);

	return builder.buildQuery();
    }

    private static void handleLanguagesQuery(TmgrSearchFilter filter, SolrQueryBuilder builder) {
	/** The line bellow is commented because of this issue: TERII-5705 */
	// if (filter.isOnlyMissingTranslation() || filter.isHideBlanks()) {
	if (filter.isOnlyMissingTranslation()) {
	    return;
	}

	String sourceLanguage = filter.getSourceLanguage();
	List<String> targetLanguages = filter.getTargetLanguages();

	String languageIdField = SolrChildDocFileds.LANGUAGE_ID_INDEX;

	if (filter.isMultiLingual()) {
	    builder.openMust().should(languageIdField, sourceLanguage, true)
		    .should(languageIdField, concatStringItemsQuery(targetLanguages, false), false).close();
	} else if (filter.isSourceSearch()) {
	    builder.must(languageIdField, sourceLanguage, true);
	} else if (filter.isTargetSearch()) {
	    builder.openMust().should(languageIdField, concatStringItemsQuery(targetLanguages, false), false).close();
	}
    }

    private static SolrQueryBuilder makeConcordanceQueryBuilder(String query, ConcordanceType type,
	    boolean caseSensitive, String... fieldNames) {
	SolrQueryBuilder builder = SolrQueryBuilder.newInstance(ConcordanceQueryPlugin.NAME);
	builder.setParam(ConcordanceQueryPlugin.CASE_SENSITIVE_PARAM, String.valueOf(caseSensitive));
	builder.setParam(ConcordanceQueryPlugin.TYPE_PARAM, type.toString());
	builder.setParam(CommonParams.DF, fieldNames);
	builder.setQuery(query, false);
	return builder;
    }

    private static String makeFilterQuery(Map<String, String> filter, SolrQueryBuilder builder) {

	for (Entry<String, String> entry : filter.entrySet()) {
	    String fieldName = entry.getKey();
	    String value = entry.getValue();
	    if (value.contains(StringConstants.COMMA)) {
		String[] values = value.split(StringConstants.COMMA);
		for (String v : values) {
		    builder.should(fieldName, v, false);
		}
	    } else {
		builder.must(fieldName, value, true);
	    }

	}

	return builder.buildQuery();
    }

    private static String makeFullDynamicFieldName(String prefix, String suffix, String localeLanguageCode) {
	String result = new StringBuilder(prefix).append(SEPARATOR).append(localeLanguageCode).append(SEPARATOR)
		.append(suffix).toString();
	return result;
    }

    private static SolrQueryBuilder makeSegmentSearchQueryBuilder(String query, String... fieldNames) {
	SolrQueryBuilder builder = SolrQueryBuilder.newInstance(SubdocumentSearchQueryPlugin.NAME);
	builder.setParam(CommonParams.DF, fieldNames);
	builder.setQuery(query, false);
	return builder;

    }

    private static SolrQueryBuilder makeSolrFilterQueryBuilder(boolean cache, int cost) {
	return makeSolrFilterQueryBuilder(null, cache, cost);

    }

    private static SolrQueryBuilder makeSolrFilterQueryBuilder(String parserName, boolean cache, int cost) {
	SolrQueryBuilder builder = SolrQueryBuilder.newInstance(parserName);
	addFilterParams(builder, cache, cost);
	return builder;
    }
}
