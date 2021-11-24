package org.gs4tr.termmanager.service.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.counter.StrategyTermCounter;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("updateTermEventListener")
public class UpdateTermEventListener extends AbstractEventListener {

    @Autowired
    private StrategyTermCounter _strategyTermCounter;

    @Override
    public void notify(EventMessage message) {
	ProjectDetailInfo info = message.getContextVariable(EventMessage.VARIABLE_DETAIL_INFO);
	TermEntry termEntry = message.getContextVariable(EventMessage.VARIABLE_TERM_ENTRY);
	UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);

	updateTerm(info, termEntry, command);
    }

    @Override
    public boolean supports(EventMessage message) {
	boolean supports = false;
	if (message.getNotifyingMessageId().equals(EventMessage.EVENT_UPDATE_TERMENTRY)) {
	    UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);
	    if (CommandEnum.UPDATE == command.getCommandEnum() && TypeEnum.TERM == command.getTypeEnum()) {
		supports = true;
	    }
	}

	return supports;
    }

    private Term findDuplicate(Term term, Collection<Term> terms) {
	Optional<Term> match = terms.stream().filter(t -> t.equals(term)).findFirst();
	return match.orElse(null);
    }

    private StrategyTermCounter getStrategyTermCounter() {
	return _strategyTermCounter;
    }

    private boolean isAddedTermStatusHigherLevel(String itemStatus1, String itemStatus2) {
	return ItemStatusTypeHolder.getItemStatusTypeLevel(itemStatus1) > ItemStatusTypeHolder
		.getItemStatusTypeLevel(itemStatus2);
    }

    private boolean isStatusesTypesProcessed(String currentStatus, String newStatus) {
	String statusProcessed = ItemStatusTypeHolder.PROCESSED.toString();

	return currentStatus.equals(statusProcessed) && newStatus.equals(statusProcessed);
    }

    private void updateTerm(ProjectDetailInfo info, TermEntry termEntry, UpdateCommand command) {
	String value = command.getValue();
	String markerId = command.getMarkerId();
	String newStatus = command.getStatus();

	if (StringUtils.isEmpty(value)) {
	    throw new UserException(Messages.getString("UpdateTermEventListener.1"), //$NON-NLS-1$
		    Messages.getString("UpdateTermEventListener.2")); //$NON-NLS-1$
	}

	Long projectId = info.getProjectId();

	List<Term> terms = termEntry.ggetTerms();

	if (CollectionUtils.isNotEmpty(terms)) {

	    List<Term> clonedTermList = new ArrayList<Term>(terms);

	    for (Term term : terms) {
		if (term.getUuId().equals(markerId)) {

		    clonedTermList.remove(term);

		    String userName = TmUserProfile.getCurrentUserName();
		    final Date dateModified = new Date();

		    String oldValue = term.getName();
		    String currentStatus = term.getStatus();

		    // TERII-3597
		    // Editing Approved terms should demote them to admitted or Pending status
		    if (isStatusesTypesProcessed(currentStatus, newStatus) && !oldValue.equals(value)) {
			newStatus = ServiceUtils.decideNewTermStatusOnRenameTerm(projectId).getName();
		    }

		    // TERII-5164
		    // this is only so that we know to increment update in statistics
		    command.setOldValue(term.getName());
		    command.setStatusOld(currentStatus);

		    term.setDateModified(dateModified.getTime());
		    term.setName(value);

		    boolean isForbidden = ItemStatusTypeHolder.BLACKLISTED.getName().equals(newStatus);
		    term.setForbidden(isForbidden);

		    if (clonedTermList.contains(term)) {
			Term duplicate = findDuplicate(term, clonedTermList);

			if (isAddedTermStatusHigherLevel(duplicate.getStatus(), newStatus)) {
			    getStrategyTermCounter().updateTermCount(info, newStatus, duplicate, duplicate.getStatus());
			    duplicate.setStatus(newStatus);

			}
			// Switch main term with synonym since we are going to
			// delete main term
			if (term.isFirst()) {
			    term.setFirst(Boolean.FALSE);
			    duplicate.setFirst(Boolean.TRUE);
			}
			// delete duplicated term
			term.setDisabled(Boolean.TRUE);
		    }

		    newStatus = term.isDisabled() ? StrategyTermCounter.DISABLED : newStatus;
		    getStrategyTermCounter().updateTermCount(info, newStatus, term, currentStatus);

		    term.setStatus(newStatus);
		    term.setUserModified(userName);
		    termEntry.setDateModified(dateModified.getTime());
		    termEntry.setUserModified(userName);

		    if (!oldValue.equals(value)) {
			info.addUpdatedLanguage(term.getLanguageId());
		    }
		    info.setDateModified(dateModified);

		    if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(Messages.getString("UpdateTermEventListener.0"), //$NON-NLS-1$
				oldValue, value, dateModified.toString()));
		    }

		    return;
		}
	    }
	}
    }
}
