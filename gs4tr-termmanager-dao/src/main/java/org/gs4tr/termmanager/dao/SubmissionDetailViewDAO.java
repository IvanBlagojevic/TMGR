package org.gs4tr.termmanager.dao;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.model.search.command.SubmissionSearchRequest;
import org.gs4tr.termmanager.model.view.SubmissionDetailView;

public interface SubmissionDetailViewDAO {

    PagedList<SubmissionDetailView> getEntityPagedList(SubmissionSearchRequest command, PagedListInfo pagedListInfo);
}