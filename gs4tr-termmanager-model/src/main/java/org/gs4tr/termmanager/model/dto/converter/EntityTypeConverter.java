package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.types.EntityType;
import org.gs4tr.termmanager.model.EntityTypeHolder;

public class EntityTypeConverter {

    public static List<EntityType> fromDtoToInternal(List<String> dtoEntities) {
	List<EntityType> internalValues = new ArrayList<EntityType>();
	if (CollectionUtils.isNotEmpty(dtoEntities)) {
	    for (String entity : dtoEntities) {
		internalValues.add(fromDtoToInternal(entity));
	    }
	}

	return internalValues;
    }

    public static EntityType fromDtoToInternal(String dtoEntity) {
	if (dtoEntity == null) {
	    return EntityTypeHolder.TERM;
	}

	return EntityTypeHolder.valueOf(dtoEntity);
    }
}