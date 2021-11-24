package org.gs4tr.termmanager.service.impl;

import org.gs4tr.termmanager.service.PluginsService;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.manualtask.ManualTaskManager;
import org.gs4tr.termmanager.service.manualtask.SystemManualTaskHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service("pluginsService")
public class PluginsServiceImpl implements PluginsService, ApplicationContextAware {
    private ApplicationContext _applicationContext;

    @Autowired
    @Qualifier("systemManualTaskManager")
    private ManualTaskManager<SystemManualTaskHandler> _systemManualTaskManager;

    @Autowired
    @Qualifier("translationManualTaskManager")
    private ManualTaskManager<SystemManualTaskHandler> _translationManualTaskManager;

    public ApplicationContext getApplicationContext() {
	return _applicationContext;
    }

    public ManualTaskManager<SystemManualTaskHandler> getSystemManualTaskManager() {
	return _systemManualTaskManager;
    }

    public ManualTaskManager<SystemManualTaskHandler> getTranslationManualTaskManager() {
	return _translationManualTaskManager;
    }

    @Override
    public ManualTaskHandler getUserTaskHandler(String taskName) {

	if (taskName.contains(".")) {
	    taskName = taskName.substring(0, taskName.indexOf("."));
	}

	ManualTaskHandler userTaskHandler = getSystemManualTaskManager().getUserTaskHandler(taskName);

	if (userTaskHandler == null) {
	    userTaskHandler = getTranslationManualTaskManager().getUserTaskHandler(taskName);
	}

	return userTaskHandler;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
	_applicationContext = applicationContext;
    }

    public void setSystemManualTaskManager(ManualTaskManager<SystemManualTaskHandler> systemManualTaskManager) {
	_systemManualTaskManager = systemManualTaskManager;
    }

    public void setTranslationManualTaskManager(
	    ManualTaskManager<SystemManualTaskHandler> translationManualTaskManager) {
	_translationManualTaskManager = translationManualTaskManager;
    }
}
