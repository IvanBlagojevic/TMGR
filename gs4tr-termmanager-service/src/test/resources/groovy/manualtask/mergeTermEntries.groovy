package groovy.manualtask

import org.gs4tr.termmanager.model.glossary.TermEntry

termEntryId01 = "474e93ae-7264-4088-9d54-termentry001"
termEntryId02 = "474e93ae-7264-4088-9d54-termentry002"
termEntryId03 = "474e93ae-7264-4088-9d54-termentry003"
termEntryId04 = "474e93ae-7264-4088-9d54-termentry004"

termEntryId05 = "474e93ae-7264-4088-9d54-termentry005"
termEntryId06 = "474e93ae-7264-4088-9d54-termentry006"


termEntryIds = [
        termEntryId01,
        termEntryId02,
        termEntryId03,
] as List

termEntryIds01 = [
        termEntryId01,
        termEntryId02,
        termEntryId03,
        termEntryId04
] as List


projectId = 1L

term01 = builder.term([name: "dog", languageId: "en-US", status: "PROCESSING"])
term02 = builder.term([name: "hund", languageId: "no-NO", status: "PROCESSING"])

term03 = builder.term([name: "cat", languageId: "en-US", status: "PROCESSING"])
term04 = builder.term([name: "katt", languageId: "no-NO", status: "PROCESSING"])

term05 = builder.term([name: "forest", languageId: "en-US", status: "PROCESSING"])
term06 = builder.term([name: "skog", languageId: "no-NO", status: "PROCESSING"])

term07 = builder.term([name: "car", languageId: "en-US", status: "INTRANSLATIONREVIEW"])
term08 = builder.term([name: "bil", languageId: "no-NO", status: "INTRANSLATIONREVIEW"])

term09 = builder.term([name: "chair", languageId: "en-US", status: "PROCESSING", dateCreated: 1403509560L])
term10 = builder.term([name: "chair", languageId: "en-US", status: "ONHOLD", dateCreated: 1403519560L])

languageTerms01 = ["en-US": [term01] as Set, "no-NO": [term02] as Set] as Map
languageTerms02 = ["en-US": [term03] as Set, "no-NO": [term04] as Set] as Map
languageTerms03 = ["en-US": [term05] as Set, "no-NO": [term06] as Set] as Map
languageTerms04 = ["en-US": [term07] as Set, "no-NO": [term08] as Set] as Map

languageTerms05 = ["en-US": [term09] as Set] as Map
languageTerms06 = ["en-US": [term10] as Set] as Map

termEntry01 = builder.termEntry = ([uuId: termEntryId01, dateCreated: 1403509560L, languageTerms: languageTerms01, projectId: projectId] as TermEntry)
termEntry02 = builder.termEntry = ([uuId: termEntryId02, dateCreated: 1403509561L, languageTerms: languageTerms02, projectId: projectId] as TermEntry)
termEntry03 = builder.termEntry = ([uuId: termEntryId03, dateCreated: 1403509562L, languageTerms: languageTerms03, projectId: projectId] as TermEntry)
termEntry04 = builder.termEntry = ([uuId: termEntryId04, dateCreated: 1403509563L, languageTerms: languageTerms04, projectId: projectId] as TermEntry)

termEntry05 = builder.termEntry = ([uuId: termEntryId05, dateCreated: 1403509560L, languageTerms: languageTerms05, projectId: projectId] as TermEntry)
termEntry06 = builder.termEntry = ([uuId: termEntryId06, dateCreated: 1403519561L, languageTerms: languageTerms06, projectId: projectId] as TermEntry)

termEntries = [
        termEntry01,
        termEntry02,
        termEntry03
] as List

termEntriesInWorkflow = [
        termEntry01,
        termEntry02,
        termEntry03,
        termEntry04
] as List

multipleTermEntryMergeCommand = builder.multipleTermEntriesMergeCommand([termEntryIds: termEntryIds, projectId: projectId])

invalidMultipleTermEntryMergeCommand = builder.multipleTermEntriesMergeCommand([termEntryIds: termEntryIds01, projectId: projectId])

mergeCommand = builder.mergeCommands([multipleTermEntriesMergeCommand: multipleTermEntryMergeCommand])

mergeCommandInWorkflow = builder.mergeCommands([multipleTermEntriesMergeCommand: invalidMultipleTermEntryMergeCommand])