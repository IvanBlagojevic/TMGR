package org.gs4tr.termmanager.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.gs4tr.foundation.modules.dao.LoadProvider;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.usermanager.service.BaseUserProfileService;
import org.gs4tr.termmanager.model.NotificationProfile;
import org.gs4tr.termmanager.model.Preferences;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UserCustomSearch;
import org.gs4tr.termmanager.model.UserProjectRole;
import org.gs4tr.termmanager.service.utils.MailHelper.ReportType;
import org.springframework.security.access.annotation.Secured;

public interface UserProfileService extends BaseUserProfileService<TmUserProfile> {

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    Long addOrUpdateCustomSearchFolder(TmUserProfile userProfile, String folder, String originalFolder, String url,
	    String searchJsonData, boolean adminFolder);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void addOrUpdateNotificationProfiles(TmUserProfile currentUserProfile,
	    List<NotificationProfile> newNotificationProfiles);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    Long addOrUpdateProjectUserDetails(Long userId, Long projectId, List<Long> newUserIds);

    @Secured({ "POLICY_FOUNDATION_PROJECT_ADD", "POLICY_FOUNDATION_PROJECT_EDIT" })
    Long addOrUpdateUserProjectRoles(Long userId, Long projectId, List<Role> roles, List<Long> newUsers,
	    UserTypeEnum userType, Boolean isGeneric);

    @Override
    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    Long changeCurrentUserPassword(String oldPassword, String newPassword);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    Long createOrUpdateGenericUser(Long userId, UserInfo userInfo, Long organizationId);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void disableUserProfiles(List<Long> userIds);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    boolean exists(String username);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<String> findAllNonGenerciUsernames();

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<TmUserProfile> findGenericUserByProjectId(Long projectId);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    TmUserProfile findUserByUsernameFetchNotifications(String username);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<Long> findUserProfileIdsByCriteria(LoadProvider loadProvider);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<TmUserProfile> findUserProfilesByCriteria(LoadProvider loadProvider);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<UserProjectRole> findUserProjectRolesByProject(Long projectId);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<TmUserProfile> findUsersByOrganization(Long organizationId);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<TmUserProfile> findUsersByOrganizationFetchLanguages(TmOrganization organization, boolean showGenericUsers);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<TmUserProfile> findUsersByType(UserTypeEnum type);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    TmUserProfile findUsersByUserNameNoFetch(String currentUsername);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<TmUserProfile> findUsersByUsernames(Collection<String> usernames);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<TmUserProfile> getAllNonGenericEnabledUsers();

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    Set<TmUserProfile> getAllPowerAndOrgUsers(List<Long> projectIds);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    UserCustomSearch getCustomSearchFolder(TmUserProfile currentUser, String folder);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<UserCustomSearch> getCustomSearchFolders(TmUserProfile userProfile, boolean adminFolders);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<String> getNotificationClassifiers(Long userId, ReportType reportType);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<TmUserProfile> getOrgUsersForReport();

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<NotificationProfile> getUserNotificationProfiles(Long userId);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    Preferences getUserPreferences(Long userId);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    boolean isAssigneeUser(Long userId);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    boolean isSubmitterUser(Long userId);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    boolean removeCustomSearchFolder(TmUserProfile userProfile, String folder);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void updateHasChangedTerms(Boolean hasChangedTerms);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    Long updatePreferences(Preferences preferences);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void updateReportDate(Long userId, String timeZone, ReportType reportType);
}
