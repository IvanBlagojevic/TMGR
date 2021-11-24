package groovy.service

import org.gs4tr.termmanager.model.ItemStatusTypeHolder

date = new Date();

statusProcessed = builder.itemStatusType(name: "processed")

processed = ItemStatusTypeHolder.PROCESSED.getName();
blackListed = ItemStatusTypeHolder.BLACKLISTED.getName();
waiting = ItemStatusTypeHolder.WAITING.getName();

organizationInfo = builder.organizationInfo([name : "Organization Nikon", domain: "DOMAIN", enabled: true,
                                             theme: "THEME", currencyCode: null])

detailState1 = builder.detailState([activeSubmissionCount: 0L, approvedTermCount: 0L, completedSubmissionCount: 0L, dateModified: date,
                                    forbiddenTermCount   : 0L, termCount: 0L, termEntryCount: 0L, termInSubmissionCount: 0L])

projectLanguageDetail = builder.projectLanguageDetail([languageId : "en-US", termCount: 0L, approvedTermCount: 0L, forbiddenTermCount: 0L,
                                                       userDetails: [] as Set]);


projectDetail = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 0L, completedSubmissionCount: 0L,
                                       dateModified         : date, forbiddenTermCount: 0L, languageCount: 0L, languageDetails: [projectLanguageDetail] as Set, project: null, projectDetailId: 1L,
                                       termCount            : 0L, termEntryCount: 0L, termInSubmissionCount: 0L, userDetails: null])

projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Skype", shortCode: "SKYPE000001"])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusProcessed,
                               metadata        : null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
                               projectLanguages: null])

termEntryUuId01 = "addTerm-termentry001";

emptyTermEntry = builder.termEntry([uuId        : termEntryUuId01, projectId: 1L, shortCode: "SKYPE000001", projectName: "Skype", userCreated: "marko",
                                    userModified: "marko"])

termId_01 = "c0643514-d908-420e-a711-431b583bef5f";

term01 = builder.term([uuId: termId_01, termEntryId: termEntryUuId01, forbidden: false, status: waiting, languageId: "en-US",
                       name: "Konto", inTranslationAsSource: true, commited: false, dateCreated: 1403509560L, dateModified: 1403509767L, userCreated: "marko", userModified: "marko"])

terms = [term01] as Set

termEntryMap1 = ["en-US": terms] as Map

termEntry01 = builder.termEntry([dateCreated: 1403509560L, projectId: 1L, dateModified: 1403509760L, userCreated: "marko", userModified: "marko",
                                 uuId       : termEntryUuId01, languageTerms: termEntryMap1])

command1 = builder.updateCommand([status: processed, markerId: "c0643514-d908-420e-a711-431b583bef5e", value: "Konto", languageId: "en-US"])
command2 = builder.updateCommand([status: blackListed, markerId: "c0643514-d908-420e-a711-431b583bef5e", value: "Konto", languageId: "en-US"])

