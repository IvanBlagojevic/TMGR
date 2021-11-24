package org.gs4tr.termmanager.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.Metadata;
import org.gs4tr.foundation.modules.entities.model.OrganizationHolder;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.spring.utils.SpringProfileUtils;
import org.gs4tr.foundation.modules.usermanager.model.AbstractUserProfile;
import org.gs4tr.foundation.modules.usermanager.model.UserProfileMetadata;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

@NamedQueries({
	@NamedQuery(name = "UserProfile.findUserProfileById", query = "select userProfile "
		+ "from TmUserProfile userProfile " + "left join fetch userProfile.systemRoles roles "
		+ "left join fetch roles.policies " + "left join fetch userProfile.preferences "
		+ "left join fetch userProfile.metadata " + "left join fetch userProfile.organization "
		+ "where userProfile.userProfileId = :entityId"),

	@NamedQuery(name = "UserProfile.setUserProfilesDisabled", query = "update from TmUserProfile userProfile "
		+ "set userProfile.userInfo.enabled = false, "
		+ "userProfile.userInfo.deactivationDate = :deactivationDate "
		+ "where userProfile.userProfileId in (:userIds)"),

	@NamedQuery(name = "UserProfile.findUserProfileByType", query = "select userProfile "
		+ "from TmUserProfile userProfile " + "where userProfile.userInfo.userType = :entityType "
		+ "and userProfile.userInfo.enabled is true"),

	@NamedQuery(name = "UserProfile.findAllNonGenericEnabledUserProfiles", query = "select userProfile from "
		+ "TmUserProfile userProfile where userProfile.userInfo.enabled is true and userProfile.generic is false"),

	@NamedQuery(name = "UserProfile.findUsernamesByType", query = "select userProfile.userInfo.userName "
		+ "from TmUserProfile userProfile " + "where userProfile.userInfo.userType = :entityType "
		+ "and userProfile.userInfo.enabled is true"),

	@NamedQuery(name = "UserProfile.findUserProfileByUserName", query = "select userProfile "
		+ "from TmUserProfile userProfile " + "left join fetch userProfile.systemRoles roles "
		+ "left join fetch roles.policies " + "left join fetch userProfile.preferences "
		+ "left join fetch userProfile.metadata " + "left join fetch userProfile.notificationProfiles "
		+ "where userProfile.userInfo.userName = :name"),

	@NamedQuery(name = "UserProfile.findUserProfilesForImpersonation", query = "select userProfile "
		+ "from TmUserProfile userProfile " + "join userProfile.systemRoles userRole "
		+ "where userProfile.hidden is false " + "and userProfile.userInfo.enabled is true "
		+ "and userProfile.userInfo.accountNonLocked is true "
		+ "and exists(select policy.policyId from Role role " + "join role.policies policy "
		+ "where userRole.id = role.id " + "and policy.policyId = :policy) "),

	@NamedQuery(name = "UserProfile.findIdByName", query = "select userProfile.userProfileId "
		+ "from TmUserProfile userProfile where userProfile.userInfo.userName = :name "),

	@NamedQuery(name = "UserProfile.findByOraganizationId", query = "select userProfile "
		+ "from TmUserProfile userProfile " + "where userProfile.organization.organizationId = :organizationId "
		+ "and userProfile.userInfo.enabled is true"),

	@NamedQuery(name = "UserProfile.findByOraganizationIdFetchLanguages", query = "select distinct userProfile "
		+ "from TmUserProfile userProfile " + "where userProfile.organization.organizationId = :organizationId "
		+ "and userProfile.generic is (:showGenericUsers) " + "and userProfile.userInfo.enabled is true"),

	@NamedQuery(name = "UserProfile.findUserProfileByUserNameNoFetch", query = "select userProfile "
		+ "from TmUserProfile userProfile " + "where userProfile.userInfo.userName = :username"),

	@NamedQuery(name = "UserProfile.findUserProfileByUserNameFetchNotifications", query = "select distinct userProfile "
		+ "from TmUserProfile userProfile " + "left join fetch userProfile.notificationProfiles "
		+ "where userProfile.userInfo.userName = :username "),

	@NamedQuery(name = "UserProfile.findUsersByUsernames", query = "select distinct userProfile "
		+ "from TmUserProfile userProfile " + "left join fetch userProfile.notificationProfiles "
		+ "where userProfile.userInfo.userName in (:usernames) "),

	@NamedQuery(name = "UserProfile.findGenericUsersByProjectId", query = "select pul.user "
		+ "from ProjectUserLanguage pul " + "join pul.project project " + "join pul.user user "
		+ "where project.projectId = :projectId " + "and user.generic is true "),

	@NamedQuery(name = "UserProfile.findDistinctGenericUsersByProjectId", query = "select distinct upr.userProfile "
		+ "from UserProjectRole upr " + "join upr.project project " + "join upr.userProfile userProfile "
		+ "where project.projectId = :projectId " + "and userProfile.generic is true "),

	@NamedQuery(name = "UserProfile.findAllNonGenerciUsernames", query = "select distinct user.userInfo.userName "
		+ "from ProjectUserLanguage pul " + "join pul.user user " + "where user.generic is false "
		+ "and user.userInfo.enabled is true"),

	@NamedQuery(name = "UserProfile.findUsernamesByProjectId", query = "select distinct user.userInfo.userName "
		+ "from ProjectUserLanguage pul " + "join pul.project project " + "join pul.user user "
		+ "where project.projectId = :projectId " + "and user.userInfo.enabled is true "),

	@NamedQuery(name = "UserProfile.findIdsByEmailQueryName", query = "select userProfile.userProfileId "
		+ "from TmUserProfile userProfile " + "where userProfile.userInfo.emailAddress = :email"),

	@NamedQuery(name = "UserProfile.findEmailByUsername", query = "select userProfile.userInfo.emailAddress "
		+ "from TmUserProfile userProfile " + " where userProfile.userInfo.userName = :username"),

	@NamedQuery(name = "UserProfile.findUsersByUsernamesNoFetch", query = "select userProfile "
		+ "from TmUserProfile userProfile " + "where userProfile.userInfo.userName in (:usernames) ") })
