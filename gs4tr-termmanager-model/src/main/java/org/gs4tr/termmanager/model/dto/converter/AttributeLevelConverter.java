package org.gs4tr.termmanager.model.dto.converter;

import org.gs4tr.termmanager.model.AttributeLevelEnum;

public class AttributeLevelConverter {
    public static AttributeLevelEnum fromDtoToInternal(org.gs4tr.termmanager.model.dto.AttributeLevelEnum dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	if (org.gs4tr.termmanager.model.dto.AttributeLevelEnum.TERMENTRY.equals(dtoEntity)) {
	    return AttributeLevelEnum.TERMENTRY;
	} else if (org.gs4tr.termmanager.model.dto.AttributeLevelEnum.LANGUAGE.equals(dtoEntity)) {
	    return AttributeLevelEnum.LANGUAGE;
	} else {
	    return null;
	}
    }

    public static org.gs4tr.termmanager.model.dto.AttributeLevelEnum fromInternalToDto(
	    AttributeLevelEnum internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	if (AttributeLevelEnum.TERMENTRY == internalEntity) {
	    return org.gs4tr.termmanager.model.dto.AttributeLevelEnum.TERMENTRY;
	} else if (AttributeLevelEnum.LANGUAGE == internalEntity) {
	    return org.gs4tr.termmanager.model.dto.AttributeLevelEnum.LANGUAGE;
	} else {
	    return null;
	}
    }
}
