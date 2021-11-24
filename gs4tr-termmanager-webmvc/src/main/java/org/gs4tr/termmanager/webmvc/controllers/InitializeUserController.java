package org.gs4tr.termmanager.webmvc.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.configuration.ApplicationConfigurationFactory;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.foundation.modules.security.model.RoleTypeEnum;
import org.gs4tr.foundation.modules.spring.utils.SpringProfileUtils;
import org.gs4tr.foundation.modules.usermanager.oauth.TptOAuthUserManagerClient;
import org.gs4tr.foundation.modules.usermanager.utils.PasswordRegexHolder;
import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.model.Language;
import org.gs4tr.termmanager.model.Preferences;
import org.gs4tr.termmanager.model.ProjectPolicyEnum;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UserCustomSearch;
import org.gs4tr.termmanager.model.dto.UserProfile;
import org.gs4tr.termmanager.model.dto.converter.LanguageConverter;
import org.gs4tr.termmanager.model.dto.converter.UserProfileConverter;
import org.gs4tr.termmanager.model.feature.TmgrFeatureConstants;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.RoleService;
import org.gs4tr.termmanager.service.SubmissionService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.webmvc.configuration.FolderViewConfiguration;
import org.gs4tr.termmanager.webmvc.model.commands.InitializeUserDataCommand;
import org.gs4tr.termmanager.webmvc.model.response.UiDetailItem;
import org.gs4tr.termmanager.webmvc.model.response.UiMenu;
import org.gs4tr.termmanager.webmvc.model.response.UiMenuItem;
import org.gs4tr.termmanager.webmvc.model.response.UiPreferences;
import org.gs4tr.termmanager.webmvc.model.search.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("initializeUserControler")
public class InitializeUserController extends AbstractController {

    public static final String AVALIABLE_THEMES = "avaliableThemes";

    public static final String THEME_COOKIE_NAME = "themeCookieName";

    private static final String CSRF_RESPONSE_HEADER = "csrf"; // X-CSRF-TOKEN

    private static final String DEFAULT_CSRF_ATTRIBUTE_NAME = "_csrf";

    private static final String LOGOUT_URL = "logoutUrl";

    private static final String MAX_INACTIVE_INTERVAL = "maxInactiveInterval";

    private static final String OAUTH_TOKEN_REFRESH_INTERVAL = "oauthTokenRefreshInterval"; //$NON-NLS-1$

    private static final String PROJECT_KEY = "project";

    private static final String SAML_LOGOUT_ENDPOINT = "logout";

    private static final String SAML_SSO_APPLICATIONS = "samlSsoApplications";

    private static final String SAML_SSO_APPLICATION_NAME = "applicationName";

    private static final String SAML_SSO_APPLICATION_URL = "applicationUrl";

    private static final String SSO_CHANGE_PASSWORD_URL = "ssoChangePasswordUrl";

    private static final String SSO_LOGOUT_SUCCESS_URL = "ssoLogoutSuccessUrl";

    private static final String SYSTEM_KEY = "system";

    private static final String TITLE_KEY = "title";

    private static String BEARER = "Bearer";

    @Value("#{adminSearchCriterias}")
    private Map<ItemFolderEnum, List<String>> _adminSearchCriterias;

    @Autowired
    private ApplicationConfigurationFactory _applicationConfigurationFactory;

    @Value("#{'${avaliable.themes}'.split(',')}")
    private List<String> _avaliableThemes;

    @Value("#{detailsConfig}")
    private Map<ItemFolderEnum, List<UiDetailItem>> _detailsConfig;

    @Value("#{folderUrls}")
    private Map<ItemFolderEnum, String> _folderUrls;

    @Autowired(required = false)
    private TptOAuthUserManagerClient _oAuthClient;

    @Value("${oauth.changePasswordUrl}")
    private String _oauthChangePasswordUrl;

    @Value("${oauth.logoutUrl}")
    private String _oauthLogoutUrl;

    @Value("${oauth.refreshTokenInterval:720}")
    private int _oauthRefreshTokenInterval;

    @Autowired
    private PasswordRegexHolder _passwordRegexHolder;

    @Autowired
    private ProjectService _projectService;

    @Value("#{rememberMeCookieName}")
    private String _rememberMeCookieName;

    @Autowired
    private RoleService _roleService;

    @Value("${saml.classifier}")
    private String _samlClassifier;

    private String _samlLogoutUrl;

    private List<Map<String, String>> _samlSsoApplications;

