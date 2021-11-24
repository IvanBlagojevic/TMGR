package org.gs4tr.termmanager.tests;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.ProjectLanguageUserDetail;
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
import org.junit.Test;

@TestSuite("translation_round_trip")
public class TranslationRoundTripTest extends AbstractSolrGlossaryTest {

    private static final String USER = "generic1";

    @Test
    @TestCase("test")
    public void testRoundTrip() throws Exception {
	// creating second submission
	Long submissionId = sendToTranslation();
	String submissionTermId = autoSave(submissionId);
	commitChanges(submissionId, submissionTermId);
	// completing translation
	approve(submissionId, submissionTermId);
	// canceling first submission
	cancelTranslation();
    }

    private void approve(Long submissionId, String submissionTermId) throws Exception {
	Long projectID = 1L;
	TmProject project = getProjectService().findProjectById(projectID, ProjectDetail.class, ProjectUserDetail.class,
		ProjectLanguageDetail.class);
	ProjectDetail projectDetail = project.getProjectDetail();
	Assert.assertNotNull(projectDetail);
	Assert.assertEquals(9, projectDetail.getTermCount());
	Assert.assertEquals(2, projectDetail.getTermEntryCount());
	Assert.assertEquals(2, projectDetail.getActiveSubmissionCount());
	Assert.assertEquals(4, projectDetail.getTermInSubmissionCount());
	Assert.assertEquals(5, projectDetail.getApprovedTermCount());

	String taskName = "approve term translation status";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String submissionTicket = IdEncrypter.encryptGenericId(submissionId);

	// String termTicket = IdEncrypter.encryptGenericId(submissionTermId);

	Object command = getTaskHandlerCommand(taskHandler, "approve.json",
		new String[] { "$submissionTicket", submissionTicket },
		new String[] { "$subTermTicket", submissionTermId });

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
	Assert.assertEquals(9, projectDetail.getTermCount());
	Assert.assertEquals(2, projectDetail.getTermEntryCount());
	Assert.assertEquals(1, projectDetail.getActiveSubmissionCount());
	Assert.assertEquals(3, projectDetail.getTermInSubmissionCount());
	Assert.assertEquals(6, projectDetail.getApprovedTermCount());

	Set<ProjectLanguageDetail> languageDetails = projectDetail.getLanguageDetails();
	Assert.assertTrue(CollectionUtils.isNotEmpty(languageDetails));
	for (ProjectLanguageDetail languageDetail : languageDetails) {
	    String languageId = languageDetail.getLanguageId();
	    if (languageId.equals("en-US")) {
		Assert.assertEquals(5, languageDetail.getTermCount());
	    } else if (languageId.equals("de-DE")) {
		Assert.assertEquals(2, languageDetail.getTermCount());
		Assert.assertEquals(1, languageDetail.getActiveSubmissionCount());
		Assert.assertEquals(1, languageDetail.getTermInSubmissionCount());
		Assert.assertEquals(1, languageDetail.getApprovedTermCount());
	    } else if (languageId.equals("fr-FR")) {
		Assert.assertEquals(1, languageDetail.getTermCount());
		Assert.assertEquals(1, languageDetail.getActiveSubmissionCount());
		Assert.assertEquals(1, languageDetail.getTermInSubmissionCount());
		Assert.assertEquals(0, languageDetail.getApprovedTermCount());
	    } else if (languageId.equals("sr-RS")) {
		Assert.assertEquals(1, languageDetail.getTermCount());
		Assert.assertEquals(0, languageDetail.getActiveSubmissionCount());
		Assert.assertEquals(0, languageDetail.getTermInSubmissionCount());
		Assert.assertEquals(1, languageDetail.getApprovedTermCount());
	    }
	}

	Long userId = 6L;

	Set<ProjectUserDetail> userDetails = projectDetail.getUserDetails();
	Assert.assertTrue(CollectionUtils.isNotEmpty(userDetails));
	ProjectUserDetail userDetail = projectDetail.getProjectUserDetail(userId);
	Assert.assertEquals(1, userDetail.getActiveSubmissionCount());
	Assert.assertEquals(1, userDetail.getCompletedSubmissionCount());
	Assert.assertEquals(2, userDetail.getTermEntryCount());
    }

