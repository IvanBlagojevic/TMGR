package org.gs4tr.termmanager.service.listeners;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
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

@Component("deleteNoteEventListener")
public class DeleteNoteEventListener extends AbstractEventListener {

    @Override
    public void notify(EventMessage message) {
	TermEntry termEntry = message.getContextVariable(EventMessage.VARIABLE_TERM_ENTRY);

	UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);

	ProjectDetailInfo info = message.getContextVariable(EventMessage.VARIABLE_DETAIL_INFO);

	deleteTermNote(termEntry, command, info);
    }

    @Override
    public boolean supports(EventMessage message) {
	boolean supports = false;
	if (message.getNotifyingMessageId().equals(EventMessage.EVENT_UPDATE_TERMENTRY)) {
	    UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);
	    if (CommandEnum.REMOVE == command.getCommandEnum() && TypeEnum.NOTE == command.getTypeEnum()) {
		supports = true;
	    }
	}

	return supports;
    }

    private void deleteTermNote(TermEntry termEntry, UpdateCommand command, ProjectDetailInfo info) {
	String markerId = command.getMarkerId();

	for (Term term : termEntry.ggetTerms()) {

	    Set<Description> termDescriptions = term.getDescriptions();
	    if (CollectionUtils.isNotEmpty(termDescriptions)) {
		Iterator<Description> each = termDescriptions.iterator();
		while (each.hasNext()) {
		    Description candidate = each.next();
		    if (candidate.getUuid().equals(markerId)) {
			each.remove();

			LogHelper.debug(LOGGER, String.format(Messages.getString("DeleteNoteEventListener.0"), //$NON-NLS-1$
				candidate, term.getName()));

			String userName = TmUserProfile.getCurrentUserName();
			final Date dateModified = new Date();

			term.setDateModified(dateModified.getTime());
			term.setUserModified(userName);
			termEntry.setDateModified(dateModified.getTime());
			termEntry.setUserModified(userName);

			info.setDateModified(dateModified);
			info.addUpdatedLanguage(term.getLanguageId());

			return;
		    }

		}
	    }
	}
    }
}
