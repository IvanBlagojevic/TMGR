package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.Metadata;

public class MetadataConverter {

    public static Metadata fromDtoToInternal(org.gs4tr.termmanager.model.dto.Metadata dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	return new Metadata(dtoEntity.getKey(), dtoEntity.getValue());
    }

    public static List<Metadata> fromDtoToInternal(org.gs4tr.termmanager.model.dto.Metadata[] dtoEntities) {
	if ((dtoEntities == null) || (dtoEntities.length == 0)
		|| ((dtoEntities.length == 1) && (dtoEntities[0] == null))) {
	    return null;
	}

	List<Metadata> internalEntities = new ArrayList<Metadata>();
	for (int i = 0; i < dtoEntities.length; i++) {
	    internalEntities.add(fromDtoToInternal(dtoEntities[i]));
	}

	return internalEntities;
    }

    public static Map<String, Object> fromDtoToInternal2(org.gs4tr.termmanager.model.dto.Metadata[] dtoEntities) {
	if ((dtoEntities == null) || (dtoEntities.length == 0)
		|| ((dtoEntities.length == 1) && (dtoEntities[0] == null))) {
	    return null;
	}

	Map<String, Object> internalEntities = new HashMap<String, Object>();
	for (org.gs4tr.termmanager.model.dto.Metadata dtoMetadata : dtoEntities) {
	    if (dtoMetadata == null) {
		continue;
	    }

	    internalEntities.put(dtoMetadata.getKey(), dtoMetadata.getValue());
	}

	return internalEntities;
    }

    public static org.gs4tr.termmanager.model.dto.Metadata[] fromInternalToDto(List<Metadata> internalEntities) {
	if (internalEntities == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Metadata[] dtoEntities = new org.gs4tr.termmanager.model.dto.Metadata[internalEntities
		.size()];
	for (int i = 0; i < dtoEntities.length; i++) {
	    dtoEntities[i] = fromInternalToDto(internalEntities.get(i));
	}

	return dtoEntities;
    }

    public static org.gs4tr.termmanager.model.dto.Metadata[] fromInternalToDto(Map<String, Object> internalEntities) {
	if (internalEntities == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Metadata[] dtoEntities = new org.gs4tr.termmanager.model.dto.Metadata[internalEntities
		.size()];

	int counter = 0;
	for (Map.Entry<String, Object> internalEntityEntry : internalEntities.entrySet()) {
	    dtoEntities[counter++] = fromInternalToDto(internalEntityEntry);
	}

	return dtoEntities;
    }

    public static org.gs4tr.termmanager.model.dto.Metadata fromInternalToDto(Metadata internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Metadata dtoEntity = new org.gs4tr.termmanager.model.dto.Metadata();

	dtoEntity.setKey(internalEntity.getKey());
	dtoEntity.setValue(internalEntity.getValue());

	return dtoEntity;
    }

    private static org.gs4tr.termmanager.model.dto.Metadata fromInternalToDto(
	    Map.Entry<String, Object> internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Metadata dtoEntity = new org.gs4tr.termmanager.model.dto.Metadata();

	dtoEntity.setKey(internalEntity.getKey());
	dtoEntity.setValue(internalEntity.getValue().toString());

	return dtoEntity;
    }

}