    @Value("${saml.sso.applications}")
    private String _samlSsoApplicationsConfiguration;

    @Value("${serverAddress}")
    private String _serverAddress;

    @Autowired
    private SessionRegistry _sessionRegistry;

    @Value("${saml.logout.redirectToUrl}")
    private String _ssoLogoutTargetRedirectionUrl;

    @Autowired
    private SubmissionService _submissionService;

    @Value("${theme.cookie.name}")
    private String _themeCookieName;

    private Map<String, String> _userFolderUrls;

    @Autowired
    private UserProfileService _userProfileService;

    @Value("#{userSearchCriterias}")
    private Map<ItemFolderEnum, List<String>> _userSearchCriterias;

    // InitializingBean creates Proxy over controller so Spring can't detect
    // this class as controller
    @PostConstruct
    public void afterPropertiesSet() throws Exception {
	String samlClassifier = getSamlClassifier();
	if (StringUtils.isNotEmpty(samlClassifier)) {
	    StringBuilder logoutUrlBuilder = new StringBuilder(samlClassifier);
	    logoutUrlBuilder.append(StringConstants.SLASH);
	    logoutUrlBuilder.append(SAML_LOGOUT_ENDPOINT);

	    _samlLogoutUrl = logoutUrlBuilder.toString();
	}

	String samlSsoApplicationsConfiguration = getSamlSsoApplicationsConfiguration();
	if (StringUtils.isNotEmpty(samlSsoApplicationsConfiguration)) {
	    List<Map<String, String>> samlSsoApplications = new ArrayList<>();
	    String[] applicationConfigurations = samlSsoApplicationsConfiguration.split(StringConstants.SEMICOLON);
	    for (String applicationConfiguration : applicationConfigurations) {
		String[] application = applicationConfiguration.split(StringConstants.COLON, 2);

		Map<String, String> samlSsoApplication = new HashMap<>();
		samlSsoApplication.put(SAML_SSO_APPLICATION_NAME, application[0]);
		samlSsoApplication.put(SAML_SSO_APPLICATION_URL, application[1]);

		samlSsoApplications.add(samlSsoApplication);
	    }

	    _samlSsoApplications = samlSsoApplications;
	}
    }

    public Map<ItemFolderEnum, List<String>> getAdminSearchCriterias() {
	return _adminSearchCriterias;
    }

    public ApplicationConfigurationFactory getApplicationConfigurationFactory() {
	return _applicationConfigurationFactory;
    }

    public Map<ItemFolderEnum, List<UiDetailItem>> getDetailsConfig() {
	return _detailsConfig;
    }

    public Map<ItemFolderEnum, String> getFolderUrls() {
	return _folderUrls;
    }

    public String getServerAddress() {
	return _serverAddress;
    }

    public Map<String, String> getUserFolderUrls() {
	return _userFolderUrls;
    }

    public Map<ItemFolderEnum, List<String>> getUserSearchCriterias() {
	return _userSearchCriterias;
    }

    public TptOAuthUserManagerClient getoAuthClient() {
	return _oAuthClient;
    }

