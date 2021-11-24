package org.gs4tr.termmanager.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.termmanager.model.ProjectLanguage;

public interface ProjectLanguageDAO extends GenericDao<ProjectLanguage, Long> {
    List<ProjectLanguage> findByProjectId(Long projectId);

    List<ProjectLanguage> findByProjectIds(List<Long> projectIds);

    Map<Long, Set<String>> getProjectLanguagesMap();

    Set<String> getLanguageIdsByProjectId(long projectId);
}
