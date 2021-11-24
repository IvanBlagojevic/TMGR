package org.gs4tr.termmanager.service.undoterm;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.glossary.backup.converter.DbSubmissionTermEntryConverter;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTerm;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntryHistory;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermHistory;
import org.gs4tr.termmanager.service.mocking.groovy.AbstractGroovyTest;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(loader = TestEnvironmentAwareContextLoader.class)
@TestSuite("/undoterm")
public class UndoTermImplTest extends AbstractGroovyTest {

    private static final String TEST_SUITE_ROOT = "src/test/resources/groovy/solr";

    private static final String IT_LOCALE = "it";

    @Rule
    public TestName _testMethodName = new TestName();

    private UndoTermImpl _undoTerm;

    @Before
    public void init() {
	prepareTestModel(getTestMethodName().getMethodName());
	_undoTerm = new UndoTermImpl();
    }

    @Test
    public void testMergeSubmissionEntriesWhileRollback() {

	DbSubmissionTermEntry submssionTermEntry = new DbSubmissionTermEntry();
	Set<DbSubmissionTermEntryHistory> histories = new HashSet<DbSubmissionTermEntryHistory>();
	DbSubmissionTermEntryHistory history = new DbSubmissionTermEntryHistory();
	history.setDateModified(new Date());
	history.setHistoryAction(Action.NOT_AVAILABLE.name());
	history.setRevisionId(2);
	histories.add(history);

	history = new DbSubmissionTermEntryHistory();
	history.setDateModified(new Date());
	history.setHistoryAction(Action.NOT_AVAILABLE.name());
	history.setRevisionId(2);
	histories.add(history);

	history = new DbSubmissionTermEntryHistory();
	history.setDateModified(new Date());
	history.setHistoryAction(Action.NOT_AVAILABLE.name());
	history.setRevisionId(1);
	histories.add(history);

	submssionTermEntry.setHistory(histories);
	submssionTermEntry.setRevisionId(2);
	submssionTermEntry.setSubmissionTerms(new HashSet<DbSubmissionTerm>());
	DbSubmissionTermEntry existing = new DbSubmissionTermEntry();
	existing.setSubmissionTerms(new HashSet<DbSubmissionTerm>());
	existing.setRevisionId(3);

	submssionTermEntry = DbSubmissionTermEntryConverter.mergeWithExistingDbTermEntry(existing, submssionTermEntry);
	Assert.assertEquals(4, submssionTermEntry.getHistory().size());
    }

    @Test
    public void testMergeTwoSubmissionTermEntries() {
	String term1ID = UUID.randomUUID().toString();
	String termEntryId = UUID.randomUUID().toString();
	Date dateModified = new Date();
	DbSubmissionTermEntry existingTermEntry = new DbSubmissionTermEntry();
	existingTermEntry.setUuId(termEntryId);
	Set<DbSubmissionTerm> terms = new HashSet<DbSubmissionTerm>();
	DbSubmissionTerm term = createTerm(term1ID, "name", "temp text", dateModified);
	terms.add(term);
	existingTermEntry.setSubmissionTerms(terms);

	DbSubmissionTermEntry incomingTermEntry = new DbSubmissionTermEntry();
	incomingTermEntry.setUuId(termEntryId);
	terms = new HashSet<DbSubmissionTerm>();
	term = createTerm(term1ID, "name", "temp text 1", new Date());
	terms.add(term);
	incomingTermEntry.setSubmissionTerms(terms);
	existingTermEntry.setHistory(new HashSet<DbSubmissionTermEntryHistory>());
	incomingTermEntry.setHistory(new HashSet<DbSubmissionTermEntryHistory>());
	incomingTermEntry.setRevisionId(3);
	existingTermEntry.setRevisionId(2);
	DbSubmissionTermEntry result = DbSubmissionTermEntryConverter.mergeWithExistingDbTermEntry(incomingTermEntry,
		existingTermEntry);
	Assert.assertNotNull(result);

	Set<DbSubmissionTermEntryHistory> dbHistories = result.getHistory();
	Assert.assertEquals(1, dbHistories.size());
	DbSubmissionTermEntryHistory dbHistory = dbHistories.iterator().next();
	Set<DbSubmissionTermHistory> termHistories = dbHistory.getHistory();
	Assert.assertEquals(1, termHistories.size());
	DbSubmissionTerm revision = JsonUtils.readValue(termHistories.iterator().next().getRevision(),
		DbSubmissionTerm.class);
	Assert.assertNotNull(revision);
	Assert.assertEquals(revision.getTempText(), "temp text");

	DbSubmissionTermEntry newIncomingTermEntry = new DbSubmissionTermEntry();
	newIncomingTermEntry.setUuId(termEntryId);
	terms = new HashSet<DbSubmissionTerm>();
	term = createTerm(term1ID, "name", "temp text 2", dateModified);
	terms.add(term);
	newIncomingTermEntry.setSubmissionTerms(terms);
	newIncomingTermEntry.setRevisionId(4);
	incomingTermEntry.setRevisionId(3);
	result = DbSubmissionTermEntryConverter.mergeWithExistingDbTermEntry(newIncomingTermEntry, incomingTermEntry);
	revision = JsonUtils.readValue(
		result.getHistory().iterator().next().getHistory().iterator().next().getRevision(),
		DbSubmissionTerm.class);
	Assert.assertEquals(revision.getTempText(), "temp text 1");
    }

