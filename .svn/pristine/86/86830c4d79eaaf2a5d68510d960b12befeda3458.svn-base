package org.gs4tr.termmanager.persistence.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;

public class TmgrGlossaryBrowserTest extends AbstractSolrGlossaryTest {

    private static final String PROJECT_SHORT_CODE = "NIK000001";

    // Find number of term entries on a project, send projectId only.
    @Test
    public void getNumberOfTermEntriesOnProjectTest() throws TmException {

	List<Long> projectIds = Arrays.asList(1L);
	TmgrSearchFilter searchFilter = new TmgrSearchFilter();
	searchFilter.setProjectIds(projectIds);
	searchFilter.setSourceLanguage(null);

	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();

	Long number = browser.getNumberOfTermEntriesOnProject(searchFilter);
	assertEquals(1l, number.longValue());

	addNewTermEntry();
	number = browser.getNumberOfTermEntriesOnProject(searchFilter);
	assertEquals(2l, number.longValue());

	addTwoTermEntries();
	number = browser.getNumberOfTermEntriesOnProject(searchFilter);
	assertEquals(4l, number.longValue());

	ITmgrGlossaryConnector connector = getRegularConnector();
	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.deleteAll();

	number = browser.getNumberOfTermEntriesOnProject(searchFilter);
	assertEquals(0l, number.longValue());

	TermEntry termEntry = new TermEntry(2L, PROJECT_SHORT_CODE, SUPER_USER);
	updater.save(termEntry);

	number = browser.getNumberOfTermEntriesOnProject(searchFilter);
	assertEquals(0l, number.longValue());
    }

    // Find number of term entries on a project, send projectId and project
    // language.
    @Test
    public void getNumberOfTermEntriesOnProjectTest_1() throws TmException {

	List<Long> projectIds = Arrays.asList(1L);
	TmgrSearchFilter searchFilter = new TmgrSearchFilter();
	searchFilter.setProjectIds(projectIds);
	searchFilter.setSourceLanguage(LANGUAGE);

	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();

	Long number = browser.getNumberOfTermEntriesOnProject(searchFilter);
	assertEquals(0l, number.longValue());

	addNewTermEntry();
	number = browser.getNumberOfTermEntriesOnProject(searchFilter);
	assertEquals(1l, number.longValue());

	String french = Locale.FRANCE.getCode();
	searchFilter.setSourceLanguage(french);

	addTwoTermEntries();

	number = browser.getNumberOfTermEntriesOnProject(searchFilter);
	assertEquals(2l, number.longValue());

	ITmgrGlossaryConnector connector = getRegularConnector();
	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.deleteAll();

	number = browser.getNumberOfTermEntriesOnProject(searchFilter);
	assertEquals(0l, number.longValue());

	TermEntry termEntry = new TermEntry(1L, PROJECT_SHORT_CODE, SUPER_USER);
	updater.save(termEntry);

	number = browser.getNumberOfTermEntriesOnProject(searchFilter);
	assertEquals(0l, number.longValue());

	termEntry.addTerm(createTerm(french, "tmp", false, STATUS, SUPER_USER));
	updater.save(termEntry);

	number = browser.getNumberOfTermEntriesOnProject(searchFilter);
	assertEquals(1l, number.longValue());
    }

    @Test
    public void searchFindByIdUnique1() throws TmException {
	Long projectId = 1L;

	String termEntryId = "term-entry-id-001";
	String termId = "term-id-001";

	TermEntry termEntry = new TermEntry();
	termEntry.setProjectId(projectId);
	termEntry.setUuId(termEntryId);
	Term term = new Term();
	term.setLanguageId("en");
	term.setUuId(termId);
	termEntry.addTerm(term);

	ITmgrGlossaryUpdater updater = getRegularConnector().getTmgrUpdater();
	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();

	updater.save(termEntry);
	TermEntry findById1 = browser.findById(termEntryId, projectId);
	Assert.assertNotNull(findById1);
	Assert.assertEquals(termEntry, findById1);

	updater.update(termEntry);
	TermEntry findById2 = browser.findById(termEntryId, projectId);
	Assert.assertNotNull(findById2);
	Assert.assertEquals(termEntry, findById2);
    }

