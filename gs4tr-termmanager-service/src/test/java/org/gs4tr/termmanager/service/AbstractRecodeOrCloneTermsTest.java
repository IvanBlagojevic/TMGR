package org.gs4tr.termmanager.service;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.gs4tr.foundation.modules.usermanager.model.SystemAuthenticationHolder;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.termmanager.dao.ProjectLanguageDetailDAO;
import org.gs4tr.termmanager.dao.backup.DbTermEntryDAO;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTerm;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryHistory;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermHistory;
import org.gs4tr.termmanager.model.reindex.BackupSearchCommand;
import org.gs4tr.termmanager.service.solr.restore.RecodeOrCloneTermsProcessor;
import org.gs4tr.termmanager.service.solr.restore.model.RecodeOrCloneCommand;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
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
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-security.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-systemTasks.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-translationTasks.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-init.xml",
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
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-hibernate-test.xml",
	"classpath:org/gs4tr/termmanager/io/spring/applicationContext-io.xml" }, loader = TestEnvironmentAwareContextLoader.class)

public abstract class AbstractRecodeOrCloneTermsTest {

    public static final String DUMMY_TERM_ID = "dummy-term-00001";

    public static final String TERM_ENTRY_ID = "term-entry-00001";

    public static final String TERM_ID = "term-00001";

    @Autowired
    private CloneTermsService _cloneTermsService;

    @Autowired
    private SimpleDriverDataSource _dataSource;

    @Autowired
    private DbSubmissionTermEntryService _dbSubmissionTermEntryService;

    @Autowired
    private DbTermEntryDAO _dbTermEntryDAO;

    @Autowired
    private JdbcTemplate _jdbcTemplate;

    @Autowired
    private ProjectDetailService _projectDetailService;

    @Autowired
    private ProjectLanguageDetailDAO _projectLanguageDetailDAO;

    @Autowired
    private ProjectLanguageDetailService _projectLanguageDetailService;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private RecodeOrCloneTermsProcessor _recodeOrCloneTermsProcessor;

    @Autowired
    private RecodeTermsService _recodeTermsService;

    @Autowired
    private SessionService _sessionService;

    @Autowired
    private StatisticsService _statisticsService;

    @Autowired
    private SubmissionService _submissionService;

    protected static String DE = Locale.GERMAN.getCode();

    protected static String EN = Locale.ENGLISH.getCode();

    protected static String EN_US = Locale.US.getCode();

    protected static Long PROJECT_ID1 = 1L;

    protected static Long PROJECT_ID2 = 2L;

    protected static String PROJECT_NAME = "TestProject1";

    protected static String SHORT_CODE_PROJECT1 = "TES000001";

    protected static String USER = "ivan";

    protected RecodeOrCloneCommand _recodeOrCloneCommand;

    @Before
    public void setUp() throws SQLException {
	restartDatabase();
	setupSystemUser();
	setTestCommand();
    }

    private void collectTermHistories(Set<DbTermEntryHistory> termEntryHistories, List<DbTerm> dbTerms,
	    String language) {
	for (DbTermEntryHistory dbTermEntryHistory : termEntryHistories) {
	    Set<DbTermHistory> dbTermHistories = dbTermEntryHistory.getHistory();

	    dbTermHistories.forEach(termHistory -> readHistoryTermsForLang(termHistory, dbTerms, language));
	}
    }

    private SimpleDriverDataSource getDataSource() {
	return _dataSource;
    }

    private void readHistoryTermsForLang(DbTermHistory dbTermHistory, List<DbTerm> dbTerms, String language) {
	DbTerm dbTerm = JsonUtils.readValue(dbTermHistory.getRevision(), DbTerm.class);
	if (dbTerm.getLanguageId().equals(language)) {
	    dbTerms.add(dbTerm);
	}
    }

