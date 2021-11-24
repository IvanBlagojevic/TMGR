package groovy.manualtask

import org.gs4tr.termmanager.model.ProjectLanguageUserDetail

date = new Date();

statusProcessed = builder.itemStatusType(name: "processed")

organizationInfo = builder.organizationInfo([name : "My First Organization", domain: "DOMAIN", enabled: true,
                                             theme: "THEME", currencyCode: null])

Set<ProjectLanguageUserDetail> userDetails = new HashSet<ProjectLanguageUserDetail>();

projectLanguageDetail1 = builder.projectLanguageDetail([languageId: "en-US", userDetails: userDetails])
projectLanguageDetail2 = builder.projectLanguageDetail([languageId: "en", userDetails: userDetails])
projectLanguageDetail3 = builder.projectLanguageDetail([languageId: "fr-FR", userDetails: userDetails])
projectLanguageDetail4 = builder.projectLanguageDetail([languageId: "de", userDetails: userDetails])

languageDetails = [
        projectLanguageDetail1,
        projectLanguageDetail2,
        projectLanguageDetail3,
        projectLanguageDetail4] as Set

projectDetail = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 0L, completedSubmissionCount: 0L,
                                       dateModified         : date, forbiddenTermCount: 0, languageCount: 0L, languageDetails: languageDetails,
                                       project              : null, projectDetailId: 1L, termCount: 0L, termEntryCount: 0L, termInSubmissionCount: 0L, userDetails: null])

projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Test5", shortCode: "TEST5000001"])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusProcessed,
                               metadata        : null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
                               projectLanguages: null])

termAttributeNames = ["context"] as Set

termEntryAttributeNames = ["TermEntry2", "termEntry1"] as Set

importCommand = builder.importCommand([attributeReplacements: null, defaultTermStatus: statusProcessed, finalyOverwriteByTermEntryId: false, ignoreCase: true, folder: "", attributeReplacements: [] as List, languageReplacementByCode: [] as Map, preImportCheck: false, projectId: 1L, sourceLanguage: "en", termAttributeNames: termAttributeNames, termEntryAttributeNames: termEntryAttributeNames])



