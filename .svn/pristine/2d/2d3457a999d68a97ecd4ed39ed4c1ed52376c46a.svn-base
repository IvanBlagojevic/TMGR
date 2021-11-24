package groovy.service

import org.gs4tr.termmanager.model.ProjectLanguageUserDetail
import org.gs4tr.termmanager.model.event.DetailState


statusWainting = builder.itemStatusType(name: "waiting")
date = new Date();

organizationInfo = builder.organizationInfo([name : "My First Organization", domain: "DOMAIN", enabled: true,
                                             theme: "THEME", currencyCode: null])

Set<ProjectLanguageUserDetail> userDetails = new HashSet<ProjectLanguageUserDetail>();

projectLanguageDetail1 = builder.projectLanguageDetail([languageId: "en-US", userDetails: userDetails])
projectLanguageDetail2 = builder.projectLanguageDetail([languageId: "de-DE", userDetails: userDetails])
projectLanguageDetail3 = builder.projectLanguageDetail([languageId: "fr-FR", userDetails: userDetails])

languageDetails = [
        projectLanguageDetail1,
        projectLanguageDetail2,
        projectLanguageDetail3] as Set

detailState = new DetailState();

languageDetailState = ["en-US": detailState, "de-DE": detailState, "fr-FR": detailState] as Map;

projectDetail = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 0L, completedSubmissionCount: 0L,
                                       dateModified         : date, forbiddenTermCount: 0, languageCount: 3L, languageDetails: languageDetails, project: null, projectDetailId: 0L, termCount: 1L, termEntryCount: 1L, termInSubmissionCount: 0L, userDetails: null])


projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "My First Project", shortCode: "MYF000001"])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusWainting,
                               metadata        : null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
                               projectLanguages: null])

uCommand1 = builder.updateCommand([markerId: UUID.randomUUID().toString(), value: "some value1", itemType: "term", languageId: "en-US", command: "add"]);
uCommand2 = builder.updateCommand([markerId: UUID.randomUUID().toString(), value: "some value2", itemType: "term", languageId: "de-DE", command: "add"]);

sourceCommandList = [uCommand1] as List
targetCommandList = [uCommand2] as List

updateCommand = builder.updateCommand([value: "some updated value", itemType: "term", languageId: "en-US", command: "update"]);
updateCommands = [updateCommand] as List


tu = builder.translationUnit([sourceTermUpdateCommands: sourceCommandList, targetTermUpdateCommands: targetCommandList])

tuList = [tu] as List

termEntryId_01 = "474e93ae-7264-4088-9d54-termentry001";
termId_01 = "474e93ae-7264-4088-9d54-term00000001";

term1 = builder.term([uuId: termId_01, name: "test term 1", projectId: 1L, termEntryId: termEntryId_01, forbidden: false, status: "WAITING", languageId: "en-US"])

termList1 = [term1] as Set
languageTerms1 = ["en-US": termList1] as Map

description1 = builder.description([baseType: "NOTE", type: "context", value: "some description context"])
description2 = builder.description([baseType: "NOTE", type: "definition", value: "some description definition"])
descriptions = [description1, description2] as Set

descriptionId_03 = "474e93ae-7264-4088-9d54-desc00000003";

description_03 = builder.description([
        uuid    : descriptionId_03,
        baseType: "ATTRIBUTE",
        type    : "definition",
        value   : "Term definition value"
])

definitionAttribute = builder.attribute([name: "definition", renameValue: "renamed value"])

termEntry1 = builder.termEntry([uuId: termEntryId_01, languageTerms: languageTerms1, descriptions: [description_03] as Set])


searchRequest = builder.termEntrySearchRequest([languagesToExport: ["en-US", "de-DE"] as List, sourceLocale: "en-US", targetLocales: ["de-DE"] as List, forbidden: true, forbiddenTermCount: 1,
                                                statuses         : ["PROCESSED", "BLACKLISTED"] as List, firstTermEntry: true, dateModifiedFrom: new Date(0L), termAttributes: ["definition"] as List])

termUuId01 = "export-term00000001";
termUuId02 = "export-term00000002";
termUuId03 = "export-term00000003";
termUuId04 = "export-term00000004";
termUuId05 = "export-term00000005";
termUuId06 = "export-term00000006";
termUuId07 = "export-term00000007";
termUuId08 = "export-term00000008";