    @Test
    public void searchFindByIdUnique2() throws TmException {
	TermEntry termEntry = addNewTermEntry();
	Long projectId = termEntry.getProjectId();

	String termEntryId = termEntry.getUuId();

	ITmgrGlossaryUpdater updater = getRegularConnector().getTmgrUpdater();
	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();

	termEntry = browser.findById(termEntryId, projectId);
	Assert.assertNotNull(termEntry);

	Term term = termEntry.ggetTerms().get(0);
	term.setName("new name");

	updater.update(termEntry);
	TermEntry findById1 = browser.findById(termEntryId, projectId);
	Assert.assertNotNull(findById1);

	Term term1 = termEntry.ggetTerms().get(0);
	Assert.assertEquals("new name", term1.getName());
    }

    @Test
    public void testBrowseByProjectId() throws TmException {
	TermEntry termEntry = addNewTermEntry();
	Long projectId = termEntry.getProjectId();

	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(projectId);

	List<TermEntry> termEntries = browser.browse(filter);
	Assert.assertNotNull(termEntries);
	Assert.assertEquals(2, termEntries.size());
    }

    @Test
    public void testBrowseNonDisabled() throws TmException {
	TermEntry termEntry = addNewTermEntry();
	Long projectId = termEntry.getProjectId();
	String termEntryId = termEntry.getUuId();

	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(projectId);

	List<TermEntry> termEntries = browser.browse(filter);
	Assert.assertNotNull(termEntries);
	Assert.assertEquals(2, termEntries.size());

	termEntry = browser.findById(termEntryId, projectId);
	Assert.assertNotNull(termEntry);

	List<Term> terms = termEntry.ggetTerms();
	Assert.assertNotNull(terms);

	for (Term term : terms) {
	    term.setDisabled(true);
	}

	ITmgrGlossaryUpdater updater = getRegularConnector().getTmgrUpdater();
	updater.update(termEntry);

	termEntries = browser.browse(filter);
	Assert.assertNotNull(termEntries);
	Assert.assertEquals(2, termEntries.size());
	Assert.assertTrue(!termEntries.contains(termEntry));
    }

    @Test
    public void testFindByIds() throws TmException {
	TermEntry termEntry = addNewTermEntry();
	Long projectId = termEntry.getProjectId();

	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();
	List<TermEntry> allTermEntries = browser.findAll();

	Assert.assertNotNull(allTermEntries);
	Assert.assertEquals(2, allTermEntries.size());

	List<String> ids = new ArrayList<String>();
	for (TermEntry entry : allTermEntries) {
	    ids.add(entry.getUuId());
	}

	List<TermEntry> termEntries = browser.findByIds(ids, Arrays.asList(projectId));
	Assert.assertNotNull(termEntries);
	Assert.assertEquals(2, termEntries.size());

	Assert.assertTrue(allTermEntries.containsAll(termEntries));
    }

    @Test
    public void testFindByProjectId() throws TmException {
	TermEntry termEntry = addNewTermEntry();
	Long projectId = termEntry.getProjectId();

	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();

	List<TermEntry> termEntries = browser.findByProjectId(projectId);
	Assert.assertNotNull(termEntries);
	Assert.assertEquals(2, termEntries.size());
    }

    @Test
    public void testFindByTermId() throws TmException {
	TermEntry termEntry = addNewTermEntry();
	Long projectId = termEntry.getProjectId();

	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();
	List<TermEntry> allTermEntries = browser.findAll();

	Assert.assertNotNull(allTermEntries);
	Assert.assertEquals(2, allTermEntries.size());

	termEntry = allTermEntries.iterator().next();
	Term term = termEntry.ggetTerms().get(0);
	String termId = term.getUuId();

	TermEntry termEntry1 = browser.findByTermId(termId, projectId);

	Assert.assertEquals(termEntry, termEntry1);
    }

    @Test
    public void testFindByTermIds() throws TmException {
	TermEntry termEntry = addNewTermEntry();
	Long projectId = termEntry.getProjectId();

	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();
	List<TermEntry> allTermEntries = browser.findAll();

	Assert.assertNotNull(allTermEntries);
	Assert.assertEquals(2, allTermEntries.size());

	Set<String> ids = new HashSet<String>();

	termEntry = allTermEntries.iterator().next();
	Term term = termEntry.ggetTerms().get(0);
	ids.add(term.getUuId());

	List<TermEntry> termEntries = browser.findByTermIds(ids, projectId);

	Assert.assertNotNull(termEntries);
	Assert.assertEquals(1, termEntries.size());
	Assert.assertEquals(termEntry, termEntries.get(0));
    }

