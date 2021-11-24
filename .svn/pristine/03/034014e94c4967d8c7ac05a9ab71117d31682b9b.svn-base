import org.gs4tr.termmanager.model.TmProject;

date = new Date();

organizationInfo = builder.organizationInfo([name: "Emisia", domain: "DOMAIN", enabled: false, theme: "THEME", currencyCode: null])

projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Pending Approval Project", shortCode: "MI5000001"])

projectDetail = builder.projectDetail([activeSubmissionCount: 1L, approvedTermCount: 53L, completedSubmissionCount: 0L,
    dateModified: date, forbiddenTermCount: 0, languageCount: 0, languageDetails: null, project: null,
    projectDetailId: 1L, termCount: 53L, termEntryCount: 9L, termInSubmissionCount: 3L, userDetails: null])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

defaultStatus = builder.itemStatusType(name: "pendingapproval")

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: defaultStatus,
    metadata: null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
    projectLanguages: null])

// Use this new project for second test case
organizationInfo1 = builder.organizationInfo([name: "Nikon", domain: "DOMAIN", enabled: false, theme: "THEME", currencyCode: null])

projectInfo1 = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Approved Project", shortCode: "MI5000001"])

projectDetail1 = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 23L, completedSubmissionCount: 0L,
    dateModified: date, forbiddenTermCount: 0, languageCount: 0, languageDetails: null, project: null,
    projectDetailId: 1L, termCount: 23L, termEntryCount: 9L, termInSubmissionCount: 0L, userDetails: null])

tmOrganization1 = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo1, parentOrganization: null])

defaultStatus1 = builder.itemStatusType(name: "approval")

tmProject1 = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: defaultStatus1,
    metadata: null, organization: tmOrganization1, projectDetail: projectDetail1, projectId: 0L, projectInfo: projectInfo1,
    projectLanguages: null])

projectIds = [1L] as List
projectIds1 = [0l, 1L] as List

projects = [tmProject] as List
projects1 = [tmProject, tmProject1] as List


