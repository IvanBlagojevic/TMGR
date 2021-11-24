package org.gs4tr.termmanager.model.dto.converter;

import org.gs4tr.termmanager.model.TermEntryAttributeTypeEnum;
import org.gs4tr.termmanager.model.dto.TermEntryAttributeTypeDto;

public class TermEntryAttributeTypeConverter {
    public static TermEntryAttributeTypeEnum fromDtoToInternal(
	    TermEntryAttributeTypeDto dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	if (TermEntryAttributeTypeDto.MULTIMEDIA.equals(dtoEntity)) {
	    return TermEntryAttributeTypeEnum.MULTIMEDIA;
	} else if (TermEntryAttributeTypeDto.TEXT.equals(dtoEntity)) {
	    return TermEntryAttributeTypeEnum.TEXT;
	} else {
	    return null;
	}
    }

    public static TermEntryAttributeTypeDto fromInternalToDto(
	    TermEntryAttributeTypeEnum internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	if (TermEntryAttributeTypeEnum.MULTIMEDIA == internalEntity) {
	    return TermEntryAttributeTypeDto.MULTIMEDIA;
	} else if (TermEntryAttributeTypeEnum.TEXT == internalEntity) {
	    return TermEntryAttributeTypeDto.TEXT;
	} else {
	    return null;
	}
    }
}