@Entity
@Table(name = "USER_PROFILE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TmUserProfile extends AbstractUserProfile implements OrganizationHolder<TmOrganization> {

    private static final Log _logger = LogFactory.getLog(TmUserProfile.class);

    private static final long serialVersionUID = 7349889295946436314L;

    @Transient
    public static String getCurrentUserEmail() {
	return getCurrentUserProfile().getUserInfo().getEmailAddress();
    }

    @Transient
    public static Long getCurrentUserId() {
	return getCurrentUserProfile().getIdentifier();
    }

    @Transient
    public static String getCurrentUserName() {

	TmUserProfile currentUser = getCurrentUserProfile();

	if (currentUser.getUserInfo().isSsoUser()
		&& SpringProfileUtils.checkIfSpringProfileIsActive(SpringProfileUtils.OAUTH_AUTHENTICATION_PROFILE)) {
	    return currentUser.getUserInfo().getEmailAddress();
	}

	return currentUser.getUserName();
    }

    @Transient
    public static TmUserProfile getCurrentUserProfile() {
	try {
	    return (TmUserProfile) UserProfileContext.getCurrentUserProfile();
	} catch (Exception e) {
	    throw new AuthenticationCredentialsNotFoundException(e.getMessage(), e);
	}

    }

    @Transient
    public static boolean isSSO() {
	return getCurrentUserProfile().getUserInfo().isSsoUser();
    }

    private List<ItemFolderEnum> _adminFolders;

    private List<ItemFolderEnum> _folders;

    private String _genericPassword;

    private Boolean _hasChangedTerms = Boolean.FALSE;

    private Boolean _hidden = Boolean.FALSE;

    private Long _lastDailyReport;

    private Long _lastWeeklyReport;

    private List<NotificationProfile> _notificationProfiles;

    // is OPE_User
    private Boolean _ope = Boolean.FALSE;

    private TmOrganization _organization;

    private Preferences _preferences;

    private Map<Long, Set<String>> _projectUserLanguages = new HashMap<>();

    private Map<Long, Map<String, Set<String>>> _submissionUserLanguages = new HashMap<>();

    public TmUserProfile() {
	super();
    }

    @Transient
    public List<ItemFolderEnum> getAdminFolders() {
	return _adminFolders;
    }

    @Transient
    public List<ItemFolderEnum> getFolders() {
	return _folders;
    }

    @Column(name = "GENERIC_PASSWORD", nullable = true)
    public String getGenericPassword() {
	return _genericPassword;
    }

    @Column(name = "CHANGED_TERMS", nullable = true, updatable = true)
    public Boolean getHasChangedTerms() {
	return _hasChangedTerms;
    }

    @Column(name = "HIDDEN", nullable = false, updatable = false)
    public Boolean getHidden() {
	return _hidden;
    }

    @Override
    @Transient
    public Long getIdentifier() {
	return getUserProfileId();
    }

    @Column(name = "LAST_DAILY_REPORT", nullable = true, updatable = true)
    public Long getLastDailyReport() {
	return _lastDailyReport;
    }

    @Column(name = "LAST_WEEKLY_REPORT", nullable = true, updatable = true)
    public Long getLastWeeklyReport() {
	return _lastWeeklyReport;
    }

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "USER_NOTIFICATION_PROFILES", joinColumns = @JoinColumn(name = "USER_PROFILE_ID"))
    @Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @OrderColumn(name = "USER_IDX")
    @Embedded
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<NotificationProfile> getNotificationProfiles() {
	return _notificationProfiles;
    }

    @Column(name = "OPE", nullable = true, updatable = true)
    public Boolean getOpe() {
	return _ope;
    }

    @Override
    @ManyToOne(targetEntity = TmOrganization.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    public TmOrganization getOrganization() {
	return _organization;
    }

    @Embedded
    public Preferences getPreferences() {
	return _preferences;
    }

    @Transient
    public Map<Long, Set<String>> getProjectUserLanguages() {
	return _projectUserLanguages;
    }

    @Transient
    public Map<Long, Map<String, Set<String>>> getSubmissionUserLanguages() {
	return _submissionUserLanguages;
    }

    @Transient
    public String getUserName() {
	return getUserInfo().getUserName();
    }

    @Transient
    public UserTypeEnum getUserType() {
	return getUserInfo().getUserType();
    }

    public void initializeOrganizationUser(Map<Long, List<Role>> rolesMap, List<FolderPolicy> foldersPolicies,
	    List<FolderPolicy> adminFoldersPolicies, List<ProjectUserLanguage> projectUserLanguages,
	    List<Submission> submissions) {

	initializeProjectUserLanguages(projectUserLanguages);

	initializeSubmissionLanguages(submissions);

	computeContextsPolicies(rolesMap, true, getUserInfo().getUserType());
	flattenContextsRoles();

	initializePreferences(foldersPolicies, false);
	initializePreferences(adminFoldersPolicies, true);
    }

    public void initializePowerUser(Map<Long, List<Role>> rolesMap, List<FolderPolicy> foldersPolicies,
	    List<FolderPolicy> adminFoldersPolicies, Map<Long, Set<String>> projectLanguagesMap,
	    List<Submission> submissions) {

	initializeProjectLanguages(projectLanguagesMap);

	initializeSubmissionLanguages(submissions);

	computeContextsPolicies(rolesMap, true, getUserInfo().getUserType());
	flattenContextsRoles();

	initializePreferences(foldersPolicies, false);
	initializePreferences(adminFoldersPolicies, true);
    }

    @Transient
    public boolean isOpe() {
	return _ope != null ? _ope : false;
    }

    @Transient
    public boolean isPowerUser() {
	return UserTypeEnum.POWER_USER == getUserInfo().getUserType();
    }

    public void setAdminFolders(List<ItemFolderEnum> adminFolders) {
	_adminFolders = adminFolders;
    }

    public void setFolders(List<ItemFolderEnum> folders) {
	_folders = folders;
    }

    public void setGenericPassword(String genericPassword) {
	_genericPassword = genericPassword;
    }

    public void setHasChangedTerms(Boolean hasAddedNewTerms) {
	_hasChangedTerms = hasAddedNewTerms;
    }

    public void setHidden(Boolean hidden) {
	_hidden = hidden;
    }

    @Override
    public void setIdentifier(Long identifier) {
	setUserProfileId(identifier);
    }

    public void setLastDailyReport(Long lastDailyReport) {
	_lastDailyReport = lastDailyReport;
    }

    public void setLastWeeklyReport(Long lastWeeklyReport) {
	_lastWeeklyReport = lastWeeklyReport;
    }

    public void setNotificationProfiles(List<NotificationProfile> notificationProfiles) {
	_notificationProfiles = notificationProfiles;
    }

    public void setOpe(Boolean ope) {
	_ope = ope;
    }

    @Override
    public void setOrganization(TmOrganization organization) {
	_organization = organization;
    }

    public void setPreferences(Preferences preferences) {
	_preferences = preferences;
    }

    public void setProjectUserLanguages(Map<Long, Set<String>> projectUserLanguages) {
	_projectUserLanguages = projectUserLanguages;
    }

    public void setSubmissionUserLanguages(Map<Long, Map<String, Set<String>>> submissionUserLanguages) {
	_submissionUserLanguages = submissionUserLanguages;
    }

    private List<ItemFolderEnum> getFoldersByPolicies(List<FolderPolicy> termsFoldersPolicies) {
	Set<String> policies = getContextPolicies(DEFAULT_CONTEXT_ID);

	List<ItemFolderEnum> folders = new ArrayList<>();

	if (policies != null) {
	    for (FolderPolicy folderPolicy : termsFoldersPolicies) {
		if (policies.contains(folderPolicy.getPolicy())) {
		    folders.add(folderPolicy.getFolder());
		}
	    }
	}
	return folders;
    }

    private void initializePreferences(List<FolderPolicy> foldersPolicies, boolean isAdmin) {
	Preferences preferences = getPreferences();

	if (preferences == null) {
	    _logger.warn(String.format(Messages.getString("UserProfile.2"), getUserInfo().getUserName()));

	    return;
	}

	List<ItemFolderEnum> folders = getFoldersByPolicies(foldersPolicies);

	if (isAdmin) {
	    if (folders.isEmpty()) {
		setAdminFolders(null);
	    } else {
		setAdminFolders(folders);
	    }
	} else {
	    setFolders(folders);
	}
    }

    private void initializeProjectLanguages(Map<Long, Set<String>> projectLanguagesMap) {
	if (MapUtils.isNotEmpty(projectLanguagesMap)) {
	    Map<Long, Set<String>> projectUserLanguagesMap = getProjectUserLanguages();
	    for (Entry<Long, Set<String>> entry : projectLanguagesMap.entrySet()) {
		Long projectId = entry.getKey();
		Set<String> languages = projectUserLanguagesMap.computeIfAbsent(projectId, k -> new HashSet<>());
		languages.addAll(entry.getValue());
	    }
	}
    }

    private void initializeProjectUserLanguages(List<ProjectUserLanguage> projectUserLanguages) {
	if (CollectionUtils.isNotEmpty(projectUserLanguages)) {
	    Map<Long, Set<String>> projectUserLanguagesMap = getProjectUserLanguages();
	    for (ProjectUserLanguage projectUserLanguage : projectUserLanguages) {
		Long projectId = projectUserLanguage.getProject().getProjectId();
		Set<String> projectLanguages = projectUserLanguagesMap.computeIfAbsent(projectId, k -> new HashSet<>());
		projectLanguages.add(projectUserLanguage.getLanguage());
	    }
	}
    }

    private void initializeSubmissionLanguages(List<Submission> submissions) {
	if (CollectionUtils.isNotEmpty(submissions)) {
	    Map<Long, Set<String>> projectUserLanguages = getProjectUserLanguages();
	    Map<Long, Map<String, Set<String>>> submissionUserLanguages = getSubmissionUserLanguages();
	    for (Submission submission : submissions) {
		Long projectId = submission.getProject().getProjectId();
		Set<String> userLanguages = projectUserLanguages.get(projectId);
		if (userLanguages == null) {
		    continue;
		}

		Long submissionId = submission.getSubmissionId();
		Map<String, Set<String>> subLanguages = submissionUserLanguages.computeIfAbsent(submissionId,
			k -> new HashMap<>());

		String sourceLanguageId = submission.getSourceLanguageId();
		Set<String> subTargetLanguages = subLanguages.computeIfAbsent(sourceLanguageId, k -> new HashSet<>());

		Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();

		String userName = getUserName();

		boolean isSubmitter = userName.equals(submission.getSubmitter());
		boolean isPowerUser = isPowerUser();

		for (SubmissionLanguage subLanguage : submissionLanguages) {
		    String languageId = subLanguage.getLanguageId();
		    if (!userLanguages.contains(languageId)) {
			continue;
		    }

		    if (isPowerUser || isSubmitter) {
			subTargetLanguages.add(languageId);
		    } else if (userName.equals(subLanguage.getAssignee())) {
			subTargetLanguages.add(languageId);
		    }
		}
	    }
	}
    }

    @Override
    protected Metadata createMetadataInstance(String key, String value) {
	return new UserProfileMetadata(key, value);
    }

}
