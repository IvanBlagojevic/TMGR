package groovy.service

import org.gs4tr.termmanager.model.ItemStatusTypeHolder
import org.gs4tr.termmanager.model.event.ProjectDetailInfo

blackListed = ItemStatusTypeHolder.BLACKLISTED.getName();

date = new Date();

statusProcessed = builder.itemStatusType(name: "processed")

command = builder.updateCommand([status: blackListed, parentMarkerId: "c0643514-d908-420e-a711-431b583bef5f", markerId: "c0643514-d908-420e-a711-431b583bef5e", value: "Konto 2", languageId: "en-US"])

projectLanguageDetail = builder.projectLanguageDetail([languageId : "en-US", termCount: 0L, approvedTermCount: 0L, forbiddenTermCount: 0L,
                                                       userDetails: [] as Set]);


projectDetail = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 0L, completedSubmissionCount: 0L,
                                       dateModified         : date, forbiddenTermCount: 0L, languageCount: 0L, languageDetails: [projectLanguageDetail] as Set, project: null, projectDetailId: 1L,
                                       termCount            : 0L, termEntryCount: 0L, termInSubmissionCount: 0L, userDetails: null])

projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Skype", shortCode: "SKYPE000001"])

projectDetailInfo = new ProjectDetailInfo(1L)
projectDetailInfo.setDateModified(new Date(1403510760L))

organizationInfo = builder.organizationInfo([name : "Organization Nikon", domain: "DOMAIN", enabled: true,
                                             theme: "THEME", currencyCode: null])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusProcessed,
                               metadata        : null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
                               projectLanguages: null])

description1 = builder.description([value: "Konto 1", uuid: "c0643514-d908-420e-a711-431b583bef5e"])

description2 = builder.description([value: "Konto 2", uuid: "c0643514-d908-520e-a711-431b583bef5w"])

descriptionTermEntry = builder.description([value: "TermEntry desc 1", uuid: "c0643514-d908-420e-a711-431b583bef5e", baseType: "ATTRIBUTE"])
descriptionsTermEntry = [descriptionTermEntry] as List

termId_01 = "c0643514-d908-420e-a711-431b583bef5g";
termId_02 = "c0643514-d908-420e-a711-431b583bef8k";
termEntryId_01 = "c0643514-d908-420e-a711-431b583bef4m";
termEntryId_02 = "c0643514-d908-420e-a711-431b583bef7t";

term01 = builder.term([uuId      : termId_01, termEntryId: termEntryId_01, forbidden: false, status: "PROCESSED", statusOld: "PROCESSED",
                       languageId: "en-US", name: "regular term text 1", inTranslationAsSource: true, commited: false, descriptions: [description1] as Set])

term02 = builder.term([uuId      : termId_02, termEntryId: termEntryId_02, forbidden: false, status: "PROCESSED", statusOld: "PROCESSED",
                       languageId: "en-US", name: "regular term text 1", inTranslationAsSource: true, commited: false, descriptions: [description2] as Set])


termEntryMap1 = ["en-US": [term01, term02] as Set] as Map

termEntryUuId01 = "addTerm-termentry001";

termEntry01 = builder.termEntry([dateCreated: 1403509560L, dateModified: null, userCreated: "sdulin", userModified: null,
                                 uuId       : termEntryUuId01, languageTerms: termEntryMap1])

termEntryForUpdate = builder.termEntry([dateCreated: 1403509560L, dateModified: 1403509860L, userCreated: "sdulin", userModified: "sdulin",
                                        uuId       : termEntryUuId01, languageTerms: termEntryMap1, descriptions: descriptionsTermEntry])