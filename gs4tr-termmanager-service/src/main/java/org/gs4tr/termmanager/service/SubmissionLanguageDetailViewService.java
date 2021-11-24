package org.gs4tr.termmanager.service;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.termmanager.model.search.command.SubmissionLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.SubmissionLanguageDetailView;
import org.springframework.security.access.annotation.Secured;

public interface SubmissionLanguageDetailViewService {

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    TaskPagedList<SubmissionLanguageDetailView> search(SubmissionLanguageDetailRequest command, PagedListInfo info);
}
