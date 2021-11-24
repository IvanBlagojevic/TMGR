package org.gs4tr.termmanager.dao.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UserProjectRole;
import org.junit.Test;

public class UserProjectRoleSearchTest extends AbstractSpringDAOIntegrationTest {

    private static final Long PROJECT_ID_01 = 1L;

    private static final Long PROJECT_ID_02 = 2L;

    private static final Long USER_ID = 1L;

    private static final Long USER_PROJECT_ROLE_ID_01 = 1L;

    private static final Long USER_PROJECT_ROLE_ID_02 = 3L;

    @Test
    public void findAllNonGenericEnabledUsers() {
	List<TmUserProfile> users = getUserProjectRoleSearchDAO().findAllNonGenericEnabledUsers();

	assertNotNull(users);

	assertEquals(2, users.size());

	assertTrue(users.get(0).getUserProfileId().equals(1L));
	assertTrue(users.get(1).getUserProfileId().equals(2L));
    }

    @Test
    public void findUsersByProjectForReportTest() {
	List<TmUserProfile> users = getUserProjectRoleSearchDAO().findUsersForReport();

	assertNotNull(users);

	assertTrue(CollectionUtils.isNotEmpty(users));

	assertEquals(2, users.size());
    }

    @Test
    public void getGenericUsersByProjectTest() {
	List<TmUserProfile> users = getUserProjectRoleSearchDAO().getGenericUsersByProject(PROJECT_ID_01);

	TmUserProfile genericUser = getUserProfileDAO().findById(3L);

	assertNotNull(users);

	assertTrue(CollectionUtils.isNotEmpty(users));

	assertTrue(users.contains(genericUser));

	assertEquals(1, users.size());
    }

    @Test
    public void getProjectRolesByUser() {
	List<UserProjectRole> userProjectRoles = getUserProjectRoleSearchDAO().getProjectRolesByUser(USER_ID);

	UserProjectRole testProjectPm = getUserProjectRoleSearchDAO().findById(USER_PROJECT_ROLE_ID_01);

	UserProjectRole translator = getUserProjectRoleSearchDAO().findById(USER_PROJECT_ROLE_ID_02);

	assertNotNull(userProjectRoles);

	assertTrue(CollectionUtils.isNotEmpty(userProjectRoles));
	assertTrue(userProjectRoles.contains(testProjectPm));
	assertTrue(userProjectRoles.contains(translator));
	assertEquals(2, userProjectRoles.size());
    }

    /* NOTE: user project roles are set on a "testProject" */
    @Test
    public void getProjectRolesByUserIdTest() {
	Map<Long, List<Role>> projectRolesByUserId = getUserProjectRoleSearchDAO().getProjectRolesByUserId(USER_ID);

	assertNotNull(projectRolesByUserId);

	for (Entry<Long, List<Role>> entry : projectRolesByUserId.entrySet()) {
	    List<Role> userRoles = entry.getValue();

	    assertNotNull(userRoles);

	    assertTrue(CollectionUtils.isNotEmpty(userRoles));
	    assertTrue(userRoles.size() == 2);
	}
    }

    @Test
    public void testFindProjectUsers() {
	List<TmUserProfile> users = getUserProjectRoleSearchDAO().getUsersByProject(new Long(1));
	assertEquals(5, users.size());
    }

    @Test
    public void testFindUserProjects() {

	List<TmProject> projects = getUserProjectRoleSearchDAO().getProjectsByUser(new Long(1));
	assertEquals(1, projects.size());

    }

    @Test
    public void testProjectRoles() {

	List<Role> roles = getUserProjectRoleSearchDAO().getRolesByUserAndProject(new Long(1), new Long(1));
	assertEquals(2, roles.size());
	List<Policy> policies = new ArrayList<Policy>();
	for (Role role : roles) {
	    policies.addAll(role.getPolicies());
	}
	assertEquals(5, policies.size());

	Policy policy = getPolicyByName("policy3", policies);
	assertNotNull(policy);
    }

    @Test
    public void testUserProjectRoles() {
	List<UserProjectRole> roles = getUserProjectRoleSearchDAO().getAllUserProjectRoles(new Long(1), new Long(1));
	assertEquals(2, roles.size());
    }

    private Policy getPolicyByName(String name, List<Policy> policies) {
	for (Policy policy : policies) {
	    if (policy.getPolicyId().equals(name)) {
		return policy;
	    }
	}

	return null;
    }
}
