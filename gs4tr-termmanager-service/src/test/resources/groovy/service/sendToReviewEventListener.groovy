package groovy.service

import org.gs4tr.termmanager.model.ItemStatusTypeHolder

termId_01 = "474e93ae-7264-4088-9d54-term00000001"
termId_02 = "474e93ae-7264-4088-9d54-term00000002"
termEntryId_01 = "474e93ae-7264-4088-9d54-termentry001"
termEntryId_02 = "474e93ae-7264-4088-9d54-termentry002"

term1 = builder.term([uuId: termId_01, termEntryId: termEntryId_01, forbidden: false, status: "PROCESSED", statusOld: "", languageId: "en-US", projectId: 1L])
term2 = builder.term([uuId: termId_02, termEntryId: termEntryId_01, forbidden: false, status: "ONHOLD", statusOld: "", languageId: "de-DE", projectId: 1L])

term3 = builder.term([uuId: termId_02, termEntryId: termEntryId_01, forbidden: false, status: "WAITING", statusOld: "", languageId: "de-DE", projectId: 1L])

termEntry1 = builder.termEntry([uuId: termEntryId_01])
termEntry1.addTerm(term1)
termEntry1.addTerm(term2)

termEntry2 = builder.termEntry([uuId: termEntryId_02])
termEntry2.addTerm(term1)
termEntry2.addTerm(term3)

onHold = ItemStatusTypeHolder.ON_HOLD.getName()

statusWainting = builder.itemStatusType(name: "onhold")

date = new Date()

entityStatusPriority = builder.EntityStatusPriority([dateCompleted: date, priority: 1L, priorityAssignee: 0L, status: null])

command = builder.updateCommand([status: onHold, parentMarkerId: "c0643514-d908-420e-a711-431b583bef5f", markerId: "474e93ae-7264-4088-9d54-term00000002", value: "Konto 1", languageId: "en-US"])

submissionLanguage = builder.submissionLanguage([assignee         : "ivan", dateModified: date, dateSubmitted: date, entityStatusPriority: entityStatusPriority,
                                                 languageId       : "de-DE", markerId: UUID.randomUUID().toString(), statusAssignee: null, submission: null, submissionLanguageComments: null, submissionLanguageId: 1L,
                                                 termCanceledCount: 1L, termCount: 1L, termInFinalReviewCount: 1L, termInTranslationCount: 1L])

submissionLanguages = [submissionLanguage] as Set

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusWainting,
                               metadata        : null, organization: null, projectDetail: null, projectId: 1L, projectInfo: null,
                               projectLanguages: null])

submission = builder.Submission([assignees          : "ivan", dateModified: date, dateSubmitted: date, entityStatusPriority: entityStatusPriority,
                                 markerId           : UUID.randomUUID().toString(), name: "mafia", project: tmProject, sourceLanguageId: "en-US", submissionId: 1L,
                                 submissionLanguages: submissionLanguages, submissionUsers: ["ivan"] as Set, submitter: "ivan",
                                 targetLanguageIds  : "de-DE", termEntryCount: 1L])