package org.gs4tr.termmanager.service.reindex;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.gs4tr.termmanager.model.test.groups.SolrTest;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.service.DbTermEntryService;
import org.gs4tr.termmanager.service.solr.GlossaryConnectionManager;
import org.gs4tr.termmanager.service.solr.SolrServiceConfiguration;
import org.gs4tr.termmanager.service.solr.restore.ICleanUpProcessorV2;
import org.gs4tr.termmanager.service.solr.restore.IRestoreProcessorV2;
import org.gs4tr.tm3.api.TmException;
import org.junit.After;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:org/gs4tr/foundation/modules/usermanager/spring/applicationContext-service.xml",
	"classpath:org/gs4tr/foundation/modules/mail/spring/applicationContext-mail.xml",
	"classpath:org/gs4tr/foundation/modules/security/spring/applicationContext-service.xml",
	"classpath:org/gs4tr/foundation/modules/usermanager/spring/applicationContext-service.xml",
	"classpath:org/gs4tr/termmanager/dao/spring/applicationContext-dao.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-service.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-solr.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-notifications.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-init.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-security.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-systemTasks.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-translationTasks.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-mocks-cache.xml",
	"classpath:org/gs4tr/termmanager/service/spring/reindex/applicationContext-init.xml",
	"classpath:org/gs4tr/termmanager/service/spring/reindex/applicationContext-hibernate.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-backup.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-fileManager.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-fileAnalysis.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-projectTerminologyCounts.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-import-report.xml",
	"classpath:org/gs4tr/termmanager/io/spring/applicationContext-io.xml" }, loader = TestEnvironmentAwareContextLoader.class)
@Transactional
@Category(SolrTest.class)
public class AbstractReIndex {

    protected static final Long PROJECT_ID = 1L;

    @Autowired
    private ICleanUpProcessorV2 _cleanUpProcessorV2;

    @Autowired
    private GlossaryConnectionManager _connectionManager;

    private ITmgrGlossaryConnector _connector;

    @Autowired
    private SingleConnectionDataSource _dataSource;

    @Autowired
    private DbTermEntryService _dbTermEntryService;

    @Autowired
    private JdbcTemplate _jdbcTemplate;

    @Autowired
    private IRestoreProcessorV2 _restoreProcessorV2;

    @Autowired
    private SolrServiceConfiguration _solrConfig;

    protected final Log LOGGER = LogFactory.getLog(this.getClass());

    @Before
    public void setUp() throws Exception {
	long start = System.currentTimeMillis();

	bindDataSource();

	initConnector();

	getConnector().getTmgrUpdater().deleteAll();

	restartDatabase();

	LOGGER.info("Time to startup: " + (System.currentTimeMillis() - start));
    }

    @After
    public void tearDown() throws Exception {
	if (Objects.nonNull(getConnector())) {
	    getConnector().getTmgrUpdater().deleteAll();
	}

	cleanUpDB();
    }

    private void bindDataSource() throws Exception {
	TestEnvironmentAwareContextLoader loader = new TestEnvironmentAwareContextLoader();
	ConfigurableApplicationContext context = loader
		.loadContext("org/gs4tr/termmanager/service/spring/reindex/applicationContext-hibernate.xml");

	SingleConnectionDataSource dataSource = (SingleConnectionDataSource) context.getBean("testDataSource");

	SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
	builder.bind("java:comp/env/jdbc/termmanager", dataSource);
    }

    private GlossaryConnectionManager getConnectionManager() {
	return _connectionManager;
    }

    private ITmgrGlossaryConnector getConnector() {
	return _connector;
    }

    private SingleConnectionDataSource getDataSource() {
	return _dataSource;
    }

    private JdbcTemplate getJdbcTemplate() {
	return _jdbcTemplate;
    }

    private SolrServiceConfiguration getSolrConfig() {
	return _solrConfig;
    }

    private void initConnector() throws TmException {
	_connector = getConnectionManager().getConnector(getRegularCollection());
	_connector.connect();
    }

	protected void restartDatabase() throws SQLException {
	Connection connection = getDataSource().getConnection();
	ScriptUtils.executeSqlScript(connection,
		new EncodedResource(new ClassPathResource("tmgr_io_test.sql"), StandardCharsets.UTF_8));
    }

