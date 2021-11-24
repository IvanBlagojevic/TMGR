package org.gs4tr.termmanager.model.dto.converter;

import org.gs4tr.termmanager.model.TermSearchRequest;

public class TermSearchRequestConverter {

    public static TermSearchRequest fromDtoToInternal(org.gs4tr.termmanager.model.dto.TermSearchRequest dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	TermSearchRequest internalEntity = new TermSearchRequest();

	internalEntity.setTerm(dtoEntity.getTerm());

	internalEntity.setSource(dtoEntity.getSource());

	internalEntity.setTarget(dtoEntity.getTarget());
	if (dtoEntity.getProjectTicket() != null) {
	    internalEntity.setProjectId(TicketConverter.fromDtoToInternal(dtoEntity.getProjectTicket(), Long.class));
	}
	internalEntity.setDateCreatedFrom(DateConverter.fromDtoToInternal(dtoEntity.getDateCreatedFrom()));

	internalEntity.setDateCreatedTo(DateConverter.fromDtoToInternal(dtoEntity.getDateCreatedTo()));

	return internalEntity;
    }

    public static org.gs4tr.termmanager.model.dto.TermSearchRequest fromInternalToDto(
	    TermSearchRequest internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.TermSearchRequest dtoEntity = new org.gs4tr.termmanager.model.dto.TermSearchRequest();

	dtoEntity.setTerm(internalEntity.getTerm());
	dtoEntity.setSource(internalEntity.getSource());
	dtoEntity.setTarget(internalEntity.getTarget());
	dtoEntity.setProjectTicket(TicketConverter.fromInternalToDto(internalEntity.getProjectId()));

	dtoEntity.setDateCreatedFrom(DateConverter.fromInternalToDto(internalEntity.getDateCreatedFrom()));
	dtoEntity.setDateCreatedTo(DateConverter.fromInternalToDto(internalEntity.getDateCreatedTo()));

	return dtoEntity;
    }

}