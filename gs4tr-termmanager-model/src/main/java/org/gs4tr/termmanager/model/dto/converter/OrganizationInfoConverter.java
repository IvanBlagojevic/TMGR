package org.gs4tr.termmanager.model.dto.converter;

import org.gs4tr.foundation.modules.entities.model.OrganizationInfo;

public class OrganizationInfoConverter {

    public static OrganizationInfo fromDtoToInternal(org.gs4tr.termmanager.model.dto.OrganizationInfo dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	OrganizationInfo internalEntity = new OrganizationInfo();

	internalEntity.setName(dtoEntity.getName());
	internalEntity.setCurrencyCode(dtoEntity.getCurrencyCode());
	internalEntity.setDomain(dtoEntity.getDomain());
	internalEntity.setTheme(dtoEntity.getTheme());

	return internalEntity;
    }

    public static org.gs4tr.termmanager.model.dto.OrganizationInfo fromInternalToDto(OrganizationInfo internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.OrganizationInfo dtoEntity = new org.gs4tr.termmanager.model.dto.OrganizationInfo();

	dtoEntity.setName(internalEntity.getName());
	dtoEntity.setCurrencyCode(internalEntity.getCurrencyCode());
	dtoEntity.setDomain(internalEntity.getDomain());
	dtoEntity.setTheme(internalEntity.getTheme());
	dtoEntity.setEnabled(internalEntity.isEnabled());

	return dtoEntity;
    }

}
