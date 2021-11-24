import java.sql.Timestamp


date = new Date();
timesatmp = new Timestamp(date.getTime());

itemStatusType = builder.itemStatusType([name: "intranslationreview"])
priority = builder.entityStatusPriority([dateCompleted: null, priority: 2, priorityAssignee: 3,
    status: itemStatusType])


subLanguage1 = builder.submissionLanguage([assignee: "marko", dateModified: timesatmp, dateSubmitted: timesatmp,
    entityStatusPriority: priority, languageId: "de-DE", markerId: "3b3c3059-8dd2-4cba-90e2-b8ad2d9b9709",
    statusAssignee: itemStatusType, submission: null, submissionLanguageComments: null, submissionLanguageId: 1L,
    termCanceledCount: 0L, termCompletedCount: 0L, termCount: 1L, termInFinalReviewCount: 0L,
    termInTranslationCount: 1L,])
subLanguage2 = builder.submissionLanguage([assignee: "marko", dateModified: timesatmp, dateSubmitted: timesatmp,
    entityStatusPriority: priority, languageId: "en-US", markerId: "4b3c3059-8dd2-4cba-90e2-b8ad2d9b9709",
    statusAssignee: null, submission: null, submissionLanguageComments: null, submissionLanguageId: 1L,
    termCanceledCount: 0L, termCompletedCount: 0L, termCount: 1L, termInFinalReviewCount: 0L,
    termInTranslationCount: 1L,])
subLanguage3 = builder.submissionLanguage([assignee: "marko", dateModified: null, dateSubmitted: null,
    entityStatusPriority: priority, languageId: "fr-FR", markerId: "5b3c3059-8dd2-4cba-90e2-b8ad2d9b9709",
    statusAssignee: itemStatusType, submission: null, submissionLanguageComments: null, submissionLanguageId: 1L,
    termCanceledCount: 0L, termCompletedCount: 0L, termCount: 1L, termInFinalReviewCount: 0L,
    termInTranslationCount: 1L,])

submissionLanguages = [subLanguage1, subLanguage2, subLanguage3] as List


project = builder.tmProject([projectId: 1L])

submission = builder.submission([project: project, submitter: "John"])

