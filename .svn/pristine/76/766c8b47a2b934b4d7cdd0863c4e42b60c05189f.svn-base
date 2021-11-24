
statusWainting = builder.itemStatusType(name: "waiting")
date = new Date();

profile1 = builder.notificationProfile([displayDashboardNotification: false, notificationClassifier: "readyForTranslation",
    profileName: null, sendDailyMailNotification: false, sendTaskMailNotification: false, sendWeeklyMailNotification: false])
profile2 = builder.notificationProfile([displayDashboardNotification: true, notificationClassifier: "translationCanceled",
    profileName: null, sendDailyMailNotification: true, sendTaskMailNotification: true, sendWeeklyMailNotification: true])

notificationProfile = [profile1, profile2] as List

preferences = builder.preferences([actionSize: "large", actionTextVisible: true, dailyHour: 17, dayOfWeek: 6,
    defaultProjectId: 1L, defaultSourceLanguage: "en-US", defaultTargetLanguage: "de-DE", itemsPerPage: 50,
    language: "en-US", refreshPeriod: 30, weeklyHour: 17])

preferences2 = builder.preferences([actionSize: "large", actionTextVisible: true, dailyHour: 0, dayOfWeek: 0,
    defaultProjectId: null, defaultSourceLanguage: null, defaultTargetLanguage: null, itemsPerPage: 50,
    language: "en-US", refreshPeriod: 30, weeklyHour: 0])

//Projects mocks 
organizationInfo1 = builder.organizationInfo([name: "My First Organization", domain: "DOMAIN", enabled: true,
    theme: "THEME", currencyCode: null])

tmOrganization1 = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo1, parentOrganization: null])

projectDetail1 = builder.projectDetail([activeSubmissionCount: 1L, approvedTermCount: 7L, completedSubmissionCount: 0L,
    dateModified: date, forbiddenTermCount: 0, languageCount: 0, languageDetails: null,
    project: null, projectDetailId: 1L, termCount: 15L, termEntryCount: 8L, termInSubmissionCount: 2L, userDetails: null])

projectInfo1 = builder.projectInfo([clientIdentifier: null, enabled: true, name: "My First Project", shortCode: "MYF000001"])

tmProject1 = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusWainting,
    metadata: null, organization: tmOrganization1, projectDetail: projectDetail1, projectId: 1L, projectInfo: projectInfo1,
    projectLanguages: null])

tmProjects = [tmProject1] as List

dashboardTickets = ([1L, 2L, 3L]) as List