package org.gs4tr.termmanager.model.dto.converter;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.gs4tr.foundation.modules.entities.model.Task;

public class TaskConverter {

    public static Task fromDtoToInternal(org.gs4tr.termmanager.model.dto.Task dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	Task internalEntity = new Task(dtoEntity.getTaskName());
	internalEntity.setSelectStyle(SelectStyleConverter.fromDtoToInternal(dtoEntity.getSelectStyle()));

	return internalEntity;
    }

    public static Task[] fromDtoToInternal(org.gs4tr.termmanager.model.dto.Task[] dtoEntities) {
	if ((dtoEntities == null) || (dtoEntities.length == 0)
		|| ((dtoEntities.length == 1) && (dtoEntities[0] == null))) {
	    return null;
	}

	Task[] internalEntities = new Task[dtoEntities.length];
	for (int i = 0; i < internalEntities.length; i++) {
	    internalEntities[i] = (fromDtoToInternal(dtoEntities[i]));
	}

	return internalEntities;
    }

    public static org.gs4tr.termmanager.model.dto.Task fromInternalToDto(Task internalEntity) {
	if (Objects.isNull(internalEntity)) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Task dtoEntity = new org.gs4tr.termmanager.model.dto.Task();
	dtoEntity.setTaskName(internalEntity.getName());

	if (internalEntity.getPriority() != null) {
	    dtoEntity.setWeight(internalEntity.getPriority().getValue());
	}

	dtoEntity.setSelectStyle(SelectStyleConverter.fromInternalToDto(internalEntity.getSelectStyle()));

	return dtoEntity;
    }

    public static org.gs4tr.termmanager.model.dto.Task[] fromInternalToDto(Task[] internalEntities) {
	if (internalEntities != null) {
	    List<org.gs4tr.termmanager.model.dto.Task> resultEntities = new ArrayList<org.gs4tr.termmanager.model.dto.Task>();
	    for (Task task : internalEntities) {
		if (task != null) {
		    resultEntities.add(fromInternalToDto(task));
		}
	    }

	    if (isEmpty(resultEntities)) {
		return null;
	    } else {
		return resultEntities.toArray(new org.gs4tr.termmanager.model.dto.Task[resultEntities.size()]);
	    }
	} else {
	    return null;
	}
    }

}
