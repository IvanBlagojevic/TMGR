package org.gs4tr.termmanager.service;

import org.gs4tr.termmanager.model.ProjectDescription;
import org.springframework.security.access.annotation.Secured;

public interface ProjectDescriptionService {

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void deleteByProjectId(Long projectId);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    ProjectDescription findByProjectId(Long projectId);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void saveOrUpdateProjectDescription(ProjectDescription description);

}
