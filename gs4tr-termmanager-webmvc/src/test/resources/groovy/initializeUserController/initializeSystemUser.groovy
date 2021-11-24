package groovy.initializeUserController

statusProcessed = builder.itemStatusType(name: "processed")
date = new Date();

organizationInfo = builder.organizationInfo([name : "Emisia", domain: "DOMAIN", enabled: true,
                                             theme: "THEME", currencyCode: null])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

projectDetail = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 15L, completedSubmissionCount: 0L,
                                       dateModified         : date, forbiddenTermCount: 0, languageCount: 3, languageDetails: null,
                                       project              : null, projectDetailId: 1L, termCount: 15L, termEntryCount: 5L, termInSubmissionCount: 0L, userDetails: null])

projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Skype", shortCode: "SKYPE0001"])

english = builder.projectLanguage([language: "en-US"])
germany = builder.projectLanguage([language: "de-DE"])
french = builder.projectLanguage([language: "fr-FR"])

projectLanguages = [english, germany, french] as Set

projectLanguages1 = ["de-DE", "fr-FR", "no-NO", "bg-BG", "en-US", "en-GB"] as Set
projectLanguages2 = ["de-DE", "fr-FR", "no-NO", "bg-BG"] as Set
projectLanguages3 = ["de-DE", "fr-FR", "no-NO", "bg-BG", "en-GB"] as Set

projectUserLanguages = [1L: projectLanguages1] as Map
projectUserLanguages1 = [1L: projectLanguages2] as Map
projectUserLanguages2 = [1L: projectLanguages3] as Map

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusProcessed,
                               metadata        : null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
                               projectLanguages: projectLanguages])

tmProjects = [tmProject] as List

usernames = [
        "admin",
        "system",
        "super",
        "translator"] as List