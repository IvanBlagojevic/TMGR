package org.gs4tr.termmanager.dao.hibernate;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.dao.hibernate.AbstractHibernateGenericDao;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.termmanager.dao.SubmissionDAO;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.SubmissionUser;
import org.gs4tr.termmanager.model.TmProject;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository("submissionDAO")
public class SubmissionDAOImpl extends AbstractHibernateGenericDao<Submission, Long> implements SubmissionDAO {

    public SubmissionDAOImpl() {
	super(Submission.class, Long.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Long> findAllSubmissionIds() {
	HibernateCallback<Set<Long>> cb = new HibernateCallback<Set<Long>>() {
	    @Override
	    public Set<Long> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("Submission.findAllSubmissionIds");

		Set<Long> ids = new HashSet<Long>();
		List<Long> list = query.list();
		if (CollectionUtils.isNotEmpty(list)) {
		    ids.addAll(list);
		}
		return ids;
	    }
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Submission> findAllSubmissions() {
	HibernateCallback<List<Submission>> cb = new HibernateCallback<List<Submission>>() {
	    @Override
	    public List<Submission> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("Submission.findAllSubmissions");
		return query.list();
	    }
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TmProject> findProjectsBySubmissionIds(final List<Long> submissionIds) {
	HibernateCallback<List<TmProject>> cb = new HibernateCallback<List<TmProject>>() {
	    @Override
	    public List<TmProject> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("Submission.findProjectsBySubmissionIds");
		query.setParameterList("submissionIds", submissionIds);

		return query.list();
	    }
	};

	return execute(cb);
    }

    @Override
    public Submission findSubmissionByIdFetchChilds(final Long submissionId) {
	HibernateCallback<Submission> cb = new HibernateCallback<Submission>() {
	    @Override
	    public Submission doInHibernate(Session session) throws HibernateException, SQLException {

		Query query = session.getNamedQuery(getFindByIdQueryName());
		query.setLong("entityId", submissionId);

		Submission submission = (Submission) query.uniqueResult();
		if (submission != null) {
		    initializeSubmissionChildEntities(submission);
		    Hibernate.initialize(submission.getProject());
		}
		return submission;
	    }
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Submission> findSubmissionsByIdsFetchChilds(final List<Long> submissionIds) {
	HibernateCallback<List<Submission>> cb = new HibernateCallback<List<Submission>>() {
	    @Override
	    public List<Submission> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery(getFindByIdsQueryName());
		query.setParameterList("entityIds", submissionIds);

		List<Submission> submissions = query.list();
		if (CollectionUtils.isNotEmpty(submissions)) {
		    for (Submission submission : submissions) {
			initializeSubmissionChildEntities(submission);
			Hibernate.initialize(submission.getProject());
		    }
		}
		return submissions;
	    }
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Submission> findSubmissionsByProjectId(final Long projectId) {
	HibernateCallback<List<Submission>> cb = new HibernateCallback<List<Submission>>() {
	    @Override
	    public List<Submission> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("Submission.findByProjectId");
		query.setLong("projectId", projectId);

		return query.list();
	    }
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Submission> findSubmissionsByProjectIds(final Collection<Long> projectIds) {
	HibernateCallback<List<Submission>> cb = new HibernateCallback<List<Submission>>() {
	    @Override
	    public List<Submission> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("Submission.findByProjectIds");
		query.setParameterList("projectIds", projectIds);

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
		Query query = session.getNamedQuery("Submission.updateLanguageByProjectId");
		query.setParameter("languageFrom", languageFrom);
		query.setParameter("languageTo", languageTo);
		query.setParameter("projectId", projectId);
		return query.executeUpdate();
	    }
	};

	return execute(cb);
    }

    private void initializeSubmissionChildEntities(Submission submission) {
	Set<SubmissionUser> submissionUsers = submission.getSubmissionUsers();
	if (CollectionUtils.isNotEmpty(submissionUsers)) {
	    Hibernate.initialize(submissionUsers);
	    for (SubmissionUser submissionUser : submissionUsers) {
		Hibernate.initialize(submissionUser.getUser());
	    }
	}
	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	if (CollectionUtils.isNotEmpty(submissionLanguages)) {
	    Hibernate.initialize(submissionLanguages);
	    for (SubmissionLanguage submissionLanguage : submissionLanguages) {
		Hibernate.initialize(submissionLanguage.getSubmissionLanguageComments());
	    }
	}
    }

    @Override
    protected String getFindByIdQueryName() {
	return "Submission.findById"; //$NON-NLS-1$
    }

    @Override
    protected String getFindByIdsQueryName() {
	return "Submission.findByIds"; //$NON-NLS-1$
    }

    @Override
    protected String getFindByNameQueryName() {
	return "Submission.findByName"; //$NON-NLS-1$
    }
}
