package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static java.lang.Long.valueOf;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static junit.framework.Assert.assertEquals;
import static org.gs4tr.foundation.modules.organization.model.BaseOrganizationPolicyEnum.POLICY_FOUNDATION_ORGANIZATION_VIEW;
import static org.gs4tr.foundation.modules.project.model.BaseProjectPolicyEnum.POLICY_FOUNDATION_PROJECT_VIEW;
import static org.gs4tr.foundation.modules.security.model.SecurityPolicyEnum.POLICY_FOUNDATION_SECURITY_ROLE_VIEW;
import static org.gs4tr.foundation.modules.usermanager.model.UserManagerPolicyEnum.POLICY_FOUNDATION_USERPROFILE_VIEW;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.eclipse.jetty.server.LocalConnector;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.OrganizationInfo;
import org.gs4tr.foundation.modules.entities.model.ProjectInfo;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.foundation.modules.usermanager.service.security.DefaultUserDetails;
import org.gs4tr.foundation.modules.webmvc.test.AbstractJettyTest;
import org.gs4tr.foundation.modules.webmvc.test.annotations.ClientBean;
import org.gs4tr.foundation.modules.webmvc.test.annotations.Connector;
import org.gs4tr.foundation.modules.webmvc.test.annotations.MvcTestConfig;
import org.gs4tr.foundation.modules.webmvc.test.runner.JettyJunit4ClassRunner;
import org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper;
import org.gs4tr.termmanager.model.FolderPolicy;
import org.gs4tr.termmanager.model.Preferences;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmPolicyEnum;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.RoleService;
import org.gs4tr.termmanager.service.StatisticsService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.webmvc.Version;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.AbstractRoleFactory;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;

@RunWith(JettyJunit4ClassRunner.class)
@MvcTestConfig
public abstract class AbstractMvcTest extends AbstractJettyTest {

    private static final Boolean ACCOUNT_NON_EXPIRED = true;
    private static final Boolean ACCOUNT_NON_LOCKED = true;
    static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CONTEXT_USER_KEY = "contextUser";
    private static final String CSRF_TOKEN = "csrf";
    private static final String DEFAULT_PASSWORD = "password";
    static final String DEFAULT_USERNAME = "sdulin";
    private static final String JSESSION_ID_KEY = "jsessionId";
    protected static final String JSON_DATA_KEY = "jsonData";
    private static final String LOGOUT_URL = "logout";
    private static final String PASSWORD_KEY = "password";
    private static final String POST_LOGIN_URL = "initializeUser.ter";
    private static final String PROCESS_LOGIN_URL = "processLogin.gs4tr";
    private static final String REMEMBER_ME_KEY = "remember-me";
    private static final String REMEMBER_ME_ON = "on";
    private static final String ROOT_TEST_JSON_FOLDER = "/json/";
    private static final String SESSION_COOKIE_NAME = "SESSION";
    private static final String SET_COOKIE_KEY = "Set-Cookie";
    protected static final String SUCCESS_KEY = "success";
    private static final String USERNAME_KEY = "username";

    private TmUserProfile _currentUserProfile;

    @Connector
    private LocalConnector _localConnector;

    @ClientBean
    private PasswordEncoder _passwordEncoder;

    @ClientBean
    private ProjectService _projectService;

    @ClientBean
    private String _rememberMeCookieName;

    @ClientBean
    private RoleService _roleService;

    @ClientBean
    private SessionService _sessionService;

    @ClientBean
    private StatisticsService _statisticsService;

    @ClientBean
    private TermEntryService _termEntryService;

    @ClientBean
    private UserProfileService _userProfileService;

    @Override
    public void afterTest() throws Exception {
	validateMockitoUsage();
	logoutAfterTest();
	super.afterTest();
	resetMocks();
    }

    @Override
    public void beforeTest() throws Exception {
	super.beforeTest();

	RequestHelper.setTestAppVersion(Version.getVersion());

	setCurrentUserProfile(initializeTmUserProfile(_testName.getMethodName()));
	if (isLoginRequired()) {
	    loginBeforeTest();
	}
	resetMocks();
    }

    private List<FolderPolicy> createAdminFolderPolicies() {
	List<FolderPolicy> policies = new ArrayList<>(4);
	policies.add(new FolderPolicy(ItemFolderEnum.USERS, POLICY_FOUNDATION_USERPROFILE_VIEW.name()));
	policies.add(new FolderPolicy(ItemFolderEnum.PROJECTS, POLICY_FOUNDATION_PROJECT_VIEW.name()));
	policies.add(new FolderPolicy(ItemFolderEnum.ORGANIZATIONS, POLICY_FOUNDATION_ORGANIZATION_VIEW.name()));
	policies.add(new FolderPolicy(ItemFolderEnum.SECURITY, POLICY_FOUNDATION_SECURITY_ROLE_VIEW.name()));
	return policies;
    }

