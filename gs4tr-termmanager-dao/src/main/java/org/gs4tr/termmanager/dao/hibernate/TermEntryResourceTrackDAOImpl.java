package org.gs4tr.termmanager.dao.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.gs4tr.foundation.modules.dao.hibernate.AbstractHibernateGenericDao;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.termmanager.dao.TermEntryResourceTrackDAO;
import org.gs4tr.termmanager.model.TermEntryResourceTrack;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository("termEntryResourceTrackDAO")
public class TermEntryResourceTrackDAOImpl extends AbstractHibernateGenericDao<TermEntryResourceTrack, Long>
	implements TermEntryResourceTrackDAO {

    public TermEntryResourceTrackDAOImpl() {
	super(TermEntryResourceTrack.class, Long.class);
    }

    @Override
    public List<TermEntryResourceTrack> findAllByTermEntryId(final String termEntryId) {
	HibernateCallback<List<TermEntryResourceTrack>> callback = new HibernateCallback<List<TermEntryResourceTrack>>() {

	    @SuppressWarnings("unchecked")
	    @Override
	    public List<TermEntryResourceTrack> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("TermEntryResourceTrack.findByTermEntryId");
		query.setParameter("termEntryId", termEntryId);

		return query.list();
	    }
	};
	return execute(callback);
    }

}
