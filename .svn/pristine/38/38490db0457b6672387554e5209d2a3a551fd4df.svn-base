package org.gs4tr.termmanager.persistence.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossarySearcher;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.termmanager.persistence.solr.faceting.FacetTermCounts;
import org.gs4tr.termmanager.persistence.solr.faceting.FacetTermCounts.LanguageTermCount;
import org.gs4tr.termmanager.persistence.solr.query.AbstractPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.IPageable;
import org.gs4tr.termmanager.persistence.solr.query.Sort.Direction;
import org.gs4tr.termmanager.persistence.solr.query.TextFilter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.solr.plugin.utils.SolrDocHelper;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.QueryMatchLocation;
import org.gs4tr.tm3.api.TmException;
import org.gs4tr.tm3.api.glossary.GlossaryConcordanceQuery;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class TmgrGlossarySearcherTest extends AbstractSolrGlossaryTest {

    @Test
    public void testConcordanceSearch() throws TmException {
	addTermEntry();

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	Long projectId = 1L;

	Locale sourceLocale = Locale.ENGLISH;
	Locale targetLocale = Locale.makeLocale("sr");

	GlossaryConcordanceQuery glossaryQuery = new GlossaryConcordanceQuery();
	glossaryQuery.setQuery("language");
	glossaryQuery.setCaseSensitive(false);
	glossaryQuery.setExactMatch(true);
	glossaryQuery.setMaxResults(50);
	glossaryQuery.setOffset(0);
	glossaryQuery.setLocation(QueryMatchLocation.SOURCE_AND_TARGET);
	TmgrSearchFilter filter = new TmgrSearchFilter(glossaryQuery);
	filter.setSourceLanguage(sourceLocale.getCode());
	filter.setTargetLanguage(targetLocale.getCode());
	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(projectId);
	filter.setProjectIds(projectIds);
	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);

	List<TermEntry> results = page.getResults();
	Assert.assertTrue(CollectionUtils.isNotEmpty(results));

	Assert.assertEquals(1, results.size());
    }

    @Test
    public void testGetNumberOfTermEntriesOnOTwoProject() throws TmException {

	// Add new term entries and set project id 1L
	addTwoTermEntries();

	// Add new term entries and set project id 2L
	addNewTermEntriesOnSecondProject();

	List<String> languageIds = Arrays.asList("en");

	TmgrSearchFilter searchFilter = new TmgrSearchFilter();

	searchFilter.setProjectIds(Arrays.asList(1L, 2L));

	searchFilter.setTargetLanguages(languageIds);
	searchFilter.addLanguageResultField(true, SYNONYM_NUMBER, languageIds.toArray(new String[languageIds.size()]));

	ITmgrGlossaryConnector regularConnector = getRegularConnector();

	ITmgrGlossarySearcher searcher = regularConnector.getTmgrSearcher();

	Map<Long, Long> termEntriesCount = searcher.getNumberOfTermEntries(searchFilter);

	assertNotNull(termEntriesCount);
	assertEquals(2, termEntriesCount.size());

	for (Entry<Long, Long> entry : termEntriesCount.entrySet()) {
	    int projectId = entry.getKey().intValue();
	    int termEntryCount = entry.getValue().intValue();
	    switch (projectId) {
	    case 1:
		assertEquals(1, termEntryCount);
		break;
	    case 2:
		assertEquals(0, termEntryCount);
		break;
	    default:
		throw new IllegalArgumentException("Illegal projectId.");
	    }
	}
	List<String> newLanguageIds = Arrays.asList("en-US", "de-DE", "fr");

	TmgrSearchFilter newFilter = new TmgrSearchFilter();

	newFilter.setProjectIds(Arrays.asList(1L, 2L));

	newFilter.setTargetLanguages(newLanguageIds);
	newFilter.addLanguageResultField(true, SYNONYM_NUMBER,
		newLanguageIds.toArray(new String[newLanguageIds.size()]));

	termEntriesCount = searcher.getNumberOfTermEntries(newFilter);
	assertNotNull(termEntriesCount);
	assertEquals(2, termEntriesCount.size());

	for (Entry<Long, Long> entry : termEntriesCount.entrySet()) {
	    int projectId = entry.getKey().intValue();
	    int termEntryCount = entry.getValue().intValue();
	    switch (projectId) {
	    case 1:
		assertEquals(2, termEntryCount);
		break;
	    case 2:
		assertEquals(4, termEntryCount);
		break;
	    default:
		throw new IllegalArgumentException("Illegal projectId.");
	    }
	}
    }

    @Test
    public void testGetNumberOfTermEntriesOnOneProject() throws TmException {

	// Add new term entries and set project id 1L
	addTwoTermEntries();

	List<String> languageIds = Arrays.asList("de-DE");

	TmgrSearchFilter searchFilter = new TmgrSearchFilter();

	searchFilter.setProjectIds(Arrays.asList(1L));

	searchFilter.setTargetLanguages(languageIds);
	searchFilter.addLanguageResultField(true, SYNONYM_NUMBER, languageIds.toArray(new String[languageIds.size()]));

	ITmgrGlossaryConnector regularConnector = getRegularConnector();

	ITmgrGlossarySearcher searcher = regularConnector.getTmgrSearcher();

	Map<Long, Long> termEntriesCount = searcher.getNumberOfTermEntries(searchFilter);

	assertNotNull(termEntriesCount);
	assertEquals(1, termEntriesCount.size());

	Iterator<Entry<Long, Long>> it = termEntriesCount.entrySet().iterator();

	Entry<Long, Long> entry = it.next();
	assertEquals(Long.valueOf(1), entry.getKey());
	assertEquals(Long.valueOf(1), entry.getValue());

	List<String> newLanguageIds = Arrays.asList("en", "de-DE", "sr");

	TmgrSearchFilter newFilter = new TmgrSearchFilter();

	newFilter.setProjectIds(Arrays.asList(1L));

	newFilter.setTargetLanguages(newLanguageIds);
	newFilter.addLanguageResultField(true, SYNONYM_NUMBER,
		newLanguageIds.toArray(new String[newLanguageIds.size()]));

	termEntriesCount = searcher.getNumberOfTermEntries(newFilter);
	assertNotNull(termEntriesCount);
	assertEquals(1, termEntriesCount.size());

	it = termEntriesCount.entrySet().iterator();

	entry = it.next();
	assertEquals(Long.valueOf(1), entry.getKey());
	assertEquals(Long.valueOf(2), entry.getValue());
    }

    @Test
    public void testGetNumberOfTermEntriesWrongLanguageIds() throws TmException {

	List<String> languageIds = Arrays.asList("en-US", "de-DE");

	TmgrSearchFilter searchFilter = new TmgrSearchFilter();

	searchFilter.setProjectIds(Arrays.asList(1L));

	searchFilter.setTargetLanguages(languageIds);
	searchFilter.addLanguageResultField(true, SYNONYM_NUMBER, languageIds.toArray(new String[languageIds.size()]));

	ITmgrGlossaryConnector regularConnector = getRegularConnector();

	ITmgrGlossarySearcher searcher = regularConnector.getTmgrSearcher();

	Map<Long, Long> termEntriesCount = searcher.getNumberOfTermEntries(searchFilter);

	assertNotNull(termEntriesCount);
	assertEquals(1, termEntriesCount.size());

	for (Entry<Long, Long> entry : termEntriesCount.entrySet()) {
	    assertEquals(Long.valueOf(1), entry.getKey());
	    assertEquals(Long.valueOf(0), entry.getValue());
	}
    }

    @Test
    public void testGetNumberOfTerms() throws TmException {
	addBlacklistTermEntry();

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	long projectId = 1L;

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(projectId);

	String languageId = "sr";
	filter.setSourceLanguage(languageId);

	List<String> statuses = new ArrayList<String>();
	statuses.add(ItemStatusTypeHolder.BLACKLISTED.getName());

	filter.setStatuses(statuses);

	long number = searcher.getNumberOfTerms(filter);

	Assert.assertEquals(2, number);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNumbersOfTermEntriesWithEmptyFilter() throws TmException {

	TmgrSearchFilter searchFilter = new TmgrSearchFilter();

	ITmgrGlossaryConnector regularConnector = getRegularConnector();

	ITmgrGlossarySearcher searcher = regularConnector.getTmgrSearcher();

	searcher.getNumberOfTermEntries(searchFilter);
    }

    @Test
    public void testGetNumbersOfTermEntriesWrongLanguageIds() throws TmException {

	List<String> languageIds = Arrays.asList("en-US", "de-DE");

	TmgrSearchFilter searchFilter = new TmgrSearchFilter();

	searchFilter.setProjectIds(Arrays.asList(1L));

	searchFilter.setTargetLanguages(languageIds);
	searchFilter.addLanguageResultField(true, SYNONYM_NUMBER, languageIds.toArray(new String[languageIds.size()]));

	ITmgrGlossaryConnector regularConnector = getRegularConnector();

	ITmgrGlossarySearcher searcher = regularConnector.getTmgrSearcher();

	FacetTermCounts termCounts = searcher.searchFacetTermCounts(searchFilter);

	Assert.assertNotNull(termCounts);

	Map<String, LanguageTermCount> termCountMap = termCounts.getTermCountByLanguage();

	Assert.assertEquals(2, termCountMap.size());

	for (Entry<String, LanguageTermCount> entry : termCountMap.entrySet()) {
	    LanguageTermCount termCount = entry.getValue();
	    assertEquals(0, termCount.getTermCount());
	    assertEquals(0, termCount.getApproved());
	    assertEquals(0, termCount.getForbidden());
	    assertEquals(0, termCount.getInFinalReview());
	    assertEquals(0, termCount.getInTranslationReview());
	}
    }

    // TERII-5705 - Status filter does not work when applying hide blanks
    @Test
    public void testHideBlanksWithStatusFilter() throws TmException {
	long projectId = 5L;
	List<String> languages = Arrays.asList("en", "zh-HK");
	String pending = ItemStatusTypeHolder.WAITING.getName();
	String processed = ItemStatusTypeHolder.PROCESSED.getName();

	TermEntry entry1 = new TermEntry(projectId, "TES000005", SUPER_USER);
	entry1.setUuId(UUID.randomUUID().toString());
	entry1.addTerm(createTerm("en", "enName", false, processed, SUPER_USER));
	entry1.addTerm(createTerm("zh-CN", "zhCnName", false, pending, SUPER_USER));
	entry1.addTerm(createTerm("zh-HK", "zhHkName", false, processed, SUPER_USER));

	TermEntry entry2 = new TermEntry(projectId, "TES000005", SUPER_USER);
	entry2.setUuId(UUID.randomUUID().toString());
	entry2.addTerm(createTerm("en", "enName", false, pending, SUPER_USER));
	entry2.addTerm(createTerm("sr", "srName", false, pending, SUPER_USER));

	ITmgrGlossaryConnector connector = getRegularConnector();
	ITmgrGlossarySearcher searcher = connector.getTmgrSearcher();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.update(Arrays.asList(entry1, entry2));

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(projectId);
	filter.setStatuses(Collections.singletonList(pending));
	filter.setHideBlanksLanguages(languages);

	filter.setTargetLanguages(languages);
	filter.addLanguageResultField(true, SYNONYM_NUMBER, languages.toArray(new String[languages.size()]));

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);
	Assert.assertEquals(0, page.getTotalResults());
	Assert.assertTrue(CollectionUtils.isEmpty(page.getResults()));
    }

    @Ignore
    @Test
    public void testMultiSortSearch() throws TmException {
	String srLanguageId = LANGUAGE;
	String srName = "srpski";
	boolean forbidden = false;
	String status = STATUS;
	String user = "power";
	Term srTerm = createTerm(srLanguageId, srName, forbidden, status, user);
	Calendar calendar = Calendar.getInstance();
	calendar.set(2014, 1, 1);
	srTerm.setDateModified(calendar.getTimeInMillis());

	String enLanguageId = "en";
	String enName = "engleski";
	Term enTerm = createTerm(enLanguageId, enName, forbidden, status, user);
	calendar.set(2014, 2, 2);
	enTerm.setDateModified(calendar.getTimeInMillis());

	Long projectId = 1L;

	TermEntry termEntry = new TermEntry(projectId, "TES000001", user);
	termEntry.addTerm(srTerm);
	termEntry.addTerm(enTerm);

	ITmgrGlossaryConnector connector = getRegularConnector();
	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();

	updater.save(termEntry);

	// add new termEntry
	String sr1Name = "srpski 1";
	Term sr1Term = createTerm(srLanguageId, sr1Name, forbidden, status, user);
	calendar.set(2013, 1, 1);
	sr1Term.setDateModified(calendar.getTimeInMillis());

	String en1Name = "engleski 1";
	Term en1Term = createTerm(enLanguageId, en1Name, forbidden, status, user);
	calendar.set(2013, 1, 1);
	en1Term.setDateModified(calendar.getTimeInMillis());

	TermEntry termEntry1 = new TermEntry(projectId, "TES000001", user);
	termEntry1.addTerm(sr1Term);
	termEntry1.addTerm(en1Term);

	updater.save(termEntry1);

	ITmgrGlossarySearcher searcher = connector.getTmgrSearcher();

	String srField = SolrDocHelper.createDynamicFieldName(srLanguageId, SolrParentDocFields.DYN_DATE_MODIFIED_SORT);
	String enField = SolrDocHelper.createDynamicFieldName(enLanguageId, SolrParentDocFields.DYN_DATE_MODIFIED_SORT);
	IPageable pageable = new TmgrPageRequest(Direction.ASC, srField, enField);

	TmgrSearchFilter filter = new TmgrSearchFilter(pageable);
	filter.addProjectId(projectId);
	filter.addUsersModifiedFilter(user);

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	List<TermEntry> termEntries = page.getResults();

	Assert.assertEquals(2, termEntries.size());
	Assert.assertEquals(termEntry1, termEntries.get(0));
    }

    @Test
    public void testSearchExactDescription() throws TmException {
	addNewTermEntry();

	String sourceLanguage = "en";

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setSourceLanguage(sourceLanguage);

	String attType = "context";
	filter.addAttributeType(attType);

	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(1L);
	filter.setProjectIds(projectIds);

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	Page<TermEntry> termEntries = searcher.concordanceSearch(filter);
	Assert.assertNotNull(termEntries);

	List<TermEntry> results = termEntries.getResults();
	Assert.assertNotNull(results);
	Assert.assertEquals(1, results.size());

	TermEntry termEntry = results.get(0);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	Assert.assertNotNull(languageTerms);

	Set<Term> terms = languageTerms.get(sourceLanguage);
	Assert.assertNotNull(terms);
	Assert.assertEquals(1, terms.size());

	Term term = terms.iterator().next();

	Assert.assertEquals(sourceLanguage, term.getLanguageId());

	Set<Description> descriptions = term.getDescriptions();
	Assert.assertNotNull(descriptions);
	Assert.assertEquals(1, descriptions.size());

	Description desc = descriptions.iterator().next();
	Assert.assertEquals(attType, desc.getType());
    }

    @Test
    public void testSearchExactMatch() throws TmException {
	addNewTermEntry();
	addTermEntry();

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addUsersCreatedFilter(SUPER_USER);
	filter.addUsersModifiedFilter(SUPER_USER);
	filter.setSourceLanguage(LANGUAGE);

	List<String> statuses = new ArrayList<String>();
	statuses.add(STATUS);
	filter.setStatuses(statuses);

	List<String> targetLanguages = new ArrayList<String>();
	targetLanguages.add("en");
	targetLanguages.add("fr");
	filter.setTargetLanguages(targetLanguages);

	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(1L);
	filter.setProjectIds(projectIds);

	String text = "jezik";
	// String text = "definicija";
	TextFilter textFilter = new TextFilter(text);
	textFilter.setExactMatch(true);
	textFilter.setCaseSensitive(false);
	textFilter.setAllTextSearch(true);

	filter.setTextFilter(textFilter);

	addPageable(filter);

	filter.addLanguageResultField(true, true, SYNONYM_NUMBER, LANGUAGE, "en", "fr");

	Page<TermEntry> termEntries = searcher.concordanceSearch(filter);
	Assert.assertNotNull(termEntries);

	List<TermEntry> results = termEntries.getResults();
	Assert.assertNotNull(results);
	Assert.assertEquals(1, results.size());

	TermEntry termEntry = results.get(0);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	Assert.assertNotNull(languageTerms);

	Set<Term> terms = languageTerms.get(LANGUAGE);
	Assert.assertNotNull(terms);
	Assert.assertEquals(1, terms.size());

	Term term = terms.iterator().next();
	Assert.assertTrue(term.getName().toLowerCase().contains(textFilter.getText()));

	Assert.assertEquals(LANGUAGE, term.getLanguageId());
    }

    @Test
    public void testSearchExactMatchMedical() throws TmException {
	addMedicalTermEntry();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(2L);
	filter.setSourceLanguage("en");
	String searchText = "bla bla Angioedema";
	filter.setTextFilter(new TextFilter(searchText, true, false, false));

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	List<TermEntry> results = searcher.concordanceSearch(filter).getResults();
	Assert.assertNotNull(results);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSearchFacetTermCountsWithEmptyFilter() throws TmException {

	TmgrSearchFilter searchFilter = new TmgrSearchFilter();

	ITmgrGlossaryConnector regularConnector = getRegularConnector();

	ITmgrGlossarySearcher searcher = regularConnector.getTmgrSearcher();

	searcher.searchFacetTermCounts(searchFilter);
    }

    // Test case: Project id: 1L, languagesIds: [en]
    @Test
    public void testSearchFacetTermCounts_1() throws TmException {

	List<String> languageIds = Arrays.asList("en");

	TmgrSearchFilter searchFilter = new TmgrSearchFilter();

	searchFilter.setProjectIds(Arrays.asList(1L));

	searchFilter.setTargetLanguages(languageIds);
	searchFilter.addLanguageResultField(true, SYNONYM_NUMBER, languageIds.toArray(new String[languageIds.size()]));

	ITmgrGlossaryConnector regularConnector = getRegularConnector();

	ITmgrGlossarySearcher searcher = regularConnector.getTmgrSearcher();

	FacetTermCounts termCounts = searcher.searchFacetTermCounts(searchFilter);

	Assert.assertNotNull(termCounts);

	Map<String, LanguageTermCount> termCountMap = termCounts.getTermCountByLanguage();

	Assert.assertEquals(1, termCountMap.size());

	for (Entry<String, LanguageTermCount> entry : termCountMap.entrySet()) {
	    LanguageTermCount termCount = entry.getValue();
	    assertEquals(1, termCount.getTermCount());
	    assertEquals(1, termCount.getApproved());
	    assertEquals(0, termCount.getForbidden());
	    assertEquals(0, termCount.getInFinalReview());
	    assertEquals(0, termCount.getInTranslationReview());
	}
    }

    // Test case: Project id: 1L, languagesIds: [en, sr]
    @Test
    public void testSearchFacetTermCounts_2() throws TmException {
	addNewTermEntry();

	List<String> languageIds = Arrays.asList("en", "sr");

	TmgrSearchFilter searchFilter = new TmgrSearchFilter();

	searchFilter.setProjectIds(Arrays.asList(1L));

	searchFilter.setTargetLanguages(languageIds);
	searchFilter.addLanguageResultField(true, SYNONYM_NUMBER, languageIds.toArray(new String[languageIds.size()]));

	ITmgrGlossaryConnector regularConnector = getRegularConnector();

	ITmgrGlossarySearcher searcher = regularConnector.getTmgrSearcher();

	FacetTermCounts termCounts = searcher.searchFacetTermCounts(searchFilter);

	Assert.assertNotNull(termCounts);

	Map<String, LanguageTermCount> termCountMap = termCounts.getTermCountByLanguage();

	Assert.assertEquals(2, termCountMap.size());

	for (Entry<String, LanguageTermCount> entry : termCountMap.entrySet()) {
	    LanguageTermCount termCount = entry.getValue();
	    switch (entry.getKey()) {
	    case "en":
		assertEquals(1, termCount.getTermCount());
		assertEquals(1, termCount.getApproved());
		assertEquals(0, termCount.getForbidden());
		assertEquals(0, termCount.getInFinalReview());
		assertEquals(0, termCount.getInTranslationReview());
		break;
	    case "sr":
		assertEquals(2, termCount.getTermCount());
		assertEquals(2, termCount.getApproved());
		assertEquals(0, termCount.getForbidden());
		assertEquals(0, termCount.getInFinalReview());
		assertEquals(0, termCount.getInTranslationReview());
		break;
	    default:
		throw new IllegalArgumentException("Illegal languageId.");
	    }
	}
    }

    // Test case: Project id: 1L, languagesIds: [en, en-US, de-DE, fr-FR,
    // "it-IT"]
    @Test
    public void testSearchFacetTermCounts_3() throws TmException {
	addTwoTermEntries();

	List<String> languageIds = Arrays.asList("en", "en-US", "de-DE", "fr-FR", "it-IT");

	TmgrSearchFilter searchFilter = new TmgrSearchFilter();

	searchFilter.setProjectIds(Arrays.asList(1L));

	searchFilter.setTargetLanguages(languageIds);
	searchFilter.addLanguageResultField(true, SYNONYM_NUMBER, languageIds.toArray(new String[languageIds.size()]));

	ITmgrGlossaryConnector regularConnector = getRegularConnector();

	ITmgrGlossarySearcher searcher = regularConnector.getTmgrSearcher();

	FacetTermCounts termCounts = searcher.searchFacetTermCounts(searchFilter);

	Assert.assertNotNull(termCounts);

	Map<String, LanguageTermCount> termCountMap = termCounts.getTermCountByLanguage();

	Assert.assertEquals(5, termCountMap.size());

	for (Entry<String, LanguageTermCount> entry : termCountMap.entrySet()) {
	    LanguageTermCount termCount = entry.getValue();
	    switch (entry.getKey()) {
	    case "en":
		assertEquals(1, termCount.getTermCount());
		assertEquals(1, termCount.getApproved());
		assertEquals(0, termCount.getForbidden());
		assertEquals(0, termCount.getInFinalReview());
		assertEquals(0, termCount.getInTranslationReview());
		break;
	    case "en-US":
		assertEquals(3, termCount.getTermCount());
		assertEquals(3, termCount.getApproved());
		assertEquals(0, termCount.getForbidden());
		assertEquals(0, termCount.getInFinalReview());
		assertEquals(0, termCount.getInTranslationReview());
		break;
	    case "de-DE":
		assertEquals(2, termCount.getTermCount());
		assertEquals(0, termCount.getApproved());
		assertEquals(1, termCount.getForbidden());
		assertEquals(0, termCount.getInFinalReview());
		assertEquals(0, termCount.getInTranslationReview());
		break;
	    case "fr-FR":
		assertEquals(4, termCount.getTermCount());
		assertEquals(0, termCount.getApproved());
		assertEquals(0, termCount.getForbidden());
		assertEquals(2, termCount.getInFinalReview());
		assertEquals(2, termCount.getInTranslationReview());
		break;
	    case "it-IT":
		assertEquals(0, termCount.getTermCount());
		assertEquals(0, termCount.getApproved());
		assertEquals(0, termCount.getForbidden());
		assertEquals(0, termCount.getInFinalReview());
		assertEquals(0, termCount.getInTranslationReview());
		break;
	    default:
		throw new IllegalArgumentException("Illegal languageId.");
	    }
	}
    }

    @Test
    public void testSearchFilteredByMultipleUsersCreated() throws TmException {
	addTermEntry();
	addTermEntry1();
	addTermEntry2();
	addTermEntry3();
	addTwoTermEntries();

	TmgrSearchFilter searchFilter = createFilter();

	List<String> usersCreated = Arrays.asList(SDULIN, POWER_USER);
	searchFilter.setUsersCreated(usersCreated);

	String english = Locale.ENGLISH.getCode();
	searchFilter.setSourceLanguage(english);

	ITmgrGlossaryConnector regularConnector = getRegularConnector();

	ITmgrGlossarySearcher searcher = regularConnector.getTmgrSearcher();

	Page<TermEntry> page = searcher.concordanceSearch(searchFilter);
	List<TermEntry> termEntries = page.getResults();
	assertEquals(3, termEntries.size());

	assertTrue(termEntries.stream().map(termEntry -> termEntry.getUserCreated()).allMatch(usersCreated::contains));
    }

    @Test
    public void testSearchFilteredByMultipleUsersModified() throws TmException {
	addTwoTermEntries();

	TmgrSearchFilter searchFilter = createFilter();

	List<String> usersModified = Arrays.asList(SUPER_USER, POWER_USER);
	searchFilter.setUsersModified(usersModified);

	ITmgrGlossaryConnector regularConnector = getRegularConnector();

	ITmgrGlossarySearcher searcher = regularConnector.getTmgrSearcher();

	Page<TermEntry> page = searcher.concordanceSearch(searchFilter);
	List<TermEntry> termEntries = page.getResults();
	assertEquals(2, termEntries.size());

	assertTrue(termEntries.stream().map(termEntry -> termEntry.ggetTerms()).flatMap(terms -> terms.stream())
		.map(term -> term.getUserModified()).allMatch(usersModified::contains));
    }

    @Test
    public void testSearchMissingTranslation_case1() throws TmException {
	addTermEntry();
	addTermEntry1();
	addTermEntry3();

	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();

	List<TermEntry> allEntries = browser.findAll();
	Assert.assertEquals(4, allEntries.size());

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	String sourceLanguage = LANGUAGE;

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setOnlyMissingTranslation(true);
	filter.setSourceLanguage(sourceLanguage);

	String targetLanguage2 = "en";

	List<String> targetLanguages = new ArrayList<String>();
	targetLanguages.add(targetLanguage2);
	filter.setTargetLanguages(targetLanguages);

	filter.addLanguageResultField(true, SYNONYM_NUMBER, sourceLanguage, targetLanguage2);

	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(1L);
	filter.setProjectIds(projectIds);

	addPageable(filter);

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);

	Assert.assertEquals(1, page.getTotalResults());
	List<TermEntry> results = page.getResults();
	Assert.assertNotNull(results);

	for (TermEntry termEntry : results) {
	    Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	    Set<Term> srTerms = languageTerms.get(sourceLanguage);
	    Assert.assertTrue(CollectionUtils.isNotEmpty(srTerms));

	    Set<Term> enTerms = languageTerms.get(targetLanguage2);

	    Assert.assertTrue(CollectionUtils.isEmpty(enTerms));
	}
    }

    @Test
    public void testSearchMissingTranslation_case2() throws TmException {
	addTermEntry();
	addTermEntry1();
	addTermEntry3();

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	String sourceLanguage = LANGUAGE;

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setOnlyMissingTranslation(true);
	filter.setSourceLanguage(sourceLanguage);

	String targetLanguage1 = "fr";
	String targetLanguage2 = "en";

	List<String> targetLanguages = new ArrayList<String>();
	targetLanguages.add(targetLanguage1);
	targetLanguages.add(targetLanguage2);
	filter.setTargetLanguages(targetLanguages);

	filter.addLanguageResultField(true, SYNONYM_NUMBER, sourceLanguage, targetLanguage1, targetLanguage2);

	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(1L);
	filter.setProjectIds(projectIds);

	addPageable(filter);

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);

	Assert.assertEquals(2, page.getTotalResults());
	List<TermEntry> results = page.getResults();
	Assert.assertNotNull(results);

	for (TermEntry termEntry : results) {
	    Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	    Set<Term> srTerms = languageTerms.get(sourceLanguage);
	    Assert.assertTrue(CollectionUtils.isNotEmpty(srTerms));

	    Set<Term> frTerms = languageTerms.get(targetLanguage1);
	    Set<Term> enTerms = languageTerms.get(targetLanguage2);

	    Assert.assertTrue(CollectionUtils.isEmpty(frTerms) || CollectionUtils.isEmpty(enTerms));
	}
    }

    @Test
    public void testSearchMissingTranslation_case3() throws TmException {
	addTermEntry();

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryBrowser browser = connector.getTmgrBrowser();

	List<TermEntry> allEntries = browser.findAll();
	Assert.assertEquals(2, allEntries.size());

	TermEntry termEntry = allEntries.get(0);
	List<Term> terms = termEntry.ggetTerms();
	for (Term term : terms) {
	    term.setDisabled(true);
	}

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.update(allEntries);

	ITmgrGlossarySearcher searcher = connector.getTmgrSearcher();

	String sourceLanguage = LANGUAGE;

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setOnlyMissingTranslation(true);
	filter.setSourceLanguage(sourceLanguage);

	String targetLanguage2 = "en";

	List<String> targetLanguages = new ArrayList<String>();
	targetLanguages.add(targetLanguage2);
	filter.setTargetLanguages(targetLanguages);

	filter.addLanguageResultField(true, SYNONYM_NUMBER, sourceLanguage, targetLanguage2);

	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(1L);
	filter.setProjectIds(projectIds);

	addPageable(filter);

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);

	Assert.assertEquals(0, page.getTotalResults());
	List<TermEntry> results = page.getResults();
	Assert.assertTrue(CollectionUtils.isEmpty(results));
    }

    @Test
    public void testSearchNgram() throws TmException {
	addNewTermEntry();

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addUsersCreatedFilter(SUPER_USER);
	filter.addUsersModifiedFilter(SUPER_USER);
	filter.setSourceLanguage(LANGUAGE);

	List<String> statuses = new ArrayList<String>();
	statuses.add(STATUS);
	filter.setStatuses(statuses);

	List<String> targetLanguages = new ArrayList<String>();
	targetLanguages.add("en");
	targetLanguages.add("fr");
	filter.setTargetLanguages(targetLanguages);

	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(1L);
	filter.setProjectIds(projectIds);

	// ngram of 'created'
	String text = "reat";
	// String text = "generated";
	TextFilter textFilter = new TextFilter(text);
	textFilter.setExactMatch(false);
	textFilter.setCaseSensitive(false);
	textFilter.setAllTextSearch(false);

	filter.setTextFilter(textFilter);

	addPageable(filter);

	Page<TermEntry> termEntries = searcher.concordanceSearch(filter);
	Assert.assertNotNull(termEntries);

	List<TermEntry> results = termEntries.getResults();
	Assert.assertNotNull(results);
	Assert.assertEquals(1, results.size());

	TermEntry termEntry = results.get(0);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	Assert.assertNotNull(languageTerms);

	Set<Term> terms = languageTerms.get(LANGUAGE);
	Assert.assertNotNull(terms);
	Assert.assertEquals(2, terms.size());

	boolean match = false;
	for (Term term : terms) {
	    match = term.getName().toLowerCase().contains(textFilter.getText());
	    if (match) {
		break;
	    }
	}

	Assert.assertTrue(match);
    }

    private void addBlacklistTermEntry() throws TmException {
	String languageId = LANGUAGE;
	String name = "Ovo je zabranjen terim";
	boolean forbidden = true;
	String status = "BLACKLISTED";
	String user = SUPER_USER;

	Term srTerm = createTerm(languageId, name, forbidden, status, user);
	srTerm.addDescription(new Description("context", "atribut"));

	Term srSynonym = createTerm(languageId, "Ovo je zabranjen sinonim", forbidden, status, user);

	Term enTerm = createTerm("en", "this is whiteList term", false, STATUS, user);

	long projectId = 1l;
	String projectShortCode = "TES000001";
	String username = SUPER_USER;

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, username);
	termEntry.addTerm(srTerm);
	termEntry.addTerm(srSynonym);
	termEntry.addTerm(enTerm);

	String type3 = "definition";
	String value3 = "ovo je definicija";
	Description description3 = createDescription(type3, value3);

	termEntry.addDescription(description3);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);
    }

    private TermEntry addMedicalTermEntry() throws TmException {
	String languageId = "en";
	String name = "Angioedema and urticaria";
	boolean forbidden = false;
	String status = STATUS;
	String user = SUPER_USER;

	Term sourceTerm = createTerm(languageId, name, forbidden, status, user);

	String targetLanguageId = "cs";
	String targetName = "Angioedém a kopřivka";

	Term targetTerm = createTerm(targetLanguageId, targetName, forbidden, status, user);

	Long projectId = 2l;
	String projectShortCode = "TES000001";
	String username = SUPER_USER;

	TermEntry termEntry1 = new TermEntry(projectId, projectShortCode, username);
	termEntry1.setUuId(UUID.randomUUID().toString());
	termEntry1.addTerm(sourceTerm);
	termEntry1.addTerm(targetTerm);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry1);

	return termEntry1;
    }

    private void addNewTermEntriesOnSecondProject() throws TmException {
	String germany = Locale.GERMANY.getCode();
	String english = Locale.US.getCode();
	String french = Locale.FRANCE.getCode();

	Term englishTerm = createTerm(english, "english term", false, ItemStatusTypeHolder.PROCESSED.getName(),
		SUPER_USER);
	Term germanyTerm = createTerm(germany, "germany term", true, ItemStatusTypeHolder.BLACKLISTED.getName(),
		SUPER_USER);
	Term frenchTerm = createTerm(french, "french term", false, ItemStatusTypeHolder.WAITING.getName(), SUPER_USER);

	String projectShortCode = "NIK000001";
	final Long projectId = 2L;

	TermEntry termEntry1 = new TermEntry(projectId, projectShortCode, SUPER_USER);
	termEntry1.setUuId(UUID.randomUUID().toString());
	termEntry1.addTerm(englishTerm);

	TermEntry termEntry2 = new TermEntry(projectId, projectShortCode, SUPER_USER);
	termEntry2.setUuId(UUID.randomUUID().toString());
	termEntry2.addTerm(englishTerm);
	termEntry2.addTerm(germanyTerm);

	TermEntry termEntry3 = new TermEntry(projectId, projectShortCode, SUPER_USER);
	termEntry3.setUuId(UUID.randomUUID().toString());
	termEntry3.addTerm(englishTerm);
	termEntry3.addTerm(frenchTerm);

	TermEntry termEntry4 = new TermEntry(projectId, projectShortCode, SUPER_USER);
	termEntry4.setUuId(UUID.randomUUID().toString());
	termEntry4.addTerm(germanyTerm);
	termEntry4.addTerm(frenchTerm);
	ITmgrGlossaryConnector connector = getRegularConnector();

	TermEntry emptyEntry = new TermEntry(projectId, projectShortCode, SUPER_USER);

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();

	// Add new term entries and set project id 2L
	updater.save(termEntry1);
	updater.save(termEntry2);
	updater.save(termEntry3);
	updater.save(termEntry4);
	updater.save(emptyEntry);
    }

    private void addPageable(TmgrSearchFilter filter) {
	IPageable pageable = new TmgrPageRequest();
	filter.setPageable(pageable);
    }

    private void addTermEntry() throws TmException {
	String languageId = LANGUAGE;
	String name = "Srpski jezik";
	boolean forbidden = false;
	String status = STATUS;
	String user = SUPER_USER;

	Term srTerm = createTerm(languageId, name, forbidden, status, user);
	srTerm.addDescription(new Description("context", "atribut"));

	Term enTerm = createTerm("en", "english language", forbidden, status, user);

	long projectId = 1l;
	String projectShortCode = "TES000001";
	String username = SUPER_USER;

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, username);
	termEntry.addTerm(srTerm);
	termEntry.addTerm(enTerm);

	String type3 = "definition";
	String value3 = "ovo je definicija";
	Description description3 = createDescription(type3, value3);

	termEntry.addDescription(description3);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);
    }

    private void addTermEntry1() throws TmException {
	String languageId = LANGUAGE;
	String name = "Srpski jezik";
	boolean forbidden = false;
	String status = STATUS;
	String user = POWER_USER;

	Term srTerm = createTerm(languageId, name, forbidden, status, user);
	srTerm.addDescription(new Description("context", "atribut"));

	long projectId = 1l;
	String projectShortCode = "TES000001";
	String username = POWER_USER;

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, username);
	termEntry.addTerm(srTerm);

	String type3 = "definition";
	String value3 = "ovo je definicija";
	Description description3 = createDescription(type3, value3);

	termEntry.addDescription(description3);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);
    }

    private void addTermEntry2() throws TmException {
	String languageId = "en";
	String name = "reserved";
	boolean forbidden = false;
	String status = STATUS;
	String user = SUPER_USER;

	Term enTerm = createTerm(languageId, name, forbidden, status, user);

	Term deTerm = createTerm("de", "wordfast", forbidden, status, user);

	long projectId = 1l;
	String projectShortCode = "TES000001";
	String username = SUPER_USER;

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, username);
	termEntry.addTerm(enTerm);
	termEntry.addTerm(deTerm);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);
    }

    private void addTermEntry3() throws TmException {
	String languageId = LANGUAGE;
	String name = "Srpski jezik i knjizevnost";
	boolean forbidden = false;
	String status = STATUS;
	String user = POWER_USER;

	Term srTerm = createTerm(languageId, name, forbidden, status, user);
	srTerm.addDescription(new Description("context", "atribut"));

	Term enTerm = createTerm("en", "english language", forbidden, status, user);

	Term frTerm = createTerm("fr", "la france", forbidden, status, user);

	long projectId = 1l;
	String projectShortCode = "TES000001";
	String username = POWER_USER;

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, username);
	termEntry.addTerm(srTerm);
	termEntry.addTerm(enTerm);
	termEntry.addTerm(frTerm);

	String type3 = "definition";
	String value3 = "ovo je definicija";
	Description description3 = createDescription(type3, value3);

	termEntry.addDescription(description3);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);
    }

    private TmgrSearchFilter createFilter() {

	TmgrSearchFilter searchFilter = new TmgrSearchFilter();
	searchFilter.setProjectIds(Arrays.asList(1L));

	String english = Locale.US.getCode();
	searchFilter.setSourceLanguage(english);
	String german = Locale.GERMANY.getCode();

	List<String> languageIds = Arrays.asList(german, english);
	searchFilter.setTargetLanguages(languageIds);
	searchFilter.addLanguageResultField(true, SYNONYM_NUMBER, languageIds.toArray(new String[languageIds.size()]));

	searchFilter.setPageable(new TmgrPageRequest(AbstractPageRequest.DEFAULT_PAGE, AbstractPageRequest.DEFAULT_SIZE,
		Direction.ASC, "en-US_termName_STRING_STORE_SORT"));

	return searchFilter;
    }
}
