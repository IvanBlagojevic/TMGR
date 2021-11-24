package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.termmanager.model.TermEntryTranslationUnit;

public class TermEntryTranslationUnitConverter {

    public static TermEntryTranslationUnit fromDtoToInternal(
	    org.gs4tr.termmanager.model.dto.TermEntryTranslationUnit dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	TermEntryTranslationUnit internalEntity = new TermEntryTranslationUnit();

	internalEntity.setProjectId(TicketConverter.fromDtoToInternal(dtoEntity.getProjectTicket(), Long.class));
	internalEntity.setTermEntryId(dtoEntity.getTermEntryTicket());
	internalEntity.setUpdateCommands(UpdateCommandConverter.fromDtoToInternal(dtoEntity.getUpdateCommands()));

	return internalEntity;
    }

    public static List<TermEntryTranslationUnit> fromDtoToInternal(
	    org.gs4tr.termmanager.model.dto.TermEntryTranslationUnit[] dtoEntities) {
	if (dtoEntities == null) {
	    return null;
	}

	List<TermEntryTranslationUnit> internalEntities = new ArrayList<TermEntryTranslationUnit>();
	for (org.gs4tr.termmanager.model.dto.TermEntryTranslationUnit dtoEntity : dtoEntities) {
	    if (dtoEntity != null) {
		internalEntities.add(fromDtoToInternal(dtoEntity));
	    }
	}

	if (internalEntities.size() > 0) {
	    return internalEntities;
	} else {
	    return null;
	}
    }
}
