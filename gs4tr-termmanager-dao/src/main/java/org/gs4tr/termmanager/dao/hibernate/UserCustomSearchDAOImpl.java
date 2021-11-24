package org.gs4tr.termmanager.dao.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.gs4tr.foundation.modules.dao.hibernate.AbstractHibernateGenericDao;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.termmanager.dao.UserCustomSearchDAO;
import org.gs4tr.termmanager.model.UserCustomSearch;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository("userCustomSearchDAO")
public class UserCustomSearchDAOImpl extends AbstractHibernateGenericDao<UserCustomSearch, Long>
	implements UserCustomSearchDAO {

    public UserCustomSearchDAOImpl() {
	super(UserCustomSearch.class, Long.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UserCustomSearch> findByUserProfileId(final Long userProfileId, final boolean adminFolders) {
	HibernateCallback<List<UserCustomSearch>> cb = new HibernateCallback<List<UserCustomSearch>>() {
	    @Override
	    public List<UserCustomSearch> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("UserCustomSearch.findAllByUserProfileId");
		query.setParameter("userProfileId", userProfileId);
		query.setParameter("adminFolders", adminFolders);

		return query.list();
	    }
	};
	return execute(cb);
    }

    @Override
    public UserCustomSearch findByUserProfileIdAndFolderName(final Long userProfileId, final String folderName) {
	HibernateCallback<UserCustomSearch> cb = new HibernateCallback<UserCustomSearch>() {
	    @Override
	    public UserCustomSearch doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("UserCustomSearch.findByFolderAndUser");
		query.setParameter("userProfileId", userProfileId);
		query.setParameter("folder", folderName);

		return (UserCustomSearch) query.uniqueResult();
	    }
	};
	return execute(cb);
    }
}
