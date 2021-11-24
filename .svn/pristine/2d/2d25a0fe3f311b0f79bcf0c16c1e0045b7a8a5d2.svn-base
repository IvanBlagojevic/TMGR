package org.gs4tr.termmanager.persistence.solr;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossarySearcher;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.termmanager.persistence.solr.query.TextFilter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;

public class TmgrGlossarySearcherTmSearchTest extends AbstractSolrGlossaryTest {

    @Test
    public void testBlacklistSearchExact1() throws TmException {
	addBlacklistTermEntry();

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();
	List<String> statuses = new ArrayList<String>();
	statuses.add(ItemStatusTypeHolder.BLACKLISTED.getName());

	Long projectId = 1L;

	Locale sourceLocale = Locale.ENGLISH;
	Locale targetLocale = Locale.GERMAN;

	TextFilter textFilter = new TextFilter("text");
	textFilter.setFuzzyMatch(false);
	textFilter.setAllTextSearch(false);
	textFilter.setCaseSensitive(false);
	textFilter.setSegmentSearch(true);

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setTextFilter(textFilter);
	filter.setPageable(new TmgrPageRequest());
	filter.addLanguageResultField(true, SYNONYM_NUMBER,
		new String[] { sourceLocale.getCode(), targetLocale.getCode() });
	filter.setSourceLanguage(sourceLocale.getCode());
	List<String> locales = new ArrayList<String>();
	locales.add(targetLocale.getCode());
	filter.setTargetLanguages(locales);
	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(projectId);
	filter.setProjectIds(projectIds);
	filter.setStatuses(statuses);
	Page<TermEntry> page = searcher.segmentSearch(filter);
	Assert.assertNotNull(page);

	List<TermEntry> results = page.getResults();
	Assert.assertTrue(CollectionUtils.isNotEmpty(results));
	Assert.assertEquals(1, results.size());
    }

    @Test
    public void testBlacklistSearchFuzzy1() throws TmException {
	addBlacklistTermEntry();

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();
	List<String> statuses = new ArrayList<String>();
	statuses.add(ItemStatusTypeHolder.BLACKLISTED.getName());

	Long projectId = 1L;

	Locale sourceLocale = Locale.ENGLISH;
	Locale targetLocale = Locale.GERMAN;

	TextFilter textFilter = new TextFilter("texts");
	textFilter.setFuzzyMatch(true);
	textFilter.setAllTextSearch(false);
	textFilter.setCaseSensitive(false);
	textFilter.setSegmentSearch(true);

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setTextFilter(textFilter);
	filter.setPageable(new TmgrPageRequest());
	filter.addLanguageResultField(true, SYNONYM_NUMBER,
		new String[] { sourceLocale.getCode(), targetLocale.getCode() });
	filter.setSourceLanguage(sourceLocale.getCode());
	List<String> locales = new ArrayList<String>();
	locales.add(targetLocale.getCode());
	filter.setTargetLanguages(locales);
	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(projectId);
	filter.setProjectIds(projectIds);
	filter.setStatuses(statuses);
	Page<TermEntry> page = searcher.segmentSearch(filter);
	Assert.assertNotNull(page);

	List<TermEntry> results = page.getResults();
	Assert.assertTrue(CollectionUtils.isNotEmpty(results));
	Assert.assertEquals(1, results.size());
    }

    @Test
    public void testGlossarySearchExact1() throws TmException {
	addTermEntry1();
	addTermEntry2();
	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	Long projectId = 1L;

	Locale sourceLocale = Locale.ENGLISH;
	Locale targetLocale = Locale.GERMAN;

	TextFilter textFilter = new TextFilter("languages");
	textFilter.setFuzzyMatch(false);
	textFilter.setAllTextSearch(false);
	textFilter.setCaseSensitive(false);
	textFilter.setSegmentSearch(true);
	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setTextFilter(textFilter);

	filter.addLanguageResultField(true, SYNONYM_NUMBER,
		new String[] { sourceLocale.getCode(), targetLocale.getCode() });

	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(projectId);
	filter.setProjectIds(projectIds);
	filter.setPageable(new TmgrPageRequest(0, 50, null));

	filter.setSourceLanguage(sourceLocale.getCode());
	Page<TermEntry> page = searcher.segmentSearch(filter);
	Assert.assertNotNull(page);

	List<TermEntry> results = page.getResults();
	Assert.assertTrue(CollectionUtils.isEmpty(results));
    }

