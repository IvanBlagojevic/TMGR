package groovy.preferencesUpdateController;

import org.gs4tr.termmanager.model.NotificationPriority;
import org.gs4tr.termmanager.model.TmNotificationType

statusProcessed = builder.itemStatusType(name: "processed")
date = new Date();

readyForTranlsation=builder.notificationProfile([displayDashboardNotification: Boolean.TRUE,
    sendDailyMailNotification :Boolean.FALSE, notificationClassifier: "readyForTranslation",
    notificationPriority: NotificationPriority.NORMAL])
translationCanceled=builder.notificationProfile([displayDashboardNotification: Boolean.TRUE,
    sendDailyMailNotification :Boolean.FALSE, notificationClassifier: "translationCanceled",
    notificationPriority: NotificationPriority.NORMAL])
translationCompleted=builder.notificationProfile([displayDashboardNotification: Boolean.TRUE,
    sendDailyMailNotification :Boolean.FALSE, notificationClassifier: "translationCompleted",
    notificationPriority: NotificationPriority.NORMAL])
addedApprovedTerm=builder.notificationProfile([displayDashboardNotification: Boolean.TRUE,
    sendDailyMailNotification :Boolean.FALSE, notificationClassifier: "addedApprovedTerm",
    notificationPriority: NotificationPriority.NORMAL])
addedPendingTerm=builder.notificationProfile([displayDashboardNotification: Boolean.TRUE,
    sendDailyMailNotification :Boolean.FALSE, notificationClassifier: "addedPendingTerm",
    notificationPriority: NotificationPriority.NORMAL])
approveTerm=builder.notificationProfile([displayDashboardNotification: Boolean.TRUE,
    sendDailyMailNotification :Boolean.FALSE, notificationClassifier: "approveTerm",
    notificationPriority: NotificationPriority.NORMAL])
deletedTerm=builder.notificationProfile([displayDashboardNotification: Boolean.TRUE,
    sendDailyMailNotification :Boolean.FALSE, notificationClassifier: "deletedTerm",
    notificationPriority: NotificationPriority.NORMAL])
demoteTerm=builder.notificationProfile([displayDashboardNotification: Boolean.TRUE,
    sendDailyMailNotification :Boolean.FALSE, notificationClassifier: "demoteTerm",
    notificationPriority: NotificationPriority.NORMAL])
editTerm=builder.notificationProfile([displayDashboardNotification: Boolean.TRUE,
    sendDailyMailNotification :Boolean.FALSE, notificationClassifier: "editTerm",
    notificationPriority: NotificationPriority.NORMAL])

notificationProfiles=[
    translationCompleted,
    translationCanceled,
    readyForTranlsation,
    addedApprovedTerm,
    addedPendingTerm,
    approveTerm,
    demoteTerm,
    editTerm,
    deletedTerm] as List

preferences = builder.preferences([actionSize: "large", actionTextVisible: true, dailyHour: 17, dayOfWeek: 6,
    defaultProjectId: 1L, defaultSourceLanguage: "en-US", defaultTargetLanguage: "de-DE", itemsPerPage: 50,
    language: "en-US", refreshPeriod: 30, weeklyHour: 17])

//Projects mocks
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

dashboardTickets = ([1L, 2L, 3L]) as List

notificationConfiguration="{\"configuration\":[{\"classifier\":\"COPY_FAILED\",\"sendMailNotificaton\":false,\"displayDashboardNotification\":true},{\"classifier\":\"EXPORT_FAILED\",\"sendMailNotificaton\":true,\"displayDashboardNotification\":false},{\"classifier\":\"DELETED_FAILED\",\"sendMailNotificaton\":false,\"displayDashboardNotification\":true},{\"classifier\":\"COPIED\",\"sendMailNotificaton\":false,\"displayDashboardNotification\":true},{\"classifier\":\"EXPORTED\",\"sendMailNotificaton\":false,\"displayDashboardNotification\":true},{\"classifier\":\"DELETED\",\"sendMailNotificaton\":true,\"displayDashboardNotification\":false}]}"

preferencesUpdateCommand=builder.preferencesUpdateCommand([actionSize: "medium", itemsPerPage: 100,
    refreshPeriod: 15, language: "en-US", notificationConfiguration: notificationConfiguration])
