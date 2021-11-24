import org.gs4tr.foundation.modules.entities.model.UserTypeEnum
import org.gs4tr.termmanager.model.search.ItemFolderEnum

userLanguages = ["en-US", "de-DE"] as Set

projectUserLanguages = [1L: userLanguages, 2L: userLanguages] as Map

userInfo = builder.userInfo(userType: UserTypeEnum.POWER_USER)

user = builder.TmUserProfile([userProfileId: 1L, projectUserLanguages: projectUserLanguages, userInfo: userInfo])

pagedListInfo = builder.pagedListInfo([index: 0, indexesSize: 5, size: 50, sortDirection: "ASCENDING", sortProperty: null])

searchRequest = builder.userProjectSearchRequest([folder    : ItemFolderEnum.PROJECTDETAILS, languageIds: ["en-US", "de-DE"] as Set,
                                                  projectIds: [1L] as List, user: user])
searchRequest1 = builder.userProjectSearchRequest([folder    : ItemFolderEnum.PROJECTDETAILS, languageIds: ["en-US", "de-DE"] as Set,
                                                   projectIds: [1L, 2L] as List, user: user])

organizationInfo = builder.organizationInfo([name : "My First Organization", domain: "DOMAIN", enabled: true,
                                             theme: "THEME", currencyCode: null])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])
dateModified = new Date()

projectLanguageDetailOne = builder.projectLanguageDetail([languageId           : "en-US", approvedTermCount: 5, forbiddenTermCount: 5,
                                                          termInSubmissionCount: 5, termCount: 15, dateModified: dateModified, disabled: false])
projectLanguageDetailTwo = builder.projectLanguageDetail([languageId           : "de-DE", approvedTermCount: 7, forbiddenTermCount: 3,
                                                          termInSubmissionCount: 5, termCount: 15, dateModified: dateModified, disabled: false])
projectLanguageDetailThree = builder.projectLanguageDetail([languageId           : "fr-FR", approvedTermCount: 10, forbiddenTermCount: 0,
                                                            termInSubmissionCount: 0, termCount: 10, dateModified: dateModified, disabled: false])
projectLanguageDetailFour = builder.projectLanguageDetail([languageId           : "en", approvedTermCount: 1, forbiddenTermCount: 3,
                                                           termInSubmissionCount: 1, termCount: 5, dateModified: dateModified, disabled: false])

projectLanguageDetails = [
        projectLanguageDetailOne,
        projectLanguageDetailTwo,
        projectLanguageDetailThree,
        projectLanguageDetailFour] as List

skypeProjectDetail = builder.projectDetail([activeSubmissionCount: 5L, approvedTermCount: 23L, completedSubmissionCount: 0L,
                                            dateModified         : dateModified, forbiddenTermCount: 11L, languageCount: 4L, languageDetails: projectLanguageDetails,
                                            project              : null, projectDetailId: 1L, termCount: 45L, termEntryCount: 0L, termInSubmissionCount: 11L, userDetails: null])

meddraProjectDetail = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 0L, completedSubmissionCount: 0L,
                                             dateModified         : dateModified, forbiddenTermCount: 0, languageCount: 2, languageDetails: [] as List,
                                             project              : null, projectDetailId: 3L, termCount: 0L, termEntryCount: 0L, termInSubmissionCount: 0L, userDetails: null])

skypeProjectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Skype", shortCode: "SKY000001"])
medraProjectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Meddra", shortCode: "MEDD000001"])

projectLanguageOne = builder.projectLanguage([language: "en-US"])
projectLanguageTwo = builder.projectLanguage([language: "de-DE"])
projectLanguageThree = builder.projectLanguage([language: "fr-FR"])
projectLanguageFour = builder.projectLanguage([language: "en"])

projectLanguages = [
        projectLanguageOne,
        projectLanguageTwo,
        projectLanguageThree,
        projectLanguageFour] as Set

meddraProjectLanguages = [
        projectLanguageOne,
        projectLanguageTwo] as Set

statusProcessed = builder.itemStatusType(name: "processed")

skype = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusProcessed,
                           metadata        : null, organization: tmOrganization, projectDetail: skypeProjectDetail, projectId: 1L, projectInfo: skypeProjectInfo,
                           projectLanguages: projectLanguages])

meddra = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusProcessed,
                            metadata        : null, organization: tmOrganization, projectDetail: meddraProjectDetail, projectId: 1L, projectInfo: medraProjectInfo,
                            projectLanguages: meddraProjectLanguages])

projectLanguageDetailFive = builder.projectLanguageDetail([languageId           : "it-IT", approvedTermCount: 12L, forbiddenTermCount: 4,
                                                           termInSubmissionCount: 0, termCount: 15, dateModified: dateModified, disabled: false])

projectLanguageDetailsOne = [
        projectLanguageDetailOne,
        projectLanguageDetailFive] as List

nikonProjectDetail = builder.projectDetail([activeSubmissionCount: 3L, approvedTermCount: 17L, completedSubmissionCount: 0L,
                                            dateModified         : dateModified, forbiddenTermCount: 9L, languageCount: 2, languageDetails: projectLanguageDetailsOne,
                                            project              : null, projectDetailId: 2L, termCount: 30L, termEntryCount: 15L, termInSubmissionCount: 5L, userDetails: null])

nikonProjectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Nikon", shortCode: "NIK000001"])

projectLanguage5 = builder.projectLanguage([language: "en-US"])
projectLanguage6 = builder.projectLanguage([language: "it-IT"])

nikonProjectLanguages = [
        projectLanguage5,
        projectLanguage6] as Set

statusProcessed = builder.itemStatusType(name: "processed")

nikon = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusProcessed,
                           metadata        : null, organization: tmOrganization, projectDetail: nikonProjectDetail, projectId: 2L, projectInfo: nikonProjectInfo,
                           projectLanguages: nikonProjectLanguages])

skypeProjectDetail.setProject(skype);
meddraProjectDetail.setProject(meddra);
nikonProjectDetail.setProject(nikon);

meddraProjectDetails = [meddraProjectDetail] as List

skypeProjectDetails = [skypeProjectDetail] as List

projectDetails = [
        meddraProjectDetail,
        nikonProjectDetail] as List

projectReportOne = builder.projectReport([languageCount: 31L, languageId: "en-US", projectName: "My First Project", termCount: 1L]);
projectReportTwo = builder.projectReport([languageCount: 11L, languageId: "de-DE", projectName: "My Second Project", termCount: 23L]);

reportList = [
        projectReportOne,
        projectReportTwo] as List

sendConnectionPolicy = [
        "POLICY_TM_USER_SEND_CONNECTION"
] as List
