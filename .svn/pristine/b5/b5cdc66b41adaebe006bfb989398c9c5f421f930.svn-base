package org.gs4tr.termmanager.tests;

import java.io.IOException;
import java.util.Arrays;

import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionUser;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;

@TestSuite("re_submit")
public class ReSubmitManualTaskHandlerTest extends AbstractSolrGlossaryTest {

    String ID_SUB_TERM = "c502608e-uuid-sub-term-005";
    String ID_SUB_TERM_ENTRY = "uuid-sub-term-entry-003";
    String ID_TERM = "c502608e-uuid-term-012";
    String ID_TERM_ENTRY = "c502608e-uuid-term-entry-005";
    String USER = "pm";

    @Test
    @TestCase("process_tasks")
    public void reSubmitManualTaskHandlerDateModified() throws TmException {

	String taskName = "re submit to translation";

	Long submissionId = 1L;
	String submissionTermId = ID_SUB_TERM;

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "reSubmit.json",
		new String[] { "$submissionTicket", IdEncrypter.encryptGenericId(submissionId) },
		new String[] { "$submissionTermTicket", submissionTermId });

	ProjectDetail projectDetailBefore = getProjectDetailService().findByProjectId(1L, ProjectLanguageDetail.class);

	TermEntry termEntryBefore = getTermEntryService().findTermEntryById(ID_TERM_ENTRY, 1L);

	taskHandler.processTasks(null, null, command, null);

	TermEntry termEntryAfter = getTermEntryService().findTermEntryById(ID_TERM_ENTRY, 1L);

	ProjectDetail projectDetailAfter = getProjectDetailService().findByProjectId(1L, ProjectLanguageDetail.class);

	Assert.assertTrue(
		projectDetailBefore.getDateModified().getTime() < projectDetailAfter.getDateModified().getTime());

	Assert.assertTrue(termEntryBefore.getDateModified() < termEntryAfter.getDateModified());

	Term termBefore = termEntryBefore.ggetTermById(ID_TERM);
	Term termAfter = termEntryAfter.ggetTermById(ID_TERM);

	Assert.assertTrue(termBefore.getDateModified() < termAfter.getDateModified());

	Assert.assertTrue(isProjectLanguageDetailDateModifiedChanged("en-US", projectDetailBefore.getLanguageDetails(),
		projectDetailAfter.getLanguageDetails()));
    }

    @Test
    @TestCase("process_tasks")
    public void testProcessTasks() throws IOException, InterruptedException {
	Long submissionId = 1L;
	String submissionTermId = SUB_TERM_ID_03;

	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);
	Assert.assertNotNull(submission);
	Assert.assertEquals(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(),
		submission.getEntityStatusPriority().getStatus().getName());
	String assignee = "translator";
	SubmissionUser submissionUser = submission.getSubmissionUser(assignee);
	if (submissionUser != null) {
	    Assert.assertEquals(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(),
		    submissionUser.getEntityStatusPriority().getStatus().getName());
	}
	Term subTerm = getSubmissionTermService().findById(submissionTermId, PROJECT_ID);
	Assert.assertNotNull(subTerm);
	Assert.assertEquals(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(), subTerm.getStatus());

	String taskName = "re submit to translation";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "reSubmit.json",
		new String[] { "$submissionTicket", IdEncrypter.encryptGenericId(submissionId) },
		new String[] { "$submissionTermTicket", submissionTermId });

	TaskResponse tasksResponse = taskHandler.processTasks(null, null, command, null);
	Assert.assertNotNull(tasksResponse);

	submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);
	Assert.assertNotNull(submission);
	Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(),
		submission.getEntityStatusPriority().getStatus().getName());

	subTerm = getSubmissionTermService().findById(submissionTermId, PROJECT_ID);
	Assert.assertNotNull(subTerm);
	Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), subTerm.getStatus());

	submissionUser = submission.getSubmissionUser(assignee);
	if (submissionUser != null) {
	    Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(),
		    submissionUser.getEntityStatusPriority().getStatus().getName());
	}
    }

    @Test
    @TestCase("process_tasks")
    public void test_TERII_3700() throws IOException, InterruptedException {
	Long submissionId = 1L;
	String submissionTermId = SUB_TERM_ID_03;

	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);
	Assert.assertNotNull(submission);
	Assert.assertEquals(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(),
		submission.getEntityStatusPriority().getStatus().getName());
	String assignee = "translator";
	SubmissionUser submissionUser = submission.getSubmissionUser(assignee);
	if (submissionUser != null) {
	    Assert.assertEquals(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(),
		    submissionUser.getEntityStatusPriority().getStatus().getName());
	}
	Term subTerm = getSubmissionTermService().findById(submissionTermId, PROJECT_ID);
	Assert.assertNotNull(subTerm);
	Assert.assertEquals(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(), subTerm.getStatus());

	String taskName = "re submit to translation";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "reSubmit.json",
		new String[] { "$submissionTicket", IdEncrypter.encryptGenericId(submissionId) },
		new String[] { "$submissionTermTicket", submissionTermId });

	TaskResponse tasksResponse = taskHandler.processTasks(null, null, command, null);
	Assert.assertNotNull(tasksResponse);

	submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);
	Assert.assertNotNull(submission);
	Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(),
		submission.getEntityStatusPriority().getStatus().getName());

	subTerm = getSubmissionTermService().findById(submissionTermId, PROJECT_ID);
	Assert.assertNotNull(subTerm);
	Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), subTerm.getStatus());

	submissionUser = submission.getSubmissionUser(assignee);
	if (submissionUser != null) {
	    Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(),
		    submissionUser.getEntityStatusPriority().getStatus().getName());
	}

	String parentUuId = subTerm.getParentUuId();
	Assert.assertNotNull(parentUuId);

	Term term = getTermService().findTermById(parentUuId, PROJECT_ID);
	Assert.assertNotNull(term);
	Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), term.getStatus());
    }

    /* Add terms with IN FINAL REVIEW status */
    private void addInFinalReviewTerms() throws TmException {

	Long ID_PROJECT = 1L;
	String PROJECT_NAME = "projectName";

	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(ID_TERM_ENTRY);
	termEntry.setProjectId(ID_PROJECT);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term1 = createTerm(ID_TERM, "en-US", "Cat", false, ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(), USER,
		true);
	termEntry.addTerm(term1);

	getTermEntryService().updateRegularTermEntries(ID_PROJECT, Arrays.asList(termEntry));

	TermEntry subTermEntry = new TermEntry();
	subTermEntry.setParentUuId(ID_TERM_ENTRY);
	subTermEntry.setUuId(ID_SUB_TERM_ENTRY);
	subTermEntry.setProjectId(ID_PROJECT);
	subTermEntry.setProjectName(PROJECT_NAME);
	subTermEntry.setSubmissionId(1L);
	subTermEntry.setUserCreated(USER);
	subTermEntry.setUserModified(USER);

	Term subTerm1 = createTerm(ID_SUB_TERM, "en-US", "Cat", false, ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(),
		USER, true);

	subTerm1.setParentUuId(term1.getUuId());
	subTerm1.setDateSubmitted(System.currentTimeMillis());

	subTermEntry.addTerm(subTerm1);

	getTermEntryService().updateSubmissionTermEntries(ID_PROJECT, Arrays.asList(subTermEntry));
    }

    @Override
    protected void populate() throws Exception {
	super.populate();
	addInFinalReviewTerms();
    }
}
