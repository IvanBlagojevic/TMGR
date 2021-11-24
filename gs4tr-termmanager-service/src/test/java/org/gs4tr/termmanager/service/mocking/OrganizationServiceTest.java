package org.gs4tr.termmanager.service.mocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.OrganizationInfo;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.termmanager.dao.OrganizationDAO;
import org.gs4tr.termmanager.dao.ProjectDAO;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.OrganizationService;
import org.gs4tr.termmanager.service.impl.Messages;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("service")
public class OrganizationServiceTest extends AbstractServiceTest {

    private static final Long PROJECT_ID_01 = 1L;

    private static final Long PROJECT_ID_02 = 2L;

    private static final Long TM_ORGANIZATION_ID_01 = 1L;

    private static final Long TM_ORGANIZATION_ID_02 = 2L;

    private static final Long TM_ORGANIZATION_ID_03 = 3L;

    @Autowired
    private OrganizationDAO _organizationDAO;

    @Autowired
    private OrganizationService _organizationService;

    @Autowired
    private ProjectDAO _projectDAO;

    @Test
    @TestCase("organization")
    public void addProjectToOrganizationTest_01() {
	TmProject tmProject = getModelObject("tmProject1", TmProject.class);

	TmOrganization tmOrganization = getModelObject("tmOrganization1", TmOrganization.class);

	when(getProjectDAO().load(anyLong())).thenReturn(tmProject);
	when(getOrganizationDAO().findById(anyLong())).thenReturn(tmOrganization);

	Long entityId = getOrganizationService().addProjectToOrganization(TM_ORGANIZATION_ID_01, PROJECT_ID_01);

	verify(getProjectDAO(), times(1)).load(any(Long.class));
	verify(getProjectDAO(), times(1)).update(any(TmProject.class));
	verify(getOrganizationDAO(), times(1)).update(any((TmOrganization.class)));

	assertNotNull(entityId);
	assertNotNull(tmProject.getOrganization());
	assertTrue(entityId instanceof Long);
	assertTrue(entityId.equals(TM_ORGANIZATION_ID_01));
	assertEquals(2, tmOrganization.getProjects().size());
    }

    @Test
    @TestCase("organization")
    public void addProjectToOrganizationTest_02() {
	TmProject tmProject = getModelObject("tmProject2", TmProject.class);

	TmOrganization tmOrganization = getModelObject("tmOrganization2", TmOrganization.class);

	TmOrganization oldOrganization = tmProject.getOrganization();

	when(getProjectDAO().load(anyLong())).thenReturn(tmProject);
	when(getOrganizationDAO().findById(anyLong())).thenReturn(tmOrganization);

	Long entityId = getOrganizationService().addProjectToOrganization(TM_ORGANIZATION_ID_02, PROJECT_ID_02);

	verify(getProjectDAO(), times(1)).load(any(Long.class));
	verify(getProjectDAO(), times(1)).update(any(TmProject.class));
	verify(getOrganizationDAO(), times(2)).update(any((TmOrganization.class)));

	assertNotNull(entityId);
	assertTrue(entityId instanceof Long);
	assertTrue(entityId.equals(TM_ORGANIZATION_ID_02));
	assertTrue(CollectionUtils.isEmpty(oldOrganization.getProjects()));
	assertEquals(2, tmOrganization.getProjects().size());
    }

    @Test
    @TestCase("organization")
    public void assignOrganizationToUserTest() {
	TmUserProfile tmUser = (TmUserProfile) UserProfileContext.getCurrentUserProfile();

	assertNull(tmUser.getOrganization());

	TmOrganization tmOrganization = getModelObject("tmOrganization1", TmOrganization.class);

	assertNull(tmOrganization.getUsers());

	when(getUserProfileDAO().load(anyLong())).thenReturn(tmUser);
	when(getOrganizationDAO().findById(anyLong())).thenReturn(tmOrganization);

	ArgumentCaptor<TmUserProfile> userCaptor = ArgumentCaptor.forClass(TmUserProfile.class);
	ArgumentCaptor<TmOrganization> organizationCaptor = ArgumentCaptor.forClass(TmOrganization.class);

	Long userId = tmUser.getIdentifier();
	Long result = getOrganizationService().assignOrganizationToUser(TM_ORGANIZATION_ID_01, userId);

	verify(getUserProfileDAO(), new Times(1)).update(userCaptor.capture());
	verify(getOrganizationDAO(), new Times(1)).update(organizationCaptor.capture());

	TmUserProfile userAfter = userCaptor.getValue();
	TmOrganization organizationAfter = organizationCaptor.getValue();

	assertEquals(userId, result);

	assertNotNull(organizationAfter.getUsers());
	assertNotNull(userAfter.getOrganization());
	assertEquals(tmOrganization, userAfter.getOrganization());
    }

    @Test
    @TestCase("organization")
    public void assignProjectsToOrganizationTest_01() {
	TmOrganization tmOrganization = getModelObject("tmOrganization3", TmOrganization.class);

	Long[] projectIds = new Long[0];

	when(getOrganizationDAO().findById(anyLong())).thenReturn(tmOrganization);

	Long organizationId = getOrganizationService().assignProjectsToOrganization(TM_ORGANIZATION_ID_03, projectIds);

	ArgumentCaptor<TmOrganization> organizationCaptor = ArgumentCaptor.forClass(TmOrganization.class);

	verify(getOrganizationDAO(), new Times(1)).update(organizationCaptor.capture());

	TmOrganization organizationAfter = organizationCaptor.getValue();

	assertNotNull(organizationId);
	assertEquals(TM_ORGANIZATION_ID_03, organizationId);
	assertTrue(CollectionUtils.isEmpty(organizationAfter.getProjects()));
    }

