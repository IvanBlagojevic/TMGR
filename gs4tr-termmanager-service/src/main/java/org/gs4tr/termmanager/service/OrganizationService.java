package org.gs4tr.termmanager.service;

import java.util.List;
import java.util.Set;

import org.gs4tr.foundation.modules.organization.service.BaseOrganizationService;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.springframework.security.access.annotation.Secured;

public interface OrganizationService extends BaseOrganizationService<TmOrganization> {

    @Secured({ "POLICY_FOUNDATION_PROJECT_ADD", "POLICY_FOUNDATION_PROJECT_EDIT" })
    Long addProjectToOrganization(Long organizationId, Long projectId);

    @Secured({ "POLICY_FOUNDATION_PROJECT_ADD", "POLICY_FOUNDATION_PROJECT_EDIT" })
    Long assignOrganizationToUser(Long organizationId, Long userId);

    @Secured({ "POLICY_FOUNDATION_PROJECT_ADD", "POLICY_FOUNDATION_PROJECT_EDIT" })
    Long assignProjectsToOrganization(Long organizationId, Long[] projectIds);

    @Secured("POLICY_FOUNDATION_ORGANIZATION_ENABLEDISABLE")
    void enableOrganizationUsers(Long organizationId, boolean enable);

    @Secured({ "POLICY_FOUNDATION_PROJECT_ADD", "POLICY_FOUNDATION_PROJECT_EDIT", "POLICY_FOUNDATION_PROJECT_VIEW" })
    List<TmOrganization> findAllEnabledOrganizations();

    @Secured({ "POLICY_FOUNDATION_ORGANIZATION_VIEW", "POLICY_FOUNDATION_ORGANIZATION_EDIT",
	    "POLICY_FOUNDATION_ORGANIZATION_ADD" })
    List<TmOrganization> findAllWithDependants();

    @Secured({ "POLICY_FOUNDATION_ORGANIZATION_VIEW", "POLICY_FOUNDATION_ORGANIZATION_EDIT",
	    "POLICY_FOUNDATION_ORGANIZATION_ADD" })
    TmOrganization findByName(String organizationName);

    @Secured({ "POLICY_FOUNDATION_ORGANIZATION_VIEW", "POLICY_FOUNDATION_ORGANIZATION_EDIT",
	    "POLICY_FOUNDATION_ORGANIZATION_ADD" })
    List<TmUserProfile> getAllChildrenOrganizationUsers(Long organizationId);

    @Secured({ "POLICY_FOUNDATION_ORGANIZATION_VIEW", "POLICY_FOUNDATION_ORGANIZATION_EDIT",
	    "POLICY_FOUNDATION_ORGANIZATION_ADD" })
    List<TmUserProfile> getAllParentOrganizationUsers(Long organizationId);

    @Secured({ "POLICY_FOUNDATION_USERPROFILE_ADD", "POLICY_FOUNDATION_USERPROFILE_EDIT",
	    "POLICY_FOUNDATION_USERPROFILE_VIEW" })
    Long getOrganizationIdByUserId(Long userId);

    @Secured({ "POLICY_FOUNDATION_PROJECT_ADD", "POLICY_FOUNDATION_PROJECT_EDIT", "POLICY_FOUNDATION_PROJECT_VIEW" })
    String getOrganizationNameByUserId(Long userId);

    @Secured({ "POLICY_FOUNDATION_PROJECT_ADD", "POLICY_FOUNDATION_PROJECT_EDIT", "POLICY_FOUNDATION_PROJECT_VIEW" })
    Set<TmProject> getOrganizationProjects(Long projectId);
}
