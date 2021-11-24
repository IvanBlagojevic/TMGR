package org.gs4tr.termmanager.dao.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.security.model.RoleTypeEnum;
import org.gs4tr.foundation.modules.security.model.search.RoleSearchRequest;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.junit.Test;

public class RoleHibernateIntegrationPTest extends AbstractSpringDAOIntegrationTest {

    @Test
    public void testAddRole() {
	TmUserProfile userProfile = getUserProfileDAO().findById(new Long(1));
	Role newRole = new Role();
	newRole.setRoleId("corrector");
	newRole.setRoleType(RoleTypeEnum.CONTEXT);
	userProfile.addRole(newRole);
	getRoleDAO().save(newRole);

	flushSession();

	List<Role> roleList = getRoleDAO().findAll();
	assertEquals(9, roleList.size());

    }

    @Test
    public void testfindRoleById() {

	String roleName = "pm";
	Role role = getRoleDAO().findById(roleName);
	assertNotNull(role);
	Set<Policy> policies = role.getPolicies();
	assertEquals(3, policies.size());

    }

    @Test
    public void testFindRolesByType() {
	RoleTypeEnum type = RoleTypeEnum.CONTEXT;
	List<Role> roles = getRoleSearchDAO().findRolesByType(type);
	assertEquals(5, roles.size());
	roles = getRoleSearchDAO().findRolesByType(RoleTypeEnum.SYSTEM);
	assertEquals(3, roles.size());
    }

    @Test
    public void testRoleSearchById() {
	RoleSearchRequest request = new RoleSearchRequest();
	request.setRoleId("admin");

	Role[] roles = getRoleSearchDAO().getEntityPagedList(request, new PagedListInfo()).getElements();

	assertNotNull(roles);
	assertEquals(1, roles.length);
    }

    @Test
    public void testRoleSearchByType() {
	RoleSearchRequest request = new RoleSearchRequest();
	request.setRoleType(RoleTypeEnum.SYSTEM);

	Role[] roles = getRoleSearchDAO().getEntityPagedList(request, new PagedListInfo()).getElements();

	assertNotNull(roles);
	assertEquals(3, roles.length);
	request.setRoleType(RoleTypeEnum.CONTEXT);
	roles = getRoleSearchDAO().getEntityPagedList(request, new PagedListInfo()).getElements();
	assertEquals(5, roles.length);
    }

    @Test
    public void testRoleSearchByTypeAndId() {
	RoleSearchRequest request = new RoleSearchRequest();
	request.setRoleType(RoleTypeEnum.SYSTEM);
	request.setRoleId("pm");

	Role[] roles = getRoleSearchDAO().getEntityPagedList(request, new PagedListInfo()).getElements();

	assertNotNull(roles);
	assertEquals(1, roles.length);
    }

    @Test
    public void testUserRole() {
	TmUserProfile userProfile = getUserProfileDAO().findById(new Long(1));
	assertNotNull(userProfile);
	Set<Role> userRoles = userProfile.getSystemRoles();
	assertEquals(1, userRoles.size());
    }

}
