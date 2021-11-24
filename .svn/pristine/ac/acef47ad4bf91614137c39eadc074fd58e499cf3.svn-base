package org.gs4tr.termmanager.dao.hibernate;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.dao.SubmissionUserDAO;
import org.gs4tr.termmanager.model.Submission;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SubmissionUserDAOTest extends AbstractSpringDAOIntegrationTest {

    private static final long USER_ID = 1L;

    @Autowired
    private SubmissionUserDAO _submissionUserDAO;

    @Test
    public void findBySubmissionAndUserIdTest() {
	Long submissionUserId = getSubmissionUserDAO().findBySubmissionAndUserId(1L, USER_ID);

	assertNotNull(submissionUserId);

	assertTrue(submissionUserId.equals(USER_ID));
    }

    @Test
    public void findSubmissionsByUserIdTest() {
	List<Submission> submissions = getSubmissionUserDAO().findSubmissionsByUserId(USER_ID);

	assertNotNull(submissions);

	assertTrue(CollectionUtils.isNotEmpty(submissions));

	assertEquals(1, submissions.size());

	verifySubmission(submissions.get(0));
    }

    public SubmissionUserDAO getSubmissionUserDAO() {
	return _submissionUserDAO;
    }

    private void verifySubmission(Submission submission) {
	assertNotNull(submission);

	assertNotNull(submission.getDateModified());
	assertNotNull(submission.getDateSubmitted());

	assertEquals("First Job", submission.getName());
	assertEquals("pm", submission.getSubmitter());
	assertEquals("INTRANSLATIONREVIEW", submission.getEntityStatusPriority().getStatus().getName());

	assertEquals("en-US", submission.getSourceLanguageId());

	assertTrue(submission.getTermEntryCount() == 2);
	assertTrue(submission.getProject().getProjectId().equals(2L));
	assertTrue(submission.getEntityStatusPriority().getPriority() == 2);
    }

}
