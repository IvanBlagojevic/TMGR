package groovy.browseSubmissionLanguages
import java.sql.Timestamp

timesatmp = new Timestamp(new Date().getTime());

itemStatusType = builder.itemStatusType([name: "intranslationreview"])

priority = builder.entityStatusPriority([dateCompleted: null, priority: 2, priorityAssignee: 3,
    status: itemStatusType])

searchCommand = builder.searchCommand([submissionTicket: "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q"])

project = builder.tmProject([projectId: 1L])

submission = builder.submission([submissionId: 1L, project: project, submitter: "sdulin"])

germany = builder.submissionLanguage([assignee: "translator", dateModified: timesatmp, dateSubmitted: timesatmp,
    entityStatusPriority: priority, languageId: "de-DE", markerId: "cbccaf65-9242-4feb-a512-79fffe3c27af",
    statusAssignee: itemStatusType, submission: submission, submissionLanguageComments: null, submissionLanguageId: 1L,
    termCanceledCount: 0L, termCompletedCount: 0L, termCount: 1L, termInFinalReviewCount: 0L,
    termInTranslationCount: 1L])
french = builder.submissionLanguage([assignee: "translator", dateModified: timesatmp, dateSubmitted: timesatmp,
    entityStatusPriority: priority, languageId: "fr-FR", markerId: "b9fb016e-0763-44e9-804d-b15381fd7226",
    statusAssignee: itemStatusType, submission: submission, submissionLanguageComments: null, submissionLanguageId: 2L,
    termCanceledCount: 0L, termCompletedCount: 0L, termCount: 1L, termInFinalReviewCount: 0L,
    termInTranslationCount: 1L])

submissionLanguages = [germany, french] as List
