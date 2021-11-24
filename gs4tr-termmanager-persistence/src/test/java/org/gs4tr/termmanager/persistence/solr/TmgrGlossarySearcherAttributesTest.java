package org.gs4tr.termmanager.persistence.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossarySearcher;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.termmanager.persistence.solr.query.DescriptionFilter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TmgrGlossarySearcherAttributesTest extends AbstractSolrGlossaryTest {

    private final String SOURCE_LANGUAGE = "en-US";

    @Before
    public void before() throws TmException {
	addTermEntry1();
	addTermEntry2();
	addTermEntry3();
	addTermEntry4();
    }

    // Search will return only those term entries with terms that contains attribute
    // value for given attribute name
    @Test
    public void testConcordanceSearchByAttributes_1() throws TmException {

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	Long projectId = 3L;

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setSourceLanguage(SOURCE_LANGUAGE);
	filter.setProjectIds(Collections.singletonList(projectId));

	DescriptionFilter descriptionFilter1 = new DescriptionFilter("context");
	filter.addDescriptionFilter(descriptionFilter1);

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);

	List<TermEntry> results = page.getResults();
	Assert.assertTrue(CollectionUtils.isNotEmpty(results));
	Assert.assertEquals(2, results.size());

	// here we assert that synonyms are also returned, even if they don't contain
	// required description
	for (TermEntry entry : results) {
	    assertTrue(entry.getLanguageTerms().get(SOURCE_LANGUAGE).size() > 1);
	}

    }

    // Search will return only those term entries which contains term with
    // exact
    // combination of description type and description value
    @Test
    public void testConcordanceSearchByAttributes_2() throws TmException {

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	Long projectId = 3L;

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setProjectIds(Collections.singletonList(projectId));

	DescriptionFilter descriptionFilter = new DescriptionFilter("context", "music");
	filter.addDescriptionFilter(descriptionFilter);

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);

	List<TermEntry> results = page.getResults();
	Assert.assertTrue(CollectionUtils.isNotEmpty(results));

	assertEquals(1, results.size());
    }

    // Search will return only those term entries which contains terms without given
    // description type
    @Test
    public void testConcordanceSearchByAttributes_3() throws TmException {

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	Long projectId = 3L;

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setProjectIds(Collections.singletonList(projectId));

	DescriptionFilter descriptionFilter1 = new DescriptionFilter("context");
	descriptionFilter1.setNot(true);
	filter.addDescriptionFilter(descriptionFilter1);

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);

	List<TermEntry> results = page.getResults();
	Assert.assertTrue(CollectionUtils.isNotEmpty(results));
	assertEquals(2, results.size());

    }

    // Search will return only those term entries which contains term with
    // either first or second
    // combination of description type and description value
    @Test
    public void testConcordanceSearchByAttributes_4() throws TmException {

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	Long projectId = 3L;

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setProjectIds(Collections.singletonList(projectId));

	DescriptionFilter descriptionFilter = new DescriptionFilter("context", "music");
	DescriptionFilter descriptionFilter1 = new DescriptionFilter("example", "example");
	filter.addDescriptionFilter(descriptionFilter);
	filter.addDescriptionFilter(descriptionFilter1);

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);

	List<TermEntry> results = page.getResults();
	Assert.assertTrue(CollectionUtils.isNotEmpty(results));

	assertEquals(2, results.size());
    }

    // Search will return only those term entries with terms that contains attribute
    // value for either first or second given attribute name
    @Test
    public void testConcordanceSearchByAttributes_5() throws TmException {

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	Long projectId = 3L;

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setProjectIds(Collections.singletonList(projectId));

	DescriptionFilter descriptionFilter1 = new DescriptionFilter("context");
	DescriptionFilter descriptionFilter2 = new DescriptionFilter("example");
	filter.addDescriptionFilter(descriptionFilter1);
	filter.addDescriptionFilter(descriptionFilter2);

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);

	List<TermEntry> results = page.getResults();
	Assert.assertTrue(CollectionUtils.isNotEmpty(results));
	Assert.assertEquals(4, results.size());

    }

    // Search will return only those term entries with terms that contains first
    // attribute
    // type but does not contain second attribute type
    @Test
    public void testConcordanceSearchByAttributes_6() throws TmException {

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	Long projectId = 3L;

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setProjectIds(Collections.singletonList(projectId));

	DescriptionFilter descriptionFilter1 = new DescriptionFilter("context");
	DescriptionFilter descriptionFilter2 = new DescriptionFilter("example");
	descriptionFilter2.setNot(true);
	filter.addDescriptionFilter(descriptionFilter1);
	filter.addDescriptionFilter(descriptionFilter2);

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);

	List<TermEntry> results = page.getResults();
	Assert.assertTrue(CollectionUtils.isNotEmpty(results));
	Assert.assertEquals(1, results.size());

    }

    // Search will return only those term entries with terms that contains
    // attribute
    // type with multiple value match
    @Test
    public void testConcordanceSearchByAttributes_7() throws TmException {

	ITmgrGlossarySearcher searcher = getRegularConnector().getTmgrSearcher();

	Long projectId = 3L;

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setProjectIds(Collections.singletonList(projectId));

	DescriptionFilter descriptionFilter1 = new DescriptionFilter("context", "music");
	DescriptionFilter descriptionFilter2 = new DescriptionFilter("context", "language");
	filter.addDescriptionFilter(descriptionFilter1);
	filter.addDescriptionFilter(descriptionFilter2);

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);

	List<TermEntry> results = page.getResults();
	Assert.assertTrue(CollectionUtils.isNotEmpty(results));
	Assert.assertEquals(2, results.size());

    }

    private void addTermEntry1() throws TmException {

	Term enTerm = createTerm("en-US", "englishTerm", false, STATUS, SUPER_USER);
	enTerm.addDescription(new Description("context", "language"));

	Term enSynonym1 = createTerm("en-US", "englishSynonym1", false, STATUS, SUPER_USER);
	enSynonym1.addDescription(new Description("type", "value"));

	long projectId = 3L;
	String projectShortCode = "TES000001";

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, SUPER_USER);
	termEntry.setUuId("termEntry0001");
	termEntry.addTerm(enTerm);
	termEntry.addTerm(enSynonym1);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);

    }

    private void addTermEntry2() throws TmException {

	Term enTerm = createTerm("en-US", "englishTerm1", false, STATUS, SUPER_USER);
	enTerm.addDescription(new Description("definition", "music"));
	enTerm.addDescription(new Description("partOfSpeech", "example of speech"));

	long projectId = 3L;
	String projectShortCode = "TES000001";

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, SUPER_USER);
	termEntry.setUuId("termEntry0002");
	termEntry.addTerm(enTerm);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);

    }

    private void addTermEntry3() throws TmException {

	Term enTerm = createTerm("en-US", "englishTerm2", false, STATUS, SUPER_USER);
	enTerm.addDescription(new Description("context", "music"));
	enTerm.addDescription(new Description("example", "text1"));

	Term enSynonym = createTerm("en-US", "englishSynonym", false, STATUS, SUPER_USER);

	long projectId = 3L;
	String projectShortCode = "TES000001";

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, SUPER_USER);
	termEntry.setUuId("termEntry0003");
	termEntry.addTerm(enTerm);
	termEntry.addTerm(enSynonym);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);

    }

    private void addTermEntry4() throws TmException {

	Term enTerm = createTerm("en-US", "englishTerm3", false, STATUS, SUPER_USER);
	enTerm.addDescription(new Description("example", "example"));
	enTerm.addDescription(new Description("partOfSpeech", "end of speech"));

	long projectId = 3L;
	String projectShortCode = "TES000001";

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, SUPER_USER);
	termEntry.setUuId("termEntry0004");
	termEntry.addTerm(enTerm);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);
    }

}
