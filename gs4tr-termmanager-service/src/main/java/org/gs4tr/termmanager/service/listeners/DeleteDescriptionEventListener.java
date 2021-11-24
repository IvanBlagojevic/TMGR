package org.gs4tr.termmanager.service.listeners;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.counter.StrategyTermCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("deleteDescriptionEventListener")
public class DeleteDescriptionEventListener extends AbstractEventListener {

    @Autowired
    private StrategyTermCounter _termCounter;

    @Override
    public void notify(EventMessage message) {
	TermEntry termEntry = message.getContextVariable(EventMessage.VARIABLE_TERM_ENTRY);

	UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);

	ProjectDetailInfo info = message.getContextVariable(EventMessage.VARIABLE_DETAIL_INFO);

	deleteDescription(termEntry, command, info);
    }

    @Override
    public boolean supports(EventMessage message) {
	boolean supports = false;
	if (message.getNotifyingMessageId().equals(EventMessage.EVENT_UPDATE_TERMENTRY)) {
	    UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);
	    if (CommandEnum.REMOVE == command.getCommandEnum() && TypeEnum.DESCRIP == command.getTypeEnum()) {
		supports = true;
	    }
	}

	return supports;
    }

    private void deleteDescription(TermEntry termEntry, UpdateCommand command, ProjectDetailInfo info) {
	Set<Description> termEntryDescriptions = termEntry.getDescriptions();

	String markerId = command.getMarkerId();

	String userName = TmUserProfile.getCurrentUserName();

	if (CollectionUtils.isNotEmpty(termEntryDescriptions)) {
	    Iterator<Description> each = termEntryDescriptions.iterator();
	    while (each.hasNext()) {
		Description candidate = each.next();
		if (markerId.equals(candidate.getUuid())) {
		    each.remove();

		    final Date dateModified = new Date();

		    termEntry.setDateModified(dateModified.getTime());
		    termEntry.setUserModified(userName);

		    LogHelper.debug(LOGGER,
			    String.format(Messages.getString("DeleteDescriptionEventListener.3"), candidate)); //$NON-NLS-1$

		    info.setDateModified(dateModified);
		    return;
		}
	    }
	}

	List<Term> terms = termEntry.ggetTerms();
	for (final Term term : terms) {
	    Set<Description> descriptions = term.getDescriptions();
	    if (CollectionUtils.isNotEmpty(descriptions)) {

		Iterator<Description> each = descriptions.iterator();
		while (each.hasNext()) {
		    Description candidate = each.next();
		    if (markerId.equals(candidate.getUuid())) {
			each.remove();

			final Date dateModified = new Date();

			mergeTermsIfNeeded(term, info, termEntry, dateModified);

			LogHelper.debug(LOGGER,
				String.format(Messages.getString("DeleteDescriptionEventListener.0"), candidate)); //$NON-NLS-1$

			termEntry.setDateModified(dateModified.getTime());
			termEntry.setUserModified(userName);
			term.setDateModified(dateModified.getTime());
			term.setUserModified(userName);

			info.setDateModified(dateModified);
			info.addUpdatedLanguage(term.getLanguageId());

			return;
		    }
		}
	    }
	}
    }

    private StrategyTermCounter getTermCounter() {
	return _termCounter;
    }

    private void mergeTermsIfNeeded(Term deletedDescTerm, ProjectDetailInfo info, TermEntry termEntry,
	    Date dateModified) {

	Set<Term> languageTerms = termEntry.getLanguageTerms().get(deletedDescTerm.getLanguageId());

	if (CollectionUtils.isEmpty(languageTerms)) {
	    return;
	}

	Term currentTerm = termEntry.ggetTermById(deletedDescTerm.getUuId());

	Term clone = currentTerm.cloneTerm();

	String userName = TmUserProfile.getCurrentUserName();

	String blacklisted = ItemStatusTypeHolder.BLACKLISTED.getName();

	boolean isCloneMergeStatus = !clone.getStatus().equals(blacklisted);

	boolean isMerged = false;

	for (Term term : languageTerms) {

	    if (Objects.isNull(term.getDescriptions())) {
		term.setDescriptions(new HashSet<>());
	    }

	    String termStatus = term.getStatus();

	    boolean isMergeStatus = isCloneMergeStatus && !termStatus.equals(blacklisted);

	    if (isMergeStatus && term.equals(clone) && !term.getUuId().equals(clone.getUuId())) {

		term.setDisabled(Boolean.TRUE);
		term.setFirst(Boolean.FALSE);

		term.setDateModified(dateModified.getTime());
		term.setUserModified(userName);

		String currentStatus = currentTerm.getStatus();

		updateStatusIfNeeded(currentTerm, term);

		String newStatus = currentTerm.getStatus();

		// Update if remaining term status is changed after merge action
		getTermCounter().updateTermCount(info, newStatus, clone, currentStatus);

		// Update when term is disabled
		getTermCounter().updateTermCount(info, StrategyTermCounter.DISABLED, term, currentStatus);

		isMerged = true;

	    }
	}
	if (isMerged) {
	    setFirstIfNotExists(languageTerms);
	}
    }

    private void setFirstIfNotExists(Set<Term> languageTerms) {
	Optional<Term> optional = languageTerms.stream().filter(term -> term.isFirst() && !term.isDisabled())
		.findFirst();
	if (!optional.isPresent()) {
	    Optional<Term> o = languageTerms.stream().filter(term -> !term.isFirst() && !term.isDisabled()).findFirst();
	    o.ifPresent(term -> term.setFirst(Boolean.TRUE));
	}
    }

    private void updateStatusIfNeeded(Term newTerm, Term oldTerm) {

	if (Objects.isNull(newTerm) || Objects.isNull(oldTerm)) {
	    return;
	}

	int newTermStatusLvl = ItemStatusTypeHolder.getItemStatusTypeLevel(newTerm.getStatus());
	int oldTermStatusLvl = ItemStatusTypeHolder.getItemStatusTypeLevel(oldTerm.getStatus());

	String status = newTermStatusLvl <= oldTermStatusLvl ? newTerm.getStatus() : oldTerm.getStatus();
	newTerm.setStatus(status);
    }

}
