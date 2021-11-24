package groovy.manualtask

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

enUS = "en-US";
deDE = "de-DE";
frFR = "fr-FR";
itIT = "it-IT";

term1 = builder.term([uuId     : termId_01, name: "test term 1", projectId: 1L, termEntryId: termEntryId_01,
                      forbidden: false, status: "PROCESSED", languageId: enUS, inTranslationAsSource: false, first: true])
term2 = builder.term([uuId     : termId_02, name: "test term 2", projectId: 1L, termEntryId: termEntryId_01,
                      forbidden: true, status: "PROCESSED", languageId: enUS, inTranslationAsSource: false, first: false])
term3 = builder.term([uuId     : termId_03, name: "test term 3", projectId: 1L, termEntryId: termEntryId_01,
                      forbidden: false, status: "PROCESSED", languageId: deDE, inTranslationAsSource: false, first: true])
term4 = builder.term([uuId     : termId_04, name: "test term 4", projectId: 1L, termEntryId: termEntryId_01,
                      forbidden: false, status: "PROCESSED", languageId: frFR, inTranslationAsSource: false, first: true])
term5 = builder.term([uuId     : termId_05, name: "test term 5", projectId: 1L, termEntryId: termEntryId_01,
                      forbidden: false, status: "PROCESSED", languageId: itIT, inTranslationAsSource: false, first: true])
term6 = builder.term([uuId     : termId_06, name: "test term 6", projectId: 1L, termEntryId: termEntryId_01,
                      forbidden: false, status: "PROCESSED", languageId: itIT, inTranslationAsSource: false, first: false])

termIds = [
        termId_01,
        termId_02,
        termId_03,
        termId_04,
        termId_05,
        termId_06] as List


terms = [
        term1,
        term2,
        term3,
        term4,
        term5,
        term6] as List

languages = [enUS, deDE, frFR, itIT] as List

termEntryMap1 = ["en-US": [term1, term2] as Set, "de-DE": [term3] as Set, "fr-FR": [term4] as Set, "it-IT": [term5, term6] as Set] as Map

termEntry01 = builder.termEntry([uuId: termEntryId_01, languageTerms: termEntryMap1, projectId: 1L])

command = builder.termCommandPerProject([termIds: termIds, termEntryIds: [termEntryId_01] as List, projectId: 1])

commands = builder.changeTermStatusCommands([changeStatusCommands: [command] as List, targetLanguages: [deDE, frFR, itIT] as List, sourceLanguage: enUS])