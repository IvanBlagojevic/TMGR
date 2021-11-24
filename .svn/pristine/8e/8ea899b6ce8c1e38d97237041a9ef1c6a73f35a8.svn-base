package org.gs4tr.termmanager.dao.hibernate;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.dao.hibernate.QueryGenerator;
import org.gs4tr.foundation.modules.dao.hibernate.QueryGeneratorTemplate;
import org.gs4tr.foundation.modules.entities.model.search.ProjectSearchRequest;
import org.gs4tr.foundation.modules.project.dao.hibernate.BaseProjectSearchDAOImpl;
import org.gs4tr.termmanager.dao.ProjectSearchDAO;
import org.gs4tr.termmanager.model.TmProject;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository("projectSearchDAO")
public class ProjectSearchDAOImpl extends BaseProjectSearchDAOImpl<TmProject> implements ProjectSearchDAO {

    protected ProjectSearchDAOImpl() {
	super(TmProject.class);

    }

    @Override
    protected QueryGenerator<TmProject, ProjectSearchRequest> getQueryGenerator() {
	return new QueryGeneratorTemplate<TmProject, ProjectSearchRequest>() {

	    @Override
	    protected void createCountJoinClause(StringBuffer buffer, ProjectSearchRequest command) {

		buffer.append("left join entity.organization organization");
	    }

	    @Override
	    protected void createCountSelectClause(StringBuffer buffer, ProjectSearchRequest command) {

		buffer.append("select count(distinct entity)");
	    }

	    @Override
	    protected void createJoinClause(StringBuffer buffer, ProjectSearchRequest command) {

		buffer.append("left join fetch entity.organization organization ");
	    }

	    @Override
	    protected void createSelectClause(StringBuffer buffer, ProjectSearchRequest command) {

		buffer.append("select entity");
	    }

	    @Override
	    protected void createStartWhereClause(StringBuffer buffer, ProjectSearchRequest command) {

		String organizationName = command.getOrganizationName();

		if (StringUtils.isNotEmpty(organizationName)) {
		    buffer.append("where organization.organizationInfo.name = :organizationName");
		} else {
		    buffer.append("where 1=1");
		}
	    }

	    @Override
	    protected void createWhereClause(StringBuffer buffer, ProjectSearchRequest command) {

		String name = command.getName();
		String clientIdentifier = command.getClientIdentifier();

		if (name != null && name.length() > 0) {
		    buffer.append("and entity.projectInfo.name like :name ");
		}
		if (clientIdentifier != null && clientIdentifier.length() > 0) {
		    buffer.append("and entity.projectInfo.clientIdentifier like :clientIdentifier ");
		}

	    }

	    @Override
	    protected void initParameters(Query query, ProjectSearchRequest command) {
		String clientIdentifier = command.getClientIdentifier();
		String name = command.getName();
		String organizationName = command.getOrganizationName();
		if (clientIdentifier != null && clientIdentifier.length() > 0) {
		    query.setString("clientIdentifier", clientIdentifier);
		}
		if (name != null && name.length() > 0) {
		    query.setString("name", '%' + name + '%');
		}

		if (StringUtils.isNotEmpty(organizationName)) {
		    query.setString("organizationName", organizationName);
		}
	    }

	};
    }
}
