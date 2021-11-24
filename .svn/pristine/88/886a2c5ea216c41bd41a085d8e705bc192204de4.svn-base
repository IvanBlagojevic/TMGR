package org.gs4tr.termmanager.model.dto.converter;

import org.gs4tr.termmanager.model.InputFieldTypeEnum;

public class InputFieldTypeConverter {
    public static InputFieldTypeEnum fromDtoToInternal(org.gs4tr.termmanager.model.dto.InputFieldTypeEnum dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	if (org.gs4tr.termmanager.model.dto.InputFieldTypeEnum.TEXT.equals(dtoEntity)) {
	    return InputFieldTypeEnum.TEXT;
	} else if (org.gs4tr.termmanager.model.dto.InputFieldTypeEnum.COMBO.equals(dtoEntity)) {
	    return InputFieldTypeEnum.COMBO;
	} else {
	    return null;
	}
    }

    public static org.gs4tr.termmanager.model.dto.InputFieldTypeEnum fromInternalToDto(
	    InputFieldTypeEnum internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	if (InputFieldTypeEnum.TEXT == internalEntity) {
	    return org.gs4tr.termmanager.model.dto.InputFieldTypeEnum.TEXT;
	} else if (InputFieldTypeEnum.COMBO == internalEntity) {
	    return org.gs4tr.termmanager.model.dto.InputFieldTypeEnum.COMBO;
	} else {
	    return null;
	}
    }
}
