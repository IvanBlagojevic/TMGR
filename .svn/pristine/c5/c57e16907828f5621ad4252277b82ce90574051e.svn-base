package org.gs4tr.termmanager.webmvc.controllers;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareWebContextLoader;
import org.gs4tr.foundation.modules.webmvc.rest.filters.InternalRestAuthenticationFilter;
import org.gs4tr.termmanager.model.FolderPolicy;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.TmPolicyEnum;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.OrganizationService;
import org.gs4tr.termmanager.service.ProjectDetailService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.SubmissionDetailViewService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.webmvc.configuration.FolderViewConfiguration;
import org.gs4tr.termmanager.webmvc.groovy.AbstractGroovyTest;
import org.gs4tr.termmanager.webmvc.groovy.TestModelCreator;
import org.gs4tr.termmanager.webmvc.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles(profiles = "defaultAuthentication")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {
	"classpath:org/gs4tr/termmanager/webmvc/controllers/test/spring/applicationContext-init.xml",
	"classpath:org/gs4tr/foundation/modules/webmvc/spring/applicationContext-i18n.xml",
	"classpath:org/gs4tr/foundation/modules/webmvc/spring/applicationContext-security-commons.xml",
	"classpath:org/gs4tr/foundation/modules/webmvc/spring/applicationContext-security-default.xml",
	"classpath:org/gs4tr/foundation/modules/webmvc/spring/applicationContext-mvc-session-management.xml",
	"classpath:org/gs4tr/foundation/modules/webmvc/spring/applicationContext-mvc.xml",
	"classpath:org/gs4tr/termmanager/cache/spring/applicationContext-hazelcast.xml",
	"classpath:org/gs4tr/termmanager/webmvc/spring/applicationContext-mvc-security.xml",
	"classpath:org/gs4tr/termmanager/webmvc/controllers/test/spring/applicationContext-mocks.xml",
	"classpath:org/gs4tr/termmanager/webmvc/controllers/test/spring/applicationContext-test.xml",
	"classpath:org/gs4tr/termmanager/webmvc/spring/applicationContext-configuration.xml",
	"classpath:org/gs4tr/termmanager/webmvc/controllers/test/spring/applicationContext-mvc.xml",
	"classpath:org/gs4tr/termmanager/webmvc/controllers/test/spring/applicationContext-mocks-cache.xml",
	"classpath:org/gs4tr/termmanager/webmvc/controllers/test/spring/applicationContext-rest.xml" }, loader = TestEnvironmentAwareWebContextLoader.class)
public abstract class AbstractControllerTest extends AbstractGroovyTest {

    private static final String ABSTRACT_GROOVY_SCRIPT_URL = "src/test/resources/groovy/abstractInitializeUser.groovy"; //$NON-NLS-1$

    private static final Log LOG = LogFactory.getLog(AbstractControllerTest.class);

    private static final String TEST_SUITE_ROOT = "src/test/resources/groovy/"; //$NON-NLS-1$

    @Autowired
    private FolderViewConfiguration _folderViewConfiguration;

    @Autowired
    private InternalRestAuthenticationFilter _internalRestAuthenticationFilter;

    @Autowired
    private OrganizationService _organizationService;

    @Autowired
    private ProjectDetailService _projectDetailService;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private SubmissionDetailViewService _submissionDetailViewService;

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private UserProfileService _userProfileService;

    @Autowired
    private WebApplicationContext _webApplicationContext;

    protected MockMvc _mockMvc;

    @Rule
    public TestName _testMethodName = new TestName();

    @Rule
    public TestName _testName = new TestName();

    @After
    public void afterTest() {
	if (LOG.isDebugEnabled()) {
	    LOG.debug(String.format("Test ended - %s", _testMethodName.getMethodName()));
	}
	UserProfileContext.clearContext();
    }

    @Before
    public void beforeTest() {

	registerApplicationContextLocatorBean();

	if (LOG.isDebugEnabled()) {
	    LOG.debug(String.format("Test started - %s", _testMethodName.getMethodName()));
	}

	MockitoAnnotations.initMocks(this);

	_mockMvc = MockMvcBuilders.webAppContextSetup(getWebApplicationContext())
		.addFilters(getInternalRestAuthenticationFilter()).build();

	TmUserProfile userProfile = mockUser();
	registerAuthentication(userProfile);
	resetMocks();
	prepareTestModel(_testMethodName.getMethodName());
    }

    private InternalRestAuthenticationFilter getInternalRestAuthenticationFilter() {
	return _internalRestAuthenticationFilter;
    }

