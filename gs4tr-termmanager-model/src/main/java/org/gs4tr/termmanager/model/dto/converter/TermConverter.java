package org.gs4tr.termmanager.model.dto.converter;

import java.util.HashSet;
import java.util.Set;

import org.gs4tr.termmanager.model.glossary.Term;

public class TermConverter {

    public static Term fromDtoToInternal(org.gs4tr.termmanager.model.dto.Term dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	Term internalEntity = new Term();

	internalEntity.setName(dtoEntity.getName());

	if (dtoEntity.getTicket() != null) {
	    internalEntity.setUuId(TicketConverter.fromDtoToInternal(dtoEntity.getTicket(), String.class));
	}
	internalEntity.setDescriptions(TermDescriptionConverter.fromDtoToInternal(dtoEntity.getTermDescriptions()));

	return internalEntity;

    }

    public static Set<Term> fromDtoToInternal(org.gs4tr.termmanager.model.dto.Term[] dtoEntities) {
	if ((dtoEntities == null) || (dtoEntities.length == 0)
		|| ((dtoEntities.length == 1) && (dtoEntities[0] == null))) {
	    return null;
	}

	Set<Term> internalEntities = new HashSet<Term>();
	for (int i = 0; i < dtoEntities.length; i++) {
	    internalEntities.add(fromDtoToInternal(dtoEntities[i]));
	}

	return internalEntities;
    }

    public static org.gs4tr.termmanager.model.dto.Term[] fromInternalToDto(Set<Term> internalEntities) {
	if (internalEntities == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Term[] dtoEntities = new org.gs4tr.termmanager.model.dto.Term[internalEntities
		.size()];
	int i = 0;
	for (Term term : internalEntities) {
	    dtoEntities[i] = fromInternalToDto(term);
	    i++;
	}

	return dtoEntities;
    }

    public static org.gs4tr.termmanager.model.dto.Term fromInternalToDto(Term internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Term dtoEntity = new org.gs4tr.termmanager.model.dto.Term();

	dtoEntity.setName(internalEntity.getName());
	dtoEntity.setTicket(TicketConverter.fromInternalToDto(internalEntity.getUuId()));
	dtoEntity.setBlacklisted(internalEntity.isForbidden());
	dtoEntity.setMarkerId(internalEntity.getUuId());

	return dtoEntity;
    }
}