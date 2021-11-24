package org.gs4tr.termmanager.dao.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.dao.SubmissionDAO;
import org.gs4tr.termmanager.dao.SubmissionLanguageCommentDAO;
import org.gs4tr.termmanager.dao.SubmissionLanguageDAO;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.SubmissionLanguageComment;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractTermTranslationTest extends AbstractSpringDAOIntegrationTest {

    protected static final String SUBMISSION_LANGUAGE_COMMENT = "This is one sub language comment";

    protected static final String SUBMISSION_NAME = "Submission name";

    protected static final String TERM_COMMENT = "This is one term comment";

    @Autowired
    private SubmissionDAO _submissionDAO;

    @Autowired
    private SubmissionLanguageCommentDAO _submissionLanguageCommentDAO;

    @Autowired
    private SubmissionLanguageDAO _submissionLanguageDAO;

    protected Long addSubmissionLanguageComment(SubmissionLanguage subLanguage) {
	Set<SubmissionLanguageComment> comments = subLanguage.getSubmissionLanguageComments();
	if (comments == null) {
	    comments = new HashSet<SubmissionLanguageComment>();
	    subLanguage.setSubmissionLanguageComments(comments);
	}

	String commentText = SUBMISSION_LANGUAGE_COMMENT;

	SubmissionLanguageComment subLanguageComment = new SubmissionLanguageComment();
	subLanguageComment.setText(commentText);
	subLanguageComment.setUser("submitter");
	subLanguageComment.setMarkerId(randomUUID());
	subLanguageComment.setSubmissionLanguage(subLanguage);

	subLanguageComment = getSubmissionLanguageCommentDAO().save(subLanguageComment);
	Long commentId = subLanguageComment.getCommentId();
	Assert.assertNotNull(commentId);

	comments.add(subLanguageComment);

	SubmissionLanguageComment subLangComment = getSubmissionLanguageCommentDAO().findById(commentId);
	Assert.assertNotNull(subLangComment);
	Assert.assertEquals(commentText, subLangComment.getText());

	return commentId;
    }

    protected void addTermComment(Term subTerm) {
	Set<Description> comments = subTerm.getDescriptions();
	if (comments == null) {
	    comments = new HashSet<Description>();
	    subTerm.setDescriptions(comments);
	}
	String termCommentText = TERM_COMMENT;

	Description termComment = new Description();
	termComment.setType(termCommentText);
	termComment.setBaseType(Description.NOTE);

	Assert.assertNotNull(termComment);

	comments.add(termComment);

    }

    protected void assertNumberOfSubmissionTerms(List<Term> termsInSubmission, int expectedSize) {
	Assert.assertNotNull(termsInSubmission);
	Assert.assertTrue(CollectionUtils.isNotEmpty(termsInSubmission));
	Assert.assertEquals(expectedSize, termsInSubmission.size());
    }

    protected void assertTermCollection(List<Term> terms) {
	Assert.assertTrue(CollectionUtils.isNotEmpty(terms));
    }

    protected SubmissionLanguage createSubLanguage(String assignee, String languageId, Submission submission) {
	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	if (submissionLanguages == null) {
	    submissionLanguages = new HashSet<SubmissionLanguage>();
	    submission.setSubmissionLanguages(submissionLanguages);
	}

	SubmissionLanguage subLanguage = new SubmissionLanguage(languageId);
	subLanguage.setAssignee(assignee);
	subLanguage.setMarkerId(randomUUID());
	subLanguage.setSubmission(submission);

	subLanguage = getSubmissionLanguageDAO().save(subLanguage);
	Assert.assertNotNull(subLanguage);
	Assert.assertNotNull(subLanguage.getSubmissionLanguageId());

	submission.appendAssingees(assignee);
	submission.appendTargetLanguageIds(languageId);
	submissionLanguages.add(subLanguage);

	addSubmissionLanguageComment(subLanguage);
	return subLanguage;
    }

    protected Long createSubmission(List<Term> terms) {
	int termListSize = terms.size();
	Term firstTerm = terms.get(0);

	String languageId = firstTerm.getLanguageId();

	Long projectId = firstTerm.getProjectId();
	TmProject project = getProjectDAO().load(projectId);

	String submitter = "submitter";

	Submission submission = new Submission(languageId);
	submission.setName(SUBMISSION_NAME);
	submission.setMarkerId(randomUUID());
	submission.setSubmitter(submitter);
	submission.setProject(project);

	submission = getSubmissionDAO().save(submission);
	Assert.assertNotNull(submission);
	Assert.assertNotNull(submission.getSubmissionId());

	String assignee1 = "assignee1";

	SubmissionLanguage subLanguage1 = createSubLanguage(assignee1, languageId, submission);
	submittTerm(firstTerm, subLanguage1, submission.getSubmissionId(), submitter, assignee1, SUBMISSION_NAME);

	if (termListSize >= 2) {
	    Term secondTerm = terms.get(1);
	    SubmissionLanguage subLanguage2 = createSubLanguage(assignee1, secondTerm.getLanguageId(), submission);
	    submittTerm(secondTerm, subLanguage2, submission.getSubmissionId(), submitter, assignee1, SUBMISSION_NAME);
	}

	getSubmissionDAO().update(submission);

	flushSession();

	return submission.getSubmissionId();
    }

    protected SubmissionDAO getSubmissionDAO() {
	return _submissionDAO;
    }

    protected SubmissionLanguageCommentDAO getSubmissionLanguageCommentDAO() {
	return _submissionLanguageCommentDAO;
    }

    protected SubmissionLanguageDAO getSubmissionLanguageDAO() {
	return _submissionLanguageDAO;
    }

    protected TmUserProfile loadFirstUser() {
	return getUserProfileDAO().load(1L);
    }

    protected String randomUUID() {
	return UUID.randomUUID().toString();
    }

    protected void submittTerm(Term term, SubmissionLanguage subLanguage, Long submissionId, String submitter,
	    String assignee, String submissionName) {
	String termText = term.getName();
	String languageId = term.getLanguageId();
	String markerId = term.getUuId();

	Term subTerm = new Term();
	subTerm.setLanguageId(term.getLanguageId());
	subTerm.setSubmissionId(submissionId);
	subTerm.setSubmissionName(submissionName);

	subTerm.setAssignee(assignee);
	subTerm.setSubmitter(submitter);

	Assert.assertNotNull(subTerm);
	Assert.assertNotNull(subTerm.getUuId());
	Assert.assertEquals(termText, subTerm.getName());
	Assert.assertEquals(languageId, subTerm.getLanguageId());
	Assert.assertEquals(markerId, subTerm.getUuId());
	// TODO Marko check status priority
	// Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW,
	// subTerm
	// .getEntityStatusPriority().getStatus());

	addTermComment(subTerm);
    }
}
