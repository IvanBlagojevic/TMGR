package org.gs4tr.termmanager.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.security.dao.PolicyDAO;
import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.security.model.search.RoleSearchRequest;
import org.gs4tr.foundation.modules.security.service.impl.BaseRoleServiceImpl;
import org.gs4tr.termmanager.dao.ProjectDAO;
import org.gs4tr.termmanager.dao.UserProfileDAO;
import org.gs4tr.termmanager.dao.UserProjectRoleSearchDAO;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UserProjectRole;
import org.gs4tr.termmanager.service.RoleService;
import org.gs4tr.termmanager.service.utils.AdminTasksHolderHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl extends BaseRoleServiceImpl implements RoleService {

    @Autowired
    private ProjectDAO _projectDAO;

    @Autowired
    private PolicyDAO _policyDAO;

    @Autowired
    private AdminTasksHolderHelper _tasksHolderHelper;

    @Autowired
    private UserProfileDAO _userProfileDAO;

    @Autowired
    private UserProjectRoleSearchDAO _userProjectRoleSearchDAO;

    @Transactional
    public Long addOrUpdateProjectRoles(Long userId, Long projectId, List<Role> roles) {

	List<UserProjectRole> userProjectRoles = getUserProjectRoleSearchDAO().getAllUserProjectRoles(userId,
		projectId);

	if (roles == null) {
	    for (UserProjectRole uProjectRole : userProjectRoles) {
		getUserProjectRoleSearchDAO().delete(uProjectRole);
	    }
	    return userId;
	}

	List<Role> rolesForSkipFromCreation = new ArrayList<Role>();

	for (UserProjectRole uProjectRole : userProjectRoles) {

	    Role role = new Role(uProjectRole.getRole());

	    if (!roles.contains(role)) {
		getUserProjectRoleSearchDAO().delete(uProjectRole);
	    } else {
		rolesForSkipFromCreation.add(role);
	    }
	}

	roles.removeAll(rolesForSkipFromCreation);
	for (Role role : roles) {

	    TmUserProfile userProfile = getUserProfileDAO().load(userId);

	    TmProject project = getProjectDAO().load(projectId);

	    UserProjectRole newUserProjectRole = new UserProjectRole(project, role, userProfile);

	    getUserProjectRoleSearchDAO().save(newUserProjectRole);

	}
	return userId;

    }

    @Override
    @Transactional(readOnly = true)
    public List<Policy> findAllPolicies() {
	return getPolicyDAO().findAll();
    }

    @Transactional(readOnly = true)
    public List<Role> getUserProjectRoles(Long userId, Long projectId) {

	List<Role> roles = getUserProjectRoleSearchDAO().getRolesByUserAndProject(userId, projectId);

	return roles;
    }

    @Transactional(readOnly = true)
    public TaskPagedList<Role> search(RoleSearchRequest request, PagedListInfo pagedListInfo) {

	PagedList<Role> entityPagedList = getRoleSearchDAO().getEntityPagedList(request, pagedListInfo);
	return getTasksHolderHelper().assignTasks(entityPagedList, null, EntityTypeHolder.ROLE);
    }

    @Override
    @Value("#{roleDAO}")
    public void setEntityDao(GenericDao<Role, String> entityDao) {
	super.setEntityDao(entityDao);
    }

    private PolicyDAO getPolicyDAO() {
	return _policyDAO;
    }

    private ProjectDAO getProjectDAO() {
	return _projectDAO;
    }

    private AdminTasksHolderHelper getTasksHolderHelper() {
	return _tasksHolderHelper;
    }

    private UserProfileDAO getUserProfileDAO() {
	return _userProfileDAO;
    }

    private UserProjectRoleSearchDAO getUserProjectRoleSearchDAO() {
	return _userProjectRoleSearchDAO;
    }
}
