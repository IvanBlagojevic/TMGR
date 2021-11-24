package org.gs4tr.termmanager.tests;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@TestSuite("undo_translation")
public class UndoTranslationChangesManualTaskHandlerTest extends AbstractSolrGlossaryTest {

    // TODO: fix this test
    @Ignore
    @Test
    @TestCase("undoTest")
    public void undoTest() throws Exception {
	// creating submission
	Long submissionId = sendToTranslation();

	String tempText = "Dummy text";
	String subTermId = autoSave(submissionId, tempText);

	// undo
	undo(subTermId);
    }

    private String autoSave(Long submissionId, String tempText) {
	Long projectID = 1L;

	List<Term> submissionTerms = getSubmissionTermService().findSubmissionTermsBySubmissionId(submissionId);
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionTerms));

	Term submissionTerm = (Term) CollectionUtils.find(submissionTerms, new Predicate() {

	    @Override
	    public boolean evaluate(Object arg0) {
		Term submissionTerm = (Term) arg0;
		if (submissionTerm.getLanguageId().equals("sr-RS")) {
		    return true;
		}

		return false;
	    }
	});

	String subTermId = submissionTerm.getUuId();

	String taskName = "auto save translation";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "autoSave.json", new String[] { "$termTicket", subTermId },
		new String[] { "$termText", tempText });

	Long[] projectIds = { projectID };

	TaskResponse tasksResponse = taskHandler.processTasks(projectIds, null, command, null);
	Assert.assertNotNull(tasksResponse);

	submissionTerm = getSubmissionTermService().findById(subTermId, projectID);
	Assert.assertNotNull(submissionTerm);
	Assert.assertTrue(!submissionTerm.getCommited());
	Assert.assertTrue(StringUtils.isEmpty(submissionTerm.getName()));
	Assert.assertEquals(tempText, submissionTerm.getTempText());

	return subTermId;
    }

    private Long sendToTranslation() {
	Long projectID = 1L;

	String taskName = "send to translation";

	String projectTicket = IdEncrypter.encryptGenericId(projectID);
	String submissionName = "mySub";
	String submissionMarkerId = UUID.randomUUID().toString();
	String termEntryTicket = TERM_ENTRY_ID_01;
	String sourceTermTicket = TERM_ID_01;

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "submit.json",
		new String[] { "$submissionMarkerId", submissionMarkerId },
		new String[] { "$projectTicket", projectTicket }, new String[] { "$submissionName", submissionName },
		new String[] { "$termEntryTicket", termEntryTicket },
		new String[] { "$sourceTermTicket", sourceTermTicket });
	TaskResponse result = taskHandler.processTasks(null, null, command, null);

	Assert.assertNotNull(result);

	String submissionTicket = result.getResponseTicket().getTicketId();
	Long submissionId = IdEncrypter.decryptGenericId(submissionTicket);
	Assert.assertNotNull(submissionId);

	ItemStatusType expectedStatus = ItemStatusTypeHolder.IN_TRANSLATION_REVIEW;

	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);
	Assert.assertNotNull(submission);
	Assert.assertEquals(submissionName, submission.getName());
	Assert.assertEquals(expectedStatus, submission.getEntityStatusPriority().getStatus());
	Assert.assertEquals(expectedStatus, submission.getEntityStatusPriority().getStatus());

	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionLanguages));
	Assert.assertEquals(1, submissionLanguages.size());
	for (SubmissionLanguage submissionLanguage : submissionLanguages) {
	    Assert.assertEquals(expectedStatus, submissionLanguage.getStatusAssignee());
	    Assert.assertEquals(expectedStatus, submissionLanguage.getEntityStatusPriority().getStatus());
	}

	return submissionId;
    }

    private void undo(String subTermId) throws InterruptedException {
	Long projectID = 1L;

	Term subTerm = getTermService().findTermById(subTermId, projectID);

	String taskName = "undo translation changes";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "undo.json", new String[] { "$termTicket", subTermId });

	TaskResponse result = taskHandler.processTasks(new Long[] { projectID }, null, command, null);
	Assert.assertNotNull(result);

	Thread.sleep(200);

	subTerm = getSubmissionTermService().findById(subTermId, projectID);
	Assert.assertNotNull(subTerm);
	Assert.assertTrue(subTerm.getCommited());
	Assert.assertTrue(StringUtils.isEmpty(subTerm.getName()));
	Assert.assertEquals(subTerm.getTempText(), subTerm.getTempText());
    }
}
