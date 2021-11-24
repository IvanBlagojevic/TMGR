package org.gs4tr.termmanager.model.dto.converter;

import org.gs4tr.termmanager.model.dto.DtoSubmissionLanguageDetailView;
import org.gs4tr.termmanager.model.view.SubmissionLanguageDetailView;

public class SubmissionLanguageDetailViewConverter {

    public static DtoSubmissionLanguageDetailView fromInternalToDto(SubmissionLanguageDetailView internal) {
	if (internal == null) {
	    return null;
	}

	DtoSubmissionLanguageDetailView dtoEntity = new DtoSubmissionLanguageDetailView();
	dtoEntity.setAssignee(internal.getAssignee());
	dtoEntity.setDateCompleted(internal.getDateCompleted());
	dtoEntity.setDateModified(internal.getDateModified());
	dtoEntity.setDateSubmitted(internal.getDateSubmitted());
	dtoEntity.setLanguageId(internal.getLanguageId());
	dtoEntity.setStatus(ItemStatusTypeConverter.fromInternalToDto(internal.getStatus()));
	dtoEntity.setTicket(TicketConverter.fromInternalToDto(internal.getSubmissionLanguageId()));
	dtoEntity.setMarkerId(internal.getMarkerId());
	dtoEntity.setComments(
		SubmissionLanguageCommentConverter.fromInternalToDto(internal.getSubmissionLanguageComments()));
	dtoEntity.setTermCanceledCount(internal.getTermCanceledCount());
	dtoEntity.setTermCompletedCount(internal.getTermCompletedCount());
	dtoEntity.setTermCount(internal.getTermCount());
	dtoEntity.setTermInFinalReviewCount(internal.getTermInFinalReviewCount());
	dtoEntity.setTermInTranslationCount(internal.getTermInTranslationCount());
	dtoEntity.setCanceled(internal.isCanceled());

	return dtoEntity;
    }
}
