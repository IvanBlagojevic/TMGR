package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.gs4tr.foundation.modules.service.VersionService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class VersionServiceTest extends AbstractSpringServiceTests {

    @Autowired
    private VersionService _versionService;

    @Test
    public void checkVersionTest() {

	assertNotNull("Version service is invalid", getVersionService());
	String version = getVersionService().getCurrentVersion();
	assertNotNull("Invalid version", version);
	assertEquals("Versions are different", version, Version.getVersion());

    }

    protected VersionService getVersionService() {
	return _versionService;
    }

    protected void setVersionService(VersionService versionService) {
	_versionService = versionService;
    }
}