    @Test
    @TestCase("organization")
    public void assignProjectsToOrganizationTest_02() {
	Long[] projectIds = getModelObject("projectIds", Long[].class);
	TmProject tmProject1 = getModelObject("tmProject1", TmProject.class);
	TmProject tmProject2 = getModelObject("tmProject2", TmProject.class);

	TmOrganization tmOrganization = getModelObject("tmOrganization3", TmOrganization.class);

	when(getOrganizationDAO().findById(anyLong())).thenReturn(tmOrganization);
	when(getProjectDAO().load(anyLong())).thenReturn(tmProject1, tmProject2);

	Long organizationId = getOrganizationService().assignProjectsToOrganization(TM_ORGANIZATION_ID_03, projectIds);

	ArgumentCaptor<TmProject> projectCaptor = ArgumentCaptor.forClass(TmProject.class);

	verify(getProjectDAO(), new Times(2)).update(projectCaptor.capture());

	List<TmProject> projectsSendToUpdate = projectCaptor.getAllValues();
	Set<TmProject> projects = tmOrganization.getProjects();

	assertEquals(tmProject1, projectsSendToUpdate.get(0));
	assertEquals(tmProject2, projectsSendToUpdate.get(1));
	assertEquals(tmOrganization, projectsSendToUpdate.get(0).getOrganization());
	assertEquals(tmOrganization, projectsSendToUpdate.get(1).getOrganization());
	assertEquals(TM_ORGANIZATION_ID_03, organizationId);

	assertTrue(projects.contains(tmProject1));
	assertTrue(projects.contains(tmProject2));
	assertTrue(projects.size() == 2);
    }

    @Test
    @TestCase("organization")
    @SuppressWarnings("unchecked")
    public void enableOrganizationUsersTest() {
	OrganizationService service = getOrganizationService();
	List<TmOrganization> allOrganizations = getModelObject("allOrganizations", List.class);

	when(getOrganizationDAO().findAllWithDependants()).thenReturn(allOrganizations);

	service.enableOrganizationUsers(TM_ORGANIZATION_ID_01, true);

	ArgumentCaptor<TmOrganization> organizationCaptor = ArgumentCaptor.forClass(TmOrganization.class);

	verify(getOrganizationDAO(), new Times(1)).update(organizationCaptor.capture());

	OrganizationInfo organizationInfo = organizationCaptor.getValue().getOrganizationInfo();

	assertTrue(organizationInfo.isEnabled());
    }

    @Test
    @TestCase("organization")
    @SuppressWarnings("unchecked")
    public void enableOrganizationUsersWhenTheOrganizationDoesNotExistTest() {
	OrganizationService service = getOrganizationService();

	final String allOrganizations = "allOrganizations"; //$NON-NLS-1$
	List<TmOrganization> organizations = getModelObject(allOrganizations, List.class);

	when(getOrganizationDAO().findAllWithDependants()).thenReturn(organizations);
	try {
	    service.enableOrganizationUsers(TM_ORGANIZATION_ID_03, true);

	} catch (Exception e) {
	    assertTrue(e instanceof NullPointerException);
	} finally {
	    verify(getOrganizationDAO(), new Times(1)).enableOrganizationUsers(anyLong(), anyBoolean());
	}
    }

    @Test
    @TestCase("organization")
    public void findByNameTest() {
	final String organizationName = "Emisia"; //$NON-NLS-1$

	OrganizationService service = getOrganizationService();

	service.findByName(organizationName);

	verify(getOrganizationDAO(), times(1)).findByName(eq(organizationName));
    }

    @Test
    @TestCase("organization")
    public void findByNameWhenNameIsBlankTest() {
	OrganizationService service = getOrganizationService();

	TmOrganization tmOrganization = null;
	try {
	    tmOrganization = service.findByName(StringConstants.EMPTY); // $NON-NLS-1$

	} catch (RuntimeException e) {
	    assertEquals(Messages.getString("OrganizationServiceImpl.0")//$NON-NLS-1$
		    , e.getMessage());
	} finally {
	    assertNull(tmOrganization);
	}

    }

    @Test
    @TestCase("organization")
    public void getOrganizationProjects() {
	TmProject tmProject = getModelObject("tmProject2", TmProject.class);

	when(getProjectDAO().load(anyLong())).thenReturn(tmProject);

	Set<TmProject> projects = getOrganizationService().getOrganizationProjects(PROJECT_ID_02);

	assertNotNull(projects);

	assertTrue(CollectionUtils.isNotEmpty(projects));
	assertTrue(projects.contains(tmProject));
	assertTrue(projects.size() == 1);
    }

    @Before
    public void setUp() throws Exception {
	reset(getOrganizationDAO());
	reset(getProjectDAO());
    }

    private OrganizationDAO getOrganizationDAO() {
	return _organizationDAO;
    }

    private OrganizationService getOrganizationService() {
	return _organizationService;
    }

    private ProjectDAO getProjectDAO() {
	return _projectDAO;
    }
}
