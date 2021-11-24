package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.termmanager.model.UpdateCommand;

public class UpdateCommandConverter {
    public static UpdateCommand fromDtoToInternal(org.gs4tr.termmanager.model.dto.UpdateCommand dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	UpdateCommand internalEntity = new UpdateCommand();
	internalEntity.setCommand(dtoEntity.getCommand());
	internalEntity.setItemType(dtoEntity.getType());
	internalEntity.setMarkerId(dtoEntity.getMarkerId());
	internalEntity.setParentMarkerId(dtoEntity.getParentMarkerId());
	internalEntity.setSubType(dtoEntity.getSubType());
	internalEntity.setValue(dtoEntity.getValue());
	internalEntity.setAsssignee(dtoEntity.getAssignee());
	internalEntity.setLanguageId(dtoEntity.getLanguageId());
	internalEntity.setStatus(dtoEntity.getStatus());

	return internalEntity;
    }

    public static List<UpdateCommand> fromDtoToInternal(org.gs4tr.termmanager.model.dto.UpdateCommand[] dtoEntities) {
	if (dtoEntities == null) {
	    return null;
	}

	List<UpdateCommand> internalEntities = new ArrayList<UpdateCommand>();
	for (org.gs4tr.termmanager.model.dto.UpdateCommand entity : dtoEntities) {
	    if (entity != null) {
		internalEntities.add(fromDtoToInternal(entity));
	    }
	}

	return internalEntities.size() > 0 ? internalEntities : null;
    }

    public static org.gs4tr.termmanager.model.dto.UpdateCommand[] fromInternalToDto(
	    List<UpdateCommand> internalEntities) {
	if (internalEntities == null) {
	    return new org.gs4tr.termmanager.model.dto.UpdateCommand[0];
	}

	List<org.gs4tr.termmanager.model.dto.UpdateCommand> dtoEntities = new ArrayList<org.gs4tr.termmanager.model.dto.UpdateCommand>();
	for (UpdateCommand command : internalEntities) {
	    dtoEntities.add(fromInternalToDto(command));
	}

	return dtoEntities.toArray(new org.gs4tr.termmanager.model.dto.UpdateCommand[dtoEntities.size()]);
    }

    public static org.gs4tr.termmanager.model.dto.UpdateCommand fromInternalToDto(UpdateCommand internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.UpdateCommand dtoEntity = new org.gs4tr.termmanager.model.dto.UpdateCommand();
	dtoEntity.setCommand(internalEntity.getCommandEnum().getName());
	dtoEntity.setType(internalEntity.getTypeEnum().getName());
	dtoEntity.setMarkerId(internalEntity.getMarkerId());
	dtoEntity.setParentMarkerId(internalEntity.getParentMarkerId());
	dtoEntity.setSubType(internalEntity.getSubType());
	dtoEntity.setValue(internalEntity.getValue());
	dtoEntity.setAssignee(internalEntity.getAsssignee());
	dtoEntity.setLanguageId(internalEntity.getLanguageId());
	dtoEntity.setStatus(internalEntity.getStatus());

	return dtoEntity;
    }

}