    @RequestMapping(method = RequestMethod.POST, value = "initializeUser.ter")
    @ResponseBody
    public ModelMapResponse handleRequestInternal(HttpServletRequest request, HttpServletResponse response,
	    @ModelAttribute InitializeUserDataCommand dataCommand) throws Exception {

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	Preferences preferences = userProfile.getPreferences();

	Map<SearchCriteria, Object> searchBar = ControllerUtils.createSearchBarElements(getProjectService(),
		getSubmissionService(), getUserProfileService());

	List<org.gs4tr.termmanager.model.dto.Language> dtoLanguages = getAvailableLanguages();

	UiMenu menuConfig = createMenuConfig(userProfile);

	checkMenuConfig(request, menuConfig);

	ModelMapResponse mapResponse = new ModelMapResponse();

	addSessionSalt(request, response);
	mapResponse.put("passwordRegex", getPasswordRegexHolder().getPasswordRegex());

	if (!rememberMeActive(request)) {
	    // 24-August-2016, as per [Issue#TERII-4284]:
	    mapResponse.put(MAX_INACTIVE_INTERVAL, request.getSession(false).getMaxInactiveInterval());
	}

	mapResponse.addObject(THEME_COOKIE_NAME, getThemeCookieName());

	mapResponse.addObject(AVALIABLE_THEMES, getAvaliableThemes());

	Boolean canSwitchUser = getUserProfileService().canSwitchUser(userProfile);

	mapResponse.put("canSwitchUser", canSwitchUser);
	mapResponse.put("isSwitch", getUserProfileService().isSwitchUser());
	mapResponse.put("menuConfig", menuConfig);
	mapResponse.put("userProfile", createUiUserInfo(userProfile));
	mapResponse.put("preferences", createUiPreferences(preferences));
	mapResponse.put("languages", dtoLanguages);
	mapResponse.put("searchBar", searchBar);

	addPoliciesToModel(mapResponse);

	boolean canAddEditTerm = isAddEditEnabled(userProfile);
	boolean canAutoSave = isAutoSaveEnabled(userProfile);
	mapResponse.put("canAddTerm", canAddEditTerm);
	mapResponse.put("canEditTerm", canAddEditTerm);
	mapResponse.put("canAutoSave", canAutoSave);

	String[] policies = { ProjectPolicyEnum.POLICY_TM_TERM_ADD_APPROVED_TERM.toString(),
		ProjectPolicyEnum.POLICY_TM_TERM_ADD_PENDING_TERM.toString(),
		ProjectPolicyEnum.POLICY_TM_TERM_ADD_BLACKLIST_TERM.toString(),
		ProjectPolicyEnum.POLICY_TM_TERM_ADD_ON_HOLD_TERM.toString() };

	mapResponse.put(ControllerConstants.PROJECT_CONFIGURATION,
		ControllerUtils.isProjectPolicyEnabled(userProfile, policies));

	boolean ssoUser = userProfile.getUserInfo().isSsoUser();
	boolean oAuth = SpringProfileUtils
		.checkIfSpringProfileIsActive(SpringProfileUtils.OAUTH_AUTHENTICATION_PROFILE);
	if (ssoUser) {

	    List<Map<String, String>> ssoApps = getSamlSsoApplications() != null
		    ? new ArrayList<>(getSamlSsoApplications())
		    : new ArrayList<>();

	    if (dataCommand != null && dataCommand.getTptClients() != null) {
		addTptAuthClientsToList(ssoApps, dataCommand.getTptClients());
	    }

	    mapResponse.put(SAML_SSO_APPLICATIONS, ssoApps);
	    mapResponse.put(LOGOUT_URL, getSamlLogoutUrl());
	    mapResponse.put(SSO_LOGOUT_SUCCESS_URL, getSsoLogoutTargetRedirectionUrl());
	    if (oAuth) {
		mapResponse.put(SSO_CHANGE_PASSWORD_URL, getOauthChangePasswordUrl());
		mapResponse.put(LOGOUT_URL, getOauthLogoutUrl());
	    }
	}

	if (oAuth && isSSOtokenOrImpersonatedBySSO(userProfile)) {
	    mapResponse.put(OAUTH_TOKEN_REFRESH_INTERVAL, getOauthRefreshTokenInterval());
	}

	mapResponse.put("applicationInfo", createApplicationInfo());

	return mapResponse;
    }

    public void setAdminSearchCriterias(Map<ItemFolderEnum, List<String>> adminSearchCriterias) {
	_adminSearchCriterias = adminSearchCriterias;
    }

    public void setDetailsConfig(Map<ItemFolderEnum, List<UiDetailItem>> detailsConfig) {
	_detailsConfig = detailsConfig;
    }

    public void setFolderUrls(Map<ItemFolderEnum, String> folderUrls) {
	_folderUrls = folderUrls;
    }

    public void setUserFolderUrls(Map<String, String> userFolderUrls) {
	_userFolderUrls = userFolderUrls;
    }

    public void setUserSearchCriterias(Map<ItemFolderEnum, List<String>> userSearchCriterias) {
	_userSearchCriterias = userSearchCriterias;
    }

    public void setoAuthClient(TptOAuthUserManagerClient oAuthClient) {
	_oAuthClient = oAuthClient;
    }

    private void addActiveSessionsCount(UserProfile dtoUser) {
	int sessionsCount = getSessionRegistry()
		.getAllSessions(SecurityContextHolder.getContext().getAuthentication().getPrincipal(), false).size();

	if (sessionsCount > 0) {
	    dtoUser.getUserInfo().setActiveSessionsCount(sessionsCount);
	}
    }

