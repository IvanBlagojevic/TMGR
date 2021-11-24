package org.gs4tr.termmanager.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.dto.ProjectDetailCountsIO;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.search.command.UserProjectSearchRequest;
import org.gs4tr.termmanager.model.view.ProjectReport;

public interface ProjectDetailDAO extends GenericDao<ProjectDetail, Long> {

    ProjectDetail findByProjectId(final Long projectId, final Class<?>... classesToFetch);

    List<ProjectReport> getAllProjectsReport(boolean groupByLanguages, boolean isPowerUser, Set<Long> projectIds,
	    Set<String> languageIds);

    boolean incrementalUpdateProjectDetail(ProjectDetailInfo info);

    boolean incrementalUpdateProjectDetail(ProjectDetailCountsIO counts);

    List<ProjectDetail> searchProjectDetails(UserProjectSearchRequest searchRequest, PagedListInfo pagedListInfo);

    void updateDateModifiedByProjectId(Long projectId, Date newDateModified);

    void updateProjectAndLanguagesDateModifiedByProjectId(Long projectId, Set<String> languages, Date newDateModified);

    boolean updateProjectDetail(ProjectDetailInfo info);

    boolean updateProjectDetail(ProjectDetailCountsIO counts);
}
