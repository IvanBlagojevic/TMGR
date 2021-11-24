package org.gs4tr.termmanager.migration;

import org.springframework.beans.factory.FactoryBean;

public class VersionHolder implements FactoryBean<String> {

    @Override
    public String getObject() throws Exception {
	return Version.getVersion();
    }

    @Override
    public Class<?> getObjectType() {
	return String.class;
    }

    @Override
    public boolean isSingleton() {
	return true;
    }
}
