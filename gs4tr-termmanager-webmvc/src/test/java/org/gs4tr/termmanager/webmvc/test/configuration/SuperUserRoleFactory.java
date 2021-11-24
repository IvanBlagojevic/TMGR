package org.gs4tr.termmanager.webmvc.test.configuration;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.security.model.RoleTypeEnum;

public class SuperUserRoleFactory extends AbstractRoleFactory {

    private static final String SYSTEM_SUPER_USER = RoleNameEnum.SYSTEM_SUPER_USER.name();

    @Override
    public List<Role> createProjectRoles() {
	Role super_user = new Role();
	super_user.setRoleId(getProjectRoleId());
	super_user.setRoleType(RoleTypeEnum.CONTEXT);
	super_user.setGeneric(Boolean.TRUE);
	super_user.setPolicies(createProjectPolicies());
	return Arrays.asList(super_user);
    }

    @Override
    public Set<Role> createSystemRoles() {
	Role system_super_user = new Role();
	system_super_user.setRoleType(RoleTypeEnum.SYSTEM);
	system_super_user.setGeneric(Boolean.FALSE);
	system_super_user.setRoleId(SYSTEM_SUPER_USER.toLowerCase());
	system_super_user.setPolicies(createSystemPolicies());
	return newHashSet(system_super_user);
    }

    private Set<Policy> createProjectPolicies() {
	Policy policy_1 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_READ");

	Policy policy_2 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_CANCEL_TRANSLATION");
	Policy policy_3 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_ADD_COMMENT");
	Policy policy_4 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERMENTRY_IMPORT");
	Policy policy_5 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERM_VIEW_TERM_HISTORY_USERS");

	Policy policy_6 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_PROJECT_REPORT");
	Policy policy_7 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERM_APPROVE_TERM_STATUS");
	Policy policy_8 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERMENTRY_FORBID_TERMENTRY");
	Policy policy_9 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERMENTRY_ASSIGN_ATTRIBUTES");

	Policy policy_10 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERM_AUTO_SAVE_TRANSLATION");
	Policy policy_11 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERM_COMMIT_TRANSLATION_CHANGES");
	Policy policy_12 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERM_DEMOTE_TERM_STATUS");
	Policy policy_13 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERM_UNDO_TRANSLATION_CHANGES");

	Policy policy_14 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_SEND_TO_TRANSLATION_REVIEW");
	Policy policy_15 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERM_ADD_PENDING_TERM");
	Policy policy_16 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERM_VIEW_TERM_HISTORY");
	Policy policy_17 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERMENTRY_EXPORT");

	Policy policy_18 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERM_ADD_APPROVED_TERM");
	Policy policy_19 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERM_DISABLE_TERM");

	Policy policy_20 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERM_ADD_BLACKLIST_TERM");
	Policy policy_21 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERM_ADD_ON_HOLD_TERM");
	Policy policy_22 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERM_ON_HOLD_TERM_STATUS");

	return newHashSet(policy_1, policy_2, policy_3, policy_4, policy_5, policy_6, policy_7, policy_8, policy_9,
		policy_10, policy_11, policy_12, policy_13, policy_14, policy_15, policy_16, policy_17, policy_18,
		policy_19, policy_20, policy_21, policy_22);
    }

    private Set<Policy> createSystemPolicies() {
	Policy policy_1 = createPolicy(RoleTypeEnum.SYSTEM, "Term", "POLICY_TM_MENU_TERMS");
	Policy policy_2 = createPolicy(RoleTypeEnum.SYSTEM, "Term", "POLICY_TM_VIEW_TRANSLATOR_INBOX");
	return newHashSet(policy_1, policy_2);
    }

    private String getProjectRoleId() {
	final int index = SYSTEM_SUPER_USER.indexOf("_");
	String roleId = SYSTEM_SUPER_USER.substring(index);
	return roleId.toLowerCase();
    }
}
