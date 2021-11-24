package org.gs4tr.termmanager.service.counter;

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.glossary.Term;

public class DemoteCounterImpl implements TermCounter {

    @Override
    public void updateTermCount(ProjectDetailInfo detailInfo, Term term) {

	final String processed = ItemStatusTypeHolder.PROCESSED.getName();

	final String blackListed = ItemStatusTypeHolder.BLACKLISTED.getName();

	final String onHold = ItemStatusTypeHolder.ON_HOLD.getName();

	String languageId = term.getLanguageId();

	detailInfo.incrementPendingTermCount(languageId);

	String currentStatus = term.getStatus();
	if (processed.equals(currentStatus)) {
	    detailInfo.decrementApprovedTermCount(languageId);
	} else if (blackListed.equals(currentStatus)) {
	    detailInfo.decrementForbiddenTermCount(languageId);
	    term.setForbidden(Boolean.FALSE);
	} else if (onHold.equals(currentStatus)) {
	    detailInfo.decrementOnHoldTermCount(languageId);
	}
    }
}
