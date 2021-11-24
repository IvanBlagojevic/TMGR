package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.search.OrganizationSearchRequest;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.TmOrganization;
import org.junit.Test;

public class OrganizationSearchServiceTest extends AbstractSearchTests {

    @Test
    public void testFindAllEnabledOrg() {
	List<TmOrganization> organizations = getOrganizationService().findAllEnabledOrganizations();

	assertNotNull(organizations);
	assertEquals(2, organizations.size());
    }

    @Test
    public void testOrganizationByCurrency() {
	OrganizationSearchRequest searchRequest = new OrganizationSearchRequest();
	searchRequest.setCurrencyCode("GBP");

	TaskPagedList<TmOrganization> taskPagedList = (TaskPagedList<TmOrganization>) getOrganizationService()
		.search(searchRequest, new PagedListInfo());

	TmOrganization[] organizations = taskPagedList.getElements();

	assertNotNull(organizations);
	assertEquals(1, organizations.length);

	assertAREPTaskNames(taskPagedList, EntityTypeHolder.ORGANIZATION);
    }

    @Test
    public void testOrganizationByDomain() {
	OrganizationSearchRequest searchRequest = new OrganizationSearchRequest();
	searchRequest.setDomain("traders.com");
	TmOrganization[] organizations = (TmOrganization[]) getOrganizationService()
		.search(searchRequest, new PagedListInfo()).getElements();
	assertNotNull(organizations);
	assertEquals(1, organizations.length);

    }

    @Test
    public void testOrganizationByName() {
	OrganizationSearchRequest searchRequest = new OrganizationSearchRequest();
	searchRequest.setName("Traders");
	TmOrganization[] organizations = (TmOrganization[]) getOrganizationService()
		.search(searchRequest, new PagedListInfo()).getElements();
	assertNotNull(organizations);
	assertEquals(1, organizations.length);

    }

}
