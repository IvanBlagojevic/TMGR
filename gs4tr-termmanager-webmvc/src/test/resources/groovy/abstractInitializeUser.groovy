package groovy

import org.gs4tr.termmanager.model.FolderPolicy
import org.gs4tr.termmanager.model.search.ItemFolderEnum

date = new Date()

languageSet = ["en-US", "de-DE"] as Set

statusWainting = builder.itemStatusType(name: "waiting")
organizationInfo = builder.organizationInfo([name : "My First Organization", domain: "DOMAIN", enabled: true,
                                             theme: "THEME", currencyCode: null])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

projectDetail = builder.projectDetail([activeSubmissionCount: 1L, approvedTermCount: 7L, completedSubmissionCount: 0L,
                                       dateModified         : date, forbiddenTermCount: 0, languageCount: 0, languageDetails: null,
                                       project              : null, projectDetailId: 1L, termCount: 15L, termEntryCount: 8L, termInSubmissionCount: 2L, userDetails: null])

projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "My First Project", shortCode: "MYF000001"])

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusWainting,
                               metadata        : null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
                               projectLanguages: null])

userInfo = builder.userInfo([accountNonExpired  : true, accountNonLocked: true, address: null, credentialsNonExpired: true,
                             dateLastFailedLogin: null, dateLastLogin: date, datePasswordChanged: null, department: null,
                             emailAddress       : "mstrainovic@emisia.net", emailNotification: true, enabled: true, fax: null, firstName: "Marko",
                             lastName           : "Strainovic", password: "9a08ef76b3ae02adc6e8d52d51ffd7099669073a", phone1: null, phone2: null,
                             timeZone           : "Europe/Belgrade", unsuccessfulAuthCount: 0, userName: "marko", userType: "ORGANIZATION"])

policy1 = builder.policy([description: "Some policy description", policyId: "POLICY_FOUNDATION_PROJECT_ADD",
                          policyType : "CONTEXT", category: "Project"])
policy2 = builder.policy([description: "Some policy description", policyId: "POLICY_FOUNDATION_USERPROFILE_VIEW",
                          policyType : "CONTEXT", category: "User"])
policy3 = builder.policy([description: "Some policy description", policyId: "POLICY_FOUNDATION_ORGANIZATION_VIEW",
                          policyType : "CONTEXT", category: "Organization"])
policy4 = builder.policy([description: "Some policy description", policyId: "POLICY_TM_TERM_ADD_APPROVED_TERM",
                          policyType : "CONTEXT", category: "Task"])
policy5 = builder.policy([description: "Some policy description", policyId: "POLICY_TM_TERM_ADD_PENDING_TERM",
                          policyType : "CONTEXT", category: "Task"])
policy6 = builder.policy([description: "Some policy description", policyId: "POLICY_TM_TERMENTRY_IMPORT",
                          policyType : "CONTEXT", category: "Task"])
policy7 = builder.policy([description: "Some policy description", policyId: "POLICY_TM_TERM_ADD_ON_HOLD_TERM",
                          policyType : "CONTEXT", category: "Task"])
policy8 = builder.policy([description: "Some policy description", policyId: "POLICY_TM_TERM_ADD_BLACKLIST_TERM",
                          policyType : "CONTEXT", category: "Task"])
policy9 = builder.policy([description: "Some policy description", policyId: "POLICY_TM_TERM_ON_HOLD_TERM_STATUS",
                          policyType : "CONTEXT", category: "Task"])


policies1 = [
        policy1,
        policy2,
        policy3,
        policy4,
        policy5,
        policy6,
        policy7,
        policy8,
        policy9
]

role1 = builder.role([policies: policies1, roleId: "My Project Role", roleType: "CONTEXT", generic: false])

roles = [role1] as List

policyS1 = builder.policy([description: "Some policy description", policyId: "POLICY_TM_MENU_TERMS",
                           policyType : "SYSTEM", category: "Term"])

