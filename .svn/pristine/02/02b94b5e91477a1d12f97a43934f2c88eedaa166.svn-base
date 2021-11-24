import org.gs4tr.foundation.modules.entities.model.PagedList
import org.gs4tr.foundation.modules.entities.model.PagedListInfo


statusWainting = builder.itemStatusType(name: "waiting")
date = new Date();

organizationInfo1 = builder.organizationInfo([name: "My First Organization", domain: "DOMAIN", enabled: true,
    theme: "THEME", currencyCode: null])

tmOrganization1 = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo1, parentOrganization: null])

projectDetail1 = builder.projectDetail([activeSubmissionCount: 1L, approvedTermCount: 7L, completedSubmissionCount: 0L,
    dateModified: date, forbiddenTermCount: 0, languageCount: 0, languageDetails: null,
    project: null, projectDetailId: 1L, termCount: 15L, termEntryCount: 8L, termInSubmissionCount: 2L, userDetails: null])

projectDetail2 = builder.projectDetail([activeSubmissionCount: 2L, approvedTermCount: 10L, completedSubmissionCount: 0L,
    dateModified: date, forbiddenTermCount: 1, languageCount: 10, languageDetails: null,
    project: null, projectDetailId: 1L, termCount: 15L, termEntryCount: 8L, termInSubmissionCount: 7L, userDetails: null])

projectInfo1 = builder.projectInfo([clientIdentifier: null, enabled: true, name: "My First Project", shortCode: "MYF000001"])
projectInfo2 = builder.projectInfo([clientIdentifier: null, enabled: true, name: "My Second Project", shortCode: "MSF000001"])


tmProject1 = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusWainting,
    metadata: null, organization: tmOrganization1, projectDetail: projectDetail1, projectId: 1L, projectInfo: projectInfo1,
    projectLanguages: null])

tmProject2 = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusWainting,
    metadata: null, organization: tmOrganization1, projectDetail: projectDetail2, projectId: 1L, projectInfo: projectInfo2,
    projectLanguages: null])





