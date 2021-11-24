package groovy.webservice;

invalidTermSearchCommand = builder.segmentTermSearchCommand([fuzzy        : false, maxNumFound: 100, sourceLanguage: "en-US", targetLanguages: null,
                                                             projectTicket: "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q", searchForbidden: false, segment: null])

languages = ["en-US", "de-DE"] as List

termSearchCommand = builder.segmentTermSearchCommand([fuzzy        : false, maxNumFound: 100, sourceLanguage: "en-US", targetLanguages: languages,
                                                      projectTicket: "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q", searchForbidden: false, segment: "Just an ordinary sentence."])


highlightTermSearchCommand = builder.segmentTermSearchCommand([fuzzy: true, maxNumFound: 100, sourceLanguage: "en-US", targetLanguages: languages, projectTicket: "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q", searchForbidden: false, segment: "I like to walk. Walking is my favorite hobby."])

statusProcessed = builder.itemStatusType(name: "processed")
date = new Date();

organizationInfo = builder.organizationInfo([name : "My First Organization", domain: "DOMAIN", enabled: true,
                                             theme: "THEME", currencyCode: null])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

projectDetail = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 15L, completedSubmissionCount: 0L,
                                       dateModified         : date, forbiddenTermCount: 0, languageCount: 2, languageDetails: null,
                                       project              : null, projectDetailId: 1L, termCount: 15L, termEntryCount: 7L, termInSubmissionCount: 0L, userDetails: null])

projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Skype", shortCode: "SKYPE0001"])

projectLanguage1 = builder.projectLanguage([language: "en-US"])
projectLanguage2 = builder.projectLanguage([language: "de-DE"])

projectLanguages = [
        projectLanguage1,
        projectLanguage2] as Set

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusProcessed,
                               metadata        : null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
                               projectLanguages: projectLanguages])

descriptionId_01 = "474e93ae-7264-4088-9d54-desc00000001";
descriptionId_02 = "474e93ae-7264-4088-9d54-desc00000002";
descriptionId_03 = "474e93ae-7264-4088-9d54-desc00000003";
descriptionId_04 = "474e93ae-7264-4088-9d54-desc00000004";

description1 = builder.description([
        uuid    : descriptionId_01,
        baseType: "ATTRIBUTE",
        type    : "Custom1",
        value   : "Term entry custom value1"
])
description2 = builder.description([
        uuid    : descriptionId_02,
        baseType: "ATTRIBUTE",
        type    : "abbreviatedFormFor",
        value   : "Term entry custom value2"
])
description3 = builder.description([
        uuid    : descriptionId_03,
        baseType: "NOTE",
        type    : "Usage",
        value   : "part of speech"
])
description4 = builder.description([
        uuid    : descriptionId_04,
        baseType: "ATTRIBUTE",
        type    : "definition",
        value   : "Definition value"
])

descriptions1 = [description1, description2] as Set
descriptions2 = [description3, description4] as Set

termEntryId_01 = "474e93ae-7264-4088-9d54-termentry001";
termEntryId_02 = "474e93ae-7264-4088-9d54-termentry002";
termEntryId_03 = "474e93ae-7264-4088-9d54-termentry003";
termEntryId_04 = "474e93ae-7264-4088-9d54-termentry004";
termId_01 = "474e93ae-7264-4088-9d54-term00000001";
termId_02 = "474e93ae-7264-4088-9d54-term00000002";
termId_03 = "474e93ae-7264-4088-9d54-term00000003";
termId_04 = "474e93ae-7264-4088-9d54-term00000004";
termId_05 = "474e93ae-7264-4088-9d54-term00000005";
termId_06 = "474e93ae-7264-4088-9d54-term00000006";
termId_07 = "474e93ae-7264-4088-9d54-term00000007";
termId_08 = "474e93ae-7264-4088-9d54-term00000008";

term1 = builder.term([uuId     : termId_01, name: "ordinary", projectId: 1L, termEntryId: termEntryId_01,
                      forbidden: false, status: "PROCESSED", languageId: "en-US", inTranslationAsSource: false])
term2 = builder.term([uuId     : termId_02, name: "gewöhnlich", projectId: 1L, termEntryId: termEntryId_01,
                      forbidden: false, status: "PROCESSED", languageId: "de-DE", inTranslationAsSource: false, descriptions: descriptions2])

term3 = builder.term([uuId     : termId_03, name: "walk", projectId: 1L, termEntryId: termEntryId_02,
                      forbidden: false, status: "PROCESSED", languageId: "en-US", inTranslationAsSource: false])
term4 = builder.term([uuId     : termId_04, name: "laufen", projectId: 1L, termEntryId: termEntryId_02,
                      forbidden: false, status: "PROCESSED", languageId: "en-US", inTranslationAsSource: false])

termList1 = [term1] as Set
termList2 = [term2] as Set
languageTerms1 = ["en-US": termList1, "de-DE": termList2] as Map

termList3 = [term3] as Set
termList4 = [term4] as Set
languageTerms2 = ["en-US": termList3, "de-DE": termList4] as Map

termEntry_01 = builder.termEntry([dateCreated: 1403509560L, dateModified: 1303509560L, userCreated: "sdulin", userModified: "sdulin",
                                  uuId       : termEntryId_01, languageTerms: languageTerms1, descriptions: descriptions1, projectId: 1L])

termEntries = [termEntry_01] as List

termUuId01 = "export-term00000001";
termUuId02 = "export-term00000002";
termUuId03 = "export-term00000003";
termUuId04 = "export-term00000004";
termUuId05 = "export-term00000005";

term01 = builder.term([uuId: termUuId01, name: "term test 1 source", languageId: "en-US", dateModified: 1403509560L, status: "PROCESSED"])
term06 = builder.term([uuId: termUuId03, name: "term test 1 synonym", languageId: "en-US", dateModified: 1403509567L, status: "PROCESSED"])
term02 = builder.term([uuId: termUuId02, name: "term test 2 target", languageId: "de-DE", dateModified: 1403509561L, status: "PROCESSED"])
term03 = builder.term([uuId: termUuId03, name: "term test 3 source", languageId: "en-US", dateModified: 1403509562L, status: "PROCESSED"])
term04 = builder.term([uuId: termUuId04, name: "term test 4 target", languageId: "de-DE", dateModified: 1403509563L, status: "PROCESSED"])
term05 = builder.term([uuId     : termUuId05, name: "term test 5 target", languageId: "de-DE", dateModified: 1403509564L, status: "BLACKLISTED",
                       forbidden: true])

termEntryMap1 = ["en-US": [term01, term06] as Set, "de-DE": [term02] as Set] as Map
termEntryMap2 = ["en-US": [term03] as Set, "de-DE": [term04, term05] as Set] as Map

termEntryUuId01 = "export-termentry001";
termEntryUuId02 = "export-termentry002";


termEntry_02 = builder.termEntry([dateCreated: 1403509560L, dateModified: 1303509560L, userCreated: "sdulin", userModified: "sdulin",
                                  uuId       : termEntryUuId01, languageTerms: termEntryMap1])
termEntry_03 = builder.termEntry([dateCreated: 1103509560L, dateModified: 1703509560L, userCreated: "njonhson", userModified: "njonhson",
                                  uuId       : termEntryUuId02, languageTerms: termEntryMap2])
termEntry_04 = builder.termEntry([dateCreated: 1403509560L, dateModified: 1303509560L, userCreated: "sdulin", userModified: "sdulin",
                                  uuId       : termEntryId_02, languageTerms: languageTerms2, projectId: 1L])


termEntries1 = [termEntry_02, termEntry_03] as List
termEntries2 = [termEntry_04] as List