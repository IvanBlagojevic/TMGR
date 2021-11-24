package org.gs4tr.termmanager.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;

@TestSuite("save_dashboard")
public class SaveDashboardManualTaskHandlerTest extends AbstractSolrGlossaryTest {

    /*
     * TERII-3208 Dashboard | Add Pending Approval and On Hold columns
     */
    @Test
    @TestCase("process_tasks")
    public void testOnHoldPendingApprovalStatusCountProcessTasks() throws IOException, InterruptedException {
	String taskName = "save dashboard";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "saveDashboard.json");

	ProjectDetail projectDetailBefore = getProjectDetailService().findByProjectId(1L, ProjectLanguageDetail.class);

	/* Check number of terms per status before save action */
	Assert.assertEquals(5, projectDetailBefore.getApprovedTermCount());
	Assert.assertEquals(3, projectDetailBefore.getForbiddenTermCount());
	Assert.assertEquals(2, projectDetailBefore.getPendingApprovalCount());
	Assert.assertEquals(1, projectDetailBefore.getOnHoldTermCount());

	Assert.assertEquals(4, projectDetailBefore.getLanguageCount());
	Assert.assertEquals(8, projectDetailBefore.getTermCount());
	Assert.assertEquals(2, projectDetailBefore.getTermEntryCount());

	TaskResponse tasksResponse = taskHandler.processTasks(null, null, command, null);

	ProjectDetail projectDetailAfter = getProjectDetailService().findByProjectId(1L, ProjectLanguageDetail.class);
	Set<ProjectLanguageDetail> languageDetailsAfter = projectDetailAfter.getLanguageDetails();

	Assert.assertEquals(4, languageDetailsAfter.size());

	/* Check number of terms per status after save action */
	Assert.assertEquals(5, projectDetailAfter.getApprovedTermCount());
	Assert.assertEquals(3, projectDetailAfter.getForbiddenTermCount());
	/* One term is saved as OnHold and one is saved as PendingApproval */
	Assert.assertEquals(3, projectDetailAfter.getPendingApprovalCount());
	Assert.assertEquals(2, projectDetailAfter.getOnHoldTermCount());

	/* Language count should not be changed */
	Assert.assertEquals(4, projectDetailAfter.getLanguageCount());
	/* Two terms are saved. Now there should be 10 terms */
	Assert.assertEquals(10, projectDetailAfter.getTermCount());
	/* One TermEntry is saved. Now there should be 3 TermEntries */
	Assert.assertEquals(3, projectDetailAfter.getTermEntryCount());

	Assert.assertEquals(2, projectDetailAfter.getOnHoldTermCount());
	Assert.assertEquals(3, projectDetailAfter.getPendingApprovalCount());

	assertOnHoldPendingApprovalCount("en-US", 2, 2, languageDetailsAfter);

	/* For de-DE language On Hold count and Pending Approval count was 0 */
	assertOnHoldPendingApprovalCount("de-DE", 0, 1, languageDetailsAfter);

