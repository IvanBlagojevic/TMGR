package groovy.projectSearchController;

import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.model.TmProject
import org.gs4tr.foundation.modules.entities.model.PagedList
import org.gs4tr.foundation.modules.entities.model.PagedListInfo
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum
import org.gs4tr.foundation.modules.entities.model.Task
import org.gs4tr.foundation.modules.entities.model.TaskPagedList
import org.gs4tr.foundation.modules.entities.model.TaskPriority


statusProcessed = builder.itemStatusType(name: "processed")
date = new Date();

organizationInfo = builder.organizationInfo([name: "Emisia", domain: "DOMAIN", enabled: true,
    theme: "THEME", currencyCode: null])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

projectDetail = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 15L, completedSubmissionCount: 0L,
    dateModified: date, forbiddenTermCount: 0, languageCount: 3, languageDetails: null,
    project: null, projectDetailId: 1L, termCount: 15L, termEntryCount: 5L, termInSubmissionCount: 0L, userDetails: null])

projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Skype", shortCode: "SKY000001"])

english = builder.projectLanguage([language: "en-US"])
germany = builder.projectLanguage([language: "de-DE"])
french = builder.projectLanguage([language: "fr-FR"])

projectLanguages = [english, germany, french] as Set

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusProcessed,
    metadata: null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
    projectLanguages: projectLanguages])

entities = [tmProject] as TmProject[]

pagedListInfo = builder.pagedListInfo([index: 0, indexesSize: 5, size: 50, sortDirection: "DESCENDING",
    sortProperty: "entity.projectInfo.name"])

pagedList = builder.pagedList([elements: entities, pagedListInfo: pagedListInfo, totalCount: 1])

addProject = builder.task([name: "add project", selectStyle: SelectStyleEnum.NO_SELECT, priority: TaskPriority.LEVEL_ZERO])

editProject = builder.task([name: "edit project", selectStyle: SelectStyleEnum.SINGLE_SELECT, priority: TaskPriority.LEVEL_FIVE])

checkProjectNameUniqueness = builder.task([name: "check project name uniqueness", selectStyle: SelectStyleEnum.NO_SELECT, priority: TaskPriority.LEVEL_FIVE])

generateShortCode = builder.task([name: "generate short code", selectStyle: SelectStyleEnum.NO_SELECT, priority: TaskPriority.LEVEL_FIVE])

assignProjectUsers = builder.task([name: "assign project users", selectStyle: SelectStyleEnum.SINGLE_SELECT, priority: TaskPriority.LEVEL_FIVE])

getProjectUserLanguages = builder.task([name: "get project user languages", selectStyle: SelectStyleEnum.SINGLE_SELECT, priority: TaskPriority.LEVEL_FIVE])

assignProjectAttributes = builder.task([name: "assign project attributes", selectStyle: SelectStyleEnum.SINGLE_SELECT, priority: TaskPriority.LEVEL_FIVE])

assignProjectNotes = builder.task([name: "assign project users", selectStyle: SelectStyleEnum.SINGLE_SELECT, priority: TaskPriority.LEVEL_FIVE])

sendConnectionString = builder.task([name: "send connection string", selectStyle: SelectStyleEnum.MULTI_SELECT, priority: TaskPriority.LEVEL_FIVE])

tasks=[
    getProjectUserLanguages,
    assignProjectAttributes,
    assignProjectNotes,
    checkProjectNameUniqueness,
    sendConnectionString,
    assignProjectUsers,
    generateShortCode,
    addProject,
    editProject,
] as Task[]

taskPagedList= new TaskPagedList<TmProject>(pagedList)
taskPagedList.setTasks(tasks)