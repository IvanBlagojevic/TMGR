package org.gs4tr.termmanager.dao.hibernate;

import static java.util.Arrays.*;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.dao.ProjectDAO;
import org.gs4tr.termmanager.dao.ProjectDetailDAO;
import org.gs4tr.termmanager.dao.ProjectLanguageDetailDAO;
import org.gs4tr.termmanager.dao.ProjectLanguageUserDetailDAO;
import org.gs4tr.termmanager.dao.ProjectUserDetailDAO;
import org.gs4tr.termmanager.dao.UserProfileDAO;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.ProjectLanguageUserDetail;
import org.gs4tr.termmanager.model.ProjectUserDetail;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.ProjectDetailCountsIO;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.search.command.UserProjectSearchRequest;
import org.gs4tr.termmanager.model.view.ProjectReport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

public class ProjectDetailDAOTest extends AbstractSpringDAOIntegrationTest {

    @Autowired
    private ProjectDetailDAO _projectDetailDAO;

    @Autowired
    private ProjectLanguageDetailDAO _projectLanguageDetailDAO;

    @Autowired
    private ProjectLanguageUserDetailDAO _projectLanguageUserDetailDAO;

    @Autowired
    private ProjectUserDetailDAO _projectUserDetailDAO;

    @Test
    public void findProjectUserDetailsByProjectNameTest() {
	UserProjectSearchRequest request = new UserProjectSearchRequest();
	request.setName("project");
	request.setProjectIds(new HashSet<>(asList(1L, 2L)));
	request.setLanguageIds(new HashSet<>(asList("fr-FR", "de-DE", "en-UK")));

	List<ProjectDetail> details = getProjectDetailDAO().searchProjectDetails(request, new PagedListInfo());
	assertTrue(isNotEmpty(details));
	assertEquals(2, details.size());

	for (ProjectDetail projectDetail : details) {
	    String projectName = projectDetail.getProject().getProjectInfo().getName().toLowerCase();
	    assertTrue(projectName.contains(request.getName().toLowerCase()));
	}
    }

    @Test
    public void findProjectUserDetailsByProjectNameTest1() {
	UserProjectSearchRequest request = new UserProjectSearchRequest();
	request.setName("pRoJect");
	request.setProjectIds(new HashSet<>(asList(1L, 2L)));
	request.setLanguageIds(new HashSet<>(asList("fr-FR", "de-DE", "en-UK")));

	List<ProjectDetail> details = getProjectDetailDAO().searchProjectDetails(request, new PagedListInfo());
	assertTrue(isNotEmpty(details));
	assertEquals(2, details.size());

	for (ProjectDetail projectDetail : details) {
	    String projectName = projectDetail.getProject().getProjectInfo().getName().toLowerCase();
	    assertTrue(projectName.contains(request.getName().toLowerCase()));
	}
    }

    @Test
    public void findProjectUserDetailsByProjectShortcodeTest() {
	UserProjectSearchRequest request = new UserProjectSearchRequest();
	request.setShortCode("TEST");
	request.setProjectIds(new HashSet<>(asList(1L, 2L)));
	request.setLanguageIds(new HashSet<>(asList("fr-FR", "de-DE", "en-UK")));

	List<ProjectDetail> details = getProjectDetailDAO().searchProjectDetails(request, new PagedListInfo());
	assertTrue(isNotEmpty(details));
	assertEquals(2, details.size());

	for (ProjectDetail projectDetail : details) {
	    String projectShortcode = projectDetail.getProject().getProjectInfo().getShortCode().toLowerCase();
	    assertTrue(projectShortcode.contains(request.getShortCode().toLowerCase()));
	}
    }

    @Test
    public void findProjectUserDetailsByProjectShortcodeTest1() {
	UserProjectSearchRequest request = new UserProjectSearchRequest();
	request.setShortCode("TeSt");
	request.setProjectIds(new HashSet<>(asList(1L, 2L)));
	request.setLanguageIds(new HashSet<>(asList("fr-FR", "de-DE", "en-UK")));

	List<ProjectDetail> details = getProjectDetailDAO().searchProjectDetails(request, new PagedListInfo());
	assertTrue(isNotEmpty(details));
	assertEquals(2, details.size());

	for (ProjectDetail projectDetail : details) {
	    String projectShortcode = projectDetail.getProject().getProjectInfo().getShortCode().toLowerCase();
	    assertTrue(projectShortcode.contains(request.getShortCode().toLowerCase()));
	}
    }

