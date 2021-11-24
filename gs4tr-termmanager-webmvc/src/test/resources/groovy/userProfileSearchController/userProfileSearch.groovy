package groovy.userProfileSearchController

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum
import org.gs4tr.foundation.modules.entities.model.Task
import org.gs4tr.foundation.modules.entities.model.TaskPagedList
import org.gs4tr.foundation.modules.entities.model.TaskPriority
import org.gs4tr.termmanager.model.TmUserProfile

pagedListInfo = builder.pagedListInfo([index       : 0, indexesSize: 5, size: 50, sortDirection: "ASCENDING",
                                       sortProperty: "entity.userInfo.userName"])

dateLastLogin = new Date();

admin_user_info = builder.userInfo([accountNonExpired  : true, accountNonLocked: true, address: null, credentialsNonExpired: true,
                                    dateLastFailedLogin: null, dateLastLogin: dateLastLogin, datePasswordChanged: null, department: null,
                                    emailAddress       : null, emailNotification: null, enabled: true, fax: null, firstName: null,
                                    lastName           : null, password: "fcb820a31b3ac2c1bb1c66fc18f13b3cac572b98", phone1: null, phone2: null,
                                    timeZone           : null, unsuccessfulAuthCount: 0, userName: "admin", userType: "ORGANIZATION"])

super_user_info = builder.userInfo([accountNonExpired  : true, accountNonLocked: true, address: null, credentialsNonExpired: true,
                                    dateLastFailedLogin: null, dateLastLogin: dateLastLogin, datePasswordChanged: null, department: null,
                                    emailAddress       : "dbrasko@emisia.net", emailNotification: true, enabled: true, fax: null, firstName: "Donnie",
                                    lastName           : "Brasko", password: "dab9c2a86c1e2c874367e69e5af5e913a7d24ecf", phone1: null, phone2: null,
                                    timeZone           : "America/Argentina/Buenos_Aires", unsuccessfulAuthCount: 0, userName: "dbrasko", userType: "ORGANIZATION"])

organizationInfo = builder.organizationInfo([name        : "Emisia", domain: "DOMAIN", enabled: true, theme: "THEME",
                                             currencyCode: null])
tmOrganization = builder.tmOrganization([organizationId    : 1L, organizationInfo: organizationInfo,
                                         parentOrganization: null])

admin_user = builder.tmUserProfile([adminFolders   : null, contextsPolicies: new HashMap<>(), contextsRoles: new HashMap<>(),
                                    folders        : null, generic: false, genericPassword: null, hasChangedTerms: false, hidden: false,
                                    lastDailyReport: null, lastWeeklyReport: null, metadata: null, notificationProfiles: null, organization: tmOrganization,
                                    preferences    : null, projectUserLanguages: null, submissionUserLanguages: null, substituteSystemRoles: null,
                                    systemRoles    : null, userInfo: admin_user_info, userProfileId: 1L])

super_user = builder.tmUserProfile([adminFolders   : null, contextsPolicies: new HashMap<>(), contextsRoles: new HashMap<>(),
                                    folders        : null, generic: false, genericPassword: null, hasChangedTerms: false, hidden: false,
                                    lastDailyReport: null, lastWeeklyReport: null, metadata: null, notificationProfiles: null, organization: null,
                                    preferences    : null, projectUserLanguages: null, submissionUserLanguages: null, substituteSystemRoles: null,
                                    systemRoles    : null, userInfo: super_user_info, userProfileId: 2L])

elements = [admin_user, super_user] as TmUserProfile[]

pagedList = builder.pagedList([elements: elements, pagedListInfo: pagedListInfo, totalCount: 1])

task_1 = builder.task([name: "add user", selectStyle: SelectStyleEnum.NO_SELECT, priority: TaskPriority.LEVEL_FIVE])

task_2 = builder.task([name: "edit user", selectStyle: SelectStyleEnum.SINGLE_SELECT, priority: TaskPriority.LEVEL_FIVE])

task_3 = builder.task([name: "assign role", selectStyle: SelectStyleEnum.NO_SELECT, priority: TaskPriority.LEVEL_FIVE])

task_4 = builder.task([name: "assign organization", selectStyle: SelectStyleEnum.NO_SELECT, priority: TaskPriority.LEVEL_FIVE])

task_5 = builder.task([name: "assign languages", selectStyle: SelectStyleEnum.NO_SELECT, priority: TaskPriority.LEVEL_FIVE])

task_6 = builder.task([name: "enable user", selectStyle: SelectStyleEnum.MULTI_SELECT, priority: TaskPriority.LEVEL_FIVE])

task_7 = builder.task([name: "assign project", selectStyle: SelectStyleEnum.NO_SELECT, priority: TaskPriority.LEVEL_FIVE])

task_8 = builder.task([name: "clear user metadata", selectStyle: SelectStyleEnum.SINGLE_SELECT, priority: TaskPriority.LEVEL_FIVE])

task_9 = builder.task([name: "unlock user account", selectStyle: SelectStyleEnum.SINGLE_SELECT, priority: TaskPriority.LEVEL_SIX])

task_10 = builder.task([name: "check user uniqueness", selectStyle: SelectStyleEnum.NO_SELECT, priority: TaskPriority.LEVEL_FIVE])

task_11 = builder.task([name: "change password", selectStyle: SelectStyleEnum.SINGLE_SELECT, priority: TaskPriority.LEVEL_FIVE])

tasks = [
        task_1,
        task_2,
        task_3,
        task_4,
        task_4,
        task_5,
        task_6,
        task_7,
        task_8,
        task_9,
        task_10,
        task_11] as Task[]

taskPagedList = new TaskPagedList<TmUserProfile>(pagedList)
taskPagedList.setTasks(tasks)