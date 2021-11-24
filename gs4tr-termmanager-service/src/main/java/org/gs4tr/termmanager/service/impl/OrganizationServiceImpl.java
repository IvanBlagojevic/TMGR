package org.gs4tr.termmanager.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.foundation.modules.entities.model.IdentifiableUtils;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.search.OrganizationSearchRequest;
import org.gs4tr.foundation.modules.organization.service.impl.BaseOrganizationServiceImpl;
import org.gs4tr.termmanager.dao.OrganizationDAO;
import org.gs4tr.termmanager.dao.ProjectDAO;
import org.gs4tr.termmanager.dao.ProjectUserLanguageDAO;
import org.gs4tr.termmanager.dao.UserProfileDAO;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.OrganizationService;
import org.gs4tr.termmanager.service.utils.AdminTasksHolderHelper;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("organizationService")
public class OrganizationServiceImpl extends BaseOrganizationServiceImpl<TmOrganization>
	implements OrganizationService {

    @Autowired
    private OrganizationDAO _organizationDAO;

    @Autowired
    private ProjectDAO _projectDAO;

    @Autowired
    private ProjectUserLanguageDAO _projectUserLanguageDAO;

    @Autowired
    private AdminTasksHolderHelper _tasksHolderHelper;

    @Autowired
    private UserProfileDAO _userProfileDAO;

    @Override
    @Transactional
    public Long addProjectToOrganization(Long organizationId, Long projectId) {
	ProjectDAO projectDAO = getProjectDAO();

	TmProject project = projectDAO.load(projectId);

	TmOrganization organization = findById(organizationId);

	TmOrganization oldOrganization = project.getOrganization();

	if (oldOrganization == null || !oldOrganization.equals(organization)) {
	    if (oldOrganization != null) {
		Set<TmProject> oldOrganizationProjects = oldOrganization.getProjects();

		oldOrganizationProjects.remove(project);

		update(oldOrganization);
	    }

	    project.setOrganization(organization);

	    projectDAO.update(project);

	    Set<TmProject> projects = organization.getProjects();

	    projects.add(project);

	    update(organization);
	}

	return IdentifiableUtils.<Long, TmOrganization> getEntityId(organization);

    }

    @Override
    @Transactional
    public Long assignOrganizationToUser(Long organizationId, Long userId) {

	UserProfileDAO userProfileDAO = getUserProfileDAO();

	TmUserProfile user = userProfileDAO.load(userId);

	TmOrganization previousOrganization = user.getOrganization();
	if (previousOrganization != null) {
	    Set<TmProject> userProjects = previousOrganization.getProjects();

	    removeUserFromProjects(userId, userProjects);
	}

	TmOrganization organization = findById(organizationId);

	Set<TmUserProfile> users = organization.getUsers();

	if (users == null) {
	    users = new HashSet<TmUserProfile>();

	    organization.setUsers(users);
	}

	user.setOrganization(organization);

	users.add(user);

	userProfileDAO.update(user);
	update(organization);

	return userId;
    }

    @Transactional
    public Long assignProjectsToOrganization(Long organizationId, Long[] projectIds) {

	TmOrganization organization = findById(organizationId);

	ProjectDAO projectDAO = getProjectDAO();

	Set<TmProject> projects = organization.getProjects();

	for (Long projectId : projectIds) {
	    TmProject project = projectDAO.load(projectId);

	    if (!projects.contains(project)) {
		project.setOrganization(organization);
		projectDAO.update(project);

		projects.add(project);
	    }
	}

	update(organization);

	return organizationId;
    }

    @Override
    @Transactional
    public void enableOrganizationUsers(Long organizationId, boolean enabled) {
	getOrganizationDAO().enableOrganizationUsers(organizationId, enabled);

	List<TmOrganization> allOrganizations = findAllWithDependants();

	TmOrganization organization = ServiceUtils.findOrganizationById(allOrganizations, organizationId);
	organization.getOrganizationInfo().setEnabled(enabled);

	updateAllChildOrganizations(organization, enabled);

	update(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmOrganization> findAllEnabledOrganizations() {
	return getOrganizationDAO().findAllByProperty("organizationInfo.enabled", Boolean.TRUE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmOrganization> findAllWithDependants() {
	return getOrganizationDAO().findAllWithDependants();
    }

    @Override
    @Transactional(readOnly = true)
    public TmOrganization findByName(String organizationName) {
	if (StringUtils.isBlank(organizationName)) {
	    throw new RuntimeException(Messages.getString("OrganizationServiceImpl.0")); //$NON-NLS-1$
	}

	return getOrganizationDAO().findByName(organizationName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmUserProfile> getAllChildrenOrganizationUsers(Long organizationId) {
	TmOrganization organization = findById(organizationId);
	List<TmUserProfile> users = new ArrayList<TmUserProfile>();
	Set<TmOrganization> childOrganizations = organization.getChildOrganizations();
	for (TmOrganization childOrganization : childOrganizations) {
	    if (childOrganization != null && CollectionUtils.isNotEmpty(childOrganization.getUsers())) {
		users.addAll(childOrganization.getUsers());
	    }
	}

	return users;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmUserProfile> getAllParentOrganizationUsers(Long organizationId) {
	TmOrganization organization = findById(organizationId);
	List<TmUserProfile> users = new ArrayList<TmUserProfile>();
	TmOrganization parentOrganization = organization.getParentOrganization();
	while (parentOrganization != null) {
	    users.addAll(parentOrganization.getUsers());

	    parentOrganization = parentOrganization.getParentOrganization();
	}

	return users;
    }

    public OrganizationDAO getOrganizationDAO() {
	return _organizationDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getOrganizationIdByUserId(Long userId) {
	return getOrganizationDAO().getOrganizationIdByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public String getOrganizationNameByUserId(Long userId) {
	return getOrganizationDAO().getOrganizationNameByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Set<TmProject> getOrganizationProjects(Long projectId) {

	TmProject project = getProjectDAO().load(projectId);

	return project.getOrganization().getProjects();
    }

    public ProjectDAO getProjectDAO() {
	return _projectDAO;
    }

    public ProjectUserLanguageDAO getProjectUserLanguageDAO() {
	return _projectUserLanguageDAO;
    }

    public AdminTasksHolderHelper getTasksHolderHelper() {
	return _tasksHolderHelper;
    }

    public UserProfileDAO getUserProfileDAO() {
	return _userProfileDAO;
    }

    @Transactional(readOnly = true)
    public TaskPagedList<TmOrganization> search(OrganizationSearchRequest command, PagedListInfo pagedListInfo) {
	PagedList<TmOrganization> entityPagedList = getOrganizationSearchDAO().getEntityPagedList(command,
		pagedListInfo);
	return getTasksHolderHelper().assignTasks(entityPagedList, null, EntityTypeHolder.ORGANIZATION);
    }

    @Override
    @Value("#{organizationDAO}")
    public void setEntityDao(GenericDao<TmOrganization, Long> entityDao) {
	super.setEntityDao(entityDao);
    }

    private void removeUserFromProjects(Long userId, Set<TmProject> userProjects) {
	ProjectUserLanguageDAO projectUserLanguageDAO = getProjectUserLanguageDAO();
	for (TmProject project : userProjects) {
	    List<ProjectUserLanguage> projectUserLanguages = projectUserLanguageDAO
		    .getProjectUserLanguages(project.getProjectId(), userId);
	    for (ProjectUserLanguage projectUserLanguage : projectUserLanguages) {
		projectUserLanguageDAO.delete(projectUserLanguage);
	    }
	}
    }

    private void updateAllChildOrganizations(TmOrganization organization, boolean enabled) {
	Set<TmOrganization> childOrganizations = organization.getChildOrganizations();

	if (CollectionUtils.isEmpty(childOrganizations)) {
	    return;
	}

	for (TmOrganization childOrganization : childOrganizations) {
	    childOrganization.getOrganizationInfo().setEnabled(enabled);
	    getOrganizationDAO().enableOrganizationUsers(childOrganization.getOrganizationId(), enabled);
	    update(childOrganization);

	    updateAllChildOrganizations(childOrganization, enabled);
	}
    }

    @Override
    protected TmOrganization createBaseOrganizationInstance() {
	return new TmOrganization();
    }
}
