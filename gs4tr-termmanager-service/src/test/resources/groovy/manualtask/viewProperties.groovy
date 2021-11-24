import org.gs4tr.termmanager.service.model.command.BaseLingualCommand;
import org.gs4tr.termmanager.service.model.command.TranslationViewCommand;

termId_01 = "474e93ae-7264-4088-9d54-term00000001";
termId_02 = "474e93ae-7264-4088-9d54-term00000002";
termEntryId_01 = "474e93ae-7264-4088-9d54-termentry001";
descriptionId_01 = "474e93ae-7264-4088-9d54-desc00000001";
descriptionId_02 = "474e93ae-7264-4088-9d54-desc00000002";

sourceDesc1 = builder.description([uuid: descriptionId_01, baseType: "ATTRIBUTE", type: "definition",
    value:"source description"])
targetDesc1 = builder.description([uuid: descriptionId_02, baseType: "ATTRIBUTE", type: "definition",
    value:"target description"])

sourceDescs = [sourceDesc1] as Set
targetDescs = [targetDesc1] as Set

termSource1 = builder.term([uuId: termId_01, name: "original source", projectId: 1L, termEntryId: termEntryId_01, forbidden: false,
    status: "WAITING", languageId: "en-US", dateModified: 1402578301L, descriptions: sourceDescs])
termTarget1 = builder.term([uuId: termId_02, name: "original target", projectId: 1L, termEntryId: termEntryId_01, forbidden: true,
    status: "BLACKLISTED", languageId: "de-DE", dateModified: 1402578302L, descriptions: targetDescs])

terms = [termSource1, termTarget1] as List

sourceIds = [termId_01] as List
targetIds = [termId_02] as List

termIds = [
    termId_01,
    termId_02] as List

translationViewCommand = builder.translationViewCommand([targetId: termId_02, targetLocale: "de-DE"])
targetTermsList = [translationViewCommand] as List<TranslationViewCommand>

command = builder.baseLingualCommand([sourceId: termId_01, sourceLocale: "en-US", targetTerms: targetTermsList])

sourceDescAfter1 = builder.description([uuid: descriptionId_01, baseType: "ATTRIBUTE", type: "definition", value:"changed source description"])
targetDescAfter1 = builder.description([uuid: descriptionId_02, baseType: "ATTRIBUTE", type: "definition", value:"changed target description"])

sourceDescsAfter = [sourceDescAfter1] as Set
targetDescsAfter = [targetDescAfter1] as Set

termAfterS1 = builder.term([uuId: termId_01, name: "changed source", projectId: 1L, termEntryId: termEntryId_01, forbidden: false,
    status: "WAITING", languageId: "en-US", dateModified: 1402578303L, descriptions: sourceDescsAfter])
termAfterT2 = builder.term([uuId: termId_02, name: "changed target", projectId: 1L, termEntryId: termEntryId_01, forbidden: false,
    status: "PROCESSED", languageId: "de-DE", dateModified: 1402578304L, descriptions: targetDescsAfter])

originalTermEntryId_01 = "474e93ae-7264-4088-9d54-termentry001";
sourceTermId_01 = "474e93ae-7264-4088-9d54-sourceTerm00000001";
targetTermId_02 = "474e93ae-7264-4088-9d54-targetTerm00000002";

sourceTerm = builder.term([uuId: sourceTermId_01, name: "source test term", projectId: 1L, termEntryId: originalTermEntryId_01, forbidden: false,
    status: "PROCESSED", languageId: "en-US", dateModified: 1402578301L])
targetTerm = builder.term([uuId: targetTermId_02, name: "target test term", projectId: 1L, termEntryId: originalTermEntryId_01, forbidden: true,
    status: "BLACKLISTED", languageId: "de-DE", dateModified: 1402578302L])

testTerms = [sourceTerm, targetTerm] as List

termHistory1 = builder.term([uuId: sourceTermId_01, name: "source test term", projectId: 1L, termEntryId: originalTermEntryId_01,
    forbidden: false, status: "PROCESSED", languageId: "en-US", dateModified: 1402578701L, inTranslationAsSource: false])
termHistory2 = builder.term([uuId: targetTermId_02, name: "target test term", projectId: 1L, termEntryId: originalTermEntryId_01,
    forbidden: false, status: "PROCESSED", languageId: "de-DE", dateModified: 1402578702L, inTranslationAsSource: false])
termHistory3 = builder.term([uuId: sourceTermId_01, name: "source test term", projectId: 1L, termEntryId: originalTermEntryId_01,
    forbidden: false, status: "PROCESSED", languageId: "en-US", dateModified: 1402578703L, inTranslationAsSource: false])
termHistory4 = builder.term([uuId: targetTermId_02, name: "target test term", projectId: 1L, termEntryId: originalTermEntryId_01,
    forbidden: true, status: "BLACKLISTED", languageId: "de-DE", dateModified: 1402578704L, inTranslationAsSource: false])

termHistorySet1 = [termHistory1] as Set
termHistorySet2 = [termHistory2] as Set
languageTerms1 = ["en-US": termHistorySet1, "de-DE": termHistorySet2] as Map

termHistorySet3 = [termHistory3] as Set
termHistorySet4 = [termHistory4] as Set
languageTerms2 = ["en-US": termHistorySet3, "de-DE": termHistorySet4] as Map

historyTermEntry1 = builder.termEntry([uuId: originalTermEntryId_01, languageTerms: languageTerms1, projectId: 1L])
historyTermEntry2 = builder.termEntry([uuId: originalTermEntryId_01, languageTerms: languageTerms2, projectId: 1L])

historyTermentries = [
    historyTermEntry1,
    historyTermEntry2] as List

translationViewCommand1 = builder.translationViewCommand([targetId: targetTermId_02, targetLocale: "de-DE"])

targetTermsList1 = [translationViewCommand1] as List<TranslationViewCommand>

baseLingualCommand = builder.baseLingualCommand([sourceId: sourceTermId_01, sourceLocale: "en-US", targetTerms: targetTermsList1])

/* TERII-3212: Creation date of the entry or the term is not shown in history */
currentTermEntryID_01 = "474e93ae-7264-4088-9d54-currentTermEntryId_01";

englishSourceTermID = "474e93ae-7264-4088-9d54-englishSourceTermID00000001";

englishSourceTerm = builder.term([uuId: englishSourceTermID, name: "English source term", projectId: 1L, termEntryId: currentTermEntryID_01,
    forbidden: false,status: "PROCESSED", languageId: "en-US", dateModified: 5402598303L])

currentTerms = [englishSourceTerm] as List

termHistory_01 = builder.term([uuId: englishSourceTermID, name: "English source term", projectId: 1L, termEntryId: currentTermEntryID_01,
    forbidden: false, status: "PROCESSED", languageId: "en-US", dateModified: 5602598303L, inTranslationAsSource: false])

termHistorySet_01 = [termHistory_01] as Set

languageTerms_01 = ["en-US": termHistorySet_01] as Map

historyTermEntry_01 = builder.termEntry([uuId: currentTermEntryID_01, languageTerms: languageTerms_01, projectId: 1L])
historyTermentries_01 = [historyTermEntry_01] as List

baseLingualCommand_01 = builder.baseLingualCommand([sourceId: englishSourceTermID, sourceLocale: "en-US", targetTerms: [] as List<TranslationViewCommand>])


