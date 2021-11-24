package org.gs4tr.termmanager.dao.hibernate;

import static java.util.Objects.nonNull;

import org.gs4tr.foundation.modules.dao.hibernate.QueryGenerator;
import org.gs4tr.foundation.modules.dao.hibernate.QueryGeneratorTemplate;
import org.gs4tr.foundation.modules.entities.model.search.UserProfileSearchRequest;
import org.gs4tr.foundation.modules.usermanager.dao.hibernate.BaseUserProfileSearchDAOImpl;
import org.gs4tr.termmanager.dao.UserProfileSearchDAO;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository("userProfileSearchDAO")
public class UserProfileSearchDAOImpl extends BaseUserProfileSearchDAOImpl<TmUserProfile>
	implements UserProfileSearchDAO {

    public UserProfileSearchDAOImpl() {
	super(TmUserProfile.class);
    }

    @Override
    protected QueryGenerator<TmUserProfile, UserProfileSearchRequest> getQueryGenerator() {

	return new QueryGeneratorTemplate<TmUserProfile, UserProfileSearchRequest>() {

	    @Override
	    protected void createCountJoinClause(StringBuffer buffer, UserProfileSearchRequest command) {

	    }

	    @Override
	    protected void createCountSelectClause(StringBuffer buffer, UserProfileSearchRequest command) {
		buffer.append("select count(distinct entity) ");
	    }

	    @Override
	    protected void createJoinClause(StringBuffer buffer, UserProfileSearchRequest command) {
		// buffer.append("left join fetch entity.systemRoles as role ");
		buffer.append("left join fetch entity.organization as organization ");
		// buffer.append("left join fetch role.policies as policy ");

	    }

	    @Override
	    protected void createSelectClause(StringBuffer buffer, UserProfileSearchRequest command) {
		buffer.append("select entity");
	    }

	    @Override
	    protected void createStartWhereClause(StringBuffer buffer, UserProfileSearchRequest command) {

		Long organizationId = command.getOrganizationId();

		if (nonNull(organizationId)) {
		    buffer.append("where organization.organizationId = :organizationId");
		} else {
		    buffer.append("where 1=1");
		}
	    }

	    @Override
	    protected void createWhereClause(StringBuffer buffer, UserProfileSearchRequest userProfileSearchRequest) {
		String username = userProfileSearchRequest.getUsername();

		String firstname = userProfileSearchRequest.getFirstname();

		String lastname = userProfileSearchRequest.getLastname();

		String emailAddress = userProfileSearchRequest.getEmailAddress();

		buffer.append("and entity.hidden = false ");

		buffer.append("and entity.generic = false ");

		if (username != null && username.length() > 0) {
		    buffer.append("and entity.userInfo.userName like :username ");
		}

		if (firstname != null && firstname.length() > 0) {
		    buffer.append("and entity.userInfo.firstName like :firstname ");
		}

		if (lastname != null && lastname.length() > 0) {
		    buffer.append("and entity.userInfo.lastName like :lastname ");
		}

		if (emailAddress != null && emailAddress.length() > 0) {
		    buffer.append("and entity.userInfo.emailAddress like :emailAddress ");
		}

	    }

	    @Override
	    protected void initParameters(Query query, UserProfileSearchRequest userProfileSearchRequest) {
		String username = userProfileSearchRequest.getUsername();

		String firstname = userProfileSearchRequest.getFirstname();

		String lastname = userProfileSearchRequest.getLastname();

		String emailAddress = userProfileSearchRequest.getEmailAddress();

		Long organizationId = userProfileSearchRequest.getOrganizationId();

		if (username != null && username.length() > 0) {
		    query.setString("username", '%' + username + '%');
		}
		if (firstname != null && firstname.length() > 0) {
		    query.setString("firstname", '%' + firstname + '%');
		}

		if (lastname != null && lastname.length() > 0) {
		    query.setString("lastname", '%' + lastname + '%');
		}
		if (emailAddress != null && emailAddress.length() > 0) {
		    query.setString("emailAddress", '%' + emailAddress + '%');
		}
		if (nonNull(organizationId)) {
		    query.setLong("organizationId", organizationId);
		}
	    }
	};
    }

}
