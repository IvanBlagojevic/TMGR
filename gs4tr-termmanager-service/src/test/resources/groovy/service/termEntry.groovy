package groovy.service

import org.gs4tr.termmanager.model.ProjectLanguageUserDetail
import org.gs4tr.termmanager.model.event.DetailState
import org.gs4tr.termmanager.model.glossary.Description

import java.sql.Timestamp

date = new Date()
timesatmp = new Timestamp(date.getTime())

itemStatusType = builder.itemStatusType([name: "intranslationreview"])
priority = builder.entityStatusPriority([dateCompleted: null, priority: 2, priorityAssignee: 3,
                                         status       : itemStatusType])

statusWainting = builder.itemStatusType(name: "waiting")
date = new Date()

organizationInfo = builder.organizationInfo([name : "My First Organization", domain: "DOMAIN", enabled: true,
                                             theme: "THEME", currencyCode: null])

Set<ProjectLanguageUserDetail> userDetails = new HashSet<ProjectLanguageUserDetail>()

projectLanguageDetail1 = builder.projectLanguageDetail([languageId: "en-US", userDetails: userDetails])
projectLanguageDetail2 = builder.projectLanguageDetail([languageId: "de-DE", userDetails: userDetails])
projectLanguageDetail3 = builder.projectLanguageDetail([languageId: "fr-FR", userDetails: userDetails])

languageDetails = [
        projectLanguageDetail1,
        projectLanguageDetail2,
        projectLanguageDetail3] as Set

detailState = new DetailState()

languageDetailState = ["en-US": detailState, "de-DE": detailState, "fr-FR": detailState] as Map

projectDetail = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 0L, completedSubmissionCount: 0L,
                                       dateModified         : date, forbiddenTermCount: 0, languageCount: 3L, languageDetails: languageDetails, project: null, projectDetailId: 0L, termCount: 1L, termEntryCount: 1L, termInSubmissionCount: 0L, userDetails: null])


projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "My First Project", shortCode: "MYF000001"])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusWainting,
                               metadata        : null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
                               projectLanguages: null])

uCommand1 = builder.updateCommand([markerId: UUID.randomUUID().toString(), value: "some value1", itemType: "term", languageId: "en-US", command: "add"])
uCommand2 = builder.updateCommand([markerId: UUID.randomUUID().toString(), value: "some value2", itemType: "term", languageId: "de-DE", command: "add"])

sourceCommandList = [uCommand1] as List
targetCommandList = [uCommand2] as List

updateCommand = builder.updateCommand([value: "some updated value", itemType: "term", languageId: "en-US", command: "update"])
updateCommands = [updateCommand] as List


tu = builder.translationUnit([sourceTermUpdateCommands: sourceCommandList, targetTermUpdateCommands: targetCommandList])

tuList = [tu] as List

termEntryId_01 = "474e93ae-7264-4088-9d54-termentry001"
termId_01 = "474e93ae-7264-4088-9d54-term00000001"

term1 = builder.term([uuId: termId_01, name: "test term 1", projectId: 1L, termEntryId: termEntryId_01, forbidden: false, status: "WAITING", languageId: "en-US"])

termList1 = [term1] as Set
languageTerms1 = ["en-US": termList1] as Map

description1 = builder.description([baseType: "NOTE", type: "context", value: "some description context"])
description2 = builder.description([baseType: "NOTE", type: "definition", value: "some description definition"])
descriptions = [description1, description2] as Set

descriptionId_03 = "474e93ae-7264-4088-9d54-desc00000003"

description_03 = builder.description([
        uuid    : descriptionId_03,
        baseType: "ATTRIBUTE",
        type    : "definition",
        value   : "Term definition value"
])

definitionAttribute = builder.attribute([name: "definition", renameValue: "renamed value"])

termEntry1 = builder.termEntry([uuId: termEntryId_01, languageTerms: languageTerms1, descriptions: [description_03] as Set])

termEntries = [termEntry1] as List

/* Export document WS */
searchRequest = builder.termEntrySearchRequest([languagesToExport: ["en-US", "de-DE"] as List, sourceLocale: "en-US", targetLocales: ["de-DE"] as List, forbidden: true,
                                                statuses         : ["PROCESSED", "BLACKLISTED"] as List])
