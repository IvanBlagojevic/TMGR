package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;

public class TranslationUnitConverter {
    public static TranslationUnit fromDtoToInternal(org.gs4tr.termmanager.model.dto.TranslationUnit dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	TranslationUnit internalEntity = new TranslationUnit();

	internalEntity.setTermEntryId(dtoEntity.getTermEntryTicket());
	internalEntity.setMatchedTermEntryId(dtoEntity.getMatchedTermEntryTicket());
	internalEntity.setSourceTermUpdateCommands(
		UpdateCommandConverter.fromDtoToInternal(dtoEntity.getSourceTermUpdateCommands()));
	internalEntity.setTargetTermUpdateCommands(
		UpdateCommandConverter.fromDtoToInternal(dtoEntity.getTargetTermUpdateCommands()));

	return internalEntity;
    }

    public static List<TranslationUnit> fromDtoToInternal(
	    org.gs4tr.termmanager.model.dto.TranslationUnit[] dtoEntities) {
	if (dtoEntities == null) {
	    return null;
	}

	List<TranslationUnit> internalEntities = new ArrayList<TranslationUnit>();
	for (org.gs4tr.termmanager.model.dto.TranslationUnit dtoEntity : dtoEntities) {
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

    public static org.gs4tr.termmanager.model.dto.TranslationUnit[] fromInternalToDto(
	    List<TranslationUnit> internalEntities) {
	List<org.gs4tr.termmanager.model.dto.TranslationUnit> dtoUnits = new ArrayList<>();

	if (CollectionUtils.isNotEmpty(internalEntities)) {
	    for (TranslationUnit internalEntity : internalEntities) {
		org.gs4tr.termmanager.model.dto.TranslationUnit dtoUnit = fromInternalToDto(internalEntity);
		if (dtoUnit != null) {
		    dtoUnits.add(dtoUnit);
		}
	    }
	}

	return dtoUnits.toArray(new org.gs4tr.termmanager.model.dto.TranslationUnit[dtoUnits.size()]);
    }

    public static org.gs4tr.termmanager.model.dto.TranslationUnit fromInternalToDto(TranslationUnit internalEntity) {

	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.TranslationUnit dtoEntity = new org.gs4tr.termmanager.model.dto.TranslationUnit();
	dtoEntity.setTermEntryTicket(internalEntity.getTermEntryId());
	dtoEntity.setMatchedTermEntryTicket(internalEntity.getMatchedTermEntryId());

	List<UpdateCommand> sourceTermUpdateCommands = internalEntity.getSourceTermUpdateCommands();
	if (CollectionUtils.isNotEmpty(sourceTermUpdateCommands)) {
	    dtoEntity.setSourceTermUpdateCommands(UpdateCommandConverter.fromInternalToDto(sourceTermUpdateCommands));
	}

	List<UpdateCommand> targetTermUpdateCommands = internalEntity.getTargetTermUpdateCommands();
	if (CollectionUtils.isNotEmpty(targetTermUpdateCommands)) {
	    dtoEntity.setTargetTermUpdateCommands(UpdateCommandConverter.fromInternalToDto(targetTermUpdateCommands));
	}

	return dtoEntity;
    }
}
