package org.gs4tr.termmanager.glossaryV2;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.tm3.api.LiteSegment;
import org.gs4tr.tm3.api.LiteSegmentSearchQuery;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TermsHolder;
import org.gs4tr.tm3.api.glossary.Term;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrGlossaryOperations;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

@TestSuite("glossary")
public class SegmentSearchTest extends AbstractV2GlossaryServiceTest {

    private static final Long[] EXPECTED_PROJECT_IDS = { 1L };

    private static final String[] EXPECTED_RESULT_FIELDS = new String[] { "en-US_termName_STRING_STORE_SORT",
	    "en-US_ID_STRING_BASIC_SORT", "en-US_userCreated_STRING_BASIC_SORT", "en-US_status_STRING_BASIC_SORT",
	    "en-US1_termName_STRING_STORE", "en-US1_userCreated_STRING_STORE", "en-US1_status_STRING_STORE",
	    "en-US1_ID_STRING_STORE", "en-US_assignee_STRING_BASIC_SORT", "en-US1_disabled_BOOL_STORE",
	    "en-US1_forbidden_BOOL_STORE", "en-US1_inTranslationAsSource_BOOL_STORE", "en-US_disabled_BOOL_STORE",
	    "en-US_forbidden_BOOL_STORE", "en-US_inTranslationAsSource_BOOL_STORE", "en-US_dateCompleted_LONG_SORT",
	    "en-US_dateSubmitted_LONG_SORT", "en-US_tempTermName_STRING_STORE_SORT",
	    "en-US_comment_STRING_STORE_MULTI", "en-US_deleted_STRING_STORE_MULTI", "en-US1_deleted_STRING_STORE_MULTI" };

    private static final String EXPECTED_SOURCE_CODE = Locale.US.getCode();

    private static final String[] EXPECTED_STATUSES = new String[] { ItemStatusTypeHolder.ON_HOLD.getName(),
	    ItemStatusTypeHolder.PROCESSED.getName(), ItemStatusTypeHolder.WAITING.getName() };

    private static final int LENGTH = 50;

    private static final int OFFSET = 0;

    private ITmgrGlossaryOperations _glossaryOperations;

    @Captor
    private ArgumentCaptor<TmgrSearchFilter> _searchFilterCaptor;

    @Before
    public void setUp() throws OperationsException {
	_glossaryOperations = getGlossaryFactory().getOperations(createKey());
    }