searchRequest1 = builder.termEntrySearchRequest([languagesToExport: ["en-US", "de-DE"] as List, sourceLocale: "en-US", targetLocales: ["de-DE"] as List, forbidden: true,
                                                 statuses         : ["PROCESSED", "BLACKLISTED"] as List, firstTermEntry: true])

termUuId01 = "export-term00000001"
termUuId02 = "export-term00000002"
termUuId03 = "export-term00000003"
termUuId04 = "export-term00000004"
termUuId05 = "export-term00000005"
termUuId06 = "export-term00000006"
termUuId07 = "export-term00000007"
termUuId08 = "export-term00000008"


term01 = builder.term([uuId: termUuId01, name: "term test 1 source", languageId: "en-US", dateCreated: 1403509560L, dateModified: 1403509577L, status: "PROCESSED", userCreated: "sdulin"])
term02 = builder.term([uuId: termUuId02, name: "term test 2 target", languageId: "de-DE", dateCreated: 1403509561L, dateModified: 1403509578L, status: "PROCESSED", userCreated: "sdulin"])
term03 = builder.term([uuId: termUuId03, name: "term test 3 source", languageId: "en-US", dateCreated: 1403509562L, dateModified: 1403509579L, status: "PROCESSED", userCreated: "sdulin"])
term04 = builder.term([uuId: termUuId04, name: "term test 4 target", languageId: "de-DE", dateCreated: 1403509563L, dateModified: 1403509580L, status: "PROCESSED", userCreated: "sdulin"])
term05 = builder.term([uuId     : termUuId05, name: "term test 5 target", languageId: "de-DE", dateCreated: 1403509564L, dateModified: 1403509581L, status: "BLACKLISTED", userCreated: "sdulin",
                       forbidden: true])

term06 = builder.term([uuId        : termUuId06, name: "en term text", languageId: "en-US", dateCreated: 1403509560L, dateModified: 1403509577L, status: "PROCESSED",
                       descriptions: [description_03] as Set])
term07 = builder.term([uuId: termUuId07, name: "fr term text", languageId: "fr-FR", dateCreated: 1403509560L, dateModified: 1403509577L, status: "PROCESSED"])
term08 = builder.term([uuId: termUuId08, name: "de term text", languageId: "de-DE", dateCreated: 1403509560L, dateModified: 1403509577L, status: "BLACKLISTED"])

termEntryMap1 = ["en-US": [term01] as Set, "de-DE": [term02] as Set] as Map
termEntryMap2 = ["en-US": [term03] as Set, "de-DE": [term04, term05] as Set] as Map
termEntryMap3 = ["en-US": [term06] as Set, "fr-FR": [term07] as Set, "de-DE": [term08] as Set] as Map

termEntryUuId01 = "474e93ae-7264-4088-9d54-termentry001"
termEntryUuId02 = "474e93ae-7264-4088-9d54-termentry002"
termEntryUuId03 = "474e93ae-7264-4088-9d54-termentry003"
termEntryUuId04 = "474e93ae-7264-4088-9d54-termentry004"

termEntry01 = builder.termEntry([dateCreated: 1403509560L, dateModified: 1303509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId01, languageTerms: termEntryMap1])
termEntry02 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "njonhson", userModified: "njonhson",
                                 uuId       : termEntryUuId02, languageTerms: termEntryMap2])
termEntry03 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId03, languageTerms: termEntryMap3])

targetTermUpdateCommand1 = builder.updateCommand([command: "update", itemType: "term", value: "term test 1 source", languageId: "de-DE",
                                                  status : "PROCESSED", markerId: termUuId02, projectId: 1L])
targetTermUpdateCommand2 = builder.updateCommand([command: "add", itemType: "term", value: "term test 2 target", languageId: "de-DE",
                                                  status : "PROCESSED", markerId: "6327d684-f2fd-4d01-8fa5-03cd53b52fe8", projectId: 1L])

targetTermUpdateCommands = [
        targetTermUpdateCommand1,
        targetTermUpdateCommand2] as List

translationUnit = builder.translationUnit([termEntryId             : "07a3f037-bc0a-4b4d-8684-808f2f9868bc", sourceTermUpdateCommands: null,
                                           targetTermUpdateCommands: targetTermUpdateCommands])

