package org.gs4tr.termmanager.service.mocking.sso;

import static org.gs4tr.termmanager.io.tlog.TransactionProcessor.LOGGER;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.gs4tr.termmanager.dao.ProjectDAO;
import org.gs4tr.termmanager.dao.ProjectDetailDAO;
import org.gs4tr.termmanager.dao.UserProfileDAO;
import org.gs4tr.termmanager.io.tlog.impl.TransactionLogHandler;
import org.gs4tr.termmanager.model.FolderPolicy;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossarySearcher;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.mocking.groovy.AbstractGroovyTest;
import org.gs4tr.termmanager.service.mocking.groovy.TestModelCreator;
import org.gs4tr.termmanager.service.solr.GlossaryConnectionManager;
import org.gs4tr.tm3.api.TmException;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import jetbrains.exodus.entitystore.EntityId;

@ActiveProfiles(profiles = "oauthAuthentication")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:org/gs4tr/termmanager/service/spring/applicationContext-mocks.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-test.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-systemTasks.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-translationTasks.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-dao-mocks.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-solr.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-mocks-cache.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-fileManager.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-fileAnalysis.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-projectTerminologyCounts.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-backup.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-io-mock.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-profiles.xml",
	"classpath:org/gs4tr/termmanager/io/spring/applicationContext-io.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-applicationContextLocator.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-import-report.xml" }, loader = TestEnvironmentAwareContextLoader.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public abstract class AbstractSsoServiceTest extends AbstractGroovyTest {

    private static final String SSO_GROOVY_SCRIPT_URL = "src/test/resources/groovy/sso/abstractSsoUser.groovy"; //$NON-NLS-1$

    private static final String TEST_SUITE_ROOT = "src/test/resources/groovy/";

    @Autowired
    private GlossaryConnectionManager _connectionManager;

    @Autowired
    private ITmgrGlossaryConnector _connector;

    @Autowired
    private ITmgrGlossaryBrowser _glossaryBrowser;

    @Autowired
    private ITmgrGlossarySearcher _glossarySearcher;

    @Autowired
    private ITmgrGlossaryUpdater _glossaryUpdater;

    @Autowired
    private ProjectDAO _projectDAO;

    private ProjectDetail _projectDetail;

    @Autowired
    private ProjectDetailDAO _projectDetailDAO;

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private TransactionLogHandler _transactionLogHandler;

    @Autowired
    private UserProfileDAO _userProfileDAO;

    @Autowired
    private UserProfileService _userProfileService;

    @Rule
    public TestName _testMethodName = new TestName();

    @After
    public void afterTest() {
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format("Test ended - %s", _testMethodName.getMethodName()));
	}

	UserProfileContext.clearContext();
    }

    @Before
    public void beforeTest() {
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format("Test started - %s", _testMethodName.getMethodName()));
	}

	MockitoAnnotations.initMocks(this);
	resetMocks();
	mockUser();
	mockSolr();
	prepareTestModel(_testMethodName.getMethodName());
    }

    public GlossaryConnectionManager getConnectionManager() {
	return _connectionManager;
    }

    public ITmgrGlossaryConnector getConnector() {
	return _connector;
    }

    public ITmgrGlossaryBrowser getGlossaryBrowser() {
	return _glossaryBrowser;
    }

    public ITmgrGlossarySearcher getGlossarySearcher() {
	return _glossarySearcher;
    }

    public ITmgrGlossaryUpdater getGlossaryUpdater() {
	return _glossaryUpdater;
    }

    public ProjectDAO getProjectDAO() {
	return _projectDAO;
    }

    public TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    public TransactionLogHandler getTransactionLogHandler() {
	return _transactionLogHandler;
    }

    public UserProfileDAO getUserProfileDAO() {
	return _userProfileDAO;
    }

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    private ProjectDetail getProjectDetail() {
	return _projectDetail;
    }

    private ProjectDetailDAO getProjectDetailDAO() {
	return _projectDetailDAO;
    }

    private void mockSolr() {
	try {
	    when(getConnectionManager().getConnector(anyString())).thenReturn(getConnector());
	    when(getConnector().getTmgrBrowser()).thenReturn(getGlossaryBrowser());
	    when(getConnector().getTmgrUpdater()).thenReturn(getGlossaryUpdater());
	    when(getConnector().getTmgrSearcher()).thenReturn(getGlossarySearcher());
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}

    }

    @SuppressWarnings("unchecked")
    private void mockUser() {
	UserProfileContext.clearContext();
	setTestModel(TestModelCreator.create(SSO_GROOVY_SCRIPT_URL));

	List<Role> roles = getModelObject("roles", List.class);

	Map<Long, List<Role>> rolesMap = new HashMap<>();
	rolesMap.put(1L, roles);

	List<FolderPolicy> foldersPolicies = getModelObject("foldersPolicies", List.class);
	List<FolderPolicy> adminFoldersPolicies = getModelObject("adminFolderPolocies", List.class);
	List<ProjectUserLanguage> projectUserLanguages = getModelObject("projectUserLanguages", List.class);
	List<Submission> submissions = new ArrayList<>();

	TmUserProfile userProfile = getModelObject("userProfile", TmUserProfile.class);

	Set<String> languageSet = getModelObject("languageSet", HashSet.class);

	Map<Long, Set<String>> data = new HashMap<>();
	data.put(1L, languageSet);

	userProfile.setProjectUserLanguages(data);

	userProfile.initializeOrganizationUser(rolesMap, foldersPolicies, adminFoldersPolicies, projectUserLanguages,
		submissions);
	userProfile.setSystemRoles(new HashSet<>(roles));

	when(getUserProfileService().findById(any(Long.class))).thenReturn(userProfile);
	when(getUserProfileDAO().findUsersByUserNameNoFetch(any(String.class))).thenReturn(userProfile);
	when(getUserProfileService().findUsersByUserNameNoFetch(any(String.class))).thenReturn(userProfile);

	setUpProjectDetail();

	setTestModel(null);
	UserProfileContext.setCurrentUserProfile(userProfile);
    }

    private void resetMocks() {
	reset(getProjectDetailDAO());

    }

    private void setUpProjectDetail() {
	TmProject project = getModelObject("tmProject", TmProject.class);

	_projectDetail = getModelObject("projectDetail", ProjectDetail.class);
	_projectDetail.setProject(project);

	when(getProjectDetailDAO().load(any(Long.class))).thenReturn(_projectDetail);
    }

    EntityId getEntityId() {
	return new EntityId() {
	    @Override
	    public int compareTo(@NotNull EntityId o) {
		return 0;
	    }

	    @Override
	    public long getLocalId() {
		return 0;
	    }

	    @Override
	    public int getTypeId() {
		return 0;
	    }
	};
    }

    @Override
    protected String getTestSuiteRoot() {
	return TEST_SUITE_ROOT;
    }
}
