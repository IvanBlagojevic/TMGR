package groovy.export

termUUID1 = "exportTbx-term01";
termUUID2 = "exportTbx-term02";
termUUID3 = "exportTbx-term03";
termUUID4 = "exportTbx-term04";
termUUID5 = "exportTbx-term05";


term1 = builder.term(uuId: termUUID1, name: "under the influence", languageId: "en-US", status: "WAITING");
term2 = builder.term(uuId: termUUID2, name: "out of control", languageId: "en-US", status: "BLACKLISTED");
term3 = builder.term(uuId: termUUID3, name: "dig your own hole", languageId: "en-US", status: "PROCESSED");
term4 = builder.term(uuId: termUUID4, name: "push the button", languageId: "en-US", status: "ONHOLD");
term5 = builder.term(uuId: termUUID5, name: "leave home", languageId: "en-US", status: "PROCESSED");

termEntryMap1 = ["en-US": [term1, term5] as Set] as Map
termEntryMap2 = ["en-US": [term1, term2, term3, term4] as Set] as Map

termEntryUuId1 = "exportTbx-termEntry01";
termEntryUuId2 = "exportTbx-termEntry02";

termEntry1 = builder.termEntry([uuId: termEntryUuId1, languageTerms: termEntryMap1])
termEntry2 = builder.termEntry([uuId: termEntryUuId2, languageTerms: termEntryMap2])

languagesToExport = ["en-US"] as List

request = builder.termEntrySearchRequest(languagesToExport: languagesToExport)