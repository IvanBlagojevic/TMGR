package org.gs4tr.termmanager.dao;

import java.util.List;

import org.gs4tr.foundation.modules.organization.dao.BaseOrganizationDAO;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;

public interface OrganizationDAO extends BaseOrganizationDAO<TmOrganization> {

    void enableOrganizationUsers(final Long organizationId, final boolean enabled);

    List<TmOrganization> findAllWithDependants();

    List<TmProject> findOrganizationProjects(Long organizationId);

    Long getOrganizationIdByUserId(Long userId);

    String getOrganizationNameByUserId(Long userId);
}
