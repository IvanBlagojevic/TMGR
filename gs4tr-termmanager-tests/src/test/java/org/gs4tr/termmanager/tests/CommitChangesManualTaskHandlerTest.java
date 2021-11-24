package org.gs4tr.termmanager.tests;

import java.util.Arrays;

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;

@TestSuite("commit_translation_changes")
public class CommitChangesManualTaskHandlerTest extends AbstractSolrGlossaryTest {

    @Test
    @TestCase("process_tasks")
    public void commitTranslationChangesPostTest() throws Exception {
	String subTermId = SUB_TERM_ID_01;

	String newTempTermText = "Big Maus";

	Term subTerm = getSubmissionTermService().findById(subTermId, PROJECT_ID);

	Assert.assertEquals(newTempTermText, subTerm.getTempText());

	String submissionTicket = TicketConverter.fromInternalToDto(1L);

	String taskName = "commit translation changes";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "commitTranslationChanges.json",
		new String[] { "$submissionTicket", submissionTicket });

	TaskResponse tasksResponse = taskHandler.processTasks(null, null, command, null);
	Assert.assertNotNull(tasksResponse);

	subTerm = getSubmissionTermService().findById(subTermId, PROJECT_ID);

	Assert.assertEquals(newTempTermText, subTerm.getName());
    }

    @Test
    @TestCase("process_tasks")
    public void commitTranslationChangesReviewNotRequiredDateModifiedTest() throws TmException {

	// Set review required to false. After this term status should be PROCESSED.
	TermEntry submissionTermEntry = getSubmissionService().findSubmissionTermEntryById(SUB_TERM_ENTRY_ID_01, 1L);
	Term submissionTerm = submissionTermEntry.ggetTermById(SUB_TERM_ID_02);
	submissionTerm.setReviewRequired(false);

	setNewTermStatus(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), 1L, TERM_ENTRY_ID_01, TERM_ID_01);
	dateModifiedTest();
    }

    @Test
    @TestCase("process_tasks")
    public void commitTranslationReviewChangesRequiredDateModifiedTest() throws TmException {
	setNewTermStatus(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), 1L, TERM_ENTRY_ID_01, TERM_ID_01);
	dateModifiedTest();
    }

    private void dateModifiedTest() {
	String submissionTicket = TicketConverter.fromInternalToDto(1L);

	String taskName = "commit translation changes";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "commitTranslationChanges.json",
		new String[] { "$submissionTicket", submissionTicket });

	ProjectDetail projectDetailBefore = getProjectDetailService().findByProjectId(1L, ProjectLanguageDetail.class);

	Term termBefore = getTermService().findTermById(TERM_ID_01, 1L);
	Term submissionTerm = getSubmissionTermService().findById(SUB_TERM_ID_02, 1L);
	TermEntry termEntryBefore = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_01, 1L);

	Assert.assertEquals("INTRANSLATIONREVIEW", termBefore.getStatus());

	String expectedTermStatusAfterCommit = "PROCESSED";
	if (submissionTerm.getReviewRequired()) {
	    expectedTermStatusAfterCommit = "INFINALREVIEW";
	}

	taskHandler.processTasks(null, null, command, null);

	Term termAfter = getTermService().findTermById(TERM_ID_01, 1L);
	TermEntry termEntryAfter = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_01, 1L);

	ProjectDetail projectDetailAfter = getProjectDetailService().findByProjectId(1L, ProjectLanguageDetail.class);

	Assert.assertEquals(expectedTermStatusAfterCommit, termAfter.getStatus());

	Assert.assertTrue(termBefore.getDateModified() < termAfter.getDateModified());
	Assert.assertTrue(termEntryBefore.getDateModified() < termEntryAfter.getDateModified());

	Assert.assertTrue(
		projectDetailBefore.getDateModified().getTime() < (projectDetailAfter.getDateModified().getTime()));

	/* Testic if de-DE language has updated dateModified */
	Assert.assertTrue(isProjectLanguageDetailDateModifiedChanged("de-DE", projectDetailBefore.getLanguageDetails(),
		projectDetailAfter.getLanguageDetails()));

    }

    private void setNewTermStatus(String newStatus, Long projectId, String termEntryUuId, String termUuId)
	    throws TmException {
	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryUuId, projectId);

	Term term = termEntry.ggetTermById(termUuId);
	term.setStatus(newStatus);

	getTermEntryService().updateRegularTermEntries(projectId, Arrays.asList(termEntry));
    }

}