    @SuppressWarnings("unchecked")
    private TmUserProfile mockUser() {
	UserProfileContext.clearContext();
	// force mocking without annotation
	setTestModel(TestModelCreator.create(ABSTRACT_GROOVY_SCRIPT_URL));

	List<Role> roles = getModelObject("roles", List.class);

	Map<Long, List<Role>> rolesMap = new HashMap<Long, List<Role>>();
	rolesMap.put(1L, roles);

	List<FolderPolicy> foldersPolicies = getModelObject("foldersPolicies", List.class);
	List<FolderPolicy> adminFoldersPolicies = getModelObject("adminFolderPolocies", List.class);
	List<ProjectUserLanguage> projectUserLanguages = getModelObject("projectUserLanguages", List.class);
	List<Submission> submissions = new ArrayList<Submission>();

	TmUserProfile userProfile = getModelObject("userProfile", TmUserProfile.class);

	Set<String> languageSet = getModelObject("languageSet", HashSet.class);

	Map<Long, Set<String>> data = new HashMap<Long, Set<String>>();
	data.put(1L, languageSet);

	userProfile.setProjectUserLanguages(data);

	userProfile.initializeOrganizationUser(rolesMap, foldersPolicies, adminFoldersPolicies, projectUserLanguages,
		submissions);

	when(getUserProfileService().findById(any(Long.class))).thenReturn(userProfile);

	setTestModel(null);
	UserProfileContext.setCurrentUserProfile(userProfile);

	return userProfile;
    }

    /*
     * *****************************************************************************
     * TODO: This is only temporary solution. Jetty and Controller tests should be
     * separated and this method should be removed. Here is registered
     * ApplicationContextLocator bean if not exists (If only one test is executed).
     * *****************************************************************************
     */
    private void registerApplicationContextLocatorBean() {
	try {
	    // Register applicationContextLocator bean if not exists
	    new ClassPathXmlApplicationContext(
		    "org/gs4tr/termmanager/webmvc/controllers/test/spring/applicationContext-context.xml");
	} catch (BeanCreationException e) {
	    // Do nothing is applicationContextLocator bean already exists
	}
    }

    /**
     * 1-August-2017, after [Improvement#TERII-4798]:
     * <code>{@link org.gs4tr.termmanager.webmvc.rest.RestLogoutController#doGet(String)}</code>
     * method requires authentication in the current thread
     */
    private void registerAuthentication(TmUserProfile userProfile) {
	Authentication authentication = new UsernamePasswordAuthenticationToken(userProfile.getUserName(),
		userProfile.getUserInfo().getPassword(), stream(TmPolicyEnum.values()).map(TmPolicyEnum::name)
			.map(SimpleGrantedAuthority::new).collect(toList()));
	SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void resetMocks() {
	reset(getProjectDetailService());
	reset(getSubmissionDetailViewService());
	reset(getOrganizationService());
	reset(getProjectService());
	reset(getUserProfileService());
	reset(getTermEntryService());
    }

    protected FolderViewConfiguration getFolderViewConfiguration() {
	return _folderViewConfiguration;
    }

    protected String getJsonData(String jsonInputName, String[]... varReplacements) {
	// compute test case folder
	String testCaseFolder = TestUtils.getTestCaseFolder(this.getClass(), _testName.getMethodName());

	String inputJson;
	try {
	    inputJson = FileUtils.readFileToString(new File(testCaseFolder, jsonInputName));
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}

	// perform var replacement
	if (varReplacements != null && varReplacements.length > 0) {
	    for (String[] replacement : varReplacements) {
		inputJson = inputJson.replaceAll(Pattern.quote(replacement[0]), replacement[1]);
	    }
	}

	return inputJson;
    }

    protected OrganizationService getOrganizationService() {
	return _organizationService;
    }

    protected ProjectDetailService getProjectDetailService() {
	return _projectDetailService;
    }

    protected ProjectService getProjectService() {
	return _projectService;
    }

    protected SubmissionDetailViewService getSubmissionDetailViewService() {
	return _submissionDetailViewService;
    }

    protected TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    @Override
    protected String getTestSuiteRoot() {
	return TEST_SUITE_ROOT;
    }

    protected UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    protected WebApplicationContext getWebApplicationContext() {
	return _webApplicationContext;
    }

    protected String loginGet(String username, String password) throws Exception {

	MockHttpServletRequestBuilder get = get("/rest/login");
	get.servletPath("/rest/login");
	get.param("username", username);
	get.param("password", password);

	ResultActions resultActions = _mockMvc.perform(get);

	String userId = resultActions.andReturn().getResponse().getContentAsString();

	return userId;
    }

    protected String loginPost(String username, String password) throws Exception {
	MockHttpServletRequestBuilder post = post("/rest/login");
	post.servletPath("/rest/login");
	post.content("username=" + username + "&password=" + password);
	post.contentType(MediaType.TEXT_PLAIN);

	ResultActions resultActions = _mockMvc.perform(post);

	String userId = resultActions.andReturn().getResponse().getContentAsString();

	return userId;
    }
}