    @Test
    public void testGlossarySearchFuzzy1() throws TmException {
	addTermEntry1();
	addTermEntry2();

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();
	List<String> statuses = new ArrayList<String>();
	statuses.add(ItemStatusTypeHolder.PROCESSED.getName());
	statuses.add(ItemStatusTypeHolder.WAITING.getName());
	Long projectId = 1L;

	Locale sourceLocale = Locale.ENGLISH;
	Locale targetLocale = Locale.GERMAN;

	TextFilter textFilter = new TextFilter("languages");
	textFilter.setFuzzyMatch(true);
	textFilter.setAllTextSearch(false);
	textFilter.setCaseSensitive(false);
	textFilter.setSegmentSearch(true);

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setTextFilter(textFilter);
	filter.setPageable(new TmgrPageRequest());
	filter.addLanguageResultField(true, SYNONYM_NUMBER,
		new String[] { sourceLocale.getCode(), targetLocale.getCode() });
	filter.setSourceLanguage(sourceLocale.getCode());
	List<String> locales = new ArrayList<String>();
	locales.add(targetLocale.getCode());
	filter.setTargetLanguages(locales);
	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(projectId);
	filter.setProjectIds(projectIds);
	filter.setStatuses(statuses);
	Page<TermEntry> page = searcher.segmentSearch(filter);
	Assert.assertNotNull(page);

	List<TermEntry> results = page.getResults();
	Assert.assertTrue(CollectionUtils.isNotEmpty(results));
	Assert.assertEquals(1, results.size());
    }

    private void addBlacklistTermEntry() throws TmException {
	String languageId = Locale.GERMAN.getCode();
	String name = "text";
	boolean forbidden = true;
	String status = ItemStatusTypeHolder.BLACKLISTED.getName();
	String user = SUPER_USER;

	Term deTerm = createTerm(languageId, name, forbidden, status, user);

	Term enTerm = createTerm(Locale.ENGLISH.getCode(), "text", forbidden, status, user);

	long projectId = 1l;
	String projectShortCode = "TES000001";
	String username = SUPER_USER;

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, username);
	termEntry.addTerm(deTerm);
	termEntry.addTerm(enTerm);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);
    }

    private void addTermEntry1() throws TmException {
	String languageId = Locale.GERMAN.getCode();
	String name = "deutsche";
	boolean forbidden = false;
	String status = STATUS;
	String user = SUPER_USER;

	Term deTerm = createTerm(languageId, name, forbidden, status, user);
	deTerm.addDescription(new Description("context", "attribute"));

	Term enTerm = createTerm(Locale.ENGLISH.getCode(), "english", forbidden, status, user);

	long projectId = 1l;
	String projectShortCode = "TES000001";
	String username = SUPER_USER;

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, username);
	termEntry.addTerm(deTerm);
	termEntry.addTerm(enTerm);

	String type3 = "Definition";
	String value3 = "Das ist Definition";
	Description description3 = createDescription(type3, value3);

	termEntry.addDescription(description3);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);
    }

    private void addTermEntry2() throws TmException {
	String languageId = Locale.GERMAN.getCode();
	String name = "sprache";
	boolean forbidden = false;
	String status = STATUS;
	String user = SUPER_USER;

	Term deTerm = createTerm(languageId, name, forbidden, status, user);
	deTerm.addDescription(new Description("context", "attribute"));

	Term enTerm = createTerm(Locale.ENGLISH.getCode(), "language", forbidden, status, user);

	long projectId = 1l;
	String projectShortCode = "TES000001";
	String username = SUPER_USER;

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, username);
	termEntry.addTerm(deTerm);
	termEntry.addTerm(enTerm);

	String type3 = "Definition";
	String value3 = "Das ist Definition";
	Description description3 = createDescription(type3, value3);

	termEntry.addDescription(description3);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);
    }

}
