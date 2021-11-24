package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.gs4tr.foundation.modules.entities.model.OrganizationInfo;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.search.OrganizationSearchRequest;
import org.gs4tr.termmanager.model.TmOrganization;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrganizationServiceTest extends AbstractSpringServiceTests {

    private static final String ORGANIZATION_NAME = "OrganizationServiceTestName";

    private static final Long USER_ID = 6L;

    @Autowired
    private OrganizationService _organizationService;

    @Test
    public void getOrganizationIdByUserIdTest() {
	Long organizationId = getOrganizationService().getOrganizationIdByUserId(USER_ID);
	assertNotNull(organizationId);
    }

    @Test
    public void getOrganizationNameByUserIdTest() {
	String organizationName = getOrganizationService().getOrganizationNameByUserId(USER_ID);
	assertNotNull(organizationName);
    }

    @Test
    public void testAREPOrganizationTasks() {
	setupPm();

	OrganizationSearchRequest request = new OrganizationSearchRequest();

	PagedListInfo info = new PagedListInfo();

	TaskPagedList<TmOrganization> organizations = (TaskPagedList<TmOrganization>) getOrganizationService()
		.search(request, info);

	assertNotNull(organizations.getTasks()[0]);

	organizations = (TaskPagedList<TmOrganization>) getOrganizationService().search(request, info);

	assertEquals(4, organizations.getTasks().length);

    }

    @Test
    public void testCreateOrganization() {
	Long id = createOrganization();

	assertNotNull(id);

	assertNotNull(getOrganizationService().findById(id));
    }

    @Test
    public void testCreateSimpleOrganization() {
	OrganizationSearchRequest request = new OrganizationSearchRequest();

	PagedListInfo info = new PagedListInfo();

	TaskPagedList<TmOrganization> organizations = (TaskPagedList<TmOrganization>) getOrganizationService()
		.search(request, info);

	TmOrganization parentOrganization = organizations.getElements()[1];

	OrganizationInfo organizationInfo = new OrganizationInfo();

	organizationInfo.setName(ORGANIZATION_NAME);

	Long organizationId = getOrganizationService().createOrganization(organizationInfo,
		parentOrganization.getOrganizationId());

	assertNotNull(organizationId);

    }

    @Test
    public void testUpdateOrganization() {
	Long organizationId = createOrganization();

	TmOrganization organization = getOrganizationService().findById(organizationId);

	OrganizationInfo organizationInfo = organization.getOrganizationInfo();

	assertNotNull(organizationInfo);

	assertEquals(ORGANIZATION_NAME, organization.getOrganizationInfo().getName());

	organizationInfo.setName("tempName");

	getOrganizationService().updateOrganization(organizationId, null, organizationInfo);

	organization = getOrganizationService().findById(organizationId);

	assertEquals("tempName", organization.getOrganizationInfo().getName());
    }

    private Long createOrganization() {
	OrganizationInfo organizationInfo = new OrganizationInfo();

	OrganizationSearchRequest request = new OrganizationSearchRequest();

	PagedListInfo info = new PagedListInfo();

	TaskPagedList<TmOrganization> organizations = (TaskPagedList<TmOrganization>) getOrganizationService()
		.search(request, info);

	TmOrganization parentOrganization = organizations.getElements()[1];

	organizationInfo.setName(ORGANIZATION_NAME);

	return getOrganizationService().createOrganization(organizationInfo, parentOrganization.getOrganizationId());
    }

    protected OrganizationService getOrganizationService() {
	return _organizationService;
    }
}
