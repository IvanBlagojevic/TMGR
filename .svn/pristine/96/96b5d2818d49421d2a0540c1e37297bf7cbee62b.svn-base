package org.gs4tr.termmanager.model.dto.converter;

import org.gs4tr.termmanager.model.glossary.TermEntry;

public class TermEntryConverter {

    public static TermEntry fromDtoToInternal(org.gs4tr.termmanager.model.dto.TermEntry dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	TermEntry internalEntity = new TermEntry();

	if (dtoEntity.getTicket() != null) {
	    internalEntity.setUuId(dtoEntity.getTicket());
	}

	internalEntity.setDescriptions(TermEntryDescriptionConverter.fromDtoToInternal(dtoEntity.getDescriptions()));

	return internalEntity;

    }

    public static org.gs4tr.termmanager.model.dto.TermEntry fromInternalToDto(TermEntry internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.TermEntry dtoEntity = new org.gs4tr.termmanager.model.dto.TermEntry();

	dtoEntity.setDescriptions(TermEntryDescriptionConverter.fromInternalToDto(internalEntity.getDescriptions()));

	dtoEntity.setTicket(TicketConverter.fromInternalToDto(internalEntity.getUuId()));

	return dtoEntity;
    }

}