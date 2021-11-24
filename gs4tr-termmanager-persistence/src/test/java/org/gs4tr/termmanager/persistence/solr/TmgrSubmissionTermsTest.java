package org.gs4tr.termmanager.persistence.solr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import org.junit.Ignore;
import org.junit.Test;

public class TmgrSubmissionTermsTest extends AbstractSolrGlossaryTest {

    @Test
    public void testSaveSubmissionTerms() throws TmException {
	String type1 = "context";
	String value1 = "This is some dummy term attribute context";
	Description description1 = createDescription(type1, value1);

	String type2 = "definition";
	String value2 = "This is some dummy term attribute definition";
	Description description2 = createDescription(type2, value2);

	String languageId = "sr";
	String name = "Neki termin za prevod";
	boolean forbidden = false;
	String status = "INTRANSLATION";
	String user = "user";

	String assignee = "assignee";
	String submitter = "submitter";
	String submissionName = "sub1";
	Long submissionId = 1L;

	Term term = createTerm(languageId, name, forbidden, status, user);
	term.setAssignee(assignee);
	// term.setSubmissionName(submissionName);
	// term.setSubmitter(submitter);
	term.setParentUuId(UUID.randomUUID().toString());

	term.addDescription(description1);
	term.addDescription(description2);

	String name1 = "source term";
	String languageId2 = "en";
	Term term1 = createTerm(languageId2, name1, forbidden, status, user);
	term1.setAssignee(assignee);
	// term1.setSubmissionName(submissionName);
	// term1.setSubmitter(submitter);
	term1.setInTranslationAsSource(Boolean.TRUE);
	term1.setParentUuId(UUID.randomUUID().toString());

	long projectId = 1l;
	String projectShortCode = "TES000001";
	String username = "user";

	String termEntryId = UUID.randomUUID().toString();

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, username);
	termEntry.setUuId(termEntryId);
	termEntry.setSubmissionId(submissionId);
	termEntry.setSubmissionName(submissionName);
	termEntry.setSubmitter(submitter);
	termEntry.addTerm(term);
	termEntry.addTerm(term1);

	ITmgrGlossaryConnector connector = getRegularConnector();
	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();

	updater.save(termEntry);

	TermEntry subTermEntry = connector.getTmgrBrowser().findById(termEntryId, projectId);
	Assert.assertNotNull(subTermEntry);

	Map<String, Set<Term>> languageTerms = subTermEntry.getLanguageTerms();
	Assert.assertNotNull(languageTerms);
	Assert.assertEquals(2, languageTerms.size());

	for (Term t : languageTerms.get(languageId)) {
	    Assert.assertEquals(term, t);
	    Assert.assertEquals(submissionName, t.getSubmissionName());
	    Assert.assertEquals(submitter, t.getSubmitter());
	}

	for (Term t : languageTerms.get(languageId2)) {
	    Assert.assertEquals(term1, t);
	    Assert.assertEquals(submissionName, t.getSubmissionName());
	    Assert.assertEquals(submitter, t.getSubmitter());
	}
    }

    @Ignore
    @Test
    public void testSearchSubmissionTerms() throws TmException {
	addNewSubmissionTermEntry();

	ITmgrGlossaryConnector connector = getRegularConnector();
	ITmgrGlossarySearcher searcher = connector.getTmgrSearcher();

	Long submissionId = 1L;
	String submitter = "submitter";
	String sourceLanguageId = "en";
	String targetLanguageId = "sr";
	List<String> targets = new ArrayList<String>();
	targets.add(targetLanguageId);

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setSubmissionId(submissionId);

	filter.setSubmitter(submitter);
	filter.setSourceLanguage(sourceLanguageId);
	filter.setTargetLanguages(targets);

	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(1L);
	filter.setProjectIds(projectIds);

	String text = "prevod";
	// String text = "definicija";
	TextFilter textFilter = new TextFilter(text);
	textFilter.setExactMatch(false);
	textFilter.setCaseSensitive(false);
	textFilter.setAllTextSearch(true);

	filter.setTextFilter(textFilter);

	Page<TermEntry> results = searcher.concordanceSearch(filter);
	Assert.assertNotNull(results);

	List<TermEntry> termEntries = results.getResults();
	Assert.assertNotNull(termEntries);
	Assert.assertEquals(1, termEntries.size());
    }

    private void addNewSubmissionTermEntry() throws TmException {
	String type1 = "context";
	String value1 = "This is some dummy term attribute context";
	Description description1 = createDescription(type1, value1);

	String type2 = "definition";
	String value2 = "This is some dummy term attribute definition";
	Description description2 = createDescription(type2, value2);

	String languageId = "sr";
	String name = "Neki termin za prevod";
	boolean forbidden = false;
	String status = "INTRANSLATION";
	String user = "user";

	String assignee = "assignee";
	String submitter = "submitter";
	String submissionName = "sub1";
	Long submissionId = 1L;

	Term term = createTerm(languageId, name, forbidden, status, user);
	term.setAssignee(assignee);
	term.setSubmissionName(submissionName);
	term.setSubmitter(submitter);
	term.setParentUuId(UUID.randomUUID().toString());

	term.addDescription(description1);
	term.addDescription(description2);

	String name1 = "source term";
	String languageId2 = "en";
	Term term1 = createTerm(languageId2, name1, forbidden, status, user);
	term1.setAssignee(assignee);
	term1.setSubmissionName(submissionName);
	term1.setSubmitter(submitter);
	term1.setInTranslationAsSource(Boolean.TRUE);
	term1.setParentUuId(UUID.randomUUID().toString());

	Long projectId = 1l;
	String projectShortCode = "TES000001";
	String username = "user";

	String termEntryId = UUID.randomUUID().toString();

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, username);
	termEntry.setUuId(termEntryId);
	termEntry.setParentUuId(UUID.randomUUID().toString());
	termEntry.setSubmissionId(submissionId);
	termEntry.addTerm(term);
	termEntry.addTerm(term1);

	ITmgrGlossaryConnector connector = getRegularConnector();
	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();

	updater.save(termEntry);
    }
}
