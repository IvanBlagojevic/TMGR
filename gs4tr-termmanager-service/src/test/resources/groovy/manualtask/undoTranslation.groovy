termId_01 = "474e93ae-7264-4088-9d54-term00000001";
termId_02 = "474e93ae-7264-4088-9d54-term00000002";
termEntryId_01 = "474e93ae-7264-4088-9d54-termentry001";

term1 = builder.term([uuId: termId_01, name: "test term 1", projectId: 1L, termEntryId: termEntryId_01, forbidden: false,
    status: "WAITING", languageId: "en-US", inTranslationAsSource: false])

terms = [term1] as List

termIds = [termId_01] as List

undoMap = ["474e93ae-7264-4088-9d54-term00000001": "test term"] as Map