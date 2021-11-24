package org.gs4tr.termmanager.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.foundation.modules.organization.model.BaseOrganizationPolicyEnum;
import org.gs4tr.foundation.modules.project.model.BaseProjectPolicyEnum;
import org.gs4tr.foundation.modules.security.dao.PolicyDAO;
import org.gs4tr.foundation.modules.security.dao.RoleDAO;
import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.foundation.modules.security.model.PolicyEnum;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.security.model.RoleTypeEnum;
import org.gs4tr.foundation.modules.security.model.SecurityPolicyEnum;
import org.gs4tr.foundation.modules.usermanager.model.SystemAuthenticationHolder;
import org.gs4tr.foundation.modules.usermanager.model.UserManagerPolicyEnum;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.termmanager.dao.UserProfileDAO;
import org.gs4tr.termmanager.model.Preferences;
import org.gs4tr.termmanager.model.ProjectPolicyEnum;
import org.gs4tr.termmanager.model.TmPolicyEnum;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.SetupService;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "setupService")
public class SetupServiceImpl implements SetupService {

    private static final String ADMIN_PASSWORD = "sup3rs3cur3";

    private static final String ADMIN_ROLE_NAME = "admin";

    private static final String ADMIN_USER_NAME = "admin";

    private static final String GENERIC_USER_ROLE = "generic_user";

    private static final String POWER_USER_SYSTEM_ROLE_NAME = "system_power_user";

    private static final String SUPER_USER_ROLE = "super_user";

    private static final String SYSTEM_SUPER_USER_ROLE_NAME = "system_super_user";

    private static final String TRANSLATOR_USER_SYSTEM_ROLE_NAME = "system_translator_user";

    private static final PolicyEnum[][] policyEnums = new PolicyEnum[][] { SecurityPolicyEnum.policies(),
	    UserManagerPolicyEnum.policies(), BaseProjectPolicyEnum.policies(), BaseOrganizationPolicyEnum.policies(),
	    TmPolicyEnum.policies() };

    private boolean _impersonateDisabled = true;

    @Autowired
    @Qualifier("passwordEncoder")
    private PasswordEncoder _passwordEncoder;

    private Set<Policy> _policies = null;

    @Autowired
    private PolicyDAO _policyDAO;

    @Autowired
    private RoleDAO _roleDAO;

    @Autowired
    private SessionService _sessionService;

    @Autowired(required = false)
    private SwitchUserFilter _switchUserProcessingFilter;

    @Autowired
    private UserProfileDAO _userProfileDAO;

    @Override
    @Transactional
    public void setup() {
	boolean impersonateDisabled = getSwitchUserProcessingFilter() == null;
	setImpersonateDisabled(impersonateDisabled);

	List<Policy> existingPolicies = getPolicyDAO().findAll();
	setPolicies(createPolicySet(existingPolicies));

	Role adminRole = getRoleDAO().findByName(ADMIN_ROLE_NAME);

	// in case we have new project policies,
	// those will be created into the database
	// createPowerUserProjectPolicySet();

	if (adminRole != null) {
	    createSystemUser(adminRole);
	    authenticateSystemUser();
	    createSuperUserRole();
	    createSuperUserSystemRole();
	    createGenericUserSystemRole();
	    createGenericUserRole();
	    createPowerUserSystemRole();
	    createPowerUserProjectRole();
	    createTranslatorUserProjectRole();
	    createTranslatorUserSystemRole();
	    return;
	}

	adminRole = createAdminRole(getPolicies());
	createAdminUser(adminRole);
	createProjectPolicySet();
	createSystemUser(adminRole);
	authenticateSystemUser();
	createSuperUserRole();
	createSuperUserSystemRole();
	createGenericUserSystemRole();
	createGenericUserRole();
	createPowerUserSystemRole();
	createPowerUserProjectRole();
	createTranslatorUserProjectRole();
	createTranslatorUserSystemRole();
    }

    private void authenticateSystemUser() {
	SessionService sessionService = getSessionService();
	sessionService.login(SystemAuthenticationHolder.SYSTEM_USERNAME, SystemAuthenticationHolder.SYSTEM_PASSWORD);

	Authentication systemAuthentication = SecurityContextHolder.getContext().getAuthentication();

	SystemAuthenticationHolder.setSystemAuthentication(systemAuthentication);
    }