    @Test
    public void incrementalUpdateProjectDetailWithProjectDetailCountsTest() {

	ProjectDetailDAO projectDetailDAO = getProjectDetailDAO();

	long projectId = 1L;

	ProjectDetail projectDetailBeforeUpdate = projectDetailDAO.findByProjectId(projectId);
	long termEntryCountBeforeUpdate = projectDetailBeforeUpdate.getTermEntryCount();
	long approvedTermCountBeforeUpdate = projectDetailBeforeUpdate.getApprovedTermCount();
	long forbiddenTermBeforeUpdate = projectDetailBeforeUpdate.getForbiddenTermCount();
	long termCountBeforeUpdate = projectDetailBeforeUpdate.getTermCount();
	long activeSubmissionCountBeforeUpdate = projectDetailBeforeUpdate.getActiveSubmissionCount();
	long completedSubmissionCountBeforeUpdate = projectDetailBeforeUpdate.getCompletedSubmissionCount();
	long onHoldCountBeforeUpdate = projectDetailBeforeUpdate.getOnHoldTermCount();
	long pendingApprovalCountBeforeUpdate = projectDetailBeforeUpdate.getPendingApprovalCount();
	long termInSubmissionCountBeforeUpdate = projectDetailBeforeUpdate.getTermInSubmissionCount();

	ProjectDetailCountsIO countsIO = createProjectDetailCounts(projectId);
	long termEntryCount = countsIO.getTermEntryCount();
	long approvedTermCount = countsIO.getApprovedTermCount();
	long forbiddenTermCount = countsIO.getForbiddenTermCount();
	long termCount = countsIO.getTotalCount();
	long activeSubmissionCount = countsIO.getActiveSubmissionCount();
	long completedSubmissionCount = countsIO.getCompletedSubmissionCount();
	long onHoldCount = countsIO.getOnHoldTermCount();
	long pendingApprovalCount = countsIO.getPendingTermCount();
	long termInSubmissionCount = countsIO.getTermInSubmissionCount();

	projectDetailDAO.incrementalUpdateProjectDetail(countsIO);

	projectDetailDAO.flush();
	projectDetailDAO.clear();

	ProjectDetail projectDetailAfterUpdate = projectDetailDAO.findByProjectId(projectId);

	long termEntryCountAfterUpdate = projectDetailAfterUpdate.getTermEntryCount();
	long approvedTermCountAfterUpdate = projectDetailAfterUpdate.getApprovedTermCount();
	long forbiddenTermAfterUpdate = projectDetailAfterUpdate.getForbiddenTermCount();
	long termCountAfterUpdate = projectDetailAfterUpdate.getTermCount();
	long activeSubmissionCountAfterUpdate = projectDetailAfterUpdate.getActiveSubmissionCount();
	long completedSubmissionCountAfterUpdate = projectDetailAfterUpdate.getCompletedSubmissionCount();
	long onHoldCountAfterUpdate = projectDetailAfterUpdate.getOnHoldTermCount();
	long pendingApprovalCountAfterUpdate = projectDetailAfterUpdate.getPendingApprovalCount();
	long termInSubmissionCountAfterUpdate = projectDetailAfterUpdate.getTermInSubmissionCount();

	assertEquals(termEntryCountBeforeUpdate + termEntryCount, termEntryCountAfterUpdate);
	assertEquals(approvedTermCountBeforeUpdate + approvedTermCount, approvedTermCountAfterUpdate);
	assertEquals(forbiddenTermBeforeUpdate + forbiddenTermCount, forbiddenTermAfterUpdate);
	assertEquals(termCountBeforeUpdate + termCount, termCountAfterUpdate);
	assertEquals(activeSubmissionCountBeforeUpdate + activeSubmissionCount, activeSubmissionCountAfterUpdate);
	assertEquals(completedSubmissionCountBeforeUpdate + completedSubmissionCount,
		completedSubmissionCountAfterUpdate);
	assertEquals(onHoldCountBeforeUpdate + onHoldCount, onHoldCountAfterUpdate);
	assertEquals(pendingApprovalCountBeforeUpdate + pendingApprovalCount, pendingApprovalCountAfterUpdate);
	assertEquals(termInSubmissionCountBeforeUpdate + termInSubmissionCount, termInSubmissionCountAfterUpdate);

    }

