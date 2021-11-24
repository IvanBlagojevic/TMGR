package org.gs4tr.termmanager.model.dto.converter;

import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.dto.DtoProjectDetailView;
import org.gs4tr.termmanager.model.view.ProjectDetailView;

public class ProjectDetailViewConverter {

    public static DtoProjectDetailView fromInternalToDto(ProjectDetailView internal) {
	if (internal == null) {
	    return null;
	}

	DtoProjectDetailView dtoEntity = new DtoProjectDetailView();
	dtoEntity.setActiveSubmissionCount(internal.getActiveSubmissionCount());
	dtoEntity.setApprovedTermCount(internal.getApprovedTermCount());
	dtoEntity.setPendingApprovalTermCount(internal.getPendingApprovalTermCount());
	dtoEntity.setOnHoldTermCount(internal.getOnHoldTermCount());
	dtoEntity.setCompletedSubmissionCount(internal.getCompletedSubmissionCount());
	dtoEntity.setDateModified(internal.getDateModified());
	dtoEntity.setForbiddenTermCount(internal.getForbiddenTermCount());
	dtoEntity.setLanguageCount(internal.getLanguageCount());
	dtoEntity.setTermInSubmissionCount(internal.getTermInSubmissionCount());
	dtoEntity.setTermCount(internal.getTermCount());
	dtoEntity.setTermEntryCount(internal.getTermEntryCount());
	dtoEntity.setName(internal.getName());
	dtoEntity.setShortCode(internal.getShortCode());
	dtoEntity.setTicket(IdEncrypter.encryptGenericId(internal.getProjectDetailViewId()));
	dtoEntity.setAvailableDescription(internal.isAvailableDescription());
	dtoEntity.setProjectTicket(IdEncrypter.encryptGenericId(internal.getProjectId()));

	return dtoEntity;
    }
}
