package org.gs4tr.termmanager.service;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.search.command.ProjectLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.ProjectLanguageDetailView;
import org.springframework.security.access.annotation.Secured;

public interface ProjectLanguageDetailService {

    void cloneProjectLangDetail(ProjectLanguageDetail projectLanguageDetail, String languageTo, Long projectId,
	    int dummyTermsCount);

    ProjectLanguageDetail findProjectLangDetailByLangId(Long projectId, String languageId);

    void recodeProjectLanguageDetail(Long projectId, String languageFrom, String languageTo);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    TaskPagedList<ProjectLanguageDetailView> search(ProjectLanguageDetailRequest command, PagedListInfo pagedListInfo);
}