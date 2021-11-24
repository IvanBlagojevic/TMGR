package org.gs4tr.termmanager.model.dto.converter;

import org.gs4tr.foundation.modules.security.model.RoleTypeEnum;

public class RoleTypeEnumConverter {

    public static RoleTypeEnum fromDtoToInternal(org.gs4tr.termmanager.model.dto.RoleTypeEnum dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	switch (dtoEntity.getValue()) {
	case (0):
	    return RoleTypeEnum.CONTEXT;
	case (1):
	    return RoleTypeEnum.SYSTEM;
	}

	return null;
    }

    public static org.gs4tr.termmanager.model.dto.RoleTypeEnum fromInternalToDto(RoleTypeEnum internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	switch (internalEntity) {
	case CONTEXT:
	    return org.gs4tr.termmanager.model.dto.RoleTypeEnum.CONTEXT;
	case SYSTEM:
	    return org.gs4tr.termmanager.model.dto.RoleTypeEnum.SYSTEM;
	default:
	    throw new RuntimeException(Messages.getString("RoleTypeEnumConverter.0")); //$NON-NLS-1$
	}

    }
}
