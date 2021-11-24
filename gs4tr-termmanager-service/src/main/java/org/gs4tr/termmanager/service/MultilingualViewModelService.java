package org.gs4tr.termmanager.service;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.termmanager.model.MultilingualTerm;
import org.gs4tr.termmanager.model.TermSearchRequest;
import org.springframework.security.access.annotation.Secured;

public interface MultilingualViewModelService {

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    TaskPagedList<MultilingualTerm> search(TermSearchRequest command, PagedListInfo pagedListInfo);
}
