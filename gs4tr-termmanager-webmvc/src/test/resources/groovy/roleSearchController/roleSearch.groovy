package groovy.roleSearchController

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum
import org.gs4tr.foundation.modules.entities.model.Task
import org.gs4tr.foundation.modules.entities.model.TaskPagedList
import org.gs4tr.foundation.modules.entities.model.TaskPriority
import org.gs4tr.foundation.modules.security.model.Role
import org.gs4tr.foundation.modules.security.model.RoleTypeEnum
import org.gs4tr.termmanager.model.TmProject

addOrganization = builder.policy(category: "Organization", policyId: "POLICY_FOUNDATION_ORGANIZATION_ADD", policyType: RoleTypeEnum.SYSTEM)

roleView = builder.policy(category: "Role", policyId: "POLICY_FOUNDATION_SECURITY_ROLE_VIEW", policyType: RoleTypeEnum.SYSTEM)

addProject = builder.policy(category: "Project", policyId: "POLICY_FOUNDATION_PROJECT_ADD", policyType: RoleTypeEnum.SYSTEM)

enableDisableOrganization = builder.policy(category: "Organization", policyId: "POLICY_FOUNDATION_ORGANIZATION_ENABLEDISABLE", policyType: RoleTypeEnum.SYSTEM)

enableDisableProject = builder.policy(category: "Project", policyId: "POLICY_FOUNDATION_PROJECT_ENABLEDISABLE", policyType: RoleTypeEnum.SYSTEM)

addRole = builder.policy(category: "Role", policyId: "POLICY_FOUNDATION_SECURITY_ROLE_ADD", policyType: RoleTypeEnum.SYSTEM)

projectView = builder.policy(category: "Project", policyId: "POLICY_FOUNDATION_PROJECT_VIEW", policyType: RoleTypeEnum.SYSTEM)

editUser = builder.policy(category: "User", policyId: "POLICY_FOUNDATION_USERPROFILE_EDIT", policyType: RoleTypeEnum.SYSTEM)

sendConnection = builder.policy(category: "Project", policyId: "POLICY_TM_PROJECT_SEND_CONNECTION", policyType: RoleTypeEnum.SYSTEM)

editOrganization = builder.policy(category: "Organization", policyId: "POLICY_FOUNDATION_ORGANIZATION_EDIT", policyType: RoleTypeEnum.SYSTEM)

viewOrganization = builder.policy(category: "Organization", policyId: "POLICY_FOUNDATION_ORGANIZATION_VIEW", policyType: RoleTypeEnum.SYSTEM)

viewUser = builder.policy(category: "User", policyId: "POLICY_FOUNDATION_USERPROFILE_VIEW", policyType: RoleTypeEnum.SYSTEM)

addUser = builder.policy(category: "User", policyId: "POLICY_FOUNDATION_USERPROFILE_ADD", policyType: RoleTypeEnum.SYSTEM)

editRole = builder.policy(category: "Role", policyId: "POLICY_FOUNDATION_SECURITY_ROLE_EDIT", policyType: RoleTypeEnum.SYSTEM)

enableDisableUser = builder.policy(category: "User", policyId: "POLICY_FOUNDATION_USERPROFILE_ENABLEDISABLE", policyType: RoleTypeEnum.SYSTEM)

editProject = builder.policy(category: "Project", policyId: "POLICY_FOUNDATION_PROJECT_EDIT", policyType: RoleTypeEnum.SYSTEM)

nonExpiringUser = builder.policy(category: "User", policyId: "POLICY_FOUNDATION_USERPROFILE_NON_EXPIRING_USER", policyType: RoleTypeEnum.SYSTEM)

adminPolicies = [
        addOrganization,
        roleView,
        addProject,
        enableDisableOrganization,
        enableDisableProject,
        addRole,
        projectView,
        editUser,
        sendConnection,
        editOrganization,
        viewOrganization,
        viewUser,
        addUser,
        editRole,
        enableDisableUser,
        editProject,
        nonExpiringUser
] as Set

