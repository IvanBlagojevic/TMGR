package org.gs4tr.termmanager.tests;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.ProjectUserDetail;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.SubmissionUser;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@TestSuite("translation_round_trip")
public class TranslationRoundTripSynonymsTest extends AbstractSpringServiceTests {

    private static final String USER = "generic1";

    private static final Long USER_ID = 6L;

    // TODO [mstrainovic] tests
    @Ignore
    @Test
    @TestCase("test")
    public void testRoundTripWithSynonyms() throws Exception {
	// creating second submission
	Long submissionId = sendToTranslation();
	String submissionTermId = autoSaveSynonym(submissionId);
	commitChanges(submissionId, submissionTermId);
	// canceling one synonym
	cancelTranslation(submissionId);
	// completing translation
	approve(submissionId, submissionTermId);

    }

    private void approve(Long submissionId, String submissionTermId) throws Exception {
	Long projectID = 2L;
	TmProject project = getProjectService().findProjectById(projectID, ProjectDetail.class, ProjectUserDetail.class,
		ProjectLanguageDetail.class);
	ProjectDetail projectDetail = project.getProjectDetail();
	Assert.assertNotNull(projectDetail);
	Assert.assertEquals(3, projectDetail.getTermCount());
	Assert.assertEquals(1, projectDetail.getTermEntryCount());
	Assert.assertEquals(1, projectDetail.getActiveSubmissionCount());
	Assert.assertEquals(1, projectDetail.getTermInSubmissionCount());
	Assert.assertEquals(1, projectDetail.getApprovedTermCount());

	String taskName = "approve term translation status";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String submissionTicket = IdEncrypter.encryptGenericId(submissionId);

	String termTicket = submissionTermId;

	Object command = getTaskHandlerCommand(taskHandler, "approve.json",
		new String[] { "$submissionTicket", submissionTicket }, new String[] { "$subTermTicket", termTicket });

	TaskResponse tasksResponse = taskHandler.processTasks(null, null, command, null);
	Assert.assertNotNull(tasksResponse);

	Term subTerm = getSubmissionTermService().findById(submissionTermId, projectID);
	Assert.assertEquals(subTerm.getTempText(), subTerm.getName());
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), subTerm.getStatus());
	Assert.assertNotNull(subTerm.getDateCompleted());

	ItemStatusType expectedStatus = ItemStatusTypeHolder.COMPLETED;

	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);
	Assert.assertNotNull(submission);
	SubmissionUser submissionUser = submission.getSubmissionUser(USER);
	Assert.assertNotNull(submissionUser);
	Assert.assertEquals(expectedStatus, submissionUser.getEntityStatusPriority().getStatus());
	Assert.assertEquals(expectedStatus, submission.getEntityStatusPriority().getStatus());
	Assert.assertNotNull(submission.getEntityStatusPriority().getDateCompleted());

	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionLanguages));
	Assert.assertEquals(1, submissionLanguages.size());
	for (SubmissionLanguage submissionLanguage : submissionLanguages) {
	    Assert.assertEquals(expectedStatus, submissionLanguage.getStatusAssignee());
	    Assert.assertEquals(expectedStatus, submissionLanguage.getEntityStatusPriority().getStatus());
	    Assert.assertNotNull(submissionLanguage.getEntityStatusPriority().getDateCompleted());
	    Assert.assertEquals(0, submissionLanguage.getTermInFinalReviewCount());
	    Assert.assertEquals(1, submissionLanguage.getTermCompletedCount());
	}

	project = getProjectService().findProjectById(projectID, ProjectDetail.class, ProjectUserDetail.class,
		ProjectLanguageDetail.class);
	projectDetail = project.getProjectDetail();
	Assert.assertNotNull(projectDetail);
	Assert.assertEquals(3, projectDetail.getTermCount());
	Assert.assertEquals(1, projectDetail.getTermEntryCount());
	Assert.assertEquals(0, projectDetail.getActiveSubmissionCount());
	Assert.assertEquals(0, projectDetail.getTermInSubmissionCount());
	Assert.assertEquals(2, projectDetail.getApprovedTermCount());

	Set<ProjectLanguageDetail> languageDetails = projectDetail.getLanguageDetails();
	Assert.assertTrue(CollectionUtils.isNotEmpty(languageDetails));
	for (ProjectLanguageDetail languageDetail : languageDetails) {
	    String languageId = languageDetail.getLanguageId();
	    if (languageId.equals("en-US")) {
		Assert.assertEquals(1, languageDetail.getTermCount());
	    } else if (languageId.equals("de-DE")) {
		Assert.assertEquals(2, languageDetail.getTermCount());
		Assert.assertEquals(0, languageDetail.getActiveSubmissionCount());
		Assert.assertEquals(0, languageDetail.getTermInSubmissionCount());
		Assert.assertEquals(1, languageDetail.getApprovedTermCount());
	    }
	}

	Set<ProjectUserDetail> userDetails = projectDetail.getUserDetails();
	Assert.assertTrue(CollectionUtils.isNotEmpty(userDetails));
	ProjectUserDetail userDetail = projectDetail.getProjectUserDetail(USER_ID);
	Assert.assertEquals(0, userDetail.getActiveSubmissionCount());
	Assert.assertEquals(1, userDetail.getCompletedSubmissionCount());
	Assert.assertEquals(1, userDetail.getTermEntryCount());
    }

    private String autoSaveSynonym(Long submissionId) {
	Long projectID = 2L;
	List<Term> submissionTerms = getSubmissionTermService().findSubmissionTermsBySubmissionId(submissionId);
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionTerms));
	Assert.assertEquals(2, submissionTerms.size());

	Term submissionTerm = submissionTerms.get(0);
	String termName = submissionTerm.getName();
	String subTermId = submissionTerm.getUuId();

	String termTicket = subTermId;

	String taskName = "auto save translation";

	String termText = "This is dummy text";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "autoSave.json", new String[] { "$termTicket", termTicket },
		new String[] { "$termText", termText });

	TaskResponse tasksResponse = taskHandler.processTasks(null, null, command, null);
	Assert.assertNotNull(tasksResponse);

	submissionTerm = getSubmissionTermService().findById(subTermId, projectID);
	Assert.assertNotNull(submissionTerm);
	Assert.assertTrue(!submissionTerm.getCommited());
	Assert.assertEquals(termName, submissionTerm.getName());
	Assert.assertEquals(termText, submissionTerm.getTempText());

	return subTermId;
    }

    private void cancelTranslation(Long submissionID) throws Exception {
	Long projectID = 2L;
	TmProject project = getProjectService().findProjectById(projectID, ProjectDetail.class);
	ProjectDetail projectDetail = project.getProjectDetail();
	Assert.assertNotNull(projectDetail);
	Assert.assertEquals(1, projectDetail.getActiveSubmissionCount());
	Assert.assertEquals(2, projectDetail.getTermInSubmissionCount());
	Assert.assertEquals(1, projectDetail.getApprovedTermCount());

	List<Term> submissionTerms = getSubmissionTermService().findSubmissionTermsBySubmissionId(submissionID);
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionTerms));
	Assert.assertEquals(2, submissionTerms.size());

	Term submissionTerm = submissionTerms.get(1);
	Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), submissionTerm.getStatus());

	String subTermId = submissionTerm.getUuId();
	String termTicket = subTermId;

	String taskName = "cancel translation";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String submissionTicket = TicketConverter.fromInternalToDto(submissionID);

	Object command = getTaskHandlerCommand(taskHandler, "cancelSynonym.json",
		new String[] { "$submissionTicket", submissionTicket }, new String[] { "$termTicket", termTicket });

	TaskResponse tasksResponse = taskHandler.processTasks(null, null, command, null);
	Assert.assertNotNull(tasksResponse);

	Term subTerm = getSubmissionTermService().findById(subTermId, projectID);
	Assert.assertTrue(subTerm.getCanceled());
	Assert.assertNotNull(subTerm.getDateCompleted());

	ItemStatusType expectedStatus = ItemStatusTypeHolder.IN_FINAL_REVIEW;

	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionID);
	Assert.assertNotNull(submission);
	SubmissionUser submissionUser = submission.getSubmissionUser("pm");
	Assert.assertNotNull(submissionUser);
	Assert.assertEquals(expectedStatus, submissionUser.getEntityStatusPriority().getStatus());
	Assert.assertEquals(expectedStatus, submission.getEntityStatusPriority().getStatus());
	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionLanguages));
	for (SubmissionLanguage subLang : submissionLanguages) {
	    Assert.assertEquals(expectedStatus, subLang.getEntityStatusPriority().getStatus());
	    Assert.assertEquals(ItemStatusTypeHolder.COMPLETED, subLang.getStatusAssignee());
	    Assert.assertEquals(1, subLang.getTermCanceledCount());
	    Assert.assertEquals(1, subLang.getTermInFinalReviewCount());
	}

	project = getProjectService().findProjectById(projectID, ProjectDetail.class);
	projectDetail = project.getProjectDetail();
	Assert.assertNotNull(projectDetail);
	Assert.assertEquals(1, projectDetail.getActiveSubmissionCount());
	Assert.assertEquals(1, projectDetail.getTermInSubmissionCount());
	Assert.assertEquals(1, projectDetail.getApprovedTermCount());
    }

    private void commitChanges(Long submissionId, String submissionTermId) {
	Long projectID = 2L;
	TmProject project = getProjectService().findProjectById(projectID, ProjectDetail.class);
	ProjectDetail projectDetail = project.getProjectDetail();
	Assert.assertNotNull(projectDetail);
	Assert.assertEquals(1, projectDetail.getActiveSubmissionCount());
	Assert.assertEquals(2, projectDetail.getTermInSubmissionCount());
	Assert.assertEquals(1, projectDetail.getApprovedTermCount());

	String submissionTicket = TicketConverter.fromInternalToDto(submissionId);

	String taskName = "commit translation changes";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "commit.json",
		new String[] { "$submissionTicket", submissionTicket });

	TaskResponse tasksResponse = taskHandler.processTasks(null, null, command, null);
	Assert.assertNotNull(tasksResponse);

	Term subTerm = getSubmissionTermService().findById(submissionTermId, projectID);
	Assert.assertEquals(subTerm.getTempText(), subTerm.getName());
	Assert.assertEquals(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(), subTerm.getStatus());
	Assert.assertNotNull(subTerm.getDateCompleted());

	ItemStatusType expectedStatus = ItemStatusTypeHolder.IN_TRANSLATION_REVIEW;

	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);
	Assert.assertNotNull(submission);
	SubmissionUser submissionUser = submission.getSubmissionUser(USER);
	Assert.assertNotNull(submissionUser);
	Assert.assertEquals(expectedStatus, submissionUser.getEntityStatusPriority().getStatus());
	Assert.assertEquals(expectedStatus, submission.getEntityStatusPriority().getStatus());

	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionLanguages));
	Assert.assertEquals(1, submissionLanguages.size());
	for (SubmissionLanguage submissionLanguage : submissionLanguages) {
	    Assert.assertEquals(expectedStatus, submissionLanguage.getStatusAssignee());
	    Assert.assertEquals(expectedStatus, submissionLanguage.getEntityStatusPriority().getStatus());
	    Assert.assertEquals(1, submissionLanguage.getTermInFinalReviewCount());
	    Assert.assertEquals(0, submissionLanguage.getTermCompletedCount());
	}
    }

    private Long sendToTranslation() {
	Long projectID = 2L;
	TmProject project = getProjectService().findProjectById(projectID, ProjectDetail.class, ProjectUserDetail.class,
		ProjectLanguageDetail.class);
	ProjectDetail projectDetail = project.getProjectDetail();
	Assert.assertNotNull(projectDetail);
	Assert.assertEquals(0, projectDetail.getActiveSubmissionCount());
	Assert.assertEquals(0, projectDetail.getTermInSubmissionCount());
	Assert.assertEquals(1, projectDetail.getApprovedTermCount());

	Set<ProjectUserDetail> userDetails = projectDetail.getUserDetails();
	Assert.assertTrue(CollectionUtils.isNotEmpty(userDetails));
	ProjectUserDetail userDetail = projectDetail.getProjectUserDetail(USER_ID);
	Assert.assertEquals(0, userDetail.getActiveSubmissionCount());
	Assert.assertEquals(0, userDetail.getCompletedSubmissionCount());
	Assert.assertEquals(1, userDetail.getTermEntryCount());

	String taskName = "send to translation";

	String projectTicket = IdEncrypter.encryptGenericId(projectID);
	String submissionName = "mySubSynonyms";
	String submissionMarkerId = UUID.randomUUID().toString();
	String termEntryTicket = IdEncrypter.encryptGenericId(13L);
	String sourceTermTicket = IdEncrypter.encryptGenericId(10L);

	Term term1 = getTermService().findTermById("11", projectID);
	Term term2 = getTermService().findTermById("12", projectID);

	String term1MarkerId = term1.getUuId();
	String term2MarkerId = term2.getUuId();

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "submitSynonyms.json",
		new String[] { "$submissionMarkerId", submissionMarkerId },
		new String[] { "$projectTicket", projectTicket }, new String[] { "$submissionName", submissionName },
		new String[] { "$termEntryTicket", termEntryTicket },
		new String[] { "$sourceTermTicket", sourceTermTicket },
		new String[] { "$termMarkerId1", term1MarkerId }, new String[] { "$termMarkerId2", term2MarkerId });
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

	project = getProjectService().findProjectById(projectID, ProjectDetail.class, ProjectUserDetail.class,
		ProjectLanguageDetail.class);
	projectDetail = project.getProjectDetail();
	Assert.assertNotNull(projectDetail);
	Assert.assertEquals(1, projectDetail.getActiveSubmissionCount());
	Assert.assertEquals(2, projectDetail.getTermInSubmissionCount());
	Assert.assertEquals(1, projectDetail.getApprovedTermCount());

	userDetails = projectDetail.getUserDetails();
	Assert.assertTrue(CollectionUtils.isNotEmpty(userDetails));
	userDetail = projectDetail.getProjectUserDetail(USER_ID);
	Assert.assertEquals(1, userDetail.getActiveSubmissionCount());
	Assert.assertEquals(0, userDetail.getCompletedSubmissionCount());
	Assert.assertEquals(1, userDetail.getTermEntryCount());

	return submissionId;
    }
}