translationUnitsForUpdate = [translationUnit] as List

termentries = [termEntry01, termEntry02] as List

termEntries1 = [termEntry03] as List

forbiddenTerms = [term05] as List

// configuration for deleteTermEntryDescriptionsByTypeTest()
descriptionId_01 = "474e93ae-7264-4088-9d54-desc00000001"

description_01 = builder.description([
        uuid    : descriptionId_01,
        baseType: "ATTRIBUTE",
        type    : "definition",
        value   : "Term entry definition value"
])

descriptionId_02 = "474e93ae-7264-4088-9d54-desc00000002"

description_02 = builder.description([
        uuid    : descriptionId_02,
        baseType: "ATTRIBUTE",
        type    : "definition",
        value   : ""
])

searchRequest2 = builder.termEntrySearchRequest([languagesToExport: ["en-US", "de-DE"] as List, sourceLocale: "en-US",
                                                 targetLocales    : ["de-DE"] as List, statuses: ["PROCESSED", "BLACKLISTED"] as List, forbidden: true, targetTermSearch: true,
                                                 statusRequestList: ["PROCESSED", "BLACKLISTED"] as List, projectId: 1L, includeSource: true,
                                                 termAttributes   : ["definition"] as List, descriptions: [description_02] as List])

termEntryUuId04 = "474e93ae-7264-4088-9d54-termentry004"

termEntry04 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId04, languageTerms: termEntryMap1, descriptions: [description_01] as Set])

termEntries2 = [
        termEntry02,
        termEntry03,
        termEntry04] as List

languageIds = ["en-US", "de-DE"] as List

// Additional test configuration for TERII-3640

termEntries3 = [termEntry01] as List

targetTermUpdateCommand1 = builder.updateCommand([command: "update", itemType: "term", value: "term test 1 source", languageId: "de-DE",
                                                  status : "PROCESSED", markerId: termUuId02, projectId: 1L])
targetTermUpdateCommand2 = builder.updateCommand([command: "add", itemType: "term", value: "term test 2 target", languageId: "de-DE",
                                                  status : "PROCESSED", markerId: "6327d684-f2fd-4d01-8fa5-03cd53b52fe8", projectId: 1L])

targetTermUpdateCommands = [
        targetTermUpdateCommand1,
        targetTermUpdateCommand2] as List

translationUnit = builder.translationUnit([termEntryId             : "07a3f037-bc0a-4b4d-8684-808f2f9868bc", sourceTermUpdateCommands: null,
                                           targetTermUpdateCommands: targetTermUpdateCommands])

translationUnits = [translationUnit] as List

// Translation unit for Term Entry note
updateCommandTermEntryNote = builder.updateCommand([command: "add", itemType: "description", value: "term entry note", languageId: null,
                                                    status : null, markerId: termUuId02, parentMarkerId: termEntryId_01])

translationUnitAddTermEntryNote = builder.translationUnit([termEntryId: "07a3f037-bc0a-4b4d-8684-808f2f9868bc", sourceTermUpdateCommands: null],
        targetTermUpdateCommands: [updateCommandTermEntryNote] as List)

translationUnitsTermEntryNote = [translationUnitAddTermEntryNote] as List

//TestCase for export term entries with description filter

description3 = builder.description([baseType: Description.NOTE, type: "customer", value: "PPD"])
description4 = builder.description([baseType: Description.NOTE, type: "customer", value: "Disney"])

termDescriptions1 = [description3] as Set
termDescriptions2 = [description4] as Set

termUuId09 = "export-term00000009"
termUuId10 = "export-term00000010"
termUuId11 = "export-term00000011"
termUuId12 = "export-term00000012"
termUuId13 = "export-term00000013"
termUuId14 = "export-term00000014"
termUuId15 = "export-term00000015"
termUuId16 = "export-term00000016"
termUuId17 = "export-term00000017"
termUuId18 = "export-term00000018"
termUuId19 = "export-term00000019"
termUuId20 = "export-term00000020"
termUuId21 = "export-term00000021"
termUuId22 = "export-term00000022"
termUuId23 = "export-term00000023"
termUuId24 = "export-term00000024"
termUuId25 = "export-term00000025"


