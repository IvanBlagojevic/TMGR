package org.gs4tr.termmanager.service.listeners;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.event.SubmissionDetailInfo;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.springframework.stereotype.Component;

@Component("sendToReviewEventListener")
public class SendToReviewEventListener extends AbstractEventListener {

    @Override
    public void notify(EventMessage message) {
	ProjectDetailInfo detailInfo = message.getContextVariable(EventMessage.VARIABLE_DETAIL_INFO);

	TermEntry regularTermEntry = message.getContextVariable(EventMessage.VARIABLE_TERM_ENTRY);

	TermEntry submissionTermEntry = message.getContextVariable(EventMessage.VARIABLE_SUBMISSION_TERM_ENTRY);

	Submission submission = message.getContextVariable(EventMessage.VARIABLE_SUBMISSION);

	SubmissionDetailInfo submissionInfo = message.getContextVariable(EventMessage.VARIABLE_SUBMISSION_INFO);

	Boolean reviewRequired = message.getContextVariable(EventMessage.VARIABLE_REVIEW_REQUIRED);

	UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);

	sendToReviewTerms(detailInfo, regularTermEntry, submissionTermEntry, submission, submissionInfo,
		command.getMarkerId(), command.getAsssignee(), reviewRequired, command.getTermId());
    }

    @Override
    public boolean supports(EventMessage message) {
	boolean supports = false;
	if (message.getNotifyingMessageId().equals(EventMessage.EVENT_UPDATE_TERMENTRY)) {
	    UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);
	    if (CommandEnum.REVIEW == command.getCommandEnum() && TypeEnum.TERM == command.getTypeEnum()) {
		supports = true;
	    }
	}

	return supports;
    }

    private void sendToReviewTerms(ProjectDetailInfo detailInfo, TermEntry regularTermEntry,
	    TermEntry submissionTermEntry, Submission submission, SubmissionDetailInfo submissionInfo, String markerId,
	    String assignee, Boolean reviewRequired, String termId) {
	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	if (submissionLanguages == null) {
	    submissionLanguages = new HashSet<>();
	    submission.setSubmissionLanguages(submissionLanguages);
	}

	Date date = new Date();
	long dateTime = date.getTime();
	String userName = TmUserProfile.getCurrentUserName();

	regularTermEntry.setDateModified(dateTime);
	regularTermEntry.setUserModified(userName);

	Map<String, Set<Term>> terms = regularTermEntry.getLanguageTerms();
	for (Map.Entry<String, Set<Term>> entry : terms.entrySet()) {
	    for (Term term : entry.getValue()) {
		if (markerId.equals(term.getUuId())) {
		    String languageId = term.getLanguageId();

		    String oldStatus = term.getStatus();

		    term.setStatusOld(oldStatus);
		    term.setStatus(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName());
		    term.setDateModified(dateTime);
		    term.setUserModified(userName);

		    detailInfo.setDateModified(date);

		    if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(Messages.getString("SendToReviewEventListener.0"), //$NON-NLS-1$
				term.getName(), term.getUuId(), submission.getName()));
		    }

		    SubmissionLanguage submissionLanguage = createOrGetSubmissionLanguage(languageId, assignee,
			    submissionLanguages, submission, detailInfo);

		    Term submissionTerm = term.cloneTerm();
		    submissionTerm.setUuId(termId);
		    submissionTerm.setLanguageId(submissionLanguage.getLanguageId());
		    submissionTerm.setAssignee(assignee);
		    submissionTerm.setReviewRequired(reviewRequired);
		    submissionTerm.setCanceled(Boolean.FALSE);
		    submissionTerm.setParentUuId(term.getUuId());
		    submissionTerm.setTempText(term.getName());
		    submissionTerm.setInTranslationAsSource(Boolean.FALSE);
		    submissionTerm.setFirst(term.isFirst());
		    submissionTerm.setDateSubmitted(dateTime);
		    submissionTerm.setUserLatestChange(null);

		    submissionTermEntry.addTerm(submissionTerm);

		    submissionInfo.incrementInTranslationCount(languageId);
		    submissionInfo.incrementTotalCount(languageId);

		    detailInfo.incrementTermInSubmissionCount(languageId);
		    if (oldStatus.equals(ItemStatusTypeHolder.PROCESSED.getName())) {
			detailInfo.decrementApprovedTermCount(languageId);
		    } else if (oldStatus.equals(ItemStatusTypeHolder.ON_HOLD.getName())) {
			detailInfo.decrementOnHoldTermCount(languageId);
		    } else if (oldStatus.equals(ItemStatusTypeHolder.WAITING.getName())) {
			detailInfo.decrementPendingTermCount(languageId);
		    }

		    createNewSubmissionUser(submission, assignee);

		    return;
		}
	    }
	}
    }
}
