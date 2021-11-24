package org.gs4tr.termmanager.model.dto.converter;

import java.util.Date;

public class DateConverter {

    public static Date fromDtoToInternal(org.gs4tr.termmanager.model.dto.Date dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	return new Date(dtoEntity.getDate());
    }

    public static org.gs4tr.termmanager.model.dto.Date fromInternalToDto(Date internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	return new org.gs4tr.termmanager.model.dto.Date(internalEntity.getTime());
    }

}
