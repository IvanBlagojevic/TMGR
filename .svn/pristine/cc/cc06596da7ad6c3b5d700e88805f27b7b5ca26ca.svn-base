package org.gs4tr.termmanager.model.dto.converter;

import org.gs4tr.termmanager.model.ExportInfo;

public class ExportInfoConverter {
    public static org.gs4tr.termmanager.model.dto.ExportInfo fromInternalToDto(ExportInfo internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.ExportInfo dtoEntity = new org.gs4tr.termmanager.model.dto.ExportInfo();
	dtoEntity.setTempFile(internalEntity.getTempFile());

	return dtoEntity;
    }
}
