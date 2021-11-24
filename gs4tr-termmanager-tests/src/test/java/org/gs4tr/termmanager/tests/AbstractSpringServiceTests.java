package org.gs4tr.termmanager.tests;

import static org.gs4tr.termmanager.service.manualtask.CheckAnalysisProgressTaskHandler.COMPLETED;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.dao.test.DbUnitHelper;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.jsonvalidator.JSONValidator;
import org.gs4tr.foundation.modules.jsonvalidator.JSONValidatorFactory;
import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareWebContextLoader;
import org.gs4tr.foundation.modules.usermanager.model.SystemAuthenticationHolder;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.foundation.modules.webmvc.rest.filters.InternalRestAuthenticationFilter;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.model.update.ConnectionInfoHolder;
import org.gs4tr.termmanager.service.PluginsService;
import org.gs4tr.termmanager.service.ProjectDetailService;
import org.gs4tr.termmanager.service.ProjectLanguageDetailService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.SetupService;
import org.gs4tr.termmanager.service.SubmissionService;
import org.gs4tr.termmanager.service.SubmissionTermService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.TermService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.concurrency.ServiceThreadPoolHandler;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.CheckAnalysisProgressCommand;
import org.gs4tr.termmanager.service.model.command.ImportCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.utils.TestUtils;
import org.gs4tr.termmanager.webmvc.model.commands.TaskCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles(profiles = "defaultAuthentication")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:org/gs4tr/termmanager/dao/spring/applicationContext-dao.xml",
	"classpath:org/gs4tr/termmanager/tests/spring/applicationContext-test-service.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-aop.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-hibernate.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-security.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-systemTasks.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-translationTasks.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-solr.xml",
	"classpath:org/gs4tr/termmanager/tests/spring/applicationContext-init.xml",
	"classpath:org/gs4tr/termmanager/tests/spring/applicationContext-test-repository.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-schedule.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-notifications.xml",
	"classpath:org/gs4tr/termmanager/tests/spring/applicationContext-test-migration.xml",
	"classpath:org/gs4tr/foundation/modules/usermanager/spring/applicationContext-service.xml",
	"classpath:org/gs4tr/foundation/modules/security/spring/applicationContext-dao.xml",
	"classpath:org/gs4tr/foundation/modules/security/spring/applicationContext-service.xml",
	"classpath:org/gs4tr/foundation/modules/webmvc/spring/applicationContext-security-commons.xml",
	"classpath:org/gs4tr/foundation/modules/webmvc/spring/applicationContext-security-default.xml",
	"classpath:org/gs4tr/foundation/modules/webmvc/spring/applicationContext-mvc.xml",
	"classpath:org/gs4tr/termmanager/webmvc/spring/applicationContext-mvc-security.xml",
	"classpath:org/gs4tr/foundation/modules/webmvc/spring/applicationContext-mvc-session-management.xml",
	"classpath:org/gs4tr/foundation/modules/webmvc/spring/applicationContext-rest.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-service.xml",
	"classpath:org/gs4tr/termmanager/webmvc/spring/applicationContext-configuration.xml",
	"classpath:org/gs4tr/termmanager/webmvc/spring/applicationContext-mvc.xml",
	"classpath:org/gs4tr/termmanager/tests/spring/applicationContext-test-webmvc.xml",
	"classpath:org/gs4tr/termmanager/tests/spring/applicationContext-test.xml",
	"classpath:org/gs4tr/foundation/modules/webmvc/spring/applicationContext-i18n.xml",
	"classpath:org/gs4tr/foundation/modules/mail/spring/applicationContext-mail.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-fileManager.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-fileAnalysis.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-projectTerminologyCounts.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-import-report.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-backup.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-health.xml",
	"classpath:org/gs4tr/termmanager/webmvc/spring/applicationContext-rest-health.xml",
	"classpath:org/gs4tr/termmanager/cache/spring/applicationContext-hazelcast.xml",
	"classpath:org/gs4tr/termmanager/io/spring/applicationContext-io.xml" }, loader = TestEnvironmentAwareWebContextLoader.class)
@TestExecutionListeners({ TransactionalTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class })
public abstract class AbstractSpringServiceTests {

    private static final String DEFAULT_PASSWORD = "test";
    private static final Log LOGGER = LogFactory.getLog(AbstractSpringServiceTests.class);
    private static final long TIMEOUT = 300000L; // 5 min
    private static final String TMP = System.getProperty("java.io.tmpdir");
    private static final int WAIT_TIME = 20000;

