package org.gs4tr.termmanager.persistence.solr;

import java.util.Arrays;
import java.util.List;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossarySearcher;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.termmanager.persistence.solr.query.TextFilter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;

public class TmgrGlossaryConcordanceSearch extends AbstractSolrGlossaryTest {

    @Test
    public void test01() throws TmException {
	getRegularConnector().getTmgrUpdater().deleteAll();
	addTermEntry();
	List<String> languageIds = Arrays.asList(Locale.ENGLISH.getCode(), Locale.GERMAN.getCode());

	TmgrSearchFilter searchFilter = new TmgrSearchFilter();

	searchFilter.setProjectIds(Arrays.asList(1L));

	searchFilter.setTargetLanguages(languageIds);
	searchFilter.addLanguageResultField(true, SYNONYM_NUMBER, languageIds.toArray(new String[languageIds.size()]));
	TextFilter filter = new TextFilter("dep");
	filter.setAttributeTextSearch(false);
	filter.setCaseSensitive(true);
	filter.setExactMatch(false);
	searchFilter.setTextFilter(filter);
	ITmgrGlossaryConnector regularConnector = getRegularConnector();

	ITmgrGlossarySearcher searcher = regularConnector.getTmgrSearcher();

	Page<TermEntry> result = searcher.concordanceSearch(searchFilter);

	Assert.assertEquals(1, result.getResults().size());

    }

    @Test
    public void test02() throws TmException {
	getRegularConnector().getTmgrUpdater().deleteAll();
	addTermEntry();
	List<String> languageIds = Arrays.asList(Locale.ENGLISH.getCode(), Locale.GERMAN.getCode());

	TmgrSearchFilter searchFilter = new TmgrSearchFilter();

	searchFilter.setProjectIds(Arrays.asList(1L));

	searchFilter.setTargetLanguages(languageIds);
	searchFilter.addLanguageResultField(true, SYNONYM_NUMBER, languageIds.toArray(new String[languageIds.size()]));
	TextFilter filter = new TextFilter("epart");
	filter.setAttributeTextSearch(false);
	filter.setCaseSensitive(true);
	filter.setExactMatch(false);
	searchFilter.setTextFilter(filter);
	ITmgrGlossaryConnector regularConnector = getRegularConnector();

	ITmgrGlossarySearcher searcher = regularConnector.getTmgrSearcher();

	Page<TermEntry> result = searcher.concordanceSearch(searchFilter);

	Assert.assertEquals(1, result.getResults().size());

    }

    @Test
    public void test03() throws TmException {
	getRegularConnector().getTmgrUpdater().deleteAll();
	addTermEntry();
	List<String> languageIds = Arrays.asList(Locale.ENGLISH.getCode(), Locale.GERMAN.getCode());

	TmgrSearchFilter searchFilter = new TmgrSearchFilter();

	searchFilter.setProjectIds(Arrays.asList(1L));

	searchFilter.setTargetLanguages(languageIds);
	searchFilter.addLanguageResultField(true, SYNONYM_NUMBER, languageIds.toArray(new String[languageIds.size()]));
	TextFilter filter = new TextFilter("department");
	filter.setAttributeTextSearch(false);
	filter.setCaseSensitive(true);
	filter.setExactMatch(true);
	searchFilter.setTextFilter(filter);
	ITmgrGlossaryConnector regularConnector = getRegularConnector();

	ITmgrGlossarySearcher searcher = regularConnector.getTmgrSearcher();

	Page<TermEntry> result = searcher.concordanceSearch(searchFilter);

	Assert.assertEquals(1, result.getResults().size());

    }

    @Test
    public void test04() throws TmException {
	getRegularConnector().getTmgrUpdater().deleteAll();
	addTermEntry();
	List<String> languageIds = Arrays.asList(Locale.ENGLISH.getCode(), Locale.GERMAN.getCode());

	TmgrSearchFilter searchFilter = new TmgrSearchFilter();

	searchFilter.setProjectIds(Arrays.asList(1L));

	searchFilter.setTargetLanguages(languageIds);
	searchFilter.addLanguageResultField(true, SYNONYM_NUMBER, languageIds.toArray(new String[languageIds.size()]));
	TextFilter filter = new TextFilter("Department");
	filter.setAttributeTextSearch(false);
	filter.setCaseSensitive(true);
	filter.setExactMatch(true);
	searchFilter.setTextFilter(filter);
	ITmgrGlossaryConnector regularConnector = getRegularConnector();

	ITmgrGlossarySearcher searcher = regularConnector.getTmgrSearcher();

	Page<TermEntry> result = searcher.concordanceSearch(searchFilter);

	Assert.assertEquals(0, result.getResults().size());

    }