    private Role createAdminRole(Set<Policy> policies) {
	RoleDAO roleDao = getRoleDAO();

	Role adminRole = new Role();

	adminRole.setRoleType(RoleTypeEnum.SYSTEM);

	Policy policy = createPolicy(TmPolicyEnum.POLICY_TM_MENU_TERMS.name(), RoleTypeEnum.SYSTEM, "Term");
	policies.remove(policy);

	TmPolicyEnum viewTranslatorInbox = TmPolicyEnum.POLICY_TM_VIEW_TRANSLATOR_INBOX;
	Policy policyViewTranslatorInbox = createPolicy(viewTranslatorInbox.name(), RoleTypeEnum.SYSTEM,
		viewTranslatorInbox.getCategory());
	policies.remove(policyViewTranslatorInbox);

	Policy twoFactorAuth = createPolicy(SecurityPolicyEnum.POLICY_FOUNDATION_SECURITY_TWO_FACTOR_AUTH.name(),
		RoleTypeEnum.SYSTEM, "User");
	policies.remove(twoFactorAuth);

	adminRole.setPolicies(policies);
	adminRole.setRoleId(ADMIN_ROLE_NAME);

	adminRole = roleDao.save(adminRole);

	return adminRole;
    }

    private void createAdminUser(Role adminRole) {
	UserProfileDAO userProfileDao = getUserProfileDAO();

	TmUserProfile adminUserProfile = new TmUserProfile();

	UserInfo userInfo = new UserInfo();

	userInfo.setUserName(ADMIN_USER_NAME);
	userInfo.setPassword(encodePassword(ADMIN_PASSWORD));
	userInfo.setEnabled(true);
	userInfo.setUserType(UserTypeEnum.ORGANIZATION);

	Preferences preferences = new Preferences();

	adminUserProfile.setUserInfo(userInfo);

	Set<Role> roles = new HashSet<>();
	roles.add(adminRole);

	adminUserProfile.setSystemRoles(roles);
	adminUserProfile.setPreferences(preferences);

	userProfileDao.save(adminUserProfile);
    }

    private void createGenericUserRole() {

	if (getRoleDAO().findByName(GENERIC_USER_ROLE) != null) {
	    return;
	}

	Role genericUserRole = new Role();
	genericUserRole.setIdentifier(GENERIC_USER_ROLE);
	genericUserRole.setGeneric(Boolean.TRUE);
	genericUserRole.setRoleId(GENERIC_USER_ROLE);
	genericUserRole.setRoleType(RoleTypeEnum.CONTEXT);

	ProjectPolicyEnum policyTmReadOnly = ProjectPolicyEnum.POLICY_TM_READ;

	Policy readOnlyPolicy = createPolicy(policyTmReadOnly.name(), RoleTypeEnum.CONTEXT,
		policyTmReadOnly.getCategory());

	Set<Policy> policies = new HashSet<>();
	policies.add(readOnlyPolicy);

	genericUserRole.setPolicies(policies);

	getRoleDAO().save(genericUserRole);
    }

    private void createGenericUserSystemRole() {

	if (getRoleDAO().findByName(ServiceUtils.SYSTEM_GENERIC_USER_ROLE_NAME) != null) {
	    return;
	}

	TmPolicyEnum termMenu = TmPolicyEnum.POLICY_TM_MENU_TERMS;
	Policy policyTermMenu = createPolicy(termMenu.name(), RoleTypeEnum.SYSTEM, termMenu.getCategory());

	Set<Policy> policies = new HashSet<>();
	policies.add(policyTermMenu);

	Role systemGenericUserRole = new Role();
	systemGenericUserRole.setRoleType(RoleTypeEnum.SYSTEM);
	systemGenericUserRole.setGeneric(Boolean.TRUE);
	systemGenericUserRole.setPolicies(policies);

	systemGenericUserRole.setRoleId(ServiceUtils.SYSTEM_GENERIC_USER_ROLE_NAME);

	getRoleDAO().save(systemGenericUserRole);

    }

    private Policy createPolicy(String policyId, RoleTypeEnum type, String category) {
	Policy policy = getPolicyDAO().findByName(policyId);

	if (policy == null) {
	    policy = new Policy();

	    policy.setCategory(category);
	    policy.setPolicyId(policyId);
	    policy.setPolicyType(type);

	    policy = getPolicyDAO().save(policy);
	}

	return policy;
    }

