package org.gs4tr.termmanager.dao.hibernate.backup;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertNull;

import org.gs4tr.termmanager.dao.backup.SubmissionBackupCleanerDAO;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SubmissionBackupCleanerDAOTest extends AbstractBackupDAOTest {

    @Autowired
    private SubmissionBackupCleanerDAO _submissionBackupCleanerDAO;

    @Test
    public void deleteSubmissionsByProjectIds() {

	String termEntryId = prepareSubmissionDbForTest();

	SubmissionBackupCleanerDAO dao = getSubmissionBackupCleanerDAO();
	dao.deleteSubmissionsByProjectIds(singletonList(PROJECT_ID), CHUNK_SIZE);
	dao.flush();
	dao.clear();

	DbSubmissionTermEntry termEntry = getDbSubmissionTermEntryDAO().findByUuid(termEntryId, true);
	assertNull(termEntry);

	Submission submission = getSubmissionDAO().findSubmissionByIdFetchChilds(SUBMISSION_ID);
	assertNull(submission);

    }

    public SubmissionBackupCleanerDAO getSubmissionBackupCleanerDAO() {
	return _submissionBackupCleanerDAO;
    }
}
