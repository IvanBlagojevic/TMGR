package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation3.tbx.TbxNoteTypes;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.BaseTypeEnum;

public class ProjectNoteConverter {

    public static Attribute fromDtoToInternal(org.gs4tr.termmanager.model.dto.ProjectNote dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	Attribute internalEntity = new Attribute();

	internalEntity.setName(dtoEntity.getNoteName());
	internalEntity.setRequired(dtoEntity.getRequired());
	internalEntity.setComboValues(dtoEntity.getComboValues());
	internalEntity
		.setInputFieldTypeEnum(InputFieldTypeConverter.fromDtoToInternal(dtoEntity.getInputFieldTypeEnum()));
	internalEntity.setMultiplicity(dtoEntity.getMultiplicity());
	internalEntity.setReadOnly(dtoEntity.getReadOnly());
	internalEntity.setBaseTypeEnum(BaseTypeEnum.NOTE);
	internalEntity.setRenameValue(dtoEntity.getRenameValue());

	return internalEntity;
    }

    public static List<Attribute> fromDtoToInternal(org.gs4tr.termmanager.model.dto.ProjectNote[] dtoEntities) {
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

    public static org.gs4tr.termmanager.model.dto.ProjectNote fromInternalToDto(Attribute internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.ProjectNote dtoEntity = new org.gs4tr.termmanager.model.dto.ProjectNote();

	dtoEntity.setNoteName(internalEntity.getName());
	dtoEntity.setRequired(internalEntity.getRequired());
	dtoEntity.setComboValues(internalEntity.getComboValues());
	dtoEntity.setInputFieldTypeEnum(
		InputFieldTypeConverter.fromInternalToDto(internalEntity.getInputFieldTypeEnum()));
	dtoEntity.setMultiplicity(internalEntity.getMultiplicity());
	dtoEntity.setReadOnly(internalEntity.getReadOnly());
	dtoEntity.setRequired(internalEntity.getRequired());
	dtoEntity.setIsCustom(!TbxNoteTypes.isPreDefinedNote(internalEntity.getName()));

	return dtoEntity;
    }

    public static org.gs4tr.termmanager.model.dto.ProjectNote[] fromInternalToDto(List<Attribute> internalEntities) {
	if (internalEntities == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.ProjectNote[] dtoEntities = new org.gs4tr.termmanager.model.dto.ProjectNote[internalEntities
		.size()];
	int i = 0;
	for (Attribute attribute : internalEntities) {
	    dtoEntities[i] = fromInternalToDto(attribute);
	    i++;
	}

	return dtoEntities;
    }

}
