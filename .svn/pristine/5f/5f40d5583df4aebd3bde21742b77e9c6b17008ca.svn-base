package org.gs4tr.termmanager.service.mocking.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.util.ObjectGraphBuilder;

import java.io.File;

import org.gs4tr.foundation.modules.entities.model.test.TestModelClassNameResolver;

public class TestModelCreator {

    public static TestModel create(String fileName) {
	try {
	    GroovyShell groovyShell = new GroovyShell();
	    Script compiledScript = groovyShell.parse(new File(fileName));

	    ObjectGraphBuilder builder = new ObjectGraphBuilder();

	    builder.setClassNameResolver(new TestModelClassNameResolver());
	    builder.setChildPropertySetter(new TestModelChildSetter());

	    Binding binding = new Binding();
	    binding.setProperty("builder", builder);

	    compiledScript.setBinding(binding);
	    compiledScript.run();

	    TestModel testModel = new TestModel(binding);
	    return testModel;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

}
