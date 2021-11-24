package org.gs4tr.termmanager.service.listeners;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.counter.StrategyTermCounter;
import org.gs4tr.termmanager.service.utils.TermEntryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("deleteTermEventListener")
public class DeleteTermEventListener extends AbstractEventListener {

    @Autowired
    private StrategyTermCounter _strategyTermCounter;

    @Override
    public void notify(EventMessage message) {
	TermEntry termEntry = message.getContextVariable(EventMessage.VARIABLE_TERM_ENTRY);

	UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);

	ProjectDetailInfo info = message.getContextVariable(EventMessage.VARIABLE_DETAIL_INFO);

	deleteTerm(termEntry, command, info);
    }

    @Override
    public boolean supports(EventMessage message) {
	boolean supports = false;
	if (message.getNotifyingMessageId().equals(EventMessage.EVENT_UPDATE_TERMENTRY)) {
	    UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);
	    if (CommandEnum.REMOVE == command.getCommandEnum() && TypeEnum.TERM == command.getTypeEnum()) {
		supports = true;
	    }
	}

	return supports;
    }

    private void deleteTerm(TermEntry termEntry, UpdateCommand command, ProjectDetailInfo info) {

	Map<String, Set<Term>> languageMap = termEntry.getLanguageTerms();
	Set<Term> terms = languageMap.get(command.getLanguageId());

	Set<String> synonymIds = new HashSet<String>();
	for (Term term : terms) {
	    if (!term.isDisabled() && !term.getUuId().equals(command.getMarkerId())) {
		synonymIds.add(term.getUuId());
	    }
	}

	TmUserProfile user = TmUserProfile.getCurrentUserProfile();
	final Date dateModified = new Date();

	Iterator<Term> each = terms.iterator();
	while (each.hasNext()) {
	    Term term = each.next();
	    if (term.getUuId().equals(command.getMarkerId())) {
		String termId = term.getUuId();
		String name = term.getName();
		String status = term.getStatus();
		String username = user.getUserName();
		term.setDisabled(Boolean.TRUE);

		term.setDateModified(dateModified.getTime());
		term.setUserModified(username);

		termEntry.setDateModified(dateModified.getTime());
		termEntry.setUserModified(username);

		info.setDateModified(dateModified);

		if (term.isFirst()) {
		    if (!synonymIds.isEmpty()) {
			term.setFirst(Boolean.FALSE);
			String synonymId = synonymIds.iterator().next();
			Term synonym = termEntry.ggetTermById(synonymId);
			if (synonym != null) {
			    synonym.setFirst(Boolean.TRUE);
			}
		    }
		}

		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug(String.format(Messages.getString("DeleteTermEventListener.0"), name, termId)); //$NON-NLS-1$
		}

		if (CollectionUtils.isEmpty(termEntry.ggetTerms())) {
		    info.decrementTermEntryCount();
		    info.decrementTermEntryCount(user.getUserProfileId());
		}

		getStrategyTermCounter().updateTermCount(info, StrategyTermCounter.DISABLED, term, status);

		if (TermEntryUtils.updateDuplicateTerms(terms, term)) {
		    if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(Messages.getString("DeleteTermEventListener.2"), //$NON-NLS-1$
				term.getName(), term.getUuId()));
		    }
		}

		return;
	    }
	}
    }

    private StrategyTermCounter getStrategyTermCounter() {
	return _strategyTermCounter;
    }
}
