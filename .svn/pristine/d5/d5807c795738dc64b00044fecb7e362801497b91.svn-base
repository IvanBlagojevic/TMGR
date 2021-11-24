package org.gs4tr.termmanager.service.manualtask;

import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.types.EntityType;

public interface ManualTaskManager<T extends ManualTaskHandler> {
    Map<EntityType, Map<String, T>> getManualTaskHandlerMap();

    T getUserTaskHandler(String taskName);
}
