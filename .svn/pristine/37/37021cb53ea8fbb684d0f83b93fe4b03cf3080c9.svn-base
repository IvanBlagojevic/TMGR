package org.gs4tr.termmanager.service;

import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.termmanager.model.TmPowerUserProjectRole;
import org.springframework.security.access.annotation.Secured;

public interface PowerUserProjectRoleService {

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    TmPowerUserProjectRole findByUserId(Long userId);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void save(TmPowerUserProjectRole tmPowerUserProjectRole);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    boolean updatePowerUserProjectRoles(Role role, Long UserId);

}