policiesS1 = [policyS1, policy7, policy8, policy9]

allPolicies = [
        policy1,
        policy2,
        policy3,
        policy4,
        policy5,
        policyS1,
        policy7,
        policy8,
        policy9
] as List

roleS1 = builder.role([policies: policiesS1, roleId: "My System Role", roleType: "SYSTEM", generic: false])

substituteSystemRoles = [roleS1] as Set

FolderPolicy folderPolicy1 = new FolderPolicy(ItemFolderEnum.PROJECTDETAILS, "POLICY_TM_MENU_TERMS")
FolderPolicy folderPolicy2 = new FolderPolicy(ItemFolderEnum.TERM_LIST, "POLICY_TM_MENU_TERMS")
FolderPolicy folderPolicy3 = new FolderPolicy(ItemFolderEnum.SUBMISSIONDETAILS, "POLICY_TM_VIEW_TRANSLATOR_INBOX")
FolderPolicy folderPolicy4 = new FolderPolicy(ItemFolderEnum.SUBMISSIONTERMLIST, "POLICY_TM_VIEW_TRANSLATOR_INBOX")

foldersPolicies = [
        folderPolicy1,
        folderPolicy2,
        folderPolicy3,
        folderPolicy4] as List

FolderPolicy adminFolderPolicy1 = new FolderPolicy(ItemFolderEnum.USERS, "POLICY_FOUNDATION_USERPROFILE_VIEW")
FolderPolicy adminFolderPolicy2 = new FolderPolicy(ItemFolderEnum.PROJECTS, "POLICY_FOUNDATION_PROJECT_VIEW")
FolderPolicy adminFolderPolicy3 = new FolderPolicy(ItemFolderEnum.ORGANIZATIONS, "POLICY_FOUNDATION_ORGANIZATION_VIEW")
FolderPolicy adminFolderPolicy4 = new FolderPolicy(ItemFolderEnum.SECURITY, "POLICY_FOUNDATION_SECURITY_ROLE_VIEW")

adminFolderPolocies = [
        adminFolderPolicy1,
        adminFolderPolicy2,
        adminFolderPolicy3,
        adminFolderPolicy4] as List


projectUserLanguage1 = builder.projectUserLanguage([language  : "en-US", project: tmProject, projectUserLanguageId: 1L,
                                                    statistics: null, user: null])
projectUserLanguage2 = builder.projectUserLanguage([language  : "de-DE", project: tmProject, projectUserLanguageId: 2L,
                                                    statistics: null, user: null])

projectUserLanguages = [
        projectUserLanguage1,
        projectUserLanguage2] as List

pref = builder.preferences([actionSize           : "lagre", actionTextVisible: true, defaultProjectId: 1L, defaultSourceLanguage: "en-US",
                            defaultTargetLanguage: "de-DE", itemsPerPage: 50, language: "en-US", refreshPeriod: 10,])

folders = [
        "BILINGUAL",
        "PROJECTDETAILS",
        "ORGANIZATIONS",
        "USERS",
        "SUBMISSIONTERMLIST"] as List

adminFolders = [
        "ORGANIZATIONS",
        "PROJECTDETAILS",
        "PROJECTS",
        "SUBMISSIONTERMLIST"] as List

set1 = ["de-DE", "en-UK", "sr-SR"] as Set
set2 = ["de-DE", "en-UK"] as Set
set3 = ["en-US", "en-UK"] as Set

map1 = ["en-US": set1] as Map
map2 = ["en-US": set2] as Map
map3 = ["de-DE": set3] as Map

subUserLang = [1L: map1, 2L: map2, 3L: map3] as Map

userProfile = builder.tmUserProfile([userProfileId: 1L, userInfo: userInfo, substituteSystemRoles: substituteSystemRoles,
                                     preferences  : pref, folders: folders, adminFolders: adminFolders, submissionUserLanguages: subUserLang, organization: tmOrganization])






