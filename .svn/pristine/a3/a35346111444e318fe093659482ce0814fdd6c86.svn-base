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
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTerm;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryHistory;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermHistory;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RegularHistoryImplTest extends AbstractHistoryCreatorTest {

    private static final String ENGLISH_TERM_UUID = UUID.randomUUID().toString();

    private static final String GERMAN_TERM_UUID = UUID.randomUUID().toString();

    private static final String TERM_ENTRY_UUID = UUID.randomUUID().toString();

    @Autowired
    @Qualifier("regularHistoryCreator")
    private HistoryCreator _historyCreator;

    @Test
    public void testEmptyHistory() {
	DbTermEntry entry = createDbTermEntry(Action.ADDED.name());
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
	DbTermEntry entry = createDbTermEntry(Action.EDITED.name());
	HashSet<String> uuids = new HashSet<>();

	DbTermEntryHistory entryHistory = createDbEntryHistory(Action.ADDED.name(), TERM_ENTRY_UUID, 1, uuids);
	Set<DbTermEntryHistory> histories = new HashSet<>();
	histories.add(entryHistory);
	entry.setHistory(histories);
	List<TermEntry> entries = getHistoryCreator().createHistory(entry);
	Assert.assertEquals(2, entries.size());
	TermEntry termEntry = entries.get(0);
	Assert.assertEquals(Long.valueOf(2), termEntry.getRevisionId());
	Assert.assertEquals(Action.EDITED.name(), termEntry.getAction().name());
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
	Assert.assertEquals(Action.ADDED.name(), termEntry.getAction().name());
	engTerms = termEntry.getLanguageTerms().get(Locale.ENGLISH.getCode());
	Assert.assertEquals(1, engTerms.size());
	engTerm = engTerms.iterator().next();
	Assert.assertEquals("englishTerm", engTerm.getName());

	deTerms = termEntry.getLanguageTerms().get(Locale.GERMAN.getCode());
	Assert.assertEquals(1, deTerms.size());
	deTerm = deTerms.iterator().next();
	Assert.assertEquals("germanTerm", deTerm.getName());

    }

    @Test
    public void testTermDeleted() {
	DbTermEntry entry = createDbTermEntry(Action.EDITED.name());
	HashSet<String> uuids = new HashSet<>();

	DbTermEntryHistory entryHistory = createDbEntryHistory(Action.ADDED.name(), TERM_ENTRY_UUID, 1, uuids);
	Set<DbTermEntryHistory> histories = new HashSet<>();
	histories.add(entryHistory);
	entry.setHistory(histories);
	Set<DbTerm> dbTerms = new HashSet<>();
	dbTerms.add(createTerm(GERMAN_TERM_UUID, "term1", Locale.GERMAN.getCode()));
	entry.setTerms(dbTerms);
	List<TermEntry> entries = getHistoryCreator().createHistory(entry);
	Assert.assertEquals(2, entries.size());
	TermEntry termEntry = entries.get(0);
	Assert.assertEquals(Long.valueOf(2), termEntry.getRevisionId());
	Set<Term> germanTerms = termEntry.getLanguageTerms().get(Locale.GERMAN.getCode());
	Assert.assertEquals(1, germanTerms.size());
	Term deTerm = germanTerms.iterator().next();
	Assert.assertEquals("term1", deTerm.getName());
	Assert.assertFalse(termEntry.getLanguageTerms().containsKey(Locale.ENGLISH.getCode()));

	termEntry = entries.get(1);
	Assert.assertEquals(Long.valueOf(1), termEntry.getRevisionId());
	germanTerms = termEntry.getLanguageTerms().get(Locale.GERMAN.getCode());
	Assert.assertEquals(1, germanTerms.size());
	deTerm = germanTerms.iterator().next();
	Assert.assertEquals("germanTerm", deTerm.getName());
	Assert.assertTrue(termEntry.getLanguageTerms().containsKey(Locale.ENGLISH.getCode()));

    }

    private DbTermEntryHistory createDbEntryHistory(String action, String uuid, int revisionId, HashSet<String> uuids) {
	DbTermEntryHistory dbHistory = new DbTermEntryHistory();
	dbHistory.setHistoryAction(action);
	dbHistory.setDateModified(new Date());
	dbHistory.setUserModified("USER_MODIFIED");
	dbHistory.setRevisionId(revisionId);
	dbHistory.setTermUUIDs(JsonUtils.writeValueAsBytes(uuids));
	Set<DbTermHistory> dbTerms = new HashSet<>();
	dbTerms.add(createDbTermHistory(uuid, Locale.ENGLISH.getCode(), "englishTerm", revisionId));
	dbTerms.add(createDbTermHistory(uuid, Locale.GERMAN.getCode(), "germanTerm", revisionId));
	dbHistory.setHistory(dbTerms);
	return dbHistory;
    }

    private DbTermEntry createDbTermEntry(String action) {
	DbTermEntry entry = new DbTermEntry();
	entry.setAction(action);
	entry.setDateCreated(new Date());
	entry.setDateModified(new Date());
	entry.setProjectId(1L);
	entry.setShortCode("SHORT_CODE");
	entry.setUserCreated("USER_CREATED");
	entry.setUserModified("USER_MODIFIED");
	entry.setUuId(TERM_ENTRY_UUID);
	Set<DbTerm> terms = new HashSet<>();
	DbTerm term = createTerm(ENGLISH_TERM_UUID, "term1", Locale.ENGLISH.getCode());
	terms.add(term);
	term = createTerm(GERMAN_TERM_UUID, "term2", Locale.GERMAN.getCode());
	terms.add(term);
	entry.setTerms(terms);

	return entry;

    }

    private DbTermHistory createDbTermHistory(String uuid, String language, String name, int revisionId) {
	DbTermHistory termHistory = new DbTermHistory();
	termHistory.setRevisionId(revisionId);
	termHistory.setTermEntryUuid(uuid);
	DbTerm revision = createTerm(uuid, name, language);
	termHistory.setRevision(JsonUtils.writeValueAsBytes(revision));
	return termHistory;
    }

    private DbTerm createTerm(String uuid, String name, String language) {
	DbTerm term = new DbTerm();
	term.setDateCreated(new Date());
	term.setDateModified(new Date());
	term.setDisabled(false);
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
