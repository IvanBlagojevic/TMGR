package groovy.glossary

descriptionId_01 = "474e93ae-7264-4088-9d54-desc00000001"
descriptionId_02 = "474e93ae-7264-4088-9d54-desc00000002"
descriptionId_03 = "474e93ae-7264-4088-9d54-desc00000003"
descriptionId_04 = "474e93ae-7264-4088-9d54-desc00000004"

description1 = builder.description([
    uuid: descriptionId_01,
    baseType: "ATTRIBUTE",
    type: "Custom1",
    value: "Term entry custom value1"
])
description2 = builder.description([
    uuid: descriptionId_02,
    baseType: "ATTRIBUTE",
    type: "abbreviatedFormFor",
    value: "Term entry custom value2"
])
description3 = builder.description([
    uuid: descriptionId_03,
    baseType: "NOTE",
    type: "Usage",
    value: "part of speech"
])
description4 = builder.description([
    uuid: descriptionId_04,
    baseType: "ATTRIBUTE",
    type: "definition",
    value: "Definition value"
])

descriptions1 = [description1, description2] as Set
descriptions2 = [description3, description4] as Set

termEntryId_01 = "474e93ae-7264-4088-9d54-termentry001"
termId_01 = "474e93ae-7264-4088-9d54-term00000001"
termId_02 = "474e93ae-7264-4088-9d54-term00000002"

term1 = builder.term([uuId: termId_01, name: "ordinary", projectId: 1L, termEntryId: termEntryId_01,
    forbidden: false, status: "PROCESSED", languageId: "en-US", inTranslationAsSource: false])
term2 = builder.term([uuId: termId_02, name: "gewöhnlich", projectId: 1L, termEntryId: termEntryId_01,
    forbidden: false, status: "PROCESSED", languageId: "de-DE", inTranslationAsSource: false, descriptions: descriptions2])

termList1 = [term1] as Set
termList2 = [term2] as Set
languageTerms1 = ["en-US": termList1, "de-DE": termList2] as Map

termEntry_01 = builder.termEntry([dateCreated: 1403509560L, dateModified:1303509560L, userCreated:"sdulin", userModified: "sdulin",
    uuId: termEntryId_01, languageTerms: languageTerms1, descriptions: descriptions1, projectId: 1L])

termEntries = [termEntry_01] as List

