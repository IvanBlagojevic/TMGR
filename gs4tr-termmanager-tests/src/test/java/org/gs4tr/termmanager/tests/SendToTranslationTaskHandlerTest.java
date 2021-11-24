package org.gs4tr.termmanager.tests;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.SubmissionLanguageComment;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;

@TestSuite("send_to_translation")
public class SendToTranslationTaskHandlerTest extends AbstractSolrGlossaryTest {

    @Test
    @TestCase("process_tasks")
    public void sendToTranslationDateModifiedTest() throws Exception {
	String jobMarkerId = UUID.randomUUID().toString();
	String jobName = "JOB000001";
	String projectTicket = TicketConverter.fromInternalToDto(1L);
	String termEntryTicket1 = TERM_ENTRY_ID_01;
	String termEntryTicket2 = TERM_ENTRY_ID_02;

	String sourceTicket1 = TERM_ID_01;
	String sourceTicket2 = TERM_ID_04;

	String taskName = "send to translation";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String[] terms = { sourceTicket1, sourceTicket2 };

	List<TermEntry> regularTermEntriesBefore = getTermEntryService().findTermEntriesByTermIds(Arrays.asList(terms),
		1L);

	TermEntry termEntryBefore = regularTermEntriesBefore.get(0);
	Term termBefore = termEntryBefore.ggetTerms().get(0);

	ProjectDetail projectDetailBefore = getProjectDetailService().findByProjectId(1L, ProjectLanguageDetail.class);

	Object command = getTaskHandlerCommand(taskHandler, "sendToTranslationDateModifiedTest.json",
		new String[] { "$jobMarkerId", jobMarkerId }, new String[] { "$jobName", jobName },
		new String[] { "$projectTicket", projectTicket },
		new String[] { "$termEntryTicket1", termEntryTicket1 },
		new String[] { "$termEntryTicket2", termEntryTicket2 },
		new String[] { "$sourceTicket1", sourceTicket1 }, new String[] { "$sourceTicket2", sourceTicket2 });

	taskHandler.processTasks(null, null, command, null);

	List<TermEntry> regularTermEntriesAfter = getTermEntryService().findTermEntriesByTermIds(Arrays.asList(terms),
		1L);

	TermEntry termEntryAfter = regularTermEntriesAfter.get(0);
	Term termAfter = termEntryAfter.ggetTerms().get(0);

	ProjectDetail projectDetailAfter = getProjectDetailService().findByProjectId(1L, ProjectLanguageDetail.class);

	Assert.assertTrue(termEntryBefore.getDateModified() < termEntryAfter.getDateModified());
	Assert.assertTrue(termBefore.getDateModified() < termAfter.getDateModified());

	Assert.assertTrue(
		projectDetailBefore.getDateModified().getTime() < projectDetailAfter.getDateModified().getTime());

	/* Source language date modified should not be changed */
	Assert.assertFalse(isProjectLanguageDetailDateModifiedChanged("en-US", projectDetailBefore.getLanguageDetails(),
		projectDetailAfter.getLanguageDetails()));

	Assert.assertTrue(isProjectLanguageDetailDateModifiedChanged("de-DE", projectDetailBefore.getLanguageDetails(),
		projectDetailAfter.getLanguageDetails()));
    }

    @Test
    @TestCase("get_task_infos")
    public void sendToTranslationGetTest() throws Exception {
	String taskName = "send to translation";

	String projectTicket = IdEncrypter.encryptGenericId(1);
	String ticket1 = TERM_ENTRY_ID_01;
	String sourceLanguage = "en-US";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "sendToTranslationCommand.json",
		new String[] { "$projectTicket", projectTicket }, new String[] { "$sourceLanguage", sourceLanguage },
		new String[] { "$ticket1", ticket1 });

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] {}, taskName, command);

	String result = JsonUtils.writeValueAsString(taskInfos);

	Assert.assertNotNull(result);

	assertJSONResponse(result, "sendToTranslationGetValidation.json");
    }

    @Test
    @TestCase("process_tasks")
    public void sendToTranslationMultipleUnitsPostTest() throws Exception {
	String jobMarkerId = UUID.randomUUID().toString();
	String jobName = "JOB000001";
	String projectTicket = TicketConverter.fromInternalToDto(1L);
	String termEntryTicket1 = TERM_ENTRY_ID_01;
	String termEntryTicket2 = TERM_ENTRY_ID_02;

	String sourceTicket1 = TERM_ID_01;
	String sourceTicket2 = TERM_ID_04;

	String taskName = "send to translation";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "sentToTranslationMultipleUnits.json",
		new String[] { "$jobMarkerId", jobMarkerId }, new String[] { "$jobName", jobName },
		new String[] { "$projectTicket", projectTicket },
		new String[] { "$termEntryTicket1", termEntryTicket1 },
		new String[] { "$termEntryTicket2", termEntryTicket2 },
		new String[] { "$sourceTicket1", sourceTicket1 }, new String[] { "$sourceTicket2", sourceTicket2 });

	TaskResponse tasksResponse = taskHandler.processTasks(null, null, command, null);
	Assert.assertNotNull(tasksResponse);

	Ticket responseTicket = tasksResponse.getResponseTicket();
	Assert.assertNotNull(responseTicket);
	Long submissionId = TicketConverter.fromDtoToInternal(responseTicket, Long.class);

	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);
	Assert.assertNotNull(submission);

	List<Term> subTerms = getSubmissionTermService().findSubmissionTermsBySubmissionId(submissionId);
	Assert.assertTrue(CollectionUtils.isNotEmpty(subTerms));
    }

    @Test
    @TestCase("process_tasks")
    public void sendToTranslationPostTest() throws Exception {
	String submissionMarkerId = UUID.randomUUID().toString();
	String submissionName = "SUB000001";
	String projectTicket = TicketConverter.fromInternalToDto(1L);
	String termEntryTicket = TERM_ENTRY_ID_01;
	String sourceTicket1 = TERM_ID_01;

	String taskName = "send to translation";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "sendToTranslation.json",
		new String[] { "$submissionMarkerId", submissionMarkerId },
		new String[] { "$submissionName", submissionName }, new String[] { "$projectTicket", projectTicket },
		new String[] { "$termEntryTicket", termEntryTicket }, new String[] { "$sourceTicket1", sourceTicket1 });

	TaskResponse tasksResponse = taskHandler.processTasks(null, null, command, null);
	Assert.assertNotNull(tasksResponse);

	Ticket responseTicket = tasksResponse.getResponseTicket();
	Assert.assertNotNull(responseTicket);
	Long submissionId = TicketConverter.fromDtoToInternal(responseTicket, Long.class);

	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);
	Assert.assertNotNull(submission);
	Set<SubmissionLanguage> subLangs = submission.getSubmissionLanguages();
	Assert.assertTrue(CollectionUtils.isNotEmpty(subLangs));
	Assert.assertEquals(1, subLangs.size());

	for (SubmissionLanguage subLang : subLangs) {
	    Set<SubmissionLanguageComment> submissionLanguageComments = subLang.getSubmissionLanguageComments();
	    Assert.assertTrue(CollectionUtils.isNotEmpty(submissionLanguageComments));
	}

	List<Term> subTerms = getSubmissionTermService().findSubmissionTermsBySubmissionId(submissionId);
	Assert.assertTrue(CollectionUtils.isNotEmpty(subTerms));
    }

}
