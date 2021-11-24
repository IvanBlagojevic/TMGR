package org.gs4tr.termmanager.service.counter;

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.glossary.Term;

public class BlacklistedCounterImpl implements TermCounter {

    @Override
    public void updateTermCount(ProjectDetailInfo detailInfo, Term term) {
	String languageId = term.getLanguageId();

	detailInfo.incrementForbiddenTermCount(languageId);

	term.setForbidden(Boolean.TRUE);

	final String processed = ItemStatusTypeHolder.PROCESSED.getName();

	final String onHold = ItemStatusTypeHolder.ON_HOLD.getName();

	final String pending = ItemStatusTypeHolder.WAITING.getName();

	String currentStatus = term.getStatus();
	if (processed.equals(currentStatus)) {
	    detailInfo.decrementApprovedTermCount(languageId);
	} else if (onHold.equals(currentStatus)) {
	    detailInfo.decrementOnHoldTermCount(languageId);
	} else if (pending.equals(currentStatus)) {
	    detailInfo.decrementPendingTermCount(languageId);
	}
    }
}