    @Test
    public void testGetAllProjectsReportCase1() {
	boolean groupByLanguages = false;
	boolean isPowerUser = true;
	Set<String> languageIds = new HashSet<>();
	languageIds.add("en-US");
	languageIds.add("de-DE");
	languageIds.add("fr-FR");

	List<ProjectReport> reports = getProjectDetailDAO().getAllProjectsReport(groupByLanguages, isPowerUser, null,
		languageIds);
	Assert.assertTrue(isNotEmpty(reports));
	for (ProjectReport report : reports) {
	    Assert.assertNotNull(report.getProjectName());
	    Assert.assertNotNull(report.getMaxModifiedDate());
	    Assert.assertTrue(report.getTermCount() > 0);
	}
    }

    @Test
    public void testGetAllProjectsReportCase2() {
	boolean groupByLanguages = true;
	boolean isPowerUser = true;

	List<ProjectReport> reports = getProjectDetailDAO().getAllProjectsReport(groupByLanguages, isPowerUser, null,
		null);
	Assert.assertTrue(isNotEmpty(reports));
	for (ProjectReport report : reports) {
	    Assert.assertNotNull(report.getProjectName());
	    Assert.assertNotNull(report.getLanguageId());
	    Assert.assertNotNull(report.getMaxModifiedDate());
	    Assert.assertTrue(report.getTermCount() > 0);
	}
    }

    @Test
    public void testGetAllProjectsReportCase3() {
	List<Long> projectIds = getProjectDAO().findAllEnabledProjectIds();
	boolean groupByLanguages = true;
	boolean isPowerUser = true;
	List<ProjectReport> reports = getProjectDetailDAO().getAllProjectsReport(groupByLanguages, isPowerUser,
		new HashSet<>(projectIds), null);
	Assert.assertTrue(isNotEmpty(reports));
	for (ProjectReport report : reports) {
	    Assert.assertNotNull(report.getProjectName());
	    Assert.assertNotNull(report.getLanguageId());
	    Assert.assertNotNull(report.getMaxModifiedDate());
	    Assert.assertTrue(report.getTermCount() > 0);
	}
    }

    @Test
    public void testProjectDetailOnHoldPendingApprovalSave() throws Exception {
	Long projectId = 4L;

	TmProject project = getProjectDAO().load(projectId);

	ProjectDetail detail = new ProjectDetail(project);
	detail = getProjectDetailDAO().save(detail);

	String languageId = "en_US";
	ProjectLanguageDetail langDetail = new ProjectLanguageDetail(languageId, detail);
	langDetail.setOnHoldTermCount(10);
	langDetail.setPendingApprovalCount(15);
	langDetail = getProjectLanguageDetailDAO().save(langDetail);

	detail = getProjectDetailDAO().findByProjectId(projectId);

	Assert.assertNotNull(detail);

	langDetail = getProjectLanguageDetailDAO().findById(langDetail.getProjectLanguageDetailId());

	Assert.assertNotNull(langDetail);

	Assert.assertEquals(10, langDetail.getOnHoldTermCount());
	Assert.assertEquals(15, langDetail.getPendingApprovalCount());
    }

