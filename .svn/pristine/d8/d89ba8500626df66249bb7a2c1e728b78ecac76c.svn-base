package org.gs4tr.termmanager.service.file.analysis;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.gs4tr.termmanager.service.mocking.groovy.AbstractGroovyTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:org/gs4tr/foundation/modules/webmvc/spring/applicationContext-i18n.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-applicationContextLocator.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-fileManager.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-fileAnalysis.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-mocks-cache.xml" }, loader = TestEnvironmentAwareContextLoader.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public abstract class AbstractSpringFilesAnalysisTest extends AbstractGroovyTest {

    private static final String TEST_SUITE_ROOT = "src/test/resources/groovy/";

    final Log LOG = LogFactory.getLog(getClass());

    @Rule
    public TestName _testMethodName = new TestName();

    @Before
    public void prepareTestModel() {
	prepareTestModel(getTestMethodName().getMethodName());
    }

    private TestName getTestMethodName() {
	return _testMethodName;
    }

    protected URL getResourceFrom(String pathname) {
	return AbstractSpringFilesAnalysisTest.class.getClassLoader().getResource(pathname);
    }

    @Override
    protected String getTestSuiteRoot() {
	return TEST_SUITE_ROOT;
    }
}
