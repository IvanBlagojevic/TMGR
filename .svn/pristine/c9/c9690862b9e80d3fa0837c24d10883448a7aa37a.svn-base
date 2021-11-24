import org.gs4tr.termmanager.model.ItemStatusTypeHolder
import org.gs4tr.termmanager.service.utils.MailHelper.ReportType

organizationInfo = builder.organizationInfo([name : "My First Organization", domain: "DOMAIN", enabled: true,
                                             theme: "THEME", currencyCode: null])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

projectDetail = builder.projectDetail([languageCount     : 2, activeSubmissionCount: 0L, completedSubmissionCount: 0L, approvedTermCount: 15L,
                                       forbiddenTermCount: 0, projectDetailId: 1L, termCount: 15L, termEntryCount: 7L, termInSubmissionCount: 0L, dateModified: new Date()])

projectInfo = builder.projectInfo([enabled: true, name: "My First Project", shortCode: "MFP0001"])

tmProject = builder.tmProject([addApprovedTerms: false, defaultTermStatus: ItemStatusTypeHolder.PROCESSED,
                               organization    : tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo])

english = builder.projectUserLanguage([language: "en-US", project: tmProject])
germany = builder.projectUserLanguage([language: "de-DE", project: tmProject])

stats1 = builder.statistics([statisticsId : 1L, projectUserLanguage: english, reportType: ReportType.WEEKLY.getName(),
                             addedApproved: 0L, addedPending: 0L, approved: 0L, deleted: 0L, demoted: 0L, updated: 0L, addedOnHold: 0L,
                             onHold       : 0L, addedBlacklisted: 0L, blacklisted: 0L])
stats2 = builder.statistics([statisticsId : 2L, projectUserLanguage: english, reportType: ReportType.DAILY.getName(),
                             addedApproved: 0L, addedPending: 0L, approved: 0L, deleted: 0L, demoted: 0L, updated: 0L,
                             onHold       : 0L, addedBlacklisted: 0L, blacklisted: 0L])

statistics1 = [stats1] as List
statistics2 = [stats2] as List

stats3 = builder.statistics([statisticsId : 1L, projectUserLanguage: english, reportType: ReportType.WEEKLY.getName(),
                             addedApproved: 1L, addedPending: 1L, approved: 1L, deleted: 1L, demoted: 1L, updated: 1L, addedOnHold: 1L, onHold: 1L, addedBlacklisted: 1L, blacklisted: 1L])
stats4 = builder.statistics([statisticsId : 2L, projectUserLanguage: english, reportType: ReportType.DAILY.getName(),
                             addedApproved: 2L, addedPending: 2L, approved: 2L, deleted: 2L, demoted: 2L, updated: 2L, addedOnHold: 2L, onHold: 2L, addedBlacklisted: 2L, blacklisted: 2L])

stats5 = builder.statistics([statisticsId : 3L, projectUserLanguage: germany, reportType: ReportType.WEEKLY.getName(),
                             addedApproved: 3L, addedPending: 3L, approved: 3L, deleted: 3L, demoted: 3L, updated: 3L, addedOnHold: 3L, onHold: 3L, addedBlacklisted: 3L, blacklisted: 3L])
stats6 = builder.statistics([statisticsId : 4L, projectUserLanguage: germany, reportType: ReportType.DAILY.getName(),
                             addedApproved: 4L, addedPending: 4L, approved: 4L, deleted: 4L, demoted: 4L, updated: 4L, addedOnHold: 4L, onHold: 4L, addedBlacklisted: 4L, blacklisted: 4L])

statistics3 = [stats1, stats5,] as List
statistics4 = [stats2, stats6,] as List

statistics5 = [stats4, stats6,] as List