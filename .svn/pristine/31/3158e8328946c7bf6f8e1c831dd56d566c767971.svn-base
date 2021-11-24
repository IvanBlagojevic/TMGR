package groovy.service

import org.gs4tr.termmanager.model.ItemStatusTypeHolder
import org.gs4tr.termmanager.model.event.ProjectDetailInfo

date = new Date();

statusProcessed = builder.itemStatusType(name: "processed")

blackListed = ItemStatusTypeHolder.BLACKLISTED.getName();

organizationInfo = builder.organizationInfo([name : "Organization Nikon", domain: "DOMAIN", enabled: true,
                                             theme: "THEME", currencyCode: null])

projectLanguageDetail = builder.projectLanguageDetail([languageId : "en-US", termCount: 0L, approvedTermCount: 0L, forbiddenTermCount: 0L,
                                                       userDetails: [] as Set]);


projectDetail = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 0L, completedSubmissionCount: 0L,
                                       dateModified         : date, forbiddenTermCount: 0L, languageCount: 0L, languageDetails: [projectLanguageDetail] as Set, project: null, projectDetailId: 1L,
                                       termCount            : 0L, termEntryCount: 0L, termInSubmissionCount: 0L, userDetails: null])

projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Skype", shortCode: "SKYPE000001"])

projectDetailInfo = new ProjectDetailInfo(1L)
projectDetailInfo.setDateModified(new Date(1403510760L))

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusProcessed,
                               metadata        : null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
                               projectLanguages: null])

termEntryUuId01 = "addTerm-termentry001";

command = builder.updateCommand([status: blackListed, parentMarkerId: "c0643514-d908-420e-a711-431b583bef5f", markerId: "c0643514-d908-420e-a711-431b583bef5e", value: "Konto 1", languageId: "en-US"])

termId_01 = "c0643514-d908-420e-a711-431b583bef5f";
termId_02 = "c0643514-d908-420e-a711-431b583bef5e";
termEntryId_01 = "474e93ae-7264-4088-9d54-termentry001";

description1 = builder.description([value: "Konto 1", baseType: "NOTE"])
description2 = builder.description([value: "Konto 2", baseType: "NOTE"])

descriptions = [description1, description2] as List

term01 = builder.term([uuId      : termId_01, termEntryId: termEntryId_01, forbidden: false, status: "PROCESSED", statusOld: "PROCESSED",
                       languageId: "en-US", name: "regular term text 1", inTranslationAsSource: true, commited: false, descriptions: [description1] as Set])

term02 = builder.term([uuId      : termId_02, termEntryId: termEntryId_01, forbidden: false, status: "PROCESSED", statusOld: "PROCESSED",
                       languageId: "en-US", name: "regular term text 1", inTranslationAsSource: true, commited: false, descriptions: descriptions])

terms = [term01, term02] as Set

termEntryMap1 = ["en-US": terms] as Map

termEntryMap2 = ["en-US": [term02] as Set] as Map

termEntry01 = builder.termEntry([dateCreated: 1403509560L, dateModified: null, userCreated: "sdulin", userModified: null,
                                 uuId       : termEntryUuId01, languageTerms: termEntryMap1])

termEntry02 = builder.termEntry([dateCreated: 1403509572L, dateModified: null, userCreated: "sdulin", userModified: null,
                                 uuId       : termEntryUuId01, languageTerms: termEntryMap2])