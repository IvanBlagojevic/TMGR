package org.gs4tr.termmanager.persistence.solr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossarySearcher;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;

public class TmgrGlossarySearcherUserTest extends AbstractSolrGlossaryTest {

    private static final String ENTRY_USER = "entryuser";
    private static final String MOD_USER_1 = "srbin";
    private static final String MOD_USER_2 = "englez";

    @Test
    public void testSearchByCreationUser_case1() throws TmException {
	addTermEntry();

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addUsersCreatedFilter(ENTRY_USER);
	filter.setSourceLanguage(LANGUAGE);

	List<String> statuses = new ArrayList<String>();
	statuses.add(STATUS);
	filter.setStatuses(statuses);

	String targetLanguage = "en";

	List<String> targetLanguages = new ArrayList<String>();
	targetLanguages.add(targetLanguage);
	targetLanguages.add("fr");
	filter.setTargetLanguages(targetLanguages);

	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(1L);
	filter.setProjectIds(projectIds);

	filter.addLanguageResultField(true, true, SYNONYM_NUMBER, LANGUAGE, targetLanguage, "fr");

	Page<TermEntry> termEntries = searcher.concordanceSearch(filter);
	Assert.assertNotNull(termEntries);

	List<TermEntry> results = termEntries.getResults();
	Assert.assertNotNull(results);
	Assert.assertEquals(1, results.size());

	TermEntry termEntry = results.get(0);
	Assert.assertEquals(ENTRY_USER, termEntry.getUserCreated());

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	Assert.assertNotNull(languageTerms);

	Assert.assertEquals(2, languageTerms.size());
    }

    @Test
    public void testSearchByModificationUser_case1() throws TmException {
	addTermEntry();

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addUsersModifiedFilter(MOD_USER_1);
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
	Assert.assertEquals(LANGUAGE, term.getLanguageId());
	Assert.assertEquals(MOD_USER_1, term.getUserModified());
    }

    @Test
    public void testSearchByModificationUser_case2() throws TmException {
	addTermEntry();

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addUsersModifiedFilter(MOD_USER_2);
	filter.setSourceLanguage(LANGUAGE);

	List<String> statuses = new ArrayList<String>();
	statuses.add(STATUS);
	filter.setStatuses(statuses);

	String targetLanguage = "en";

	List<String> targetLanguages = new ArrayList<String>();
	targetLanguages.add(targetLanguage);
	targetLanguages.add("fr");
	filter.setTargetLanguages(targetLanguages);

	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(1L);
	filter.setProjectIds(projectIds);

	filter.addLanguageResultField(true, true, SYNONYM_NUMBER, LANGUAGE, targetLanguage, "fr");

	Page<TermEntry> termEntries = searcher.concordanceSearch(filter);
	Assert.assertNotNull(termEntries);

	List<TermEntry> results = termEntries.getResults();
	Assert.assertNotNull(results);
	Assert.assertEquals(1, results.size());

	TermEntry termEntry = results.get(0);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	Assert.assertNotNull(languageTerms);

	Set<Term> terms = languageTerms.get(targetLanguage);
	Assert.assertNotNull(terms);
	Assert.assertEquals(1, terms.size());

	Term term = terms.iterator().next();
	Assert.assertEquals(targetLanguage, term.getLanguageId());
	Assert.assertEquals(MOD_USER_2, term.getUserModified());
    }

    private void addTermEntry() throws TmException {
	String languageId = LANGUAGE;
	String name = "Srpski jezik";
	boolean forbidden = false;
	String status = STATUS;

	Term srTerm = createTerm(languageId, name, forbidden, status, MOD_USER_1);

	Term enTerm = createTerm("en", "english language", forbidden, status, "englez");

	long projectId = 1l;
	String projectShortCode = "TES000001";

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, ENTRY_USER);
	termEntry.addTerm(srTerm);
	termEntry.addTerm(enTerm);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);
    }
}