    @Test
    public void testProjectDetailOnHoldPendingApprovalSaveWriteInProjectDetail() throws Exception {
	Long projectId = 4L;

	TmProject project = getProjectDAO().load(projectId);

	ProjectDetail detail = new ProjectDetail(project);

	String languageId = "en_US";
	ProjectLanguageDetail langDetail = new ProjectLanguageDetail(languageId, detail);

	langDetail.setPendingApprovalCount(8);
	langDetail.setOnHoldTermCount(10);

	detail.setPendingApprovalCount(12);
	detail.setOnHoldTermCount(18);

	detail.setLanguageDetails(new HashSet<>());
	detail.getLanguageDetails().add(langDetail);

	getProjectDetailDAO().save(detail);

	detail = getProjectDetailDAO().findByProjectId(projectId);

	Assert.assertNotNull(detail);

	// Get language detail by projectLanguageDetailId
	langDetail = getProjectLanguageDetailDAO().findById(langDetail.getProjectLanguageDetailId());

	Assert.assertNotNull(langDetail);

	// assert On Hold and Pending Approval count
	Assert.assertEquals(10, langDetail.getOnHoldTermCount());
	Assert.assertEquals(8, langDetail.getPendingApprovalCount());

	detail = getProjectDetailDAO().findByProjectId(projectId, ProjectLanguageDetail.class);

	Assert.assertEquals(1, detail.getLanguageDetails().size());

	Assert.assertEquals(12, detail.getPendingApprovalCount());
	Assert.assertEquals(18, detail.getOnHoldTermCount());

	Iterator<ProjectLanguageDetail> langDeIterator = detail.getLanguageDetails().iterator();
	ProjectLanguageDetail projectLanguageDetail = langDeIterator.next();

	// assert On Hold and Pending Approval count from projectDetail
	Assert.assertEquals(10, projectLanguageDetail.getOnHoldTermCount());
	Assert.assertEquals(8, projectLanguageDetail.getPendingApprovalCount());
    }

    @Test
    public void testProjectDetailSave() throws Exception {
	Long projectId = 4L;

	TmProject project = getProjectDAO().load(projectId);

	ProjectDetail detail = new ProjectDetail(project);
	detail = getProjectDetailDAO().save(detail);

	String languageId = "en_US";
	ProjectLanguageDetail langDetail = new ProjectLanguageDetail(languageId, detail);

	langDetail = getProjectLanguageDetailDAO().save(langDetail);

	detail = getProjectDetailDAO().findByProjectId(projectId);

	Assert.assertNotNull(detail);

	langDetail = getProjectLanguageDetailDAO().findById(langDetail.getProjectLanguageDetailId());

	Assert.assertNotNull(langDetail);
    }

    @Test
    public void testSearchProjectDetails() throws Exception {
	ProjectDAO projectDAO = getProjectDAO();
	UserProfileDAO userProfileDAO = getUserProfileDAO();
	ProjectDetailDAO projectDetailDAO = getProjectDetailDAO();
	ProjectLanguageDetailDAO projectLanguageDetailDAO = getProjectLanguageDetailDAO();

	Long projectId = 2L;

	String enLanguageId = "en-US";
	String deLanguageId = "de-DE";

	ProjectDetailInfo info = new ProjectDetailInfo(projectId);

	TmProject project = projectDAO.load(projectId);
	TmUserProfile user = userProfileDAO.load(1L);

	ProjectDetail projectDetail = projectDetailDAO.findByProjectId(projectId);
	Assert.assertNotNull(projectDetail);

	ProjectUserDetail userDetail = new ProjectUserDetail(user, projectDetail);
	projectDetail.getUserDetails().add(userDetail);

	ProjectLanguageDetail enLanguageDetail = new ProjectLanguageDetail(enLanguageId, projectDetail);
	projectDetail.getLanguageDetails().add(enLanguageDetail);

	ProjectLanguageDetail deLanguageDetail = new ProjectLanguageDetail(deLanguageId, projectDetail);
	projectDetail.getLanguageDetails().add(deLanguageDetail);

	projectDetailDAO.saveOrUpdate(projectDetail);

	projectDetailDAO.flush();
	projectDetailDAO.clear();

	info.incrementTermCount(enLanguageId);

	projectDetailDAO.incrementalUpdateProjectDetail(info);
	projectLanguageDetailDAO.incrementalUpdateProjectLanguageDetail(enLanguageId, info, new Date());
	projectLanguageDetailDAO.incrementalUpdateProjectLanguageDetail(deLanguageId, info, new Date());

	projectDetailDAO.flush();
	projectDetailDAO.clear();

	projectLanguageDetailDAO.flush();
	projectLanguageDetailDAO.clear();

	Set<Long> projectIds = new HashSet<>();
	projectIds.add(projectId);

	Set<String> languageIds = new HashSet<>();
	languageIds.add(enLanguageId);

	UserProjectSearchRequest command = new UserProjectSearchRequest();
	command.setProjectIds(projectIds);
	command.setName(project.getProjectInfo().getName());
	command.setLanguageIds(languageIds);
	command.setUser(user);
	command.setShortCode(project.getProjectInfo().getShortCode());

	PagedListInfo pagedListInfo = new PagedListInfo();
	List<ProjectDetail> projectDetails = projectDetailDAO.searchProjectDetails(command, pagedListInfo);

	assertNotNull(projectDetails);

	assertEquals(1, projectDetails.size());

	Iterator<ProjectDetail> iterator = projectDetails.iterator();
	projectDetail = iterator.next();
	assertEquals(2, projectDetail.getTermCount());

	Set<ProjectUserDetail> userDetails = projectDetail.getUserDetails();
	assertEquals(1, userDetails.size());
    }

