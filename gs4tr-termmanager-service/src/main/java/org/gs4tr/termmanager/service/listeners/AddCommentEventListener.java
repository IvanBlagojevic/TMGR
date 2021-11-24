package org.gs4tr.termmanager.service.listeners;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.SubmissionLanguageComment;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.glossary.Comment;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("addCommentEventListener")
public class AddCommentEventListener extends AbstractEventListener {

    @Autowired
    private SubmissionService _submissionService;

    @Override
    public void notify(EventMessage message) {
	Submission submission = message.getContextVariable(EventMessage.VARIABLE_SUBMISSION);
	UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);
	TermEntry subTermEntry = message.getContextVariable(EventMessage.VARIABLE_SUBMISSION_TERM_ENTRY);
	addComment(submission, subTermEntry, command.getParentMarkerId(), command.getMarkerId(), command.getValue(),
		command.getTermId());
    }

    @Override
    public boolean supports(EventMessage message) {
	boolean supports = false;
	String messageId = message.getNotifyingMessageId();
	if (messageId.equals(EventMessage.EVENT_ADD_COMMENT)
		|| (messageId.equals(EventMessage.EVENT_UPDATE_TERMENTRY))) {
	    UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);
	    CommandEnum commandEnum = command.getCommandEnum();
	    if ((CommandEnum.REVIEW == commandEnum || CommandEnum.TRANSLATE == commandEnum)
		    && TypeEnum.COMMENT == command.getTypeEnum()) {
		supports = true;
	    }
	}

	return supports;
    }

    private void addComment(Submission submission, TermEntry subTermEntry, String parentMarkerId, String markerId,
	    String value, String termId) {
	Long projectId = submission.getProject().getProjectId();

	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();

	if (CollectionUtils.isNotEmpty(submissionLanguages)) {
	    if (parentMarkerId.equals(submission.getMarkerId())) {
		for (SubmissionLanguage submissionLanguage : submissionLanguages) {
		    addSubmissionLanguageComment(submission, markerId, value, submissionLanguage);
		}
		return;
	    }

	    for (SubmissionLanguage submissionLanguage : submissionLanguages) {
		String subLanguageMarkerId = submissionLanguage.getMarkerId();
		if (parentMarkerId.equals(subLanguageMarkerId)) {
		    addSubmissionLanguageComment(submission, markerId, value, submissionLanguage);
		    return;
		}
	    }
	}

	if (subTermEntry == null) {
	    subTermEntry = getSubmissionTermService().findTermEntryByTermId(parentMarkerId, projectId);
	}

	if (subTermEntry == null) {
	    return;
	}

	Term subTerm = subTermEntry.ggetTermById(parentMarkerId);

	if (parentMarkerId.equals(subTerm.getUuId())) {
	    Comment termComment = createTermComment(markerId, value, subTerm);
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(String.format(Messages.getString("AddCommentEventListener.1"), //$NON-NLS-1$
			subTerm.getUuId(), termComment.getText()));
	    }

	    subTerm.addComment(termComment);

	    getTermEntryService().updateSubmissionTermEntries(projectId, Arrays.asList(subTermEntry));

	    return;
	}
    }

    private void addSubmissionLanguageComment(Submission submission, String markerId, String value,
	    SubmissionLanguage submissionLanguage) {
	Set<SubmissionLanguageComment> comments = submissionLanguage.getSubmissionLanguageComments();
	if (comments == null) {
	    comments = new HashSet<SubmissionLanguageComment>();
	    submissionLanguage.setSubmissionLanguageComments(comments);
	}

	SubmissionLanguageComment comment = createSubmissionLanguageComment(markerId, value, submissionLanguage);

	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("AddCommentEventListener.0"), //$NON-NLS-1$
		    submission.getSubmissionId(), submissionLanguage.getLanguageId(), comment.getText()));
	}
	comments.add(comment);

	getSubmissionService().saveOrUpdateSubmissionLanguage(submissionLanguage);
    }

    private SubmissionLanguageComment createSubmissionLanguageComment(String markerId, String value,
	    SubmissionLanguage submissionLanguage) {
	SubmissionLanguageComment comment = new SubmissionLanguageComment();
	comment.setMarkerId(UUID.randomUUID().toString());
	comment.setText(value);
	comment.setSubmissionLanguage(submissionLanguage);
	comment.setUser(TmUserProfile.getCurrentUserName());

	return comment;
    }

    private Comment createTermComment(String markerId, String value, Term subTerm) {
	Comment termComment = new Comment();
	termComment.setText(value);
	termComment.setUser(TmUserProfile.getCurrentUserName());
	return termComment;
    }

    private SubmissionService getSubmissionService() {
	return _submissionService;
    }
}
