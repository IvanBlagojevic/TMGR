package org.gs4tr.termmanager.service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.SortDirection;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.model.search.command.UserProjectSearchRequest;
import org.gs4tr.termmanager.model.view.ProjectDetailView;
import org.gs4tr.termmanager.model.view.ProjectReport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProjectDetailServiceTest extends AbstractSolrGlossaryTest {

    @Autowired
    private ProjectDetailService _projectDetailService;

    @Test
    public void testGetAllProjectsReport() {
	List<ProjectReport> reports = getProjectDetailService().getAllProjectsReport(false);
	Assert.assertTrue(CollectionUtils.isNotEmpty(reports));
	for (ProjectReport report : reports) {
	    Assert.assertNotNull(report.getProjectName());
	    Assert.assertNotNull(report.getMaxModifiedDate());
	    Assert.assertTrue(report.getLanguageCount() > 0);
	    Assert.assertTrue(report.getTermCount() > 0);
	}
    }

    @Test
    public void testGetAllProjectsReportGroupedByLanguage() {
	List<ProjectReport> reports = getProjectDetailService().getAllProjectsReport(true);
	Assert.assertTrue(CollectionUtils.isNotEmpty(reports));
	for (ProjectReport report : reports) {
	    Assert.assertNotNull(report.getProjectName());
	    Assert.assertNotNull(report.getMaxModifiedDate());
	    Assert.assertNotNull(report.getLanguageId());
	    Assert.assertTrue(report.getTermCount() > 0);
	}
    }

    /*
     * TERII-3208 Dashboard | Add Pending Approval and On Hold columns.
     * ProjectDetailView should contain On Hold and Pending Approval counts.
     */
    @Test
    public void testSearchProjectDetailOnHoldPendingApproval() {

	/* Create command for test */

	UserProjectSearchRequest command = new UserProjectSearchRequest();

	Set<String> languageIds = new HashSet<>();
	languageIds.add("en-US");

	Set<Long> projectIds = new HashSet<>();
	projectIds.add(1L);

	command.setLanguageIds(languageIds);

	command.setProjectIds(projectIds);

	command.setFolder(ItemFolderEnum.PROJECTDETAILS);

	TmUserProfile user = new TmUserProfile();

	UserInfo userInfo = new UserInfo();
	userInfo.setUserType(UserTypeEnum.POWER_USER);
	user.setUserInfo(userInfo);

	Map<Long, Set<String>> projectUserlang = new HashMap<>();
	projectUserlang.put(1L, new HashSet<>(Arrays.asList("en-US")));
	user.setProjectUserLanguages(projectUserlang);
	command.setUser(user);

	PagedListInfo pagedListInfo = new PagedListInfo();
	pagedListInfo.setSize(200);
	pagedListInfo.setSortDirection(SortDirection.ASCENDING);
	pagedListInfo.setSortProperty("dateModified");

	/* Perform search */

	TaskPagedList<ProjectDetailView> pagedList = getProjectDetailService().search(command, pagedListInfo);
	Assert.assertNotNull(pagedList);

	ProjectDetailView[] projectDetailView = pagedList.getElements();

	Assert.assertNotNull(projectDetailView);

	Assert.assertEquals(1, projectDetailView.length);

	Assert.assertNotNull(projectDetailView[0].getOnHoldTermCount());

	Assert.assertEquals(1, projectDetailView[0].getOnHoldTermCount());

	Assert.assertEquals(2, projectDetailView[0].getPendingApprovalTermCount());
    }

    @Test
    public void testUpdateProjectDetail_case_1() {
	Map<String, Long> languagesDates = getLanguageDates();

	getProjectDetailService().updateProjectDetailOnImport(PROJECT_ID, languagesDates, new Date().getTime());

	ProjectDetail projectDetail = getProjectDetailService().findByProjectId(PROJECT_ID);
	Assert.assertNotNull(projectDetail);
	Assert.assertTrue(projectDetail.getTermCount() > 0);
	Assert.assertTrue(projectDetail.getLanguageCount() > 0);
	Assert.assertTrue(projectDetail.getTermInSubmissionCount() > 0);
    }

    /* TERII-4720 */
    @Test
    public void testUpdateProjectDetail_case_2() {
	Map<String, Long> languagesDates = getLanguageDates();

	getProjectDetailService().updateProjectDetailOnImport(PROJECT_ID, languagesDates, new Date().getTime());

	ProjectDetail projectDetail = getProjectDetailService().findByProjectId(PROJECT_ID);
	long termCount = projectDetail.getTermCount();
	long languageCount = projectDetail.getLanguageCount();
	long termInSubmissionCount = projectDetail.getTermInSubmissionCount();

	Assert.assertNotNull(projectDetail);
	Assert.assertTrue(termCount > 0);
	Assert.assertTrue(languageCount > 0);
	Assert.assertTrue(termInSubmissionCount > 0);

	getProjectDetailService().updateProjectDetailOnImport(PROJECT_ID, languagesDates, new Date().getTime());

	projectDetail = getProjectDetailService().findByProjectId(PROJECT_ID);

	Assert.assertEquals(termCount, projectDetail.getTermCount());
	Assert.assertEquals(languageCount, projectDetail.getLanguageCount());
	Assert.assertEquals(termInSubmissionCount, projectDetail.getTermInSubmissionCount());
    }

    private Map<String, Long> getLanguageDates() {
	Map<String, Long> languageDates = new HashMap<>();
	Long date = System.currentTimeMillis();
	languageDates.put("en-US", date);
	languageDates.put("sr-RS", date);
	languageDates.put("fr-FR", date);
	languageDates.put("de-DE", date);
	return languageDates;
    }

    private ProjectDetailService getProjectDetailService() {
	return _projectDetailService;
    }
}
