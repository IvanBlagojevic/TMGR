package org.gs4tr.termmanager.service.impl;

import static java.util.Objects.nonNull;

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.event.SubmissionDetailInfo;
import org.gs4tr.termmanager.service.SubmissionDetailService;
import org.gs4tr.termmanager.service.lock.manager.ExclusiveWriteLockManager;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("submissionDetailService")
public class SubmissionDetailServiceImpl implements SubmissionDetailService {

    private static final String ENTITY_ID_KEY = "entityId";

    private static final String FIND_BY_ID = "Submission.findById";

    private static final Log LOG = LogFactory.getLog(SubmissionDetailServiceImpl.class);

    @Autowired
    private ExclusiveWriteLockManager _exclusiveWriteLockManager;

    @Autowired
    private SessionFactory _sessionFactory;

    public SessionFactory getSessionFactory() {
	return _sessionFactory;
    }

    @Override
    public void updateSubmissionDetail(SubmissionDetailInfo detailInfo) {
	if (Objects.isNull(detailInfo)) {
	    return;
	}
	Long submissionId = detailInfo.getSubmissionId();
	String username = TmUserProfile.getCurrentUserName();

	getExclusiveWriteLockManager().acquireLock(submissionId, username);

	Transaction transaction = null;
	Session session = null;
	try {
	    session = getSessionFactory().openSession();
	    transaction = session.beginTransaction();
	    Query query = session.getNamedQuery(FIND_BY_ID);
	    query.setLong(ENTITY_ID_KEY, submissionId);
	    Submission submission = (Submission) query.uniqueResult();
	    submission.notifyObservers(detailInfo);
	    session.update(submission);
	    session.flush();
	    session.clear();
	    transaction.commit();
	} catch (Exception e) {
	    LOG.error(e, e);
	    if (nonNull(transaction)) {
		transaction.rollback();
	    }
	    throw new RuntimeException(e.getMessage(), e);
	} finally {
	    if (session != null) {
		session.close();
	    }
	    getExclusiveWriteLockManager().releaseLock(submissionId, username);
	}
    }

    private ExclusiveWriteLockManager getExclusiveWriteLockManager() {
	return _exclusiveWriteLockManager;
    }
}
