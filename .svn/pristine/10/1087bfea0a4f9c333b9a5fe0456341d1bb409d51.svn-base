package org.gs4tr.termmanager.service.listeners;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
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

@Component("updateDescriptionEventListener")
public class UpdateDescriptionEventListener extends AbstractEventListener {

    @Override
    public void notify(EventMessage message) {
	Long projectId = message.getContextVariable(EventMessage.VARIABLE_PROJECT_ID);

	ProjectDetailInfo info = message.getContextVariable(EventMessage.VARIABLE_DETAIL_INFO);

	TermEntry termEntry = message.getContextVariable(EventMessage.VARIABLE_TERM_ENTRY);

	UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);

	updateDescription(projectId, info, termEntry, command);
    }

    @Override
    public boolean supports(EventMessage message) {
	boolean supports = false;
	if (message.getNotifyingMessageId().equals(EventMessage.EVENT_UPDATE_TERMENTRY)) {
	    UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);
	    TypeEnum typeEnum = command.getTypeEnum();
	    if (CommandEnum.UPDATE == command.getCommandEnum()
		    && (TypeEnum.NOTE == typeEnum || TypeEnum.DESCRIP == typeEnum)) {
		supports = true;
	    }
	}

	return supports;
    }

    public void updateDescription(Long projectId, ProjectDetailInfo info, TermEntry termEntry, UpdateCommand command) {

	String markerId = command.getMarkerId();
	String newValue = command.getValue();

	Set<Description> termEntryDescriptions = termEntry.getDescriptions();

	String userName = TmUserProfile.getCurrentUserName();
	final Date dateModified = new Date();

	if (CollectionUtils.isNotEmpty(termEntryDescriptions)) {
	    for (Description description : termEntryDescriptions) {
		if (markerId.equals(description.getUuid())) {
		    String type = description.getType();
		    String oldValue = description.getValue();
		    description.setValue(newValue);

		    termEntry.setDateModified(dateModified.getTime());
		    termEntry.setUserModified(userName);

		    info.setDateModified(dateModified);

		    if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(Messages.getString("UpdateDescriptionEventListener.0"), //$NON-NLS-1$
				type, oldValue, newValue));
		    }

		    return;
		}
	    }
	}

	List<Term> terms = termEntry.ggetTerms();
	if (CollectionUtils.isNotEmpty(terms)) {
	    for (Term term : terms) {
		Set<Description> termDescriptions = term.getDescriptions();
		if (CollectionUtils.isNotEmpty(termDescriptions)) {
		    for (Description description : termDescriptions) {
			if (markerId.equals(description.getUuid())) {

			    // TERII-5164
			    // this is only so that we know to increment update in statistics
			    command.setOldValue(description.getValue());

			    // Create term copy for duplicate term test
			    // Description is removed and added new with changed
			    // desc for shorten code

			    Term termCopy = new Term(term);
			    termCopy.getDescriptions().remove(description);
			    Description descCopy = new Description(description);
			    descCopy.setValue(newValue);
			    termCopy.addDescription(descCopy);
			    if (CollectionUtils.isNotEmpty(terms) && terms.contains(termCopy)) {
				throw new UserException(Messages.getString("UpdateDescriptionEventListener.2"),
					Messages.getString("UpdateDescriptionEventListener.3"));
			    }

			    String type = description.getType();
			    String oldValue = description.getValue();
			    description.setValue(newValue);

			    if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format(Messages.getString("UpdateDescriptionEventListener.1"), //$NON-NLS-1$
					type, oldValue, newValue));
			    }

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
}
