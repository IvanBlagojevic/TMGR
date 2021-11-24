package org.gs4tr.termmanager.service.listeners;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.springframework.stereotype.Component;

@Component("updateSubmissionDescriptionEventListener")
public class UpdateSubmissionDescriptionEventListener extends AbstractEventListener {

    @Override
    public void notify(EventMessage message) {
	Submission submission = message.getContextVariable(EventMessage.VARIABLE_SUBMISSION);

	UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);

	updateDescription(submission, command);
    }

    @Override
    public boolean supports(EventMessage message) {
	boolean supports = false;
	if (message.getNotifyingMessageId().equals(EventMessage.EVENT_UPDATE_TERMENTRY)) {
	    UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);
	    TypeEnum typeEnum = command.getTypeEnum();
	    if (CommandEnum.UPDATE_TRANSLATION == command.getCommandEnum()
		    && (TypeEnum.NOTE == typeEnum || TypeEnum.DESCRIP == typeEnum)) {
		supports = true;
	    }
	}

	return supports;
    }

    private boolean isDescription(UpdateCommand command, Description description) {
	boolean isSameType = description.getType().equals(command.getType());
	boolean isSameValue = description.getValue().equals(command.getValue());
	return isSameType && isSameValue;
    }

    private void updateDescription(Submission submission, UpdateCommand command) {
	Long projectId = submission.getProject().getProjectId();

	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	if (submissionLanguages == null) {
	    submissionLanguages = new HashSet<>();
	    submission.setSubmissionLanguages(submissionLanguages);
	}

	Term submissionTerm = getSubmissionTermService().findById(command.getParentMarkerId(), projectId);
	if (submissionTerm == null) {
	    return;
	}

	Set<Description> descriptions = submissionTerm.getDescriptions();
	if (CollectionUtils.isNotEmpty(descriptions)) {
	    for (Description description : descriptions) {
		if (isDescription(command, description)) {

		    String type = description.getType();
		    String oldValue = description.getValue();
		    description.setValue(command.getValue());
		    if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(Messages.getString("UpdateSubmissionDescriptionEventListener.0"), //$NON-NLS-1$
				submissionTerm.getUuId(), type, oldValue, command.getValue()));
		    }

		    return;
		}
	    }
	}
    }
}
