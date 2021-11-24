package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.security.model.RoleTypeEnum;
import org.gs4tr.termmanager.model.TmPowerUserProjectRole;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.junit.Assert;
import org.junit.Test;

public class PowerUserProjectRoleServiceTest extends AbstractSpringServiceTests {

    private static final Long POWER_USER_PROJECT_ROLE_ID = 2L;

    private static final Long PROJECT_ID = 1L;

    @Test
    public void findByUserIdTest() {
	Long userProfileId = 10L;

	TmPowerUserProjectRole powerUserProjectRole = getPowerUserProjectRoleService().findByUserId(userProfileId);
	assertNotNull(powerUserProjectRole);
	assertNotNull(powerUserProjectRole.getUserProfile());
	assertNotNull(powerUserProjectRole.getRole());

	Assert.assertEquals(powerUserProjectRole.getPowerUserProjectRoleId(), POWER_USER_PROJECT_ROLE_ID);
	Assert.assertEquals(powerUserProjectRole.getRole().getRoleId(), "term_translator");
	Assert.assertEquals(powerUserProjectRole.getUserProfile().getUserProfileId(), userProfileId);

    }

    @Test
    public void saveTest() {
	Long userProfileId = 11L;

	TmUserProfile userProfile = getUserProfileService().findById(userProfileId);
	assertNotNull(userProfile);

	Role termTranslatorProjectRole = getProjectRoleByUserAndProjectId(PROJECT_ID, 3L);
	assertNotNull(termTranslatorProjectRole);

	TmPowerUserProjectRole tmPowerUserProjectRole = new TmPowerUserProjectRole();
	tmPowerUserProjectRole.setUserProfile(userProfile);
	tmPowerUserProjectRole.setRole(termTranslatorProjectRole);

	getPowerUserProjectRoleService().save(tmPowerUserProjectRole);

	TmPowerUserProjectRole savedPowerUserProjectRole = getPowerUserProjectRoleService().findByUserId(userProfileId);
	assertNotNull(savedPowerUserProjectRole);
	Assert.assertEquals(savedPowerUserProjectRole.getUserProfile(), userProfile);
	Assert.assertEquals(savedPowerUserProjectRole.getRole(), termTranslatorProjectRole);
    }

    @Test
    public void updatePowerUserProjectRolesTest() {
	Long userProfileId = 10L;

	TmPowerUserProjectRole powerUserProjectRole = getPowerUserProjectRoleService().findByUserId(userProfileId);
	assertNotNull(powerUserProjectRole);

	Role newProjectRole = getProjectRoleByUserAndProjectId(PROJECT_ID, 6L);
	assertNotNull(newProjectRole);

	boolean b = getPowerUserProjectRoleService().updatePowerUserProjectRoles(newProjectRole, userProfileId);
	Assert.assertTrue(b);

	powerUserProjectRole = getPowerUserProjectRoleService().findByUserId(userProfileId);

	Assert.assertEquals(powerUserProjectRole.getRole(), newProjectRole);
    }

    private Role getProjectRoleByUserAndProjectId(Long projectId, Long userId) {
	List<Role> rolesByUserAndProject = getProjectService().getRolesByUserAndProject(projectId, userId);

	for (Role role : rolesByUserAndProject) {
	    if (role.getRoleType().equals(RoleTypeEnum.CONTEXT))
		return role;
	}
	return null;
    }

}