    private Set<Policy> createPolicySet(List<Policy> existingPolicies) {
	Set<Policy> policies = new HashSet<>();

	for (PolicyEnum[] policyEnumItem : policyEnums) {
	    for (PolicyEnum policyEnum : policyEnumItem) {
		if (isImpersonateDisabled()) {
		    // If impersonate is not enabled then skip creation of
		    // impersonate policies.
		    // This way impersonate action will not show up in the UI.
		    if (UserManagerPolicyEnum.POLICY_FOUNDATION_USERPROFILE_IMPERSONATIBLE == policyEnum
			    || UserManagerPolicyEnum.POLICY_FOUNDATION_USERPROFILE_SWITCH == policyEnum) {
			continue;
		    }
		}

		for (Policy existingPolicy : existingPolicies) {
		    if (existingPolicy.getPolicyId().equals(policyEnum.name())) {
			break;
		    }
		}

		Policy policy = createPolicy(policyEnum.name(), RoleTypeEnum.SYSTEM, policyEnum.getCategory());

		policies.add(policy);
	    }
	}

	return policies;
    }

    private Set<Policy> createPowerUserProjectPolicySet() {
	Set<Policy> policies = new HashSet<>();

	for (ProjectPolicyEnum policy : ProjectPolicyEnum.values()) {
	    Policy projectPolicy = createPolicy(policy.name(), RoleTypeEnum.CONTEXT, policy.getCategory());
	    policies.add(projectPolicy);
	}

	return policies;
    }

    private void createPowerUserProjectRole() {

	String powerUserRoleId = ServiceUtils.POWER_USER_PROJECT_ROLE_NAME;

	if (getRoleDAO().findByName(powerUserRoleId) != null) {
	    return;
	}

	Set<Policy> policies = createPowerUserProjectPolicySet();

	Role powerUserRole = new Role();
	powerUserRole.setRoleType(RoleTypeEnum.CONTEXT);
	powerUserRole.setPolicies(policies);
	powerUserRole.setRoleId(powerUserRoleId);
	powerUserRole.setGeneric(Boolean.TRUE);

	getRoleDAO().save(powerUserRole);
    }

    private Set<Policy> createPowerUserSystemPolicySet() {
	TmPolicyEnum viewInbox = TmPolicyEnum.POLICY_TM_VIEW_TRANSLATOR_INBOX;
	Policy policyViewInbox = createPolicy(viewInbox.name(), RoleTypeEnum.SYSTEM, viewInbox.getCategory());
	Policy policyTermMenu = createPolicy(TmPolicyEnum.POLICY_TM_MENU_TERMS.name(), RoleTypeEnum.SYSTEM,
		TmPolicyEnum.POLICY_TM_MENU_TERMS.getCategory());
	Policy policySendConnection = createPolicy(TmPolicyEnum.POLICY_TM_PROJECT_SEND_CONNECTION.name(),
		RoleTypeEnum.SYSTEM, TmPolicyEnum.POLICY_TM_PROJECT_SEND_CONNECTION.getCategory());
	Policy policyProjectAdd = createPolicy(BaseProjectPolicyEnum.POLICY_FOUNDATION_PROJECT_ADD.name(),
		RoleTypeEnum.SYSTEM, BaseProjectPolicyEnum.POLICY_FOUNDATION_PROJECT_ADD.getCategory());
	Policy policyProjectEdit = createPolicy(BaseProjectPolicyEnum.POLICY_FOUNDATION_PROJECT_EDIT.name(),
		RoleTypeEnum.SYSTEM, BaseProjectPolicyEnum.POLICY_FOUNDATION_PROJECT_EDIT.getCategory());
	Policy policyProjectEnable = createPolicy(BaseProjectPolicyEnum.POLICY_FOUNDATION_PROJECT_ENABLEDISABLE.name(),
		RoleTypeEnum.SYSTEM, BaseProjectPolicyEnum.POLICY_FOUNDATION_PROJECT_ENABLEDISABLE.getCategory());
	Policy policyProjectView = createPolicy(BaseProjectPolicyEnum.POLICY_FOUNDATION_PROJECT_VIEW.name(),
		RoleTypeEnum.SYSTEM, BaseProjectPolicyEnum.POLICY_FOUNDATION_PROJECT_VIEW.getCategory());

	Policy policyUserEdit = createPolicy(UserManagerPolicyEnum.POLICY_FOUNDATION_USERPROFILE_EDIT.name(),
		RoleTypeEnum.SYSTEM, UserManagerPolicyEnum.POLICY_FOUNDATION_USERPROFILE_EDIT.getCategory());

	Set<Policy> policies = new HashSet<>();
	policies.add(policyViewInbox);
	policies.add(policyTermMenu);
	policies.add(policySendConnection);
	policies.add(policyProjectAdd);
	policies.add(policyProjectEdit);
	policies.add(policyProjectEnable);
	policies.add(policyProjectView);
	policies.add(policyUserEdit);

	return policies;
    }