    @Test
    public void testSearchProjectDetailsOnHoldPendingApproval() {
	Long projectId = 1L;

	TmUserProfile user = getUserProfileDAO().load(1L);

	String enLanguageId = "en-US";

	String deLanguageId = "de-DE";

	TmProject project = getProjectDAO().load(projectId);

	ProjectDetail detail = new ProjectDetail(project);
	detail = getProjectDetailDAO().save(detail);

	Assert.assertNotNull(detail);
	Assert.assertNotNull(detail.getProjectDetailId());

	ProjectDetailInfo info = new ProjectDetailInfo(projectId);

	ProjectUserDetail userDetail = new ProjectUserDetail(user, detail);
	userDetail = getProjectUserDetailDAO().save(userDetail);
	Assert.assertNotNull(userDetail);
	Assert.assertNotNull(userDetail.getProjectUserDetailId());

	Set<ProjectUserDetail> userDetails = new HashSet<>();
	userDetails.add(userDetail);

	ProjectLanguageDetail enLanguageDetail = new ProjectLanguageDetail(enLanguageId, detail);
	enLanguageDetail = getProjectLanguageDetailDAO().save(enLanguageDetail);

	ProjectLanguageUserDetail enLanguageUserDetail = new ProjectLanguageUserDetail(enLanguageDetail, user);
	enLanguageUserDetail = getProjectLanguageUserDetailDAO().save(enLanguageUserDetail);

	Set<ProjectLanguageUserDetail> enLanguageUserDetails = new HashSet<>();
	enLanguageUserDetails.add(enLanguageUserDetail);
	enLanguageDetail.setUserDetails(enLanguageUserDetails);

	ProjectLanguageDetail deLanguageDetail = new ProjectLanguageDetail(deLanguageId, detail);
	deLanguageDetail = getProjectLanguageDetailDAO().save(deLanguageDetail);

	ProjectLanguageUserDetail deLanguageUserDetail = new ProjectLanguageUserDetail(deLanguageDetail, user);
	deLanguageUserDetail = getProjectLanguageUserDetailDAO().save(deLanguageUserDetail);

	Set<ProjectLanguageUserDetail> deLanguageUserDetails = new HashSet<>();
	deLanguageUserDetails.add(deLanguageUserDetail);
	deLanguageDetail.setUserDetails(deLanguageUserDetails);

	Set<ProjectLanguageDetail> languageDetails = new HashSet<>();
	languageDetails.add(enLanguageDetail);
	languageDetails.add(deLanguageDetail);
	detail.setLanguageDetails(languageDetails);
	detail.setUserDetails(userDetails);

	info.incrementTermCount(enLanguageId);
	getProjectDetailDAO().updateProjectDetail(info);

	Set<Long> projectIds = new HashSet<>();
	projectIds.add(projectId);

	Set<String> languageIds = new HashSet<>();
	languageIds.add(enLanguageId);

	UserProjectSearchRequest command = new UserProjectSearchRequest();
	command.setProjectIds(projectIds);
	command.setName(project.getProjectInfo().getName());
	command.setLanguageIds(languageIds);
	command.setUser(user);
	command.setShortCode(project.getProjectInfo().getShortCode());

	PagedListInfo pagedListInfo = new PagedListInfo();
	List<ProjectDetail> projectDetails = getProjectDetailDAO().searchProjectDetails(command, pagedListInfo);

	assertNotNull(projectDetails);

	assertEquals(2, projectDetails.size());

	ProjectDetail projectDetail = projectDetails.get(0);

	assertEquals(4, projectDetail.getOnHoldTermCount());
	assertEquals(7, projectDetail.getPendingApprovalCount());
    }

