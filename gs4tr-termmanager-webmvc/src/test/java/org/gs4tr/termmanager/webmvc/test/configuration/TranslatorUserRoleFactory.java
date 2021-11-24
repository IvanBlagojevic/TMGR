package org.gs4tr.termmanager.webmvc.test.configuration;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.security.model.RoleTypeEnum;

public class TranslatorUserRoleFactory extends AbstractRoleFactory {

    private static final String SYSTEM_TRANSLATOR_USER = RoleNameEnum.SYSTEM_TRANSLATOR_USER.name();

    @Override
    public List<Role> createProjectRoles() {
	Role tranlsator_user = new Role();
	tranlsator_user.setRoleId(getProjectRoleId());
	tranlsator_user.setRoleType(RoleTypeEnum.CONTEXT);
	tranlsator_user.setGeneric(Boolean.TRUE);
	tranlsator_user.setPolicies(createProjectPolicies());
	return Arrays.asList(tranlsator_user);
    }

    @Override
    public Set<Role> createSystemRoles() {
	Role system_translator_user = new Role();
	system_translator_user.setRoleId(SYSTEM_TRANSLATOR_USER.toLowerCase());
	system_translator_user.setRoleType(RoleTypeEnum.SYSTEM);
	system_translator_user.setGeneric(Boolean.FALSE);
	system_translator_user.setPolicies(createSystemPolicies());
	return newHashSet(system_translator_user);
    }

    private Set<Policy> createProjectPolicies() {
	Policy policy_1 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERM_COMMIT_TRANSLATION_CHANGES");
	Policy policy_2 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERM_UNDO_TRANSLATION_CHANGES");
	Policy policy_3 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_ADD_COMMENT");
	Policy policy_4 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERM_VIEW_TERM_HISTORY");
	Policy policy_5 = createPolicy(RoleTypeEnum.CONTEXT, "Task", "POLICY_TM_TERM_AUTO_SAVE_TRANSLATION");

	return newHashSet(policy_1, policy_2, policy_3, policy_4, policy_5);
    }

    private Set<Policy> createSystemPolicies() {
	return newHashSet(createPolicy(RoleTypeEnum.SYSTEM, "Term", "POLICY_TM_VIEW_TRANSLATOR_INBOX"));
    }

    private String getProjectRoleId() {
	return SYSTEM_TRANSLATOR_USER.substring(SYSTEM_TRANSLATOR_USER.indexOf("_")).toLowerCase();
    }
}
