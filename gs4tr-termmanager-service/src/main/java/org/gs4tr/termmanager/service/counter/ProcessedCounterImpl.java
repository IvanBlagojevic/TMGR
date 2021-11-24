package org.gs4tr.termmanager.service.counter;

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.glossary.Term;

public class ProcessedCounterImpl implements TermCounter {

    @Override
    public void updateTermCount(ProjectDetailInfo detailInfo, Term term) {

	String languageId = term.getLanguageId();

	detailInfo.incrementApprovedTermCount(languageId);

	final String blackListed = ItemStatusTypeHolder.BLACKLISTED.getName();

	final String onHold = ItemStatusTypeHolder.ON_HOLD.getName();

	/* WAITING status is for Pending Approval because SOLR reindex */
	final String pending = ItemStatusTypeHolder.WAITING.getName();

	String currentStatus = term.getStatus();
	if (blackListed.equals(currentStatus)) {
	    detailInfo.decrementForbiddenTermCount(languageId);
	    term.setForbidden(Boolean.FALSE);
	} else if (onHold.equals(currentStatus)) {
	    detailInfo.decrementOnHoldTermCount(languageId);
	} else if (pending.equals(currentStatus)) {
	    detailInfo.decrementPendingTermCount(languageId);
	}
    }
}
