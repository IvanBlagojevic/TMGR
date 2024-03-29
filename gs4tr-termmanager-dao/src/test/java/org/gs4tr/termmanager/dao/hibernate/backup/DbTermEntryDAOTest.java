package org.gs4tr.termmanager.dao.hibernate.backup;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SerializationUtils;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.dao.backup.DbTermEntryDAO;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTerm;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryHistory;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermHistory;
import org.gs4tr.termmanager.model.reindex.BackupSearchCommand;
import org.hibernate.LazyInitializationException;
import org.junit.Assert;
import org.junit.Test;

public class DbTermEntryDAOTest extends AbstractBackupDAOTest {

    @Test
    public void testDelete() {
	String termEntryId = UUID.randomUUID().toString();

	DbTermEntry termEntry = createTermEntry(termEntryId);

	DbTermEntryDAO dbTermEntryDAO = getDbTermEntryDAO();

	dbTermEntryDAO.saveOrUpdate(termEntry);

	DbTermEntry entry = dbTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getTerms()));
	Assert.assertEquals(1, entry.getTerms().size());

	dbTermEntryDAO.delete(entry);

	try {
	    entry = dbTermEntryDAO.load(termEntryId);
	} catch (Exception e) {
	    entry = null;
	}

	Assert.assertNull(entry);
    }

    @Test
    public void testFindByUUID() {
	String termEntryId = UUID.randomUUID().toString();

	DbTermEntry termEntry = createTermEntry(termEntryId);

	DbTermEntryDAO dbTermEntryDAO = getDbTermEntryDAO();

	dbTermEntryDAO.saveOrUpdateLocked(Collections.singletonList(termEntry));

	DbTermEntry dbTermEntryByUuid = dbTermEntryDAO.findByUUID(termEntryId);

	Assert.assertNotNull(dbTermEntryByUuid);
	Assert.assertEquals(1, dbTermEntryByUuid.getTerms().size());
	Assert.assertEquals(1, dbTermEntryByUuid.getDescriptions().size());
	Assert.assertEquals(1, dbTermEntryByUuid.getHistory().size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetDbTermEntries() {
	String termEntryId = UUID.randomUUID().toString();

	DbTermEntry termEntry = createTermEntry(termEntryId);

	DbTermEntryDAO dbTermEntryDAO = getDbTermEntryDAO();

	dbTermEntryDAO.saveOrUpdate(termEntry);

	BackupSearchCommand command = new BackupSearchCommand(Collections.EMPTY_LIST, Collections.EMPTY_LIST);

	PagedList<DbTermEntry> page = dbTermEntryDAO.getDbTermEntries(new PagedListInfo(), command);

	Assert.assertNotNull(page);
	Assert.assertEquals(1, page.getTotalCount().longValue());
	Assert.assertTrue(ArrayUtils.isNotEmpty(page.getElements()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetDbTermEntriesByProjectIds() {
	String termEntryId = UUID.randomUUID().toString();

	DbTermEntry termEntry = createTermEntry(termEntryId);

	DbTermEntryDAO dbTermEntryDAO = getDbTermEntryDAO();

	dbTermEntryDAO.saveOrUpdate(termEntry);

	BackupSearchCommand command = new BackupSearchCommand(Collections.singletonList(PROJECT_ID),
		Collections.EMPTY_LIST);

	PagedList<DbTermEntry> page = dbTermEntryDAO.getDbTermEntries(new PagedListInfo(), command);

	Assert.assertNotNull(page);
	Assert.assertEquals(1, page.getTotalCount().longValue());
	Assert.assertTrue(ArrayUtils.isNotEmpty(page.getElements()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetDbTermEntriesByShortCode() {
	String termEntryId = UUID.randomUUID().toString();

	DbTermEntry termEntry = createTermEntry(termEntryId);

	DbTermEntryDAO dbTermEntryDAO = getDbTermEntryDAO();

	dbTermEntryDAO.saveOrUpdate(termEntry);

	BackupSearchCommand command = new BackupSearchCommand(Collections.EMPTY_LIST,
		Collections.singletonList(SHORTCODE));

	PagedList<DbTermEntry> page = dbTermEntryDAO.getDbTermEntries(new PagedListInfo(), command);

	Assert.assertNotNull(page);
	Assert.assertEquals(1, page.getTotalCount().longValue());
	Assert.assertTrue(ArrayUtils.isNotEmpty(page.getElements()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetDbTermEntriesForRecode() {
	String termEntryId = UUID.randomUUID().toString();

	DbTermEntry termEntry = createTermEntry(termEntryId);

	DbTermEntryDAO dbTermEntryDAO = getDbTermEntryDAO();

	dbTermEntryDAO.saveOrUpdateLocked(Collections.singletonList(termEntry));

	BackupSearchCommand command = new BackupSearchCommand(Collections.singletonList(PROJECT_ID),
		Collections.EMPTY_LIST);

	PagedList<DbTermEntry> page = dbTermEntryDAO.getDbTermEntriesForRecode(new PagedListInfo(), command);

	Assert.assertNotNull(page);
	Assert.assertEquals(1, page.getTotalCount().longValue());
	Assert.assertTrue(ArrayUtils.isNotEmpty(page.getElements()));

	Set<DbTermEntryHistory> histories = page.getElements()[0].getHistory();
	Assert.assertEquals(histories.size(), 1);

	DbTermEntryHistory history = histories.iterator().next();
	Assert.assertNotNull(history);

	Set<DbTermHistory> termHistories = history.getHistory();
	Assert.assertEquals(termHistories.size(), 1);

	DbTermHistory termHistory = termHistories.iterator().next();
	Assert.assertNotNull(termHistory);

	boolean exceptionThrown = false;

	try {
	    // getDbTermEntriesForRecode() method fetch history only
	    Set<DbTerm> terms = page.getElements()[0].getTerms();
	    terms.iterator().next();

	} catch (LazyInitializationException e) {
	    exceptionThrown = true;
	}
	Assert.assertTrue(exceptionThrown);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetTotalCount() {
	DbTermEntryDAO dbTermEntryDAO = getDbTermEntryDAO();

	String termEntryId = UUID.randomUUID().toString();

	DbTermEntry termEntry = createTermEntry(termEntryId);

	dbTermEntryDAO.saveOrUpdate(termEntry);

	DbTermEntry entry = dbTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getTerms()));
	Assert.assertEquals(1, entry.getTerms().size());

	long count = dbTermEntryDAO.getTotalCount(Collections.EMPTY_LIST);
	Assert.assertEquals(1, count);
    }

    @Test
    public void testGetTotalCount_ShortCode_case() {
	DbTermEntryDAO dbTermEntryDAO = getDbTermEntryDAO();

	String termEntryId = UUID.randomUUID().toString();

	DbTermEntry termEntry = createTermEntry(termEntryId);

	dbTermEntryDAO.saveOrUpdate(termEntry);

	DbTermEntry entry = dbTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getTerms()));
	Assert.assertEquals(1, entry.getTerms().size());

	long count = dbTermEntryDAO.getTotalCount(Collections.singletonList(PROJECT_ID));
	Assert.assertEquals(1, count);
    }

    @Test
    public void testRemoveChild() {
	String termEntryId = UUID.randomUUID().toString();

	DbTermEntry termEntry = createTermEntry(termEntryId);

	DbTermEntryDAO dbTermEntryDAO = getDbTermEntryDAO();

	dbTermEntryDAO.saveOrUpdate(termEntry);

	DbTermEntry entry = dbTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getTerms()));
	Assert.assertEquals(1, entry.getTerms().size());

	entry.setDescriptions(null);
	dbTermEntryDAO.saveOrUpdate(entry);

	entry = dbTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isEmpty(entry.getDescriptions()));
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getTerms()));
	Assert.assertEquals(1, entry.getTerms().size());

	DbTerm term = entry.getTerms().iterator().next();
	Assert.assertTrue(CollectionUtils.isNotEmpty(term.getDescriptions()));

	term.setDescriptions(null);
	dbTermEntryDAO.saveOrUpdate(entry);

	entry = dbTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isEmpty(entry.getDescriptions()));
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getTerms()));
	Assert.assertEquals(1, entry.getTerms().size());

	term = entry.getTerms().iterator().next();
	Assert.assertTrue(CollectionUtils.isEmpty(term.getDescriptions()));

	entry.setTerms(null);
	dbTermEntryDAO.saveOrUpdate(entry);

	entry = dbTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isEmpty(entry.getDescriptions()));
	Assert.assertTrue(CollectionUtils.isEmpty(entry.getTerms()));
    }

    @Test
    public void testRemoveTerm() {
	String termEntryId = UUID.randomUUID().toString();

	DbTermEntry termEntry = createTermEntry(termEntryId);

	DbTermEntryDAO dbTermEntryDAO = getDbTermEntryDAO();

	dbTermEntryDAO.saveOrUpdate(termEntry);

	DbTermEntry entry = dbTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getTerms()));
	Assert.assertEquals(1, entry.getTerms().size());

	entry.setTerms(new HashSet<>());

	dbTermEntryDAO.update(entry);

	entry = dbTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isEmpty(entry.getTerms()));
    }

    @Test
    public void testSaveOrUpdate() {
	String termEntryId = UUID.randomUUID().toString();

	DbTermEntry termEntry = createTermEntry(termEntryId);

	DbTermEntryDAO dbTermEntryDAO = getDbTermEntryDAO();

	dbTermEntryDAO.saveOrUpdate(termEntry);

	DbTermEntry entry = dbTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getTerms()));
	Assert.assertEquals(1, entry.getTerms().size());
    }

    @Test
    public void testUpdate() {
	String termEntryId = UUID.randomUUID().toString();

	DbTermEntry termEntry = createTermEntry(termEntryId);

	DbTermEntryDAO dbTermEntryDAO = getDbTermEntryDAO();

	dbTermEntryDAO.saveOrUpdateLocked(Collections.singletonList(termEntry));

	DbTermEntry entry = dbTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getTerms()));
	Assert.assertEquals(1, entry.getTerms().size());

	String text = "updated term text";

	DbTerm term = entry.getTerms().iterator().next();
	term.setName(text.getBytes());

	dbTermEntryDAO.saveOrUpdateLocked(Collections.singletonList(entry));
	entry = dbTermEntryDAO.findByUuid(termEntryId, true);
	term = entry.getTerms().iterator().next();

	Assert.assertEquals(text, new String(term.getName()));
    }

    @Test
    public void testUpdateEntitiesForRecodeOrClone() {
	String newTermName = "new term name";
	String termEntryId = UUID.randomUUID().toString();

	DbTermEntry termEntry = createTermEntry(termEntryId);

	DbTermEntryDAO dbTermEntryDAO = getDbTermEntryDAO();

	dbTermEntryDAO.saveOrUpdate(termEntry);

	DbTermEntry entry = dbTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);

	// Set new term name
	entry.getTerms().iterator().next().setName(newTermName.getBytes());

	getCurrentSession().evict(entry);

	// Perform update
	dbTermEntryDAO.updateEntitiesForRecodeOrClone(Collections.singletonList(entry));

	DbTermEntry entryAfterUpdate = dbTermEntryDAO.findByUuid(termEntryId, true);

	String termNameAfter = entryAfterUpdate.getTerms().iterator().next().getNameAsString();

	Assert.assertEquals(newTermName, termNameAfter);

    }

    @Test
    public void testUpdateTermEntryHistory() {
	String termEntryUuid = UUID.randomUUID().toString();

	DbTermEntry termEntry = createTermEntry(termEntryUuid);

	DbTermEntryDAO dbTermEntryDAO = getDbTermEntryDAO();

	dbTermEntryDAO.saveOrUpdate(termEntry);

	DbTermEntry entry = dbTermEntryDAO.findByUuid(termEntryUuid, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getTerms()));
	Assert.assertEquals(1, entry.getTerms().size());

	byte[] revision1 = "Test term entry history revision 1!".getBytes(StandardCharsets.UTF_8);

	DbTermEntryHistory history1 = new DbTermEntryHistory();
	history1.setRevisionId(1);
	history1.setDateModified(new Date());
	history1.setUserModified("User1");
	history1.setHistoryAction("ADDED");
	HashSet<String> uuIds = new HashSet<>();
	uuIds.add(termEntryUuid);
	history1.setTermUUIDs(SerializationUtils.serialize(uuIds));
	DbTermHistory termHistory = new DbTermHistory();
	termHistory.setRevisionId(1);
	termHistory.setTermEntryUuid(termEntryUuid);

	termHistory.setRevision(revision1);
	Set<DbTermHistory> histories = new HashSet<>();
	histories.add(termHistory);
	history1.setHistory(histories);
	history1.setTermEntryUUid(termEntryUuid);

	history1.setRevisionId(1);

	Set<DbTermEntryHistory> teHistory = new HashSet<>();
	teHistory.add(history1);

	entry.setHistory(teHistory);
	dbTermEntryDAO.saveOrUpdate(entry);

	entry = dbTermEntryDAO.findByUuid(termEntryUuid, true);
	teHistory = entry.getHistory();

	Assert.assertTrue(CollectionUtils.isNotEmpty(teHistory));
	Assert.assertEquals(1, teHistory.size());

	byte[] revision2 = "Test term entry history revision 2!".getBytes(StandardCharsets.UTF_8);

	DbTermEntryHistory history2 = new DbTermEntryHistory();
	history2.setTermEntryUUid(termEntryUuid);
	// history2.setTermEntry(termEntry);
	history2.setDateModified(new Date());
	history2.setUserModified("user2");
	history2.setRevisionId(2);
	history2.setHistoryAction("EDITED");
	history2.setTermUUIDs(SerializationUtils.serialize(uuIds));
	DbTermHistory termHistory2 = new DbTermHistory();
	termHistory2.setRevision(revision2);
	termHistory2.setRevisionId(2);
	termHistory2.setTermEntryUuid(termEntryUuid);
	Set<DbTermHistory> histories2 = new HashSet<>();
	histories2.add(termHistory2);

	history2.setHistory(histories2);

	teHistory.add(history2);

	entry.setHistory(teHistory);
	dbTermEntryDAO.saveOrUpdate(entry);

	DbTermEntry termEntry1 = dbTermEntryDAO.findByUuid(termEntryUuid, true);
	Set<DbTermEntryHistory> teHistory1 = termEntry1.getHistory();
	Assert.assertTrue(CollectionUtils.isNotEmpty(teHistory1));
	Assert.assertEquals(2, teHistory1.size());
    }

    @Test
    public void testUpdateTermLanguage() {

	String srLanguage = "sr";

	String deLanguage = "de";

	String termEntryId = UUID.randomUUID().toString();

	DbTermEntry termEntry = createTermEntry(termEntryId);

	DbTermEntryDAO dbTermEntryDAO = getDbTermEntryDAO();

	dbTermEntryDAO.saveOrUpdate(termEntry);

	DbTermEntry entry = dbTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);

	Assert.assertEquals(srLanguage, entry.getTerms().iterator().next().getLanguageId());

	getDbTermEntryDAO().updateTermLanguage(1L, srLanguage, deLanguage);
	dbTermEntryDAO.flush();
	dbTermEntryDAO.clear();

	DbTermEntry entryAfterUpdate = dbTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entryAfterUpdate);

	Assert.assertEquals(deLanguage, entryAfterUpdate.getTerms().iterator().next().getLanguageId());

    }
}
