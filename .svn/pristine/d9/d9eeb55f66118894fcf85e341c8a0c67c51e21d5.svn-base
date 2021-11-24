package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.termmanager.model.glossary.Term;
import org.springframework.util.CollectionUtils;

public class DtoTermConverter {

    public static org.gs4tr.termmanager.model.dto.BaseDtoTerm[] fromInternalToDto(List<Term> internalEntities) {
	if (internalEntities == null) {
	    return null;
	}

	List<org.gs4tr.termmanager.model.dto.BaseDtoTerm> dtoEntities = new ArrayList<org.gs4tr.termmanager.model.dto.BaseDtoTerm>();
	for (Term internalEntity : internalEntities) {
	    dtoEntities.add(fromInternalToDto(internalEntity));
	}

	if (CollectionUtils.isEmpty(dtoEntities)) {
	    return null;
	} else {
	    return dtoEntities.toArray(new org.gs4tr.termmanager.model.dto.BaseDtoTerm[dtoEntities.size()]);
	}
    }

    public static org.gs4tr.termmanager.model.dto.BaseDtoTerm fromInternalToDto(Term internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	return new org.gs4tr.termmanager.model.dto.BaseDtoTerm(internalEntity);
    }
}
