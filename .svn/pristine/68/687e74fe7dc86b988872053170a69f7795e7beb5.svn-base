package org.gs4tr.termmanager.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.foundation.modules.dao.LoadProvider;
import org.gs4tr.foundation.modules.entities.model.ImpersonateUserInfo;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.foundation.modules.entities.model.search.UserProfileSearchRequest;
import org.gs4tr.foundation.modules.security.dao.PolicyDAO;
import org.gs4tr.foundation.modules.security.dao.RoleDAO;
import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.usermanager.service.impl.AbstractUserProfileServiceImpl;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.dao.OrganizationDAO;
import org.gs4tr.termmanager.dao.PowerUserProjectRoleDAO;
import org.gs4tr.termmanager.dao.ProjectDAO;
import org.gs4tr.termmanager.dao.ProjectLanguageDAO;
import org.gs4tr.termmanager.dao.ProjectUserDetailDAO;
import org.gs4tr.termmanager.dao.ProjectUserLanguageDAO;
import org.gs4tr.termmanager.dao.SubmissionDAO;
import org.gs4tr.termmanager.dao.SubmissionUserDAO;
import org.gs4tr.termmanager.dao.UserCustomSearchDAO;
import org.gs4tr.termmanager.dao.UserProfileDAO;
import org.gs4tr.termmanager.dao.UserProfileSearchDAO;
import org.gs4tr.termmanager.dao.UserProjectRoleSearchDAO;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.FolderPolicy;
import org.gs4tr.termmanager.model.NotificationProfile;
import org.gs4tr.termmanager.model.Preferences;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectPolicyEnum;
import org.gs4tr.termmanager.model.ProjectUserDetail;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmPowerUserProjectRole;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UserCustomSearch;
import org.gs4tr.termmanager.model.UserProjectRole;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.utils.AdminTasksHolderHelper;
import org.gs4tr.termmanager.service.utils.MailHelper.ReportType;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userProfileService")
public class UserProfileServiceImpl extends AbstractUserProfileServiceImpl<TmUserProfile>
	implements UserProfileService {

    private List<FolderPolicy> _adminFoldersPolicies;

    private List<FolderPolicy> _foldersPolicies;

    @Autowired
    private OrganizationDAO _organizationDAO;

    @Autowired
    @Qualifier("passwordEncoder")
    private PasswordEncoder _passwordEncoder;

    @Autowired
    private PolicyDAO _policyDAO;

    @Autowired
    private PowerUserProjectRoleDAO _powerUserProjectRoleDAO;

    @Autowired
    private ProjectDAO _projectDAO;

    @Autowired
    private ProjectLanguageDAO _projectLanguageDAO;

    @Autowired
    private ProjectUserDetailDAO _projectUserDetailDAO;

    @Autowired
    private ProjectUserLanguageDAO _projectUserLanguageDAO;

    @Autowired
    private RoleDAO _roleDao;

    @Autowired
    private SubmissionDAO _submissionDAO;

    @Autowired
    private SubmissionUserDAO _submissionUserDAO;

    @Autowired
    private AdminTasksHolderHelper _tasksHolderHelper;

    @Autowired
    private UserCustomSearchDAO _userCustomSearchDAO;

    @Autowired
    private UserProfileDAO _userProfileDAO;

    @Autowired
    private UserProfileSearchDAO _userProfileSearchDAO;

    @Autowired
    private UserProjectRoleSearchDAO _userProjectRoleSearchDAO;

    @Override
    @Transactional
    public Long addOrUpdateCustomSearchFolder(TmUserProfile userProfile, String folder, String originalFolder,
	    String url, String searchJsonData, boolean adminFolder) {

	Long userProfileId = userProfile.getUserProfileId();

	UserCustomSearchDAO userCustomSearchDAO = getUserCustomSearchDAO();

	UserCustomSearch userCustomSearch = userCustomSearchDAO.findByUserProfileIdAndFolderName(userProfileId, folder);

	if (userCustomSearch == null) {
	    userCustomSearch = new UserCustomSearch();
	}

	userCustomSearch.setCustomFolder(folder);
	userCustomSearch.setSearchJsonData(searchJsonData);
	userCustomSearch.setUserProfile(userProfile);
	userCustomSearch.setOriginalFolder(originalFolder);
	userCustomSearch.setAdminFolder(adminFolder);
	userCustomSearch.setUrl(url);

	userCustomSearchDAO.saveOrUpdate(userCustomSearch);

	String originalMetadata = userProfile.getMetadataValue(originalFolder.toLowerCase());
	if (StringUtils.isNotEmpty(originalMetadata)) {
	    addOrUpdateMetadata(folder.toLowerCase(), originalMetadata);
	}

	return userCustomSearch.getCustomSearchId();
    }

    @Override
    @Transactional
    public void addOrUpdateNotificationProfiles(TmUserProfile currentUserProfile,
	    List<NotificationProfile> newNotificationProfiles) {
	currentUserProfile.setNotificationProfiles(newNotificationProfiles);

	update(currentUserProfile);
    }

    @Override
    @Transactional
    public Long addOrUpdateProjectUserDetails(Long userId, Long projectId, List<Long> newUserIds) {

	if (CollectionUtils.isEmpty(newUserIds)) {
	    return projectId;
	}

	TmUserProfile user = getUserProfileDAO().load(userId);
	if (user.getGeneric()) {
	    return projectId;
	}

	List<ProjectUserDetail> userDetails = getProjectUserDetailDAO().findByProjectId(projectId);

	TmProject project = getProjectDAO().load(projectId);
	ProjectDetail projectDetail = project.getProjectDetail();

	ProjectUserDetail userDetail = new ProjectUserDetail(user, projectDetail);

	if (!userDetails.contains(userDetail)) {
	    getProjectUserDetailDAO().save(userDetail);
	}

	return projectId;
    }

    @Override
    @Transactional
    public Long addOrUpdateUserProjectRoles(Long userId, Long projectId, List<Role> roles, List<Long> newUsers,
	    final UserTypeEnum userType, Boolean isGeneric) {
	if (newUsers.size() == 0) {
	    List<UserProjectRole> userProjectRoles = getUserProjectRoleSearchDAO().findAll();

	    for (UserProjectRole projectRole : userProjectRoles) {
		TmUserProfile userProfile = projectRole.getUserProfile();
		if (!isGeneric.equals(userProfile.getGeneric())) {
		    continue;
		}

		if (userType == userProfile.getUserInfo().getUserType()
			&& projectRole.getProject().getProjectId().equals(projectId)) {
		    getUserProjectRoleSearchDAO().delete(projectRole);
		}
	    }

	    return projectId;
	}

	List<UserProjectRole> oldUserProjectRoles = getUserProjectRoleSearchDAO().getAllUserProjectRoles(userId,
		projectId);

	List<TmUserProfile> oldUsers = getUserProjectRoleSearchDAO().getUsersByProject(projectId);
	List<Long> oldUserIds = new ArrayList<>();
	for (TmUserProfile odlUser : oldUsers) {
	    if (!isGeneric.equals(odlUser.getGeneric())) {
		continue;
	    }

	    if (userType == odlUser.getUserInfo().getUserType()) {
		oldUserIds.add(odlUser.getUserProfileId());
	    }
	}

	oldUserIds.removeAll(newUsers);

	for (Long oldUserId : oldUserIds) {
	    List<UserProjectRole> rolesForRemove = getUserProjectRoleSearchDAO().getAllUserProjectRoles(oldUserId,
		    projectId);
	    for (UserProjectRole projectRole : rolesForRemove) {
		getUserProjectRoleSearchDAO().delete(projectRole);
	    }
	}

	List<Role> rolesList = new ArrayList<>(roles);

	List<Role> rolesListForSkipFromCreation = new ArrayList<>();

	for (UserProjectRole userProjectRole : oldUserProjectRoles) {
	    Role oldRole = userProjectRole.getRole();
	    TmUserProfile oldUser = userProjectRole.getUserProfile();

	    if (!newUsers.contains(oldUser.getUserProfileId())) {
		getUserProjectRoleSearchDAO().delete(userProjectRole);
		continue;
	    }
	    if (rolesList.contains(oldRole)) {
		rolesListForSkipFromCreation.add(userProjectRole.getRole());
	    } else {
		getUserProjectRoleSearchDAO().delete(userProjectRole);
	    }
	}
	rolesList.removeAll(rolesListForSkipFromCreation);
	TmUserProfile user = getUserProfileDAO().load(userId);
	TmProject project = getProjectDAO().load(projectId);
	for (Role role : rolesList) {
	    Role newRole = getRoleDao().findById(role.getRoleId());
	    UserProjectRole newUserProjectRole = new UserProjectRole(project, newRole, user);
	    getUserProjectRoleSearchDAO().save(newUserProjectRole);
	}
	return projectId;
    }

    @Override
    @Transactional
    public Long changeCurrentUserPassword(String oldPassword, String newPassword) {
	UserInfo currentUserInfo = UserProfileContext.getCurrentUserProfile().getUserInfo();

	String currentUsername = currentUserInfo.getUserName();
	TmUserProfile userProfile = findUsersByUserNameNoFetch(currentUsername);

	changeUserProfileOrLdapPassword(userProfile, newPassword, oldPassword);
	update(userProfile);

	currentUserInfo.setPassword(userProfile.getUserInfo().getPassword());
	return userProfile.getIdentifier();
    }

    @Transactional
    @Override
    public Long createOrUpdateGenericUser(Long userId, UserInfo userInfo, Long organizationId) {
	if (userInfo == null) {
	    return null;
	}

	String password = userInfo.getPassword();

	if (StringUtils.isBlank(userInfo.getUserName()) && StringUtils.isBlank(password)) {
	    return userId;
	}

	String encodePassword = getPasswordEncoder().encode(password);

	TmUserProfile userProfile = null;
	if (userId != null) {
	    userProfile = getUserProfileDAO().findById(userId);
	}

	if (userProfile != null && !userProfile.isOpe()) {

	    UserInfo existingUserInfo = userProfile.getUserInfo();

	    if (StringUtils.isNotBlank(userInfo.getUserName())) {
		existingUserInfo.setUserName(userInfo.getUserName());
	    }

	    if (StringUtils.isNotBlank(password)) {
		existingUserInfo.setPassword(encodePassword);
		userProfile.setGenericPassword(password);
	    }

	    update(userProfile);

	    return userProfile.getUserProfileId();
	}

	userProfile = createBaseUserProfileInstance(userInfo);

	userInfo.setPassword(encodePassword);

	userProfile.setUserInfo(userInfo);
	userProfile.setGeneric(Boolean.TRUE);
	userProfile.setGenericPassword(password);

	TmOrganization organization = getOrganizationDAO().findById(organizationId);

	userProfile.setOrganization(organization);

	Set<Role> roles = new HashSet<>();
	Role systemGenericUserRole = getRoleDao().findById(ServiceUtils.SYSTEM_GENERIC_USER_ROLE_NAME);
	roles.add(systemGenericUserRole);

	userProfile.setSystemRoles(roles);

	try {
	    userProfile = save(userProfile);
	} catch (Exception e) {
	    throw new UserException(String.format(MessageResolver.getMessage("UserProfileServiceImpl.7"), //$NON-NLS-1$
		    userInfo.getUserName()), e);
	}

	return userProfile.getUserProfileId();
    }

    @Transactional
    @Override
    public void disableUserProfiles(List<Long> userIds) {
	getUserProfileDAO().disableUserProfiles(userIds);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean exists(String username) {
	boolean exists = true;

	try {
	    TmUserProfile userProfile = findUsersByUserNameNoFetch(username);
	    if (userProfile == null) {
		exists = false;
	    }
	} catch (Exception e) {
	    exists = false;
	}

	return exists;
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> findAllNonGenerciUsernames() {
	List<String> userNames = new ArrayList<>();

	List<String> orgUserNames = getUserProfileDAO().findAllNonGenerciUsernames();
	if (CollectionUtils.isNotEmpty(orgUserNames)) {
	    userNames.addAll(orgUserNames);
	}

	List<String> powerUserNames = getUserProfileDAO().findUsernamesByType(UserTypeEnum.POWER_USER);
	if (CollectionUtils.isNotEmpty(powerUserNames)) {
	    userNames.addAll(powerUserNames);
	}

	return userNames;
    }

    @Transactional(readOnly = true)
    @Override
    public List<TmUserProfile> findGenericUserByProjectId(Long projectId) {
	List<TmUserProfile> genericUsers = new ArrayList<>();

	List<TmUserProfile> genericUserByProjectId = getUserProfileDAO().findDistinctGenericUserByProjectId(projectId);

	if (CollectionUtils.isNotEmpty(genericUserByProjectId)) {
	    genericUsers.addAll(genericUserByProjectId);
	}

	return genericUsers;
    }

    @Transactional(readOnly = true)
    @Override
    public TmUserProfile findUserByUsernameFetchNotifications(String username) {
	if (StringUtils.isEmpty(username)) {
	    return null;
	}

	return getUserProfileDAO().findUserProfileByUserNameFetchNotifications(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<ImpersonateUserInfo> findUserInfosForImpersonation() {

	Set<ImpersonateUserInfo> sortedUsersInfo = new TreeSet<>(createNewComparator());

	sortedUsersInfo.addAll(getUserProfileDAO().findUserNamesForImpersonation());

	return sortedUsersInfo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findUserProfileIdsByCriteria(LoadProvider loadProvider) {
	return loadAllIds(loadProvider);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmUserProfile> findUserProfilesByCriteria(LoadProvider loadProvider) {
	return loadAll(loadProvider);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProjectRole> findUserProjectRolesByProject(Long projectId) {
	return getUserProjectRoleSearchDAO().getProjectUserRolesByProject(projectId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TmUserProfile> findUsersByOrganization(Long organizationId) {
	return getUserProfileDAO().findUsersByOrganization(organizationId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TmUserProfile> findUsersByOrganizationFetchLanguages(TmOrganization organization,
	    boolean showGenericUsers) {
	List<TmUserProfile> users = getUserProfileDAO()
		.findUsersByOrganizationFetchLanguages(organization.getOrganizationId(), showGenericUsers);

	TmOrganization parentOrganization = organization.getParentOrganization();

	while (parentOrganization != null) {
	    users.addAll(getUserProfileDAO()
		    .findUsersByOrganizationFetchLanguages(parentOrganization.getOrganizationId(), showGenericUsers));

	    parentOrganization = parentOrganization.getParentOrganization();
	}

	return users;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmUserProfile> findUsersByType(UserTypeEnum type) {
	return getUserProfileDAO().findAllByProperty("userInfo.userType", type); //$NON-NLS-1$
    }

    @Override
    @Transactional(readOnly = true)
    public TmUserProfile findUsersByUserNameNoFetch(String currentUsername) {
	return getUserProfileDAO().findUsersByUserNameNoFetch(currentUsername);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TmUserProfile> findUsersByUsernames(Collection<String> usernames) {
	if (CollectionUtils.isEmpty(usernames)) {
	    return null;
	}

	return getUserProfileDAO().findUsersByUsernames(usernames);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmUserProfile> getAllNonGenericEnabledUsers() {
	return getUserProjectRoleSearchDAO().findAllNonGenericEnabledUsers();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<TmUserProfile> getAllPowerAndOrgUsers(List<Long> projectIds) {
	Set<TmUserProfile> users = new HashSet<>();

	if (CollectionUtils.isEmpty(projectIds)) {
	    return users;
	}

	List<TmUserProfile> orgUsers = getUserProjectRoleSearchDAO().findUsersForReport();
	if (CollectionUtils.isNotEmpty(orgUsers)) {
	    users.addAll(orgUsers);
	}

	List<TmUserProfile> powerUsers = getUserProfileDAO().findUsersByType(UserTypeEnum.POWER_USER);
	if (CollectionUtils.isNotEmpty(powerUsers)) {
	    users.addAll(powerUsers);
	}

	return users;
    }

    @Override
    @Transactional(readOnly = true)
    public UserCustomSearch getCustomSearchFolder(TmUserProfile currentUser, String folder) {
	return getUserCustomSearchDAO().findByUserProfileIdAndFolderName(currentUser.getIdentifier(), folder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCustomSearch> getCustomSearchFolders(TmUserProfile userProfile, boolean adminFolders) {
	return getUserCustomSearchDAO().findByUserProfileId(userProfile.getUserProfileId(), adminFolders);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getNotificationClassifiers(Long userId, ReportType reportType) {
	TmUserProfile user = load(userId);

	List<String> userClassifiers = new ArrayList<>();
	List<NotificationProfile> notificationProfiles = user.getNotificationProfiles();
	for (NotificationProfile np : notificationProfiles) {
	    if (np == null) {
		continue;
	    }

	    switch (reportType) {
	    case DAILY:
		if (np.isSendDailyMailNotification()) {
		    userClassifiers.add(np.getNotificationClassifier());
		}
		break;
	    case WEEKLY:
		if (np.isSendWeeklyMailNotification()) {
		    userClassifiers.add(np.getNotificationClassifier());
		}
		break;
	    }
	}

	return userClassifiers;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmUserProfile> getOrgUsersForReport() {
	return getUserProjectRoleSearchDAO().findUsersForReport();
    }

    @Override
    public PasswordEncoder getPasswordEncoder() {
	return _passwordEncoder;
    }

    @Override
    public PolicyDAO getPolicyDAO() {
	return _policyDAO;
    }

    @Override
    public RoleDAO getRoleDao() {
	return _roleDao;
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotificationProfile> getUserNotificationProfiles(Long userId) {
	List<NotificationProfile> notificationProfiles = load(userId).getNotificationProfiles();
	if (CollectionUtils.isNotEmpty(notificationProfiles)) {
	    return notificationProfiles;
	}
	return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Preferences getUserPreferences(Long userId) {
	return load(userId).getPreferences();
    }

    @Override
    public UserProfileDAO getUserProfileDAO() {
	return _userProfileDAO;
    }

    @Override
    public UserProfileSearchDAO getUserProfileSearchDAO() {
	return _userProfileSearchDAO;
    }

    @Transactional(readOnly = true)
    public List<Role> getUsersProjectRoles(Long userProfileId, Long projectId) {
	return getUserProjectRoleSearchDAO().getRolesByUserAndProject(userProfileId, projectId);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isAssigneeUser(Long userId) {
	TmUserProfile user = load(userId);

	boolean containsTranslatorView = ServiceUtils.containsTranslationView(user);

	Policy autoSave = new Policy();
	autoSave.setPolicyId(ProjectPolicyEnum.POLICY_TM_TERM_AUTO_SAVE_TRANSLATION.name());

	boolean containsAutoSave = userContainsPolicy(userId, autoSave);

	return containsTranslatorView && containsAutoSave;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isGenericUserProfile(String userName) {
	boolean isGeneric; // false

	try {
	    TmUserProfile userProfile = findUsersByUserNameNoFetch(userName);
	    if (userProfile == null) {
		isGeneric = true;
	    } else {
		isGeneric = userProfile.getGeneric();
	    }
	} catch (Exception e) {
	    isGeneric = true;
	}

	return isGeneric;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isSubmitterUser(Long userId) {
	TmUserProfile user = load(userId);
	if (user.isPowerUser()) {
	    return true;
	}

	boolean containsTranslatorView = ServiceUtils.containsTranslationView(user);

	Policy submitPolicy = new Policy();
	submitPolicy.setPolicyId(ProjectPolicyEnum.POLICY_TM_SEND_TO_TRANSLATION_REVIEW.name());

	boolean containsSubmit = userContainsPolicy(userId, submitPolicy);

	return containsSubmit && containsTranslatorView;
    }

    @Override
    @Transactional
    public boolean removeCustomSearchFolder(TmUserProfile userProfile, String folder) {
	Long userProfileId = userProfile.getUserProfileId();
	UserCustomSearch userCustomSearch = getUserCustomSearchDAO().findByUserProfileIdAndFolderName(userProfileId,
		folder);
	if (userCustomSearch != null) {
	    getUserCustomSearchDAO().delete(userCustomSearch);
	    return true;
	} else {
	    return false;
	}
    }

    @Override
    @Transactional(readOnly = true)
    public TaskPagedList<TmUserProfile> search(UserProfileSearchRequest command, PagedListInfo pagedListInfo) {
	PagedList<TmUserProfile> entityPagedList = getUserProfileSearchDAO().getEntityPagedList(command, pagedListInfo);

	return getTasksHolderHelper().assignTasks(entityPagedList, null, EntityTypeHolder.USER);
    }

    @Value("#{adminFoldersPolicies}")
    public void setAdminFoldersPolicies(List<FolderPolicy> adminFoldersPolicies) {
	_adminFoldersPolicies = adminFoldersPolicies;
    }

    @Override
    @Value("#{userProfileDAO}")
    public void setEntityDao(GenericDao<TmUserProfile, Long> entityDao) {
	super.setEntityDao(entityDao);
    }

    @Value("#{foldersPolicies}")
    public void setFoldersPolicies(List<FolderPolicy> foldersPolicies) {
	_foldersPolicies = foldersPolicies;
    }

    @Override
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
	_passwordEncoder = passwordEncoder;
    }

    @Override
    public void setRoleDao(RoleDAO roleDao) {
	_roleDao = roleDao;
    }

    @Override
    @Transactional
    public void updateHasChangedTerms(Boolean hasChangedTerms) {
	TmUserProfile currentUserProfile = TmUserProfile.getCurrentUserProfile();

	getUserProfileDAO().updatePropertyValue(currentUserProfile.getUserProfileId(), "hasChangedTerms", //$NON-NLS-1$
		hasChangedTerms);
    }

    @Override
    @Transactional
    public Long updatePreferences(Preferences preferences) {
	if (preferences == null) {
	    throw new RuntimeException(Messages.getString("UserProfileServiceImpl.2")); //$NON-NLS-1$
	}
	TmUserProfile currentUserProfile = TmUserProfile.getCurrentUserProfile();

	currentUserProfile.setPreferences(preferences);

	update(currentUserProfile);

	return ServiceUtils.createGenericId();
    }

    @Transactional
    public void updateReportDate(Long userId, String timeZone, ReportType reportType) {
	Calendar ca = Calendar.getInstance();
	ca.setTimeZone(TimeZone.getTimeZone(timeZone));
	ca.setTime(new Date());

	long timeInMillis = ca.getTimeInMillis();
	if (ReportType.DAILY == reportType) {
	    getUserProfileDAO().updatePropertyValue(userId, "lastDailyReport", timeInMillis);
	} else if (ReportType.WEEKLY == reportType) {
	    getUserProfileDAO().updatePropertyValue(userId, "lastWeeklyReport", timeInMillis);
	}
    }

    private void changeUserProfileOrLdapPassword(TmUserProfile userProfile, String newPassword, String oldPassword) {
	String currentEncodedPassword = userProfile.getUserInfo().getPassword();

	String enteredEncodedOldPassword = StringUtils.isEmpty(oldPassword) ? currentEncodedPassword
		: getPasswordEncoder().encode(oldPassword);

	boolean ldapChanged = ServiceUtils.updateLdapPassword(userProfile.getUserInfo().getUserName(), oldPassword,
		newPassword, null);

	if (!ldapChanged) {
	    String encodedNewPassword = getPasswordEncoder().encode(newPassword);

	    if (enteredEncodedOldPassword != null && currentEncodedPassword.compareTo(enteredEncodedOldPassword) != 0) {
		throw new UserException(MessageResolver.getMessage("UserProfileServiceImpl.6")); //$NON-NLS-1$
	    }

	    if (encodedNewPassword.compareTo(currentEncodedPassword) == 0) {
		throw new UserException(MessageResolver.getMessage("UserProfileServiceImpl.8")); //$NON-NLS-1$
	    }

	    userProfile.getUserInfo().setPassword(encodedNewPassword);
	    userProfile.getUserInfo().setDatePasswordChanged(new Date());
	}
    }

    private Comparator<ImpersonateUserInfo> createNewComparator() {
	return (u1, u2) -> u1.getUserName().compareToIgnoreCase(u2.getUserName());
    }

    private List<FolderPolicy> getAdminFoldersPolicies() {
	return _adminFoldersPolicies;
    }

    private List<FolderPolicy> getFoldersPolicies() {
	return _foldersPolicies;
    }

    private OrganizationDAO getOrganizationDAO() {
	return _organizationDAO;
    }

    private PowerUserProjectRoleDAO getPowerUserProjectRoleDAO() {
	return _powerUserProjectRoleDAO;
    }

    private ProjectDAO getProjectDAO() {
	return _projectDAO;
    }

    private ProjectLanguageDAO getProjectLanguageDAO() {
	return _projectLanguageDAO;
    }

    private ProjectUserDetailDAO getProjectUserDetailDAO() {
	return _projectUserDetailDAO;
    }

    private ProjectUserLanguageDAO getProjectUserLanguageDAO() {
	return _projectUserLanguageDAO;
    }

    private SubmissionDAO getSubmissionDAO() {
	return _submissionDAO;
    }

    private SubmissionUserDAO getSubmissionUserDAO() {
	return _submissionUserDAO;
    }

    private AdminTasksHolderHelper getTasksHolderHelper() {
	return _tasksHolderHelper;
    }

    private UserCustomSearchDAO getUserCustomSearchDAO() {
	return _userCustomSearchDAO;
    }

    private UserProjectRoleSearchDAO getUserProjectRoleSearchDAO() {
	return _userProjectRoleSearchDAO;
    }

    private void unlinkProjectUserLanguagesByUser(Long userId, Set<String> languagesForRemoval) {
	List<String> tempLanguagesForRemoval = new ArrayList<>(languagesForRemoval);

	List<ProjectUserLanguage> projectUserLanguages = getProjectUserLanguageDAO()
		.findAllByProperty("user.userProfileId", userId); //$NON-NLS-1$

	for (ProjectUserLanguage projectUserLanguage : projectUserLanguages) {
	    if (tempLanguagesForRemoval.contains(projectUserLanguage.getLanguage())) {
		getProjectUserLanguageDAO().delete(projectUserLanguage);
	    }
	}
    }

    private boolean userContainsPolicy(Long userId, Policy policy) {
	Map<Long, List<Role>> userProjectRoles = getUserProjectRoleSearchDAO().getProjectRolesByUserId(userId);
	for (Entry<Long, List<Role>> entry : userProjectRoles.entrySet()) {
	    List<Role> roles = entry.getValue();
	    for (Role role : roles) {
		Set<Policy> policies = role.getPolicies();
		if (policies.contains(policy)) {
		    return true;

		}
	    }
	}
	return false;
    }

    @Override
    protected TmUserProfile createBaseUserProfileInstance(UserInfo userInfo) {
	TmUserProfile userProfile = new TmUserProfile();
	userProfile.setPreferences(new Preferences());
	return userProfile;
    }

    @Override
    protected void postProcessUserProfile(TmUserProfile user) {

	if (user.isPowerUser()) {
	    Map<Long, List<Role>> projectRoles = new HashMap<>();

	    Role powerRole;

	    Map<Long, Set<String>> projectLanguagesMap = getProjectLanguageDAO().getProjectLanguagesMap();

	    TmPowerUserProjectRole powerUserProjectRole = getPowerUserProjectRoleDAO()
		    .findByUserId(user.getUserProfileId());

	    if (Objects.nonNull(powerUserProjectRole)) {
		powerRole = powerUserProjectRole.getRole();
	    } else {
		powerRole = getRoleDao().findById(ServiceUtils.POWER_USER_PROJECT_ROLE_NAME);
	    }

	    for (Long projectId : projectLanguagesMap.keySet()) {
		List<Role> roleList = projectRoles.computeIfAbsent(projectId, k -> new ArrayList<>());
		roleList.add(powerRole);
	    }

	    List<Submission> allSubs = getSubmissionDAO().findAllSubmissions();

	    user.initializePowerUser(projectRoles, getFoldersPolicies(), getAdminFoldersPolicies(), projectLanguagesMap,
		    allSubs);

	    return;
	}

	Map<Long, List<Role>> projectRoles = new HashMap<>();
	List<ProjectUserLanguage> projectUserLanguages = new ArrayList<>();
	List<Submission> submissions = new ArrayList<>();

	if (user.getOrganization() != null) {
	    Long userId = user.getUserProfileId();

	    projectRoles = getUserProjectRoleSearchDAO().getProjectRolesByUserId(userId);

	    projectUserLanguages = getProjectUserLanguageDAO().getProjectUserLanguagesByUser(userId);

	    if (!user.isGenericUser()) {
		submissions = getSubmissionUserDAO().findSubmissionsByUserId(userId);
	    }
	}

	user.initializeOrganizationUser(projectRoles, getFoldersPolicies(), getAdminFoldersPolicies(),
		projectUserLanguages, submissions);
    }
}
