package groovy.webservice;

import org.gs4tr.foundation.modules.entities.model.PagedList
import org.gs4tr.foundation.modules.entities.model.PagedListInfo
import org.gs4tr.termmanager.model.TmProject;

browseUserProjectsCommand1=builder.browseUserProjectsCommand(fetchLanguages: true)
browseUserProjectsCommand2=builder.browseUserProjectsCommand(fetchLanguages: false)

statusWaiting = builder.itemStatusType(name: "waiting")
statusProcessed = builder.itemStatusType(name: "processed")
date = new Date();

organizationInfo1 = builder.organizationInfo([name: "My First Organization", domain: "DOMAIN", enabled: true,
    theme: "THEME", currencyCode: null])
organizationInfo2 = builder.organizationInfo([name: "My Second Organization", domain: "DOMAIN", enabled: true,
    theme: "THEME", currencyCode: null])

tmOrganization1 = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo1, parentOrganization: null])
tmOrganization2 = builder.tmOrganization([organizationId: 2L, organizationInfo: organizationInfo2, parentOrganization: null])

projectDetail1 = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 15L, completedSubmissionCount: 0L,
    dateModified: date, forbiddenTermCount: 0, languageCount: 0, languageDetails: null,
    project: null, projectDetailId: 1L, termCount: 15L, termEntryCount: 7L, termInSubmissionCount: 0L, userDetails: null])

projectDetail2 = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 10L, completedSubmissionCount: 0L,
    dateModified: date, forbiddenTermCount: 0L, languageCount: 0, languageDetails: null,
    project: null, projectDetailId: 2L, termCount: 10L, termEntryCount: 5L, termInSubmissionCount: 0L, userDetails: null])

projectInfo1 = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Meddra", shortCode: "MYF000001"])
projectInfo2 = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Nikon", shortCode: "MYS000001"])

projectLanguage1 = builder.projectLanguage([language: "en-US"])
projectLanguage2 = builder.projectLanguage([language: "de-DE"])

projectLanguagesSet = [
    projectLanguage1,
    projectLanguage2] as Set

tmProject1 = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusProcessed,
    metadata: null, organization: tmOrganization1, projectDetail: projectDetail1, projectId: 1L, projectInfo: projectInfo1,
    projectLanguages: projectLanguagesSet])

tmProject2 = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusWaiting,
    metadata: null, organization: tmOrganization2, projectDetail: projectDetail2, projectId: 2L, projectInfo: projectInfo2,
    projectLanguages: null])

userProjects1 = [tmProject1] as List
userProjects2 = [tmProject1, tmProject2] as List