    private List<FolderPolicy> createFolderPolicies() {
	List<FolderPolicy> policies = new ArrayList<>(4);
	policies.add(new FolderPolicy(ItemFolderEnum.PROJECTDETAILS, TmPolicyEnum.POLICY_TM_MENU_TERMS.name()));
	policies.add(new FolderPolicy(ItemFolderEnum.TERM_LIST, TmPolicyEnum.POLICY_TM_MENU_TERMS.name()));
	policies.add(new FolderPolicy(ItemFolderEnum.SUBMISSIONDETAILS,
		TmPolicyEnum.POLICY_TM_VIEW_TRANSLATOR_INBOX.name()));
	policies.add(new FolderPolicy(ItemFolderEnum.SUBMISSIONTERMLIST,
		TmPolicyEnum.POLICY_TM_VIEW_TRANSLATOR_INBOX.name()));
	return policies;
    }

    private UserInfo createUserInfo() {
	final UserInfo userInfo = new UserInfo();
	userInfo.setUserName(DEFAULT_USERNAME);
	userInfo.setPassword(getPasswordEncoder().encode(DEFAULT_PASSWORD));
	userInfo.setUserType(UserTypeEnum.ORGANIZATION);
	userInfo.setAccountNonExpired(ACCOUNT_NON_EXPIRED);
	userInfo.setAccountNonLocked(ACCOUNT_NON_LOCKED);
	userInfo.setDatePasswordChanged(new Date());
	return userInfo;
    }

    private String findCookieByName(List<String> cookies, String cookieName) {
	if (CollectionUtils.isEmpty(cookies)) {
	    return StringUtils.EMPTY;
	}
	return cookies.stream().filter(c -> c.contains(cookieName)).findFirst()
		.orElseThrow(NoSuchElementException::new);
    }

    private PasswordEncoder getPasswordEncoder() {
	return _passwordEncoder;
    }

    private String getRememberMeCookieName() {
	return _rememberMeCookieName;
    }

    private SessionService getSessionService() {
	return _sessionService;
    }

    private StatisticsService getStatisticsService() {
	return _statisticsService;
    }