term01 = builder.term([uuId: termUuId01, name: "Source term 1 Approved", languageId: "en-US", dateCreated: 1403509560L, dateModified: 1403509577L, status: "PROCESSED", userCreated: "sdulin"])
term02 = builder.term([uuId: termUuId02, name: "Target term 1 Pending", languageId: "de-DE", dateCreated: 1403509561L, dateModified: 1403509578L, status: "WAITING", userCreated: "sdulin"])

term03 = builder.term([uuId: termUuId03, name: "Source term 2 Pending", languageId: "en-US", dateCreated: 1403509562L, dateModified: 1403509579L, status: "WAITING", userCreated: "sdulin"])
term04 = builder.term([uuId: termUuId04, name: "Target term 2 Approved", languageId: "de-DE", dateCreated: 1403509563L, dateModified: 1403509580L, status: "PROCESSED", userCreated: "sdulin"])

term05 = builder.term([uuId: termUuId05, name: "Source term 3 Approved", languageId: "en-US", dateCreated: 1403509564L, dateModified: 1403509581L, status: "PROCESSED", userCreated: "sdulin"])
term06 = builder.term([uuId: termUuId06, name: "Target term 3 Approved", languageId: "de-DE", dateCreated: 1403509560L, dateModified: 1403509577L, status: "PROCESSED", userCreated: "sdulin"])

term07 = builder.term([uuId: termUuId07, name: "Source term 4 OnHold", languageId: "en-US", dateCreated: 1403509560L, dateModified: 1403509577L, status: "ONHOLD", userCreated: "sdulin"])
term08 = builder.term([uuId: termUuId08, name: "Target term 4 OnHold", languageId: "de-DE", dateCreated: 1403509560L, dateModified: 1403509577L, status: "ONHOLD", userCreated: "sdulin"])

term09 = builder.term([uuId: termUuId07, name: "Source term 5 Blacklisted", languageId: "en-US", dateCreated: 1403509560L, dateModified: 1403509577L, status: "BLACKLISTED", forbidden: "true"])
term10 = builder.term([uuId: termUuId08, name: "Target term 5 Blacklisted", languageId: "de-DE", dateCreated: 1403509560L, dateModified: 1403509577L, status: "BLACKLISTED", forbidden: "true"])

term11 = builder.term([uuId: termUuId07, name: "Source term 6 Approved", languageId: "en-US", dateCreated: 1403509560L, dateModified: 1403509560L, status: "PROCESSED"])
term12 = builder.term([uuId: termUuId08, name: "Target term 6 Approved", languageId: "de-DE", dateCreated: 1403509760L, dateModified: 1403509760L, status: "PROCESSED"])


termEntryMap1 = ["en-US": [term01] as Set, "de-DE": [term02] as Set] as Map
termEntryMap2 = ["en-US": [term03] as Set, "de-DE": [term04] as Set] as Map
termEntryMap3 = ["en-US": [term05] as Set, "de-DE": [term06] as Set] as Map
termEntryMap4 = ["en-US": [term07] as Set, "de-DE": [term08] as Set] as Map
termEntryMap5 = ["en-US": [term09] as Set, "de-DE": [term10] as Set] as Map
termEntryMap6 = ["en-US": [term11] as Set, "de-DE": [term12] as Set] as Map

termEntryUuId01 = "474e93ae-7264-4088-9d54-termentry001";
termEntryUuId02 = "474e93ae-7264-4088-9d54-termentry002";
termEntryUuId03 = "474e93ae-7264-4088-9d54-termentry003";
termEntryUuId04 = "474e93ae-7264-4088-9d54-termentry004";
termEntryUuId05 = "474e93ae-7264-4088-9d54-termentry005";
termEntryUuId06 = "474e93ae-7264-4088-9d54-termentry006";

termEntry01 = builder.termEntry([dateCreated: 1403509560L, dateModified: 1303509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId01, languageTerms: termEntryMap1])
termEntry02 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId02, languageTerms: termEntryMap2])
termEntry03 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId03, languageTerms: termEntryMap3])
termEntry04 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId04, languageTerms: termEntryMap4])
termEntry05 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId05, languageTerms: termEntryMap5])
termEntry06 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId06, languageTerms: termEntryMap6])
termEntries = [termEntry01, termEntry02, termEntry03, termEntry04, termEntry05, termEntry06] as List




