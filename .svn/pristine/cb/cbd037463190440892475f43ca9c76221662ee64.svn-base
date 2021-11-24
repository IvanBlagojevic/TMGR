import org.gs4tr.foundation.modules.entities.model.SortDirection
import org.gs4tr.termmanager.model.ItemStatusTypeHolder
import org.gs4tr.termmanager.model.glossary.TermEntry
import org.gs4tr.termmanager.model.search.TypeSearchEnum

termEntryId_01 = "474e93ae-7264-4088-9d54-termentry001";
termId_01 = "474e93ae-7264-4088-9d54-term00000001";
termId_02 = "474e93ae-7264-4088-9d54-term00000002";
termId_03 = "474e93ae-7264-4088-9d54-term00000003";

date = new Date().getTime();
waiting = ItemStatusTypeHolder.WAITING.getName();
processed = ItemStatusTypeHolder.PROCESSED.getName();
inTranslation = ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName();

sourceTerm1 = builder.term([assignee: "assignee marko", canceled: false, dateCreated: date, dateModified: date, first: true,
    inTranslationAsSource: true, languageId: "en-US", name: "source text", projectId: 1L, status: waiting, submissionId: 1L,
    submissionName: "sub name", submitter: "submitter marko", tempText: "temp source text", termEntryId: termEntryId_01,
    userCreated: "user created", userModified: "user modified", uuId: termId_01])

targetTerm1 = builder.term([assignee: "assignee marko", canceled: false, dateCreated: date, dateModified: date, first: true,
    inTranslationAsSource: true, languageId: "de-DE", name: "target text", projectId: 1L, status: inTranslation, submissionId: 1L,
    submissionName: "sub name", submitter: "submitter marko", tempText: "temp target text", termEntryId: termEntryId_01,
    userCreated: "user created", userModified: "user modified", uuId: termId_02])

targetTerm2 = builder.term([assignee: "assignee marko", canceled: false, dateCreated: date, dateModified: date, first: false,
    inTranslationAsSource: false, languageId: "de-DE", name: "target synonym text", projectId: 1L, status: waiting, submissionId: 1L,
    submissionName: "sub name 2", submitter: "submitter marko", tempText: "temp target text", termEntryId: termEntryId_01,
    userCreated: "user created", userModified: "user modified", uuId: termId_03])

TermEntry termEntry01 = new TermEntry();
termEntry01.setUuId(termEntryId_01);
termEntry01.addTerm(sourceTerm1);
termEntry01.addTerm(targetTerm1);
termEntry01.addTerm(targetTerm2);

termentries = [termEntry01] as List

pagedListInfo = builder.pagedListInfo([index: 0, size: 50])

termSearchRequest = builder.termSearchRequest([projectId: 1L, term: "text", source: "en-US", targetLanguagesList: ["de-DE"] as List,
    folder: "TERM_LIST"])

command = builder.termSearchRequest([folder: "TERM_LIST", projectIds: [1L] as List, searchType: "DEFAULT", searchUserLatestChanges: false, showAutoSaved: false	,
    source: "en-US", sourceAndTargetSearch: true, targetLanguagesList:["de-DE", "fr-FR"] as List, targetTermSearch: true, term: "account", typeSearch:TypeSearchEnum.TERM])

pagedListInfo1 = builder.pagedListInfo([index: 0, indexesSize: 5, size: 50, sortDirection: SortDirection.DESCENDING, sortProperty: "en-US_termName_STRING_STORE-SORT"])

termEntryId = "474e93ae-7264-4088-9d54-termentryId";
termId1 = "474e93ae-7264-4088-9d54-termId1";
termId2 = "474e93ae-7264-4088-9d54-termId2";
termId3 = "474e93ae-7264-4088-9d54-termId3";


sourceTerm = builder.term([assignee: "assignee Johnny", canceled: false, dateCreated: date, dateModified: date, first: true,
    inTranslationAsSource: true, languageId: "en-US", name: "account", projectId: 1L, status: processed, submissionId: 1L,
    submissionName: "Test submission", submitter: "submitter Johnny", termEntryId: termEntryId,
    userCreated: "Johnny", userModified: "Johnny", uuId: termId1])

sourceTerm2 = builder.term([assignee             : "assignee Johnny", canceled: false, dateCreated: date, dateModified: date, first: true,
                            inTranslationAsSource: false, languageId: "en-US", name: "account", projectId: 1L, status: processed, submissionId: 1L,
                            submissionName       : "Test submission", submitter: "submitter Johnny", termEntryId: termEntryId,
                            userCreated          : "Johnny", userModified: "Johnny", uuId: termId1])

targetTerm3 = builder.term([assignee: "assignee Johnny", canceled: false, dateCreated: date, dateModified: date, first: false,
    inTranslationAsSource: false, languageId: "de-DE", name: "Konto", projectId: 1L, status: inTranslation, submissionId: 1L,
    submissionName: "Test submission", submitter: "submitter Johnny", tempText: "temp target text", termEntryId: termEntryId,
    userCreated: "Johnny", userModified: "Johnny", uuId: termId2])

targetTerm4 = builder.term([dateCreated: date, dateModified: date, first: false, inTranslationAsSource: false,
    languageId: "fr-FR", name: "compte", projectId: 1L, status: processed, termEntryId: termEntryId, userCreated: "Johnny",
    userModified: "Johnny", uuId: termId3])

TermEntry termEntry1 = new TermEntry();
termEntry1.setUuId(termEntryId);
termEntry1.setSubmissionId(1L);
termEntry1.setSubmissionName("Test submission");
termEntry1.setSubmitter("submitter Johnny");
termEntry1.addTerm(sourceTerm);
termEntry1.addTerm(targetTerm3);
termEntry1.addTerm(targetTerm4);

TermEntry termEntry2 = new TermEntry();
termEntry2.setUuId(termEntryId);
termEntry2.setSubmissionId(1L);
termEntry2.setSubmissionName("Test submission");
termEntry2.setSubmitter("submitter Johnny");
termEntry2.addTerm(sourceTerm2);
termEntry2.addTerm(targetTerm3);
termEntry2.addTerm(targetTerm4);


termEntries1 = [termEntry1] as List

termEntries2 = [termEntry2] as List

userPolices = ["POLICY_TM_CANCEL_TRANSLATION",
               "POLICY_TM_TERM_VIEW_TERM_HISTORY_USERS",
               "POLICY_TM_TERMENTRY_IMPORT",
               "POLICY_TM_ADD_COMMENT",
               "POLICY_TM_PROJECT_REPORT",
               "POLICY_TM_TERM_APPROVE_TERM_STATUS",
               "POLICY_TM_TERMENTRY_FORBID_TERMENTRY",
               "POLICY_TM_TERMENTRY_ASSIGN_ATTRIBUTES",
               "POLICY_TM_TERM_DISABLE_TERM",
               "POLICY_TM_TERM_AUTO_SAVE_TRANSLATION",
               "POLICY_TM_TERM_COMMIT_TRANSLATION_CHANGES",
               "POLICY_TM_TERM_ADD_PENDING_TERM",
               "POLICY_TM_SEND_TO_TRANSLATION_REVIEW",
               "POLICY_TM_TERM_UNDO_TRANSLATION_CHANGES",
               "POLICY_TM_TERM_DEMOTE_TERM_STATUS",
               "POLICY_TM_READ",
               "POLICY_TM_TERM_ADD_APPROVED_TERM",
               "POLICY_TM_TERM_VIEW_TERM_HISTORY",
               "POLICY_TM_TERMENTRY_EXPORT"] as Set