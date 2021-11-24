package org.gs4tr.termmanager.model.dto.converter;

import java.util.HashSet;
import java.util.Set;

import org.gs4tr.foundation.modules.security.model.Policy;

public class PolicyConverter {

    public static Policy fromDtoToInternal(org.gs4tr.termmanager.model.dto.Policy dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	Policy internalEntity = new Policy();

	internalEntity.setCategory(dtoEntity.getCategory());
	internalEntity.setPolicyId(dtoEntity.getPolicyId());
	internalEntity.setPolicyType(RoleTypeEnumConverter.fromDtoToInternal(dtoEntity.getPolicyType()));

	return internalEntity;
    }

    public static Set<Policy> fromDtoToInternal(org.gs4tr.termmanager.model.dto.Policy[] dtoEntities) {
	if ((dtoEntities == null) || (dtoEntities.length == 0)
		|| ((dtoEntities.length == 1) && (dtoEntities[0] == null))) {
	    return null;
	}

	Set<Policy> internalEntities = new HashSet<Policy>();
	for (int i = 0; i < dtoEntities.length; i++) {
	    internalEntities.add(fromDtoToInternal(dtoEntities[i]));
	}

	return internalEntities;
    }

    public static org.gs4tr.termmanager.model.dto.Policy fromInternalToDto(Policy internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Policy dtoEntity = new org.gs4tr.termmanager.model.dto.Policy();

	dtoEntity.setCategory(internalEntity.getCategory());
	dtoEntity.setPolicyId(internalEntity.getPolicyId());
	dtoEntity.setPolicyType(RoleTypeEnumConverter.fromInternalToDto(internalEntity.getPolicyType()));

	return dtoEntity;
    }

    public static org.gs4tr.termmanager.model.dto.Policy[] fromInternalToDto(Set<Policy> internalEntities) {
	if (internalEntities == null) {
	    return null;
	}

	Policy[] internalEntitiesArray = internalEntities.toArray(new Policy[internalEntities.size()]);
	org.gs4tr.termmanager.model.dto.Policy[] dtoEntities = new org.gs4tr.termmanager.model.dto.Policy[internalEntities
		.size()];
	for (int i = 0; i < dtoEntities.length; i++) {
	    dtoEntities[i] = fromInternalToDto(internalEntitiesArray[i]);
	}

	return dtoEntities;
    }
}