    private void restartDatabase() throws SQLException {

	Connection connection = getDataSource().getConnection();
	ScriptUtils.executeSqlScript(connection,
		new EncodedResource(new ClassPathResource("recodeCloneTest.sql"), StandardCharsets.UTF_8));

    }

    private void setTestCommand() {
	_recodeOrCloneCommand = new RecodeOrCloneCommand();
	_recodeOrCloneCommand.setLocaleFrom(EN);
	_recodeOrCloneCommand.setLocaleTo(EN_US);
	_recodeOrCloneCommand.setProjectId(PROJECT_ID1);
	_recodeOrCloneCommand.setProjectShortCode(SHORT_CODE_PROJECT1);
    }

    private void setupSystemUser() {
	getSessionService().logout();

	getSessionService().login(SystemAuthenticationHolder.SYSTEM_USERNAME,
		SystemAuthenticationHolder.SYSTEM_PASSWORD);

	Authentication systemAuthentication = SecurityContextHolder.getContext().getAuthentication();

	SystemAuthenticationHolder.setSystemAuthentication(systemAuthentication);
    }

    protected RecodeOrCloneCommand createRecodeOrCloneCommand(Long projectId, String languageFrom, String languageTo) {
	RecodeOrCloneCommand command = new RecodeOrCloneCommand();
	command.setLocaleFrom(languageFrom);
	command.setLocaleTo(languageTo);
	command.setProjectId(projectId);

	return command;
    }

    protected List<DbTerm> findTermsByLanguage(Set<DbTerm> dbTerms, String languageId) {
	return dbTerms.stream().filter(dbTerm -> dbTerm.getLanguageId().equals(languageId))
		.collect(Collectors.toList());
    }

    protected List<DbTerm> getAllHistoryDbTermsByLanguage(DbTermEntry[] dbTermEntries, String language) {

	List<DbTerm> dbTerms = new ArrayList<>();

	for (DbTermEntry dbTermEntry : dbTermEntries) {
	    Set<DbTermEntryHistory> termEntryHistories = dbTermEntry.getHistory();

	    collectTermHistories(termEntryHistories, dbTerms, language);

	}

	return dbTerms;
    }

    protected CloneTermsService getCloneTermsService() {
	return _cloneTermsService;
    }

    protected DbSubmissionTermEntryService getDbSubmissionTermEntryService() {
	return _dbSubmissionTermEntryService;
    }

    protected DbTermEntryDAO getDbTermEntryDAO() {
	return _dbTermEntryDAO;
    }

    protected ProjectDetailService getProjectDetailService() {
	return _projectDetailService;
    }

    protected ProjectLanguageDetailDAO getProjectLanguageDetailDAO() {
	return _projectLanguageDetailDAO;
    }

    protected ProjectLanguageDetailService getProjectLanguageDetailService() {
	return _projectLanguageDetailService;
    }

    protected ProjectService getProjectService() {
	return _projectService;
    }

    protected RecodeOrCloneCommand getRecodeOrCloneCommand() {
	return _recodeOrCloneCommand;
    }

    protected RecodeOrCloneTermsProcessor getRecodeOrCloneTermsProcessor() {
	return _recodeOrCloneTermsProcessor;
    }

    protected RecodeTermsService getRecodeTermsService() {
	return _recodeTermsService;
    }

    protected SessionService getSessionService() {
	return _sessionService;
    }

    protected StatisticsService getStatisticsService() {
	return _statisticsService;
    }

    protected SubmissionService getSubmissionService() {
	return _submissionService;
    }

    protected DbTermEntry[] getTermEntriesByProjectId(Long projectId) {
	PagedListInfo info = new PagedListInfo();
	BackupSearchCommand backupSearchCommand = new BackupSearchCommand(Collections.singletonList(projectId), null,
		true);
	PagedList<DbTermEntry> termEntryPagedList = getDbTermEntryDAO().getDbTermEntries(info, backupSearchCommand);
	return termEntryPagedList.getElements();
    }

}