    @Test
    public void testUpdateDateModifiedByProjectId() {
	Date currentDateModified = (Date) getProjectDetailDAO().getPropertyValue(1L, "dateModified");
	assertNotNull(currentDateModified);

	final Date newDateModified = new Date();

	getProjectDetailDAO().updateDateModifiedByProjectId(1L, newDateModified);

	ProjectDetail updatedDetail = getProjectDetailDAO().load(1L);

	assertFalse(currentDateModified.equals(newDateModified));
	assertEquals(newDateModified, updatedDetail.getDateModified());
    }

    @Test
    public void testUpdateProjectAndLanguagesDateModifiedByProjectId() {
	final Set<String> languages = Sets.newHashSet(Locale.US.getCode(), Locale.GERMANY.getCode());
	final Date newDateModified = new Date();

	getProjectDetailDAO().updateProjectAndLanguagesDateModifiedByProjectId(1L, languages, newDateModified);
	getProjectDetailDAO().flush();
	getProjectDetailDAO().clear();

	ProjectDetail updatedDetail = getProjectDetailDAO().load(1L);
	assertEquals(newDateModified, updatedDetail.getDateModified());

	Set<ProjectLanguageDetail> languageDetails = updatedDetail.getLanguageDetails();
	assertTrue(languageDetails.stream().filter(l -> languages.contains(l.getLanguageId()))
		.map(ProjectLanguageDetail::getDateModified).allMatch(Predicate.isEqual(newDateModified)));
    }

    @Test
    public void testUpdateProjectDetail_basic_case() {
	ProjectDetailDAO dao = getProjectDetailDAO();

	long projectId = 1L;

	ProjectDetail pd = dao.findByProjectId(projectId);

	long termEntryCount = pd.getTermEntryCount();
	long termCount = pd.getTermCount();
	long approvedTermCount = pd.getApprovedTermCount();
	long forbiddenTermCount = pd.getForbiddenTermCount();
	long pendingApprovalCount = pd.getPendingApprovalCount();
	long onHoldTermCount = pd.getOnHoldTermCount();

	assertTrue(termEntryCount >= 0);
	assertTrue(termCount >= 0);
	assertTrue(approvedTermCount >= 0);
	assertTrue(forbiddenTermCount >= 0);
	assertTrue(pendingApprovalCount >= 0);
	assertTrue(onHoldTermCount >= 0);

	ProjectDetailInfo info = new ProjectDetailInfo(projectId);
	info.getTermEntryCount().addAndGet(10);
	info.addApprovedTermCount("en-US", 10);
	info.addForbiddenTermCount("en-US", 10);
	info.addPendingTermCount("en-US", 10);
	info.addOnHoldTermCount("en-US", 10);

	dao.incrementalUpdateProjectDetail(info);

	dao.flush();
	dao.clear();

	pd = dao.findByProjectId(projectId);

	long termEntryCountUpdated = pd.getTermEntryCount();
	long termCountUpdated = pd.getTermCount();
	long approvedTermCountUpdated = pd.getApprovedTermCount();
	long forbiddenTermCountUpdated = pd.getForbiddenTermCount();
	long pendingApprovalCountUpdated = pd.getPendingApprovalCount();
	long onHoldTermCountUpdated = pd.getOnHoldTermCount();

	assertEquals(termEntryCount + 10, termEntryCountUpdated);
	assertEquals(termCount + 40, termCountUpdated);
	assertEquals(approvedTermCount + 10, approvedTermCountUpdated);
	assertEquals(forbiddenTermCount + 10, forbiddenTermCountUpdated);
	assertEquals(pendingApprovalCount + 10, pendingApprovalCountUpdated);
	assertEquals(onHoldTermCount + 10, onHoldTermCountUpdated);
    }

