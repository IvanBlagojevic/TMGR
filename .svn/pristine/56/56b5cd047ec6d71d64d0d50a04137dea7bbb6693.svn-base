package org.gs4tr.termmanager.service.listeners;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.springframework.stereotype.Component;

@Component("addDescriptionEventListener")
public class AddDescriptionEventListener extends AbstractEventListener {

    @Override
    public void notify(EventMessage message) {
	TermEntry termEntry = message.getContextVariable(EventMessage.VARIABLE_TERM_ENTRY);

	UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);

	ProjectDetailInfo info = message.getContextVariable(EventMessage.VARIABLE_DETAIL_INFO);

	String remoteUser = message.getContextVariable(EventMessage.VARIABLE_REMOTE_USER);

	addDescription(info, termEntry, command.getParentMarkerId(), command.getMarkerId(), command.getSubType(),
		command.getValue(), remoteUser);
    }

    @Override
    public boolean supports(EventMessage message) {
	boolean supports = false;
	if (message.getNotifyingMessageId().equals(EventMessage.EVENT_UPDATE_TERMENTRY)) {
	    UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);
	    if (CommandEnum.ADD == command.getCommandEnum() && TypeEnum.DESCRIP == command.getTypeEnum()) {
		supports = true;
	    }
	}
	return supports;
    }

    private void addDescription(ProjectDetailInfo info, TermEntry termEntry, String parentMarkerId, String markerId,
	    String type, String value, String remoteUser) {

	if (parentMarkerId.equals(termEntry.getUuId())) {
	    Set<Description> termEntryDescriptions = termEntry.getDescriptions();
	    if (termEntryDescriptions == null) {
		termEntryDescriptions = new HashSet<Description>();
		termEntry.setDescriptions(termEntryDescriptions);
	    }

	    Description description = new Description(type, value);
	    termEntryDescriptions.add(description);

	    LogHelper.debug(LOGGER, String.format(Messages.getString("AddDescriptionEventListener.0"), description)); //$NON-NLS-1$

	    Date dateModified = new Date();
	    info.setDateModified(dateModified);

	    termEntry.setDateModified(dateModified.getTime());
	    termEntry.setUserModified(TmUserProfile.getCurrentUserName());

	    return;
	}

	final List<Term> terms = termEntry.ggetTerms();

	if (CollectionUtils.isNotEmpty(terms)) {
	    for (final Term term : terms) {
		if (parentMarkerId.equals(term.getUuId())) {
		    Description description = new Description(type, value);

		    Term termCopy = new Term(term);
		    termCopy.addDescription(description);

		    if (CollectionUtils.isNotEmpty(terms) && terms.contains(termCopy)) {
			throw new UserException(Messages.getString("AddDescriptionEventListener.3"),
				Messages.getString("AddDescriptionEventListener.4"));
		    }

		    term.addDescription(description);

		    LogHelper.debug(LOGGER, String.format(Messages.getString("AddDescriptionEventListener.2"), //$NON-NLS-1$
			    description, term.getName()));

		    String currentUserName = TmUserProfile.getCurrentUserName();

		    String userName = StringUtils.isEmpty(remoteUser) ? currentUserName : remoteUser;

		    Date dateModified = new Date();

		    termEntry.setDateModified(dateModified.getTime());
		    term.setDateModified(dateModified.getTime());
		    termEntry.setUserModified(userName);
		    term.setUserModified(userName);

		    info.setDateModified(dateModified);
		    info.addUpdatedLanguage(term.getLanguageId());

		    return;
		}
	    }
	}
    }
}
