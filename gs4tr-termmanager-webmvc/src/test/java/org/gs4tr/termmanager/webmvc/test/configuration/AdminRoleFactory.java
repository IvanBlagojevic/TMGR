package org.gs4tr.termmanager.webmvc.test.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.security.model.RoleTypeEnum;
import org.gs4tr.foundation.modules.security.model.SecurityPolicyEnum;

import static com.google.common.collect.Sets.newHashSet;

public class AdminRoleFactory extends AbstractRoleFactory {

    private static final String ADMIN = RoleNameEnum.ADMIN.name();

    @Override
    public List<Role> createProjectRoles() {
	return new ArrayList<Role>(0);
    }

    @Override
    public Set<Role> createSystemRoles() {
	Role adminRole = new Role();
	adminRole.setRoleType(RoleTypeEnum.SYSTEM);
	adminRole.setRoleId(ADMIN.toLowerCase());
	adminRole.setGeneric(Boolean.TRUE);
	adminRole.setPolicies(createSystemPolicies());
	return newHashSet(adminRole);
    }

    private Set<Policy> createSystemPolicies() {
	Policy roleEditPolicy = createPolicy(RoleTypeEnum.SYSTEM, "Role",
		SecurityPolicyEnum.POLICY_FOUNDATION_SECURITY_ROLE_EDIT.name());
	Policy roleViewPolicy = createPolicy(RoleTypeEnum.SYSTEM, "Role",
		SecurityPolicyEnum.POLICY_FOUNDATION_SECURITY_ROLE_VIEW.name());
	Policy roleAddPolicy = createPolicy(RoleTypeEnum.SYSTEM, "Role",
		SecurityPolicyEnum.POLICY_FOUNDATION_SECURITY_ROLE_ADD.name());

	Policy organizationAddPolicy = createPolicy(RoleTypeEnum.SYSTEM, "Organization",
		"POLICY_FOUNDATION_ORGANIZATION_ADD");
	Policy organizationEditPolicy = createPolicy(RoleTypeEnum.SYSTEM, "Organization",
		"POLICY_FOUNDATION_ORGANIZATION_EDIT");
	Policy organizationViewPolicy = createPolicy(RoleTypeEnum.SYSTEM, "Organization",
		"POLICY_FOUNDATION_ORGANIZATION_VIEW");
	Policy enableDisableOrganizationPolicy = createPolicy(RoleTypeEnum.SYSTEM, "Organization",
		"POLICY_FOUNDATION_ORGANIZATION_ENABLEDISABLE");

	Policy projectAddPolicy = createPolicy(RoleTypeEnum.SYSTEM, "Project", "POLICY_FOUNDATION_PROJECT_ADD");
	Policy projectEditPolicy = createPolicy(RoleTypeEnum.SYSTEM, "Project", "POLICY_FOUNDATION_PROJECT_EDIT");
	Policy enableDisableProjectPolicy = createPolicy(RoleTypeEnum.SYSTEM, "Project",
		"POLICY_FOUNDATION_PROJECT_ENABLEDISABLE");
	Policy projectViewPolicy = createPolicy(RoleTypeEnum.SYSTEM, "Project", "POLICY_FOUNDATION_PROJECT_VIEW");
	Policy sendConnectionPolicy = createPolicy(RoleTypeEnum.SYSTEM, "Project", "POLICY_TM_PROJECT_SEND_CONNECTION");

	Policy userProfileAddPolicy = createPolicy(RoleTypeEnum.SYSTEM, "User", "POLICY_FOUNDATION_USERPROFILE_ADD");
	Policy enableDisableUserPolicy = createPolicy(RoleTypeEnum.SYSTEM, "Organization",
		"POLICY_FOUNDATION_USERPROFILE_ENABLEDISABLE");
	Policy userProfileEditPolicy = createPolicy(RoleTypeEnum.SYSTEM, "User", "POLICY_FOUNDATION_USERPROFILE_EDIT");
	Policy nonExpiringUserPolicy = createPolicy(RoleTypeEnum.SYSTEM, "User",
		"POLICY_FOUNDATION_USERPROFILE_NON_EXPIRING_USER");
	Policy userProfileViewPolicy = createPolicy(RoleTypeEnum.SYSTEM, "User", "POLICY_FOUNDATION_USERPROFILE_VIEW");

	return newHashSet(roleEditPolicy, roleViewPolicy, roleAddPolicy, projectAddPolicy, projectEditPolicy,
		enableDisableProjectPolicy, projectViewPolicy, sendConnectionPolicy, organizationAddPolicy,
		organizationEditPolicy, enableDisableOrganizationPolicy, organizationViewPolicy, userProfileAddPolicy,
		userProfileEditPolicy, userProfileViewPolicy, enableDisableUserPolicy, nonExpiringUserPolicy);
    }
}