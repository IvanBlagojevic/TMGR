package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gs4tr.termmanager.model.glossary.Description;

public class TermEntryDescriptionConverter {

    public static Description fromDtoToInternal(org.gs4tr.termmanager.model.dto.Description dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	Description internalEntity = new Description();

	internalEntity.setType(dtoEntity.getType());
	internalEntity.setValue(dtoEntity.getValue());
	internalEntity.setUuid(dtoEntity.getMarkerId());

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
	dtoEntity.setBaseType(internalEntity.getType());
	dtoEntity.setMarkerId(internalEntity.getUuid());

	return dtoEntity;
    }

    public static org.gs4tr.termmanager.model.dto.Description[] fromInternalToDto(Set<Description> internalEntities) {
	if (internalEntities == null) {
	    return null;
	}

	List<Description> sortedDescriptions = new ArrayList<>(internalEntities);

	sortedDescriptions.sort(new Comparator<Description>() {

	    @Override
	    public int compare(Description o1, Description o2) {
		return o1.getType().compareTo(o2.getType());
	    }
	});

	org.gs4tr.termmanager.model.dto.Description[] dtoEntities = new org.gs4tr.termmanager.model.dto.Description[sortedDescriptions
		.size()];
	int i = 0;
	for (Description termEntryDescription : sortedDescriptions) {
	    dtoEntities[i] = fromInternalToDto(termEntryDescription);
	    i++;
	}

	return dtoEntities;
    }
}