    @Test
    public void testUpdateProjectDetail_case_1() {
	ProjectDetailDAO dao = getProjectDetailDAO();

	long projectId = 1L;

	ProjectDetail pd = dao.findByProjectId(projectId);

	long termEntryCount = pd.getTermEntryCount();
	long termCount = pd.getTermCount();
	long approvedTermCount = pd.getApprovedTermCount();
	long forbiddenTermCount = pd.getForbiddenTermCount();
	long pendingApprovalCount = pd.getPendingApprovalCount();
	long onHoldTermCount = pd.getOnHoldTermCount();

	ProjectDetailInfo info = new ProjectDetailInfo(projectId);
	info.getTermEntryCount().addAndGet(10);
	info.addApprovedTermCount("en-US", 10);
	info.addForbiddenTermCount("en-US", 10);
	info.addPendingTermCount("en-US", -10);
	info.addOnHoldTermCount("en-US", 10);

	dao.incrementalUpdateProjectDetail(info);

	dao.flush();
	dao.clear();

	pd = dao.findByProjectId(projectId);

	long termEntryCountUpdated = pd.getTermEntryCount();
	long termCountUpdated = pd.getTermCount();
	long approvedTermCountUpdated = pd.getApprovedTermCount();
	long forbiddenTermCountUpdated = pd.getForbiddenTermCount();
	long pendingApprovalCountUpdated = pd.getPendingApprovalCount();
	long onHoldTermCountUpdated = pd.getOnHoldTermCount();

	assertEquals(termEntryCount + 10, termEntryCountUpdated);
	assertEquals(termCount + 20, termCountUpdated);
	assertEquals(approvedTermCount + 10, approvedTermCountUpdated);
	assertEquals(forbiddenTermCount + 10, forbiddenTermCountUpdated);
	assertEquals(pendingApprovalCount - 10, pendingApprovalCountUpdated);
	assertEquals(onHoldTermCount + 10, onHoldTermCountUpdated);
    }

    @Test
    public void updateProjectDetailWithProjectDetailCountTest() {

	ProjectDetailDAO projectDetailDAO = getProjectDetailDAO();

	Long projectId = 1L;

	ProjectDetail projectDetailBeforeUpdate = projectDetailDAO.findByProjectId(projectId);
	assertNotNull(projectDetailBeforeUpdate);

	ProjectDetailCountsIO countsIO = createProjectDetailCounts(projectId);
	long termEntryCount = countsIO.getTermEntryCount();
	long approvedTermCount = countsIO.getApprovedTermCount();
	long forbiddenTermCount = countsIO.getForbiddenTermCount();
	long termCount = countsIO.getTotalCount();
	long activeSubmissionCount = countsIO.getActiveSubmissionCount();
	long completedSubmissionCount = countsIO.getCompletedSubmissionCount();
	long onHoldCount = countsIO.getOnHoldTermCount();
	long pendingApprovalCount = countsIO.getPendingTermCount();
	long termInSubmissionCount = countsIO.getTermInSubmissionCount();

	projectDetailDAO.updateProjectDetail(countsIO);

	projectDetailDAO.flush();
	projectDetailDAO.clear();

	ProjectDetail projectDetailAfterUpdate = projectDetailDAO.findByProjectId(projectId);
	assertEquals(termEntryCount, projectDetailAfterUpdate.getTermEntryCount());
	assertEquals(approvedTermCount, projectDetailAfterUpdate.getApprovedTermCount());
	assertEquals(forbiddenTermCount, projectDetailAfterUpdate.getForbiddenTermCount());
	assertEquals(termCount, projectDetailAfterUpdate.getTermCount());
	assertEquals(activeSubmissionCount, projectDetailAfterUpdate.getActiveSubmissionCount());
	assertEquals(completedSubmissionCount, projectDetailAfterUpdate.getCompletedSubmissionCount());
	assertEquals(onHoldCount, projectDetailAfterUpdate.getOnHoldTermCount());
	assertEquals(pendingApprovalCount, projectDetailAfterUpdate.getPendingApprovalCount());
	assertEquals(termInSubmissionCount, projectDetailAfterUpdate.getTermInSubmissionCount());

    }

