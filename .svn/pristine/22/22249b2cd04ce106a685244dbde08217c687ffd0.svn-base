package org.gs4tr.termmanager.glossaryV2.blacklist;

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
import org.gs4tr.termmanager.glossaryV2.AbstractV2GlossaryServiceTest;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TextFilter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.tm3.api.LiteSegment;
import org.gs4tr.tm3.api.LiteSegmentSearchQuery;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TermsHolder;
import org.gs4tr.tm3.api.blacklist.BlacklistTerm;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrBlacklistOperations;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

@TestSuite("glossary")
public class BlacklistSegmentSearchTest extends AbstractV2GlossaryServiceTest {

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

    private static final String[] EXPECTED_STATUSES = new String[] { ItemStatusTypeHolder.BLACKLISTED.getName() };

    private static final String EXPECTED_SUGGESTION_TEXT_1 = "suggestion source term 1";

    private static final String EXPECTED_SUGGESTION_TEXT_2 = "suggestion source term 2";

    private static final String EXPECTED_SUGGESTION_TEXT_3 = "suggestion source term 3";

    private static final int LENGTH = 50;

    private static final int OFFSET = 0;

    private static final String USER = "sdulin";

    private ITmgrBlacklistOperations _blacklistOperations;

    @Captor
    private ArgumentCaptor<TmgrSearchFilter> _searchFilterCaptor;

    public ITmgrBlacklistOperations getBlacklistOperations() {
	return _blacklistOperations;
    }

    @Before
    public void setUp() throws OperationsException {
	_blacklistOperations = getBlacklistFactory().getOperations(createKey());
    }

