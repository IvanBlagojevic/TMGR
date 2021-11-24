import org.gs4tr.termmanager.model.ItemStatusTypeHolder

date = new Date();

statusProcessed = builder.itemStatusType(name: "processed")

processed = ItemStatusTypeHolder.PROCESSED.getName();
blackListed = ItemStatusTypeHolder.BLACKLISTED.getName();
onHold = ItemStatusTypeHolder.ON_HOLD.getName();

organizationInfo = builder.organizationInfo([name : "Nikon", domain: "DOMAIN", enabled: true,
                                             theme: "THEME", currencyCode: null])

detailState = builder.detailState([activeSubmissionCount: 0L, approvedTermCount: 1L, completedSubmissionCount: 0L, dateModified: date,
                                   forbiddenTermCount   : 0L, termCount: 1L, termEntryCount: 1L, termInSubmissionCount: 0L])

projectLanguageDetail = builder.projectLanguageDetail([languageId: "en-US", termCount: 1L, approvedTermCount: 1L, userDetails: [] as Set]);


projectDetail = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 1L, completedSubmissionCount: 0L,
                                       dateModified         : date, forbiddenTermCount: 0L, languageCount: 1L, languageDetails: [projectLanguageDetail] as Set,
                                       project              : null, projectDetailId: 1L,
                                       termCount            : 1L, termEntryCount: 1L, termInSubmissionCount: 0L, userDetails: null])

projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Skype", shortCode: "SKYPE000001"])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusProcessed,
                               metadata        : null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
                               projectLanguages: null])

termEntryUuId01 = "updateTerm-termentry001";
termId_01 = "474e93ae-7264-4088-9d54-term00000001";
termId_02 = "474e93ae-7264-4088-9d54-term00000002";

enTerm = builder.term([disabled   : false, forbidden: false, inTranslationAsSource: false, dateCreated: date.getTime(), dateModified: date.getTime(),
                       first      : true, languageId: "en-US", name: "Business Control Panel", projectId: 1L, status: processed, termEntryId: termEntryUuId01,
                       userCreated: "super", userModified: "super", uuId: termId_01])

enTerm1 = builder.term([disabled   : false, forbidden: false, inTranslationAsSource: false, dateCreated: date.getTime(), dateModified: date.getTime(),
                        first      : true, languageId: "en-US", name: "mainTermName", projectId: 1L, status: onHold, termEntryId: termEntryUuId01,
                        userCreated: "super", userModified: "super", uuId: termId_01])

enTerm2 = builder.term([disabled   : false, forbidden: false, inTranslationAsSource: false, dateCreated: date.getTime(), dateModified: date.getTime(),
                        first      : false, languageId: "en-US", name: "synonymName", projectId: 1L, status: processed, termEntryId: termEntryUuId01,
                        userCreated: "super", userModified: "super", uuId: termId_02])

enTerm3 = builder.term([disabled   : false, forbidden: false, inTranslationAsSource: false, dateCreated: date.getTime(), dateModified: date.getTime(),
                        first      : true, languageId: "en-US", name: "mainTermName", projectId: 1L, status: processed, termEntryId: termEntryUuId01,
                        userCreated: "super", userModified: "super", uuId: termId_01])

enTerm4 = builder.term([disabled   : false, forbidden: false, inTranslationAsSource: false, dateCreated: date.getTime(), dateModified: date.getTime(),
                        first      : false, languageId: "en-US", name: "synonymName", projectId: 1L, status: onHold, termEntryId: termEntryUuId01,
                        userCreated: "super", userModified: "super", uuId: termId_02])

languageTermsMap = ["en-US": [enTerm] as Set] as Map

languageTermsMap1 = ["en-US": [enTerm1, enTerm2] as Set] as Map

languageTermsMap2 = ["en-US": [enTerm3, enTerm4] as Set] as Map

termEntry = builder.termEntry([uuId       : termEntryUuId01, languageTerms: languageTermsMap, projectId: 1L, shortCode: "SKYPE000001",
                               projectName: "Skype", userCreated: "super", userModified: "super"])

termEntry1 = builder.termEntry([uuId       : termEntryUuId01, languageTerms: languageTermsMap1, projectId: 1L, shortCode: "SKYPE000001",
                                projectName: "Skype", userCreated: "super", userModified: "super"])

termEntry2 = builder.termEntry([uuId       : termEntryUuId01, languageTerms: languageTermsMap2, projectId: 1L, shortCode: "SKYPE000001",
                                projectName: "Skype", userCreated: "super", userModified: "super"])

command = builder.updateCommand([status    : blackListed, markerId: "474e93ae-7264-4088-9d54-term00000001", value: "Business Control Panel",
                                 languageId: "en-US"])

command1 = builder.updateCommand([status    : processed, markerId: termId_02, value: "mainTermName", parentMarkerId: termEntryUuId01,
                                  languageId: "en-US"])

command2 = builder.updateCommand([status    : processed, markerId: termId_01, value: "synonymName", parentMarkerId: termEntryUuId01,
                                  languageId: "en-US"])

command3 = builder.updateCommand([status    : processed, markerId: termId_02, value: "mainTermName", parentMarkerId: termEntryUuId01,
                                  languageId: "en-US"])

command4 = builder.updateCommand([status    : processed, markerId: termId_01, value: "synonymName", parentMarkerId: termEntryUuId01,
                                  languageId: "en-US"])


demoteToPendingPoliciesIds = [
        "POLICY_TM_TERM_DEMOTE_TO_PENDING_APPROVAL_TERM_STATUS",
        "POLICY_TM_TERM_DEMOTE_TERM_STATUS"
] as List

demoteToWaitingPoliciesIds = [
        "POLICY_TM_TERM_DEMOTE_TO_ON_HOLD_TERM_STATUS",
        "POLICY_TM_TERM_ON_HOLD_TERM_STATUS"
] as List




