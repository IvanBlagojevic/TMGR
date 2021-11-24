package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.Metadata;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.search.UserProfileSearchRequest;
import org.gs4tr.foundation.modules.usermanager.service.impl.SessionServiceException;
import org.gs4tr.termmanager.model.Preferences;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectUserDetail;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UserCustomSearch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserProfileServiceTest extends AbstractSpringServiceTests {

    @Test
    public void getUsersByOrganizationName() {

	String organizationName = "Translations";

	UserProfileSearchRequest request = new UserProfileSearchRequest();
	request.setOrganizationId(1L);

	PagedList<TmUserProfile> userProfilePagedList = getUserProfileService().search(request, null);

	for (TmUserProfile profile : userProfilePagedList.getElements()) {
	    assertEquals(profile.getOrganization().getOrganizationInfo().getName(), organizationName);
	}
    }

    @Override
    @Before
    public void setUp() throws Exception {
	super.setUp();

	Preferences preferences = new Preferences();

	preferences.setItemsPerPage(50);
	preferences.setRefreshPeriod(5000);

	getUserProfileService().addOrUpdateMetadata("inbox_target", "testMetadata");

	getUserProfileService().updatePreferences(preferences);
    }

    @Test
    public void testAddMetadata() {
	getUserProfileService().addOrUpdateMetadata("testKey", "testValue");

	List<Metadata> metadatas = TmUserProfile.getCurrentUserProfile().getMetadata();

	assertEquals(2, metadatas.size());

	assertEquals("testMetadata", TmUserProfile.getCurrentUserProfile().getMetadataValue("inbox_target"));
    }

    @Test
    public void testAddOrUpdateCustomSearchFolder() {
	TmUserProfile user = TmUserProfile.getCurrentUserProfile();

	String folder = "newFolder";
	String originalFolder = "originalFolder";
	String url = "task.ter";
	String searchJsonData = "searchJsonData";

	getUserProfileService().addOrUpdateCustomSearchFolder(user, folder, originalFolder, url, searchJsonData, false);

	UserCustomSearch customSearchFolder = getUserProfileService().getCustomSearchFolder(user, folder);
	Assert.assertNotNull(customSearchFolder);
	Assert.assertEquals(folder, customSearchFolder.getCustomFolder());
	Assert.assertEquals(url, customSearchFolder.getUrl());
    }

    @Test
    public void testAddOrUpdateProjectUserDetails() {
	Long projectId = 1L;
	Long userId = 6L;

	List<Long> newUserIds = new ArrayList<Long>();
	newUserIds.add(userId);

	TmProject project = getProjectService().findProjectById(projectId, ProjectDetail.class,
		ProjectUserDetail.class);
	Assert.assertNotNull(project);

	ProjectDetail projectDetail = project.getProjectDetail();
	Assert.assertNotNull(projectDetail);

	Set<ProjectUserDetail> userDetails = projectDetail.getUserDetails();
	Assert.assertEquals(1, userDetails.size());

	getUserProfileService().addOrUpdateProjectUserDetails(userId, projectId, newUserIds);

	project = getProjectService().findProjectById(projectId, ProjectDetail.class, ProjectUserDetail.class);
	projectDetail = project.getProjectDetail();
	userDetails = projectDetail.getUserDetails();

	Assert.assertEquals(1, userDetails.size());
    }

    @Test
    public void testDisableUserProfiles() {

	TmUserProfile userBefore = getUserProfileService().findById(8L);

	Assert.assertTrue(userBefore.getUserInfo().isEnabled());

	Date deactivationDate2Before = userBefore.getUserInfo().getDeactivationDate();

	Assert.assertNull(deactivationDate2Before);

	List<Long> userIds = Arrays.asList(8L);

	// Disable users
	getUserProfileService().disableUserProfiles(userIds);

	TmUserProfile userAfter = getUserProfileService().findById(8L);

	Assert.assertFalse(userAfter.getUserInfo().isEnabled());

	Date deactivationDateAfter = userAfter.getUserInfo().getDeactivationDate();

	// assert if deactivation date is updated
	Assert.assertNotNull(deactivationDateAfter);

    }

    @Test
    public void testFindAllNonGenerciUsernames() {
	List<String> userNames = getUserProfileService().findAllNonGenerciUsernames();
	Assert.assertTrue(CollectionUtils.isNotEmpty(userNames));
    }

    /*
     * TERII-4414 TPT4 | Application error while switching tabs on Edit Project
     */
    @Test
    public void testFindGenericUserByProjectId() throws Exception {

	List<TmUserProfile> allUsers = getUserProfileService().findAllUserProfiles();

	TmUserProfile userToUpdate = allUsers.stream().filter(userProfile -> userProfile.getUserProfileId().equals(3L))
		.findFirst().orElse(null);
	userToUpdate.setGeneric(Boolean.TRUE);

	// Update user to be generic
	String updatedUserName = userToUpdate.getUserName();
	Long updatedUserId = userToUpdate.getUserProfileId();
	getUserProfileService().update(userToUpdate);

	// Test if user is generic after update
	List<TmUserProfile> allUsersAfterUpdate = getUserProfileService().findAllUserProfiles();
	TmUserProfile updatedUserProfile = allUsersAfterUpdate.stream()
		.filter(userProfile -> userProfile.getUserProfileId().equals(updatedUserId)).findFirst().orElse(null);
	assertTrue(updatedUserProfile.isGenericUser());

	List<TmProject> updatedProject = getProjectService().getUserProjects(updatedUserProfile.getUserProfileId());

	// Check if generic user is found on the project
	List<TmUserProfile> genericUsers = getUserProfileService()
		.findGenericUserByProjectId(updatedProject.get(0).getProjectId());
	assertNotNull(genericUsers);
	assertEquals(1, genericUsers.size());
	assertEquals(updatedUserId, genericUsers.get(0).getUserProfileId());
	assertEquals(updatedUserName, genericUsers.get(0).getUserName());
    }

    @Test
    public void testFindUsersByUsernames() {
	List<String> usernames = new ArrayList<String>();
	usernames.add("pm");

	List<TmUserProfile> users = getUserProfileService().findUsersByUsernames(usernames);
	Assert.assertNotNull(users);
	Assert.assertTrue(CollectionUtils.isNotEmpty(users));
    }

    @Test
    public void testGetAllPowerAndOrgUsers_case1() {
	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(1L);
	projectIds.add(2L);
	projectIds.add(3L);

	Set<TmUserProfile> users = getUserProfileService().getAllPowerAndOrgUsers(projectIds);
	Assert.assertTrue(CollectionUtils.isNotEmpty(users));
    }

    @Test
    public void testGetAllPowerAndOrgUsers_case2() {
	List<Long> projectIds = new ArrayList<Long>();

	Set<TmUserProfile> users = getUserProfileService().getAllPowerAndOrgUsers(projectIds);
	Assert.assertTrue(CollectionUtils.isEmpty(users));
    }

    /*
     * TERII-5597 Unable to login with existing power users
     */
    @Test
    public void testPowerUserLogin() {
	boolean isLoginException = false;
	try {

	    getSessionService().logout();
	    getSessionService().login("power", "test");

	} catch (SessionServiceException e) {
	    isLoginException = true;
	}

	Assert.assertFalse(isLoginException);
    }

    @Test
    public void testRemoveCustomSearchFolder() {
	TmUserProfile user = TmUserProfile.getCurrentUserProfile();

	String folder = "customFolder";

	UserCustomSearch customSearchFolder = getUserProfileService().getCustomSearchFolder(user, folder);

	getUserProfileService().removeCustomSearchFolder(user, folder);

	customSearchFolder = getUserProfileService().getCustomSearchFolder(user, folder);
	org.junit.Assert.assertNull(customSearchFolder);
    }

}
