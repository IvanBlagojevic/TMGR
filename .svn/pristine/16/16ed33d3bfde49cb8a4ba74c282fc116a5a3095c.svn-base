package org.gs4tr.termmanager.dao.hibernate.backup;

import static java.util.Collections.singletonList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.gs4tr.termmanager.dao.backup.RegularBackupCleanerDAO;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.ProjectLanguageUserDetail;
import org.gs4tr.termmanager.model.ProjectUserDetail;
import org.gs4tr.termmanager.model.Statistics;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RegularBackupCleanerDAOTest extends AbstractBackupDAOTest {

    @Autowired
    private RegularBackupCleanerDAO _regularBackupCleanerDAO;

    @Test
    public void testClearCountsByProjectIds() {

	RegularBackupCleanerDAO dao = getRegularBackupCleanerDAO();
	dao.clearCountsAndStatistics(singletonList(PROJECT_ID), CHUNK_SIZE);
	dao.flush();
	dao.clear();

	ProjectDetail projectDetail = getProjectDetailDAO().findByProjectId(PROJECT_ID);
	assertNotNull(projectDetail);
	validateProjectDetailAfterCleanUp(projectDetail);

	List<ProjectLanguageDetail> projectLanguageDetails = getProjectLanguageDetailDAO()
		.getProjectLanguageDetailsByProjectId(PROJECT_ID);
	assertTrue(isNotEmpty(projectLanguageDetails));
	validateProjectLanguageDetailAfterCleanUp(projectLanguageDetails);

	List<ProjectUserDetail> projectUserDetails = getProjectUserDetailDAO().findByProjectId(PROJECT_ID);
	assertTrue(isNotEmpty(projectUserDetails));
	validateProjectUserDetailAfterCleanUp(projectUserDetails);

	List<Statistics> statistics = getStatisticsDAO().getStatisticsByProjectId(PROJECT_ID);
	assertTrue(isNotEmpty(statistics));
	validateStatisticsAfterCleanUp(statistics);
    }

    @Test
    public void testDeleteByProjectIds() {
	String termEntryId = prepareDbForTests();

	RegularBackupCleanerDAO dao = getRegularBackupCleanerDAO();
	dao.deleteByProjectIds(singletonList(PROJECT_ID), CHUNK_SIZE);
	dao.flush();
	dao.clear();

	DbTermEntry dbTermEntry = getDbTermEntryDAO().findByUuid(termEntryId, true);
	assertNull(dbTermEntry);
    }

    @Test
    public void testDeleteHiddenTerms() {
	String termEntryId = prepareDbForTests();

	RegularBackupCleanerDAO dao = getRegularBackupCleanerDAO();
	dao.deleteHiddenTerms(CHUNK_SIZE);
	dao.flush();
	dao.clear();

	DbTermEntry dbTermEntry = getDbTermEntryDAO().findByUuid(termEntryId, true);
	assertNull(dbTermEntry);
    }

    private RegularBackupCleanerDAO getRegularBackupCleanerDAO() {
	return _regularBackupCleanerDAO;
    }

    private void validateProjectDetailAfterCleanUp(ProjectDetail projectDetail) {
	assertEquals(0, projectDetail.getActiveSubmissionCount());
	assertEquals(0, projectDetail.getApprovedTermCount());
	assertEquals(0, projectDetail.getCompletedSubmissionCount());
	assertEquals(0, projectDetail.getForbiddenTermCount());
	assertEquals(0, projectDetail.getOnHoldTermCount());
	assertEquals(0, projectDetail.getPendingApprovalCount());
	assertEquals(0, projectDetail.getTermCount());
	assertEquals(0, projectDetail.getTermEntryCount());
	assertEquals(0, projectDetail.getTermInSubmissionCount());
	assertEquals(0, projectDetail.getLanguageCount());
    }

    private void validateProjectLanguageDetailAfterCleanUp(List<ProjectLanguageDetail> projectLanguageDetails) {
	for (ProjectLanguageDetail projectLanguageDetail : projectLanguageDetails) {

	    validateProjectLanguageUserDetails(projectLanguageDetail.getUserDetails());

	    assertEquals(0, projectLanguageDetail.getActiveSubmissionCount());
	    assertEquals(0, projectLanguageDetail.getApprovedTermCount());
	    assertEquals(0, projectLanguageDetail.getCompletedSubmissionCount());
	    assertEquals(0, projectLanguageDetail.getForbiddenTermCount());
	    assertEquals(0, projectLanguageDetail.getOnHoldTermCount());
	    assertEquals(0, projectLanguageDetail.getPendingApprovalCount());
	    assertEquals(0, projectLanguageDetail.getTermCount());
	    assertEquals(0, projectLanguageDetail.getTermEntryCount());
	    assertEquals(0, projectLanguageDetail.getTermInSubmissionCount());
	}
    }

    private void validateProjectLanguageUserDetails(Set<ProjectLanguageUserDetail> details) {
	assertTrue(isNotEmpty(details));
	for (ProjectLanguageUserDetail detail : details) {
	    assertEquals(0, detail.getActiveSubmissionCount());
	    assertEquals(0, detail.getCompletedSubmissionCount());
	}
    }

    private void validateProjectUserDetailAfterCleanUp(List<ProjectUserDetail> details) {
	for (ProjectUserDetail detail : details) {
	    assertEquals(0, detail.getActiveSubmissionCount());
	    assertEquals(0, detail.getCompletedSubmissionCount());
	    assertEquals(0, detail.getTermEntryCount());
	}
    }

    private void validateStatisticsAfterCleanUp(List<Statistics> statistics) {
	for (Statistics statistic : statistics) {
	    assertEquals(0, statistic.getAddedApproved());
	    assertEquals(0, statistic.getAddedBlacklisted());
	    assertEquals(0, statistic.getAddedOnHold());
	    assertEquals(0, statistic.getAddedPending());
	    assertEquals(0, statistic.getApproved());
	    assertEquals(0, statistic.getBlacklisted());
	    assertEquals(0, statistic.getDeleted());
	    assertEquals(0, statistic.getDemoted());
	    assertEquals(0, statistic.getOnHold());
	    assertEquals(0, statistic.getUpdated());
	}
    }
}