    private String getTestCaseFolder() {
	Class<? extends AbstractMvcTest> testClass = this.getClass();

	TestSuite annotation = testClass.getAnnotation(TestSuite.class);
	if (annotation == null) {
	    throw new RuntimeException("You must declare TestSuite annotation on this test file.");
	}

	StringBuilder path = new StringBuilder(ROOT_TEST_JSON_FOLDER);
	path.append(annotation.value());
	path.append(StringConstants.SLASH);

	ClassPathResource classPathResource = new ClassPathResource(path.toString());
	try {
	    return classPathResource.getFile().getAbsolutePath();
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    private TmUserProfile initializeUser(TmProject project, String methodName) {
	final TmUserProfile userProfile = new TmUserProfile();
	userProfile.setPreferences(new Preferences());
	userProfile.setUserInfo(createUserInfo());
	userProfile.setUserProfileId(Long.valueOf(1));

	List<ProjectUserLanguage> languages = Arrays.asList(
		new ProjectUserLanguage(project, userProfile, Locale.US.getCode()),
		new ProjectUserLanguage(project, userProfile, Locale.GERMANY.getCode()),
		new ProjectUserLanguage(project, userProfile, Locale.FRANCE.getCode()));
	initializeUserPolicies(userProfile, languages, methodName);

	return userProfile;
    }

    private void initializeUserPolicies(TmUserProfile userProfile, List<ProjectUserLanguage> languages,
	    String methodName) {
	AbstractRoleFactory roleFactory = null;
	try {
	    Class<?>[] classes = new Class<?>[] {};
	    Class<? extends AbstractMvcTest> clazz = this.getClass();
	    Method testMethod = clazz.getMethod(methodName, classes);
	    TestUser methodAnnotation = testMethod.getAnnotation(TestUser.class);
	    RoleNameEnum roleName = methodAnnotation.roleName();
	    roleFactory = AbstractRoleFactory.newRoleFactory(roleName);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}

	Map<Long, List<Role>> roles = new HashMap<>(1);
	List<Role> projectRoles = roleFactory.createProjectRoles();
	if (CollectionUtils.isNotEmpty(projectRoles)) {
	    roles.put(valueOf(1), projectRoles);
	}
	List<FolderPolicy> folderPolicies = createFolderPolicies(), adminFolderPolicies = createAdminFolderPolicies();

	userProfile.setSystemRoles(roleFactory.createSystemRoles());
	userProfile.initializeOrganizationUser(roles, folderPolicies, adminFolderPolicies, languages, emptyList());
    }

    private void loginBeforeTest() {
	TmUserProfile userProfile = getCurrentUserProfile();

	when(getUserProfileService().findUserProfileIdByUserName(DEFAULT_USERNAME)).thenReturn(valueOf(1));
	when(getUserProfileService().loadUserByUsername(DEFAULT_USERNAME))
		.thenReturn(new DefaultUserDetails<>(userProfile));
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(userProfile);

	Map<String, String> loginParams = new HashMap<>(3);
	loginParams.put(USERNAME_KEY, DEFAULT_USERNAME);
	loginParams.put(PASSWORD_KEY, DEFAULT_PASSWORD);
	loginParams.put(REMEMBER_ME_KEY, REMEMBER_ME_ON);

	Request processLoginRequest = createPostRequest(PROCESS_LOGIN_URL, loginParams, getSessionParameters());
	Response processLoginResponse = sendRecieve(getLocalConnector(), processLoginRequest);

	List<String> cookies = processLoginResponse.getValuesList(SET_COOKIE_KEY);

	String rememberMeCookie = findCookieByName(cookies, getRememberMeCookieName());
	// Be sure that remember-me token exist
	Assert.assertTrue(StringUtils.isNotEmpty(rememberMeCookie));

	String sessionCookie = findCookieByName(cookies, SESSION_COOKIE_NAME);
	getSessionParameters().put(JSESSION_ID_KEY, sessionCookie);

	Request postLoginRequest = createPostRequest(POST_LOGIN_URL, loginParams, getSessionParameters());
	Response postLoginResponse = sendRecieve(getLocalConnector(), postLoginRequest);

	String csrfToken = postLoginResponse.get(CSRF_TOKEN);
	if (StringUtils.isNotEmpty(csrfToken)) {
	    getSessionParameters().put(CSRF_TOKEN, csrfToken);
	}

	// reseting because of times argument in verify method
	reset(getUserProfileService());
    }

    private void logoutAfterTest() {
	Map<String, String> logoutParams = singletonMap(CONTEXT_USER_KEY, DEFAULT_USERNAME);

	Request logoutRequest = createPostRequest(LOGOUT_URL, logoutParams, getSessionParameters());
	Response logoutResponse = sendRecieve(getLocalConnector(), logoutRequest);
	assertEquals(200, logoutResponse.getStatus());

	new JSONValidator(logoutResponse.getContent()).assertProperty(SUCCESS_KEY, String.valueOf(true));
    }

    private void resetMocks() {
	reset(getTermEntryService());
	reset(getProjectService());
	reset(getSessionService());
	reset(getUserProfileService());
	reset(getStatisticsService());
	reset(getRoleService());
    }

    private void setCurrentUserProfile(TmUserProfile currentUserProfile) {
	_currentUserProfile = currentUserProfile;
    }

    TmUserProfile getCurrentUserProfile() {
	return _currentUserProfile;
    }

    String getJsonData(String child, String[]... vars) {
	String testCaseFolder = getTestCaseFolder();

	String inputJson = null;
	try {
	    inputJson = FileUtils.readFileToString(new File(testCaseFolder, child));
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
	if (ArrayUtils.isNotEmpty(vars)) {
	    for (String[] replacement : vars) {
		String quote = Pattern.quote(replacement[0]);
		inputJson = inputJson.replaceAll(quote, replacement[1]);
	    }
	}
	return inputJson;
    }

    LocalConnector getLocalConnector() {
	return _localConnector;
    }

    ProjectService getProjectService() {
	return _projectService;
    }

    protected RoleService getRoleService() {
	return _roleService;
    }

    TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    TmUserProfile initializeTmUserProfile(String methodName) {
	OrganizationInfo organizationInfo = new OrganizationInfo();
	TmOrganization tmOrganization = new TmOrganization();
	tmOrganization.setOrganizationId(valueOf(1));
	tmOrganization.setOrganizationInfo(organizationInfo);

	ProjectDetail projectDetail = new ProjectDetail();

	TmProject project = new TmProject();
	project.setProjectId(valueOf(1));
	project.setOrganization(tmOrganization);
	project.setProjectDetail(projectDetail);
	ProjectInfo projectInfo = new ProjectInfo();
	project.setProjectInfo(projectInfo);
	projectInfo.setEnabled(true);

	return initializeUser(project, methodName);
    }

    boolean isLoginRequired() {
	return Boolean.TRUE;
    }
}
