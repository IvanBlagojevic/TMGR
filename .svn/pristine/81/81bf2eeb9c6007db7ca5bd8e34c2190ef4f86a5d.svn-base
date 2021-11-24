package groovy.preferencesUpdateController;

statusProcessed = builder.itemStatusType(name: "processed")
date = new Date();

organizationInfo = builder.organizationInfo([name: "Emisia", domain: "DOMAIN", enabled: true,
    theme: "THEME", currencyCode: null])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

projectDetail = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 15L, completedSubmissionCount: 0L,
    dateModified: date, forbiddenTermCount: 0, languageCount: 3, languageDetails: null,
    project: null, projectDetailId: 1L, termCount: 15L, termEntryCount: 5L, termInSubmissionCount: 0L, userDetails: null])

projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Skype", shortCode: "SKYPE0001"])

english = builder.projectLanguage([language: "en-US"])
germany = builder.projectLanguage([language: "de-DE"])
french = builder.projectLanguage([language: "fr-FR"])

projectLanguages = [english, germany, french] as Set

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusProcessed,
    metadata: null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
    projectLanguages: projectLanguages])

tmProjects = [tmProject] as List
