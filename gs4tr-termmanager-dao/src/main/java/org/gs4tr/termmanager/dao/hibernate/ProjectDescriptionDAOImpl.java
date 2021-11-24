package org.gs4tr.termmanager.dao.hibernate;

import org.gs4tr.foundation.modules.dao.hibernate.AbstractHibernateGenericDao;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.termmanager.dao.ProjectDescriptionDAO;
import org.gs4tr.termmanager.model.ProjectDescription;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository("projectDescriptionDAO")
public class ProjectDescriptionDAOImpl extends AbstractHibernateGenericDao<ProjectDescription, Long>
	implements ProjectDescriptionDAO {

    public ProjectDescriptionDAOImpl() {
	super(ProjectDescription.class, Long.class);
    }

    @Override
    public void deleteByProjectId(Long projectId) {
	execute(session -> {
	    Query query = session.getNamedQuery("ProjectDescription.deleteByProjectId");
	    query.setLong("projectId", projectId);
	    query.executeUpdate();
	    return null;
	});

    }

    @Override
    public ProjectDescription findByProjectId(Long projectId) {
	HibernateCallback<ProjectDescription> cb = session -> {
	    Query query = session.getNamedQuery("ProjectDescription.findByProjectId");
	    query.setLong("projectId", projectId);
	    return (ProjectDescription) query.uniqueResult();
	};

	return execute(cb);

    }

    @Override
    public void updateProjectAvailableDescription(boolean value, Long projectId) {
	execute(session -> {
	    Query query = session.getNamedQuery("ProjectDescription.updateProjectAvailableDescription");
	    query.setBoolean("availableDescription", value);
	    query.setLong("projectId", projectId);
	    query.executeUpdate();
	    return null;
	});

    }
}
