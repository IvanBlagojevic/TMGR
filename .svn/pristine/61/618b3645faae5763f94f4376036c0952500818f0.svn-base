package org.gs4tr.termmanager.service.listeners;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.termmanager.model.BaseTypeEnum;
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

@Component("addNoteEventListener")
public class AddNoteEventListener extends AbstractEventListener {

    @Override
    public void notify(EventMessage message) {
	TermEntry termEntry = message.getContextVariable(EventMessage.VARIABLE_TERM_ENTRY);

	UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);

	ProjectDetailInfo info = message.getContextVariable(EventMessage.VARIABLE_DETAIL_INFO);

	addNote(info, termEntry, command.getParentMarkerId(), command.getMarkerId(), command.getSubType(),
		command.getValue());
    }

    @Override
    public boolean supports(EventMessage message) {
	boolean supports = false;
	if (message.getNotifyingMessageId().equals(EventMessage.EVENT_UPDATE_TERMENTRY)) {
	    UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);
	    if (CommandEnum.ADD == command.getCommandEnum() && TypeEnum.NOTE == command.getTypeEnum()) {
		supports = true;
	    }
	}

	return supports;
    }

    private void addNote(ProjectDetailInfo info, TermEntry termEntry, final String parentMarkerId,
	    final String markerId, final String noteType, final String noteValue) {
	final List<Term> terms = termEntry.ggetTerms();
	if (CollectionUtils.isEmpty(terms)) {
	    /*
	     * TODO: Check if this is a real use case scenario. If it is, this is going to
	     * cause Staticstics increment bug.
	     */
	    return;
	}

	for (final Term term : terms) {
	    if (parentMarkerId.equals(term.getUuId())) {
		Description note = createTermNote(markerId, noteType, noteValue);

		Term termCopy = new Term(term);
		termCopy.addDescription(note);

		if (CollectionUtils.isNotEmpty(terms) && terms.contains(termCopy)) {
		    throw new UserException(Messages.getString("AddNoteEventListener.1"),
			    Messages.getString("AddNoteEventListener.2"));
		}

		term.addDescription(note);

		LogHelper.debug(LOGGER,
			String.format(Messages.getString("AddNoteEventListener.0"), note, term.getName())); //$NON-NLS-1$

		String userName = TmUserProfile.getCurrentUserName();
		Date dateModified = new Date();

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

    private Description createTermNote(String markerId, String noteType, String noteValue) {
	Description note = new Description();
	note.setBaseType(BaseTypeEnum.NOTE.getTypeName());
	note.setType(noteType);
	note.setValue(noteValue);
	note.setUuid(markerId);
	return note;
    }
}
