package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.termmanager.model.ImportErrorMessage;

public class ImportErrorMessageConverter {
    public static org.gs4tr.termmanager.model.dto.ImportErrorMessage fromInternalToDto(
	    ImportErrorMessage internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.ImportErrorMessage dtoEntity = new org.gs4tr.termmanager.model.dto.ImportErrorMessage();
	dtoEntity.setErrorMessage(internalEntity.getErrorMessage());
	dtoEntity.setTermEntryCounter(internalEntity.getTermEntryCounter());

	return dtoEntity;
    }

    public static org.gs4tr.termmanager.model.dto.ImportErrorMessage[] fromInternalToDto(
	    List<ImportErrorMessage> internalEntities) {
	if (internalEntities == null) {
	    return null;
	}

	List<org.gs4tr.termmanager.model.dto.ImportErrorMessage> dtoEntities = new ArrayList<org.gs4tr.termmanager.model.dto.ImportErrorMessage>();
	for (ImportErrorMessage internalEntity : internalEntities) {
	    if (internalEntity != null) {
		dtoEntities.add(fromInternalToDto(internalEntity));
	    }
	}

	if (dtoEntities.size() > 0) {
	    return dtoEntities.toArray(new org.gs4tr.termmanager.model.dto.ImportErrorMessage[dtoEntities.size()]);
	} else {
	    return null;
	}
    }
}
