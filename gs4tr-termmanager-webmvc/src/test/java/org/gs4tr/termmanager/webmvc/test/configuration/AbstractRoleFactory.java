package org.gs4tr.termmanager.webmvc.test.configuration;

import java.util.List;
import java.util.Set;

import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.security.model.RoleTypeEnum;

public abstract class AbstractRoleFactory {

    public static AbstractRoleFactory newRoleFactory(RoleNameEnum roleName)
	    throws InstantiationException, IllegalAccessException {
	return (AbstractRoleFactory) roleName.getFactoryClazz().newInstance();
    }

    public final Policy createPolicy(RoleTypeEnum policyType, String category, String policyId) {
	final Policy policy = new Policy();
	policy.setPolicyType(policyType);
	policy.setCategory(category);
	policy.setPolicyId(policyId);
	return policy;
    }

    public abstract List<Role> createProjectRoles();

    public abstract Set<Role> createSystemRoles();
}