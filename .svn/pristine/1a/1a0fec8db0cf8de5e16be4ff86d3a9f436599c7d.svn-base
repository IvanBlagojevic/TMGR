package org.gs4tr.termmanager.service.history;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTerm;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntryHistory;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermHistory;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import junit.framework.Assert;

public class SubmissionHistoryImplTest extends AbstractHistoryCreatorTest {

    private static final String ENGLISH_TERM_UUID = UUID.randomUUID().toString();

    private static final String GERMAN_TERM_UUID = UUID.randomUUID().toString();

    private static final String TERM_ENTRY_UUID = UUID.randomUUID().toString();

    @Autowired
    @Qualifier("submissionHistoryCreator")
    private HistoryCreator _historyCreator;

    @Test
    public void testEmptyHistory() {
	DbSubmissionTermEntry entry = createDbSubmissionTermEntry(Action.ADDED.name(), "tempText");
	List<TermEntry> history = getHistoryCreator().createHistory(entry);
	Assert.assertNotNull(history);
	Assert.assertEquals(1, history.size());
	Set<Term> englishTerm = history.get(0).getLanguageTerms().get(Locale.ENGLISH.getCode());
	Assert.assertEquals(1, englishTerm.size());
	Term engTerm = englishTerm.iterator().next();
	Assert.assertEquals("term1", engTerm.getName());

	Set<Term> germanTerm = history.get(0).getLanguageTerms().get(Locale.GERMAN.getCode());
	Assert.assertEquals(1, germanTerm.size());
	Term gerTerm = germanTerm.iterator().next();
	Assert.assertEquals("term2", gerTerm.getName());
    }

    @Test
    public void testTermAdded() {
	DbSubmissionTermEntry entry = createDbSubmissionTermEntry(Action.EDITED.name(), "tempText");
	HashSet<String> uuids = new HashSet<>();

	DbSubmissionTermEntryHistory entryHistory = createDbSubmissionEntryHistory(Action.ADDED.name(), TERM_ENTRY_UUID,
		1, uuids);
	Set<DbSubmissionTermEntryHistory> histories = new HashSet<>();
	histories.add(entryHistory);
	entry.setHistory(histories);
	List<TermEntry> entries = getHistoryCreator().createHistory(entry);
	Assert.assertEquals(2, entries.size());
	TermEntry termEntry = entries.get(0);
	Assert.assertEquals(Long.valueOf(2), termEntry.getRevisionId());
	Assert.assertEquals(Action.NOT_AVAILABLE.name(), termEntry.getAction().name());
	Set<Term> engTerms = termEntry.getLanguageTerms().get(Locale.ENGLISH.getCode());
	Assert.assertEquals(1, engTerms.size());
	Term engTerm = engTerms.iterator().next();
	Assert.assertEquals("term1", engTerm.getName());

	Set<Term> deTerms = termEntry.getLanguageTerms().get(Locale.GERMAN.getCode());
	Assert.assertEquals(1, deTerms.size());
	Term deTerm = deTerms.iterator().next();
	Assert.assertEquals("term2", deTerm.getName());

	termEntry = entries.get(1);
	Assert.assertEquals(Long.valueOf(1), termEntry.getRevisionId());
	Assert.assertEquals(Action.NOT_AVAILABLE.name(), termEntry.getAction().name());
	engTerms = termEntry.getLanguageTerms().get(Locale.ENGLISH.getCode());
	Assert.assertEquals(1, engTerms.size());
	engTerm = engTerms.iterator().next();
	Assert.assertEquals("englishTerm", engTerm.getName());

	deTerms = termEntry.getLanguageTerms().get(Locale.GERMAN.getCode());
	Assert.assertEquals(1, deTerms.size());
	deTerm = deTerms.iterator().next();
	Assert.assertEquals("germanTerm", deTerm.getName());

    }

    private DbSubmissionTermEntryHistory createDbSubmissionEntryHistory(String action, String uuid, int revisionId,
	    HashSet<String> uuids) {
	DbSubmissionTermEntryHistory dbHistory = new DbSubmissionTermEntryHistory();
	dbHistory.setHistoryAction(action);
	dbHistory.setDateModified(new Date());
	dbHistory.setUserModified("USER_MODIFIED");
	dbHistory.setRevisionId(revisionId);
	dbHistory.setTermUUIDs(JsonUtils.writeValueAsBytes(uuids));
	Set<DbSubmissionTermHistory> dbTerms = new HashSet<>();
	dbTerms.add(createDbSubmissionTermHistory(uuid, Locale.ENGLISH.getCode(), "englishTerm", revisionId));
	dbTerms.add(createDbSubmissionTermHistory(uuid, Locale.GERMAN.getCode(), "germanTerm", revisionId));
	dbHistory.setHistory(dbTerms);
	return dbHistory;
    }

    private DbSubmissionTermEntry createDbSubmissionTermEntry(String action, String tempText) {
	DbSubmissionTermEntry entry = new DbSubmissionTermEntry();
	entry.setAction(action);
	entry.setDateCreated(new Date());
	entry.setDateModified(new Date());
	entry.setProjectId(1L);
	entry.setShortCode("SHORT_CODE");
	entry.setUserCreated("USER_CREATED");
	entry.setUserModified("USER_MODIFIED");
	entry.setUuId(TERM_ENTRY_UUID);
	Set<DbSubmissionTerm> terms = new HashSet<>();
	DbSubmissionTerm term = createSubmissionTerm(ENGLISH_TERM_UUID, "term1", Locale.ENGLISH.getCode(), tempText);
	terms.add(term);
	term = createSubmissionTerm(GERMAN_TERM_UUID, "term2", Locale.GERMAN.getCode(), tempText);
	terms.add(term);
	entry.setSubmissionTerms(terms);

	return entry;

    }

    private DbSubmissionTermHistory createDbSubmissionTermHistory(String uuid, String language, String name,
	    int revisionId) {
	DbSubmissionTermHistory termHistory = new DbSubmissionTermHistory();
	termHistory.setRevisionId(revisionId);
	termHistory.setTermEntryUuid(uuid);
	DbSubmissionTerm revision = createSubmissionTerm(uuid, name, language, "some temp text");
	termHistory.setRevision(JsonUtils.writeValueAsBytes(revision));
	return termHistory;
    }

    private DbSubmissionTerm createSubmissionTerm(String uuid, String name, String language, String tempText) {
	DbSubmissionTerm term = new DbSubmissionTerm();
	term.setDateCreated(new Date());
	term.setDateModified(new Date());
	term.setTempText(tempText);
	term.setName(name.getBytes());
	term.setTermEntryUuid(uuid);
	term.setUserCreated("USER_CREATED");
	term.setUserModified("USER_MODIFIED");
	term.setUuId(uuid);
	term.setLanguageId(language);
	return term;
    }

    private HistoryCreator getHistoryCreator() {
	return _historyCreator;
    }

}