    @Test
    @TestCase("segmentSearch")
    public void testSegmentSearchWhenLanguageTermsMapIsNull() throws Exception {
	LiteSegmentSearchQuery query = getModelObject("query1", LiteSegmentSearchQuery.class);

	List<TermEntry> termEntries = Collections.singletonList(new TermEntry());

	when(getTermEntryService().segmentTMSearch(any(TmgrSearchFilter.class)))
		.thenReturn(new Page<TermEntry>(termEntries.size(), OFFSET, LENGTH, termEntries));

	List<TermsHolder<Term>> results = getGlossaryOperations().segmentSearch(query);

	assertTrue(CollectionUtils.isNotEmpty(results));

	List<LiteSegment> segments = query.getSegments();
	assertEquals(segments.size(), results.size());

	assertTrue(CollectionUtils.isEmpty(results.get(0).getResults()));
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("segmentSearch")
    public void testSegmentSearchWhenLiteSegmentListHasMultipleSegments() throws Exception {
	LiteSegmentSearchQuery query = getModelObject("query2", LiteSegmentSearchQuery.class);

	List<TermEntry> termEntries1 = getModelObject("termEntries1", List.class);
	List<TermEntry> termEntries2 = getModelObject("termEntries2", List.class);

	Page<TermEntry> page1 = new Page<TermEntry>(termEntries1.size(), OFFSET, LENGTH, termEntries1);
	Page<TermEntry> page2 = new Page<TermEntry>(termEntries2.size(), OFFSET, LENGTH, termEntries2);

	when(getTermEntryService().segmentTMSearch(any(TmgrSearchFilter.class))).thenReturn(page1, page2);

	List<TermsHolder<Term>> results = getGlossaryOperations().segmentSearch(query);

	verify(getTermEntryService(), times(2)).segmentTMSearch(getSearchFilterCaptor().capture());

	List<TmgrSearchFilter> searchFilters = getSearchFilterCaptor().getAllValues();
	TmgrSearchFilter searchFilter1 = searchFilters.get(0);
	validateBasicSearchFilter(searchFilter1);
	TmgrSearchFilter searchFilter2 = searchFilters.get(1);
	validateBasicSearchFilter(searchFilter2);

	List<LiteSegment> segments = query.getSegments();
	LiteSegment liteSegment1 = segments.get(0);
	LiteSegment liteSegment2 = segments.get(1);
	boolean fuzzy = query.isFuzzySearchEnabled();

	assertEquals(fuzzy, !searchFilter1.getTextFilter().isExactMatch());
	assertEquals(fuzzy, !searchFilter2.getTextFilter().isExactMatch());

	assertTrue(CollectionUtils.isNotEmpty(results));
	assertEquals(segments.size(), results.size());

	TermsHolder<Term> bilingualTerms1 = results.get(0);
	TermsHolder<Term> bilingualTerms2 = results.get(1);

	assertTrue(CollectionUtils.isNotEmpty(bilingualTerms1.getResults()));
	assertTrue(CollectionUtils.isNotEmpty(bilingualTerms2.getResults()));

	assertEquals(1, bilingualTerms1.getResults().size());
	assertEquals(2, bilingualTerms2.getResults().size());

	Term result1 = bilingualTerms1.getResults().get(0);

	assertEquals(liteSegment1.getSource(), result1.getSource());
	assertEquals(liteSegment1.getTarget(), result1.getTarget());
	assertEquals(liteSegment1.getSourceCode(), result1.getSourceLocale().getCode());
	assertEquals(liteSegment1.getTargetCode(), result1.getTargetLocale().getCode());

	Term result2 = bilingualTerms2.getResults().get(0);

	assertEquals(liteSegment2.getSource(), result2.getSource());
	assertEquals(liteSegment2.getTarget(), result2.getTarget());
	assertEquals(liteSegment2.getSourceCode(), result2.getSourceLocale().getCode());
	assertEquals(liteSegment2.getTargetCode(), result2.getTargetLocale().getCode());

	Term result3 = bilingualTerms2.getResults().get(1);

	assertEquals(liteSegment2.getSource(), result3.getSource());
	assertEquals(liteSegment2.getSourceCode(), result3.getSourceLocale().getCode());
	assertEquals(liteSegment2.getTargetCode(), result3.getTargetLocale().getCode());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("segmentSearch")
    public void testSegmentSearchWhenLiteSegmentListHasOneSegment() throws Exception {
	LiteSegmentSearchQuery query = getModelObject("query1", LiteSegmentSearchQuery.class);

	List<TermEntry> termEntries = getModelObject("termEntries1", List.class);

	when(getTermEntryService().segmentTMSearch(any(TmgrSearchFilter.class)))
		.thenReturn(new Page<TermEntry>(termEntries.size(), OFFSET, LENGTH, termEntries));

	List<TermsHolder<Term>> results = getGlossaryOperations().segmentSearch(query);

	verify(getTermEntryService()).segmentTMSearch(getSearchFilterCaptor().capture());

	TmgrSearchFilter searchFilter = getSearchFilterCaptor().getValue();
	validateBasicSearchFilter(searchFilter);

	List<LiteSegment> segments = query.getSegments();
	assertEquals(segments.size(), results.size());

	LiteSegment liteSegment = segments.get(0);

	assertEquals(liteSegment.getSource(), searchFilter.getTextFilter().getText());
	assertEquals(query.isFuzzySearchEnabled(), !searchFilter.getTextFilter().isExactMatch());

	TermsHolder<Term> bilingualTerms = results.get(0);
	assertEquals(1, bilingualTerms.getResults().size());

	Term result = bilingualTerms.getResults().get(0);

	assertEquals(liteSegment.getSource(), result.getSource());
	assertEquals(liteSegment.getTarget(), result.getTarget());
	assertEquals(liteSegment.getSourceCode(), result.getSourceLocale().getCode());
	assertEquals(liteSegment.getTargetCode(), result.getTargetLocale().getCode());
    }

    @Test
    public void testSegmentSearchWhenLiteSegmentListIsEmpty() throws Exception {
	LiteSegmentSearchQuery query = new LiteSegmentSearchQuery();
	List<TermsHolder<Term>> results = getGlossaryOperations().segmentSearch(query);
	assertNotNull(results);
	assertTrue(results.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSegmentSearchWhenLiteSegmentSearchQueryIsNull() throws Exception {
	getGlossaryOperations().segmentSearch(null);
    }

    private ITmgrGlossaryOperations getGlossaryOperations() {
	return _glossaryOperations;
    }

    private ArgumentCaptor<TmgrSearchFilter> getSearchFilterCaptor() {
	return _searchFilterCaptor;
    }

    private void validateBasicSearchFilter(TmgrSearchFilter searchFilter) {
	assertEquals(EXPECTED_SOURCE_CODE, searchFilter.getSourceLanguage());
	assertTrue(CollectionUtils.isEmpty(searchFilter.getTargetLanguages()));

	assertArrayEquals(EXPECTED_PROJECT_IDS, searchFilter.getProjectIds().toArray());
	assertArrayEquals(EXPECTED_STATUSES, searchFilter.getStatuses().toArray());
	assertEquals(LENGTH, searchFilter.getPageable().getPageSize());

	assertTrue(Arrays.stream(EXPECTED_RESULT_FIELDS).allMatch(searchFilter.getResultFields()::contains));

    }
}