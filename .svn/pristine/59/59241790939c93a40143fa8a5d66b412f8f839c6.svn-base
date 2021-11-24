package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.security.model.search.RoleSearchRequest;
import org.junit.Test;

public class RoleServiceTest extends AbstractSpringServiceTests {

    @Test
    public void testAddOrUpdateProjectRolesCase1() {
	Long userId = new Long(3);
	Long projectId = new Long(1);
	List<Role> roles = null;

	Long result = getRoleService().addOrUpdateProjectRoles(userId, projectId, roles);
	assertNotNull(result);
	assertEquals(userId, result);

	List<Role> userProjectRoles = getProjectService().getRolesByUserAndProject(projectId, userId);
	assertTrue(CollectionUtils.isEmpty(userProjectRoles));

    }

    @Test
    public void testAddOrUpdateProjectRolesCase2() {
	Long userId = new Long(3);
	Long projectId = new Long(1);

	List<Role> roles = getProjectService().getRolesByUserAndProject(projectId, userId);

	Long result = getRoleService().addOrUpdateProjectRoles(userId, projectId, roles);
	assertNotNull(result);
	assertEquals(userId, result);

	List<Role> userProjectRoles = getProjectService().getRolesByUserAndProject(projectId, userId);
	assertTrue(CollectionUtils.isNotEmpty(userProjectRoles));
    }

    @Test
    public void testSearchRoles() {
	RoleSearchRequest request = new RoleSearchRequest();
	request.setRoleId("pm");

	PagedListInfo pagedListInfo = new PagedListInfo();

	PagedList<Role> result = getRoleService().search(request, pagedListInfo);
	assertNotNull(result);
	assertNotNull(result.getElements());
	assertTrue(result.getElements().length == 1);
    }
}
