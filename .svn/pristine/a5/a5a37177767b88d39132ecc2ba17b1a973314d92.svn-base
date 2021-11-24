package org.gs4tr.termmanager.dao;

import java.util.List;
import java.util.Map;

import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UserProjectRole;

public interface UserProjectRoleSearchDAO extends GenericDao<UserProjectRole, Long> {

    List<TmUserProfile> findAllNonGenericEnabledUsers();

    List<TmUserProfile> findUsersForReport();

    List<UserProjectRole> getAllUserProjectRoles(Long userId, Long projectId);

    List<TmUserProfile> getGenericUsersByProject(Long projectId);

    List<UserProjectRole> getProjectRolesByUser(Long userId);

    Map<Long, List<Role>> getProjectRolesByUserId(Long userProfileId);

    List<UserProjectRole> getProjectUserRolesByProject(Long projectId);

    List<TmProject> getProjectsByUser(Long userId);

    List<String> getRoleIdsByUserAndProject(Long userId, Long projectId);

    List<Role> getRolesByUserAndProject(Long userId, Long projectId);

    List<TmUserProfile> getUsersByProject(Long projectId);

    List<TmUserProfile> getUsersByProjectAndRole(Long projectId, String roleId);

    Integer updateUserProjectRoles(String oldRoleId, String newRoleId);
}
