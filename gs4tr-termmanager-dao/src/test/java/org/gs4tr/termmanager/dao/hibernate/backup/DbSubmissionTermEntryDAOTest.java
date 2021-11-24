package org.gs4tr.termmanager.dao.hibernate.backup;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SerializationUtils;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.dao.backup.DbSubmissionTermEntryDAO;
import org.gs4tr.termmanager.dao.hibernate.AbstractSpringDAOIntegrationTest;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryDescription;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbComment;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbPriority;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTerm;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermDescription;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntryDescription;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntryHistory;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermHistory;
import org.gs4tr.termmanager.model.reindex.BackupSearchCommand;
import org.hibernate.LazyInitializationException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DbSubmissionTermEntryDAOTest extends AbstractSpringDAOIntegrationTest {

    private static final long PROJECT_ID = 1L;

    private static final String PROJECT_NAME = "HPE";

    private static final String SHORTCODE = "HPE000001";

    private static final String STATUS = ItemStatusTypeHolder.PROCESSED.getName();

    private static final long SUBMISSION_ID = 1L;

    private static final String SUBMISSION_NAME = "SUB1";

    private static final String USER = "user";

    @Autowired
    private DbSubmissionTermEntryDAO _dbSubmissionTermEntryDAO;

    @Test
    public void testDelete() {
	String termEntryId = UUID.randomUUID().toString();

	DbSubmissionTermEntry termEntry = createTermEntry(termEntryId);

	DbSubmissionTermEntryDAO dbSubmissionTermEntryDAO = getDbSubmissionTermEntryDAO();

	dbSubmissionTermEntryDAO.saveOrUpdate(termEntry);

	DbSubmissionTermEntry entry = dbSubmissionTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getSubmissionTerms()));
	Assert.assertEquals(1, entry.getSubmissionTerms().size());

	dbSubmissionTermEntryDAO.delete(entry);

	try {
	    entry = dbSubmissionTermEntryDAO.load(termEntryId);
	} catch (Exception e) {
	    entry = null;
	}

	Assert.assertNull(entry);
    }

    @Test
    public void testFindByUUIds() throws UnsupportedEncodingException {
	String termEntryUuid = UUID.randomUUID().toString();

	DbSubmissionTermEntry termEntry = createTermEntry(termEntryUuid);

	DbSubmissionTermEntryDAO dbSubmissionTermEntryDAO = getDbSubmissionTermEntryDAO();

	dbSubmissionTermEntryDAO.saveOrUpdate(termEntry);

	DbSubmissionTermEntry entry = dbSubmissionTermEntryDAO.findByUuid(termEntryUuid, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getSubmissionTerms()));
	Assert.assertEquals(1, entry.getSubmissionTerms().size());

	byte[] revision1 = SerializationUtils.serialize(createTerm(entry));

	DbSubmissionTermEntryHistory history1 = new DbSubmissionTermEntryHistory();

	DbSubmissionTermHistory termHistory = new DbSubmissionTermHistory();
	termHistory.setRevision(revision1);
	termHistory.setTermEntryUuid(termEntryUuid);
	termHistory.setRevisionId(1);

	Set<DbSubmissionTermHistory> histories = new HashSet<DbSubmissionTermHistory>();
	histories.add(termHistory);
	history1.setHistory(histories);
	history1.setHistoryAction("ADDED");
	history1.setUserModified("User1");
	history1.setDateModified(new Date());
	history1.setTermEntryUUid(termEntryUuid);
	history1.setRevisionId(1);

	Set<DbSubmissionTermEntryHistory> teHistory = new HashSet<DbSubmissionTermEntryHistory>();
	teHistory.add(history1);

	entry.setHistory(teHistory);
	dbSubmissionTermEntryDAO.saveOrUpdate(entry);

	List<DbSubmissionTermEntry> entries = dbSubmissionTermEntryDAO.findByUuids(Arrays.asList(termEntryUuid), true);

	Assert.assertNotNull(entries);
	Assert.assertEquals(1, entries.size());
	DbSubmissionTermEntry dbEntry = entries.get(0);
	Assert.assertEquals("NOT_AVAILABLE", dbEntry.getAction());
	Set<DbSubmissionTermEntryHistory> dbSubmissionHistories = dbEntry.getHistory();
	Assert.assertFalse(dbSubmissionHistories.isEmpty());
	DbSubmissionTermEntryHistory dbSubmissionHistory = dbSubmissionHistories.iterator().next();
	Set<DbSubmissionTermHistory> dbTermHistories = dbSubmissionHistory.getHistory();
	Assert.assertFalse(dbTermHistories.isEmpty());
	DbSubmissionTermHistory dbTermHistory = dbTermHistories.iterator().next();
	Assert.assertNotNull(dbTermHistory.getRevision());

    }

    @Test
    public void testFindByUuids() {
	String termEntryId = UUID.randomUUID().toString();

	DbSubmissionTermEntry termEntry = createTermEntry(termEntryId);

	DbSubmissionTermEntryDAO dbSubmissionTermEntryDAO = getDbSubmissionTermEntryDAO();

	dbSubmissionTermEntryDAO.saveOrUpdateLocked(Collections.singleton(termEntry));

	DbSubmissionTermEntry dbSubmissionByUuid = dbSubmissionTermEntryDAO.findByUuid(termEntryId);

	Assert.assertNotNull(dbSubmissionByUuid);
	Assert.assertEquals(dbSubmissionByUuid.getSubmissionTerms().size(), 1);
	Assert.assertEquals(dbSubmissionByUuid.getDescriptions().size(), 1);
    }

    @Test
    public void testGetDbSubmissionTermEntriesForRecode() throws UnsupportedEncodingException {
	String termEntryId = UUID.randomUUID().toString();

	DbSubmissionTermEntry subTermEntry = createTermEntry(termEntryId);

	subTermEntry.setHistory(createSubHistory(termEntryId));

	DbSubmissionTermEntryDAO dao = getDbSubmissionTermEntryDAO();

	dao.saveOrUpdateLocked(Collections.singletonList(subTermEntry));

	BackupSearchCommand command = new BackupSearchCommand(Collections.singletonList(PROJECT_ID), null);

	PagedList<DbSubmissionTermEntry> page = dao.getDbSubmissionTermEntriesForRecode(new PagedListInfo(), command);

	Assert.assertNotNull(page);
	Assert.assertEquals(1, page.getTotalCount().longValue());
	Assert.assertTrue(ArrayUtils.isNotEmpty(page.getElements()));

	Set<DbSubmissionTermEntryHistory> histories = page.getElements()[0].getHistory();
	Assert.assertEquals(histories.size(), 1);

	DbSubmissionTermEntryHistory history = histories.iterator().next();
	Assert.assertNotNull(history);

	Set<DbSubmissionTermHistory> subTermHistories = history.getHistory();
	Assert.assertEquals(subTermHistories.size(), 1);

	DbSubmissionTermHistory subTermHistory = subTermHistories.iterator().next();
	Assert.assertNotNull(subTermHistory);

	boolean exceptionThrown = false;

	try {
	    // getDbSubmissionTermEntriesForRecode() method fetch history only
	    Set<DbSubmissionTerm> submissionTerms = page.getElements()[0].getSubmissionTerms();
	    submissionTerms.iterator().next();

	} catch (LazyInitializationException e) {
	    exceptionThrown = true;
	}
	Assert.assertTrue(exceptionThrown);
    }

    @Test
    public void testGetDbTermEntries() {
	String termEntryId = UUID.randomUUID().toString();

	DbSubmissionTermEntry termEntry = createTermEntry(termEntryId);

	DbSubmissionTermEntryDAO dbSubmissionTermEntryDAO = getDbSubmissionTermEntryDAO();

	dbSubmissionTermEntryDAO.saveOrUpdate(termEntry);

	BackupSearchCommand command = new BackupSearchCommand(null, null);
	PagedList<DbSubmissionTermEntry> page = dbSubmissionTermEntryDAO.getDbSubmissionTermEntries(new PagedListInfo(),
		command);

	Assert.assertNotNull(page);
	Assert.assertEquals(1, page.getTotalCount().longValue());
	Assert.assertTrue(ArrayUtils.isNotEmpty(page.getElements()));
    }

    @Test
    public void testGetDbTermEntriesByShortCode() {
	String termEntryId = UUID.randomUUID().toString();

	DbSubmissionTermEntry termEntry = createTermEntry(termEntryId);

	DbSubmissionTermEntryDAO dbSubmissionTermEntryDAO = getDbSubmissionTermEntryDAO();

	dbSubmissionTermEntryDAO.saveOrUpdate(termEntry);

	BackupSearchCommand command = new BackupSearchCommand(Arrays.asList(PROJECT_ID), Collections.EMPTY_LIST);
	PagedList<DbSubmissionTermEntry> page = dbSubmissionTermEntryDAO.getDbSubmissionTermEntries(new PagedListInfo(),
		command);

	Assert.assertNotNull(page);
	Assert.assertEquals(1, page.getTotalCount().longValue());
	Assert.assertTrue(ArrayUtils.isNotEmpty(page.getElements()));
    }

    @Test
    public void testGetTotalCount() {
	String termEntryId = UUID.randomUUID().toString();

	DbSubmissionTermEntry termEntry = createTermEntry(termEntryId);

	DbSubmissionTermEntryDAO dbSubmissionTermEntryDAO = getDbSubmissionTermEntryDAO();

	dbSubmissionTermEntryDAO.saveOrUpdate(termEntry);

	DbSubmissionTermEntry entry = dbSubmissionTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getSubmissionTerms()));
	Assert.assertEquals(1, entry.getSubmissionTerms().size());

	long count = dbSubmissionTermEntryDAO.getTotalCount(Collections.EMPTY_LIST);
	Assert.assertEquals(1, count);
    }

    @Test
    public void testGetTotalCount_Shortcode_case() {
	String termEntryId = UUID.randomUUID().toString();

	DbSubmissionTermEntry termEntry = createTermEntry(termEntryId);

	DbSubmissionTermEntryDAO dbSubmissionTermEntryDAO = getDbSubmissionTermEntryDAO();

	dbSubmissionTermEntryDAO.saveOrUpdate(termEntry);

	DbSubmissionTermEntry entry = dbSubmissionTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getSubmissionTerms()));
	Assert.assertEquals(1, entry.getSubmissionTerms().size());

	long count = dbSubmissionTermEntryDAO.getTotalCount(Arrays.asList(PROJECT_ID));
	Assert.assertEquals(1, count);
    }

    @Test
    public void testRemoveLastRevision() throws UnsupportedEncodingException {
	String termEntryUuid = UUID.randomUUID().toString();

	DbSubmissionTermEntry termEntry = createTermEntry(termEntryUuid);

	DbSubmissionTermEntryDAO dbSubmissionTermEntryDAO = getDbSubmissionTermEntryDAO();

	dbSubmissionTermEntryDAO.saveOrUpdate(termEntry);

	DbSubmissionTermEntry entry = dbSubmissionTermEntryDAO.findByUuid(termEntryUuid, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getSubmissionTerms()));
	Assert.assertEquals(1, entry.getSubmissionTerms().size());

	byte[] revision1 = "Test term entry history revision 1!".getBytes("UTF-8");

	DbSubmissionTermEntryHistory history1 = new DbSubmissionTermEntryHistory();

	DbSubmissionTermHistory termHistory = new DbSubmissionTermHistory();
	termHistory.setRevision(revision1);
	termHistory.setTermEntryUuid(termEntryUuid);
	termHistory.setRevisionId(1);

	Set<DbSubmissionTermHistory> histories = new HashSet<DbSubmissionTermHistory>();
	histories.add(termHistory);
	history1.setHistory(histories);
	history1.setHistoryAction("ADDED");
	history1.setUserModified("User1");
	history1.setDateModified(new Date());
	history1.setTermEntryUUid(termEntryUuid);
	history1.setRevisionId(1);

	Set<DbSubmissionTermEntryHistory> teHistory = new HashSet<DbSubmissionTermEntryHistory>();
	teHistory.add(history1);

	entry.setHistory(teHistory);
	dbSubmissionTermEntryDAO.saveOrUpdate(entry);

	entry = dbSubmissionTermEntryDAO.findByUuid(termEntryUuid, true);
	teHistory = entry.getHistory();

	Assert.assertTrue(CollectionUtils.isNotEmpty(teHistory));
	Assert.assertEquals(1, teHistory.size());

	byte[] revision2 = "Test term entry history revision 2!".getBytes("UTF-8");

	DbSubmissionTermEntryHistory history2 = new DbSubmissionTermEntryHistory();
	DbSubmissionTermHistory termHistory2 = new DbSubmissionTermHistory();
	termHistory2.setRevision(revision2);
	termHistory2.setTermEntryUuid(termEntryUuid);
	termHistory2.setRevisionId(2);

	Set<DbSubmissionTermHistory> histories2 = new HashSet<DbSubmissionTermHistory>();
	histories2.add(termHistory2);

	history2.setHistory(histories2);
	history2.setTermEntryUUid(termEntryUuid);
	history2.setRevisionId(2);
	history2.setHistoryAction("EDITED");
	history2.setUserModified("user3");
	history2.setDateModified(new Date());
	teHistory.add(history2);

	entry.setHistory(teHistory);
	dbSubmissionTermEntryDAO.saveOrUpdate(entry);

	DbSubmissionTermEntry termEntry1 = dbSubmissionTermEntryDAO.findByUuid(termEntryUuid, true);
	Set<DbSubmissionTermEntryHistory> teHistory1 = termEntry1.getHistory();

	Assert.assertTrue(CollectionUtils.isNotEmpty(teHistory1));
	Assert.assertEquals(2, teHistory1.size());

	Set<DbSubmissionTermEntryHistory> histories3 = termEntry1.getHistory();
	Set<DbSubmissionTermEntryHistory> newHistory = new HashSet<DbSubmissionTermEntryHistory>();
	for (DbSubmissionTermEntryHistory dbSubmissionTermEntryHistory : histories3) {
	    {
		if (dbSubmissionTermEntryHistory.getRevisionId() < 2) {
		    newHistory.add(dbSubmissionTermEntryHistory);
		    break;

		}
	    }
	}
	termEntry1.getHistory().clear();
	termEntry1.setHistory(newHistory);

	dbSubmissionTermEntryDAO.saveOrUpdate(termEntry1);
	DbSubmissionTermEntry afterRollback = dbSubmissionTermEntryDAO.findByUuid(termEntryUuid, true);
	Assert.assertEquals(1, afterRollback.getHistory().size());
    }

    @Test
    public void testRemoveTerm() {
	String termEntryId = UUID.randomUUID().toString();

	DbSubmissionTermEntry termEntry = createTermEntry(termEntryId);

	DbSubmissionTermEntryDAO dbSubmissionTermEntryDAO = getDbSubmissionTermEntryDAO();

	dbSubmissionTermEntryDAO.saveOrUpdate(termEntry);

	DbSubmissionTermEntry entry = dbSubmissionTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getSubmissionTerms()));
	Assert.assertEquals(1, entry.getSubmissionTerms().size());

	entry.setSubmissionTerms(new HashSet<DbSubmissionTerm>());

	dbSubmissionTermEntryDAO.update(entry);

	entry = dbSubmissionTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isEmpty(entry.getSubmissionTerms()));
    }

    @Test
    public void testSaveOrUpdate() {
	DbSubmissionTermEntryDAO dbSubmissionTermEntryDAO = getDbSubmissionTermEntryDAO();

	String termEntryId = UUID.randomUUID().toString();

	DbSubmissionTermEntry entry = createTermEntry(termEntryId);
	dbSubmissionTermEntryDAO.saveOrUpdate(entry);

	entry = dbSubmissionTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getSubmissionTerms()));
	Assert.assertEquals(1, entry.getSubmissionTerms().size());
    }

    @Test
    public void testUpdate() {
	String termEntryId = UUID.randomUUID().toString();

	DbSubmissionTermEntry entry = createTermEntry(termEntryId);

	DbSubmissionTermEntryDAO dbSubmissionTermEntryDAO = getDbSubmissionTermEntryDAO();

	dbSubmissionTermEntryDAO.saveOrUpdate(entry);

	entry = dbSubmissionTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getSubmissionTerms()));
	Assert.assertEquals(1, entry.getSubmissionTerms().size());

	String text = "updated term text";

	DbSubmissionTerm term = entry.getSubmissionTerms().iterator().next();
	term.setName(text.getBytes());

	dbSubmissionTermEntryDAO.update(entry);
	entry = dbSubmissionTermEntryDAO.findByUuid(termEntryId, true);
	term = entry.getSubmissionTerms().iterator().next();

	Assert.assertEquals(text, new String(term.getName()));
    }

    @Test
    public void testUpdateTermEntryHistory() throws UnsupportedEncodingException {
	String termEntryUuid = UUID.randomUUID().toString();

	DbSubmissionTermEntry termEntry = createTermEntry(termEntryUuid);

	DbSubmissionTermEntryDAO dbSubmissionTermEntryDAO = getDbSubmissionTermEntryDAO();

	dbSubmissionTermEntryDAO.saveOrUpdate(termEntry);

	DbSubmissionTermEntry entry = dbSubmissionTermEntryDAO.findByUuid(termEntryUuid, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getSubmissionTerms()));
	Assert.assertEquals(1, entry.getSubmissionTerms().size());

	byte[] revision1 = "Test term entry history revision 1!".getBytes("UTF-8");

	DbSubmissionTermEntryHistory history1 = new DbSubmissionTermEntryHistory();

	DbSubmissionTermHistory termHistory = new DbSubmissionTermHistory();
	termHistory.setRevision(revision1);
	termHistory.setTermEntryUuid(termEntryUuid);
	termHistory.setRevisionId(1);

	Set<DbSubmissionTermHistory> histories = new HashSet<DbSubmissionTermHistory>();
	histories.add(termHistory);
	history1.setHistory(histories);
	history1.setHistoryAction("ADDED");
	history1.setUserModified("User1");
	history1.setDateModified(new Date());
	history1.setTermEntryUUid(termEntryUuid);
	history1.setRevisionId(1);

	Set<DbSubmissionTermEntryHistory> teHistory = new HashSet<DbSubmissionTermEntryHistory>();
	teHistory.add(history1);

	entry.setHistory(teHistory);
	dbSubmissionTermEntryDAO.saveOrUpdate(entry);

	entry = dbSubmissionTermEntryDAO.findByUuid(termEntryUuid, true);
	teHistory = entry.getHistory();

	Assert.assertTrue(CollectionUtils.isNotEmpty(teHistory));
	Assert.assertEquals(1, teHistory.size());

	byte[] revision2 = "Test term entry history revision 2!".getBytes("UTF-8");

	DbSubmissionTermEntryHistory history2 = new DbSubmissionTermEntryHistory();
	DbSubmissionTermHistory termHistory2 = new DbSubmissionTermHistory();
	termHistory2.setRevision(revision2);
	termHistory2.setTermEntryUuid(termEntryUuid);
	termHistory2.setRevisionId(2);

	Set<DbSubmissionTermHistory> histories2 = new HashSet<DbSubmissionTermHistory>();
	histories2.add(termHistory2);

	history2.setHistory(histories2);
	history2.setTermEntryUUid(termEntryUuid);
	history2.setRevisionId(2);
	history2.setHistoryAction("EDITED");
	history2.setUserModified("user3");
	history2.setDateModified(new Date());
	teHistory.add(history2);

	entry.setHistory(teHistory);
	dbSubmissionTermEntryDAO.saveOrUpdate(entry);

	DbSubmissionTermEntry termEntry1 = dbSubmissionTermEntryDAO.findByUuid(termEntryUuid, true);
	Set<DbSubmissionTermEntryHistory> teHistory1 = termEntry1.getHistory();

	Assert.assertTrue(CollectionUtils.isNotEmpty(teHistory1));
	Assert.assertEquals(2, teHistory1.size());
    }

    @Test
    public void updateSubmissionTermLanguagesByProjectIdTest() {
	String termEntryId1 = UUID.randomUUID().toString();
	String termEntryId2 = UUID.randomUUID().toString();

	DbSubmissionTermEntry termEntry1 = createTermEntry(termEntryId1);
	DbSubmissionTermEntry termEntry2 = createTermEntry(termEntryId2);
	termEntry2.setProjectId(2L);

	DbSubmissionTerm term = createTerm(termEntry1);
	term.setLanguageId("de");
	termEntry1.getSubmissionTerms().add(term);

	DbSubmissionTermEntryDAO dbSubmissionTermEntryDAO = getDbSubmissionTermEntryDAO();

	dbSubmissionTermEntryDAO.saveOrUpdate(termEntry1);
	dbSubmissionTermEntryDAO.saveOrUpdate(termEntry2);

	DbSubmissionTermEntry termEntryBefore1 = dbSubmissionTermEntryDAO.findByUuid(termEntryId1, true);
	DbSubmissionTermEntry termEntryBefore2 = dbSubmissionTermEntryDAO.findByUuid(termEntryId2, true);

	Assert.assertNotNull(termEntryBefore1);
	Assert.assertNotNull(termEntryBefore2);

	// Perform update operation
	getDbSubmissionTermEntryDAO().updateSubmissionTermLanguagesByProjectId("sr", "sr-SR", 1L);

	getDbSubmissionTermEntryDAO().flush();
	getDbSubmissionTermEntryDAO().clear();

	DbSubmissionTermEntry termEntryAfter1 = dbSubmissionTermEntryDAO.findByUuid(termEntryId1, true);
	DbSubmissionTermEntry termEntryAfter2 = dbSubmissionTermEntryDAO.findByUuid(termEntryId2, true);

	Assert.assertNotNull(termEntryAfter1);
	Assert.assertNotNull(termEntryAfter2);

	Set<DbSubmissionTerm> terms1 = termEntryAfter1.getSubmissionTerms();
	Set<DbSubmissionTerm> terms2 = termEntryAfter2.getSubmissionTerms();

	Assert.assertEquals(2, terms1.size());
	Assert.assertEquals(1, terms2.size());

	Iterator<DbSubmissionTerm> termIterator1 = terms1.iterator();
	Iterator<DbSubmissionTerm> termIterator2 = terms2.iterator();

	// Should not be changed
	DbSubmissionTerm deSubmissionTerm = termIterator1.next();
	Assert.assertEquals("de", deSubmissionTerm.getLanguageId());

	// Should be updated from sr to sr-SR
	DbSubmissionTerm srSubmissionTerm = termIterator1.next();
	Assert.assertEquals("sr-SR", srSubmissionTerm.getLanguageId());

	/*
	 * Should not be updated from sr to sr-SR even if it's sr language because it's
	 * in project 2
	 */
	DbSubmissionTerm srTermInProject2 = termIterator2.next();
	Assert.assertEquals("sr", srTermInProject2.getLanguageId());
    }

    private DbComment createComment(DbSubmissionTerm term) {
	DbComment comment = new DbComment();
	comment.setSubmissionTermUuid(term.getUuId());
	comment.setText("comment text");
	comment.setUser(USER);
	comment.setUuid(UUID.randomUUID().toString());

	return comment;
    }

    private Set<DbSubmissionTermEntryHistory> createSubHistory(String termEntryId) throws UnsupportedEncodingException {
	byte[] revision1 = "Test term entry history revision 1!".getBytes("UTF-8");

	Set<DbSubmissionTermEntryHistory> histories = new HashSet<>();

	DbSubmissionTermHistory termHistory = new DbSubmissionTermHistory();
	termHistory.setRevision(revision1);
	termHistory.setTermEntryUuid(termEntryId);
	termHistory.setRevisionId(1);

	DbSubmissionTermEntryHistory history = new DbSubmissionTermEntryHistory();

	history.setHistory(Collections.singleton(termHistory));
	history.setHistoryAction("ADDED");
	history.setUserModified("User1");
	history.setDateModified(new Date());
	history.setTermEntryUUid(termEntryId);
	history.setRevisionId(1);

	histories.add(history);

	return histories;
    }

    private DbSubmissionTermDescription createSubmissionTermDescription(DbSubmissionTerm term) {
	DbSubmissionTermDescription desc = new DbSubmissionTermDescription();
	desc.setBaseType(DbTermEntryDescription.ATTRIBUTE);
	desc.setType("context");
	desc.setValue("term att value".getBytes());
	desc.setTempValue("temp value");
	desc.setUuid(UUID.randomUUID().toString());
	desc.setSubmissionTermUuid(term.getUuId());

	return desc;
    }

    private DbSubmissionTermEntryDescription createSubmissionTermEntryDescription(DbSubmissionTermEntry termEntry) {
	DbSubmissionTermEntryDescription desc = new DbSubmissionTermEntryDescription();
	desc.setBaseType(DbTermEntryDescription.ATTRIBUTE);
	desc.setType("definition");
	desc.setValue("termentry att value".getBytes());
	desc.setTempValue("temp value");
	desc.setUuid(UUID.randomUUID().toString());
	desc.setSubmissionTermEntryUuid(termEntry.getUuId());

	return desc;
    }

    private DbSubmissionTerm createTerm(DbSubmissionTermEntry termEntry) {
	Date date = new Date();

	DbSubmissionTerm term = new DbSubmissionTerm();
	term.setDateCreated(date);
	term.setDateModified(date);
	term.setFirst(true);
	term.setForbidden(false);
	term.setLanguageId("sr");
	term.setName("term text".getBytes());
	term.setProjectId(PROJECT_ID);
	term.setStatus(STATUS);
	term.setUserCreated(USER);
	term.setUserModified(USER);
	term.setUuId(UUID.randomUUID().toString());
	term.setParentUuId(UUID.randomUUID().toString());

	DbComment comment = createComment(term);

	Set<DbComment> comments = new HashSet<DbComment>();
	comments.add(comment);

	term.setAssignee(USER);
	term.setCanceled(false);
	term.setComments(comments);
	term.setCommited(false);
	term.setDateCompleted(date);
	term.setDateSubmitted(date);
	term.setPriority(new DbPriority());
	term.setReviewRequired(true);
	term.setStatusOld(STATUS);
	term.setSubmissionId(SUBMISSION_ID);
	term.setSubmissionName(SUBMISSION_NAME);
	term.setTermEntryUuid(termEntry.getUuId());
	term.setSubmitter(USER);
	term.setTempText("temp term text");

	DbSubmissionTermDescription desc = createSubmissionTermDescription(term);

	Set<DbSubmissionTermDescription> termDescs = new HashSet<DbSubmissionTermDescription>();
	termDescs.add(desc);

	term.setDescriptions(termDescs);

	return term;
    }

    private DbSubmissionTermEntry createTermEntry(String termEntryId) {
	Date date = new Date();

	DbSubmissionTermEntry entry = new DbSubmissionTermEntry();
	entry.setDateCreated(date);
	entry.setDateModified(date);
	entry.setProjectId(PROJECT_ID);
	entry.setProjectName(PROJECT_NAME);
	entry.setShortCode(SHORTCODE);

	entry.setUserCreated(USER);
	entry.setUserModified(USER);
	entry.setUuId(termEntryId);

	entry.setParentUuId(termEntryId);
	entry.setSubmissionId(SUBMISSION_ID);
	entry.setSubmissionName(SUBMISSION_NAME);
	entry.setSubmitter(USER);
	entry.setParentUuId(termEntryId);
	entry.setActionRollback(false);
	entry.setRevisionId(1);

	DbSubmissionTermEntryDescription desc = createSubmissionTermEntryDescription(entry);
	Set<DbSubmissionTermEntryDescription> descs = new HashSet<DbSubmissionTermEntryDescription>();
	descs.add(desc);

	entry.setDescriptions(descs);

	DbSubmissionTerm term = createTerm(entry);
	Set<DbSubmissionTerm> submissionTerms = new HashSet<DbSubmissionTerm>();
	submissionTerms.add(term);

	entry.setSubmissionTerms(submissionTerms);

	return entry;
    }

    private DbSubmissionTermEntryDAO getDbSubmissionTermEntryDAO() {
	return _dbSubmissionTermEntryDAO;
    }
}