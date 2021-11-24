package org.gs4tr.termmanager.model.dto.converter;

import org.gs4tr.foundation.modules.entities.model.types.EntityType;
import org.gs4tr.termmanager.model.EntityTypeHolder;

public class EntityTypeEnumConverter {

    public static EntityType fromDtoToInternal(org.gs4tr.termmanager.model.dto.EntityTypeEnum dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	switch (dtoEntity.getValue()) {
	case (0):
	    return EntityTypeHolder.ORGANIZATION;
	case (1):
	    return EntityTypeHolder.PROJECT;
	case (2):
	    return EntityTypeHolder.ROLE;
	case (3):
	    return EntityTypeHolder.TERM;
	case (4):
	    return EntityTypeHolder.TERMENTRY;
	case (5):
	    return EntityTypeHolder.USER;
	case (6):
	    return EntityTypeHolder.TRANSLATION;
	}

	return null;
    }

    public static org.gs4tr.termmanager.model.dto.EntityTypeEnum fromInternalToDto(EntityType internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	if (internalEntity.equals(EntityTypeHolder.ORGANIZATION)) {
	    return org.gs4tr.termmanager.model.dto.EntityTypeEnum.ORGANIZATION;
	} else if (internalEntity.equals(EntityTypeHolder.PROJECT)) {
	    return org.gs4tr.termmanager.model.dto.EntityTypeEnum.PROJECT;
	} else if (internalEntity.equals(EntityTypeHolder.ROLE)) {
	    return org.gs4tr.termmanager.model.dto.EntityTypeEnum.ROLE;
	} else if (internalEntity.equals(EntityTypeHolder.TERM)) {
	    return org.gs4tr.termmanager.model.dto.EntityTypeEnum.TERM;
	} else if (internalEntity.equals(EntityTypeHolder.TERMENTRY)) {
	    return org.gs4tr.termmanager.model.dto.EntityTypeEnum.TERMENTRY;
	} else if (internalEntity.equals(EntityTypeHolder.USER)) {
	    return org.gs4tr.termmanager.model.dto.EntityTypeEnum.USER;
	}

	return null;
    }

}