    private void addCustomSearchFolders(List<UiMenuItem> menuItems, TmUserProfile userProfile, boolean adminFolders) {
	List<UserCustomSearch> userCustomSearchFolders = getUserProfileService().getCustomSearchFolders(userProfile,
		adminFolders);

	for (UserCustomSearch customFolder : userCustomSearchFolders) {
	    String originalFolderName = customFolder.getOriginalFolder();
	    ItemFolderEnum folder = ItemFolderEnum.valueOf(originalFolderName);

	    String customFolderName = customFolder.getCustomFolder();

	    List<String> folderSearchBar = null;

	    if (adminFolders) {
		folderSearchBar = getAdminSearchCriterias().get(folder);
	    } else {
		folderSearchBar = getUserSearchCriterias().get(folder);
	    }

	    String url = customFolder.getUrl();
	    UiMenuItem menuItem = new UiMenuItem(customFolderName, url, originalFolderName,
		    customFolder.getSearchJsonData(), createDtoSearchBar(folderSearchBar), false);

	    List<UiDetailItem> detailConfig = getDetailsConfig().get(folder);
	    if (detailConfig != null) {
		menuItem.setDetailsUrl(detailConfig.toArray(new UiDetailItem[detailConfig.size()]));
	    }

	    menuItems.add(menuItem);
	}
    }

    private void addPoliciesToModel(ModelMapResponse mapResponse) {
	List<Policy> allPolicies = getRoleService().findAllPolicies();

	Set<Policy> projectPolicies = new HashSet<Policy>();
	Set<Policy> systemPolicies = new HashSet<Policy>();

	for (Policy policy : allPolicies) {
	    RoleTypeEnum policyType = policy.getPolicyType();
	    if (RoleTypeEnum.CONTEXT == policyType) {
		projectPolicies.add(policy);
	    } else if (RoleTypeEnum.SYSTEM == policyType) {
		systemPolicies.add(policy);
	    }
	}

	Map<String, Set<Policy>> projectPoliciesMap = ControllerUtils.createCategoryPolicyMap(projectPolicies);
	List<Object> projectPoliciesList = extractPoliciesFromPoliciesMap(projectPoliciesMap);

	Map<String, Set<Policy>> systemPoliciesMap = ControllerUtils.createCategoryPolicyMap(systemPolicies);
	List<Object> systemPoliciesList = extractPoliciesFromPoliciesMap(systemPoliciesMap);

	mapResponse.put(PROJECT_KEY, projectPoliciesList);
	mapResponse.put(SYSTEM_KEY, systemPoliciesList);
    }

    private void addSessionSalt(HttpServletRequest request, HttpServletResponse response) {
	CsrfToken token = (CsrfToken) request.getAttribute(DEFAULT_CSRF_ATTRIBUTE_NAME);
	if (Objects.nonNull(token)) {
	    response.setHeader(CSRF_RESPONSE_HEADER, token.getToken());
	}
    }

    private void addTptAuthClientsToList(List<Map<String, String>> ssoApps, String[] userTptClients) {
	Map<String, String> tptClientsMap = getoAuthClient().getAvailableClientsMap();
	String serverAddress = getServerAddress();
	for (String clientName : userTptClients) {
	    if (tptClientsMap.containsKey(clientName) && !tptClientsMap.get(clientName).contains(serverAddress)) {
		Map<String, String> ssoApplication = new HashMap<>();
		ssoApplication.put(SAML_SSO_APPLICATION_NAME, clientName);
		ssoApplication.put(SAML_SSO_APPLICATION_URL, tptClientsMap.get(clientName));
		ssoApps.add(ssoApplication);
	    }
	}
    }

    private void checkMenuConfig(HttpServletRequest request, UiMenu menuConfig) {
	if (CollectionUtils.isEmpty(menuConfig.getUserMenu()) && CollectionUtils.isEmpty(menuConfig.getAdminMenu())) {
	    logout(request);

	    throw new UserException(MessageResolver.getMessage("InitializeUserController.16"), //$NON-NLS-1$
		    MessageResolver.getMessage("InitializeUserController.0")); //$NON-NLS-1$
	}
    }

    private Object createApplicationInfo() {
	// runtime info
	Map<String, Object> applicationInfoMap = new HashMap<>();
	String[] enabledFeatures = TmgrFeatureConstants.getEnabledFeatures();
	for (String feature : enabledFeatures) {
	    if (!TmgrFeatureConstants.DEFAULT.equals(feature)) {
		applicationInfoMap.put(feature, Boolean.TRUE);
	    }

	}

	return applicationInfoMap;
    }

