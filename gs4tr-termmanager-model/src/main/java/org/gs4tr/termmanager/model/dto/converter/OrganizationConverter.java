package org.gs4tr.termmanager.model.dto.converter;

import org.gs4tr.termmanager.model.TmOrganization;

public class OrganizationConverter {

    public static TmOrganization fromDtoToInternal(org.gs4tr.termmanager.model.dto.Organization dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	TmOrganization internalEntity = new TmOrganization();

	internalEntity
		.setOrganizationInfo(OrganizationInfoConverter.fromDtoToInternal(dtoEntity.getOrganizationInfo()));
	internalEntity
		.setParentOrganization(OrganizationConverter.fromDtoToInternal(dtoEntity.getParentOrganization()));

	if (dtoEntity.getTicket() != null) {
	    internalEntity.setIdentifier(TicketConverter.fromDtoToInternal(dtoEntity.getTicket(), Long.class));
	}

	return internalEntity;
    }

    public static org.gs4tr.termmanager.model.dto.Organization fromInternalToDto(TmOrganization internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Organization dtoEntity = new org.gs4tr.termmanager.model.dto.Organization();

	dtoEntity
		.setOrganizationInfo(OrganizationInfoConverter.fromInternalToDto(internalEntity.getOrganizationInfo()));
	dtoEntity
		.setParentOrganization(OrganizationConverter.fromInternalToDto(internalEntity.getParentOrganization()));
	dtoEntity.setTicket(TicketConverter.fromInternalToDto(internalEntity.getOrganizationId()));

	return dtoEntity;
    }

}
