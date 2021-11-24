package org.gs4tr.termmanager.model.dto.converter;

import java.util.HashSet;
import java.util.Set;

import org.gs4tr.foundation.modules.security.model.Role;

public class RoleConverter {

    public static Role fromDtoToInternal(org.gs4tr.termmanager.model.dto.Role dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	Role internalEntity = new Role();

	internalEntity.setPolicies(PolicyConverter.fromDtoToInternal(dtoEntity.getPolicies()));
	internalEntity.setRoleId(dtoEntity.getRoleId());
	internalEntity.setRoleType(RoleTypeEnumConverter.fromDtoToInternal(dtoEntity.getRoleType()));
	if (dtoEntity.getTicket() != null) {
	    internalEntity.setIdentifier(TicketConverter.fromDtoToInternal(dtoEntity.getTicket(), String.class));
	}

	return internalEntity;
    }

    public static Set<Role> fromDtoToInternal(org.gs4tr.termmanager.model.dto.Role[] dtoEntities) {
	if ((dtoEntities == null) || (dtoEntities.length == 0)
		|| ((dtoEntities.length == 1) && (dtoEntities[0] == null))) {
	    return null;
	}

	Set<Role> internalEntities = new HashSet<Role>();
	for (int i = 0; i < dtoEntities.length; i++) {
	    internalEntities.add(fromDtoToInternal(dtoEntities[i]));
	}

	return internalEntities;
    }

    public static org.gs4tr.termmanager.model.dto.Role fromInternalToDto(Role internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Role dtoEntity = new org.gs4tr.termmanager.model.dto.Role();

	dtoEntity.setPolicies(PolicyConverter.fromInternalToDto(internalEntity.getPolicies()));
	dtoEntity.setRoleId(internalEntity.getRoleId());
	dtoEntity.setTicket(TicketConverter.fromInternalToDto(internalEntity.getRoleId()));
	dtoEntity.setRoleType(RoleTypeEnumConverter.fromInternalToDto(internalEntity.getRoleType()));

	return dtoEntity;
    }

    public static org.gs4tr.termmanager.model.dto.Role[] fromInternalToDto(Set<Role> internalEntities) {
	if (internalEntities == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Role[] dtoEntities = new org.gs4tr.termmanager.model.dto.Role[internalEntities
		.size()];
	int i = 0;
	for (Role role : internalEntities) {
	    dtoEntities[i] = fromInternalToDto(role);
	    i++;
	}

	return dtoEntities;
    }
}
