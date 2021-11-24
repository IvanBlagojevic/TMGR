import java.sql.Timestamp

import org.gs4tr.termmanager.model.LanguageAlignmentEnum
import org.gs4tr.termmanager.model.TermEntry
import org.gs4tr.termmanager.model.dto.pagedlist.PagedListInfo

sourceTerm1 = builder.term([])

statusWainting = builder.itemStatusType(name: "waiting")
alingment = LanguageAlignmentEnum.LTR
TermEntry termEntry = new TermEntry();
date = new Date();
timesatmp = new Timestamp(date.getTime());

itemStatusType = builder.itemStatusType([name: "intranslationreview"])
priority = builder.entityStatusPriority([dateCompleted: null, priority: 2, priorityAssignee: 3, 
    status: itemStatusType])

sourceTerm = builder.submissionTerm([assignee: "marko", canceled: false, commited: true, dateModified: timesatmp,
    dateSubmitted: timesatmp, descriptions: null, entityStatusPriority: priority, forbidden: false, inTranslationAsSource: true,
    languageId: "en-US", markerId: "aea0ed8a-dac8-4473-93e3-c8a28768accb", reviewRequired: false, source: true,
    submissionId: 1L, submissionLanguage: null, submissionName: "sub", submissionTermComments: null, submissionTermId: 1L,
    submitter: "marko", tempText: "house", term: null, termEntryId: 4L, text: "house", translationHistory: null])

targetTerm = builder.submissionTerm([assignee: "marko", canceled: false, commited: true, dateModified: null,
    dateSubmitted: null, descriptions: null, entityStatusPriority: priority, forbidden: false, inTranslationAsSource: true,
    languageId: "de-DE", markerId: "535a8f7c-499f-4fb4-b480-420ee0d7c808", reviewRequired: true, source: true,
    submissionId: 1L, submissionLanguage: null, submissionName: "sub", submissionTermComments: null, submissionTermId: 2L,
    submitter: "marko", tempText: "", term: null, termEntryId: 4L, text: "haus", translationHistory: null])

sourceTerms = [sourceTerm] as List
targetTerms = [targetTerm] as List

bilingualTerm = builder.bilingualTermTranslation([assignee: "marko", availableTasks: 0L, canceled: false, inFinalReview: false,
    inTranslation: true, projectId: 1L, projectName: "My First Project", searchedForbidden: false, searchedTermName: null,
    showAutoSaved: true, sourceAlignment: alingment, sourceInTranslation: false, sourceTerms: sourceTerms, sourceTermTooltip: null,
    status: null, submissionId: 1L, submissionName: "sub", submitter: "marko", submitterView: true, targetAlignment: alingment,
    targetTerm: null, targetTerms: targetTerms, targetTermTooltip: null, termEntryId: 1L])