term09 = builder.term([uuId: termUuId09, name: "america", languageId: "en-US", status: "PROCESSED", userCreated: "sdulin", descriptions: termDescriptions1])
term10 = builder.term([uuId: termUuId10, name: "france", languageId: "fr-FR", status: "PROCESSED", userCreated: "sdulin"])

termEntryMap4 = ["en-US": [term09] as Set, "fr-FR": [term10] as Set] as Map

termEntryUuId05 = "474e93ae-7264-4088-9d54-termentry005"

termEntry05 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId05, languageTerms: termEntryMap4])

term11 = builder.term([uuId: termUuId11, name: "america1", languageId: "en-US", status: "PROCESSED", userCreated: "sdulin"])
term12 = builder.term([uuId: termUuId12, name: "france1", languageId: "fr-FR", status: "PROCESSED", userCreated: "sdulin"])
term13 = builder.term([uuId: termUuId13, name: "greatBritain", languageId: "en-GB", status: "PROCESSED", userCreated: "sdulin", descriptions: termDescriptions1])

termEntryMap5 = ["en-US": [term11] as Set, "fr-FR": [term12] as Set, "en-GB": [term13] as Set] as Map

termEntryUuId06 = "474e93ae-7264-4088-9d54-termentry006"

termEntry06 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId06, languageTerms: termEntryMap5])

term14 = builder.term([uuId: termUuId14, name: "america2", languageId: "en-US", status: "PROCESSED", userCreated: "sdulin"])
term15 = builder.term([uuId: termUuId15, name: "france2", languageId: "fr-FR", status: "PROCESSED", userCreated: "sdulin", descriptions: termDescriptions1])

termEntryMap6 = ["en-US": [term14] as Set, "fr-FR": [term15] as Set] as Map

termEntryUuId07 = "474e93ae-7264-4088-9d54-termentry007"

termEntry07 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId07, languageTerms: termEntryMap6])


term16 = builder.term([uuId: termUuId16, name: "america3", languageId: "en-US", status: "PROCESSED", userCreated: "sdulin"])
term17 = builder.term([uuId: termUuId17, name: "france3", languageId: "fr-FR", status: "PROCESSED", userCreated: "sdulin", descriptions: termDescriptions2])

termEntryMap7 = ["en-US": [term16] as Set, "fr-FR": [term17] as Set] as Map

termEntryUuId08 = "474e93ae-7264-4088-9d54-termentry008"

termEntryUuId09 = "474e93ae-7264-4088-9d54-termentry009"
termEntryUuId10 = "474e93ae-7264-4088-9d54-termentry010"
termEntryUuId11 = "474e93ae-7264-4088-9d54-termentry011"
termEntryUuId12 = "474e93ae-7264-4088-9d54-termentry012"
termEntryUuId13 = "474e93ae-7264-4088-9d54-termentry013"


termEntry08 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId08, languageTerms: termEntryMap7])

termEntries4 = [termEntry05, termEntry06, termEntry07, termEntry08] as List

searchRequest3 = builder.termEntrySearchRequest([languagesToExport: ["en-US", "fr-FR"] as List, sourceLocale: "en-US",
                                                 targetLocales    : ["fr-FR"] as List, statuses: ["PROCESSED"] as List, forbidden: true, targetTermSearch: true,
                                                 statusRequestList: ["PROCESSED"] as List, projectId: 1L, includeSource: true,
                                                 termAttributes   : ["customer"] as List, descriptions: [description3] as List])

term18 = builder.term([uuId: termUuId18, name: "approvedTermEnglish", languageId: "en-US", status: "PROCESSED", dateModified: 1703509560L])
term19 = builder.term([uuId: termUuId19, name: "blacklistedTermEnglish", languageId: "en-US", status: "BLACKLISTED", dateModified: 1703509560L])
term20 = builder.term([uuId: termUuId20, name: "pendingApprovalTermEnglish", languageId: "en-US", status: "WAITING", dateModified: 1703509560L])

