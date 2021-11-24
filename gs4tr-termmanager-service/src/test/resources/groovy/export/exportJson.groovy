termUUID1 = "exportJson-term01";
termUUID2 = "exportJson-term02";
projectId=1L;

termEntryUuId1 = "exportJson-termEntry01";

term1 = builder.term(uuId: termUUID1, name: "dog", languageId: "en-US", status: "WAITING",userCreated:"miha",userModified:"miha",projectId:projectId,disabled:false,termEntryId:termEntryUuId1,dateModified:1543912959353L);
term2 = builder.term(uuId: termUUID2, name: "hund", languageId: "de-DE", status: "WAITING",userCreated:"miha",userModified:"miha",projectId:projectId,disabled:false,termEntryId:termEntryUuId1,dateModified:1543912959353L);

termEntryMap1 = ["en-US": [term1] as Set,"de-DE":[term2] as Set] as Map

termEntry1 = builder.termEntry([uuId: termEntryUuId1, languageTerms: termEntryMap1])

languagesToExport = ["en-US"] as List

targetLocales = ["de-DE"] as List

String date = '12-01-2014'
Date d = Date.parse( 'dd-MM-yyyy', date )

request = builder.termEntrySearchRequest(languagesToExport: languagesToExport,sharePendingTerms:false,projectId:projectId,sourceLocale:"en-US",targetLocales:targetLocales,dateModifiedFrom:d)