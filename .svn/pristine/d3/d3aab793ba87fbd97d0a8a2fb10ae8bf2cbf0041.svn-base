package org.gs4tr.termmanager.dao.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.termmanager.dao.OrganizationDAO;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrganizationDAOTest extends AbstractSpringDAOIntegrationTest {

    private static final Long ORGANIZATION_ID_01 = 1l;

    private static final Long ORGANIZATION_ID_02 = 2l;

    private static final Long ORGANIZATION_ID_03 = 3l;

    private static final Long PROJECT_ID_01 = 2l;

    private static final Long PROJECT_ID_02 = 1l;

    private static final Long PROJECT_ID_04 = 4l;

    @Autowired
    private OrganizationDAO _organizationDAO;

    @Test
    public void enableOrganizationUsersTest() {
	TmOrganization organization = getOrganizationDAO().findById(ORGANIZATION_ID_01);

	Set<TmUserProfile> organizationUsers = organization.getUsers();

	getOrganizationDAO().enableOrganizationUsers(ORGANIZATION_ID_01, Boolean.FALSE);

	verifyUsersIsDisabled(organizationUsers);
    }

    @Test
    public void findAllWithDependantsTest() {
	List<TmOrganization> tmOrganizations = getOrganizationDAO().findAllWithDependants();

	TmOrganization tmOrganization1 = getOrganizationDAO().findById(ORGANIZATION_ID_01);
	TmOrganization tmOrganization2 = getOrganizationDAO().findById(ORGANIZATION_ID_02);
	TmOrganization tmOrganization3 = getOrganizationDAO().findById(ORGANIZATION_ID_03);

	assertNotNull(tmOrganizations);
	assertNotNull(tmOrganization1);
	assertNotNull(tmOrganization2);
	assertNotNull(tmOrganization3);

	assertTrue(CollectionUtils.isNotEmpty(tmOrganizations));

	assertTrue(tmOrganizations.contains(tmOrganization1));
	assertTrue(tmOrganizations.contains(tmOrganization2));
	assertTrue(tmOrganizations.contains(tmOrganization3));
	assertTrue(tmOrganizations.size() == 3);

    }

    @Test
    public void findOrganizationProjectsTest() {
	List<TmProject> tmProjects = getOrganizationDAO().findOrganizationProjects(ORGANIZATION_ID_01);

	TmProject tmProject1 = getProjectDAO().findById(PROJECT_ID_01);
	TmProject tmProject2 = getProjectDAO().findById(PROJECT_ID_02);
	TmProject tmProject3 = getProjectDAO().findById(PROJECT_ID_04);

	assertNotNull(tmProjects);
	assertNotNull(tmProject1);
	assertNotNull(tmProject2);
	assertNotNull(tmProject3);

	assertTrue(CollectionUtils.isNotEmpty(tmProjects));

	assertTrue(tmProjects.contains(tmProject1));
	assertTrue(tmProjects.contains(tmProject2));
	assertTrue(tmProjects.contains(tmProject3));
	assertEquals(7, tmProjects.size());

    }

    @Test
    public void getOrganizationIdByUserIdTest() {
	Long organizationId = getOrganizationDAO().getOrganizationIdByUserId(1L);
	assertNotNull(organizationId);
	assertEquals(Long.valueOf(1), organizationId);
    }

    @Test
    public void getOrganizationNameByUserIdTest() {
	String organizationName = getOrganizationDAO().getOrganizationNameByUserId(1L);
	assertNotNull(organizationName);
	assertEquals("Translations", organizationName);
    }

    private OrganizationDAO getOrganizationDAO() {
	return _organizationDAO;
    }

    private void verifyUsersIsDisabled(Set<TmUserProfile> organizationUsers) {
	if (CollectionUtils.isNotEmpty(organizationUsers)) {
	    for (TmUserProfile tmUserProfile : organizationUsers) {
		UserInfo userInfo = tmUserProfile.getUserInfo();

		assertFalse((userInfo.isEnabled()));
	    }
	}
    }

}
