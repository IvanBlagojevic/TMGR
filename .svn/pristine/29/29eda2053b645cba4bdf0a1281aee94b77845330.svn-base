package org.gs4tr.termmanager.dao.hibernate;

import org.gs4tr.foundation.modules.dao.hibernate.QueryGenerator;
import org.gs4tr.foundation.modules.entities.model.search.OrganizationSearchRequest;
import org.gs4tr.foundation.modules.organization.dao.hibernate.BaseOrganizationSearchDAOImpl;
import org.gs4tr.foundation.modules.organization.dao.hibernate.OrganizationQueryGeneratorTemplate;
import org.gs4tr.termmanager.dao.OrganizationSearchDAO;
import org.gs4tr.termmanager.model.TmOrganization;
import org.springframework.stereotype.Repository;

@Repository("organizationSearchDAO")
public class OrganizationSearchDAOImpl extends BaseOrganizationSearchDAOImpl<TmOrganization>
	implements OrganizationSearchDAO {

    protected OrganizationSearchDAOImpl() {
	super(TmOrganization.class);
    }

    @Override
    protected QueryGenerator<TmOrganization, OrganizationSearchRequest> getQueryGenerator() {

	return new OrganizationQueryGeneratorTemplate<TmOrganization>() {

	    @Override
	    protected void createCountJoinClause(StringBuffer buffer, OrganizationSearchRequest command) {

	    }

	    @Override
	    protected void createJoinClause(StringBuffer buffer, OrganizationSearchRequest command) {
		buffer.append("left join entity.parentOrganization as parentOrganization");
	    }

	    @Override
	    protected void createCountSelectClause(StringBuffer buffer, OrganizationSearchRequest command) {
		buffer.append("select count(entity)");
	    }

	};
    }
}
