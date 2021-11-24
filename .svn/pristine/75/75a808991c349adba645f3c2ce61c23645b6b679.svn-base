package org.gs4tr.termmanager.dao;

import java.util.List;
import java.util.Map;

import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;

public interface ProjectUserLanguageDAO extends GenericDao<ProjectUserLanguage, Long> {

    List<ProjectUserLanguage> findByProjectIds(List<Long> projectIds, Class<?>... classesToFetch);

    List<TmUserProfile> getAllProjectUsers(Long projectId);

    List<TmProject> getAllUserProjects(Long userId, Class<?>... classesToFetch);

    List<TmProject> getAllUserProjectsScoped(Long userId);

    List<ProjectUserLanguage> getProjectUserLanguages(Long projectId, Long userId);

    List<ProjectUserLanguage> getProjectUserLanguagesByProject(Long projectId);

    List<ProjectUserLanguage> getProjectUserLanguagesByUser(Long userId);

    List<TmUserProfile> getProjectUsersByLanguageIds(Long projectId, List<String> languageIds);

    Map<Long, List<String>> getUserLanguagesMap(Long projectId);

    boolean recodeProjectUserLanguage(Long projectId, String languageFrom, String languageTo);

}