    @Test
    public void testFindTermsById() throws TmException {
	TermEntry termEntry = addNewTermEntry();
	Long projectId = termEntry.getProjectId();

	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();
	List<TermEntry> allTermEntries = browser.findAll();

	Assert.assertNotNull(allTermEntries);
	Assert.assertEquals(2, allTermEntries.size());

	Set<String> ids = new HashSet<String>();
	for (TermEntry entry : allTermEntries) {
	    Map<String, Set<Term>> languageTerms = entry.getLanguageTerms();
	    for (Entry<String, Set<Term>> entrySet : languageTerms.entrySet()) {
		for (Term term : entrySet.getValue()) {
		    ids.add(term.getUuId());
		}
	    }
	}

	Term term = browser.findTermById(ids.iterator().next(), projectId);
	Assert.assertNotNull(term);
	Assert.assertNotNull(term.getDateCreated());
    }

    @Test
    public void testFindTermsByIds() throws TmException {
	TermEntry termEntry = addNewTermEntry();
	Long projectId = termEntry.getProjectId();

	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();
	List<TermEntry> allTermEntries = browser.findAll();

	Assert.assertNotNull(allTermEntries);
	Assert.assertEquals(2, allTermEntries.size());

	Set<String> ids = new HashSet<String>();
	for (TermEntry entry : allTermEntries) {
	    Map<String, Set<Term>> languageTerms = entry.getLanguageTerms();
	    for (Entry<String, Set<Term>> entrySet : languageTerms.entrySet()) {
		for (Term term : entrySet.getValue()) {
		    ids.add(term.getUuId());
		}
	    }
	}

	List<Term> terms = browser.findTermsByIds(ids, Arrays.asList(projectId));
	Assert.assertNotNull(terms);
	Assert.assertEquals(ids.size(), terms.size());
    }

    @Test
    public void testFindTermsByLanguageIds() throws TmException {
	addNewTermEntry();

	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();

	List<Term> termsByProjectId = browser.findTermsByProjectId(PROJECT_ID);
	assertNotNull(termsByProjectId);
	Assert.assertEquals(3, termsByProjectId.size());

	List<String> languageId = new ArrayList<>();
	languageId.add("en");
	languageId.add("sr");

	/* There are three terms in ("en") and ("sr") languages */

	List<Term> terms = browser.findTermsByLanguageIds(PROJECT_ID, languageId, 1);
	Assert.assertNotNull(terms);
	Assert.assertEquals(terms.size(), 3);
	Assert.assertTrue(termsByProjectId.containsAll(terms));
    }

    @Test
    public void testFindTermsByLanguageIdsForDifferentProject() throws TmException {
	addNewTermEntry();

	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();

	List<String> languageId = new ArrayList<>();
	languageId.add("en");
	languageId.add("sr");

	/*
	 * There are three terms in ("en") and ("sr") languages in project with ID=1,
	 * but not in project with ID=2
	 */
	Long projectId = 2L;

	List<Term> terms = browser.findTermsByLanguageIds(projectId, languageId, 1);
	assertTrue(CollectionUtils.isEmpty(terms));
    }

    @Test
    public void testFindTermsByLanguageIdsNoTermsToFind() throws TmException {
	addNewTermEntry();

	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();

	List<Term> allTerms = browser.findTermsByProjectId(PROJECT_ID);
	assertNotNull(allTerms);
	Assert.assertEquals(3, allTerms.size());

	Term term = allTerms.stream().filter(s -> s.getLanguageId().equals("it")).findFirst().orElse(null);
	Assert.assertNull(term);

	List<String> languageId = new ArrayList<>();
	languageId.add("it");

	/* There are no terms in ("it") langauge */

	List<Term> terms = browser.findTermsByLanguageIds(PROJECT_ID, languageId, 1);
	assertTrue(CollectionUtils.isEmpty(terms));
    }

    @Test
    public void testFindTermsByProjectId() throws TmException {
	addNewTermEntry();

	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();

	Long projectId = 1L;

	List<Term> terms = browser.findTermsByProjectId(projectId);
	Assert.assertNotNull(terms);
    }

    @Test
    public void testGetTermsByNonExistingIds() throws TmException {
	List<String> termIds = new ArrayList<String>();
	termIds.add("TERM_ID_01");
	termIds.add("TERM_ID_02");

	List<Term> terms = getRegularConnector().getTmgrBrowser().findTermsByIds(termIds, Arrays.asList(1L));
	Assert.assertTrue(CollectionUtils.isEmpty(terms));
    }
}
