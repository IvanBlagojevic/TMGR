package org.gs4tr.termmanager.service.listeners;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.counter.StrategyTermCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("addTermEventListener")
public class AddTermEventListener extends AbstractEventListener {

    @Autowired
    private StrategyTermCounter _termCounter;

    @Override
    public void notify(EventMessage message) {
	TmProject project = message.getContextVariable(EventMessage.VARIABLE_PROJECT);

	ProjectDetailInfo detailInfo = message.getContextVariable(EventMessage.VARIABLE_DETAIL_INFO);

	TermEntry termEntry = message.getContextVariable(EventMessage.VARIABLE_TERM_ENTRY);

	String status = message.getContextVariable(EventMessage.VARIABLE_STATUS_TYPE);

	Boolean updateUserLatestChange = message.getContextVariable(EventMessage.VARIABLE_USER_LATEST_CHANGE);

	UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);

	String commandStatus = command.getStatus();
	if (StringUtils.isNotEmpty(commandStatus)) {
	    status = commandStatus;
	}

	String remoteUser = message.getContextVariable(EventMessage.VARIABLE_REMOTE_USER);

	addTerm(project, detailInfo, termEntry, status, updateUserLatestChange, command.getMarkerId(),
		command.getValue(), command.getLanguageId(), remoteUser);
    }

    @Override
    public boolean supports(EventMessage message) {
	boolean supports = false;
	if (message.getNotifyingMessageId().equals(EventMessage.EVENT_UPDATE_TERMENTRY)) {
	    UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);
	    if (CommandEnum.ADD == command.getCommandEnum() && TypeEnum.TERM == command.getTypeEnum()) {
		supports = true;
	    }
	}

	return supports;
    }

    private void addTerm(TmProject project, ProjectDetailInfo info, TermEntry termEntry, String status,
	    Boolean updateUserLatestChange, String markerId, String value, String languageId, String remoteUser) {
	if (StringUtils.isEmpty(value)) {
	    throw new UserException(Messages.getString("AddTermEventListener.1"), //$NON-NLS-1$
		    Messages.getString("AddTermEventListener.2")); //$NON-NLS-1$
	}

	TmUserProfile user = TmUserProfile.getCurrentUserProfile();

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	if (languageTerms == null) {
	    languageTerms = new HashMap<>();
	    termEntry.setLanguageTerms(languageTerms);

	    info.incrementTermEntryCount();
	    info.incrementTermEntryCount(user.getUserProfileId());
	}

	Term term = new Term();
	term.setUuId(markerId == null ? UUID.randomUUID().toString() : markerId);
	term.setName(value);

	term.setLanguageId(languageId);
	if (Objects.nonNull(updateUserLatestChange) && updateUserLatestChange) {
	    term.setUserLatestChange(user.getUserProfileId());
	}

	final Date date = new Date();
	String userName = StringUtils.isEmpty(remoteUser) ? TmUserProfile.getCurrentUserName() : remoteUser;

	term.setStatus(status);
	term.setDisabled(Boolean.FALSE);
	term.setUserCreated(userName);
	term.setUserModified(userName);
	term.setDateCreated(date.getTime());
	term.setDateModified(date.getTime());

	term.setForbidden(ItemStatusTypeHolder.BLACKLISTED.getName().equals(status));

	Set<Term> terms = languageTerms.get(languageId);
	if (CollectionUtils.isNotEmpty(terms)) {
	    // This code is added due to hashCode problem (contains method will work
	    // properly only on new collection)
	    Set<Term> clone = new HashSet<>(terms.size());
	    clone.addAll(terms);
	    if (clone.contains(term)) {
		// TERII-5706 After merging approved and demoted terms from floating-multi
		// editor, merged term has pending status
		for (Term existingTerm : terms) {
		    if (isAddedTermStatusHigherLevel(existingTerm.getStatus(), term.getStatus())) {

			getTermCounter().updateTermCount(info, term.getStatus(), existingTerm,
				existingTerm.getStatus());

			existingTerm.setStatus(term.getStatus());
			existingTerm.setDateModified(date.getTime());
			existingTerm.setUserModified(userName);
			existingTerm.setUserLatestChange(term.getUserLatestChange());
			termEntry.setUserModified(userName);
			info.setDateModified(date);
		    }
		}
		return;
	    }
	}

	if (CollectionUtils.isEmpty(termEntry.ggetAllTerms())) {
	    /*
	     * If termEntry does not contains terms, it means that this is a new termEntry,
	     * so set creation date and user.
	     */
	    termEntry.setDateCreated(date.getTime());
	    termEntry.setUserCreated(userName);
	}

	termEntry.setUserModified(userName);
	termEntry.setDateModified(date.getTime());
	termEntry.addTerm(term);

	// Make sure to update term counts only after we add new term.
	getTermCounter().updateTermCount(info, status, term, null);

	LogHelper.debug(LOGGER, String.format(Messages.getString("AddTermEventListener.0"), //$NON-NLS-1$
		value, project.getProjectInfo().getName()));

	info.incrementTermCount(languageId);
	info.setDateModified(date);

	return;
    }

    private StrategyTermCounter getTermCounter() {
	return _termCounter;
    }

    private boolean isAddedTermStatusHigherLevel(String itemStatus1, String itemStatus2) {
	return ItemStatusTypeHolder.getItemStatusTypeLevel(itemStatus1) > ItemStatusTypeHolder
		.getItemStatusTypeLevel(itemStatus2);
    }
}