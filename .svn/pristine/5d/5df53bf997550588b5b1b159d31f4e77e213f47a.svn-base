package org.gs4tr.termmanager.dao.hibernate;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.foundation.modules.entities.model.ImpersonateUserInfo;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.usermanager.dao.hibernate.AbstractUserProfileDAOImpl;
import org.gs4tr.foundation.modules.usermanager.model.UserManagerPolicyEnum;
import org.gs4tr.termmanager.dao.UserProfileDAO;
import org.gs4tr.termmanager.dao.utils.DaoUtils;
import org.gs4tr.termmanager.model.NotificationProfile;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository("userProfileDAO")
public class UserProfileDAOImpl extends AbstractUserProfileDAOImpl<TmUserProfile> implements UserProfileDAO {

    public UserProfileDAOImpl() {
	super(TmUserProfile.class);
    }

    @Override
    public void disableUserProfiles(final List<Long> userIds) {
	execute(new HibernateCallback<Integer>() {
	    @Override
	    public Integer doInHibernate(Session session) throws HibernateException {
		Query query = session.getNamedQuery("UserProfile.setUserProfilesDisabled");
		query.setParameterList("userIds", userIds);
		query.setTimestamp("deactivationDate", new Date());

		return query.executeUpdate();
	    }
	});
    }

    @Override
    public List<String> findAllNonGenerciUsernames() {
	HibernateCallback<List<String>> cb = new HibernateCallback<List<String>>() {
	    @SuppressWarnings("unchecked")
	    @Override
	    public List<String> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("UserProfile.findAllNonGenerciUsernames");
		return query.list();
	    }
	};