    @Test
    public void testBlacklistSegmentSearchWhenLiteSegmentListIsEmpty() throws Exception {
	LiteSegmentSearchQuery query = new LiteSegmentSearchQuery();
	List<TermsHolder<BlacklistTerm>> results = getBlacklistOperations().segmentSearch(query);
	assertNotNull(results);
	assertTrue(results.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBlacklistSegmentSearchWhenLiteSegmentSearchQueryIsNull() throws Exception {
	getBlacklistOperations().segmentSearch(null);
    }

    @Test
    @TestCase("blacklistSegmentSearch")
    public void testSegmentSearchWhenLanguageTermsMapIsNull() throws Exception {
	LiteSegmentSearchQuery query = getModelObject("query1", LiteSegmentSearchQuery.class);

	List<TermEntry> termEntries = Collections.singletonList(new TermEntry());

	when(getTermEntryService().segmentTMSearch(any(TmgrSearchFilter.class)))
		.thenReturn(new Page<TermEntry>(termEntries.size(), OFFSET, LENGTH, termEntries));

	List<TermsHolder<BlacklistTerm>> results = getBlacklistOperations().segmentSearch(query);

	assertTrue(!results.isEmpty());

	List<LiteSegment> segments = query.getSegments();
	assertEquals(segments.size(), results.size());

	assertTrue(CollectionUtils.isEmpty(results.get(0).getResults()));
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("blacklistSegmentSearch")
    public void testSegmentSearchWhenLiteSegmentListHasMultipleSegments() throws Exception {
	LiteSegmentSearchQuery query = getModelObject("query2", LiteSegmentSearchQuery.class);

	List<TermEntry> termEntries1 = getModelObject("termEntries1", List.class);
	List<TermEntry> termEntries2 = getModelObject("termEntries2", List.class);

	Page<TermEntry> page1 = new Page<TermEntry>(termEntries1.size(), OFFSET, LENGTH, termEntries1);
	Page<TermEntry> page2 = new Page<TermEntry>(termEntries2.size(), OFFSET, LENGTH, termEntries2);

	when(getTermEntryService().segmentTMSearch(any(TmgrSearchFilter.class))).thenReturn(page1, page2);

	List<TermsHolder<BlacklistTerm>> results = getBlacklistOperations().segmentSearch(query);

	verify(getTermEntryService(), times(2)).segmentTMSearch(getSearchFilterCaptor().capture());

	List<TmgrSearchFilter> searchFilters = getSearchFilterCaptor().getAllValues();
	TmgrSearchFilter searchFilter1 = searchFilters.get(0);
	validateBasicSearchFilter(searchFilter1);
	TmgrSearchFilter searchFilter2 = searchFilters.get(1);
	validateBasicSearchFilter(searchFilter2);

	boolean fuzzy = query.isFuzzySearchEnabled();

	assertEquals(fuzzy, !searchFilter1.getTextFilter().isExactMatch());
	assertEquals(fuzzy, !searchFilter2.getTextFilter().isExactMatch());

	assertTrue(!results.isEmpty());

	TermsHolder<BlacklistTerm> blacklistTerms1 = results.get(0);
	TermsHolder<BlacklistTerm> blacklistTerms2 = results.get(1);

	assertTrue(CollectionUtils.isNotEmpty(blacklistTerms1.getResults()));
	assertTrue(CollectionUtils.isNotEmpty(blacklistTerms2.getResults()));

	assertEquals(1, blacklistTerms1.getResults().size());
	assertEquals(1, blacklistTerms2.getResults().size());

	BlacklistTerm result1 = blacklistTerms1.getResults().get(0);
	assertNotNull(result1.getModificationDate());
	assertNotNull(result1.getCreationDate());
	assertEquals(USER, result1.getModificationUser());
	assertEquals(USER, result1.getCreationUser());

	validateExpectedSourceCode(result1);
	validateSuggestions(new String[] { EXPECTED_SUGGESTION_TEXT_1 }, result1);

	BlacklistTerm result2 = blacklistTerms2.getResults().get(0);
	assertNotNull(result2.getModificationDate());
	assertNotNull(result2.getCreationDate());
	assertEquals(USER, result2.getModificationUser());
	assertEquals(USER, result2.getCreationUser());

	validateExpectedSourceCode(result2);
	validateSuggestions(new String[] { EXPECTED_SUGGESTION_TEXT_2, EXPECTED_SUGGESTION_TEXT_3 }, result2);
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("blacklistSegmentSearch")
    public void testSegmentSearchWhenLiteSegmentListHasOneSegment() throws Exception {
	LiteSegmentSearchQuery query = getModelObject("query1", LiteSegmentSearchQuery.class);

	List<TermEntry> termEntries = getModelObject("termEntries1", List.class);

	when(getTermEntryService().segmentTMSearch(any(TmgrSearchFilter.class)))
		.thenReturn(new Page<TermEntry>(termEntries.size(), OFFSET, LENGTH, termEntries));

	List<TermsHolder<BlacklistTerm>> results = getBlacklistOperations().segmentSearch(query);

	verify(getTermEntryService()).segmentTMSearch(getSearchFilterCaptor().capture());

	TmgrSearchFilter searchFilter = getSearchFilterCaptor().getValue();
	validateBasicSearchFilter(searchFilter);

	LiteSegment liteSegment = query.getSegments().get(0);
	TextFilter textFilter = searchFilter.getTextFilter();

	assertEquals(liteSegment.getSource(), textFilter.getText());
	assertEquals(query.isFuzzySearchEnabled(), !textFilter.isExactMatch());

	List<LiteSegment> segments = query.getSegments();
	assertEquals(segments.size(), results.size());

	TermsHolder<BlacklistTerm> blacklistTerms = results.get(0);
	assertEquals(1, blacklistTerms.getResults().size());

	BlacklistTerm result = blacklistTerms.getResults().get(0);
	assertNotNull(result.getModificationDate());
	assertNotNull(result.getCreationDate());
	assertEquals(USER, result.getModificationUser());
	assertEquals(USER, result.getCreationUser());

	validateExpectedSourceCode(result);
	validateSuggestions(new String[] { EXPECTED_SUGGESTION_TEXT_1 }, result);
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

    private void validateExpectedSourceCode(BlacklistTerm result) {
	assertEquals(EXPECTED_SOURCE_CODE, result.getLocale().getCode());
    }

    private void validateSuggestions(String[] expectedSuggestions, BlacklistTerm result) {
	String[] actualSuggestions = result.getSuggestions();
	assertArrayEquals(expectedSuggestions, actualSuggestions);
    }
}