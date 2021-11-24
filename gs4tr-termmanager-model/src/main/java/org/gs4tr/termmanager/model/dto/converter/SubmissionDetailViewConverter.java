package org.gs4tr.termmanager.model.dto.converter;

import org.gs4tr.termmanager.model.dto.DtoSubmissionDetailView;
import org.gs4tr.termmanager.model.view.SubmissionDetailView;

public class SubmissionDetailViewConverter {

    public static DtoSubmissionDetailView fromInternalToDto(SubmissionDetailView internal) {
	if (internal == null) {
	    return null;
	}

	DtoSubmissionDetailView dtoEntity = new DtoSubmissionDetailView();
	dtoEntity.setAvailableTasks(internal.getAvailableTasks());
	dtoEntity.setAssignees(internal.getAssignee());
	dtoEntity.setDateCompleted(internal.getDateCompleted());
	dtoEntity.setDateModified(internal.getDateModified());
	dtoEntity.setDateSubmitted(internal.getDateSubmitted());
	dtoEntity.setProjectName(internal.getProjectName());
	dtoEntity.setProjectTicket(TicketConverter.fromInternalToDto(internal.getProjectId()));
	dtoEntity.setSourceLanguageId(internal.getSourceLanguageId());
	dtoEntity.setStatus(ItemStatusTypeConverter.fromInternalToDto(internal.getStatus()));
	dtoEntity.setSubmissionId(internal.getSubmissionId());
	dtoEntity.setSubmissionName(internal.getSubmissionName());
	dtoEntity.setSubmitter(internal.getSubmitter());
	dtoEntity.setTargetLanguageIds(internal.getTargetLanguageIds());
	dtoEntity.setTermEntryCount(internal.getTermEntryCount());
	dtoEntity.setTicket(TicketConverter.fromInternalToDto(internal.getSubmissionId()));
	dtoEntity.setMarkerId(internal.getMarkerId());
	dtoEntity.setCanceled(internal.isCanceled());

	return dtoEntity;
    }
}
