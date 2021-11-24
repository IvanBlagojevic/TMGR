package org.gs4tr.termmanager.dao.hibernate;

import static org.junit.Assert.assertNotNull;

import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.termmanager.dao.PowerUserProjectRoleDAO;
import org.gs4tr.termmanager.model.TmPowerUserProjectRole;
import org.junit.Assert;
import org.junit.Test;

public class PowerUserProjectRoleDAOTest extends AbstractSpringDAOIntegrationTest {

    private static final String NEW_PROJECT_ROLE_ID = "testProjectQm";

    private static final Long POWER_USER_PROJECT_ROLE_ID = 1L;

    private static final Long USER_ID = 1L;

    @Test
    public void findByUserIdTest() {

	TmPowerUserProjectRole powerUserProjectRole = getPowerUserProjectRoleDAO().findByUserId(USER_ID);

	assertNotNull(powerUserProjectRole);
	assertNotNull(powerUserProjectRole.getUserProfile());
	assertNotNull(powerUserProjectRole.getRole());

	Assert.assertEquals(powerUserProjectRole.getPowerUserProjectRoleId(), POWER_USER_PROJECT_ROLE_ID);
	Assert.assertEquals(powerUserProjectRole.getRole().getRoleId(), "testProjectPm");
	Assert.assertEquals(powerUserProjectRole.getUserProfile().getUserProfileId(), USER_ID);

    }

    @Test
    public void updatePowerUserProjectRoleTest() {
	PowerUserProjectRoleDAO dao = getPowerUserProjectRoleDAO();

	TmPowerUserProjectRole powerUserProjectRole = dao.findByUserId(USER_ID);
	Assert.assertNotNull(powerUserProjectRole);

	Role newProjectRole = getRoleDAO().findById(NEW_PROJECT_ROLE_ID);
	Assert.assertNotNull(newProjectRole);

	boolean b = dao.updatePowerUserProjectRole(NEW_PROJECT_ROLE_ID, USER_ID);
	Assert.assertTrue(b);
	dao.flush();
	dao.clear();

	powerUserProjectRole = dao.findByUserId(USER_ID);
	Assert.assertNotNull(powerUserProjectRole);

	Assert.assertEquals(powerUserProjectRole.getRole(), newProjectRole);
    }

}
