package org.gs4tr.termmanager.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.ImpersonateUserInfo;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.foundation.modules.usermanager.dao.BaseUserProfileDAO;
import org.gs4tr.termmanager.model.NotificationProfile;
import org.gs4tr.termmanager.model.TmUserProfile;

public interface UserProfileDAO extends BaseUserProfileDAO<TmUserProfile> {

    void disableUserProfiles(List<Long> userIds);

    List<String> findAllNonGenerciUsernames();

    List<TmUserProfile> findDistinctGenericUserByProjectId(Long projectId);

    List<TmUserProfile> findGenericUserByProjectId(Long projectId);

    TmUserProfile findUserFetchById(Long userId, Class<?>... classesToFetch);

    Set<ImpersonateUserInfo> findUserNamesForImpersonation();

    TmUserProfile findUserProfileByUserNameFetchNotifications(String username);

    List<String> findUsernamesByProjectId(Long projectId);

    List<String> findUsernamesByType(UserTypeEnum type);

    List<TmUserProfile> findUsersByOrganization(Long organizationId);

    List<TmUserProfile> findUsersByOrganizationFetchLanguages(Long organizatiOnId, boolean showGenericUsers);

    TmUserProfile findUsersByUserNameNoFetch(String username);

    List<TmUserProfile> findUsersByUsernames(Collection<String> usernames);

    List<TmUserProfile> findUsersByUsernamesNoFetch(Collection<String> usernames);

    List<NotificationProfile> getUserNotificationProfiles(Long userId);
}
