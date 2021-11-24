package org.gs4tr.termmanager.service.utils;

import org.gs4tr.termmanager.service.SetupService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class SetupServiceInitializingBean implements InitializingBean {

    @Autowired
    private SetupService _setupService;

    @Override
    public void afterPropertiesSet() {
	getSetupService().setup();
    }

    public SetupService getSetupService() {
	return _setupService;
    }

}
