package groovy.glossary

import org.gs4tr.foundation.locale.Locale
import org.gs4tr.tm3.api.QueryMatchLocation

descriptionId_01 = "474e93ae-7264-4088-9d54-desc00000001"
descriptionId_02 = "474e93ae-7264-4088-9d54-desc00000002"
descriptionId_03 = "474e93ae-7264-4088-9d54-desc00000003"
descriptionId_04 = "474e93ae-7264-4088-9d54-desc00000004"

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

descriptions1 = [
        description1,
        description2,
        description3,
        description4] as Set

descriptions2 = [description1, description4] as Set

termEntryUuId1 = "a2adccca-e08c-4abd-ba3a-e0cbc3858bc1"
termEntryUuId2 = "a2adccca-e08c-4abd-ba3a-e0cbc3858bc7"

termUuId1 = "a2adccca-e08c-4abd-ba3a-e0cbc3858bc2"
termUuId2 = "a2adccca-e08c-4abd-ba3a-e0cbc3858bc3"
termUuId3 = "a2adccca-e08c-4abd-ba3a-e0cbc3858bc4"
termUuId4 = "a2adccca-e08c-4abd-ba3a-e0cbc3858bc5"
termUuId5 = "a2adccca-e08c-4abd-ba3a-e0cbc3858bc6"

term1 = builder.term([uuId  : termUuId1, name: "blacklisted source term 1", languageId: "en-US", dateModified: 1403509560L, forbidden: true,
                      status: "BLACKLISTED"])
term3 = builder.term([uuId  : termUuId3, name: "blacklisted source term 2", languageId: "en-US", dateModified: 1403509561L, forbidden: true,
                      status: "BLACKLISTED"])

term2 = builder.term([uuId: termUuId2, name: "suggestion source term 1", languageId: "en-US", dateModified: 1403509561L, status: "PROCESSED"])
term4 = builder.term([uuId: termUuId4, name: "suggestion source term 2", languageId: "en-US", dateModified: 1403509562L, status: "PROCESSED"])
term5 = builder.term([uuId: termUuId5, name: "suggestion source term 3", languageId: "en-US", dateModified: 1403509563L, status: "PROCESSED"])

termEntryMap1 = ["en-US": [term1, term2] as Set] as Map
termEntryMap2 = ["en-US": [term3, term4, term5] as Set] as Map

termEntry1 = builder.termEntry([dateCreated: 1403509560L, dateModified: 1303509560L, userCreated: "sdulin", userModified: "sdulin",
                                uuId       : termEntryUuId1, languageTerms: termEntryMap1, descriptions: descriptions1])
termEntry2 = builder.termEntry([dateCreated: 1403509567L, dateModified: 1303509560L, userCreated: "sdulin", userModified: "sdulin",
                                uuId       : termEntryUuId2, languageTerms: termEntryMap2, descriptions: descriptions2])

segmentUuId1 = "a2adccca-e08c-4abd-ba3a-e0cbc3858bc7"
segmentUuId2 = "a2adccca-e08c-4abd-ba3a-e0cbc3858bc8"

segment1 = builder.liteSegment([segmentId: segmentUuId1, source: "blacklisted source term 1", sourceCode: Locale.US.getCode()])
segment2 = builder.liteSegment([segmentId: segmentUuId2, source: "blacklisted source term 2", sourceCode: Locale.US.getCode()])

segments1 = [segment1] as List
segments2 = [segment1, segment2] as List

query1 = builder.liteSegmentSearchQuery([fuzzySearchEnabled: false, maxResults: 50, location: QueryMatchLocation.SOURCE, segments: segments1])
query2 = builder.liteSegmentSearchQuery([fuzzySearchEnabled: false, maxResults: 50, location: QueryMatchLocation.SOURCE, segments: segments2])

termEntries1 = [termEntry1] as List
termEntries2 = [termEntry2] as List