    private void createPowerUserSystemRole() {

	String powerUserRoleId = POWER_USER_SYSTEM_ROLE_NAME;

	if (getRoleDAO().findByName(powerUserRoleId) != null) {
	    return;
	}

	Set<Policy> policies = createPowerUserSystemPolicySet();

	Role powerUserRole = new Role();
	powerUserRole.setRoleType(RoleTypeEnum.SYSTEM);
	powerUserRole.setPolicies(policies);
	powerUserRole.setRoleId(powerUserRoleId);

	getRoleDAO().save(powerUserRole);
    }

    private void createProjectPolicySet() {
	for (ProjectPolicyEnum policyEnum : ProjectPolicyEnum.values()) {
	    createPolicy(policyEnum.name(), RoleTypeEnum.CONTEXT, policyEnum.getCategory());
	}

    }

    private void createSuperUserRole() {

	if (getRoleDAO().findByName(SUPER_USER_ROLE) != null) {
	    return;
	}
	Role superUserRole = new Role();
	superUserRole.setIdentifier(SUPER_USER_ROLE);

	superUserRole.setRoleId(SUPER_USER_ROLE);
	superUserRole.setRoleType(RoleTypeEnum.CONTEXT);
	superUserRole.setPolicies(createPowerUserProjectPolicySet());

	getRoleDAO().save(superUserRole);
    }

    private void createSuperUserSystemRole() {

	String superUserSystemRoleId = SYSTEM_SUPER_USER_ROLE_NAME;

	if (getRoleDAO().findByName(superUserSystemRoleId) != null) {
	    return;
	}

	TmPolicyEnum viewInbox = TmPolicyEnum.POLICY_TM_VIEW_TRANSLATOR_INBOX;
	Policy policyViewInbox = createPolicy(viewInbox.name(), RoleTypeEnum.SYSTEM, viewInbox.getCategory());

	TmPolicyEnum termMenu = TmPolicyEnum.POLICY_TM_MENU_TERMS;
	Policy policyTermMenu = createPolicy(termMenu.name(), RoleTypeEnum.SYSTEM, termMenu.getCategory());

	Set<Policy> policies = new HashSet<>();
	policies.add(policyTermMenu);
	policies.add(policyViewInbox);

	Role superUserRole = new Role();
	superUserRole.setRoleType(RoleTypeEnum.SYSTEM);
	superUserRole.setPolicies(policies);
	superUserRole.setRoleId(superUserSystemRoleId);

	getRoleDAO().save(superUserRole);
    }

    private void createSystemUser(Role adminRole) {
	UserProfileDAO userProfileDao = getUserProfileDAO();

	TmUserProfile systemUserProfile = userProfileDao.findByName(SystemAuthenticationHolder.SYSTEM_USERNAME);

	if (systemUserProfile == null) {
	    systemUserProfile = new TmUserProfile();
	    systemUserProfile.setHidden(Boolean.TRUE);
	}

	UserInfo userInfo = new UserInfo();

	userInfo.setUserName(SystemAuthenticationHolder.SYSTEM_USERNAME);
	userInfo.setPassword(encodePassword(SystemAuthenticationHolder.SYSTEM_PASSWORD));
	userInfo.setEnabled(true);
	userInfo.setUserType(UserTypeEnum.ORGANIZATION);

	Preferences preferences = new Preferences();

	systemUserProfile.setUserInfo(userInfo);

	Set<Role> roles = new HashSet<>();
	roles.add(adminRole);

	systemUserProfile.setSystemRoles(roles);
	systemUserProfile.setPreferences(preferences);

	userProfileDao.save(systemUserProfile);
    }

