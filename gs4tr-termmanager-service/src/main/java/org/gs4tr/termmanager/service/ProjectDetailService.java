package org.gs4tr.termmanager.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.search.command.UserProjectSearchRequest;
import org.gs4tr.termmanager.model.view.ProjectDetailView;
import org.gs4tr.termmanager.model.view.ProjectReport;
import org.springframework.security.access.annotation.Secured;

public interface ProjectDetailService {

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    ProjectDetail findByProjectId(Long projectId, final Class<?>... classesToFetch);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<ProjectReport> getAllProjectsReport(boolean groupByLanguages);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    TaskPagedList<ProjectDetailView> search(UserProjectSearchRequest command, PagedListInfo info);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void updateDateModifiedByProjectId(Long projectId, Date dateModified);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void updateProjectAndLanguagesDateModified(Long projectId, Set<String> languages, Date newDateModified);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void incrementUpdateProjectDetail(ProjectDetailInfo info);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void updateProjectDetailOnImport(Long projectId, Map<String, Long> languageDateModified, Long dateModified);
}
