package org.gs4tr.termmanager.service.project.terminology.counts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.gs4tr.termmanager.service.mocking.groovy.AbstractGroovyTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-projectTerminologyCounts.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-mocks-projectTerminologyCounts.xml" }, loader = TestEnvironmentAwareContextLoader.class)
public abstract class AbstractSpringProjectTerminologyCountsProviderTest extends AbstractGroovyTest {

    private static final String TEST_SUITE_ROOT = "src/test/resources/groovy/";

    @Rule
    public TestName _testMethodName = new TestName();

    protected final Log LOG = LogFactory.getLog(getClass());

    @Before
    public void prepareTestModel() {
	prepareTestModel(getTestMethodName().getMethodName());
    }

    private TestName getTestMethodName() {
	return _testMethodName;
    }

    @Override
    protected String getTestSuiteRoot() {
	return TEST_SUITE_ROOT;
    }
}
