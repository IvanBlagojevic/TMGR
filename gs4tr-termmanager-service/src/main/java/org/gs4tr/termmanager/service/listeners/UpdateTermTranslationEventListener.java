package org.gs4tr.termmanager.service.listeners;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.event.SubmissionDetailInfo;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Priority;
import org.gs4tr.termmanager.model.glossary.PriorityEnum;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.springframework.stereotype.Component;

@Component("updateTermTranslationEventListener")
public class UpdateTermTranslationEventListener extends AbstractEventListener {

    @Override
    public void notify(EventMessage message) {
	ProjectDetailInfo detailInfo = message.getContextVariable(EventMessage.VARIABLE_DETAIL_INFO);

	TermEntry termEntry = message.getContextVariable(EventMessage.VARIABLE_TERM_ENTRY);

	TermEntry submissionTermEntry = message.getContextVariable(EventMessage.VARIABLE_SUBMISSION_TERM_ENTRY);

	SubmissionDetailInfo submissionInfo = message.getContextVariable(EventMessage.VARIABLE_SUBMISSION_INFO);

	UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);

	updateTermTranslation(detailInfo, submissionInfo, submissionTermEntry, termEntry, command.getMarkerId(),
		command.getValue());
    }

    @Override
    public boolean supports(EventMessage message) {
	boolean supports = false;
	if (message.getNotifyingMessageId().equals(EventMessage.EVENT_UPDATE_TERMENTRY)) {
	    UpdateCommand command = message.getContextVariable(EventMessage.VARIABLE_COMMAND);
	    if (CommandEnum.UPDATE_TRANSLATION == command.getCommandEnum() && TypeEnum.TERM == command.getTypeEnum()) {
		supports = true;
	    }
	}

	return supports;
    }

    private void finishTranslation(ProjectDetailInfo detailInfo, String sourceLanguageId, Term submissionTerm,
	    Term regularTerm, String newValue, TermEntry termEntry) {

	updateTargetTerm(submissionTerm, regularTerm, newValue, ItemStatusTypeHolder.PROCESSED.getName(), termEntry);

	submissionTerm.setName(newValue);
	submissionTerm.setTempText(newValue);
	submissionTerm.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	submissionTerm.setInTranslationAsSource(Boolean.FALSE);
	submissionTerm.setPriority(new Priority(PriorityEnum.LOW.getValue(), PriorityEnum.LOW.getValue()));
	submissionTerm.setDateCompleted(new Date().getTime());

	String languageId = submissionTerm.getLanguageId();
	detailInfo.incrementApprovedTermCount(languageId);
	detailInfo.decrementTermInSubmissionCount(languageId);

    }

    private void updateRegularSourceTerm(String sourceLanguageId, TermEntry termEntry) {
	boolean allTermsTranslated = true;
	List<Term> terms = termEntry.ggetTerms();
	Set<Term> sourceTerms = termEntry.getLanguageTerms().get(sourceLanguageId);

	if (CollectionUtils.isNotEmpty(sourceTerms)) {
	    terms.removeAll(sourceTerms);
	}

	for (Term term : terms) {
	    if (ItemStatusTypeHolder.isTermInTranslation(term)) {
		allTermsTranslated = false;
		break;
	    }
	}
	if (allTermsTranslated) {
	    for (Term term : sourceTerms) {
		term.setInTranslationAsSource(Boolean.FALSE);
	    }
	}
    }

    private void updateSubmissionSourceTerm(String sourceLanguageId, TermEntry submissionTermEntry) {
	boolean allTermsTranslated = true;

	List<Term> terms = submissionTermEntry.ggetTerms();
	for (Term term : terms) {
	    if (ItemStatusTypeHolder.isTermInTranslation(term)) {
		allTermsTranslated = false;
		break;
	    }
	}
	Set<Term> sourceTerms = submissionTermEntry.getLanguageTerms().get(sourceLanguageId);

	if (allTermsTranslated) {
	    for (Term term : sourceTerms) {
		term.setInTranslationAsSource(Boolean.FALSE);

		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug(String.format(Messages.getString("AbstractNotifyingService.3"), //$NON-NLS-1$
			    term.getLanguageId(), term.getTermEntryId()));
		}
	    }
	}
    }

    private void updateTargetTerm(Term submissionTerm, Term regularTerm, String newValue, String status,
	    TermEntry termEntry) {
	long dateTime = new Date().getTime();
	String userName = TmUserProfile.getCurrentUserName();

	String oldValue = regularTerm.getName();
	regularTerm.setStatus(status);
	regularTerm.setDateModified(dateTime);
	regularTerm.setUserModified(userName);
	regularTerm.setName(newValue);
	regularTerm.setDescriptions(submissionTerm.getDescriptions());

	termEntry.setDateModified(dateTime);
	termEntry.setUserModified(userName);

	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("UpdateTermTranslationEventListener.0"), //$NON-NLS-1$
		    oldValue, regularTerm.getUuId(), newValue));
	}
    }

    private void updateTermTranslation(ProjectDetailInfo detailInfo, SubmissionDetailInfo submissionInfo,
	    TermEntry submissionTermEntry, TermEntry termEntry, String markerId, String newValue) {

	submissionTermEntry.ggetTermById(markerId);

	Term submissionTerm = submissionTermEntry.ggetTermById(markerId);

	if (submissionTerm != null) {
	    Term regularTerm = termEntry.ggetTermById(submissionTerm.getParentUuId());
	    String languageId = submissionTerm.getLanguageId();
	    String oldValue = submissionTerm.getName();
	    Boolean reviewRequired = submissionTerm.getReviewRequired();
	    if (reviewRequired) {
		submissionTerm.setTempText(newValue);
		submissionTerm.setDateModified(new Date().getTime());

		Priority priority = new Priority();
		priority.setAssigneePriority(PriorityEnum.NORMAL.getValue());
		priority.setSubmitterPriority(PriorityEnum.HIGH.getValue());
		submissionTerm.setPriority(priority);
		submissionTerm.setStatus(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName());
		submissionTerm.setDateCompleted(new Date().getTime());

		submissionInfo.incrementInFinalReviewCount(languageId);

		updateTargetTerm(submissionTerm, regularTerm, newValue, ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(),
			termEntry);
	    } else {
		String sourceLanguageId = submissionInfo.getSourceLanguageId();
		finishTranslation(detailInfo, sourceLanguageId, submissionTerm, regularTerm, newValue, termEntry);
		updateSubmissionSourceTerm(sourceLanguageId, submissionTermEntry);
		updateRegularSourceTerm(sourceLanguageId, termEntry);
		submissionInfo.incrementCompletedCount(languageId);
	    }
	    submissionInfo.decrementInTranslationCount(languageId);

	    submissionTerm.setCommited(Boolean.TRUE);

	    termEntry.setAction(Action.UPDATED_TRANSLATIONS);

	    /* Update language date modified */
	    detailInfo.getUpdatedLanguages().add(regularTerm.getLanguageId());

	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(String.format(Messages.getString("UpdateTermTranslationEventListener.1"), //$NON-NLS-1$
			oldValue, submissionTerm.getUuId(), newValue));
	    }
	    return;
	}
    }
}