    private void createTranslatorUserProjectRole() {
	String translatorUserProjectRoleId = ServiceUtils.TRANSLATOR_USER_PROJECT_ROLE_NAME;
	if (getRoleDAO().findByName(translatorUserProjectRoleId) != null) {
	    return;
	}

	ProjectPolicyEnum addComment = ProjectPolicyEnum.POLICY_TM_ADD_COMMENT;
	Policy policyAddComment = createPolicy(addComment.name(), RoleTypeEnum.CONTEXT, addComment.getCategory());

	ProjectPolicyEnum autoSaveTranslation = ProjectPolicyEnum.POLICY_TM_TERM_AUTO_SAVE_TRANSLATION;
	Policy policyAutoSaveTranslation = createPolicy(autoSaveTranslation.name(), RoleTypeEnum.CONTEXT,
		autoSaveTranslation.getCategory());

	ProjectPolicyEnum undoTranslation = ProjectPolicyEnum.POLICY_TM_TERM_UNDO_TRANSLATION_CHANGES;
	Policy policyUndoTranslation = createPolicy(undoTranslation.name(), RoleTypeEnum.CONTEXT,
		undoTranslation.getCategory());

	ProjectPolicyEnum commitTranslation = ProjectPolicyEnum.POLICY_TM_TERM_COMMIT_TRANSLATION_CHANGES;
	Policy policyCommitTranslation = createPolicy(commitTranslation.name(), RoleTypeEnum.CONTEXT,
		commitTranslation.getCategory());

	ProjectPolicyEnum viewTermHistory = ProjectPolicyEnum.POLICY_TM_TERM_VIEW_TERM_HISTORY;
	Policy policyViewTermHistory = createPolicy(viewTermHistory.name(), RoleTypeEnum.CONTEXT,
		viewTermHistory.getCategory());

	ProjectPolicyEnum demoteToOnHoldTermStatus = ProjectPolicyEnum.POLICY_TM_TERM_DEMOTE_TO_ON_HOLD_TERM_STATUS;
	Policy policyDemoteToOnHoldTermStatus = createPolicy(demoteToOnHoldTermStatus.name(), RoleTypeEnum.CONTEXT,
		demoteToOnHoldTermStatus.getCategory());

	ProjectPolicyEnum demoteToPendingTermStatus = ProjectPolicyEnum.POLICY_TM_TERM_DEMOTE_TO_PENDING_APPROVAL_TERM_STATUS;
	Policy policyDemoteToPendingTermStatus = createPolicy(demoteToPendingTermStatus.name(), RoleTypeEnum.CONTEXT,
		demoteToPendingTermStatus.getCategory());

	Set<Policy> policies = new HashSet<>();
	policies.add(policyAddComment);
	policies.add(policyAutoSaveTranslation);
	policies.add(policyUndoTranslation);
	policies.add(policyCommitTranslation);
	policies.add(policyViewTermHistory);
	policies.add(policyDemoteToOnHoldTermStatus);
	policies.add(policyDemoteToPendingTermStatus);

	Role translatorUserRole = new Role();
	translatorUserRole.setRoleType(RoleTypeEnum.CONTEXT);
	translatorUserRole.setPolicies(policies);
	translatorUserRole.setRoleId(translatorUserProjectRoleId);
	translatorUserRole.setGeneric(Boolean.FALSE);

	getRoleDAO().save(translatorUserRole);
    }

    private void createTranslatorUserSystemRole() {

	String translatorUserSystemRoleId = TRANSLATOR_USER_SYSTEM_ROLE_NAME;

	if (getRoleDAO().findByName(translatorUserSystemRoleId) != null) {
	    return;
	}

	TmPolicyEnum viewTranslatorInbox = TmPolicyEnum.POLICY_TM_VIEW_TRANSLATOR_INBOX;
	Policy policyViewTranslatorInbox = createPolicy(viewTranslatorInbox.name(), RoleTypeEnum.SYSTEM,
		viewTranslatorInbox.getCategory());

	Set<Policy> policies = new HashSet<>();
	policies.add(policyViewTranslatorInbox);

	Role translatorUserRole = new Role();
	translatorUserRole.setRoleType(RoleTypeEnum.SYSTEM);
	translatorUserRole.setPolicies(policies);
	translatorUserRole.setRoleId(translatorUserSystemRoleId);

	getRoleDAO().save(translatorUserRole);
    }

    private String encodePassword(String password) {
	return getPasswordEncoder().encodePassword(password, null);
    }

    private PasswordEncoder getPasswordEncoder() {
	return _passwordEncoder;
    }

    private Set<Policy> getPolicies() {
	return _policies;
    }

    private PolicyDAO getPolicyDAO() {
	return _policyDAO;
    }

    private RoleDAO getRoleDAO() {
	return _roleDAO;
    }

    private SessionService getSessionService() {
	return _sessionService;
    }

    private SwitchUserFilter getSwitchUserProcessingFilter() {
	return _switchUserProcessingFilter;
    }

    private UserProfileDAO getUserProfileDAO() {
	return _userProfileDAO;
    }

    private boolean isImpersonateDisabled() {
	return _impersonateDisabled;
    }

    private void setImpersonateDisabled(boolean impersonateDisabled) {
	_impersonateDisabled = impersonateDisabled;
    }

    private void setPolicies(Set<Policy> policies) {
	_policies = policies;
    }
}