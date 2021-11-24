package org.gs4tr.termmanager.dao.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.dao.hibernate.AbstractHibernateGenericDao;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.termmanager.dao.UserProjectRoleSearchDAO;
import org.gs4tr.termmanager.model.NotificationProfile;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UserProjectRole;
import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository("userProjectRoleSearch")
public class UserProjectRoleSearchDAOImpl extends AbstractHibernateGenericDao<UserProjectRole, Long>
	implements UserProjectRoleSearchDAO {

    public UserProjectRoleSearchDAOImpl() {
	super(UserProjectRole.class, Long.class);
    }

    @Override
    public List<TmUserProfile> findAllNonGenericEnabledUsers() {
	HibernateCallback<List<TmUserProfile>> cb = session -> {
	    Query query = session.getNamedQuery("UserProfile.findAllNonGenericEnabledUserProfiles");
	    return query.list();
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    public List<TmUserProfile> findUsersForReport() {
	HibernateCallback<List<TmUserProfile>> cb = session -> {
	    Query query = session.getNamedQuery("UserProjectRole.getUsersByProjectForReport");
	    final List<TmUserProfile> ups = query.list();
	    if (CollectionUtils.isNotEmpty(ups)) {
		for (TmUserProfile u : ups) {
		    List<NotificationProfile> notificationProfiles = u.getNotificationProfiles();
		    if (CollectionUtils.isNotEmpty(notificationProfiles)) {
			Hibernate.initialize(notificationProfiles);
		    }
		}
	    }

	    return ups;
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    public List<UserProjectRole> getAllUserProjectRoles(final Long userId, final Long projectId) {
	HibernateCallback<List<UserProjectRole>> cb = session -> {

	    Query query = session.getNamedQuery("UserProjectRole.getAllUserProjectRolesByUserAndProject");

	    query.setLong("userId", userId);
	    query.setLong("projectId", projectId);

	    return query.list();
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    public List<TmUserProfile> getGenericUsersByProject(final Long projectId) {
	HibernateCallback<List<TmUserProfile>> cb = session -> {

	    Query query = session.getNamedQuery("UserProjectRole.getGenericUsersByProject");
	    query.setLong("projectId", projectId);

	    return query.list();
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    public List<UserProjectRole> getProjectRolesByUser(final Long userId) {
	HibernateCallback<List<UserProjectRole>> cb = session -> {

	    Query query = session.getNamedQuery("UserProjectRole.getUserProjectRolesByUser");

	    query.setLong("userId", userId);

	    return query.list();
	};

	return execute(cb);
    }

    @Override
    public Map<Long, List<Role>> getProjectRolesByUserId(final Long userProfileId) {
	HibernateCallback<Map<Long, List<Role>>> cb = session -> {
	    Map<Long, List<Role>> projectRoles = new HashMap<>();

	    Query query = session.getNamedQuery("UserProjectRole.getProjectRolesByUser");
	    query.setLong("userProfileId", userProfileId);

	    @SuppressWarnings("unchecked")
	    List<Map<String, Object>> queryResult = query.list();
	    for (Map<String, Object> item : queryResult) {
		Role role = (Role) item.get("role");
		Long projectId = (Long) item.get("projectId");

		List<Role> roles = projectRoles.computeIfAbsent(projectId, k -> new ArrayList<>());

		if (!roles.contains(role)) {
		    roles.add(role);
		}
	    }

	    return projectRoles;
	};
	return execute(cb);

    }

    @SuppressWarnings("unchecked")
    public List<UserProjectRole> getProjectUserRolesByProject(final Long projectId) {
	HibernateCallback<List<UserProjectRole>> cb = session -> {

	    Query query = session.getNamedQuery("UserProjectRole.getUserProjectRoleByProject");
	    query.setLong("projectId", projectId);

	    return (List<UserProjectRole>) query.list();
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    public List<TmProject> getProjectsByUser(final Long userId) {
	HibernateCallback<List<TmProject>> cb = session -> {

	    Query query = session.getNamedQuery("UserProjectRole.getProjectsByUser");
	    query.setLong("userId", userId);

	    return query.list();
	};

	return execute(cb);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getRoleIdsByUserAndProject(final Long userId, final Long projectId) {
	HibernateCallback<List<String>> cb = session -> {

	    Query query = session.getNamedQuery("UserProjectRole.getRoleIdsByUserAndProject");
	    query.setLong("projectId", projectId.intValue());
	    query.setLong("userId", userId);

	    return query.list();
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    public List<Role> getRolesByUserAndProject(final Long userId, final Long projectId) {
	HibernateCallback<List<Role>> cb = session -> {

	    Query query = session.getNamedQuery("UserProjectRole.getRolesByUserAndProject");
	    query.setLong("projectId", projectId.intValue());
	    query.setLong("userId", userId);

	    return query.list();
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    public List<TmUserProfile> getUsersByProject(final Long projectId) {
	HibernateCallback<List<TmUserProfile>> cb = session -> {

	    Query query = session.getNamedQuery("UserProjectRole.getUsersByProject");
	    query.setLong("projectId", projectId);

	    return query.list();
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    public List<TmUserProfile> getUsersByProjectAndRole(final Long projectId, final String roleId) {
	HibernateCallback<List<TmUserProfile>> cb = session -> {

	    Query query = session.getNamedQuery("UserProjectRole.getUsersByProjectAndRole");

	    query.setLong("projectId", projectId);
	    query.setString("roleId", roleId);
	    return query.list();
	};

	return execute(cb);

    }

    @Override
    public Integer updateUserProjectRoles(final String oldRoleId, final String newRoleId) {
	HibernateCallback<Integer> cb = session -> {

	    Query query = session.getNamedQuery("UserProjectRole.updateRoles");
	    query.setString("newRole", newRoleId);
	    query.setString("oldRole", oldRoleId);

	    session.setFlushMode(FlushMode.COMMIT);
	    return query.executeUpdate();
	};

	return execute(cb);
    }
}
