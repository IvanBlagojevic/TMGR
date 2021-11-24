package org.gs4tr.termmanager.service.mocking.manualtask;

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
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation.modules.jsonvalidator.JSONValidator;
import org.gs4tr.foundation.modules.jsonvalidator.JSONValidatorFactory;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.gs4tr.termmanager.model.FolderPolicy;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.mocking.groovy.AbstractGroovyTest;
import org.gs4tr.termmanager.service.mocking.groovy.TestModelCreator;
import org.gs4tr.termmanager.service.mocking.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles(profiles = "defaultAuthentication")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:org/gs4tr/foundation/modules/webmvc/spring/applicationContext-i18n.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-applicationContextLocator.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-mocks-manualtask.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-test-manualtask.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-systemTasks.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-translationTasks.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-dao-mocks.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-solr.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-fileManager.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-backup.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-import-report.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-mocks-cache.xml" }, loader = TestEnvironmentAwareContextLoader.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public abstract class AbstractManualtaskTest extends AbstractGroovyTest {

    private static final Log _logger = LogFactory.getLog(AbstractManualtaskTest.class);
    private static final String ABSTRACT_GROOVY_SCRIPT_URL = "src/test/resources/groovy/abstractInitializeUser.groovy"; //$NON-NLS-1$
    private static final String TEST_SUITE_ROOT = "src/test/resources/groovy/"; //$NON-NLS-1$

    @Rule
    public TestName _testMethodName = new TestName();

    @Rule
    public TestName _testName = new TestName();

    @After
    public void afterTest() {
	if (_logger.isDebugEnabled()) {
	    _logger.debug(String.format("Test ended - %s", _testMethodName.getMethodName()));
	}

	UserProfileContext.clearContext();
    }

    @Before
    public void beforeTest() {
	if (_logger.isDebugEnabled()) {
	    _logger.debug(String.format("Test started - %s", _testMethodName.getMethodName()));
	}

	MockitoAnnotations.initMocks(this);
	mockUser();
	prepareTestModel(_testMethodName.getMethodName());

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

	setTestModel(null);
	UserProfileContext.setCurrentUserProfile(userProfile);
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

    @Override
    protected String getTestSuiteRoot() {
	return TEST_SUITE_ROOT;
    }

    protected void throwWaitingThreadException() {
	throw new RuntimeException("Thread waiting has timed out.");
    }

}
