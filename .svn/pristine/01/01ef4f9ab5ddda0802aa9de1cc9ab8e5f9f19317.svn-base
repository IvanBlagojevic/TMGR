package org.gs4tr.termmanager.service.mocking;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.gs4tr.termmanager.dao.ProjectDetailDAO;
import org.gs4tr.termmanager.dao.ProjectLanguageDetailDAO;
import org.gs4tr.termmanager.dao.SubmissionDAO;
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
import org.gs4tr.termmanager.service.PluginsService;
import org.gs4tr.termmanager.service.TermEntryExporter;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.TermService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.mocking.groovy.AbstractGroovyTest;
import org.gs4tr.termmanager.service.mocking.groovy.TestModelCreator;
import org.gs4tr.termmanager.service.solr.GlossaryConnectionManager;
import org.gs4tr.tm3.api.TmException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
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

import jetbrains.exodus.entitystore.EntityId;

@ActiveProfiles(profiles = "defaultAuthentication")
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
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-import-report.xml" }, loader = TestEnvironmentAwareContextLoader.class)
public abstract class AbstractServiceTest extends AbstractGroovyTest {

    private static final String ABSTRACT_GROOVY_SCRIPT_URL = "src/test/resources/groovy/abstractInitializeUser.groovy"; //$NON-NLS-1$

    private static final String TEST_SUITE_ROOT = "src/test/resources/groovy/"; //$NON-NLS-1$

    private final Log LOGGER = LogFactory.getLog(this.getClass());

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
    private PluginsService _pluginsService;

    private ProjectDetail _projectDetail;

    @Autowired
    private ProjectDetailDAO _projectDetailDAO;

    @Autowired
    private ProjectLanguageDetailDAO _projectLanguageDetailDAO;

    private Query _query;

    @Autowired
    private SessionFactory _sessionFactory;

    @Autowired
    private SubmissionDAO _submissionDAO;

    @Autowired
    private TermEntryExporter _termEntryExporter;

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private TermService _termService;

    @Autowired
    private TransactionLogHandler _transactionLogHandler;

    @Autowired
    private UserProfileService _userProfileService;

    @Autowired
    protected UserProfileDAO _userProfileDAO;

    @Rule
    public TestName _testMethodName = new TestName();

    @Rule
    public TestName _testName = new TestName();

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
	mockSession();
	prepareTestModel(_testMethodName.getMethodName());
    }

    private SessionFactory getSessionFactory() {
	return _sessionFactory;
    }

    private void mockSession() {
	/* Session and transaction */
	org.hibernate.Session session = Mockito.mock(org.hibernate.Session.class);
	when(getSessionFactory().openSession()).thenReturn(session);

	Query query = Mockito.mock(org.hibernate.Query.class);
	when(session.getNamedQuery(anyString())).thenReturn(query);

	setQuery(query);

	Transaction tx = Mockito.mock(Transaction.class);
	when(session.beginTransaction()).thenReturn(tx);
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
	// force mocking without annotation
	setTestModel(TestModelCreator.create(ABSTRACT_GROOVY_SCRIPT_URL));

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
	reset(getTermEntryService());
	reset(getProjectLanguageDetailDAO());
	reset(getProjectDetailDAO());
	reset(getUserProfileService());
	reset(getConnectionManager());
	// reset(getConnector());
	reset(getGlossaryBrowser());
	reset(getGlossaryUpdater());
	reset(getGlossarySearcher());
	reset(getUserProfileDAO());
	reset(getTransactionLogHandler());
    }

    protected GlossaryConnectionManager getConnectionManager() {
	return _connectionManager;
    }

    protected ITmgrGlossaryConnector getConnector() {
	return _connector;
    }

    protected EntityId getEntityId() {
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

    protected ITmgrGlossaryBrowser getGlossaryBrowser() {
	return _glossaryBrowser;
    }

    protected ITmgrGlossarySearcher getGlossarySearcher() {
	return _glossarySearcher;
    }

    protected ITmgrGlossaryUpdater getGlossaryUpdater() {
	return _glossaryUpdater;
    }

    protected ProjectDetail getProjectDetail() {
	return _projectDetail;
    }

    protected ProjectDetailDAO getProjectDetailDAO() {
	return _projectDetailDAO;
    }

    protected ProjectLanguageDetailDAO getProjectLanguageDetailDAO() {
	return _projectLanguageDetailDAO;
    }

    protected Query getQuery() {
	return _query;
    }

    protected SubmissionDAO getSubmissionDAO() {
	return _submissionDAO;
    }

    protected TermEntryExporter getTermEntryExporter() {
	return _termEntryExporter;
    }

    protected TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    protected TermService getTermService() {
	return _termService;
    }

    @Override
    protected String getTestSuiteRoot() {
	return TEST_SUITE_ROOT;
    }

    protected TransactionLogHandler getTransactionLogHandler() {
	return _transactionLogHandler;
    }

    protected UserProfileDAO getUserProfileDAO() {
	return _userProfileDAO;
    }

    protected UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    protected void setProjectDetail(ProjectDetail projectDetail) {
	_projectDetail = projectDetail;
    }

    protected void setQuery(Query query) {
	_query = query;
    }

    protected void setUpProjectDetail() {
	TmProject project = getModelObject("tmProject", TmProject.class);

	_projectDetail = getModelObject("projectDetail", ProjectDetail.class);
	_projectDetail.setProject(project);

	when(getProjectDetailDAO().load(any(Long.class))).thenReturn(_projectDetail);
    }
}