    @BeforeClass
    public static void setUpClass() throws Exception {
	SingleConnectionDataSource ds = new SingleConnectionDataSource();
	ds.setDriverClassName("net.sf.log4jdbc.DriverSpy");
	ds.setUrl("jdbc:log4jdbc:h2:mem:test;create=true;MODE=MySQL;DB_CLOSE_ON_EXIT=FALSE");
	ds.setSuppressClose(true);

	SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
	builder.bind("java:comp/env/jdbc/termmanager", ds);

	initLocales();
    }

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, ConnectionInfoHolder> _cacheGateway;

    @Autowired
    private DataSource _dataSource;

    private DbUnitHelper _dbUnitHelper;

    @Autowired
    private InternalRestAuthenticationFilter _internalRestAuthenticationFilter;

    @Autowired
    private PluginsService _pluginsService;

    @Autowired
    private ProjectDetailService _projectDetailService;

    private Long _projectId;

    @Autowired
    private ProjectLanguageDetailService _projectLanguageDetailService;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private SessionService _sessionService;

    @Autowired
    private SetupService _setupService;

    @Autowired
    private SubmissionService _submissionService;

    @Autowired
    private SubmissionTermService _submissionTermService;

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private TermService _termService;

    @Autowired
    private UserProfileService _userProfileService;

    @Autowired
    private WebApplicationContext _webApplicationContext;

    protected final Log LOG = LogFactory.getLog(getClass());

    protected final int SYNONYM_NUMBER = 5;

    protected MockMvc _mockMvc;

    @Rule
    public TestName _testName = new TestName();

    public DataSource getDataSource() {
	return _dataSource;
    }

    public InternalRestAuthenticationFilter getInternalRestAuthenticationFilter() {
	return _internalRestAuthenticationFilter;
    }

