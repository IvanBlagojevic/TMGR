package org.gs4tr.termmanager.service.listeners;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.gs4tr.foundation.modules.entities.model.StringConstants;
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
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Priority;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.springframework.stereotype.Component;

@Component("sendToTranslationEventListener")
public class SendToTranslationEventListener extends AbstractEventListener {

    @Override
    public void notify(EventMessage message) {
	ProjectDetailInfo detailInfo = message.getContextVariable(EventMessage.VARIABLE_DETAIL_INFO);

	TermEntry termEntry = message.getContextVariable(EventMessage.VARIABLE_TERM_ENTRY);

	TermEntry submissionTermEntry = message.getContextVariable(EventMessage.VARIABLE_SUBMISSION_TERM_ENTRY);

	Submission submission = message.getContextVariable(EventMessage.VARIABLE_SUBMISSION);

	SubmissionDetailInfo submissionInfo = message.getContextVariable(EventMessage.VARIABLE_SUBMISSION_INFO);

	Boolean reviewRequired = message.getContextVariable(EventMessage.VARIABLE_REVIEW_REQUIRED);

	UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);

	sendToTranslationTerms(detailInfo, termEntry, submissionTermEntry, submission, submissionInfo,
		command.getMarkerId(), command.getLanguageId(), command.getAsssignee(), reviewRequired,
		command.getTermId());
    }

    @Override
    public boolean supports(EventMessage message) {
	boolean supports = false;
	if (message.getNotifyingMessageId().equals(EventMessage.EVENT_UPDATE_TERMENTRY)) {
	    UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);
	    if (CommandEnum.TRANSLATE == command.getCommandEnum() && TypeEnum.TERM == command.getTypeEnum()) {
		supports = true;
	    }
	}

	return supports;
    }

    private Term createSubmissionTerm(TermEntry submissionTermEntry, Submission submission, Term term, String termId) {
	long date = new Date().getTime();

	termId = termId != null ? termId : UUID.randomUUID().toString();

	Term submissionTerm = new Term();
	submissionTerm.setUuId(termId);
	submissionTerm.setLanguageId(term.getLanguageId());
	submissionTerm.setParentUuId(term.getUuId());
	submissionTerm.setName(term.getName());
	submissionTerm.setTempText(term.getName());
	submissionTerm.setForbidden(term.isForbidden());
	submissionTerm.setCanceled(Boolean.FALSE);
	submissionTerm.setDateSubmitted(date);
	submissionTerm.setDateModified(date);
	submissionTerm.setDateCreated(date);
	submissionTerm.setPriority(new Priority());
	submissionTerm.setCommited(Boolean.TRUE);
	submissionTerm.setInTranslationAsSource(Boolean.FALSE);
	submissionTerm.setTermEntryId(submissionTermEntry.getUuId());
	submissionTerm.setDescriptions(term.getDescriptions());
	String inTranslationReview = ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName();
	submissionTerm.setStatus(inTranslationReview);
	submissionTerm.setStatusOld(inTranslationReview);
	return submissionTerm;
    }

    private void sendToTranslationTerms(ProjectDetailInfo detailInfo, TermEntry termEntry,
	    TermEntry submissionTermEntry, Submission submission, SubmissionDetailInfo submissionInfo, String markerId,
	    String languageId, String assignee, Boolean reviewRequired, String termId) {

	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	if (submissionLanguages == null) {
	    submissionLanguages = new HashSet<>();
	    submission.setSubmissionLanguages(submissionLanguages);
	}

	createOrGetSubmissionLanguage(languageId, assignee, submissionLanguages, submission, detailInfo);

	Long projectId = submission.getProject().getProjectId();
	String termName = StringConstants.EMPTY;
	String status = ItemStatusTypeHolder.MISSING_TRANSLATION.getName();
	String userName = TmUserProfile.getCurrentUserName();
	Date date = new Date();
	long dateTime = date.getTime();

	Term term = new Term();
	term.setUuId(markerId);
	term.setName(termName);
	term.setProjectId(projectId);
	term.setTermEntryId(termEntry.getUuId());
	term.setLanguageId(languageId);
	term.setDateCreated(dateTime);
	term.setDateModified(dateTime);
	term.setStatus(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName());
	term.setStatusOld(status);
	term.setUserModified(userName);
	term.setUserCreated(userName);

	termEntry.setDateModified(dateTime);
	termEntry.setUserModified(userName);
	detailInfo.setDateModified(date);

	termEntry.addTerm(term);
	termEntry.setAction(Action.SENT_TO_TRANSLATION);

	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("SendToTranslationEventListener.0"), //$NON-NLS-1$
		    term.getUuId(), submission.getName()));
	}

	Term submissionTerm = createSubmissionTerm(submissionTermEntry, submission, term, termId);
	submissionTerm.setAssignee(assignee);
	submissionTerm.setReviewRequired(reviewRequired);

	submissionTermEntry.addTerm(submissionTerm);

	submissionInfo.incrementInTranslationCount(languageId);
	submissionInfo.incrementTotalCount(languageId);

	detailInfo.incrementTermCount(languageId);
	detailInfo.incrementTermInSubmissionCount(languageId);

	createNewSubmissionUser(submission, assignee);
    }
}
