package org.gs4tr.termmanager.dao;

import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.termmanager.model.ProjectDescription;

public interface ProjectDescriptionDAO extends GenericDao<ProjectDescription, Long> {

    void deleteByProjectId(Long projectId);

    ProjectDescription findByProjectId(Long projectId);

    void updateProjectAvailableDescription(boolean value, Long projectId);
}
