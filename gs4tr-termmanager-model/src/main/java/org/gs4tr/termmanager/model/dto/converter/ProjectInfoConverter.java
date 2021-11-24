package org.gs4tr.termmanager.model.dto.converter;

import org.gs4tr.foundation.modules.entities.model.ProjectInfo;

public class ProjectInfoConverter {

    public static ProjectInfo fromDtoToInternal(org.gs4tr.termmanager.model.dto.ProjectInfo dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	ProjectInfo internalEntity = new ProjectInfo();

	internalEntity.setClientIdentifier(dtoEntity.getClientIdentifier());
	internalEntity.setName(dtoEntity.getName());
	internalEntity.setShortCode(dtoEntity.getShortCode());
	internalEntity.setEnabled(dtoEntity.getEnabled());

	return internalEntity;
    }

    public static org.gs4tr.termmanager.model.dto.ProjectInfo fromInternalToDto(ProjectInfo internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.ProjectInfo dtoEntity = new org.gs4tr.termmanager.model.dto.ProjectInfo();

	dtoEntity.setClientIdentifier(internalEntity.getClientIdentifier());
	dtoEntity.setName(internalEntity.getName());
	dtoEntity.setShortCode(internalEntity.getShortCode());
	dtoEntity.setEnabled(internalEntity.isEnabled());

	return dtoEntity;
    }

}
