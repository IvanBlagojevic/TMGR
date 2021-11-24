package org.gs4tr.termmanager.persistence.solr.converter;

import static java.util.Objects.nonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.persistence.solr.faceting.FacetTermCounts;
import org.gs4tr.termmanager.persistence.solr.faceting.FacetTermCounts.LanguageTermCount;

public class PivotFieldConverter {

    public static FacetTermCounts convertToFacetTermCounts(NamedList<List<PivotField>> namedList,
	    List<String> languageIds) {

	FacetTermCounts termCounts = new FacetTermCounts(languageIds);
	if (namedList == null) {
	    return termCounts;
	}

	for (final Entry<String, List<PivotField>> entry : namedList) {
	    List<PivotField> pivotFields = entry.getValue();
	    if (CollectionUtils.isEmpty(pivotFields)) {
		continue;
	    }
	    Map<String, LanguageTermCount> termCountMap = termCounts.getTermCountByLanguage();
	    for (final PivotField pivotField : pivotFields) {
		LanguageTermCount termCount = termCountMap.get(pivotField.getValue());
		termCount.setTermCount(pivotField.getCount());
		List<PivotField> nestedPivot = pivotField.getPivot();
		if (CollectionUtils.isEmpty(nestedPivot)) {
		    continue;
		}
		buildTermCountByStatus(nestedPivot, termCount);
	    }
	}
	return termCounts;
    }

    public static Map<Long, Long> convertToTermEntriesCount(List<Long> projectIds, QueryResponse response) {

	Map<Long, Long> termEntriesCount = groupByProjectId(projectIds);

	List<FacetField> facetFields = response.getFacetFields();
	FacetField facetField = facetFields.get(0);
	List<Count> counts = facetField.getValues();
	if (CollectionUtils.isEmpty(counts)) {
	    return termEntriesCount;
	}
	for (final Count count : counts) {
	    Long projectId = Long.valueOf(count.getName());
	    if (nonNull(termEntriesCount.get(projectId))) {
		termEntriesCount.put(projectId, count.getCount());
	    }
	}

	return termEntriesCount;
    }

    private static Map<String, BiConsumer<LanguageTermCount, Integer>> _converters = initializeConverters();

    private PivotFieldConverter() {
    }

    private static void buildTermCountByStatus(List<PivotField> nestedPivot, LanguageTermCount termCount) {
	for (final PivotField pivotField : nestedPivot) {
	    BiConsumer<LanguageTermCount, Integer> converter = _converters.get(pivotField.getValue());
	    converter.accept(termCount, pivotField.getCount());
	}
    }

    private static Map<Long, Long> groupByProjectId(List<Long> projectIds) {
	return projectIds.stream().collect(toMap(identity(), (projectId) -> Long.valueOf(0)));
    }

    private static Map<String, BiConsumer<LanguageTermCount, Integer>> initializeConverters() {
	Map<String, BiConsumer<LanguageTermCount, Integer>> converters = new HashMap<>();

	converters.put(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(),
		(termCount, count) -> termCount.setInTranslationReview(count));
	converters.put(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(),
		(termCount, count) -> termCount.setInFinalReview(count));
	converters.put(ItemStatusTypeHolder.PROCESSED.getName(), (termCount, count) -> termCount.setApproved(count));
	converters.put(ItemStatusTypeHolder.BLACKLISTED.getName(), (termCount, count) -> termCount.setForbidden(count));
	converters.put(ItemStatusTypeHolder.WAITING.getName(), (termCount, count) -> termCount.setPending(count));
	converters.put(ItemStatusTypeHolder.ON_HOLD.getName(), (termCount, count) -> termCount.setOnHold(count));
	return converters;
    }
}
