package org.gs4tr.termmanager.dao.hibernate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gs4tr.foundation.modules.dao.hibernate.AbstractHibernateGenericDao;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.termmanager.dao.ProjectUserLanguageDAO;
import org.gs4tr.termmanager.dao.utils.DaoUtils;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository("projectUserLanguageDAO")
public class ProjectUserLanguageDAOImpl extends AbstractHibernateGenericDao<ProjectUserLanguage, Long>
	implements ProjectUserLanguageDAO {

    public ProjectUserLanguageDAOImpl() {
	super(ProjectUserLanguage.class, Long.class);
    }

    @SuppressWarnings("unchecked")
    public List<ProjectUserLanguage> findByProjectIds(final List<Long> projectIds, final Class<?>... classesToFetch) {
	HibernateCallback<List<ProjectUserLanguage>> cb = new HibernateCallback<List<ProjectUserLanguage>>() {
	    @Override
	    public List<ProjectUserLanguage> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("ProjectUserLanguage.findByProjectIds");
		query.setParameterList("projectIds", projectIds);

		List<ProjectUserLanguage> projectUserLanguage = query.list();

		if (classesToFetch != null) {
		    for (ProjectUserLanguage pul : projectUserLanguage) {
			DaoUtils.initializeEntities(pul, classesToFetch);
		    }
		}

		return projectUserLanguage;
	    }
	};

	return (List<ProjectUserLanguage>) execute(cb);
    }

    @SuppressWarnings("unchecked")
    public List<TmUserProfile> getAllProjectUsers(final Long projectId) {
	HibernateCallback<List<TmUserProfile>> cb = new HibernateCallback<List<TmUserProfile>>() {

	    @Override
	    public List<TmUserProfile> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("ProjectUserLanguage.findUsersByProject");
		query.setLong("projectId", projectId);

		return query.list();
	    }

	};
	return (List<TmUserProfile>) execute(cb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TmProject> getAllUserProjects(final Long userId, final Class<?>... classesToFetch) {
	HibernateCallback<List<TmProject>> cb = new HibernateCallback<List<TmProject>>() {

	    @Override
	    public List<TmProject> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("ProjectUserLanguage.findProjectsByUser");
		query.setLong("userId", userId);
		List<TmProject> projects = (List<TmProject>) query.list();

		for (TmProject tmProject : projects) {
		    DaoUtils.initializeEntities(tmProject, classesToFetch);
		}

		return projects;
	    }
	};
	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    public List<TmProject> getAllUserProjectsScoped(final Long userId) {
	HibernateCallback<List<TmProject>> cb = new HibernateCallback<List<TmProject>>() {
	    @Override
	    public List<TmProject> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("ProjectUserLanguage.findProjectsByUserOrdered");
		query.setLong("userId", userId);
		query.setFirstResult(0);
		query.setMaxResults(5);

		return query.list();
	    }
	};
	return execute(cb);
    }

    @Override
    public List<ProjectUserLanguage> getProjectUserLanguages(Long projectId, Long userId) {
	return findProjectUserLanguagesByUserAndProject(userId, projectId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProjectUserLanguage> getProjectUserLanguagesByProject(final Long projectId) {
	HibernateCallback<List<ProjectUserLanguage>> cb = new HibernateCallback<List<ProjectUserLanguage>>() {

	    @Override
	    public List<ProjectUserLanguage> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("ProjectUserLanguage.findProjectUserLanguagesByProject");
		query.setLong("projectId", projectId);

		return query.list();
	    }

	};
	return (List<ProjectUserLanguage>) execute(cb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProjectUserLanguage> getProjectUserLanguagesByUser(final Long userId) {
	HibernateCallback<List<ProjectUserLanguage>> cb = new HibernateCallback<List<ProjectUserLanguage>>() {

	    @Override
	    public List<ProjectUserLanguage> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("ProjectUserLanguage.findProjectUserLanguagesByUser");
		query.setLong("userId", userId);

		return query.list();
	    }

	};
	return (List<ProjectUserLanguage>) execute(cb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TmUserProfile> getProjectUsersByLanguageIds(Long projectId, List<String> languageIds) {
	HibernateCallback<List<TmUserProfile>> cb = new HibernateCallback<List<TmUserProfile>>() {

	    @Override
	    public List<TmUserProfile> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("ProjectUserLanguage.findUsersByProjectAndLanguageIds");
		query.setLong("projectId", projectId);
		query.setParameterList("languageIds", languageIds);

		return query.list();
	    }

	};
	return execute(cb);
    }

    @Override
    public Map<Long, List<String>> getUserLanguagesMap(Long projectId) {
	HibernateCallback<Map<Long, List<String>>> cb = new HibernateCallback<Map<Long, List<String>>>() {
	    @Override
	    public Map<Long, List<String>> doInHibernate(Session session) throws HibernateException, SQLException {
		Map<Long, List<String>> map = new HashMap<Long, List<String>>();

		Query query = session.getNamedQuery("ProjectUserLanguage.getUserLanguagesMap");
		query.setLong("projectId", projectId);

		List<Map<String, Object>> queryResult = query.list();

		for (Map<String, Object> item : queryResult) {
		    String languageId = (String) item.get("languageId");
		    Long userProfileId = (Long) item.get("userProfileId");

		    List<String> languageIds = map.computeIfAbsent(userProfileId, k -> new ArrayList<>());
		    languageIds.add(languageId);
		}
		return map;
	    }
	};
	return execute(cb);
    }

    @Override
    public boolean recodeProjectUserLanguage(Long projectId, String languageFrom, String languageTo) {
	HibernateCallback<Integer> cb = (session) -> {
	    Query query = session.getNamedQuery("ProjectUserLanguage.recodeProjectUserLanguage");
	    query.setString("languageFrom", languageFrom);
	    query.setString("languageTo", languageTo);
	    query.setLong("projectId", projectId);

	    return query.executeUpdate();
	};

	return execute(cb) == 1;

    }

    @SuppressWarnings("unchecked")
    private List<ProjectUserLanguage> findProjectUserLanguagesByUserAndProject(final Long userId,
	    final Long projectId) {
	HibernateCallback<List<ProjectUserLanguage>> cb = new HibernateCallback<List<ProjectUserLanguage>>() {

	    @Override
	    public List<ProjectUserLanguage> doInHibernate(Session session) throws HibernateException, SQLException {

		Query query = session.getNamedQuery("ProjectUserLanguage.findByUserAndProject");
		query.setLong("userId", userId);
		query.setLong("projectId", projectId);
		return query.list();

	    }
	};
	return (List<ProjectUserLanguage>) execute(cb);
    }

}
