package groovy.rest
import org.gs4tr.foundation.modules.entities.model.PagedList
import org.gs4tr.foundation.modules.entities.model.PagedListInfo
import org.gs4tr.termmanager.model.TmProject;


statusWainting = builder.itemStatusType(name: "waiting")
date = new Date();

organizationInfo1 = builder.organizationInfo([name: "My First Organization", domain: "DOMAIN", enabled: true,
    theme: "THEME", currencyCode: null])
organizationInfo2 = builder.organizationInfo([name: "My Second Organization", domain: "DOMAIN", enabled: true,
    theme: "THEME", currencyCode: null])

tmOrganization1 = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo1, parentOrganization: null])
tmOrganization2 = builder.tmOrganization([organizationId: 2L, organizationInfo: organizationInfo2, parentOrganization: null])

projectDetail1 = builder.projectDetail([activeSubmissionCount: 1L, approvedTermCount: 7L, completedSubmissionCount: 0L,
    dateModified: date, forbiddenTermCount: 0, languageCount: 0, languageDetails: null,
    project: null, projectDetailId: 1L, termCount: 15L, termEntryCount: 8L, termInSubmissionCount: 2L, userDetails: null])

projectDetail2 = builder.projectDetail([activeSubmissionCount: 2L, approvedTermCount: 10L, completedSubmissionCount: 1L,
    dateModified: date, forbiddenTermCount: 1L, languageCount: 0, languageDetails: null,
    project: null, projectDetailId: 1L, termCount: 15L, termEntryCount: 8L, termInSubmissionCount: 3L, userDetails: null])

projectInfo1 = builder.projectInfo([clientIdentifier: null, enabled: true, name: "My First Project", shortCode: "MYF000001"])
projectInfo2 = builder.projectInfo([clientIdentifier: null, enabled: true, name: "My Second Project", shortCode: "MYS000001"])

projectLanguage1 = builder.projectLanguage([language: "en-US"])
projectLanguage2 = builder.projectLanguage([language: "de-DE"])

projectLanguagesSet = [
    projectLanguage1,
    projectLanguage2] as Set

tmProject1 = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusWainting,
    metadata: null, organization: tmOrganization1, projectDetail: projectDetail1, projectId: 1L, projectInfo: projectInfo1,
    projectLanguages: projectLanguagesSet])

tmProject2 = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusWainting,
    metadata: null, organization: tmOrganization2, projectDetail: projectDetail2, projectId: 2L, projectInfo: projectInfo2,
    projectLanguages: null])

userProjects = [tmProject1, tmProject2] as List
