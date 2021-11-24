package org.gs4tr.termmanager.service.counter;

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.glossary.Term;

public class DisableCounterImpl implements TermCounter {

    @Override
    public void updateTermCount(ProjectDetailInfo info, Term term) {
	String languageId = term.getLanguageId();

	String status = term.getStatus();

	info.decrementTermCount(languageId);
	if (status.equals(ItemStatusTypeHolder.PROCESSED.getName())) {
	    info.decrementApprovedTermCount(languageId);
	} else if (status.equals(ItemStatusTypeHolder.BLACKLISTED.getName())) {
	    info.decrementForbiddenTermCount(languageId);
	} else if (status.equals(ItemStatusTypeHolder.ON_HOLD.getName())) {
	    info.decrementOnHoldTermCount(languageId);
	} else if (status.equals(ItemStatusTypeHolder.WAITING.getName())) {
	    info.decrementPendingTermCount(languageId);
	}
    }
}
