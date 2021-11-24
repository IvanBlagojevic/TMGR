package org.gs4tr.termmanager.dao.hibernate;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.dao.hibernate.AbstractHibernateGenericDao;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.termmanager.dao.ProjectUserDetailDAO;
import org.gs4tr.termmanager.model.ProjectUserDetail;
import org.gs4tr.termmanager.model.ProjectUserDetailIO;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository("projectUserDetailDAO")
public class ProjectUserDetailDAOImpl extends AbstractHibernateGenericDao<ProjectUserDetail, Long>
	implements ProjectUserDetailDAO {

    protected final Log LOGGER = LogFactory.getLog(getClass());

    public ProjectUserDetailDAOImpl() {
	super(ProjectUserDetail.class, Long.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProjectUserDetail> findByProjectId(final Long projectId) {
	HibernateCallback<List<ProjectUserDetail>> cb = (session) -> {

	    Query query = session.getNamedQuery("ProjectUserDetail.findByProjectId");
	    query.setLong("projectId", projectId);

	    return query.list();
	};

	return execute(cb);
    }

    @Override
    public ProjectUserDetail findByUserAndProject(final Long userId, final Long projectId) {
	HibernateCallback<ProjectUserDetail> cb = (session) -> {

	    Query query = session.getNamedQuery("ProjectUserDetail.findByUserAndProjectId");
	    query.setLong("userId", userId);
	    query.setLong("projectId", projectId);

	    return (ProjectUserDetail) query.uniqueResult();
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProjectUserDetail> findByUsersAndProject(final List<Long> userIds, final Long projectId) {
	HibernateCallback<List<ProjectUserDetail>> cb = (session) -> {

	    Query query = session.getNamedQuery("ProjectUserDetail.findByUsersAndProjectId");
	    query.setLong("projectId", projectId);
	    query.setParameterList("userIds", userIds);

	    return query.list();
	};

	return execute(cb);
    }

    @Override
    public boolean incrementalUpdateProjectDetail(Long userId, ProjectDetailInfo info, Date dateModified) {

	long activeSubmissionCount = info.getUserTotalCount(userId, info.getUserActiveSubmissionCount());
	long completedSubmissionCount = info.getUserTotalCount(userId, info.getUserCompletedSubmissionCount());

	HibernateCallback<Integer> cb = (session) -> {
	    Query query = session.getNamedQuery("ProjectUserDetail.incrementalUpdate");
	    query.setLong("activeSubmissionCount", activeSubmissionCount);
	    query.setLong("completedSubmissionCount", completedSubmissionCount);
	    query.setLong("userId", userId);
	    query.setLong("projectId", info.getProjectId());
	    query.setTimestamp("dateModified", dateModified);

	    return query.executeUpdate();
	};

	return execute(cb) == 1;
    }

    @Override
    public boolean incrementalUpdateProjectDetail(ProjectUserDetailIO info) {

	Session session = null;
	Transaction transaction = null;
	int result = 0;

	try {
	    session = getSessionFactory().openSession();
	    transaction = session.beginTransaction();

	    Query query = session.getNamedQuery("ProjectUserDetail.incrementalUpdate");
	    query.setLong("activeSubmissionCount", info.getActiveSubmissionCount());
	    query.setLong("completedSubmissionCount", info.getCompletedSubmissionCount());
	    query.setLong("userId", info.getUserId());
	    query.setLong("projectId", info.getProjectId());
	    query.setTimestamp("dateModified", info.getDateModified());

	    result = query.executeUpdate();

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

	return result == 1;

    }
}