admin = builder.role([roleType: RoleTypeEnum.SYSTEM, roleId: "admin", generic: Boolean.FALSE, policies: adminPolicies])

tmRead = builder.policy(category: "Task", policyId: "POLICY_TM_READ", policyType: RoleTypeEnum.CONTEXT)
genericUserPolicies = [tmRead] as Set
generic_user = builder.role([roleType: RoleTypeEnum.CONTEXT, roleId: "generic_user", generic: Boolean.TRUE, policies: genericUserPolicies])


cancelTranslation = builder.policy(category: "Task", policyId: "POLICY_TM_CANCEL_TRANSLATION", policyType: RoleTypeEnum.CONTEXT)

addComment = builder.policy(category: "Task", policyId: "POLICY_TM_ADD_COMMENT", policyType: RoleTypeEnum.CONTEXT)

importTermEntry = builder.policy(category: "Task", policyId: "POLICY_TM_TERMENTRY_IMPORT", policyType: RoleTypeEnum.CONTEXT)

viewTermHistoryUsers = builder.policy(category: "Task", policyId: "POLICY_TM_TERM_VIEW_TERM_HISTORY_USERS", policyType: RoleTypeEnum.CONTEXT)

projectReport = builder.policy(category: "Task", policyId: "POLICY_TM_PROJECT_REPORT", policyType: RoleTypeEnum.CONTEXT)

approveTermStatus = builder.policy(category: "Task", policyId: "POLICY_TM_TERM_APPROVE_TERM_STATUS", policyType: RoleTypeEnum.CONTEXT)

forbidTermEntry = builder.policy(category: "Task", policyId: "POLICY_TM_TERMENTRY_FORBID_TERMENTRY", policyType: RoleTypeEnum.CONTEXT)

assignAttributes = builder.policy(category: "Task", policyId: "POLICY_TM_TERMENTRY_ASSIGN_ATTRIBUTES", policyType: RoleTypeEnum.CONTEXT)

autosaveTranslation = builder.policy(category: "Task", policyId: "POLICY_TM_TERM_AUTO_SAVE_TRANSLATION", policyType: RoleTypeEnum.CONTEXT)

commitTranslationChanges = builder.policy(category: "Task", policyId: "POLICY_TM_TERM_COMMIT_TRANSLATION_CHANGES", policyType: RoleTypeEnum.CONTEXT)

demoteTermStatus = builder.policy(category: "Task", policyId: "POLICY_TM_TERM_DEMOTE_TERM_STATUS", policyType: RoleTypeEnum.CONTEXT)

undoTranslationChanges = builder.policy(category: "Task", policyId: "POLICY_TM_TERM_UNDO_TRANSLATION_CHANGES", policyType: RoleTypeEnum.CONTEXT)

sendToTranslationReview = builder.policy(category: "Task", policyId: "POLICY_TM_SEND_TO_TRANSLATION_REVIEW", policyType: RoleTypeEnum.CONTEXT)

addPendingTerm = builder.policy(category: "Task", policyId: "POLICY_TM_TERM_ADD_PENDING_TERM", policyType: RoleTypeEnum.CONTEXT)

viewTermHistory = builder.policy(category: "Task", policyId: "POLICY_TM_TERM_VIEW_TERM_HISTORY", policyType: RoleTypeEnum.CONTEXT)

exportTermEntry = builder.policy(category: "Task", policyId: "POLICY_TM_TERMENTRY_EXPORT", policyType: RoleTypeEnum.CONTEXT)

addApprovedTerm = builder.policy(category: "Task", policyId: "POLICY_TM_TERM_ADD_APPROVED_TERM", policyType: RoleTypeEnum.CONTEXT)

disableTerm = builder.policy(category: "Task", policyId: "POLICY_TM_TERM_DISABLE_TERM", policyType: RoleTypeEnum.CONTEXT)

addEditOnHoldTerm = builder.policy(category: "Task", policyId: "POLICY_TM_TERM_ADD_ON_HOLD_TERM", policyType: RoleTypeEnum.CONTEXT)

