package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gs4tr.termmanager.model.glossary.Description;

public class TermDescriptionConverter {

    public static Description fromDtoToInternal(org.gs4tr.termmanager.model.dto.Description dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	Description internalEntity = new Description();

	internalEntity.setType(dtoEntity.getType());
	internalEntity.setValue(dtoEntity.getValue());

	return internalEntity;

    }

    public static Set<Description> fromDtoToInternal(org.gs4tr.termmanager.model.dto.Description[] dtoEntities) {
	if ((dtoEntities == null) || (dtoEntities.length == 0)
		|| ((dtoEntities.length == 1) && (dtoEntities[0] == null))) {
	    return null;
	}

	Set<Description> internalEntities = new HashSet<Description>();
	for (int i = 0; i < dtoEntities.length; i++) {
	    internalEntities.add(fromDtoToInternal(dtoEntities[i]));
	}

	return internalEntities;
    }

    public static org.gs4tr.termmanager.model.dto.Description fromInternalToDto(Description internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Description dtoEntity = new org.gs4tr.termmanager.model.dto.Description();

	dtoEntity.setType(internalEntity.getType());
	dtoEntity.setValue(internalEntity.getValue());
	dtoEntity.setBaseType(internalEntity.getBaseType().toLowerCase());

	return dtoEntity;
    }

    public static org.gs4tr.termmanager.model.dto.Description[] fromInternalToDto(Set<Description> internalEntities) {
	if (internalEntities == null) {
	    return new org.gs4tr.termmanager.model.dto.Description[0];
	}

	org.gs4tr.termmanager.model.dto.Description[] dtoEntities = new org.gs4tr.termmanager.model.dto.Description[internalEntities
		.size()];
	int i = 0;
	for (Description termDescription : internalEntities) {
	    dtoEntities[i] = fromInternalToDto(termDescription);
	    i++;
	}

	return dtoEntities;
    }

    public static List<org.gs4tr.termmanager.model.dto.Description> fromInternalToDtoList(
	    Set<Description> internalEntities) {
	List<org.gs4tr.termmanager.model.dto.Description> dtoEntities = new ArrayList<org.gs4tr.termmanager.model.dto.Description>();

	if (internalEntities == null) {
	    return dtoEntities;
	}

	for (Description termDescription : internalEntities) {
	    dtoEntities.add(fromInternalToDto(termDescription));
	}

	return dtoEntities;
    }

}