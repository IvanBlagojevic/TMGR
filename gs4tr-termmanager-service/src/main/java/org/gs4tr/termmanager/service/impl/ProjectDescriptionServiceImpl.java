package org.gs4tr.termmanager.service.impl;

import org.gs4tr.termmanager.dao.ProjectDescriptionDAO;
import org.gs4tr.termmanager.model.ProjectDescription;
import org.gs4tr.termmanager.service.ProjectDescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("projectDescriptionService")
public class ProjectDescriptionServiceImpl implements ProjectDescriptionService {

    @Autowired
    private ProjectDescriptionDAO _projectDescriptionDAO;

    @Override
    @Transactional
    public void deleteByProjectId(Long projectId) {
	ProjectDescriptionDAO dao = getProjectDescriptionDAO();
	dao.deleteByProjectId(projectId);
	dao.updateProjectAvailableDescription(false, projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDescription findByProjectId(Long projectId) {
	return getProjectDescriptionDAO().findByProjectId(projectId);
    }

    @Override
    @Transactional
    public void saveOrUpdateProjectDescription(ProjectDescription description) {
	ProjectDescriptionDAO dao = getProjectDescriptionDAO();
	dao.saveOrUpdate(description);
	dao.updateProjectAvailableDescription(true, description.getTmProject().getProjectId());
    }

    private ProjectDescriptionDAO getProjectDescriptionDAO() {
	return _projectDescriptionDAO;
    }

}