    @Test
    public void test05() throws TmException {
	getRegularConnector().getTmgrUpdater().deleteAll();
	addTermEntry();
	List<String> languageIds = Arrays.asList(Locale.ENGLISH.getCode(), Locale.GERMAN.getCode());

	TmgrSearchFilter searchFilter = new TmgrSearchFilter();

	searchFilter.setProjectIds(Arrays.asList(1L));

	searchFilter.setTargetLanguages(languageIds);
	searchFilter.addLanguageResultField(true, SYNONYM_NUMBER, languageIds.toArray(new String[languageIds.size()]));
	TextFilter filter = new TextFilter("EpArT");
	filter.setAttributeTextSearch(false);
	filter.setCaseSensitive(false);
	filter.setExactMatch(false);
	searchFilter.setTextFilter(filter);
	ITmgrGlossaryConnector regularConnector = getRegularConnector();

	ITmgrGlossarySearcher searcher = regularConnector.getTmgrSearcher();

	Page<TermEntry> result = searcher.concordanceSearch(searchFilter);

	Assert.assertEquals(1, result.getResults().size());

    }

    @Test
    public void test06() throws TmException {
	getRegularConnector().getTmgrUpdater().deleteAll();
	addTermEntry();
	addTermEntry2();
	List<String> languageIds = Arrays.asList(Locale.ENGLISH.getCode(), Locale.GERMAN.getCode());

	TmgrSearchFilter searchFilter = new TmgrSearchFilter();

	searchFilter.setProjectIds(Arrays.asList(1L));

	searchFilter.setTargetLanguages(languageIds);
	searchFilter.addLanguageResultField(true, SYNONYM_NUMBER, languageIds.toArray(new String[languageIds.size()]));
	TextFilter filter = new TextFilter("this is department");
	filter.setAttributeTextSearch(false);
	filter.setCaseSensitive(true);
	filter.setExactMatch(true);
	searchFilter.setTextFilter(filter);
	ITmgrGlossaryConnector regularConnector = getRegularConnector();

	ITmgrGlossarySearcher searcher = regularConnector.getTmgrSearcher();

	Page<TermEntry> result = searcher.concordanceSearch(searchFilter);

	Assert.assertEquals(2, result.getResults().size());

    }

    private void addTermEntry() throws TmException {
	String languageId = Locale.GERMAN.getCode();

	boolean forbidden = false;
	String status = STATUS;
	String user = SUPER_USER;

	Term geTerm = createTerm(languageId, "german", forbidden, status, user);
	geTerm.addDescription(new Description("context", "context"));

	Term enTerm = createTerm(Locale.ENGLISH.getCode(), "department", forbidden, status, user);

	long projectId = 1l;
	String projectShortCode = "TES000001";
	String username = SUPER_USER;

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, username);
	termEntry.addTerm(geTerm);
	termEntry.addTerm(enTerm);

	String type3 = "definition";
	String value3 = "this is definition";
	Description description3 = createDescription(type3, value3);

	termEntry.addDescription(description3);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);
    }

    private void addTermEntry2() throws TmException {
	String languageId = Locale.GERMAN.getCode();

	boolean forbidden = false;
	String status = STATUS;
	String user = SUPER_USER;

	Term geTerm = createTerm(languageId, "german", forbidden, status, user);
	geTerm.addDescription(new Description("context", "context"));

	Term enTerm = createTerm(Locale.ENGLISH.getCode(), "this is department", forbidden, status, user);

	long projectId = 1l;
	String projectShortCode = "TES000001";
	String username = SUPER_USER;

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, username);
	termEntry.addTerm(geTerm);
	termEntry.addTerm(enTerm);

	String type3 = "definition";
	String value3 = "this is definition";
	Description description3 = createDescription(type3, value3);

	termEntry.addDescription(description3);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);
    }

}