addEditBlacklistTerm = builder.policy(category: "Task", policyId: "POLICY_TM_TERM_ADD_BLACKLIST_TERM", policyType: RoleTypeEnum.CONTEXT)

onHoldtTerm = builder.policy(category: "Task", policyId: "POLICY_TM_TERM_ON_HOLD_TERM_STATUS", policyType: RoleTypeEnum.CONTEXT)

powerUserSendConnection = builder.policy(category: "Task", policyId: "POLICY_TM_USER_SEND_CONNECTION", policyType: RoleTypeEnum.CONTEXT)

contextUserPolicies = [
        tmRead,
        cancelTranslation,
        addComment,
        importTermEntry,
        viewTermHistoryUsers,
        projectReport,
        approveTermStatus,
        forbidTermEntry,
        assignAttributes,
        autosaveTranslation,
        commitTranslationChanges,
        demoteTermStatus,
        undoTranslationChanges,
        sendToTranslationReview,
        addPendingTerm,
        viewTermHistory,
        exportTermEntry,
        addApprovedTerm,
        addEditOnHoldTerm,
        addEditBlacklistTerm,
        onHoldtTerm,
        disableTerm,
        powerUserSendConnection] as Set

power_user = builder.role([roleType: RoleTypeEnum.CONTEXT, roleId: "power_user", generic: Boolean.TRUE, policies: contextUserPolicies])

super_user = builder.role([roleType: RoleTypeEnum.CONTEXT, roleId: "super_user", generic: Boolean.FALSE, policies: contextUserPolicies])

viewTranslationInbox = builder.policy(category: "Term", policyId: "POLICY_TM_VIEW_TRANSLATOR_INBOX", policyType: RoleTypeEnum.SYSTEM)

tmMenuTerms = builder.policy(category: "Term", policyId: "POLICY_TM_MENU_TERMS", policyType: RoleTypeEnum.SYSTEM)

systemPowerUserPolicies = [
        addProject,
        editProject,
        enableDisableProject,
        viewTranslationInbox,
        tmMenuTerms,
        projectView,
        editUser,
        sendConnection] as Set

system_power_user = builder.role([roleType: RoleTypeEnum.SYSTEM, roleId: "system_power_user", generic: Boolean.FALSE, policies: systemPowerUserPolicies])

systemSuperUserPolicies = [
        viewTranslationInbox,
        tmMenuTerms
] as Set
system_super_user = builder.role([roleType: RoleTypeEnum.SYSTEM, roleId: "system_super_user", generic: Boolean.FALSE, policies: systemSuperUserPolicies])

systemTranslatorUserPolicies = [viewTranslationInbox
] as Set
system_translator_user = builder.role([roleType: RoleTypeEnum.SYSTEM, roleId: "system_translator_user", generic: Boolean.FALSE, policies: systemTranslatorUserPolicies])

translatorUserPolicies = [
        commitTranslationChanges,
        undoTranslationChanges,
        addComment,
        viewTermHistory,
        autosaveTranslation
] as Set
translator_user = builder.role([roleType: RoleTypeEnum.CONTEXT, roleId: "translator_user", generic: Boolean.FALSE, policies: translatorUserPolicies])

entities = [
        admin,
        generic_user,
        power_user,
        super_user,
        system_power_user,
        system_super_user,
        system_translator_user,
        translator_user] as Role[]

pagedListInfo = builder.pagedListInfo([index: 0, indexesSize: 5, size: 50, sortDirection: "ASCENDING"])

pagedList = builder.pagedList([elements: entities, pagedListInfo: pagedListInfo, totalCount: 8])

addRoleTask = builder.task([name: "add role", selectStyle: SelectStyleEnum.NO_SELECT, priority: TaskPriority.LEVEL_FIVE])

editRoleTask = builder.task([name: "edit role", selectStyle: SelectStyleEnum.SINGLE_SELECT, priority: TaskPriority.LEVEL_FIVE])

tasks = [
        addRoleTask,
        editRoleTask,
] as Task[]

taskPagedList = new TaskPagedList<TmProject>(pagedList)
taskPagedList.setTasks(tasks)