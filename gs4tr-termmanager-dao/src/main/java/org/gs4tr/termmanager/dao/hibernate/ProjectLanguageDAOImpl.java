package org.gs4tr.termmanager.dao.hibernate;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.dao.hibernate.AbstractHibernateGenericDao;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.termmanager.dao.ProjectLanguageDAO;
import org.gs4tr.termmanager.model.ProjectLanguage;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository("projectLanguageDAO")
public class ProjectLanguageDAOImpl extends AbstractHibernateGenericDao<ProjectLanguage, Long>
	implements ProjectLanguageDAO {

    protected final Log LOGGER = LogFactory.getLog(getClass());

    public ProjectLanguageDAOImpl() {
	super(ProjectLanguage.class, Long.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProjectLanguage> findByProjectId(final Long projectId) {
	HibernateCallback<List<ProjectLanguage>> cb = new HibernateCallback<List<ProjectLanguage>>() {
	    @Override
	    public List<ProjectLanguage> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("ProjectLanguage.findByProjectId");

		query.setLong("projectId", projectId);

		return query.list();
	    }
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProjectLanguage> findByProjectIds(final List<Long> projectIds) {
	HibernateCallback<List<ProjectLanguage>> cb = new HibernateCallback<List<ProjectLanguage>>() {
	    @Override
	    public List<ProjectLanguage> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("ProjectLanguage.findByProjectIds");

		query.setParameterList("projectIds", projectIds);

		return query.list();
	    }
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getLanguageIdsByProjectId(long projectId) {

	Session session = null;
	Transaction transaction = null;

	Set<String> languageIds = new HashSet<>();

	try {
	    session = getSessionFactory().openSession();
	    transaction = session.beginTransaction();

	    Query query = session.getNamedQuery("ProjectLanguage.getLanguageIdsByProjectId");
	    query.setLong("projectId", projectId);

	    List<String> list = query.list();

	    if (CollectionUtils.isNotEmpty(list)) {
		languageIds.addAll(list);
	    }

	    transaction.commit();
	    session.flush();
	    session.clear();

	} catch (Exception e) {
	    LOGGER.error(e, e);
	    if (transaction != null) {
		transaction.rollback();
	    }
	} finally {
	    if (session != null) {
		session.close();
	    }
	}

	return languageIds;

    }

    @Override
    public Map<Long, Set<String>> getProjectLanguagesMap() {
	HibernateCallback<Map<Long, Set<String>>> cb = new HibernateCallback<Map<Long, Set<String>>>() {
	    @Override
	    public Map<Long, Set<String>> doInHibernate(Session session) throws HibernateException, SQLException {
		Map<Long, Set<String>> map = new HashMap<Long, Set<String>>();

		Query query = session.getNamedQuery("ProjectLanguage.getProjectLanguagesMap");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> queryResult = query.list();
		for (Map<String, Object> item : queryResult) {
		    String languageId = (String) item.get("languageId");
		    Long projectId = (Long) item.get("projectId");

		    Set<String> languageIds = map.get(projectId);
		    if (languageIds == null) {
			languageIds = new HashSet<String>();
			map.put(projectId, languageIds);
		    }

		    languageIds.add(languageId);
		}

		return map;
	    }
	};
	return execute(cb);
    }
}
