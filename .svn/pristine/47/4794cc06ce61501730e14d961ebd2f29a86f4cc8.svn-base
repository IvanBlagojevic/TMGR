package org.gs4tr.termmanager.dao.hibernate.backup;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.dao.ProjectDetailDAO;
import org.gs4tr.termmanager.dao.ProjectLanguageDetailDAO;
import org.gs4tr.termmanager.dao.ProjectLanguageUserDetailDAO;
import org.gs4tr.termmanager.dao.ProjectUserDetailDAO;
import org.gs4tr.termmanager.dao.StatisticsDAO;
import org.gs4tr.termmanager.dao.SubmissionDAO;
import org.gs4tr.termmanager.dao.backup.DbSubmissionTermEntryDAO;
import org.gs4tr.termmanager.dao.backup.DbTermEntryDAO;
import org.gs4tr.termmanager.dao.hibernate.AbstractSpringDAOIntegrationTest;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.backup.converter.DbSubmissionTermEntryConverter;
import org.gs4tr.termmanager.model.glossary.backup.converter.DbTermEntryConverter;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTerm;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermDescription;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryDescription;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryHistory;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTerm;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermDescription;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntryDescription;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntryHistory;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractBackupDAOTest extends AbstractSpringDAOIntegrationTest {

    protected static final int CHUNK_SIZE = 500;

    protected static final long PROJECT_ID = 1L;

    protected static final String PROJECT_NAME = "HPE";

    protected static final String SHORTCODE = "HPE000001";

    protected static final String STATUS = ItemStatusTypeHolder.PROCESSED.getName();

    protected static final long SUBMISSION_ID = 3L;

    protected static final String SUBMISSION_STATUS = ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName();

    protected static final String USER = "user";

    @Autowired
    private DbSubmissionTermEntryDAO _dbSubmissionTermEntryDAO;

    @Autowired
    private DbTermEntryDAO _dbTermEntryDAO;

    @Autowired
    private ProjectDetailDAO _projectDetailDAO;

    @Autowired
    private ProjectLanguageDetailDAO _projectLanguageDetailDAO;

    @Autowired
    private ProjectLanguageUserDetailDAO _projectLanguageUserDetailDAO;

    @Autowired
    private ProjectUserDetailDAO _projectUserDetailDAO;

    @Autowired
    private StatisticsDAO _statisticsDAO;

    @Autowired
    private SubmissionDAO _submissionDAO;

    public DbSubmissionTermEntryDAO getDbSubmissionTermEntryDAO() {
	return _dbSubmissionTermEntryDAO;
    }

    public ProjectDetailDAO getProjectDetailDAO() {
	return _projectDetailDAO;
    }

    public ProjectLanguageDetailDAO getProjectLanguageDetailDAO() {
	return _projectLanguageDetailDAO;
    }

    public ProjectLanguageUserDetailDAO getProjectLanguageUserDetailDAO() {
	return _projectLanguageUserDetailDAO;
    }

    public ProjectUserDetailDAO getProjectUserDetailDAO() {
	return _projectUserDetailDAO;
    }

    public StatisticsDAO getStatisticsDAO() {
	return _statisticsDAO;
    }

    public SubmissionDAO getSubmissionDAO() {
	return _submissionDAO;
    }

    protected DbSubmissionTerm createSubmissionTerm(DbSubmissionTermEntry termEntry) {

	Date date = new Date();

	DbSubmissionTerm term = new DbSubmissionTerm();
	term.setDateCreated(date);
	term.setDateModified(date);
	term.setFirst(true);
	term.setForbidden(false);
	term.setInTranslationAsSource(true);
	term.setLanguageId("sr");
	term.setName("term text".getBytes());
	term.setProjectId(PROJECT_ID);
	term.setStatus(SUBMISSION_STATUS);
	term.setUserCreated(USER);
	term.setUserModified(USER);
	term.setUuId(UUID.randomUUID().toString());

	DbSubmissionTermDescription desc = createSubmissionTermDescription(term);

	Set<DbSubmissionTermDescription> termDescs = new HashSet<>();
	termDescs.add(desc);

	term.setDescriptions(termDescs);

	term.setTermEntryUuid(termEntry.getUuId());
	// term.setTermEntry(termEntry);

	return term;
    }

    protected DbSubmissionTermDescription createSubmissionTermDescription(DbSubmissionTerm term) {
	DbSubmissionTermDescription desc = new DbSubmissionTermDescription();
	desc.setBaseType(DbTermEntryDescription.ATTRIBUTE);
	desc.setType("context");
	desc.setValue("term att value".getBytes());
	desc.setUuid(UUID.randomUUID().toString());
	desc.setSubmissionTermUuid(term.getUuId());
	return desc;
    }

    protected DbSubmissionTermEntry createSubmissionTermEntry(String termEntryId) {

	int revisionId = 1;

	Date date = new Date();

	DbSubmissionTermEntry termEntry = new DbSubmissionTermEntry();
	termEntry.setDateCreated(date);
	termEntry.setDateModified(date);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setShortCode(SHORTCODE);

	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);
	termEntry.setUuId(termEntryId);
	termEntry.setRevisionId(revisionId);
	termEntry.setSubmissionId(SUBMISSION_ID);

	DbSubmissionTermEntryDescription desc = createSubmissionTermEntryDescription(termEntry);
	Set<DbSubmissionTermEntryDescription> entryDescs = new HashSet<>();
	entryDescs.add(desc);
	termEntry.setDescriptions(entryDescs);

	DbSubmissionTerm term = createSubmissionTerm(termEntry);
	Set<DbSubmissionTerm> terms = new HashSet<>();
	terms.add(term);
	termEntry.setSubmissionTerms(terms);

	DbSubmissionTermEntryHistory historyRevision = DbSubmissionTermEntryConverter.createHistory(null, termEntry,
		revisionId);
	Set<DbSubmissionTermEntryHistory> history = termEntry.getHistory();
	if (Objects.isNull(history)) {
	    history = new HashSet<>();
	    termEntry.setHistory(history);
	}
	history.add(historyRevision);

	return termEntry;

    }

    protected DbSubmissionTermEntryDescription createSubmissionTermEntryDescription(DbSubmissionTermEntry termEntry) {
	DbSubmissionTermEntryDescription desc = new DbSubmissionTermEntryDescription();
	desc.setBaseType(DbTermEntryDescription.ATTRIBUTE);
	desc.setType("definition");
	desc.setValue("termentry att value".getBytes());
	desc.setUuid(UUID.randomUUID().toString());
	desc.setSubmissionTermEntryUuid(termEntry.getUuId());

	return desc;
    }

    protected DbTerm createTerm(DbTermEntry termEntry) {
	Date date = new Date();

	DbTerm term = new DbTerm();
	term.setDateCreated(date);
	term.setDateModified(date);
	term.setDisabled(false);
	term.setFirst(true);
	term.setForbidden(false);
	term.setInTranslationAsSource(false);
	term.setLanguageId("sr");
	term.setName("term text".getBytes());
	term.setProjectId(PROJECT_ID);
	term.setStatus(STATUS);
	term.setUserCreated(USER);
	term.setUserModified(USER);
	term.setUuId(UUID.randomUUID().toString());

	DbTermDescription desc = createTermDescription(term);

	Set<DbTermDescription> termDescs = new HashSet<>();
	termDescs.add(desc);

	term.setDescriptions(termDescs);

	term.setTermEntryUuid(termEntry.getUuId());
	// term.setTermEntry(termEntry);

	return term;
    }

    protected DbTermDescription createTermDescription(DbTerm term) {
	DbTermDescription desc = new DbTermDescription();
	desc.setBaseType(DbTermEntryDescription.ATTRIBUTE);
	desc.setType("context");
	desc.setValue("term att value".getBytes());
	desc.setUuid(UUID.randomUUID().toString());
	desc.setTermUuid(term.getUuId());

	return desc;
    }

    protected DbTermEntry createTermEntry(String termEntryId) {
	int revisionId = 1;

	Date date = new Date();

	DbTermEntry termEntry = new DbTermEntry();
	termEntry.setDateCreated(date);
	termEntry.setDateModified(date);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setShortCode(SHORTCODE);

	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);
	termEntry.setUuId(termEntryId);
	termEntry.setRevisionId(revisionId);

	DbTermEntryDescription desc = createTermEntryDescription(termEntry);
	Set<DbTermEntryDescription> entryDescs = new HashSet<>();
	entryDescs.add(desc);
	termEntry.setDescriptions(entryDescs);

	DbTerm term = createTerm(termEntry);
	Set<DbTerm> terms = new HashSet<>();
	terms.add(term);
	termEntry.setTerms(terms);

	DbTermEntryHistory historyRevision = DbTermEntryConverter.createHistory(null, termEntry, revisionId);
	Set<DbTermEntryHistory> history = termEntry.getHistory();
	if (Objects.isNull(history)) {
	    history = new HashSet<>();
	    termEntry.setHistory(history);
	}
	history.add(historyRevision);

	return termEntry;
    }

    protected DbTermEntryDescription createTermEntryDescription(DbTermEntry termEntry) {
	DbTermEntryDescription desc = new DbTermEntryDescription();
	desc.setBaseType(DbTermEntryDescription.ATTRIBUTE);
	desc.setType("definition");
	desc.setValue("termentry att value".getBytes());
	desc.setUuid(UUID.randomUUID().toString());
	desc.setTermEntryUuid(termEntry.getUuId());

	return desc;
    }

    protected DbTermEntryDAO getDbTermEntryDAO() {
	return _dbTermEntryDAO;
    }

    protected String prepareDbForTests() {
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
	Assert.assertEquals(1, entry.getHistory().size());

	return termEntryId;
    }

    protected String prepareSubmissionDbForTest() {

	String termEntryId = UUID.randomUUID().toString();

	DbSubmissionTermEntry termEntry = createSubmissionTermEntry(termEntryId);

	DbSubmissionTermEntryDAO dbSubmissionTermEntryDAO = getDbSubmissionTermEntryDAO();

	dbSubmissionTermEntryDAO.saveOrUpdateLocked(Collections.singletonList(termEntry));

	DbSubmissionTermEntry entry = dbSubmissionTermEntryDAO.findByUuid(termEntryId, true);

	Assert.assertNotNull(entry);
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getDescriptions()));
	Assert.assertEquals(1, entry.getDescriptions().size());
	Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getSubmissionTerms()));
	Assert.assertEquals(1, entry.getSubmissionTerms().size());
	Assert.assertEquals(1, entry.getHistory().size());

	return termEntryId;

    }
}
