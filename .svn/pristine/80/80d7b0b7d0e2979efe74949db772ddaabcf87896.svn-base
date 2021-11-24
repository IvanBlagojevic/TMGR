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

parentMarkerId = "c0643514-d908-420e-a711-431b583bef5f"

command = builder.updateCommand([status: blackListed, parentMarkerId: parentMarkerId, markerId: "c0643514-d908-420e-a711-431b583bef5e", value: "Konto 1", languageId: "en-US"])

termId_01 = "c0643514-d908-420e-a711-431b583bef5f";
termId_02 = "c0643514-d908-420e-a711-431b583bef5e";
termEntryId_01 = "474e93ae-7264-4088-9d54-termentry001";
termEntryId_02 = "474e93ae-7264-4088-9d54-termentry002";

command1 = builder.updateCommand([status: null, parentMarkerId: termEntryId_02, markerId: termId_01, value: "Konto 1", languageId: "en-US"])
command2 = builder.updateCommand([status: null, parentMarkerId: termEntryId_02, markerId: termId_02, value: "Konto 2", languageId: "de-DE"])
command3 = builder.updateCommand([status: null, parentMarkerId: termId_02, markerId: "c0643514-d908-420e-a711-431b583bef5e", value: "Konto Desc", languageId: "de-DE"])

description1 = builder.description([value: "Konto 1", baseType: "ATTRIBUTE"])
description2 = builder.description([value: "Konto 2", baseType: "ATTRIBUTE"])

descriptions = [description1, description2] as List

descriptionTermEntry = builder.description([value: "TermEntry desc 1", baseType: "ATTRIBUTE"])
descriptionsTermEntry = [descriptionTermEntry] as List

term01 = builder.term([uuId      : termId_01, termEntryId: termEntryId_01, forbidden: false, status: "PROCESSED", statusOld: "PROCESSED",
                       languageId: "en-US", name: "regular term text 1", inTranslationAsSource: true, commited: false, dateModified: 1403509767L, userModified: "sdulin", descriptions: [description1] as Set])

term02 = builder.term([uuId      : termId_02, termEntryId: termEntryId_01, forbidden: false, status: "PROCESSED", statusOld: "PROCESSED",
                       languageId: "en-US", name: "regular term text 1", inTranslationAsSource: true, commited: false, dateModified: 1403509787L, userModified: "sdulin", descriptions: descriptions])

terms = [term01, term02] as Set

termEntryMap1 = ["en-US": terms] as Map

termEntry01 = builder.termEntry([dateCreated: 1403509560L, dateModified: 1403509760L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId01, languageTerms: termEntryMap1])

emptyTermEntry = builder.termEntry([dateCreated: 1403509560L, dateModified: 1403509760L, userCreated: "sdulin", userModified: "sdulin",
                                    uuId       : termEntryId_02, languageTerms: null, projectId: 1L])

termEntryForUpdate = builder.termEntry([dateCreated: 1403509560L, dateModified: 1403509760L, userCreated: "sdulin", userModified: "sdulin",
                                        uuId       : parentMarkerId, languageTerms: termEntryMap1, descriptions: descriptionsTermEntry])
