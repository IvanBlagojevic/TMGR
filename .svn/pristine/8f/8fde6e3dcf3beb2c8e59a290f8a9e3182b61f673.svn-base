package org.gs4tr.termmanager.dao.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.dao.ProjectDetailDAO;
import org.gs4tr.termmanager.dao.ProjectUserDetailDAO;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectUserDetail;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProjectUserDetailDAOTest extends AbstractSpringDAOIntegrationTest {

    private static final Long PROJECT_ID = 1L;

    private static final Long USER_ID_01 = 1L;

    private static final Long USER_ID_02 = 2L;

    @Autowired
    private ProjectDetailDAO _projectDetailDAO;

    @Autowired
    private ProjectUserDetailDAO _projectUserDetailDAO;

    @Test
    public void findByProjectIdTest() {
	List<ProjectUserDetail> projectUserDetails = getProjectUserDetailDAO().findByProjectId(PROJECT_ID);

	assertNotNull(projectUserDetails);

	assertTrue(CollectionUtils.isNotEmpty(projectUserDetails));

	assertEquals(3, projectUserDetails.size());
    }

    @Test
    public void findByUserAndProjectTest() {
	ProjectUserDetail projectUserDetail = getProjectUserDetailDAO().findByUserAndProject(USER_ID_01, PROJECT_ID);

	assertNotNull(projectUserDetail);

	assertTrue(projectUserDetail.getProjectUserDetailId().equals(1L));
	assertTrue(projectUserDetail.getProjectDetail().getProjectDetailId().equals(1L));
	assertTrue(projectUserDetail.getUser().getUserProfileId().equals(USER_ID_01));
    }

    @Test
    public void findByUsersAndProject() {
	final List<Long> userIds = new ArrayList<Long>();

	userIds.add(USER_ID_01);

	List<ProjectUserDetail> projectUserDetails = getProjectUserDetailDAO().findByUsersAndProject(userIds,
		PROJECT_ID);

	assertNotNull(projectUserDetails);

	assertTrue(CollectionUtils.isNotEmpty(projectUserDetails));

	assertEquals(1, projectUserDetails.size());
    }

    @Test
    public void findByUsersAndProjectTest() {
	final List<Long> userIds = new ArrayList<Long>();

	userIds.add(USER_ID_01);
	userIds.add(USER_ID_02);

	List<ProjectUserDetail> projectUserDetails = getProjectUserDetailDAO().findByUsersAndProject(userIds,
		PROJECT_ID);

	assertNotNull(projectUserDetails);

	assertTrue(CollectionUtils.isNotEmpty(projectUserDetails));

	assertEquals(2, projectUserDetails.size());
    }

    @Test
    public void testDuplicateProjectUserDetails() {
	TmUserProfile user = getUserProfileDAO().findUsersByUserNameNoFetch("bob_jones");

	ProjectDetail projectDetail = getProjectDetailDAO().findById(PROJECT_ID);

	ProjectUserDetail userDetail = new ProjectUserDetail(user, projectDetail);

	Set<ProjectUserDetail> userDetails = projectDetail.getUserDetails();

	Assert.assertTrue(CollectionUtils.isNotEmpty(userDetails));
	Assert.assertEquals(3, userDetails.size());
	Assert.assertTrue(userDetails.contains(userDetail));

	user = getUserProfileDAO().findUsersByUserNameNoFetch("giga");

	userDetail = new ProjectUserDetail(user, projectDetail);

	Assert.assertTrue(!userDetails.contains(userDetail));

	userDetail = getProjectUserDetailDAO().save(userDetail);
	userDetails.add(userDetail);
	projectDetail.setUserDetails(userDetails);

	getProjectDetailDAO().update(projectDetail);

	projectDetail = getProjectDetailDAO().findById(PROJECT_ID);
	userDetails = projectDetail.getUserDetails();
	Assert.assertEquals(4, userDetails.size());
    }

    @Test
    public void testIncrementalUpdateProjectDetail() {
	long userId = 1L;
	Date dateModified = new Date();
	long projectId = 1L;

	ProjectUserDetailDAO dao = getProjectUserDetailDAO();

	List<ProjectUserDetail> projectUserDetails = dao.findByUsersAndProject(Arrays.asList(userId), projectId);
	assertTrue(CollectionUtils.isNotEmpty(projectUserDetails));
	assertEquals(1, projectUserDetails.size());

	ProjectUserDetail projectUserDetail = projectUserDetails.get(0);
	assertEquals(1, projectUserDetail.getActiveSubmissionCount());
	assertEquals(0, projectUserDetail.getCompletedSubmissionCount());

	ProjectDetailInfo info = new ProjectDetailInfo(projectId);
	info.incrementActiveSubmissionCount(userId);
	info.incrementCompletedSubmissionCount(userId);

	dao.incrementalUpdateProjectDetail(userId, info, dateModified);
	dao.flush();
	dao.clear();

	projectUserDetails = dao.findByUsersAndProject(Arrays.asList(userId), projectId);
	assertTrue(CollectionUtils.isNotEmpty(projectUserDetails));
	assertEquals(1, projectUserDetails.size());

	projectUserDetail = projectUserDetails.get(0);
	assertEquals(2, projectUserDetail.getActiveSubmissionCount());
	assertEquals(1, projectUserDetail.getCompletedSubmissionCount());
    }

    private ProjectDetailDAO getProjectDetailDAO() {
	return _projectDetailDAO;
    }

    private ProjectUserDetailDAO getProjectUserDetailDAO() {
	return _projectUserDetailDAO;
    }

}
