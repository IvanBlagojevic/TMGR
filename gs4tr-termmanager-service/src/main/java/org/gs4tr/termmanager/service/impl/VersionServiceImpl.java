package org.gs4tr.termmanager.service.impl;

import org.gs4tr.foundation.modules.service.VersionService;
import org.gs4tr.termmanager.service.Version;
import org.springframework.stereotype.Service;

@Service("versionService")
public class VersionServiceImpl implements VersionService {

    @Override
    public String getCurrentVersion() {
	return Version.getVersion();
    }
}