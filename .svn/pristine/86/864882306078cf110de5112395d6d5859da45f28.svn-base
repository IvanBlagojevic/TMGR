import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.gs4tr.termmanager.model.event.SubmissionState;
import org.gs4tr.termmanager.model.view.SubmissionLanguageDetailView
import org.gs4tr.foundation.modules.entities.model.PagedList
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.termmanager.model.EntityStatusPriority;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.SubmissionLanguageComment;
import org.gs4tr.termmanager.model.SubmissionUser;
import org.gs4tr.termmanager.model.TmProject;

pagedListInfo = builder.pagedListInfo([index: 0, size: 50])

command = builder.submissionLanguageDetailRequest([languageIds:["de", "en"] as Set, submissionId: 1L, submitterView: false])

submissionLanguageDetailView = builder.submissionLanguageDetailView([dateCompleted: 1396338186000L, dateModified: 1396338186000L, dateSubmitted: 1396338186000L,
    languageId: "en", markerId: UUID.randomUUID().toString(), submissionLanguageComments: null, submissionLanguageId: 1L,
    termCanceledCount: 0L, termCompletedCount: 1L, termCount: 1L, termInFinalReviewCount: 1L, termInTranslationCount: 1L]) as List

pagedList = new PagedList<SubmissionLanguageDetailView>();
pagedList.setElements(submissionLanguageDetailView);
pagedList.setPagedListInfo(pagedListInfo);
pagedList.setTotalCount(1L);

date = new Date()
entityStatusPriority = builder.EntityStatusPriority([dateCompleted: date, priority: 1L, priorityAssignee: 0L, status: null])

projectDetail = builder.projectDetail([activeSubmissionCount: 1L, approvedTermCount: 7L, completedSubmissionCount: 0L,
    dateModified: date, forbiddenTermCount: 0, languageCount: 0, languageDetails: null, project: null, projectDetailId: 1L, termCount: 15L, termEntryCount: 8L, termInSubmissionCount: 2L, userDetails: null])

statusWainting = builder.itemStatusType(name: "waiting")

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusWainting,
    metadata: null, organization: null, projectDetail: null, projectId: 1L, projectInfo: null,
    projectLanguages: null])

submissionLanguage = builder.submissionLanguage([assignee: "ivan", dateModified: date, dateSubmitted: date, entityStatusPriority: entityStatusPriority,
    languageId: "en", markerId: UUID.randomUUID().toString(), , statusAssignee: null, submission: null, submissionLanguageComments: null, submissionLanguageId: 1L,
    termCanceledCount: 1L, termCount: 1L, termInFinalReviewCount: 1L, termInTranslationCount: 1L])

submissionLanguages = [submissionLanguage] as Set

submission = builder.Submission([assignees: "ivan", dateModified: date, dateSubmitted: date, entityStatusPriority: entityStatusPriority,
    markerId: UUID.randomUUID().toString(), name: "mafia", project: tmProject, sourceLanguageId: "en", submissionId: 1L,
    submissionLanguages: submissionLanguages, submissionUsers: ["ivan"] as Set, submitter: "ivan",
    targetLanguageIds: "de", termEntryCount: 1L])

