package groovy.webservice


import org.gs4tr.termmanager.model.ImportSummary
import org.gs4tr.termmanager.model.ImportTypeEnum

// This is invalid export command, project ticket is mandatory
invalidImportCommand = builder.importCommand([syncLang: "en-US", importType: ImportTypeEnum.TBX])

importCommand = builder.importCommand([projectTicket: "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q", syncLang: "en-US", importType: ImportTypeEnum.TBX])

date = new Date();
statusProcessed = builder.itemStatusType(name: "processed")

organizationInfo = builder.organizationInfo([name : "My First Organization", domain: "DOMAIN", enabled: true,
                                             theme: "THEME", currencyCode: null])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

projectDetail = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 15L, completedSubmissionCount: 0L,
                                       dateModified         : date, forbiddenTermCount: 0, languageCount: 2, languageDetails: null,
                                       project              : null, projectDetailId: 1L, termCount: 15L, termEntryCount: 7L, termInSubmissionCount: 0L, userDetails: null])

projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Skype", shortCode: "SKYPE0001"])

projectLanguage1 = builder.projectLanguage([language: "en-US"])
projectLanguage2 = builder.projectLanguage([language: "de-DE"])

projectLanguages = [
        projectLanguage1,
        projectLanguage2] as Set

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusProcessed,
                               metadata        : null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
                               projectLanguages: projectLanguages])

importSummary = new ImportSummary();


