    @Test
    public void updateProjectDetailWithProjectDetailCountWithoutSubmissionTest() {

	ProjectDetailDAO projectDetailDAO = getProjectDetailDAO();

	Long projectId = 1L;

	ProjectDetail projectDetailBeforeUpdate = projectDetailDAO.findByProjectId(projectId);
	assertNotNull(projectDetailBeforeUpdate);

	ProjectDetailCountsIO countsIO = createProjectDetailCountsWithoutSubmission(projectId);
	long termEntryCount = countsIO.getTermEntryCount();
	long approvedTermCount = countsIO.getApprovedTermCount();
	long forbiddenTermCount = countsIO.getForbiddenTermCount();
	long termCount = countsIO.getTotalCount();
	long onHoldCount = countsIO.getOnHoldTermCount();
	long pendingApprovalCount = countsIO.getPendingTermCount();
	long termInSubmissionCount = countsIO.getTermInSubmissionCount();

	projectDetailDAO.updateProjectDetail(countsIO);

	projectDetailDAO.flush();
	projectDetailDAO.clear();

	ProjectDetail projectDetailAfterUpdate = projectDetailDAO.findByProjectId(projectId);
	assertEquals(termEntryCount, projectDetailAfterUpdate.getTermEntryCount());
	assertEquals(approvedTermCount, projectDetailAfterUpdate.getApprovedTermCount());
	assertEquals(forbiddenTermCount, projectDetailAfterUpdate.getForbiddenTermCount());
	assertEquals(termCount, projectDetailAfterUpdate.getTermCount());
	assertEquals(projectDetailBeforeUpdate.getActiveSubmissionCount(),
		projectDetailAfterUpdate.getActiveSubmissionCount());
	assertEquals(projectDetailBeforeUpdate.getCompletedSubmissionCount(),
		projectDetailAfterUpdate.getCompletedSubmissionCount());
	assertEquals(onHoldCount, projectDetailAfterUpdate.getOnHoldTermCount());
	assertEquals(pendingApprovalCount, projectDetailAfterUpdate.getPendingApprovalCount());
	assertEquals(termInSubmissionCount, projectDetailAfterUpdate.getTermInSubmissionCount());

    }

    private ProjectDetailCountsIO createProjectDetailCounts(long projectId) {
	ProjectDetailCountsIO countsIO = new ProjectDetailCountsIO();
	countsIO.setProjectId(projectId);
	countsIO.setTermEntryCount(5L);
	countsIO.setApprovedTermCount(5L);
	countsIO.setForbiddenTermCount(5L);
	countsIO.setTotalCount(5L);
	countsIO.setActiveSubmissionCount(5L);
	countsIO.setCompletedSubmissionCount(5L);
	countsIO.setOnHoldTermCount(5L);
	countsIO.setPendingTermCount(5L);
	countsIO.setTermInSubmissionCount(5L);
	return countsIO;
    }

    private ProjectDetailCountsIO createProjectDetailCountsWithoutSubmission(long projectId) {
	ProjectDetailCountsIO countsIO = new ProjectDetailCountsIO();
	countsIO.setProjectId(projectId);
	countsIO.setTermEntryCount(5L);
	countsIO.setApprovedTermCount(5L);
	countsIO.setForbiddenTermCount(5L);
	countsIO.setTotalCount(5L);
	countsIO.setOnHoldTermCount(5L);
	countsIO.setPendingTermCount(5L);
	countsIO.setTermInSubmissionCount(5L);
	return countsIO;
    }

    private ProjectDetailDAO getProjectDetailDAO() {
	return _projectDetailDAO;
    }

    private ProjectLanguageDetailDAO getProjectLanguageDetailDAO() {
	return _projectLanguageDetailDAO;
    }

    private ProjectLanguageUserDetailDAO getProjectLanguageUserDetailDAO() {
	return _projectLanguageUserDetailDAO;
    }

    private ProjectUserDetailDAO getProjectUserDetailDAO() {
	return _projectUserDetailDAO;
    }
}
