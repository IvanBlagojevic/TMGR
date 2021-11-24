package org.gs4tr.termmanager.dao.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.gs4tr.foundation.modules.dao.hibernate.AbstractHibernateGenericDao;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.termmanager.dao.SubmissionLanguageDAO;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository("submissionLanguageDAO")
public class SubmissionLanguageDAOImpl extends AbstractHibernateGenericDao<SubmissionLanguage, Long>
	implements SubmissionLanguageDAO {

    public SubmissionLanguageDAOImpl() {
	super(SubmissionLanguage.class, Long.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SubmissionLanguage> findSubmissionLanguagesBySubmissionId(final Long submissionId) {
	HibernateCallback<List<SubmissionLanguage>> cb = new HibernateCallback<List<SubmissionLanguage>>() {
	    @Override
	    public List<SubmissionLanguage> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("SubmissionLanguage.findBySubmissionId");
		query.setLong("submissionId", submissionId);

		return query.list();
	    }
	};

	return execute(cb);
    }

    @Override
    public int updateLanguageByProjectId(String languageFrom, String languageTo, Long projectId) {
	HibernateCallback<Integer> cb = new HibernateCallback<Integer>() {
	    @Override
	    public Integer doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("SubmissionLanguage.updateLanguageByProjectId");
		query.setParameter("languageFrom", languageFrom);
		query.setParameter("languageTo", languageTo);
		query.setParameter("projectId", projectId);
		return query.executeUpdate();
	    }
	};

	return execute(cb);
    }

    @Override
    protected String getFindByIdQueryName() {
	return "SubmissionLanguage.findById"; //$NON-NLS-1$
    }

    @Override
    protected String getFindByIdsQueryName() {
	return "SubmissionLanguage.findByIds"; //$NON-NLS-1$
    }
}
