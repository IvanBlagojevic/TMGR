package org.gs4tr.termmanager.service.mocking;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.security.dao.RoleSearchDAO;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.security.model.search.RoleSearchRequest;
import org.gs4tr.termmanager.dao.UserProjectRoleSearchDAO;
import org.gs4tr.termmanager.model.UserProjectRole;
import org.gs4tr.termmanager.service.impl.RoleServiceImpl;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("service")
public class RoleServiceImplTest extends AbstractServiceTest {

    @Autowired
    private RoleSearchDAO _roleSearchDAO;

    @Autowired
    private RoleServiceImpl _roleServiceImpl = new RoleServiceImpl();

    @Autowired
    private UserProjectRoleSearchDAO _userProjectRoleSearchDAO;

    @Test
    @TestCase("role")
    public void addOrUpdateProjectRolesTest_01() {
	@SuppressWarnings("unchecked")
	List<Role> roles = getModelObject("roles", List.class);

	Role role = getModelObject("role1", Role.class);
	UserProjectRole userProjectRole = new UserProjectRole();
	userProjectRole.setRole(role);
	List<UserProjectRole> roleList = new ArrayList<UserProjectRole>();
	roleList.add(userProjectRole);

	when(getUserProjectRoleSearchDAO().getAllUserProjectRoles(any(Long.class), any(Long.class)))
		.thenReturn(roleList);

	Long result = getRoleServiceImpl().addOrUpdateProjectRoles(1L, 1L, roles);

	verify(getUserProjectRoleSearchDAO(), atLeastOnce()).getAllUserProjectRoles(any(Long.class), any(Long.class));
	assertTrue(result.equals(1L));
    }

    @Test
    @TestCase("role")
    public void addOrUpdateProjectRolesTest_02() {
	@SuppressWarnings("unchecked")
	List<Role> roles = getModelObject("roles2", List.class);

	Role role = getModelObject("role1", Role.class);
	UserProjectRole userProjectRole = new UserProjectRole();
	userProjectRole.setRole(role);
	List<UserProjectRole> roleList = new ArrayList<UserProjectRole>();
	roleList.add(userProjectRole);

	when(getUserProjectRoleSearchDAO().getAllUserProjectRoles(any(Long.class), any(Long.class)))
		.thenReturn(roleList);

	Long result = getRoleServiceImpl().addOrUpdateProjectRoles(1L, 1L, roles);

	verify(getUserProjectRoleSearchDAO(), atLeastOnce()).getAllUserProjectRoles(any(Long.class), any(Long.class));
	verify(getUserProjectRoleSearchDAO(), atLeastOnce()).delete(any(UserProjectRole.class));
	assertTrue(result.equals(1L));
    }

    @Test
    @TestCase("role")
    public void addOrUpdateProjectRolesTest_03() {
	Role role = getModelObject("role1", Role.class);
	UserProjectRole userProjectRole = new UserProjectRole();
	userProjectRole.setRole(role);
	List<UserProjectRole> roleList = new ArrayList<UserProjectRole>();
	roleList.add(userProjectRole);

	when(getUserProjectRoleSearchDAO().getAllUserProjectRoles(any(Long.class), any(Long.class)))
		.thenReturn(roleList);

	Long result = getRoleServiceImpl().addOrUpdateProjectRoles(1L, 1L, null);

	verify(getUserProjectRoleSearchDAO(), atLeastOnce()).getAllUserProjectRoles(any(Long.class), any(Long.class));
	verify(getUserProjectRoleSearchDAO(), atLeastOnce()).delete(any(UserProjectRole.class));
	assertTrue(result.equals(1L));
    }

    @Before
    public void setUp() throws Exception {
	reset(getRoleSearchDAO());
    }

    @Test
    @TestCase("role")
    public void testSearchRoles() {
	RoleSearchRequest request = new RoleSearchRequest();
	request.setRoleId("pm");

	PagedListInfo pagedListInfo = new PagedListInfo();

	Role role1 = getModelObject("role1", Role.class);
	Role role2 = getModelObject("role2", Role.class);
	Role[] organizations = { role1, role2 };

	PagedList<Role> pagedList = new PagedList<Role>();
	pagedList.setElements(organizations);
	pagedList.setPagedListInfo(new PagedListInfo());
	pagedList.setTotalCount(3L);

	when(getRoleSearchDAO().getEntityPagedList(request, pagedListInfo)).thenReturn(pagedList);

	PagedList<Role> result = getRoleServiceImpl().search(request, pagedListInfo);

	verify(getRoleSearchDAO(), times(1)).getEntityPagedList(request, pagedListInfo);

	assertNotNull(result);
	assertNotNull(result.getElements());
	assertTrue(result.getElements().length == 2);
    }

    private RoleSearchDAO getRoleSearchDAO() {
	return _roleSearchDAO;
    }

    private RoleServiceImpl getRoleServiceImpl() {
	return _roleServiceImpl;
    }

    private UserProjectRoleSearchDAO getUserProjectRoleSearchDAO() {
	return _userProjectRoleSearchDAO;
    }

}
