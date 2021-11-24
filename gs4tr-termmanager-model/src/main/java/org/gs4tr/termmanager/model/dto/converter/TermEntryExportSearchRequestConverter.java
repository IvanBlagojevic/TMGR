package org.gs4tr.termmanager.model.dto.converter;

import java.util.Arrays;

import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;

public class TermEntryExportSearchRequestConverter {

    public static TermEntrySearchRequest fromDtoToInternal(
	    org.gs4tr.termmanager.model.dto.TermEntrySearchRequest dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	TermEntrySearchRequest internalEntity = new TermEntrySearchRequest();
	internalEntity.setDateCreatedFrom(dtoEntity.getDateCreatedFrom());
	internalEntity.setDateCreatedTo(dtoEntity.getDateCreatedTo());
	internalEntity.setDateModifiedFrom(dtoEntity.getDateModifiedFrom());
	internalEntity.setDateCreatedFrom(dtoEntity.getDateCreatedFrom());
	internalEntity.setProjectId(TicketConverter.fromDtoToInternal(dtoEntity.getProjectTicket(), Long.class));
	internalEntity.setSourceLocale(dtoEntity.getSourceLocale());
	String[] targetLocales = dtoEntity.getTargetLocales();
	if (targetLocales != null) {
	    internalEntity.setTargetLocales(Arrays.asList(targetLocales));
	}
	internalEntity.setForbidden(dtoEntity.getForbidden());
	internalEntity.setIncludeSource(dtoEntity.isIncludeSource());

	return internalEntity;
    }
}