    public String getJsonData(String jsonInputName, String[]... varReplacements) {
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

    public PluginsService getPluginsService() {
	return _pluginsService;
    }

    public ProjectDetailService getProjectDetailService() {
	return _projectDetailService;
    }

    public ProjectLanguageDetailService getProjectLanguageDetailService() {
	return _projectLanguageDetailService;
    }

    public SessionService getSessionService() {
	return _sessionService;
    }

    public SetupService getSetupService() {
	return _setupService;
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

    public WebApplicationContext getWebApplicationContext() {
	return _webApplicationContext;
    }

    public void setSetupService(SetupService setupService) {
	_setupService = setupService;
    }

    @Before
    public void setUp() throws Exception {
	_mockMvc = MockMvcBuilders.webAppContextSetup(getWebApplicationContext())
		.addFilters(getInternalRestAuthenticationFilter()).build();
	setupDatabase();
	setupSystemUser();
	setupPm();
	waitServiceThreadPoolThreads();
    }

    @After
    public void tearDown() {
	waitServiceThreadPoolThreads();
    }

    private TaskCommand getTaskCommand(String jsonInputName, String[][] varReplacements) {
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

	TaskCommand taskCommand = new TaskCommand();
	taskCommand.setJsonTaskData(inputJson);

	return taskCommand;
    }

    private static void initLocales() {
	Locale.US.getCode();
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

	_dbUnitHelper.setupDatabase(baseDataSet);
	_dbUnitHelper.setupDatabase(roleDataSet);
    }

    private void setupSystemUser() {
	getSessionService().logout();

	getSessionService().login(SystemAuthenticationHolder.SYSTEM_USERNAME,
		SystemAuthenticationHolder.SYSTEM_PASSWORD);

	Authentication systemAuthentication = SecurityContextHolder.getContext().getAuthentication();

	SystemAuthenticationHolder.setSystemAuthentication(systemAuthentication);
    }

    private void setupUser(String username) {
	getSessionService().logout();

	getSessionService().login(username, DEFAULT_PASSWORD);
    }

    private void sleep(final long time) {
	try {
	    Thread.sleep(time);
	} catch (InterruptedException e) {
	    LOG.error(e.getMessage(), e);
	    Thread.currentThread().interrupt();
	}
    }

    protected Map<String, Object> analyzeAsync(ImportCommand analysisCommand) throws TimeoutException {
	final ManualTaskHandler importTaskHandler = getHandler("import tbx");

	Map<String, Object> model = importTaskHandler.processTasks(null, null, analysisCommand, null).getModel();
	String processingId = (String) model.get("analysisProcessingId");
	assertTrue(StringUtils.isNotEmpty(processingId));

	CheckAnalysisProgressCommand checkProgressCommand = new CheckAnalysisProgressCommand();
	checkProgressCommand.setProcessingId(processingId);

	final ManualTaskHandler checkProgressTaskHandler = getHandler("check analysis progress");
	long start = System.currentTimeMillis();
	model = checkProgressTaskHandler.getTaskInfos(null, null, checkProgressCommand)[0].getModel();

	boolean completed = (boolean) model.get(COMPLETED);
	while (!completed) {
	    long now = System.currentTimeMillis();
	    if (now - start >= TIMEOUT) {
		LOG.error(String.format("Analysis of %s file(s) took more than: %d to complete", //$NON-NLS-1$
			analysisCommand.getNumberOfTermEntriesByFileName().keySet(), TIMEOUT));
		throw new TimeoutException("Timeout has occurred while waiting for analysis to complete.");
	    }
	    sleep(1000);
	    model = checkProgressTaskHandler.getTaskInfos(null, null, checkProgressCommand)[0].getModel();
	    completed = (boolean) model.get(COMPLETED);
	}

	return model;
    }

    protected final void assertJSONResponse(String jsonObject, String validationRuleName) throws Exception {
	// compute test case folder
	String testCaseFolder = TestUtils.getTestCaseFolder(this.getClass(), _testName.getMethodName());

	// load json validator
	JSONValidator validator = JSONValidatorFactory
		.createInstance(testCaseFolder + StringConstants.FOLDER_SEPARATOR + validationRuleName);

	// perform actual validation
	validator.validate(jsonObject);
    }

    protected CacheGateway<String, ConnectionInfoHolder> getCacheGateway() {
	return _cacheGateway;
    }

    protected DbUnitHelper getDbUnitHelper() {
	return _dbUnitHelper;
    }

    protected final ManualTaskHandler getHandler(String handlerName) {
	return getPluginsService().getUserTaskHandler(handlerName);
    }

    protected Long getProjectId() {
	return _projectId;
    }

    protected ProjectService getProjectService() {
	return _projectService;
    }

    protected SubmissionService getSubmissionService() {
	return _submissionService;
    }

    protected SubmissionTermService getSubmissionTermService() {
	return _submissionTermService;
    }

    protected Object getTaskHandlerCommand(ManualTaskHandler taskHandler, String jsonInputName,
	    String[]... varReplacements) {
	TaskCommand taskCommand = getTaskCommand(jsonInputName, varReplacements);

	Class<? extends DtoTaskHandlerCommand<?>> taskHandlerCommandClazz = taskHandler.getCommandClass();

	Object taskHandlerCommand = null;

	if (taskHandlerCommandClazz != null) {
	    String jsonTaskData = taskCommand.getJsonTaskData();

	    if (StringUtils.isBlank(jsonTaskData)) {
		return null;
	    }

	    DtoTaskHandlerCommand<?> dtoTaskHandlerCommand = JsonUtils.readValue(jsonTaskData, taskHandlerCommandClazz);

	    if (dtoTaskHandlerCommand == null) {
		return null;
	    }

	    taskHandlerCommand = dtoTaskHandlerCommand.convertToInternalTaskHandlerCommand();
	}

	return taskHandlerCommand;
    }

    protected void setupPm() {
	setupUser("pm");
    }

    protected void setupTranslator() {
	setupUser("translator");
    }

    protected void throwWaitingThreadException() {
	throw new RuntimeException("Thread waiting has timed out.");
    }

    protected void waitServiceThreadPoolThreads() {
	long start = System.currentTimeMillis();

	while (true) {
	    try {
		Thread.sleep(500);
	    } catch (InterruptedException e) {
		LOGGER.error(e.getMessage(), e);
		throw new RuntimeException(e);
	    }

	    int activeThreads = ServiceThreadPoolHandler.getActiveThreadsCount();

	    LogHelper.debug(LOGGER, String.format("Active threads count: %d", activeThreads));

	    if (activeThreads == 0) {
		break;
	    }

	    long now = System.currentTimeMillis();
	    if (now - start > WAIT_TIME) {
		throw new RuntimeException("Time for waiting ServiceThreadPool threads exceeded.");
	    }
	}
    }
}