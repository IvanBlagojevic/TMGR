package groovy.manualtask

//Valid
termList = ["dog", "cat"] as List

getLookupTermCommand = builder.lookupTermCommand([terms: termList, languageId: "en-US", projectId: 1])

lookupTermCommands = [getLookupTermCommand] as List

getLookupTermCommands = builder.lookupTermCommands([commands: lookupTermCommands])

//Invalid
emptyTermList = [] as List

invalidGetLookupTermCommand = builder.lookupTermCommand([terms: emptyTermList, languageId: "en-US", projectId: 1])

invalidLookupTermCommands = [invalidGetLookupTermCommand] as List

invalidGetLookupTermCommands = builder.lookupTermCommands([commands: invalidLookupTermCommands])

//Return objects
term01 = builder.term([uuId: "termUuId01", name: "dog", languageId: "en-US", dateModified: 1403509560L, status: "PROCESSED"])
term02 = builder.term([uuId: "termUuId02", name: "puppy", languageId: "en-US", dateModified: 1403509561L, status: "PROCESSED"])
term03 = builder.term([uuId: "termUuId03", name: "cat", languageId: "en-US", dateModified: 1403509562L, status: "PROCESSED"])

termEntryMap1 = ["en-US": [term01, term02] as Set] as Map
termEntryMap2 = ["en-US": [term03] as Set] as Map
termEntryMap3 = ["en-US": [term01] as Set] as Map
termEntryMap4 = ["en-US": [term03] as Set] as Map

termEntryUuId01 = "lookup-termentry001";
termEntryUuId02 = "lookup-termentry002";
termEntryUuId03 = "lookup-termentry003";
termEntryUuId04 = "lookup-termentry004";

termEntry01 = builder.termEntry([uuId: termEntryUuId01, languageTerms: termEntryMap1, projectName: "project1", projectId: 1])
termEntry02 = builder.termEntry([uuId: termEntryUuId02, languageTerms: termEntryMap2, projectName: "project1", projectId: 1])
termEntry03 = builder.termEntry([uuId: termEntryUuId03, languageTerms: termEntryMap3, projectName: "project1", projectId: 1])
termEntry04 = builder.termEntry([uuId: termEntryUuId04, languageTerms: termEntryMap4, projectName: "project1", projectId: 1])

termentries = [termEntry01, termEntry02, termEntry03, termEntry04] as List

//Return objects in workflow
term04 = builder.term([uuId: "termUuId04", name: "dog", languageId: "en-US", dateModified: 1403509560L, status: "PROCESSED", inTranslationAsSource: true])

termEntryMap3 = ["en-US": [term04] as Set] as Map

termEntryUuId03 = "lookup-termentry003";

termEntry03 = builder.termEntry([uuId: termEntryUuId03, languageTerms: termEntryMap3, projectName: "project1", projectId: 1])

termentriesInWf = [termEntry03] as List

//returns matches for edited term entry

termEntryUuId05 = "lookup-termentry005";
termEntryUuId06 = "lookup-termentry006";

term05 = builder.term([uuId: "termUuId05", name: "dog", languageId: "en-US", dateModified: 1403509580L, status: "PROCESSED"])
term06 = builder.term([uuId: "termUuId06", name: "dog", languageId: "en-US", dateModified: 1403509570L, status: "WAITING"])


termEntryMap5 = ["en-US": [term05] as Set] as Map
termEntryMap6 = ["en-US": [term06] as Set] as Map

termEntryEdited = builder.termEntry([uuId: termEntryUuId05, languageTerms: termEntryMap5, projectName: "project1", projectId: 1])
termEntryEditedMatch = builder.termEntry([uuId: termEntryUuId06, languageTerms: termEntryMap6, projectName: "project1", projectId: 1])

termEntryIds = [termEntryUuId05] as List
terms = ["dog"] as List

matchedTermEntries = [termEntryEdited, termEntryEditedMatch] as List


lookupTermCommandForEditedTermEntries = builder.lookupTermCommand([languageId: "en-US", projectId: 1, termEntryIds: termEntryIds, terms: terms])

lookupTermComandList = [lookupTermCommandForEditedTermEntries] as List


lookupTermCommandsForEditedTermEntries = builder.lookupTermCommands([commands: lookupTermComandList])





