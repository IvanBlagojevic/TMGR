package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.dto.DtoProjectLanguageDetailView;
import org.gs4tr.termmanager.model.view.ProjectLanguageDetailView;

public class ProjectLanguageDetailViewConverter {

    public static DtoProjectLanguageDetailView[] fromInternalToDto(List<ProjectLanguageDetailView> internal) {
	List<DtoProjectLanguageDetailView> dtoList = new ArrayList<DtoProjectLanguageDetailView>();

	if (CollectionUtils.isNotEmpty(internal)) {
	    for (ProjectLanguageDetailView intrnalItem : internal) {
		dtoList.add(fromInternalToDto(intrnalItem));
	    }
	}

	return dtoList.toArray(new DtoProjectLanguageDetailView[dtoList.size()]);
    }

    public static DtoProjectLanguageDetailView fromInternalToDto(ProjectLanguageDetailView internal) {
	DtoProjectLanguageDetailView dto = new DtoProjectLanguageDetailView();
	dto.setActiveSubmissionCount(internal.getActiveSubmissionCount());
	dto.setApprovedTermCount(internal.getApprovedTermCount());
	dto.setPendingApprovalTermCount(internal.getPendingApprovalTermCount());
	dto.setOnHoldTermCount(internal.getOnHoldTermCount());
	dto.setCompletedSubmissionCount(internal.getCompletedSubmissionCount());
	dto.setDateModified(internal.getDateModified());
	dto.setForbiddenTermCount(internal.getForbiddenTermCount());
	dto.setLanguage(LanguageConverter.fromInternalToDto(internal.getLanguage()));
	dto.setTermCount(internal.getTermCount());
	dto.setTermInSubmissionCount(internal.getTermInSubmissionCount());
	dto.setTicket(IdEncrypter.encryptGenericId(internal.getProjectLanguageDetailViewId()));
	return dto;
    }
}
