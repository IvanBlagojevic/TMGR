package org.gs4tr.termmanager.service;

import javax.sql.DataSource;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.gs4tr.foundation.modules.dao.test.DbUnitHelper;
import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.gs4tr.foundation.modules.usermanager.model.SystemAuthenticationHolder;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.termmanager.dao.ProjectLanguageDAO;
import org.gs4tr.termmanager.service.loghandler.ImportTransactionLogHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles(profiles = "defaultAuthentication")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:org/gs4tr/termmanager/dao/spring/applicationContext-dao.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-service.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-solr.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-aop.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-hibernate.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-security.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-systemTasks.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-translationTasks.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-init.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-test-repository.xml",
	"classpath:org/gs4tr/foundation/modules/spring/rmi/applicationContext-rmi.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-notifications.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-schedule.xml",
	"classpath:org/gs4tr/foundation/modules/security/spring/applicationContext-service.xml",
	"classpath:org/gs4tr/foundation/modules/usermanager/spring/applicationContext-service.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-test-migration.xml",
	"classpath:org/gs4tr/foundation/modules/mail/spring/applicationContext-mail.xml",
	"classpath:org/gs4tr/foundation/modules/webmvc/spring/applicationContext-i18n.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-solr.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-glossary-test-.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-mocks-cache.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-exclusiveWriteLockManager-mock.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-fileManager.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-fileAnalysis.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-projectTerminologyCounts.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-backup.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-import-report.xml",
	"classpath:org/gs4tr/termmanager/io/spring/applicationContext-io.xml" }, loader = TestEnvironmentAwareContextLoader.class)
public abstract class AbstractSpringServiceTests {

    private static final String DEFAULT_PASSWORD = "test";

    @BeforeClass
    public static void setUpClass() throws Exception {
	SingleConnectionDataSource ds = new SingleConnectionDataSource();
	ds.setDriverClassName("net.sf.log4jdbc.DriverSpy");
	ds.setUrl("jdbc:log4jdbc:h2:mem:test;create=true;MODE=MySQL;DB_CLOSE_ON_EXIT=FALSE");
	ds.setSuppressClose(true);

	SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
	builder.bind("java:comp/env/jdbc/termmanager", ds);
    }

    @Autowired
    private DataSource _dataSource;

    private DbUnitHelper _dbUnitHelper;

    @Autowired
    private ImportTermService _importTermService;

    @Autowired
    private ImportTransactionLogHandler _importTransactionLogHandler;

    @Autowired
    private PowerUserProjectRoleService _powerUserProjectRoleService;

    private Long _projectId = 1L;

    private ProjectLanguageDAO _projectLanguageDAO;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private RoleService _roleService;

    @Autowired
    private SessionService _sessionService;

    @Autowired
    private SetupService _setupService;

    @Autowired
    private StatisticsService _statisticsService;

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private TermService _termService;

    @Autowired
    private UserProfileService _userProfileService;

    protected final int SYNONYM_NUMBER = 5;

    public DataSource getDataSource() {
	return _dataSource;
    }

    public ImportTermService getImportTermService() {
	return _importTermService;
    }

    public ImportTransactionLogHandler getImportTransactionLogHandler() {
	return _importTransactionLogHandler;
    }

    public PowerUserProjectRoleService getPowerUserProjectRoleService() {
	return _powerUserProjectRoleService;
    }

    public ProjectLanguageDAO getProjectLanguageDAO() {
	return _projectLanguageDAO;
    }

    public RoleService getRoleService() {
	return _roleService;
    }

    public SessionService getSessionService() {
	return _sessionService;
    }

    public SetupService getSetupService() {
	return _setupService;
    }

    public StatisticsService getStatisticsService() {
	return _statisticsService;
    }

    public TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    public TermService getTermService() {
	return _termService;
    }

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    public void setSetupService(SetupService setupService) {
	_setupService = setupService;
    }

    @Before
    public void setUp() throws Exception {
	setupDatabase();
	setupSystemUser();
	setupPm();
    }

    @After
    public void tearDown() throws Exception {

    }

    private void setupDatabase() throws Exception {
	_dbUnitHelper = new DbUnitHelper(getDataSource());

	FlatXmlDataSetBuilder builderClean = new FlatXmlDataSetBuilder();
	builderClean.setColumnSensing(true);
	IDataSet cleanDataSet = builderClean
		.build(getClass().getClassLoader().getResourceAsStream("data-sets/cleanDataSet.xml"));

	FlatXmlDataSetBuilder builderRole = new FlatXmlDataSetBuilder();
	builderRole.setColumnSensing(true);
	IDataSet baseDataSet = builderRole
		.build(getClass().getClassLoader().getResourceAsStream("data-sets/baseDataSet.xml"));

	FlatXmlDataSetBuilder builderData = new FlatXmlDataSetBuilder();
	builderData.setColumnSensing(true);
	IDataSet roleDataSet = builderData
		.build(getClass().getClassLoader().getResourceAsStream("data-sets/roleDataSet.xml"));

	_dbUnitHelper.cleanDatabase(cleanDataSet);

	getSetupService().setup();

	_dbUnitHelper.setupDatabase(roleDataSet);
	_dbUnitHelper.setupDatabase(baseDataSet);
    }

    private void setupSystemUser() {
	getSessionService().logout();

	getSessionService().login(SystemAuthenticationHolder.SYSTEM_USERNAME,
		SystemAuthenticationHolder.SYSTEM_PASSWORD);

	Authentication systemAuthentication = SecurityContextHolder.getContext().getAuthentication();

	SystemAuthenticationHolder.setSystemAuthentication(systemAuthentication);
    }

    protected DbUnitHelper getDbUnitHelper() {
	return _dbUnitHelper;
    }

    protected Long getProjectId() {
	return _projectId;
    }

    protected ProjectService getProjectService() {
	return _projectService;
    }

    protected void setupPm() {
	setupUser("pm");
    }

    protected void setupUser(String username) {
	getSessionService().logout();

	getSessionService().login(username, DEFAULT_PASSWORD);
    }
}