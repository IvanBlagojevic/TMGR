package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation3.tbx.TbxDescriptionTypes;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.BaseTypeEnum;

public class AttributeConverter {

    public static Attribute fromDtoToInternal(org.gs4tr.termmanager.model.dto.Attribute dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	Attribute internalEntity = new Attribute();

	internalEntity.setName(dtoEntity.getAttributeName());
	internalEntity.setValue(dtoEntity.getAttributeValue());
	internalEntity.setTermEntryAttributeType(
		TermEntryAttributeTypeConverter.fromDtoToInternal(dtoEntity.getAttributeType()));
	internalEntity.setAttributeLevel(AttributeLevelConverter.fromDtoToInternal(dtoEntity.getAttributeLevelEnum()));
	internalEntity.setRequired(dtoEntity.getRequired());
	internalEntity.setComboValues(dtoEntity.getComboValues());
	internalEntity
		.setInputFieldTypeEnum(InputFieldTypeConverter.fromDtoToInternal(dtoEntity.getInputFieldTypeEnum()));
	internalEntity.setMultiplicity(dtoEntity.getMultiplicity());
	internalEntity.setReadOnly(dtoEntity.getReadOnly());
	internalEntity.setBaseTypeEnum(BaseTypeEnum.DESCRIPTION);
	internalEntity.setSynchronizable(dtoEntity.getSynchronizable());
	internalEntity.setRenameValue(dtoEntity.getRenameValue());

	return internalEntity;
    }

    public static List<Attribute> fromDtoToInternal(org.gs4tr.termmanager.model.dto.Attribute[] dtoEntities) {
	List<Attribute> internalEntities = new ArrayList<Attribute>();
	if ((dtoEntities == null) || (dtoEntities.length == 0)
		|| ((dtoEntities.length == 1) && (dtoEntities[0] == null))) {
	    return internalEntities;
	}

	for (int i = 0; i < dtoEntities.length; i++) {
	    internalEntities.add(fromDtoToInternal(dtoEntities[i]));
	}

	return internalEntities;
    }

    public static org.gs4tr.termmanager.model.dto.Attribute fromInternalToDto(Attribute internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Attribute dtoEntity = new org.gs4tr.termmanager.model.dto.Attribute();

	dtoEntity.setAttributeName(internalEntity.getName());
	dtoEntity.setAttributeValue(internalEntity.getValue());
	dtoEntity.setAttributeType(
		TermEntryAttributeTypeConverter.fromInternalToDto(internalEntity.getTermEntryAttributeType()));
	dtoEntity.setAttributeLevelEnum(AttributeLevelConverter.fromInternalToDto(internalEntity.getAttributeLevel()));
	dtoEntity.setRequired(internalEntity.getRequired());
	dtoEntity.setComboValues(internalEntity.getComboValues());
	dtoEntity.setInputFieldTypeEnum(
		InputFieldTypeConverter.fromInternalToDto(internalEntity.getInputFieldTypeEnum()));
	dtoEntity.setMultiplicity(internalEntity.getMultiplicity());
	dtoEntity.setReadOnly(internalEntity.getReadOnly());
	dtoEntity.setRequired(internalEntity.getRequired());
	dtoEntity.setIsCustom(!TbxDescriptionTypes.isPreDefinedAttribute(internalEntity.getName()));
	dtoEntity.setSynchronizable(internalEntity.getSynchronizable());

	return dtoEntity;
    }

    public static org.gs4tr.termmanager.model.dto.Attribute[] fromInternalToDto(List<Attribute> internalEntities) {
	if (internalEntities == null) {
	    return new org.gs4tr.termmanager.model.dto.Attribute[0];
	}

	org.gs4tr.termmanager.model.dto.Attribute[] dtoEntities = new org.gs4tr.termmanager.model.dto.Attribute[internalEntities
		.size()];
	int i = 0;
	for (Attribute attribute : internalEntities) {
	    dtoEntities[i] = fromInternalToDto(attribute);
	    i++;
	}

	return dtoEntities;
    }
}
