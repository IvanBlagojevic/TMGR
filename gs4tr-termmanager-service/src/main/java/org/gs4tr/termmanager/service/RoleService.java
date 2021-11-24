package org.gs4tr.termmanager.service;

import java.util.List;

import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.security.service.BaseRoleService;
import org.springframework.security.access.annotation.Secured;

public interface RoleService extends BaseRoleService {

    @Secured({ "POLICY_FOUNDATION_USERPROFILE_ADD", "POLICY_FOUNDATION_USERPROFILE_EDIT" })
    Long addOrUpdateProjectRoles(Long userId, Long projectId, List<Role> roles);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<Policy> findAllPolicies();

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    List<Role> getUserProjectRoles(Long userId, Long projectId);

}
