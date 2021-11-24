package org.gs4tr.termmanager.service.file.manager;

import java.net.URL;
import java.nio.file.Paths;

import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.gs4tr.termmanager.service.mocking.groovy.AbstractGroovyTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:org/gs4tr/termmanager/service/spring/applicationContext-init-base.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-fileManager.xml" }, loader = TestEnvironmentAwareContextLoader.class)
public abstract class AbstractSpringFileManagerTest extends AbstractGroovyTest {

    private static final String PARENT_DIRECTORY = "filemanager";
    private static final String TEST_SUITE_ROOT = "src/test/resources/groovy/";
    
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
	Class<AbstractSpringFileManagerTest> clazz = AbstractSpringFileManagerTest.class;
	String path = Paths.get(PARENT_DIRECTORY, pathname).toString();
	URL resource = clazz.getClassLoader().getResource(path);
	return resource;
    }

    @Override
    protected String getTestSuiteRoot() {
	return TEST_SUITE_ROOT;
    }
}
