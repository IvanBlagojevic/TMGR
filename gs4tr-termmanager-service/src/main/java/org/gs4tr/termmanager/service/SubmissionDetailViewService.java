package org.gs4tr.termmanager.service;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.termmanager.model.search.command.SubmissionSearchRequest;
import org.gs4tr.termmanager.model.view.SubmissionDetailView;
import org.springframework.security.access.annotation.Secured;

public interface SubmissionDetailViewService {

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    TaskPagedList<SubmissionDetailView> search(SubmissionSearchRequest command, PagedListInfo info);
}