    protected void cleanUpDB() {
	JdbcTemplate jdbcTemplate = getJdbcTemplate();

	jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = 0;");

	String q1 = "DELETE FROM TM_TERMENTRY_DESCRIPTION";
	jdbcTemplate.update(q1);

	String q2 = "DELETE FROM TM_TERM_HISTORY";
	jdbcTemplate.update(q2);

	String q3 = "DELETE FROM TM_TERMENTRY_HISTORY";
	jdbcTemplate.update(q3);

	String q4 = "DELETE FROM TM_TERM_DESCRIPTION";
	jdbcTemplate.update(q4);

	String q5 = "DELETE FROM TM_TERM";
	jdbcTemplate.update(q5);

	String q6 = "DELETE FROM TM_TERMENTRY;";
	jdbcTemplate.update(q6);

	String q7 = "UPDATE TM_PROJECT_DETAIL SET TM_PROJECT_DETAIL.TERMENTRY_COUNT = 0, TM_PROJECT_DETAIL.TERM_COUNT = 0, TM_PROJECT_DETAIL.APPROVE_TERM_COUNT = 0, TM_PROJECT_DETAIL.PENDING_APPROVAL_TERM_COUNT= 0, TM_PROJECT_DETAIL.ON_HOLD_TERM_COUNT = 0, TM_PROJECT_DETAIL.FORBIDDEN_TERM_COUNT = 0, TM_PROJECT_DETAIL.ACTIVE_SUBMISSION_COUNT = 0, TM_PROJECT_DETAIL.COMPLETED_SUBMISSION_COUNT = 0, TM_PROJECT_DETAIL.TERM_IN_SUBMISSION_COUNT = 0;";
	jdbcTemplate.update(q7);

	String q8 = "UPDATE TM_PROJECT_LANGUAGE_DETAIL SET TM_PROJECT_LANGUAGE_DETAIL.APPROVE_TERM_COUNT = 0, TM_PROJECT_LANGUAGE_DETAIL.FORBIDDEN_TERM_COUNT = 0, TM_PROJECT_LANGUAGE_DETAIL.ON_HOLD_TERM_COUNT = 0, TM_PROJECT_LANGUAGE_DETAIL.PENDING_APPROVAL_TERM_COUNT = 0, TM_PROJECT_LANGUAGE_DETAIL.TERM_IN_SUBMISSION_COUNT= 0, TM_PROJECT_LANGUAGE_DETAIL.COMPLETED_SUBMISSION_COUNT = 0, TM_PROJECT_LANGUAGE_DETAIL.ACTIVE_SUBMISSION_COUNT= 0, TM_PROJECT_LANGUAGE_DETAIL.TERMENTRY_COUNT = 0, TM_PROJECT_LANGUAGE_DETAIL.TERM_COUNT = 0;";
	jdbcTemplate.update(q8);

	String q9 = "UPDATE TM_STATISTICS SET TM_STATISTICS.ADDED_APPROVED_COUNT = 0, TM_STATISTICS.ADDED_BLACKLISTED_COUNT = 0, TM_STATISTICS.ADDED_ON_HOLD_COUNT = 0, TM_STATISTICS.ADDED_PENDING_COUNT = 0, TM_STATISTICS.DELETED_COUNT = 0, TM_STATISTICS.DEMOTED_COUNT = 0, TM_STATISTICS.ON_HOLD_COUNT = 0, TM_STATISTICS.APPROVED_COUNT = 0, TM_STATISTICS.BLACKLISTED_COUNT=0, TM_STATISTICS.UPDATED_COUNT = 0;";
	jdbcTemplate.update(q9);

	jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = 1;");
    }

    protected ITmgrGlossaryBrowser getBrowser() throws TmException {
	return _connector.getTmgrBrowser();
    }

    protected ICleanUpProcessorV2 getCleanUpProcessorV2() {
	return _cleanUpProcessorV2;
    }

    protected DbTermEntryService getDbTermEntryService() {
	return _dbTermEntryService;
    }

    protected String getRegularCollection() {
	return getSolrConfig().getRegularCollection();
    }

    protected IRestoreProcessorV2 getRestoreProcessorV2() {
	return _restoreProcessorV2;
    }
}
