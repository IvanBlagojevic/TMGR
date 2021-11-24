package org.gs4tr.termmanager.service.counter;

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.glossary.Term;

public class OnHoldCounterImpl implements TermCounter {

    @Override
    public void updateTermCount(ProjectDetailInfo detailInfo, Term term) {

	final String processed = ItemStatusTypeHolder.PROCESSED.getName();

	final String blackListed = ItemStatusTypeHolder.BLACKLISTED.getName();

	/* WAITING status is for Pending Approval because SOLR reindex */
	final String pending = ItemStatusTypeHolder.WAITING.getName();

	String languageId = term.getLanguageId();

	detailInfo.incrementOnHoldTermCount(languageId);

	String currentStatus = term.getStatus();
	if (processed.equals(currentStatus)) {
	    detailInfo.decrementApprovedTermCount(languageId);
	} else if (blackListed.equals(currentStatus)) {
	    detailInfo.decrementForbiddenTermCount(languageId);
	    term.setForbidden(Boolean.FALSE);
	} else if (pending.equals(currentStatus)) {
	    detailInfo.decrementPendingTermCount(languageId);
	}
    }
}
