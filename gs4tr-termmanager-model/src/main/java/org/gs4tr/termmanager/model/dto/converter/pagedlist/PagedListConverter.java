package org.gs4tr.termmanager.model.dto.converter.pagedlist;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.termmanager.model.CustomCollectionUtils;
import org.gs4tr.termmanager.model.dto.converter.TermConverter;
import org.gs4tr.termmanager.model.dto.pagedlist.TermPagedList;
import org.gs4tr.termmanager.model.glossary.Term;

public class PagedListConverter {

    public static PagedList<Term> fromDtoToInternal(TermPagedList dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	PagedList<Term> internalEntity = new PagedList<Term>();

	internalEntity.setElements(
		CustomCollectionUtils.getArray(TermConverter.fromDtoToInternal(dtoEntity.getElements()), Term.class));
	internalEntity.setPagedListInfo(PagedListInfoConverter.fromDtoToInternal(dtoEntity.getPagedListInfo()));
	internalEntity.setTotalCount(dtoEntity.getTotalCount());

	return internalEntity;
    }

    public static TermPagedList fromInternalToDto(PagedList<Term> internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	TermPagedList dtoEntity = new TermPagedList();

	dtoEntity.setElements(
		TermConverter.fromInternalToDto(CustomCollectionUtils.getSet(internalEntity.getElements())));
	dtoEntity.setPagedListInfo(PagedListInfoConverter.fromInternalToDto(internalEntity.getPagedListInfo()));
	dtoEntity.setTotalCount(internalEntity.getTotalCount());

	return dtoEntity;
    }

}
