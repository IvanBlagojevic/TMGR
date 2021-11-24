package org.gs4tr.termmanager.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.termmanager.dao.PowerUserProjectRoleDAO;
import org.gs4tr.termmanager.model.TmPowerUserProjectRole;
import org.gs4tr.termmanager.service.PowerUserProjectRoleService;
import org.gs4tr.termmanager.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PowerUserProjectRoleServiceImpl implements PowerUserProjectRoleService {

    private static final Log LOGGER = LogFactory.getLog(PowerUserProjectRoleServiceImpl.class);

    @Autowired
    private PowerUserProjectRoleDAO _powerUserProjectRoleDAO;

    @Autowired
    private RoleService _roleService;

    @Override
    @Transactional(readOnly = true)
    public TmPowerUserProjectRole findByUserId(Long userId) {
	return getPowerUserProjectRoleDAO().findByUserId(userId);
    }

    public RoleService getRoleService() {
	return _roleService;
    }

    @Override
    @Transactional
    public void save(TmPowerUserProjectRole tmPowerUserProjectRole) {
	Long userProfileId = tmPowerUserProjectRole.getUserProfile().getUserProfileId();
	String roleId = tmPowerUserProjectRole.getRole().getRoleId();

	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("PowerUserProjectRoleServiceImpl.0"), //$NON-NLS-1$
		    userProfileId, roleId));
	}
	getPowerUserProjectRoleDAO().save(tmPowerUserProjectRole);
    }

    @Override
    @Transactional
    public boolean updatePowerUserProjectRoles(Role role, Long userId) {
	String roleId = role.getRoleId();

	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("PowerUserProjectRoleServiceImpl.1"), //$NON-NLS-1$
		    roleId, userId));
	}
	return getPowerUserProjectRoleDAO().updatePowerUserProjectRole(role.getRoleId(), userId);
    }

    private PowerUserProjectRoleDAO getPowerUserProjectRoleDAO() {
	return _powerUserProjectRoleDAO;
    }

}