	return execute(cb);
    }

    @Override
    public List<TmUserProfile> findDistinctGenericUserByProjectId(Long projectId) {
	HibernateCallback<List<TmUserProfile>> cb = new HibernateCallback<List<TmUserProfile>>() {
	    @SuppressWarnings("unchecked")
	    @Override
	    public List<TmUserProfile> doInHibernate(Session session) throws HibernateException, SQLException {

		Query query = session.getNamedQuery("UserProfile.findDistinctGenericUsersByProjectId");
		query.setLong("projectId", projectId);

		return query.list();

	    }
	};

	return execute(cb);
    }

    @Override
    public List<TmUserProfile> findGenericUserByProjectId(final Long projectId) {
	HibernateCallback<List<TmUserProfile>> cb = new HibernateCallback<List<TmUserProfile>>() {
	    @SuppressWarnings("unchecked")
	    @Override
	    public List<TmUserProfile> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("UserProfile.findGenericUsersByProjectId");
		query.setLong("projectId", projectId);

		return query.list();
	    }
	};

	return execute(cb);
    }

    @Override
    public TmUserProfile findUserFetchById(Long userId, Class<?>... classesToFetch) {
	TmUserProfile userProfile = findByIdEntityOnly(userId);
	DaoUtils.initializeEntities(userProfile, classesToFetch);

	return userProfile;
    }

    @Override
    public Set<ImpersonateUserInfo> findUserNamesForImpersonation() {

	HibernateCallback<Set<ImpersonateUserInfo>> cb = new HibernateCallback<Set<ImpersonateUserInfo>>() {
	    @SuppressWarnings("unchecked")
	    @Override
	    public Set<ImpersonateUserInfo> doInHibernate(Session session) throws HibernateException, SQLException {

		String queryString = session.getNamedQuery("UserProfile.findUserProfilesForImpersonation")
			.getQueryString();

		Query query = session.createQuery(queryString);
		query.setParameter("policy", UserManagerPolicyEnum.POLICY_FOUNDATION_USERPROFILE_IMPERSONATIBLE.name());

		Set<ImpersonateUserInfo> result = new HashSet<ImpersonateUserInfo>();

		List<TmUserProfile> dataList = query.list();
		if (CollectionUtils.isNotEmpty(dataList)) {
		    for (TmUserProfile data : dataList) {
			ImpersonateUserInfo info = new ImpersonateUserInfo();
			info.setUserName(data.getUserName());
			info.setLanguageId(data.getPreferences().getLanguage());

			result.add(info);
		    }
		}

		return result;
	    }

	};

	return execute(cb);

    }

    @Override
    public TmUserProfile findUserProfileByUserNameFetchNotifications(final String username) {
	HibernateCallback<TmUserProfile> cb = new HibernateCallback<TmUserProfile>() {
	    @Override
	    public TmUserProfile doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("UserProfile.findUserProfileByUserNameFetchNotifications");
		query.setString("username", username);

		return (TmUserProfile) query.uniqueResult();
	    }
	};

	return execute(cb);
    }

    @Override
    public List<String> findUsernamesByProjectId(Long projectId) {
	HibernateCallback<List<String>> cb = new HibernateCallback<List<String>>() {
	    @Override
	    @SuppressWarnings("unchecked")
	    public List<String> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("UserProfile.findUsernamesByProjectId");
		query.setLong("projectId", projectId);
		return query.list();
	    }
	};
	return execute(cb);
    }

    @Override
    public List<String> findUsernamesByType(UserTypeEnum type) {
	HibernateCallback<List<String>> cb = new HibernateCallback<List<String>>() {
	    @Override
	    @SuppressWarnings("unchecked")
	    public List<String> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("UserProfile.findUsernamesByType");
		query.setParameter("entityType", type);
		return query.list();
	    }
	};
	return execute(cb);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TmUserProfile> findUsersByOrganization(final Long organizatiOnId) {
	HibernateCallback<List<TmUserProfile>> cb = new HibernateCallback<List<TmUserProfile>>() {

	    @Override
	    public List<TmUserProfile> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("UserProfile.findByOraganizationId");
		query.setLong("organizationId", organizatiOnId);

		return query.list();
	    }
	};

	return execute(cb);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TmUserProfile> findUsersByOrganizationFetchLanguages(final Long organizatiOnId,
	    final boolean showGenericUsers) {
	HibernateCallback<List<TmUserProfile>> cb = new HibernateCallback<List<TmUserProfile>>() {

	    @Override
	    public List<TmUserProfile> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("UserProfile.findByOraganizationIdFetchLanguages");
		query.setLong("organizationId", organizatiOnId);
		query.setBoolean("showGenericUsers", showGenericUsers);

		return query.list();
	    }
	};

	return execute(cb);
    }

    @Override
    public TmUserProfile findUsersByUserNameNoFetch(final String username) {
	HibernateCallback<TmUserProfile> cb = new HibernateCallback<TmUserProfile>() {
	    @Override
	    public TmUserProfile doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("UserProfile.findUserProfileByUserNameNoFetch");
		query.setString("username", username);

		return (TmUserProfile) query.uniqueResult();
	    }
	};

	return execute(cb);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TmUserProfile> findUsersByUsernames(final Collection<String> usernames) {
	HibernateCallback<List<TmUserProfile>> cb = new HibernateCallback<List<TmUserProfile>>() {

	    @Override
	    public List<TmUserProfile> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("UserProfile.findUsersByUsernames");
		query.setParameterList("usernames", usernames);

		return query.list();
	    }
	};

	return execute(cb);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TmUserProfile> findUsersByUsernamesNoFetch(final Collection<String> usernames) {
	HibernateCallback<List<TmUserProfile>> cb = new HibernateCallback<List<TmUserProfile>>() {

	    @Override
	    public List<TmUserProfile> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("UserProfile.findUsersByUsernamesNoFetch");
		query.setParameterList("usernames", usernames);

		return query.list();
	    }
	};

	return execute(cb);
    }

    @Override
    public List<NotificationProfile> getUserNotificationProfiles(final Long userId) {
	List<NotificationProfile> notificationProfiles = load(userId).getNotificationProfiles();
	if (CollectionUtils.isNotEmpty(notificationProfiles)) {
	    return notificationProfiles;
	}
	return null;
    }

    @Override
    public void postLoginInitialize(TmUserProfile userProfile) {
	Set<Role> systemRoles = userProfile.getSystemRoles();
	Hibernate.initialize(systemRoles);
	for (Role role : systemRoles) {
	    Hibernate.initialize(role.getPolicies());
	}
	Hibernate.initialize(userProfile.getMetadata());
    }

    @Override
    protected String getFindByIdQueryName() {
	return "UserProfile.findUserProfileById";
    }

    @Override
    protected String getFindByNameQueryName() {
	return "UserProfile.findUserProfileByUserName";
    }

    @Override
    protected String getFindByTypeQueryName() {
	return "UserProfile.findUserProfileByType";
    }

    @Override
    protected String getFindEmailByUsernameQueryName() {
	return "UserProfile.findEmailByUsername";
    }

    @Override
    protected String getFindIdByNameQueryName() {
	return "UserProfile.findIdByName";
    }

    @Override
    protected String getFindIdsByEmailQueryName() {
	return "UserProfile.findIdsByEmailQueryName";
    }
}