    @Test
    public void testUndoImpl() {
	String termId = UUID.randomUUID().toString();
	DbSubmissionTermEntry submissionTermEntry = new DbSubmissionTermEntry();

	Set<DbSubmissionTerm> submissionTerms = new HashSet<DbSubmissionTerm>();
	DbSubmissionTerm term = createTerm(termId, "name", "temp text", new Date());
	submissionTerms.add(term);
	term = createTerm(UUID.randomUUID().toString(), "another term", "another term", new Date());
	submissionTerms.add(term);
	submissionTermEntry.setSubmissionTerms(submissionTerms);

	DbSubmissionTermEntryHistory dbSubmissionTermHistory = new DbSubmissionTermEntryHistory();
	Set<DbSubmissionTermHistory> dbTermHistories = new HashSet<DbSubmissionTermHistory>();

	DbSubmissionTermHistory termHistory = new DbSubmissionTermHistory();
	term = createTerm(termId, "name", "text", new Date());
	termHistory.setRevision(JsonUtils.writeValueAsBytes(term));
	dbTermHistories.add(termHistory);
	dbSubmissionTermHistory.setHistory(dbTermHistories);

	Set<DbSubmissionTermEntryHistory> submissionTermEntryHistories = new HashSet<DbSubmissionTermEntryHistory>();
	submissionTermEntryHistories.add(dbSubmissionTermHistory);
	submissionTermEntry.setHistory(submissionTermEntryHistories);

	TermEntry result = getUndoTerm().undoTerm(submissionTermEntry);
	Assert.assertNotNull(result);

	List<Term> terms = result.ggetAllTerms();
	Assert.assertEquals(2, terms.size());
	Term resultTerm = terms.get(1);
	Assert.assertNotNull(resultTerm);
	Assert.assertEquals(resultTerm.getTempText(), "text");
    }

    @Test
    @TestCase("undoterm")
    public void testUndoTermImpl() {
	DbSubmissionTermEntry firstRevision = getModelObject("firstRevision", DbSubmissionTermEntry.class);
	DbSubmissionTermEntry secondRevision = getModelObject("secondRevision", DbSubmissionTermEntry.class);
	DbSubmissionTermEntry thirdRevision = getModelObject("thirdRevision", DbSubmissionTermEntry.class);

	DbSubmissionTermEntry entry = DbSubmissionTermEntryConverter.mergeWithExistingDbTermEntry(secondRevision,
		firstRevision);
	Assert.assertNotNull(entry.getHistory());

	entry = DbSubmissionTermEntryConverter.mergeWithExistingDbTermEntry(thirdRevision, entry);
	Assert.assertNotNull(entry.getHistory());
	Set<DbSubmissionTerm> dbTerms = entry.getSubmissionTerms();
	DbSubmissionTerm itDbTerm = null;
	for (DbSubmissionTerm dbTerm : dbTerms) {
	    if (dbTerm.getLanguageId().equals(IT_LOCALE)) {
		itDbTerm = dbTerm;
		break;
	    }
	}
	Assert.assertNotNull(itDbTerm);
	Assert.assertEquals("changed currentItValue", itDbTerm.getTempText());

	TermEntry result = getUndoTerm().undoTerm(entry);
	List<Term> terms = result.ggetAllTerms();
	Assert.assertNotNull(terms);
	Assert.assertEquals(4, terms.size());
	Term itTerm = null;

	for (Term term : terms) {
	    if (term.getLanguageId().equals(IT_LOCALE)) {
		itTerm = term;
		break;
	    }
	}
	Assert.assertNotNull(itTerm);
	Assert.assertEquals("currentItValue", itTerm.getTempText());

    }

    private DbSubmissionTerm createTerm(String termId, String name, String tempText, Date dateModified) {
	DbSubmissionTerm term = new DbSubmissionTerm();
	term.setCanceled(false);
	term.setDateModified(dateModified);
	term.setUuId(termId);
	term.setNameAsBytes(name);
	term.setTempText(tempText);
	return term;
    }

    private TestName getTestMethodName() {
	return _testMethodName;
    }

    private UndoTermImpl getUndoTerm() {
	return _undoTerm;
    }

    @Override
    protected String getTestSuiteRoot() {
	return TEST_SUITE_ROOT;
    }

}
