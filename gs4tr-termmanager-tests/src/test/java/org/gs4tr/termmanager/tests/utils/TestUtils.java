package org.gs4tr.termmanager.tests.utils;

import java.io.IOException;
import java.lang.reflect.Method;

import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.springframework.core.io.ClassPathResource;

public final class TestUtils {

    private static final String ROOT_TEST_JSON_FOLDER = "/json/"; //$NON-NLS-1$

    /**
     * Computes test case location as per pattern /src/test/resources/json/
     * <TEST-SUITE>/[<TEST-CASE>]
     * 
     * TestSuite is mandatory annotation, and it defines root folder for a test
     * class. TestCase is optional annotation, and it defines resources folder
     * for a single test.
     */
    public static String getTestCaseFolder(Class<?> testClass, String testMethod) {
	TestSuite annotation = testClass.getAnnotation(TestSuite.class);
	if (annotation == null) {
	    throw new RuntimeException(Messages.getString("TestUtils.0")); //$NON-NLS-1$
	}

	StringBuilder path = new StringBuilder(ROOT_TEST_JSON_FOLDER);
	path.append(annotation.value());

	String testMethodFolder = getTestMethodFolder(testClass, testMethod);
	if (testMethodFolder != null) {
	    path.append(StringConstants.SLASH);
	    path.append(testMethodFolder);
	}

	ClassPathResource classPathResource = new ClassPathResource(path.toString());

	try {
	    return classPathResource.getFile().getAbsolutePath();
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    private static String getTestMethodFolder(Class<?> testClass, String testMethodName) {
	try {
	    Method testMethod = testClass.getMethod(testMethodName);

	    TestCase annotation = testMethod.getAnnotation(TestCase.class);

	    // TestCase is not mandatory
	    return annotation == null ? null : annotation.value();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

}
