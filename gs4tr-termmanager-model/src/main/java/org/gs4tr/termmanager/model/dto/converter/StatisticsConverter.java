package org.gs4tr.termmanager.model.dto.converter;

import org.gs4tr.termmanager.model.dto.Statistics;

public class StatisticsConverter {

    public static Statistics fromInternalToDto(org.gs4tr.termmanager.model.Statistics statistics, String langName) {
	if (isLanguageModified(statistics)) {
	    Statistics dto = new Statistics();
	    dto.setAddedApprovedTerms(statistics.getAddedApproved());
	    dto.setAddedPendingTerms(statistics.getAddedPending());
	    dto.setApprovedTerms(statistics.getApproved());
	    dto.setDeletedTerms(statistics.getDeleted());
	    dto.setDemotedTerms(statistics.getDemoted());
	    dto.setUpdatedTerms(statistics.getUpdated());
	    dto.setAddedOnHoldTerms(statistics.getAddedOnHold());
	    dto.setOnHoldTerms(statistics.getOnHold());
	    dto.setAddedBlacklistedTerms(statistics.getAddedBlacklisted());
	    dto.setBlacklistedTerms(statistics.getBlacklisted());
	    dto.setLanguageName(langName);
	    return dto;
	}
	// 5-August-2016, as per [Improvement#TERII-2948]:
	return null;
    }

    private StatisticsConverter() {
    }

    private static boolean isLanguageModified(org.gs4tr.termmanager.model.Statistics statistics) {
	boolean modified = false;
	modified |= statistics.getAddedApproved() != 0;
	modified |= statistics.getAddedPending() != 0;
	modified |= statistics.getApproved() != 0;
	modified |= statistics.getDeleted() != 0;
	modified |= statistics.getDemoted() != 0;
	modified |= statistics.getUpdated() != 0;
	modified |= statistics.getAddedOnHold() != 0;
	modified |= statistics.getOnHold() != 0;
	modified |= statistics.getAddedBlacklisted() != 0;
	modified |= statistics.getBlacklisted() != 0;
	return modified;
    }
}