	Assert.assertNotNull(tasksResponse);

    }

    @Test
    @TestCase("process_tasks")
    public void testProcessTasks() throws IOException, InterruptedException {
	String taskName = "save dashboard";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "saveDashboard.json");

	TaskResponse tasksResponse = taskHandler.processTasks(null, null, command, null);
	Assert.assertNotNull(tasksResponse);
    }

    // TERII-5370
    @Test
    @TestCase("process_tasks")
    public void test_approve_blackisted_term() throws Exception {
	ProjectDetail projectDetailBefore = getProjectDetailService().findByProjectId(PROJECT_ID_2,
		ProjectLanguageDetail.class);

	/* Check number of terms per status before save action */
	Assert.assertEquals(2, projectDetailBefore.getApprovedTermCount());
	Assert.assertEquals(2, projectDetailBefore.getForbiddenTermCount());
	Assert.assertEquals(0, projectDetailBefore.getPendingApprovalCount());
	Assert.assertEquals(0, projectDetailBefore.getOnHoldTermCount());
	Assert.assertEquals(4, projectDetailBefore.getTermCount());
	Assert.assertEquals(2, projectDetailBefore.getTermEntryCount());

	String taskName = "save dashboard";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "saveDashboard_TERII_5370.json",
		new String[] { "$projectTicket", IdEncrypter.encryptGenericId(PROJECT_ID_2) },
		new String[] { "$termEntryId", TERM_ENTRY_ID_15 }, new String[] { "$termId", TERM_ID_34 },
		new String[] { "$status", ItemStatusTypeHolder.PROCESSED.getName() },
		new String[] { "$termName", "puppy" });

	TaskResponse tasksResponse = taskHandler.processTasks(null, null, command, null);
	Assert.assertNotNull(tasksResponse);

	/* Check number of terms per status after save action */
	ProjectDetail projectDetailAfter = getProjectDetailService().findByProjectId(PROJECT_ID_2,
		ProjectLanguageDetail.class);

	Assert.assertEquals(3, projectDetailAfter.getTermCount());
	Assert.assertEquals(2, projectDetailAfter.getApprovedTermCount());
	Assert.assertEquals(1, projectDetailAfter.getForbiddenTermCount());
	Assert.assertEquals(0, projectDetailAfter.getPendingApprovalCount());
	Assert.assertEquals(0, projectDetailAfter.getOnHoldTermCount());
	Assert.assertEquals(2, projectDetailAfter.getTermEntryCount());
    }

    // TERII-5370
    @Test
    @TestCase("process_tasks")
    public void test_blackist_approved_term() throws Exception {
	ProjectDetail projectDetailBefore = getProjectDetailService().findByProjectId(PROJECT_ID_2,
		ProjectLanguageDetail.class);

	/* Check number of terms per status before save action */
	Assert.assertEquals(2, projectDetailBefore.getApprovedTermCount());
	Assert.assertEquals(2, projectDetailBefore.getForbiddenTermCount());
	Assert.assertEquals(0, projectDetailBefore.getPendingApprovalCount());
	Assert.assertEquals(0, projectDetailBefore.getOnHoldTermCount());
	Assert.assertEquals(4, projectDetailBefore.getTermCount());
	Assert.assertEquals(2, projectDetailBefore.getTermEntryCount());

	String taskName = "save dashboard";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "saveDashboard_TERII_5370.json",
		new String[] { "$projectTicket", IdEncrypter.encryptGenericId(PROJECT_ID_2) },
		new String[] { "$termEntryId", TERM_ENTRY_ID_16 }, new String[] { "$termId", TERM_ID_36 },
		new String[] { "$status", ItemStatusTypeHolder.BLACKLISTED.getName() },
		new String[] { "$termName", "kitty" });

	TaskResponse tasksResponse = taskHandler.processTasks(null, null, command, null);
	Assert.assertNotNull(tasksResponse);

	/* Check number of terms per status after save action */
	ProjectDetail projectDetailAfter = getProjectDetailService().findByProjectId(PROJECT_ID_2,
		ProjectLanguageDetail.class);

	Assert.assertEquals(3, projectDetailAfter.getTermCount());
	Assert.assertEquals(1, projectDetailAfter.getApprovedTermCount());
	Assert.assertEquals(2, projectDetailAfter.getForbiddenTermCount());
	Assert.assertEquals(0, projectDetailAfter.getPendingApprovalCount());
	Assert.assertEquals(0, projectDetailAfter.getOnHoldTermCount());
	Assert.assertEquals(2, projectDetailAfter.getTermEntryCount());
    }

    private void assertOnHoldPendingApprovalCount(String languageId, int onHoldCount, int pendingApprovalCount,
	    Set<ProjectLanguageDetail> languageDetails) {

	Optional<ProjectLanguageDetail> first = languageDetails.stream()
		.filter(ld -> ld.getLanguageId().equals(languageId)).findFirst();

	assertTrue(first.isPresent());

	ProjectLanguageDetail pld = first.get();

	assertEquals(onHoldCount, pld.getOnHoldTermCount());
	assertEquals(pendingApprovalCount, pld.getPendingApprovalCount());
    }
}
