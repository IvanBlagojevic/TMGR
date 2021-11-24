package org.gs4tr.termmanager.persistence.solr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossarySearcher;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.termmanager.persistence.solr.query.TmgrPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;

public class TmgrGlossaryHideBlanksTest extends AbstractSolrGlossaryTest {

    @Test
    public void testHideBlanks_case1() throws TmException {
	addTermEntry1();// sr
	addTermEntry2();// en, de
	addTermEntry3();// sr, en, fr

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	String sourceLanguage = "sr";

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setSourceLanguage(sourceLanguage);

	String targetLanguage1 = "en";
	String targetLanguage2 = "fr";

	List<String> targetLanguages = new ArrayList<String>();
	targetLanguages.add(targetLanguage1);
	filter.setTargetLanguages(targetLanguages);

	filter.addLanguageResultField(true, SYNONYM_NUMBER, sourceLanguage, targetLanguage1, targetLanguage2);

	filter.addHideBlanksLanguages(sourceLanguage, targetLanguage1);

	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(1L);
	filter.setProjectIds(projectIds);

	filter.setPageable(new TmgrPageRequest());

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);

	Assert.assertEquals(1, page.getTotalResults());
	List<TermEntry> results = page.getResults();
	Assert.assertNotNull(results);

	for (TermEntry termEntry : results) {
	    Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	    Set<Term> srTerms = languageTerms.get(sourceLanguage);
	    Assert.assertTrue(CollectionUtils.isNotEmpty(srTerms));

	    Set<Term> enTerms = languageTerms.get(targetLanguage1);
	    Assert.assertTrue(CollectionUtils.isNotEmpty(enTerms));

	    Set<Term> frTerms = languageTerms.get(targetLanguage2);
	    Assert.assertTrue(CollectionUtils.isNotEmpty(frTerms));
	}
    }

    @Test
    public void testHideBlanks_case2() throws TmException {
	addTermEntry1();// sr
	addTermEntry2();// en, de
	addTermEntry3();// sr, en, fr

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	String sourceLanguage = "fr";

	String targetLanguage1 = "de";

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setSourceLanguage(sourceLanguage);

	filter.addLanguageResultField(true, SYNONYM_NUMBER, sourceLanguage, targetLanguage1);

	filter.addHideBlanksLanguages(sourceLanguage);

	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(1L);
	filter.setProjectIds(projectIds);

	filter.setPageable(new TmgrPageRequest());

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);

	Assert.assertEquals(1, page.getTotalResults());
	List<TermEntry> results = page.getResults();
	Assert.assertNotNull(results);

	for (TermEntry termEntry : results) {
	    Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	    Set<Term> frTerms = languageTerms.get(sourceLanguage);
	    Assert.assertTrue(CollectionUtils.isNotEmpty(frTerms));

	    Set<Term> enTerms = languageTerms.get("en");
	    Assert.assertTrue(CollectionUtils.isEmpty(enTerms));
	}
    }

    @Test
    public void testHideBlanks_case3() throws TmException {
	addTermEntry1();// sr
	addTermEntry2();// en, de
	addTermEntry3();// sr, en, fr

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	String sourceLanguage = "sr";

	String targetLanguage1 = "de";
	String targetLanguage2 = "fr";

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setSourceLanguage(sourceLanguage);

	filter.addLanguageResultField(true, SYNONYM_NUMBER, sourceLanguage, targetLanguage1, targetLanguage2);

	filter.addHideBlanksLanguages(sourceLanguage);

	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(1L);
	filter.setProjectIds(projectIds);

	filter.setPageable(new TmgrPageRequest());

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);

	Assert.assertEquals(2, page.getTotalResults());
	List<TermEntry> results = page.getResults();
	Assert.assertNotNull(results);

	for (TermEntry termEntry : results) {
	    Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	    Set<Term> srTerms = languageTerms.get(sourceLanguage);
	    Assert.assertTrue(CollectionUtils.isNotEmpty(srTerms));

	    Set<Term> enTerms = languageTerms.get("en");
	    Assert.assertTrue(CollectionUtils.isEmpty(enTerms));
	}
    }

    @Test
    public void testHideBlanks_case4() throws TmException {
	addTermEntry1();// sr
	addTermEntry2();// en, de
	addTermEntry3();// sr, en, fr

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	String sourceLanguage = "en";

	String targetLanguage1 = "de";

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setSourceLanguage(sourceLanguage);

	filter.addLanguageResultField(true, SYNONYM_NUMBER, sourceLanguage, targetLanguage1);

	filter.addHideBlanksLanguages(targetLanguage1);

	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(1L);
	filter.setProjectIds(projectIds);

	filter.setPageable(new TmgrPageRequest());

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);

	Assert.assertEquals(1, page.getTotalResults());
	List<TermEntry> results = page.getResults();
	Assert.assertNotNull(results);

	for (TermEntry termEntry : results) {
	    Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	    Set<Term> enTerms = languageTerms.get(sourceLanguage);
	    Assert.assertTrue(CollectionUtils.isNotEmpty(enTerms));

	    Set<Term> deTerms = languageTerms.get(targetLanguage1);
	    Assert.assertTrue(CollectionUtils.isNotEmpty(deTerms));

	    Set<Term> srTerms = languageTerms.get("sr");
	    Assert.assertTrue(CollectionUtils.isEmpty(srTerms));
	}
    }

    @Test
    public void testHideBlanks_case5() throws TmException {
	addTermEntry1();// sr
	addTermEntry2();// en, de
	addTermEntry3();// sr, en, fr

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	String sourceLanguage = "en";

	String targetLanguage1 = "de";

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setSourceLanguage(sourceLanguage);

	filter.addLanguageResultField(true, SYNONYM_NUMBER, sourceLanguage, targetLanguage1);

	filter.addHideBlanksLanguages(sourceLanguage);

	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(1L);
	filter.setProjectIds(projectIds);

	filter.setPageable(new TmgrPageRequest());

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);

	Assert.assertEquals(2, page.getTotalResults());
	List<TermEntry> results = page.getResults();
	Assert.assertNotNull(results);

	for (TermEntry termEntry : results) {
	    Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	    Set<Term> enTerms = languageTerms.get(sourceLanguage);
	    Assert.assertTrue(CollectionUtils.isNotEmpty(enTerms));

	    Set<Term> srTerms = languageTerms.get("sr");
	    Assert.assertTrue(CollectionUtils.isEmpty(srTerms));
	}
    }

    // sr
    private String addTermEntry1() throws TmException {
	String languageId = LANGUAGE;
	String name = "Srpski jezik";
	boolean forbidden = false;
	String status = STATUS;
	String user = SUPER_USER;

	Term srTerm = createTerm(languageId, name, forbidden, status, user);
	srTerm.addDescription(new Description("context", "atribut"));

	long projectId = 1l;
	String projectShortCode = "TES000001";
	String username = SUPER_USER;

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, username);
	termEntry.setUuId(UUID.randomUUID().toString());
	termEntry.addTerm(srTerm);

	String type3 = "definition";
	String value3 = "ovo je definicija";
	Description description3 = createDescription(type3, value3);

	termEntry.addDescription(description3);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);

	return termEntry.getUuId();
    }

    // en, de
    private String addTermEntry2() throws TmException {
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
	termEntry.setUuId(UUID.randomUUID().toString());
	termEntry.addTerm(enTerm);
	termEntry.addTerm(deTerm);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);

	return termEntry.getUuId();
    }

    // sr, en, fr
    private String addTermEntry3() throws TmException {
	String languageId = "sr";
	String name = "Srpski jezik i knjizevnost";
	boolean forbidden = false;
	String status = STATUS;
	String user = SUPER_USER;

	Term srTerm = createTerm(languageId, name, forbidden, status, user);
	srTerm.addDescription(new Description("context", "atribut"));

	Term enTerm = createTerm("en", "english language", forbidden, status, user);

	Term frTerm = createTerm("fr", "la france", forbidden, status, user);

	long projectId = 1l;
	String projectShortCode = "TES000001";
	String username = SUPER_USER;

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, username);
	termEntry.setUuId(UUID.randomUUID().toString());
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

	return termEntry.getUuId();
    }

    @Override
    protected void populate(ITmgrGlossaryConnector connector) throws Exception {

    }
}
