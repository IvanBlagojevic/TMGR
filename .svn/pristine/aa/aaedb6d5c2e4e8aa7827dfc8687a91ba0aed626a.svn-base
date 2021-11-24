package org.gs4tr.termmanager.service.manualtask;

import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.types.EntityType;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.springframework.beans.factory.annotation.Required;

public class DefaultSystemManualTaskManager implements ManualTaskManager<SystemManualTaskHandler> {
    private Map<EntityType, Map<String, SystemManualTaskHandler>> _manualTaskHandlerMap;

    @Override
    public Map<EntityType, Map<String, SystemManualTaskHandler>> getManualTaskHandlerMap() {
	return _manualTaskHandlerMap;
    }

    @Override
    public SystemManualTaskHandler getUserTaskHandler(String taskName) {

	SystemManualTaskHandler manualTaskHandler = null;

	for (EntityType entityType : EntityTypeHolder.values()) {
	    Map<String, SystemManualTaskHandler> manualTaskHandlerMap = getManualTaskHandlerMap().get(entityType);

	    if (manualTaskHandlerMap != null) {
		manualTaskHandler = manualTaskHandlerMap.get(taskName);
	    }

	    if (manualTaskHandler != null) {
		break;
	    }
	}

	return manualTaskHandler;
    }

    @Required
    public void setManualTaskHandlerMap(Map<EntityType, Map<String, SystemManualTaskHandler>> userTaskHandlerMap) {
	_manualTaskHandlerMap = userTaskHandlerMap;
    }

}