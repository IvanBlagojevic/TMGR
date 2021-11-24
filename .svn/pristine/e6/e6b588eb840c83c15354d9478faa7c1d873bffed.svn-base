package org.gs4tr.termmanager.dao.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.gs4tr.foundation.modules.dao.hibernate.AbstractHibernateGenericDao;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.termmanager.dao.SubmissionUserDAO;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionUser;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository("submissionUserDAO")
public class SubmissionUserDAOImpl extends AbstractHibernateGenericDao<SubmissionUser, Long>
	implements SubmissionUserDAO {

    public SubmissionUserDAOImpl() {
	super(SubmissionUser.class, Long.class);
    }

    @Override
    public Long findBySubmissionAndUserId(final Long submissionId, final Long userId) {
	HibernateCallback<Long> cb = new HibernateCallback<Long>() {
	    @Override
	    public Long doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("SubmissionUser.findBySubmissionAndUserId");
		query.setLong("userId", userId);
		query.setLong("submissionId", submissionId);

		return (Long) query.uniqueResult();
	    }
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Submission> findSubmissionsByUserId(final Long userId) {
	HibernateCallback<List<Submission>> cb = new HibernateCallback<List<Submission>>() {
	    @Override
	    public List<Submission> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("SubmissionUser.findSubmissionsByUserId");
		query.setLong("userId", userId);

		return query.list();
	    }
	};

	return execute(cb);
    }
}