    private String[] createDtoSearchBar(List<String> folderSearchBar) {
	String[] result = new String[folderSearchBar.size()];

	for (int i = 0; i < result.length; i++) {
	    result[i] = folderSearchBar.get(i);
	}

	return result;
    }

    private UiMenu createMenuConfig(TmUserProfile userProfile) {
	UiMenu uiMenu = new UiMenu();

	List<UiMenuItem> adminMenuConfig = createMenuItems(userProfile.getAdminFolders(), getAdminSearchCriterias(),
		userProfile, true);
	uiMenu.setAdminMenu(adminMenuConfig);

	List<UiMenuItem> userMenuConfig = createMenuItems(userProfile.getFolders(), getUserSearchCriterias(),
		userProfile, false);
	uiMenu.setUserMenu(userMenuConfig);

	return uiMenu;
    }

    private List<UiMenuItem> createMenuItems(List<ItemFolderEnum> folders,
	    Map<ItemFolderEnum, List<String>> menuFilters, TmUserProfile userProfile, boolean adminFolders) {
	List<UiMenuItem> menuItems = new ArrayList<UiMenuItem>();

	if (CollectionUtils.isNotEmpty(folders)) {

	    for (ItemFolderEnum folder : folders) {
		String url = getFolderUrl(folder);

		List<String> filter = menuFilters.get(folder);

		if (CollectionUtils.isNotEmpty(filter)) {
		    UiMenuItem menuItem = new UiMenuItem(folder.name(), url, folder.name(),
			    filter.toArray(new String[filter.size()]));
		    if (ItemFolderEnum.SUBMISSIONTERMLIST == folder) {
			menuItem.setSystemHidden(true);
		    }

		    List<UiDetailItem> detailConfig = getDetailsConfig().get(folder);
		    if (detailConfig != null) {
			menuItem.setDetailsUrl(detailConfig.toArray(new UiDetailItem[detailConfig.size()]));
		    }

		    menuItems.add(menuItem);
		}
	    }
	}

	addCustomSearchFolders(menuItems, userProfile, adminFolders);

	return menuItems;
    }

    private UiPreferences createUiPreferences(Preferences preferences) {
	FolderViewConfiguration folderViewConfiguration = getFolderViewConfiguration();

	UiPreferences prefs = new UiPreferences();

	Integer itemsPerPage = preferences.getItemsPerPage();
	Integer refreshPeriod = preferences.getRefreshPeriod();
	String timezone = TmUserProfile.getCurrentUserProfile().getUserInfo().getTimeZone();

	prefs.setItemsPerPage(itemsPerPage != null ? itemsPerPage : folderViewConfiguration.getItemsPerPage());

	prefs.setRefreshPeriod(refreshPeriod != null ? refreshPeriod : folderViewConfiguration.getRefreshPeriod());

	prefs.setTimezone(timezone != null ? timezone : folderViewConfiguration.getTimezone());

	String actionSize = preferences.getActionSize();
	prefs.setActionSize(actionSize != null ? actionSize : folderViewConfiguration.getActionSize());

	Boolean actionTextVisible = preferences.getActionTextVisible();
	prefs.setActionTextVisible(actionTextVisible != null ? actionTextVisible.booleanValue()
		: folderViewConfiguration.getActionTextVisible().booleanValue());

	String language = null;
	if (preferences.getLanguage() != null) {
	    language = preferences.getLanguage();
	}
	prefs.setLanguage(LanguageConverter.fromInternalToDto(language));

	return prefs;
    }

    private org.gs4tr.termmanager.model.dto.UserProfile createUiUserInfo(TmUserProfile userProfile) {
	UserProfile dtoUser = UserProfileConverter.fromInternalToDto(userProfile);
	addActiveSessionsCount(dtoUser);
	return dtoUser;
    }

    private List<Object> extractPoliciesFromPoliciesMap(Map<String, Set<Policy>> policiesMap) {
	List<Object> policiesList = new ArrayList<Object>();
	for (Map.Entry<String, Set<Policy>> entry : policiesMap.entrySet()) {
	    String category = entry.getKey();
	    Map<String, Object> policies = new HashMap<String, Object>();
	    policies.put(TITLE_KEY, category);
	    policies.put(category, entry.getValue());

	    policiesList.add(policies);
	}
	return policiesList;
    }

