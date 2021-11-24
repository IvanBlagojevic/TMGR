package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.termmanager.model.dto.TargetDtoTermTranslation;
import org.gs4tr.termmanager.model.glossary.Term;
import org.springframework.util.CollectionUtils;

public class TargetTermTranslationConverter {

    public static TargetDtoTermTranslation[] fromInternalToDto(List<Term> internalEntities, boolean showAutoSaved) {
	if (internalEntities == null) {
	    return null;
	}

	List<TargetDtoTermTranslation> dtoEntities = new ArrayList<TargetDtoTermTranslation>();
	for (Term internalEntity : internalEntities) {
	    dtoEntities.add(fromInternalToDto(internalEntity, showAutoSaved));
	}

	if (CollectionUtils.isEmpty(dtoEntities)) {
	    return null;
	} else {
	    return dtoEntities.toArray(new TargetDtoTermTranslation[dtoEntities.size()]);
	}
    }

    public static TargetDtoTermTranslation fromInternalToDto(Term internalEntity, boolean showAutoSaved) {
	if (internalEntity == null) {
	    return null;
	}

	return new TargetDtoTermTranslation(internalEntity, showAutoSaved);
    }
}
