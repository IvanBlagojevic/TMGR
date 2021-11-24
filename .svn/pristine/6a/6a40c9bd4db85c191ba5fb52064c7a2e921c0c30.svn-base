package org.gs4tr.termmanager.dao.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.IdentifiableUtils;
import org.gs4tr.foundation.modules.entities.model.OrganizationInfo;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.search.OrganizationSearchRequest;
import org.gs4tr.termmanager.dao.OrganizationDAO;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrganizationSearchDAOHibernateTest extends AbstractSpringDAOIntegrationTest {

    private static final String ORGANIZATION_NAME = "Cysco Systems";

    @Autowired
    private OrganizationDAO _organizationaDAO;

    @Test
    public void addOrganizationHierarchy() {

	TmOrganization parentOrganization = getOrganizationaDAO().findById(new Long(1));
	OrganizationInfo organizationInfo = new OrganizationInfo();
	organizationInfo.setName(ORGANIZATION_NAME);
	TmOrganization newOrganization = new TmOrganization();
	newOrganization.setOrganizationInfo(organizationInfo);
	Set<TmOrganization> childOrganizationsList = parentOrganization.getChildOrganizations();
	childOrganizationsList.add(newOrganization);
	parentOrganization.setChildOrganizations(childOrganizationsList);
	newOrganization.setParentOrganization(parentOrganization);
	newOrganization = getOrganizationaDAO().save(newOrganization);
	assertEquals(1, newOrganization.getParentOrganization().getChildOrganizations().size());

	Set<TmOrganization> childs = getOrganizationaDAO().findById(new Long(1)).getChildOrganizations();

	assertEquals(1, childs.size());
    }

    @Test
    public void addProjectToOrganization() {
	TmOrganization parentOrganization = getOrganizationaDAO().findById(new Long(1));
	TmProject project = getProjectDAO().findById(new Long(1));
	Set<TmProject> projects = new HashSet<TmProject>();
	projects.add(project);

	OrganizationInfo organizationInfo = new OrganizationInfo();
	organizationInfo.setName(ORGANIZATION_NAME);
	TmOrganization newOrganization = new TmOrganization();
	newOrganization.setOrganizationInfo(organizationInfo);
	newOrganization.setProjects(projects);

	Set<TmOrganization> childOrganizationsList = parentOrganization.getChildOrganizations();
	childOrganizationsList.add(newOrganization);
	parentOrganization.setChildOrganizations(childOrganizationsList);
	newOrganization.setParentOrganization(parentOrganization);
	newOrganization = getOrganizationaDAO().save(newOrganization);
	assertEquals(1, newOrganization.getParentOrganization().getChildOrganizations().size());

	Set<TmOrganization> childs = getOrganizationaDAO().findById(new Long(1)).getChildOrganizations();

	assertEquals(1, childs.size());

	TmOrganization childOrganization = childs.toArray(new TmOrganization[childs.size()])[0];

	List<TmProject> newProjects = getOrganizationaDAO()
		.findOrganizationProjects((Long) IdentifiableUtils.getEntityId(childOrganization));

	assertEquals(1, newProjects.size());
    }

    @Test
    public void addUserToOrganization() {
	OrganizationSearchRequest searchRequest = new OrganizationSearchRequest();
	TmOrganization[] organizations = (TmOrganization[]) getOrganizationSearchDAO()
		.getEntityPagedList(searchRequest, new PagedListInfo()).getElements();
	assertNotNull(organizations);
	TmOrganization organization = organizations[0];
	TmUserProfile user = getUserProfileDAO().findById(new Long(1));
	Set<TmUserProfile> users = organization.getUsers();
	if (users == null) {
	    users = new HashSet<TmUserProfile>();
	}
	user.setOrganization(organization);

	if (!users.contains(user)) {
	    users.add(user);
	}

	getOrganizationaDAO().update(organization);
	getUserProfileDAO().update(user);
	flushSession();
	TmUserProfile newUser = getUserProfileDAO().findById(new Long(1));
	TmOrganization newOrganization = newUser.getOrganization();
	assertNotNull(newOrganization);

    }

    public OrganizationDAO getOrganizationaDAO() {
	return _organizationaDAO;
    }

    @Test
    public void testOrganizationByCurrency() {
	OrganizationSearchRequest searchRequest = new OrganizationSearchRequest();
	searchRequest.setCurrencyCode("GBP");
	TmOrganization[] organizations = (TmOrganization[]) getOrganizationSearchDAO()
		.getEntityPagedList(searchRequest, new PagedListInfo()).getElements();
	assertNotNull(organizations);
	assertEquals(1, organizations.length);

    }

    @Test
    public void testOrganizationByDomain() {
	OrganizationSearchRequest searchRequest = new OrganizationSearchRequest();
	searchRequest.setDomain("traders.com");
	TmOrganization[] organizations = (TmOrganization[]) getOrganizationSearchDAO()
		.getEntityPagedList(searchRequest, new PagedListInfo()).getElements();
	assertNotNull(organizations);
	assertEquals(1, organizations.length);

    }

    @Test
    public void testOrganizationByName() {
	OrganizationSearchRequest searchRequest = new OrganizationSearchRequest();
	searchRequest.setName("Traders");
	TmOrganization[] organizations = (TmOrganization[]) getOrganizationSearchDAO()
		.getEntityPagedList(searchRequest, new PagedListInfo()).getElements();
	assertNotNull(organizations);
	assertEquals(1, organizations.length);

    }

}
