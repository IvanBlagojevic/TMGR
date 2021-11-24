package org.gs4tr.termmanager.tests;

import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.CancelTranslationCommand;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;

@TestSuite("cancel_translation")
public class CancelTranslationTaskHandlerTest extends AbstractSolrGlossaryTest {

    @Test
    @TestCase("process_tasks")
    public void cancelTranslationSubmissionDateModifiedTest() {

	String taskName = "cancel translation";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Long submissionID = 1L;

	String submissionTicket = TicketConverter.fromInternalToDto(submissionID);

	Object command = getTaskHandlerCommand(taskHandler, "cancelTranslation.json",
		new String[] { "$submissionTicket", submissionTicket });

	/* Remove term ids from command */
	CancelTranslationCommand cancelCommand = (CancelTranslationCommand) command;
	cancelCommand.setTermIds(new ArrayList<>());

	testCancelTranslationDateModified(cancelCommand, taskHandler);
    }

    @Test
    @TestCase("process_tasks")
    public void cancelTranslationTermDateModifiedTest() {
	String taskName = "cancel translation";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Long submissionID = 1L;

	String submissionTicket = TicketConverter.fromInternalToDto(submissionID);
	String termTicket1 = SUB_TERM_ID_01;
	String termTicket2 = SUB_TERM_ID_02;
	String termTicket3 = SUB_TERM_ID_03;
	String termTicket4 = SUB_TERM_ID_04;

	Object command = getTaskHandlerCommand(taskHandler, "cancelTranslation.json",
		new String[] { "$submissionTicket", submissionTicket }, new String[] { "$ticket1", termTicket1 },
		new String[] { "$ticket2", termTicket2 }, new String[] { "$ticket3", termTicket3 },
		new String[] { "$ticket4", termTicket4 });

	testCancelTranslationDateModified((CancelTranslationCommand) command, taskHandler);
    }

    @Test
    @TestCase("process_tasks")
    public void processTasksTest() {
	String taskName = "cancel translation";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Long submissionID = 1L;

	String submissionTicket = TicketConverter.fromInternalToDto(submissionID);
	String termTicket1 = SUB_TERM_ID_01;
	String termTicket2 = SUB_TERM_ID_02;
	String termTicket3 = SUB_TERM_ID_03;
	String termTicket4 = SUB_TERM_ID_04;

	Object command = getTaskHandlerCommand(taskHandler, "cancelTranslation.json",
		new String[] { "$submissionTicket", submissionTicket }, new String[] { "$ticket1", termTicket1 },
		new String[] { "$ticket2", termTicket2 }, new String[] { "$ticket3", termTicket3 },
		new String[] { "$ticket4", termTicket4 });

	TaskResponse tasksResponse = taskHandler.processTasks(null, null, command, null);
	Assert.assertNotNull(tasksResponse);

	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionID);
	Assert.assertEquals(ItemStatusTypeHolder.CANCELLED, submission.getEntityStatusPriority().getStatus());
	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionLanguages));
	for (SubmissionLanguage subLang : submissionLanguages) {
	    Assert.assertEquals(ItemStatusTypeHolder.CANCELLED, subLang.getEntityStatusPriority().getStatus());
	    Assert.assertEquals(ItemStatusTypeHolder.CANCELLED, subLang.getStatusAssignee());
	    Assert.assertTrue(subLang.getTermCanceledCount() > 0);
	    Assert.assertTrue(subLang.getTermCanceledCount() == subLang.getTermCount());
	}
    }

    private void testCancelTranslationDateModified(CancelTranslationCommand cancelCommand,
	    ManualTaskHandler taskHandler) {

	TermEntry termEntryBefore = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_01, 1L);

	Term termBefore1 = getTermService().findTermById(TERM_ID_01, 1L);
	Term termBefore2 = getTermService().findTermById(TERM_ID_02, 1L);

	ProjectDetail projectDetailBefore = getProjectDetailService().findByProjectId(1L, ProjectLanguageDetail.class);

	TaskResponse tasksResponse = taskHandler.processTasks(null, null, cancelCommand, null);

	ProjectDetail projectDetailAfter = getProjectDetailService().findByProjectId(1L, ProjectLanguageDetail.class);

	TermEntry termEntryAfter = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_01, 1L);

	Term termAfter1 = getTermService().findTermById(TERM_ID_01, 1L);
	Term termAfter2 = getTermService().findTermById(TERM_ID_02, 1L);

	Assert.assertNotNull(tasksResponse);

	/* Testic project date modified */
	Assert.assertTrue(
		projectDetailBefore.getDateModified().getTime() < projectDetailAfter.getDateModified().getTime());

	Assert.assertTrue(termEntryBefore.getDateModified() < termEntryAfter.getDateModified());

	Assert.assertTrue(termBefore1.getDateModified() < termAfter1.getDateModified());
	Assert.assertTrue(termBefore2.getDateModified() < termAfter2.getDateModified());

	Assert.assertTrue(isProjectLanguageDetailDateModifiedChanged("en-US", projectDetailBefore.getLanguageDetails(),
		projectDetailAfter.getLanguageDetails()));

	Assert.assertTrue(isProjectLanguageDetailDateModifiedChanged("de-DE", projectDetailBefore.getLanguageDetails(),
		projectDetailAfter.getLanguageDetails()));

    }

}
