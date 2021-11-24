package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.StringConstants;
import org.gs4tr.termmanager.model.glossary.Description;

public class DescriptionConverter {

    public static Collection<org.gs4tr.termmanager.model.dto.Description> convertDescriptionsToDto(
	    Set<Description> desc) {
	List<org.gs4tr.termmanager.model.dto.Description> descriptions = new ArrayList<>();
	if (CollectionUtils.isNotEmpty(desc)) {
	    for (Description description : desc) {
		org.gs4tr.termmanager.model.dto.Description dtoDesc = new org.gs4tr.termmanager.model.dto.Description();
		dtoDesc.setBaseType(description.getBaseType());
		dtoDesc.setType(description.getType());
		dtoDesc.setValue(description.getValue());
		dtoDesc.setMarkerId(description.getUuid());
		descriptions.add(dtoDesc);
	    }
	}
	return descriptions;
    }

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

    public static List<Description> fromDtoToInternal(org.gs4tr.termmanager.model.dto.Description[] dtoEntities) {
	if ((dtoEntities == null) || (dtoEntities.length == 0)
		|| ((dtoEntities.length == 1) && (dtoEntities[0] == null))) {
	    return null;
	}

	List<Description> internalEntities = new ArrayList<Description>();
	for (int i = 0; i < dtoEntities.length; i++) {

	    org.gs4tr.termmanager.model.dto.Description dtoEntity = dtoEntities[i];
	    if (dtoEntity.getValue() != null && dtoEntity.getValue().contains(StringConstants.PIPE)) {
		org.gs4tr.termmanager.model.dto.Description[] splitedDescriptions = splitDescription(dtoEntity);
		internalEntities.addAll(fromDtoToInternal(splitedDescriptions));

	    } else {
		internalEntities.add(fromDtoToInternal(dtoEntity));
	    }
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
	dtoEntity.setMarkerId(internalEntity.getUuid());

	return dtoEntity;
    }

    public static org.gs4tr.termmanager.model.dto.Description[] fromInternalToDto(
	    Set<? extends Description> internalEntities) {
	if (internalEntities == null) {
	    return new org.gs4tr.termmanager.model.dto.Description[0];
	}

	org.gs4tr.termmanager.model.dto.Description[] dtoEntities = new org.gs4tr.termmanager.model.dto.Description[internalEntities
		.size()];
	int i = 0;
	for (Description description : internalEntities) {
	    dtoEntities[i] = fromInternalToDto(description);
	    i++;
	}

	return dtoEntities;
    }

    public static org.gs4tr.termmanager.model.dto.ExtendedDescription fromInternalToDtoExtended(
	    Description internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.ExtendedDescription dtoEntity = new org.gs4tr.termmanager.model.dto.ExtendedDescription();

	dtoEntity.setType(internalEntity.getType());
	dtoEntity.setValue(internalEntity.getValue());
	dtoEntity.setBaseType(internalEntity.getBaseType());
	dtoEntity.setMarkerId(internalEntity.getUuid());

	return dtoEntity;
    }

    public static org.gs4tr.termmanager.model.dto.ExtendedDescription fromInternalToDtoExtended(
	    Description internalEntity, boolean commited) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.ExtendedDescription dtoEntity = new org.gs4tr.termmanager.model.dto.ExtendedDescription();

	dtoEntity.setType(internalEntity.getType());
	dtoEntity.setValue(internalEntity.getValue());
	dtoEntity.setBaseType(internalEntity.getBaseType());
	dtoEntity.setMarkerId(internalEntity.getUuid());
	dtoEntity.setCommited(commited);

	return dtoEntity;
    }

    public static org.gs4tr.termmanager.model.dto.ExtendedDescription[] fromInternalToDtoExtended(
	    Set<? extends Description> internalEntities) {
	if (internalEntities == null) {
	    return new org.gs4tr.termmanager.model.dto.ExtendedDescription[0];
	}

	org.gs4tr.termmanager.model.dto.ExtendedDescription[] dtoEntities = new org.gs4tr.termmanager.model.dto.ExtendedDescription[internalEntities
		.size()];
	int i = 0;
	for (Description description : internalEntities) {
	    dtoEntities[i] = fromInternalToDtoExtended(description);
	    i++;
	}

	return dtoEntities;
    }

    public static org.gs4tr.termmanager.model.dto.ExtendedDescription[] fromInternalToDtoExtended(
	    Set<? extends Description> internalEntities, boolean commited) {
	if (internalEntities == null) {
	    return new org.gs4tr.termmanager.model.dto.ExtendedDescription[0];
	}

	org.gs4tr.termmanager.model.dto.ExtendedDescription[] dtoEntities = new org.gs4tr.termmanager.model.dto.ExtendedDescription[internalEntities
		.size()];
	int i = 0;
	for (Description description : internalEntities) {
	    dtoEntities[i] = fromInternalToDtoExtended(description, commited);
	    i++;
	}

	return dtoEntities;
    }

    private static org.gs4tr.termmanager.model.dto.Description[] splitDescription(
	    org.gs4tr.termmanager.model.dto.Description dtoEntity) {
	String type = dtoEntity.getType();
	String value = dtoEntity.getValue();
	String[] splitedValues = value.split("\\" + StringConstants.PIPE);
	org.gs4tr.termmanager.model.dto.Description[] descriptions = new org.gs4tr.termmanager.model.dto.Description[splitedValues.length];

	for (int i = 0; i < splitedValues.length; i++) {
	    String splitedValue = splitedValues[i];
	    org.gs4tr.termmanager.model.dto.Description description = new org.gs4tr.termmanager.model.dto.Description();
	    description.setType(type);
	    description.setValue(splitedValue);
	    descriptions[i] = description;
	}
	return descriptions;
    }
}