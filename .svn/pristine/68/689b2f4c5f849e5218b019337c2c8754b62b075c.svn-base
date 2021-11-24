package org.gs4tr.termmanager.webmvc.groovy;

import groovy.lang.Binding;

public class TestModel {

    private Binding _binding;

    public TestModel(Binding binding) {
	_binding = binding;
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(String name, Class<T> clazz) {
	Object object = _binding.getProperty(name);
	return (T) object;
    }

    public void setBinding(Binding binding) {
	_binding = binding;
    }
}
