package org.gs4tr.termmanager.glossaryV2;

import static org.mockito.Mockito.reset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.OrganizationInfo;
import org.gs4tr.foundation.modules.entities.model.ProjectInfo;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation.modules.entities.model.test.AbstractGroovyTest;
import org.gs4tr.foundation.modules.entities.model.test.TestModelCreator;
import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.security.model.RoleTypeEnum;
import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareWebContextLoader;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.termmanager.model.FolderPolicy;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.tm3.httpconnector.resolver.model.TmgrKey;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrBlacklistOperationsFactory;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrGlossaryOperationsFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.collect.Sets;

@ActiveProfiles(profiles = "defaultAuthentication")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {
	"classpath:org/gs4tr/termmanager/glossary/service/spring/applicationContext-init.xml",
	"classpath:org/gs4tr/termmanager/cache/spring/applicationContext-hazelcast.xml",
	"classpath:org/gs4tr/termmanager/glossaryV2/spring/applicationContext-mock.xml",
	"classpath:org/gs4tr/termmanager/glossary/spring/applicationContext-glossary-v2-service.xml" }, loader = TestEnvironmentAwareWebContextLoader.class)
public abstract class AbstractV2GlossaryServiceTest extends AbstractGroovyTest {

    private static final String CONNECT_SCRIPT = "src/test/resources/groovy/glossary/connect.groovy"; //$NON-NLS-1$

    private static final String GENERIC_USER_NAME = "generic";
    private static final String GENERIC_USER_ROLE_ID = "generic_user";

    private static final String TEST_SUITE_ROOT = "src/test/resources/groovy/"; //$NON-NLS-1$

    @Rule
    public TestName _testName = new TestName();

    @Autowired
    private ITmgrBlacklistOperationsFactory _blacklistFactory;

    @Autowired
    private ITmgrGlossaryOperationsFactory _glossaryFactory;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private SessionService _sessionService;

    @Autowired
    private TermEntryService _termEntryService;

    @After
    public void after() throws Exception {
	resetMocks();
    }

    @Before
    public void before() throws Exception {
	setTestModel(TestModelCreator.create(CONNECT_SCRIPT));
	MockitoAnnotations.initMocks(this);
	mockUser();
	mockProject();

	prepareTestModel(_testName.getMethodName());
    }

    public Policy createPolicy(RoleTypeEnum policyType, String category, String policyId) {
	final Policy policy = new Policy();
	policy.setPolicyType(policyType);
	policy.setCategory(category);
	policy.setPolicyId(policyId);
	return policy;
    }

    private Role createGenericRole() {
	Role role = new Role();
	role.setGeneric(Boolean.TRUE);
	role.setRoleType(RoleTypeEnum.CONTEXT);
	role.setRoleId(GENERIC_USER_ROLE_ID);
	Policy viewTerms = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_READ");
	role.setPolicies(Sets.newHashSet(viewTerms));
	return role;
    }

    private TmOrganization createTmOrganization() {
	OrganizationInfo organizationInfo = new OrganizationInfo();
	TmOrganization tmOrganization = new TmOrganization();
	tmOrganization.setOrganizationId(Long.valueOf(1));
	tmOrganization.setOrganizationInfo(organizationInfo);
	return tmOrganization;
    }

    private TmProject createTmProject(TmOrganization tmOrganization) {
	TmProject project = new TmProject();
	project.setProjectId(Long.valueOf(1));
	project.setOrganization(tmOrganization);
	project.setProjectDetail(new ProjectDetail());
	ProjectInfo projectInfo = new ProjectInfo();
	projectInfo.setEnabled(true);
	project.setProjectInfo(projectInfo);
	return project;
    }

    private void mockProject() {
	TmProject project = getModelObject("tmProject", TmProject.class);

	Mockito.when(getProjectService().findProjectByShortCode(Mockito.anyString())).thenReturn(project);
    }

    @SuppressWarnings("unchecked")
    private void mockUser() {
	UserProfileContext.clearContext();

	TmUserProfile userProfile = getModelObject("userProfile", TmUserProfile.class);

	List<Role> roles = getModelObject("roles", List.class);

	Map<Long, List<Role>> rolesMap = new HashMap<Long, List<Role>>();
	rolesMap.put(1L, roles);

	List<FolderPolicy> foldersPolicies = getModelObject("foldersPolicies", List.class);
	List<FolderPolicy> adminFoldersPolicies = getModelObject("adminFolderPolocies", List.class);
	List<ProjectUserLanguage> projectUserLanguages = getModelObject("projectUserLanguages", List.class);

	userProfile.initializeOrganizationUser(rolesMap, foldersPolicies, adminFoldersPolicies, projectUserLanguages,
		new ArrayList<Submission>());
	userProfile.setSystemRoles(new HashSet<Role>(roles));

	Mockito.when(getSessionService().login(Mockito.anyString(), Mockito.anyString())).thenReturn(userProfile);

	UserProfileContext.setCurrentUserProfile(userProfile);
    }

    private void resetMocks() {
	reset(getSessionService());
	reset(getProjectService());
	reset(getTermEntryService());
    }

    protected TmUserProfile createGenericTmUserProfile() {
	TmUserProfile generic = new TmUserProfile();
	UserInfo genericInfo = new UserInfo();
	genericInfo.setUserName(GENERIC_USER_NAME);
	generic.setUserInfo(genericInfo);

	TmOrganization tmOrganization = createTmOrganization();
	TmProject project = createTmProject(tmOrganization);

	ProjectUserLanguage germany = new ProjectUserLanguage(project, generic, Locale.GERMANY.getCode());
	ProjectUserLanguage english = new ProjectUserLanguage(project, generic, Locale.US.getCode());

	List<ProjectUserLanguage> puls = new ArrayList<>(2);
	puls.add(english);
	puls.add(germany);

	Map<Long, List<Role>> rolesMap = new HashMap<>(1);
	Role genericRole = createGenericRole();
	List<Role> roles = Arrays.asList(genericRole);
	rolesMap.put(1L, roles);

	generic.initializeOrganizationUser(rolesMap, null, null, puls, new ArrayList<>());
	return generic;
    }

    protected TmgrKey createKey() {
	String session = UUID.randomUUID().toString();
	String shortcode = "TES000001";

	TmgrKey key = new TmgrKey();
	key.setSession(session);
	key.setProject(shortcode);
	key.setUser("user");
	key.setPass("pass");
	key.setSource("en-US");
	key.setTarget("de-DE");
	return key;
    }

    protected ITmgrBlacklistOperationsFactory getBlacklistFactory() {
	return _blacklistFactory;
    }

    protected ITmgrGlossaryOperationsFactory getGlossaryFactory() {
	return _glossaryFactory;
    }

    protected ProjectService getProjectService() {
	return _projectService;
    }

    protected SessionService getSessionService() {
	return _sessionService;
    }

    protected TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    @Override
    protected String getTestSuiteRoot() {
	return TEST_SUITE_ROOT;
    }
}