    private String autoSave(Long submissionId) {
	Long projectID = 1L;

	List<Term> submissionTerms = getSubmissionTermService().findSubmissionTermsBySubmissionId(submissionId);
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionTerms));

	Term submissionTerm = submissionTerms.stream().filter(s -> s.getLanguageId().equals("sr-RS")).findFirst()
		.orElse(null);

	String subTermId = submissionTerm.getUuId();

	// String termTicket = IdEncrypter.encryptGenericId(subTermId);

	String taskName = "auto save translation";

	String termText = "This is dummy text";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "autoSave.json", new String[] { "$termTicket", subTermId },
		new String[] { "$termText", termText });

	Long[] projectIds = new Long[] { projectID };

	TaskResponse tasksResponse = taskHandler.processTasks(projectIds, null, command, null);
	Assert.assertNotNull(tasksResponse);

	submissionTerm = getSubmissionTermService().findById(subTermId, projectID);
	Assert.assertNotNull(submissionTerm);
	Assert.assertTrue(!submissionTerm.getCommited());
	Assert.assertTrue(StringUtils.isEmpty(submissionTerm.getName()));
	Assert.assertEquals(termText, submissionTerm.getTempText());

	return subTermId;
    }

    private void cancelTranslation() throws Exception {
	Long projectID = 1L;

	String taskName = "cancel translation";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Long submissionID = 1L;

	String submissionTicket = TicketConverter.fromInternalToDto(submissionID);

	Object command = getTaskHandlerCommand(taskHandler, "cancel.json",
		new String[] { "$submissionTicket", submissionTicket });

	Long[] projectIds = new Long[] { projectID };

	TaskResponse tasksResponse = taskHandler.processTasks(projectIds, null, command, null);
	Assert.assertNotNull(tasksResponse);

	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionID);
	Assert.assertNotNull(submission);
	SubmissionUser submissionUser = submission.getSubmissionUser("pm");
	Assert.assertNotNull(submissionUser);
	Assert.assertEquals(ItemStatusTypeHolder.CANCELLED, submissionUser.getEntityStatusPriority().getStatus());
	Assert.assertEquals(ItemStatusTypeHolder.CANCELLED, submission.getEntityStatusPriority().getStatus());
	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionLanguages));
	for (SubmissionLanguage subLang : submissionLanguages) {
	    Assert.assertEquals(ItemStatusTypeHolder.CANCELLED, subLang.getEntityStatusPriority().getStatus());
	    Assert.assertEquals(ItemStatusTypeHolder.CANCELLED, subLang.getStatusAssignee());
	    Assert.assertTrue(subLang.getTermCanceledCount() > 0);
	    Assert.assertTrue(subLang.getTermCanceledCount() == subLang.getTermCount());
	}

	TmProject project = getProjectService().findProjectById(projectID, ProjectDetail.class, ProjectUserDetail.class,
		ProjectLanguageDetail.class);
	ProjectDetail projectDetail = project.getProjectDetail();
	Assert.assertNotNull(projectDetail);
	Assert.assertEquals(9, projectDetail.getTermCount());
	Assert.assertEquals(2, projectDetail.getTermEntryCount());
	Assert.assertEquals(0, projectDetail.getActiveSubmissionCount());
	Assert.assertEquals(0, projectDetail.getTermInSubmissionCount());
	Assert.assertEquals(6, projectDetail.getApprovedTermCount());

	Set<ProjectLanguageDetail> languageDetails = projectDetail.getLanguageDetails();
	Assert.assertTrue(CollectionUtils.isNotEmpty(languageDetails));
	for (ProjectLanguageDetail languageDetail : languageDetails) {
	    String languageId = languageDetail.getLanguageId();
	    if (languageId.equals("en-US")) {
		Assert.assertEquals(5, languageDetail.getTermCount());
	    } else if (languageId.equals("de-DE")) {
		Assert.assertEquals(2, languageDetail.getTermCount());
		Assert.assertEquals(0, languageDetail.getActiveSubmissionCount());
		Assert.assertEquals(0, languageDetail.getTermInSubmissionCount());
		Assert.assertEquals(1, languageDetail.getCompletedSubmissionCount());
		Assert.assertEquals(1, languageDetail.getApprovedTermCount());
	    } else if (languageId.equals("fr-FR")) {
		Assert.assertEquals(1, languageDetail.getTermCount());
		Assert.assertEquals(0, languageDetail.getActiveSubmissionCount());
		Assert.assertEquals(0, languageDetail.getTermInSubmissionCount());
		Assert.assertEquals(0, languageDetail.getApprovedTermCount());
		Assert.assertEquals(1, languageDetail.getCompletedSubmissionCount());
	    } else if (languageId.equals("sr-RS")) {
		Assert.assertEquals(1, languageDetail.getTermCount());
		Assert.assertEquals(0, languageDetail.getActiveSubmissionCount());
		Assert.assertEquals(0, languageDetail.getTermInSubmissionCount());
		Assert.assertEquals(1, languageDetail.getApprovedTermCount());
		Assert.assertEquals(1, languageDetail.getCompletedSubmissionCount());
	    }
	}

	Long userId = 6L;

	Set<ProjectUserDetail> userDetails = projectDetail.getUserDetails();
	Assert.assertTrue(CollectionUtils.isNotEmpty(userDetails));
	ProjectUserDetail userDetail = projectDetail.getProjectUserDetail(userId);
	Assert.assertEquals(0, userDetail.getActiveSubmissionCount());
	Assert.assertEquals(2, userDetail.getCompletedSubmissionCount());
	Assert.assertEquals(2, userDetail.getTermEntryCount());
    }

    private void commitChanges(Long submissionId, String submissionTermId) {
	Long projectID = 1L;

	String submissionTicket = TicketConverter.fromInternalToDto(submissionId);

	String taskName = "commit translation changes";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "commit.json",
		new String[] { "$submissionTicket", submissionTicket });

	TaskResponse tasksResponse = taskHandler.processTasks(null, null, command, null);
	Assert.assertNotNull(tasksResponse);

	Term subTerm = getSubmissionTermService().findById(submissionTermId, projectID);
	Assert.assertTrue(StringUtils.isNotEmpty(subTerm.getTempText()));
	Assert.assertEquals(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(), subTerm.getStatus());
	Assert.assertNotNull(subTerm.getDateCompleted());

	ItemStatusType expectedStatus = ItemStatusTypeHolder.COMPLETED;

	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);
	Assert.assertNotNull(submission);
	SubmissionUser submissionUser = submission.getSubmissionUser(USER);
	Assert.assertNotNull(submissionUser);
	Assert.assertEquals(expectedStatus, submissionUser.getEntityStatusPriority().getStatus());
	Assert.assertEquals(ItemStatusTypeHolder.IN_FINAL_REVIEW, submission.getEntityStatusPriority().getStatus());
	Assert.assertNotNull(submission.getEntityStatusPriority().getDateCompleted());

	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionLanguages));
	Assert.assertEquals(1, submissionLanguages.size());
	for (SubmissionLanguage submissionLanguage : submissionLanguages) {
	    Assert.assertEquals(expectedStatus, submissionLanguage.getStatusAssignee());
	    Assert.assertEquals(ItemStatusTypeHolder.IN_FINAL_REVIEW,
		    submissionLanguage.getEntityStatusPriority().getStatus());
	    Assert.assertNotNull(submissionLanguage.getEntityStatusPriority().getDateCompleted());
	    Assert.assertEquals(1, submissionLanguage.getTermInFinalReviewCount());
	    Assert.assertEquals(0, submissionLanguage.getTermCompletedCount());
	}
    }

    private Long sendToTranslation() {
	Long projectID = 1L;
	TmProject project = getProjectService().findProjectById(projectID, ProjectDetail.class, ProjectUserDetail.class,
		ProjectLanguageDetail.class, ProjectLanguageUserDetail.class);
	ProjectDetail projectDetail = project.getProjectDetail();
	Assert.assertNotNull(projectDetail);
	Assert.assertEquals(8, projectDetail.getTermCount());
	Assert.assertEquals(2, projectDetail.getTermEntryCount());
	Assert.assertEquals(1, projectDetail.getActiveSubmissionCount());
	Assert.assertEquals(3, projectDetail.getTermInSubmissionCount());
	Assert.assertEquals(5, projectDetail.getApprovedTermCount());

	Set<ProjectLanguageDetail> languageDetails = projectDetail.getLanguageDetails();
	Assert.assertTrue(CollectionUtils.isNotEmpty(languageDetails));
	for (ProjectLanguageDetail languageDetail : languageDetails) {
	    String languageId = languageDetail.getLanguageId();
	    if (languageId.equals("en-US")) {
		Assert.assertEquals(5, languageDetail.getTermCount());
	    } else if (languageId.equals("de-DE")) {
		Assert.assertEquals(2, languageDetail.getTermCount());
		Assert.assertEquals(1, languageDetail.getActiveSubmissionCount());
		Assert.assertEquals(1, languageDetail.getTermInSubmissionCount());
		Assert.assertEquals(1, languageDetail.getApprovedTermCount());
	    } else if (languageId.equals("fr-FR")) {
		Assert.assertEquals(1, languageDetail.getTermCount());
		Assert.assertEquals(1, languageDetail.getActiveSubmissionCount());
		Assert.assertEquals(1, languageDetail.getTermInSubmissionCount());
		Assert.assertEquals(0, languageDetail.getApprovedTermCount());
	    } else if (languageId.equals("sr-RS")) {
		Assert.assertEquals(0, languageDetail.getTermCount());
		Assert.assertEquals(0, languageDetail.getActiveSubmissionCount());
		Assert.assertEquals(0, languageDetail.getTermInSubmissionCount());
		Assert.assertEquals(0, languageDetail.getApprovedTermCount());
	    }
	}

	Long userId = 6L;

	Set<ProjectUserDetail> userDetails = projectDetail.getUserDetails();
	Assert.assertTrue(CollectionUtils.isNotEmpty(userDetails));
	ProjectUserDetail userDetail = projectDetail.getProjectUserDetail(userId);
	Assert.assertEquals(1, userDetail.getActiveSubmissionCount());
	Assert.assertEquals(0, userDetail.getCompletedSubmissionCount());
	Assert.assertEquals(2, userDetail.getTermEntryCount());

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

	project = getProjectService().findProjectById(projectID, ProjectDetail.class, ProjectUserDetail.class,
		ProjectLanguageDetail.class, ProjectLanguageUserDetail.class);
	projectDetail = project.getProjectDetail();
	Assert.assertNotNull(projectDetail);
	Assert.assertEquals(9, projectDetail.getTermCount());
	Assert.assertEquals(2, projectDetail.getTermEntryCount());
	Assert.assertEquals(2, projectDetail.getActiveSubmissionCount());
	Assert.assertEquals(4, projectDetail.getTermInSubmissionCount());
	Assert.assertEquals(5, projectDetail.getApprovedTermCount());

	languageDetails = projectDetail.getLanguageDetails();
	Assert.assertTrue(CollectionUtils.isNotEmpty(languageDetails));
	for (ProjectLanguageDetail languageDetail : languageDetails) {
	    String languageId = languageDetail.getLanguageId();
	    if (languageId.equals("en-US")) {
		Assert.assertEquals(5, languageDetail.getTermCount());
	    } else if (languageId.equals("de-DE")) {
		Assert.assertEquals(2, languageDetail.getTermCount());
		Assert.assertEquals(1, languageDetail.getActiveSubmissionCount());
		Assert.assertEquals(1, languageDetail.getTermInSubmissionCount());
		Assert.assertEquals(1, languageDetail.getApprovedTermCount());
	    } else if (languageId.equals("fr-FR")) {
		Assert.assertEquals(1, languageDetail.getTermCount());
		Assert.assertEquals(1, languageDetail.getActiveSubmissionCount());
		Assert.assertEquals(1, languageDetail.getTermInSubmissionCount());
		Assert.assertEquals(0, languageDetail.getApprovedTermCount());
	    } else if (languageId.equals("sr-RS")) {
		Assert.assertEquals(1, languageDetail.getTermCount());
		Assert.assertEquals(1, languageDetail.getActiveSubmissionCount());
		Assert.assertEquals(1, languageDetail.getTermInSubmissionCount());
		Assert.assertEquals(0, languageDetail.getApprovedTermCount());
		Assert.assertEquals(0, languageDetail.getCompletedSubmissionCount());
	    }
	}

	userDetails = projectDetail.getUserDetails();
	Assert.assertTrue(CollectionUtils.isNotEmpty(userDetails));
	userDetail = projectDetail.getProjectUserDetail(userId);
	Assert.assertEquals(2, userDetail.getActiveSubmissionCount());
	Assert.assertEquals(0, userDetail.getCompletedSubmissionCount());
	Assert.assertEquals(2, userDetail.getTermEntryCount());

	return submissionId;
    }
}