term21 = builder.term([uuId: termUuId21, name: "approvedTermGermany", languageId: "de-DE", status: "PROCESSED", dateModified: 1703509560L])
term22 = builder.term([uuId: termUuId22, name: "blacklistedTermGermany", languageId: "de-DE", status: "BLACKLISTED", dateModified: 1703509560L])
term23 = builder.term([uuId: termUuId23, name: "pendingApprovalTermGermany", languageId: "de-DE", status: "WAITING", dateModified: 1703509560L])

term24 = builder.term([uuId: termUuId24, name: "englishTerm", languageId: "en-US", status: "PROCESSED"])
term25 = builder.term([uuId: termUuId25, name: "germanTerm", languageId: "de-DE", status: "PROCESSED"])


termEntryMap8 = ["en-US": [term18] as Set, "de-DE": [term21] as Set] as Map
termEntryMap9 = ["en-US": [term19] as Set, "de-DE": [term22] as Set] as Map
termEntryMap10 = ["en-US": [term20] as Set, "de-DE": [term23] as Set] as Map

termEntryMap11 = ["en-US": [term24] as Set] as Map
termEntryMap12 = [:]

termEntry09 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId09, languageTerms: termEntryMap8, projectId: 1L])
termEntry10 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId10, languageTerms: termEntryMap9, projectId: 1L])
termEntry11 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId11, languageTerms: termEntryMap10, projectId: 1L])
termEntry12 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId12, languageTerms: termEntryMap11, projectId: 1L])

termEntry13 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "sdulin", userModified: "sdulin",
                                 uuId       : termEntryUuId13, languageTerms: termEntryMap12, projectId: 1L])

termentries5 = [termEntry09, termEntry10, termEntry11] as List

date = new Date(1703509559L)

searchRequest4 = builder.termEntrySearchRequest([languagesToExport: ["en-US"] as List, sourceLocale: "en-US", targetLocales: ["de-DE"] as List, sharePendingTerms: true, forbidden: true, targetTermSearch: true,
                                                 statusRequestList: ["PROCESSED"] as List, projectId: 1L, includeSource: true, dateModifiedFrom: date])

targetTermUpdateCommand5 = builder.updateCommand([command: "remove", itemType: "term", value: "germanTerm", languageId: "de-DE",
                                                  status : "PROCESSED", markerId: termUuId25, projectId: 1L])

sourceTermUpdateCommand5 = builder.updateCommand([command: "remove", itemType: "term", value: "englishTerm", languageId: "en-EN",
                                                  status : "PROCESSED", markerId: termUuId24, projectId: 1L])

sourceTermUpdateCommand6 = builder.updateCommand([command: "add", itemType: "term", value: "englishTerm", languageId: "en-US",
                                                  status : "PROCESSED", markerId: termUuId24, projectId: 1L])

sourceTermUpdateCommand7 = builder.updateCommand([command: "update", itemType: "term", value: "englishTerm", languageId: "en-US",
                                                  status : "PROCESSED", markerId: termUuId24, projectId: 1L])

targetTermUpdateCommands5 = [targetTermUpdateCommand5] as List

sourceTermUpdateCommands5 = [sourceTermUpdateCommand5] as List


sourceTermUpdateCommands6 = [sourceTermUpdateCommand6] as List

sourceTermUpdateCommands7 = [sourceTermUpdateCommand7] as List


translationUnit5 = builder.translationUnit([termEntryId             : termEntryUuId12, sourceTermUpdateCommands: null,
                                            targetTermUpdateCommands: targetTermUpdateCommands5])

translationUnit6 = builder.translationUnit([termEntryId             : termEntryUuId12, sourceTermUpdateCommands: null,
                                            targetTermUpdateCommands: sourceTermUpdateCommands5])


translationUnit7 = builder.translationUnit([termEntryId             : termEntryUuId12, sourceTermUpdateCommands: sourceTermUpdateCommands6,
                                            targetTermUpdateCommands: null])

translationUnit8 = builder.translationUnit([termEntryId             : termEntryUuId12, sourceTermUpdateCommands: sourceTermUpdateCommands7,
                                            targetTermUpdateCommands: null])

targetTermUpdateTranslationUnit = [translationUnit5] as List

sourceTermUpdateTranslationUnit = [translationUnit6] as List

addTermTranslationUnit = [translationUnit7] as List

updateTermTranslationUnit = [translationUnit8] as List
