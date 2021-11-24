package org.gs4tr.termmanager.model.dto.converter.pagedlist;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.SortDirection;

public class PagedListInfoConverter {

    private static final String ASCENDING = "ASCENDING";

    private static final String DESCENDING = "DESCENDING";

    public static PagedListInfo fromDtoToInternal(org.gs4tr.termmanager.model.dto.pagedlist.PagedListInfo dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	PagedListInfo internalEntity = new PagedListInfo();

	internalEntity.setIndex(dtoEntity.getIndex());
	internalEntity.setIndexesSize(dtoEntity.getIndexesSize());
	internalEntity.setSize(dtoEntity.getSize());
	internalEntity.setSortProperty(dtoEntity.getSortDirection());

	if (dtoEntity.getSortDirection() != null) {
	    if (ASCENDING.equalsIgnoreCase(dtoEntity.getSortDirection())) {
		internalEntity.setSortDirection(SortDirection.ASCENDING);
	    } else {
		internalEntity.setSortDirection(SortDirection.DESCENDING);
	    }
	}

	return internalEntity;
    }

    public static org.gs4tr.termmanager.model.dto.pagedlist.PagedListInfo fromInternalToDto(
	    PagedListInfo internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.pagedlist.PagedListInfo dtoEntity = new org.gs4tr.termmanager.model.dto.pagedlist.PagedListInfo();

	dtoEntity.setIndex(internalEntity.getIndex());
	dtoEntity.setIndexesSize(internalEntity.getIndexesSize());
	dtoEntity.setSize(internalEntity.getSize());
	dtoEntity.setSortProperty(internalEntity.getSortProperty());

	if (internalEntity.getSortDirection() != null) {
	    if (SortDirection.ASCENDING == internalEntity.getSortDirection()) {
		dtoEntity.setSortDirection(ASCENDING);
	    } else {
		dtoEntity.setSortDirection(DESCENDING);
	    }
	}

	return dtoEntity;
    }

}
