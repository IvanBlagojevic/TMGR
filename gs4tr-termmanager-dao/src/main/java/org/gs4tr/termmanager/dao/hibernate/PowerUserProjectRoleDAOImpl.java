package org.gs4tr.termmanager.dao.hibernate;

import org.gs4tr.foundation.modules.dao.hibernate.AbstractHibernateGenericDao;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.termmanager.dao.PowerUserProjectRoleDAO;
import org.gs4tr.termmanager.model.TmPowerUserProjectRole;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository("powerUserProjectRoleDAO")
public class PowerUserProjectRoleDAOImpl extends AbstractHibernateGenericDao<TmPowerUserProjectRole, Long>
	implements PowerUserProjectRoleDAO {

    public PowerUserProjectRoleDAOImpl() {
	super(TmPowerUserProjectRole.class, Long.class);
    }

    @Override
    public TmPowerUserProjectRole findByUserId(Long userId) {
	HibernateCallback<TmPowerUserProjectRole> cb = session -> {

	    Query query = session.getNamedQuery("PowerUserProjectRole.findByUserId");
	    query.setLong("userId", userId);

	    return (TmPowerUserProjectRole) query.uniqueResult();
	};

	return execute(cb);
    }

    @Override
    public boolean updatePowerUserProjectRole(String newRoleId, Long id) {
	HibernateCallback<Integer> cb = session -> {

	    Query query = session.getNamedQuery("PowerUserProjectRole.updateRole");
	    query.setString("newRole", newRoleId);
	    query.setLong("userId", id);

	    session.setFlushMode(FlushMode.COMMIT);
	    return query.executeUpdate();
	};

	return execute(cb) == 1;
    }

}
