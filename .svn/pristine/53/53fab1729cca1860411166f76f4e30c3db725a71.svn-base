package org.gs4tr.termmanager.dao.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.foundation.modules.organization.dao.hibernate.BaseOrganizationDAOImpl;
import org.gs4tr.termmanager.dao.OrganizationDAO;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository("organizationDAO")
public class OrganizationDAOImpl extends BaseOrganizationDAOImpl<TmOrganization> implements OrganizationDAO {

    public OrganizationDAOImpl() {
	super(TmOrganization.class);
    }

    @Override
    public void enableOrganizationUsers(final Long organizationId, final boolean enabled) {
	HibernateCallback<Integer> callback = new HibernateCallback<Integer>() {

	    @Override
	    public Integer doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("Organization.enableOrganizationUsers");
		query.setParameter("entityId", organizationId);
		query.setParameter("enabled", enabled);

		return query.executeUpdate();
	    }
	};

	execute(callback);
    }

    @Override
    public List<TmOrganization> findAllWithDependants() {
	HibernateCallback<List<TmOrganization>> callback = new HibernateCallback<List<TmOrganization>>() {

	    @Override
	    public List<TmOrganization> doInHibernate(Session session) throws HibernateException, SQLException {

		List<TmOrganization> organizations = findAll();

		for (TmOrganization organization : organizations) {
		    Hibernate.initialize(organization.getChildOrganizations());
		    Hibernate.initialize(organization.getParentOrganization());
		}

		return organizations;
	    }
	};
	return execute(callback);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TmProject> findOrganizationProjects(final Long organizationId) {
	HibernateCallback<List<TmProject>> callback = new HibernateCallback<List<TmProject>>() {

	    @Override
	    public List<TmProject> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("Organization.findOrganizationProjectsById");
		query.setParameter("entityId", organizationId);

		return query.list();
	    }
	};

	return execute(callback);
    }

    @Override
    public Long getOrganizationIdByUserId(Long userId) {
	HibernateCallback<Long> callback = session -> {
	    Query query = session.getNamedQuery("Organization.findOrganizationIdByUserId");
	    query.setParameter("userId", userId);

	    return (Long) query.uniqueResult();
	};

	return execute(callback);
    }

    @Override
    public String getOrganizationNameByUserId(Long userId) {
	HibernateCallback<String> callback = session -> {
	    Query query = session.getNamedQuery("Organization.findOrganizationNameByUserId");
	    query.setParameter("userId", userId);

	    return (String) query.uniqueResult();
	};

	return execute(callback);
    }

    @Override
    protected String getChildOrganizationsQueryName() {
	return "findChildOrganizationsById";
    }

    @Override
    protected String getFindByNameQueryName() {
	return "Organization.findByName";
    }
}
