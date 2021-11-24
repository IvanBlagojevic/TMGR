package groovy.rest
import org.gs4tr.foundation.modules.entities.model.PagedList
import org.gs4tr.foundation.modules.entities.model.PagedListInfo
import org.gs4tr.termmanager.model.TmProject;


statusWainting = builder.itemStatusType(name: "waiting")
date = new Date();

organizationInfo = builder.organizationInfo([name: "My First Organization", domain: "DOMAIN", enabled: true,
    theme: "THEME", currencyCode: null])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

projectDetail = builder.projectDetail([activeSubmissionCount: 1L, approvedTermCount: 7L, completedSubmissionCount: 0L,
    dateModified: date, forbiddenTermCount: 0, languageCount: 0, languageDetails: null,
    project: null, projectDetailId: 1L, termCount: 15L, termEntryCount: 8L, termInSubmissionCount: 2L, userDetails: null])

projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "My First Project", shortCode: "MYF000001"])

projectLanguage1 = builder.projectLanguage([language: "en-US"])
projectLanguage2 = builder.projectLanguage([language: "de-DE"])

projectLanguagesSet = [
    projectLanguage1,
    projectLanguage2] as Set

tmProject1 = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusWainting,
    metadata: null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
    projectLanguages: projectLanguagesSet])

tmProject2 = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusWainting,
    metadata: null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
    projectLanguages: null])


