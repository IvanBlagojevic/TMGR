package org.gs4tr.termmanager.dao.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.dao.ProjectDetailDAO;
import org.gs4tr.termmanager.dao.ProjectLanguageDetailDAO;
import org.gs4tr.termmanager.dao.ProjectLanguageUserDetailDAO;
import org.gs4tr.termmanager.dao.ProjectUserDetailDAO;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetailInfoIO;
import org.gs4tr.termmanager.model.ProjectLanguageUserDetail;
import org.gs4tr.termmanager.model.ProjectUserDetail;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.search.command.ProjectLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.ProjectLanguageDetailView;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProjectLanguageDetailDAOTest extends AbstractSpringDAOIntegrationTest {

    @Autowired
    private ProjectDetailDAO _projectDetailDAO;

    @Autowired
    private ProjectLanguageDetailDAO _projectLanguageDetailDAO;

    @Autowired
    private ProjectLanguageUserDetailDAO _projectLanguageUserDetailDAO;

    @Autowired
    private ProjectUserDetailDAO _projectUserDetailDAO;

    @Test
    public void findProjectLanguageDetailByProjectAndLanguageIdTest() {
	ProjectLanguageDetailDAO dao = getProjectLanguageDetailDAO();
	String languageId = "en-US";

	List<ProjectLanguageDetail> projectLanguageDetailsByProjectId = dao.getProjectLanguageDetailsByProjectId(1L);
	assertTrue(CollectionUtils.isNotEmpty(projectLanguageDetailsByProjectId));

	ProjectLanguageDetail pldByProjectId = projectLanguageDetailsByProjectId.stream()
		.filter(pul -> pul.getLanguageId().equals(languageId)).findFirst().orElse(null);
	Assert.assertNotNull(pldByProjectId);

	ProjectLanguageDetail pldByLanguageId = dao.findProjectLangDetailByLangId(1L, languageId);
	Assert.assertNotNull(pldByLanguageId);
	Assert.assertEquals(pldByProjectId, pldByLanguageId);
    }

    @Test
    public void incrementalUpdateProjectLanguageDetailWithProjectLanguageDetailInfoIOTest() {

	long projectId = 1L;
	String languageId = "en-US";

	ProjectLanguageDetailDAO projectLanguageDetailDAO = getProjectLanguageDetailDAO();

	ProjectLanguageDetail beforeUpdate = projectLanguageDetailDAO.findById(projectId);
	long approvedTermCountBeforeUpdate = beforeUpdate.getApprovedTermCount();
	long termEntryCountBeforeUpdate = beforeUpdate.getTermEntryCount();
	long forbiddenTermCountBeforeUpdate = beforeUpdate.getForbiddenTermCount();
	long activeSubmissionCountBeforeUpdate = beforeUpdate.getActiveSubmissionCount();
	long onHoldTermCountBeforeUpdate = beforeUpdate.getOnHoldTermCount();
	long pendingApprovalCountBeforeUpdate = beforeUpdate.getPendingApprovalCount();
	long completedSubmissionCountBeforeUpdate = beforeUpdate.getCompletedSubmissionCount();
	long termCountBeforeUpdate = beforeUpdate.getTermCount();
	long termInSubmissionCountBeforeUpdate = beforeUpdate.getTermInSubmissionCount();

	ProjectLanguageDetailInfoIO infoIO = createProjectLanguageDetail(projectId, languageId);
	long approvedTermCount = infoIO.getApprovedTermCount();
	long termEntryCount = infoIO.getTermEntryCount();
	long forbiddenTermCount = infoIO.getForbiddenTermCount();
	long activeSubmissionCount = infoIO.getActiveSubmissionCount();
	long onHoldTermCount = infoIO.getOnHoldTermCount();
	long pendingTermCount = infoIO.getPendingTermCount();
	long completedSubmissionCount = infoIO.getCompletedSubmissionCount();
	long termCount = infoIO.getTermCount();
	long termInSubmissionCount = infoIO.getTermInSubmissionCount();

	projectLanguageDetailDAO.incrementalUpdateProjectLanguageDetail(infoIO);

	projectLanguageDetailDAO.flush();
	projectLanguageDetailDAO.clear();

	ProjectLanguageDetail afterUpdate = projectLanguageDetailDAO.findById(projectId);
	long approvedTermCountAfterUpdate = afterUpdate.getApprovedTermCount();
	long termEntryCountAfterUpdate = afterUpdate.getTermEntryCount();
	long forbiddenTermCountAfterUpdate = afterUpdate.getForbiddenTermCount();
	long activeSubmissionCountAfterUpdate = afterUpdate.getActiveSubmissionCount();
	long onHoldTermCountAfterUpdate = afterUpdate.getOnHoldTermCount();
	long pendingApprovalCountAfterUpdate = afterUpdate.getPendingApprovalCount();
	long completedSubmissionCountAfterUpdate = afterUpdate.getCompletedSubmissionCount();
	long termCountAfterUpdate = afterUpdate.getTermCount();
	long termInSubmissionCountAfterUpdate = afterUpdate.getTermInSubmissionCount();

	assertEquals(approvedTermCountBeforeUpdate + approvedTermCount, approvedTermCountAfterUpdate);
	assertEquals(termEntryCountBeforeUpdate + termEntryCount, termEntryCountAfterUpdate);
	assertEquals(forbiddenTermCountBeforeUpdate + forbiddenTermCount, forbiddenTermCountAfterUpdate);
	assertEquals(activeSubmissionCountBeforeUpdate + activeSubmissionCount, activeSubmissionCountAfterUpdate);
	assertEquals(onHoldTermCountBeforeUpdate + onHoldTermCount, onHoldTermCountAfterUpdate);
	assertEquals(pendingApprovalCountBeforeUpdate + pendingTermCount, pendingApprovalCountAfterUpdate);
	assertEquals(completedSubmissionCountBeforeUpdate + completedSubmissionCount,
		completedSubmissionCountAfterUpdate);
	assertEquals(termCountBeforeUpdate + termCount, termCountAfterUpdate);
	assertEquals(termInSubmissionCountBeforeUpdate + termInSubmissionCount, termInSubmissionCountAfterUpdate);

    }

    @Test
    public void recodeProjectLanguageDetailTest() {
	ProjectLanguageDetailDAO dao = getProjectLanguageDetailDAO();
	String languageFrom = "en-US";
	String languageTo = "en-CA";

	List<ProjectLanguageDetail> pldListBefore = dao.getProjectLanguageDetailsByProjectId(1L);
	assertTrue(CollectionUtils.isNotEmpty(pldListBefore));

	ProjectLanguageDetail pldBefore = dao.findProjectLangDetailByLangId(1L, languageFrom);
	assertNotNull(pldBefore);

	// Perform record action
	dao.recodeProjectLanguageDetail(1L, languageFrom, languageTo);
	dao.flush();
	dao.clear();

	// Number of projectLanguageDetails remains the same
	List<ProjectLanguageDetail> pldListAfter = dao.getProjectLanguageDetailsByProjectId(1L);
	assertEquals(pldListAfter.size(), pldListBefore.size());

	// No projectLanguageDetail on languageFrom after recoding
	ProjectLanguageDetail pldAfterOnLangFrom = dao.findProjectLangDetailByLangId(1L, languageFrom);
	Assert.assertNull(pldAfterOnLangFrom);

	// Assert projectLanguageDetail state after recoding
	ProjectLanguageDetail pldAfter = dao.findProjectLangDetailByLangId(1L, languageTo);
	assertNotNull(pldAfter);
	assertProjectLanguageDetail(pldBefore, pldAfter);
    }

    @Test
    public void testGetEntityPagedList() throws Exception {
	Long projectId = 4L;

	TmUserProfile user = getUserProfileDAO().load(1L);

	TmProject project = getProjectDAO().load(projectId);

	Date dateModified = new Date();

	// creating project detail
	ProjectDetail detail = new ProjectDetail(project);
	detail.setDateModified(dateModified);
	getProjectDetailDAO().save(detail);
	getProjectDetailDAO().flush();
	getProjectDetailDAO().clear();
	detail = getProjectDetailDAO().findByProjectId(projectId);
	Assert.assertNotNull(detail);

	ProjectDetailInfo info = new ProjectDetailInfo(projectId);

	ProjectUserDetail userDetail = new ProjectUserDetail(user, detail);
	userDetail = getProjectUserDetailDAO().save(userDetail);
	getProjectUserDetailDAO().flush();
	getProjectUserDetailDAO().clear();
	Assert.assertNotNull(userDetail);
	Assert.assertNotNull(userDetail.getProjectUserDetailId());

	Set<ProjectUserDetail> userDetails = new HashSet<ProjectUserDetail>();
	userDetails.add(userDetail);

	String enLanguageId = "en_US";
	String deLanguageId = "de_DE";

	// creating en-US language detail
	ProjectLanguageDetail enLanguageDetail = new ProjectLanguageDetail(enLanguageId, detail);
	enLanguageDetail.setDateModified(dateModified);
	enLanguageDetail = getProjectLanguageDetailDAO().save(enLanguageDetail);
	getProjectLanguageDetailDAO().flush();
	getProjectLanguageDetailDAO().clear();
	enLanguageDetail = getProjectLanguageDetailDAO().findById(enLanguageDetail.getProjectLanguageDetailId());
	Assert.assertNotNull(enLanguageDetail);

	ProjectLanguageUserDetail enLanguageUserDetail = new ProjectLanguageUserDetail(enLanguageDetail, user);
	enLanguageUserDetail = getProjectLanguageUserDetailDAO().save(enLanguageUserDetail);
	getProjectLanguageUserDetailDAO().flush();
	getProjectLanguageUserDetailDAO().clear();

	Set<ProjectLanguageUserDetail> enLanguageUserDetails = new HashSet<ProjectLanguageUserDetail>();
	enLanguageUserDetails.add(enLanguageUserDetail);
	enLanguageDetail.setUserDetails(enLanguageUserDetails);

	// creating de-DE language detail
	ProjectLanguageDetail deLanguageDetail = new ProjectLanguageDetail(deLanguageId, detail);
	deLanguageDetail.setDateModified(dateModified);
	getProjectLanguageDetailDAO().save(deLanguageDetail);
	getProjectLanguageDetailDAO().flush();
	getProjectLanguageDetailDAO().clear();
	deLanguageDetail = getProjectLanguageDetailDAO().findById(deLanguageDetail.getProjectLanguageDetailId());
	Assert.assertNotNull(deLanguageDetail);

	ProjectLanguageUserDetail deLanguageUserDetail = new ProjectLanguageUserDetail(deLanguageDetail, user);
	deLanguageUserDetail = getProjectLanguageUserDetailDAO().save(deLanguageUserDetail);
	getProjectLanguageUserDetailDAO().flush();
	getProjectLanguageUserDetailDAO().clear();

	Set<ProjectLanguageUserDetail> deLanguageUserDetails = new HashSet<ProjectLanguageUserDetail>();
	deLanguageUserDetails.add(deLanguageUserDetail);
	enLanguageDetail.setUserDetails(deLanguageUserDetails);

	Set<ProjectLanguageDetail> languageDetails = new HashSet<ProjectLanguageDetail>();
	languageDetails.add(enLanguageDetail);
	languageDetails.add(deLanguageDetail);
	detail.setLanguageDetails(languageDetails);
	detail.setUserDetails(userDetails);

	// incrementing term count for en-US
	info.incrementTermCount(enLanguageId);
	// detail.notifyObservers(info);
	getProjectDetailDAO().incrementalUpdateProjectDetail(info);
	getProjectLanguageDetailDAO().incrementalUpdateProjectLanguageDetail(enLanguageId, info, new Date());
	getProjectDetailDAO().flush();
	getProjectDetailDAO().clear();

	Set<String> languageIds = new HashSet<String>();
	languageIds.add(enLanguageId);

	// searching for en-US language detail
	ProjectLanguageDetailRequest command = new ProjectLanguageDetailRequest();
	command.setProjectDetailId(detail.getProjectDetailId());
	command.setLanguageIds(languageIds);
	command.setUser(user);

	PagedListInfo pagedListInfo = new PagedListInfo();

	PagedList<ProjectLanguageDetailView> pagedList = getProjectLanguageDetailDAO().getEntityPagedList(command,
		pagedListInfo);

	Assert.assertEquals(1, pagedList.getTotalCount().intValue());

	ProjectLanguageDetailView[] elements = pagedList.getElements();

	Assert.assertTrue(ArrayUtils.isNotEmpty(elements));

	ProjectLanguageDetailView element = elements[0];

	Assert.assertEquals(1, element.getTermCount());
    }

    /*
     ***************************************************************************
     * TERII-3208 Dashboard | Add Pending Approval and On Hold columns. This test
     * case testing reading ProjectLanguageDetail for new Pending Approval and On
     * Hold term statuses.
     ***************************************************************************
     */
    @Test
    public void testGetEntityPagedListOnHoldPendingApprovalReading() throws Exception {
	Long projectId = 4L;

	TmUserProfile user = getUserProfileDAO().load(1L);

	TmProject project = getProjectDAO().load(projectId);

	/*
	 * create project detail. en-US langId has one Pending approval term and de-DE
	 * has one On Hold term
	 */
	ProjectDetail detail = new ProjectDetail(project);
	detail.setPendingApprovalCount(1L);
	detail.setOnHoldTermCount(1L);
	detail = getProjectDetailDAO().save(detail);
	detail = getProjectDetailDAO().findByProjectId(projectId);
	Assert.assertNotNull(detail);

	String enLanguageId = "en-US";
	String deLanguageId = "de-DE";

	ProjectDetailInfo info = new ProjectDetailInfo(projectId);

	/* Increment Pending Approval for en_US */
	info.incrementPendingTermCount(enLanguageId);

	/* Increment On Hold fir de_DE */
	info.incrementOnHoldTermCount(deLanguageId);

	ProjectUserDetail userDetail = new ProjectUserDetail(user, detail);
	userDetail = getProjectUserDetailDAO().save(userDetail);
	Assert.assertNotNull(userDetail);
	Assert.assertNotNull(userDetail.getProjectUserDetailId());

	Set<ProjectUserDetail> userDetails = new HashSet<ProjectUserDetail>();
	userDetails.add(userDetail);

	// creating en-US language detail
	ProjectLanguageDetail enLanguageDetail = new ProjectLanguageDetail(enLanguageId, detail);
	enLanguageDetail.setPendingApprovalCount(1L);
	enLanguageDetail.setTermCount(1L);
	enLanguageDetail = getProjectLanguageDetailDAO().save(enLanguageDetail);
	enLanguageDetail = getProjectLanguageDetailDAO().findById(enLanguageDetail.getProjectLanguageDetailId());
	Assert.assertNotNull(enLanguageDetail);

	ProjectLanguageUserDetail enLanguageUserDetail = new ProjectLanguageUserDetail(enLanguageDetail, user);
	enLanguageUserDetail = getProjectLanguageUserDetailDAO().save(enLanguageUserDetail);

	Set<ProjectLanguageUserDetail> enLanguageUserDetails = new HashSet<ProjectLanguageUserDetail>();
	enLanguageUserDetails.add(enLanguageUserDetail);
	enLanguageDetail.setUserDetails(enLanguageUserDetails);

	// creating de-DE language detail
	ProjectLanguageDetail deLanguageDetail = new ProjectLanguageDetail(deLanguageId, detail);
	deLanguageDetail.setOnHoldTermCount(1L);
	deLanguageDetail.setTermCount(1L);
	getProjectLanguageDetailDAO().save(deLanguageDetail);
	deLanguageDetail = getProjectLanguageDetailDAO().findById(deLanguageDetail.getProjectLanguageDetailId());
	Assert.assertNotNull(deLanguageDetail);

	ProjectLanguageUserDetail deLanguageUserDetail = new ProjectLanguageUserDetail(deLanguageDetail, user);
	deLanguageUserDetail = getProjectLanguageUserDetailDAO().save(deLanguageUserDetail);

	Set<ProjectLanguageUserDetail> deLanguageUserDetails = new HashSet<ProjectLanguageUserDetail>();
	deLanguageUserDetails.add(deLanguageUserDetail);
	enLanguageDetail.setUserDetails(deLanguageUserDetails);

	Set<ProjectLanguageDetail> languageDetails = new HashSet<ProjectLanguageDetail>();
	languageDetails.add(enLanguageDetail);
	languageDetails.add(deLanguageDetail);
	detail.setLanguageDetails(languageDetails);
	detail.setUserDetails(userDetails);

	Set<String> languageIds = new HashSet<String>();
	languageIds.add(enLanguageId);
	languageIds.add(deLanguageId);

	// searching for en-US language detail
	ProjectLanguageDetailRequest command = new ProjectLanguageDetailRequest();
	command.setProjectDetailId(detail.getProjectDetailId());
	command.setLanguageIds(languageIds);
	command.setUser(user);

	PagedListInfo pagedListInfo = new PagedListInfo();

	PagedList<ProjectLanguageDetailView> pagedList = getProjectLanguageDetailDAO().getEntityPagedList(command,
		pagedListInfo);

	Assert.assertEquals(2, pagedList.getTotalCount().intValue());

	ProjectLanguageDetailView[] elements = pagedList.getElements();

	Assert.assertTrue(ArrayUtils.isNotEmpty(elements));

	ProjectLanguageDetailView element = elements[0];

	Assert.assertEquals(enLanguageId, element.getLanguage().getLanguageId());
	Assert.assertEquals(1, element.getTermCount());
	Assert.assertEquals(1, element.getPendingApprovalTermCount());

	/* Rest term statuses should be 0 */
	Assert.assertEquals(0, element.getOnHoldTermCount());
	Assert.assertEquals(0, element.getApprovedTermCount());
	Assert.assertEquals(0, element.getForbiddenTermCount());

	element = elements[1];
	Assert.assertEquals(deLanguageId, element.getLanguage().getLanguageId());
	Assert.assertEquals(1, element.getTermCount());
	Assert.assertEquals(1, element.getOnHoldTermCount());

	/* Rest term statuses should be 0 */
	Assert.assertEquals(0, element.getPendingApprovalTermCount());
	Assert.assertEquals(0, element.getApprovedTermCount());
	Assert.assertEquals(0, element.getForbiddenTermCount());

    }

    @Test
    public void testUpdateProjectLanguageDetail() {
	ProjectLanguageDetailDAO dao = getProjectLanguageDetailDAO();

	long projectId = 1L;

	List<ProjectLanguageDetail> plds = dao.getProjectLanguageDetailsByProjectId(projectId);

	Assert.assertTrue(plds.size() > 0);

	ProjectLanguageDetail pld = plds.get(0);
	Assert.assertEquals("en-US", pld.getLanguageId());

	ProjectDetailInfo info = new ProjectDetailInfo(projectId);
	info.addApprovedTermCount("en-US", 10);
	info.addForbiddenTermCount("en-US", 10);
	info.addPendingTermCount("en-US", 10);
	info.addOnHoldTermCount("en-US", 10);
	info.addTermCount("en-US", 40);

	dao.updateProjectLanguageDetail("en-US", info, new Date());
	dao.flush();
	dao.clear();

	plds = dao.getProjectLanguageDetailsByProjectId(projectId);
	pld = plds.get(0);

	long termCountUpdated = pld.getTermCount();
	long approvedTermCountUpdated = pld.getApprovedTermCount();
	long forbiddenTermCountUpdated = pld.getForbiddenTermCount();
	long pendingApprovalCountUpdated = pld.getPendingApprovalCount();
	long onHoldTermCountUpdated = pld.getOnHoldTermCount();

	assertEquals(40, termCountUpdated);
	assertEquals(10, approvedTermCountUpdated);
	assertEquals(10, forbiddenTermCountUpdated);
	assertEquals(10, pendingApprovalCountUpdated);
	assertEquals(10, onHoldTermCountUpdated);
    }

    @Test
    public void testUpdateProjectLanguageDetail_basic_case() {
	ProjectLanguageDetailDAO dao = getProjectLanguageDetailDAO();

	long projectId = 1L;

	List<ProjectLanguageDetail> plds = dao.getProjectLanguageDetailsByProjectId(projectId);

	Assert.assertTrue(plds.size() > 0);

	ProjectLanguageDetail pld = plds.get(0);
	Assert.assertEquals("en-US", pld.getLanguageId());

	long termCount = pld.getTermCount();
	long approvedTermCount = pld.getApprovedTermCount();
	long forbiddenTermCount = pld.getForbiddenTermCount();
	long onHoldTermCount = pld.getOnHoldTermCount();
	long pendingApprovalCount = pld.getPendingApprovalCount();

	ProjectDetailInfo info = new ProjectDetailInfo(projectId);
	info.addApprovedTermCount("en-US", 10);
	info.addForbiddenTermCount("en-US", 10);
	info.addPendingTermCount("en-US", 10);
	info.addOnHoldTermCount("en-US", 10);
	info.addTermCount("en-US", 40);

	dao.incrementalUpdateProjectLanguageDetail("en-US", info, new Date());
	dao.flush();
	dao.clear();

	plds = dao.getProjectLanguageDetailsByProjectId(projectId);
	pld = plds.get(0);

	long termCountUpdated = pld.getTermCount();
	long approvedTermCountUpdated = pld.getApprovedTermCount();
	long forbiddenTermCountUpdated = pld.getForbiddenTermCount();
	long pendingApprovalCountUpdated = pld.getPendingApprovalCount();
	long onHoldTermCountUpdated = pld.getOnHoldTermCount();

	assertEquals(termCount + 40, termCountUpdated);
	assertEquals(approvedTermCount + 10, approvedTermCountUpdated);
	assertEquals(forbiddenTermCount + 10, forbiddenTermCountUpdated);
	assertEquals(pendingApprovalCount + 10, pendingApprovalCountUpdated);
	assertEquals(onHoldTermCount + 10, onHoldTermCountUpdated);
    }

    @Test
    public void testUpdateProjectLanguageDetail_dateModified_case() {
	ProjectLanguageDetailDAO dao = getProjectLanguageDetailDAO();

	long projectId = 1L;

	List<ProjectLanguageDetail> plds = dao.getProjectLanguageDetailsByProjectId(projectId);

	Assert.assertTrue(plds.size() > 0);

	ProjectLanguageDetail pld = plds.get(0);
	Assert.assertEquals("en-US", pld.getLanguageId());

	Date oldDateModified = pld.getDateModified();

	ProjectDetailInfo info = new ProjectDetailInfo(projectId);

	dao.incrementalUpdateProjectLanguageDetail("en-US", info, new Date());
	dao.flush();
	dao.clear();

	plds = dao.getProjectLanguageDetailsByProjectId(projectId);
	pld = plds.get(0);

	Date dateModifiedUpdated = pld.getDateModified();

	Assert.assertTrue(oldDateModified.getTime() < dateModifiedUpdated.getTime());

	dao.incrementalUpdateProjectLanguageDetail("en-US", info, oldDateModified);
	dao.flush();
	dao.clear();

	plds = dao.getProjectLanguageDetailsByProjectId(projectId);
	pld = plds.get(0);

	Date dateModifiedUpdatedAgain = pld.getDateModified();

	Assert.assertTrue(oldDateModified.getTime() < dateModifiedUpdatedAgain.getTime());
    }

    @Test
    public void updateProjectLanguageDetailWithProjectLanguageDetailInfoIOTest() {

	long projectId = 1L;
	String languageId = "en-US";

	ProjectLanguageDetailDAO projectLanguageDetailDAO = getProjectLanguageDetailDAO();

	ProjectLanguageDetail beforeUpdate = projectLanguageDetailDAO.findById(projectId);
	assertEquals(2, beforeUpdate.getApprovedTermCount());
	assertEquals(2, beforeUpdate.getTermEntryCount());
	assertEquals(0, beforeUpdate.getForbiddenTermCount());
	assertEquals(1, beforeUpdate.getActiveSubmissionCount());
	assertEquals(0, beforeUpdate.getOnHoldTermCount());
	assertEquals(0, beforeUpdate.getPendingApprovalCount());
	assertEquals(0, beforeUpdate.getCompletedSubmissionCount());
	assertEquals(3, beforeUpdate.getTermCount());
	assertEquals(1, beforeUpdate.getTermInSubmissionCount());

	ProjectLanguageDetailInfoIO infoIO = createProjectLanguageDetail(projectId, languageId);
	long approvedTermCount = infoIO.getApprovedTermCount();
	long termEntryCount = infoIO.getTermEntryCount();
	long forbiddenTermCount = infoIO.getForbiddenTermCount();
	long activeSubmissionCount = infoIO.getActiveSubmissionCount();
	long onHoldTermCount = infoIO.getOnHoldTermCount();
	long pendingTermCount = infoIO.getPendingTermCount();
	long completedSubmissionCount = infoIO.getCompletedSubmissionCount();
	long termCount = infoIO.getTermCount();
	long termInSubmissionCount = infoIO.getTermInSubmissionCount();

	projectLanguageDetailDAO.updateProjectLanguageDetail(infoIO);

	projectLanguageDetailDAO.flush();
	projectLanguageDetailDAO.clear();

	ProjectLanguageDetail afterUpdate = projectLanguageDetailDAO.findById(projectId);
	assertEquals(approvedTermCount, afterUpdate.getApprovedTermCount());
	assertEquals(termEntryCount, afterUpdate.getTermEntryCount());
	assertEquals(forbiddenTermCount, afterUpdate.getForbiddenTermCount());
	assertEquals(activeSubmissionCount, afterUpdate.getActiveSubmissionCount());
	assertEquals(onHoldTermCount, afterUpdate.getOnHoldTermCount());
	assertEquals(pendingTermCount, afterUpdate.getPendingApprovalCount());
	assertEquals(completedSubmissionCount, afterUpdate.getCompletedSubmissionCount());
	assertEquals(termCount, afterUpdate.getTermCount());
	assertEquals(termInSubmissionCount, afterUpdate.getTermInSubmissionCount());

    }

    @Test
    public void updateProjectLanguageDetailWithProjectLanguageDetailInfoIOWithoutSubmissionTest() {

	long projectId = 1L;
	String languageId = "en-US";

	ProjectLanguageDetailDAO projectLanguageDetailDAO = getProjectLanguageDetailDAO();

	ProjectLanguageDetail beforeUpdate = projectLanguageDetailDAO.findById(projectId);
	assertEquals(2, beforeUpdate.getApprovedTermCount());
	assertEquals(2, beforeUpdate.getTermEntryCount());
	assertEquals(0, beforeUpdate.getForbiddenTermCount());
	assertEquals(1, beforeUpdate.getActiveSubmissionCount());
	assertEquals(0, beforeUpdate.getOnHoldTermCount());
	assertEquals(0, beforeUpdate.getPendingApprovalCount());
	assertEquals(0, beforeUpdate.getCompletedSubmissionCount());
	assertEquals(3, beforeUpdate.getTermCount());
	assertEquals(1, beforeUpdate.getTermInSubmissionCount());

	ProjectLanguageDetailInfoIO infoIO = createProjectLanguageDetailWithoutSubmission(projectId, languageId);
	long approvedTermCount = infoIO.getApprovedTermCount();
	long termEntryCount = infoIO.getTermEntryCount();
	long forbiddenTermCount = infoIO.getForbiddenTermCount();
	long onHoldTermCount = infoIO.getOnHoldTermCount();
	long pendingTermCount = infoIO.getPendingTermCount();
	long termCount = infoIO.getTermCount();
	long termInSubmissionCount = infoIO.getTermInSubmissionCount();

	projectLanguageDetailDAO.updateProjectLanguageDetail(infoIO);

	projectLanguageDetailDAO.flush();
	projectLanguageDetailDAO.clear();

	ProjectLanguageDetail afterUpdate = projectLanguageDetailDAO.findById(projectId);
	assertEquals(approvedTermCount, afterUpdate.getApprovedTermCount());
	assertEquals(termEntryCount, afterUpdate.getTermEntryCount());
	assertEquals(forbiddenTermCount, afterUpdate.getForbiddenTermCount());
	assertEquals(beforeUpdate.getActiveSubmissionCount(), afterUpdate.getActiveSubmissionCount());
	assertEquals(onHoldTermCount, afterUpdate.getOnHoldTermCount());
	assertEquals(pendingTermCount, afterUpdate.getPendingApprovalCount());
	assertEquals(beforeUpdate.getCompletedSubmissionCount(), afterUpdate.getCompletedSubmissionCount());
	assertEquals(termCount, afterUpdate.getTermCount());
	assertEquals(termInSubmissionCount, afterUpdate.getTermInSubmissionCount());

    }

    private void assertProjectLanguageDetail(ProjectLanguageDetail pldBefore, ProjectLanguageDetail pldAfter) {
	Assert.assertEquals(pldBefore.getApprovedTermCount(), pldAfter.getApprovedTermCount());
	Assert.assertEquals(pldBefore.getOnHoldTermCount(), pldAfter.getOnHoldTermCount());
	Assert.assertEquals(pldBefore.getPendingApprovalCount(), pldAfter.getPendingApprovalCount());
	Assert.assertEquals(pldBefore.getTermCount(), pldAfter.getTermCount());
	Assert.assertEquals(pldBefore.getTermEntryCount(), pldAfter.getTermEntryCount());
	Assert.assertEquals(pldBefore.getForbiddenTermCount(), pldAfter.getForbiddenTermCount());
	Assert.assertEquals(pldBefore.getCompletedSubmissionCount(), pldAfter.getCompletedSubmissionCount());
	Assert.assertEquals(pldBefore.getTermInSubmissionCount(), pldAfter.getTermInSubmissionCount());
	Assert.assertEquals(pldBefore.getActiveSubmissionCount(), pldAfter.getActiveSubmissionCount());
	Assert.assertEquals(pldBefore.getDateModified(), pldAfter.getDateModified());
	Assert.assertFalse(pldAfter.isDisabled());
    }

    private ProjectLanguageDetailInfoIO createProjectLanguageDetail(long projectId, String languageId) {
	ProjectLanguageDetailInfoIO infoIO = new ProjectLanguageDetailInfoIO();
	infoIO.setLanguageId(languageId);
	infoIO.setProjectId(projectId);
	infoIO.setApprovedTermCount(5L);
	infoIO.setForbiddenTermCount(5L);
	infoIO.setActiveSubmissionCount(5L);
	infoIO.setOnHoldTermCount(5L);
	infoIO.setPendingTermCount(5L);
	infoIO.setCompletedSubmissionCount(5L);
	infoIO.setTermCount(5L);
	infoIO.setTermInSubmissionCount(5L);
	infoIO.setTermEntryCount(5L);
	return infoIO;
    }

    private ProjectLanguageDetailInfoIO createProjectLanguageDetailWithoutSubmission(long projectId,
	    String languageId) {
	ProjectLanguageDetailInfoIO infoIO = new ProjectLanguageDetailInfoIO();
	infoIO.setLanguageId(languageId);
	infoIO.setProjectId(projectId);
	infoIO.setApprovedTermCount(5L);
	infoIO.setForbiddenTermCount(5L);
	infoIO.setOnHoldTermCount(5L);
	infoIO.setPendingTermCount(5L);
	infoIO.setTermCount(5L);
	infoIO.setTermInSubmissionCount(5L);
	infoIO.setTermEntryCount(5L);
	return infoIO;
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