    private List<org.gs4tr.termmanager.model.dto.Language> getAvailableLanguages() {

	List<org.gs4tr.termmanager.model.dto.Language> dtoLanguages = Language.getAllAvailableLanguages().stream()
		.map(LanguageConverter::fromInternalToDto).sorted((l1, l2) -> l1.getValue().compareTo(l2.getValue()))
		.collect(Collectors.toList());

	return dtoLanguages;
    }

    private List<String> getAvaliableThemes() {
	return _avaliableThemes;
    }

    private String getFolderUrl(ItemFolderEnum folder) {
	return getFolderUrls().get(folder);
    }

    private FolderViewConfiguration getFolderViewConfiguration() {
	return getApplicationConfigurationFactory()
		.createApplicationConfiguration(FolderViewConfiguration.class.getName());
    }

    private String getOauthChangePasswordUrl() {
	return _oauthChangePasswordUrl;
    }

    private String getOauthLogoutUrl() {
	return _oauthLogoutUrl;
    }

    private int getOauthRefreshTokenInterval() {
	return _oauthRefreshTokenInterval;
    }

    private PasswordRegexHolder getPasswordRegexHolder() {
	return _passwordRegexHolder;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private String getRememberMeCookieName() {
	return _rememberMeCookieName;
    }

    private RoleService getRoleService() {
	return _roleService;
    }

    private String getSamlClassifier() {
	return _samlClassifier;
    }

    private String getSamlLogoutUrl() {
	return _samlLogoutUrl;
    }

    private List<Map<String, String>> getSamlSsoApplications() {
	return _samlSsoApplications;
    }

    private String getSamlSsoApplicationsConfiguration() {
	return _samlSsoApplicationsConfiguration;
    }

    private SessionRegistry getSessionRegistry() {
	return _sessionRegistry;
    }

    private String getSsoLogoutTargetRedirectionUrl() {
	return _ssoLogoutTargetRedirectionUrl;
    }

    private SubmissionService getSubmissionService() {
	return _submissionService;
    }

    private String getThemeCookieName() {
	return _themeCookieName;
    }

    private UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    private boolean isAddEditEnabled(TmUserProfile userProfile) {
	return isPolicyEnabled(userProfile, ProjectPolicyEnum.POLICY_TM_TERM_ADD_APPROVED_TERM)
		|| isPolicyEnabled(userProfile, ProjectPolicyEnum.POLICY_TM_TERM_ADD_PENDING_TERM)
		|| isPolicyEnabled(userProfile, ProjectPolicyEnum.POLICY_TM_TERM_ADD_BLACKLIST_TERM)
		|| isPolicyEnabled(userProfile, ProjectPolicyEnum.POLICY_TM_TERM_ADD_ON_HOLD_TERM);
    }

    private boolean isAutoSaveEnabled(TmUserProfile user) {
	return isPolicyEnabled(user, ProjectPolicyEnum.POLICY_TM_TERM_AUTO_SAVE_TRANSLATION);
    }

    private boolean isPolicyEnabled(TmUserProfile userProfile, ProjectPolicyEnum policyEnum) {
	boolean containsPolicy = false;
	Set<Long> projectIds = userProfile.getProjectUserLanguages().keySet();
	String[] policies = new String[] { policyEnum.toString() };
	for (Long projectId : projectIds) {
	    containsPolicy = userProfile.containsContextPolicies(projectId, policies);
	    if (containsPolicy) {
		break;
	    }
	}

	return containsPolicy;
    }

    private boolean isSSOtokenOrImpersonatedBySSO(TmUserProfile userProfile) {
	if (getUserProfileService().isSwitchUser()) {
	    org.gs4tr.foundation.modules.entities.model.UserProfile parentUser = getUserProfileService()
		    .getParentOfCurrentSwitchedUser();
	    return Objects.nonNull(parentUser) && Boolean.TRUE.equals(parentUser.getUserInfo().isSsoUser());
	} else {
	    return userProfile.getUserInfo().isSsoUser();
	}
    }

    private void logout(HttpServletRequest request) {
	HttpSession session = request.getSession(false);

	if (session != null) {
	    session.invalidate();
	}
    }

    private boolean rememberMeActive(HttpServletRequest request) {
	String rememberMeCookieName = getRememberMeCookieName();

	Cookie[] cookies = request.getCookies();
	if (ArrayUtils.isEmpty(cookies)) {
	    return false;
	}

	return Arrays.stream(cookies).anyMatch(cookie -> rememberMeCookieName.equals(cookie.getName()));
    }
}
