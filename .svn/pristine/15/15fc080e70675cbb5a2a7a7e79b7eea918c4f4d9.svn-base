package org.gs4tr.termmanager.webmvc.groovy;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestSuite;

public abstract class AbstractGroovyTest {

    private TestModel _testModel;

    private static final String TEST_CASE_EXSTENSION = ".groovy";

    public TestModel getTestModel() {
	return _testModel;
    }

    public void setTestModel(TestModel testModel) {
	_testModel = testModel;
    }

    private String buildTestCasePath(TestSuite testSuite, TestCase testCase) {
	StringBuilder path = new StringBuilder(getTestSuiteRoot());
	path.append(testSuite.value());
	path.append(StringConstants.SLASH);
	path.append(testCase.value());
	path.append(TEST_CASE_EXSTENSION);
	return path.toString();
    }

    protected <T> T getModelObject(String name, Class<T> clazz) {
	T object;
	if (getTestModel() == null) {
	    throw new RuntimeException("add prepareTestModel call to test setup");
	} else {

	    object = getTestModel().getObject(name, clazz);
	}
	return object;
    }

    protected abstract String getTestSuiteRoot();

    protected void prepareTestModel(String methodName) {
	try {
	    Class<?> clazz = this.getClass();
	    AnnotatedElement annotatedClass = clazz;
	    TestSuite classAnnotation = annotatedClass.getAnnotation(TestSuite.class);
	    Method testMethod = clazz.getMethod(methodName, new Class<?>[] {});
	    TestCase methodAnnotation = testMethod.getAnnotation(TestCase.class);
	    setTestModel(null);

	    if (classAnnotation != null && methodAnnotation != null) {
		String testCasePath = buildTestCasePath(classAnnotation, methodAnnotation);
		setTestModel(TestModelCreator.create(testCasePath));
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

}
