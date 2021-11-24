package groovy.submissionLanguageDetailSearchController
import org.gs4tr.foundation.modules.entities.model.PagedList
import org.gs4tr.foundation.modules.entities.model.PagedListInfo
import org.gs4tr.termmanager.model.ItemStatusTypeHolder
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum
import org.gs4tr.foundation.modules.entities.model.SortDirection;
import org.gs4tr.foundation.modules.entities.model.Task
import org.gs4tr.foundation.modules.entities.model.TaskPagedList
import org.gs4tr.foundation.modules.entities.model.TaskPriority
import org.gs4tr.termmanager.model.view.SubmissionLanguageDetailView


submissionLanguageDetailView = builder.submissionLanguageDetailView([assignee: "translator", availableTasks: null, canceled: false,
    dateCompleted: 0L, dateModified: 1448352948000L, dateSubmitted: 1448352947000L, inFinalReview: false, inTranslation: true,
    languageId: "de-DE", markerId: "6e7ec11f-de77-4a22-8196-17e3f1fa5fb5", projectId: null, sourceInTranslation: false,
    status: ItemStatusTypeHolder.IN_TRANSLATION_REVIEW, submissionLanguageComments: [], submissionLanguageId: 1L, submitter: null,
    termCanceledCount: 0L, termCompletedCount: 0L, termCount: 1L, termInFinalReviewCount: 0L, termInTranslationCount: 1L])

pagedListInfo = builder.pagedListInfo([index: 0, indexesSize: 5, size: 25, sortDirection: SortDirection.ASCENDING.name()])

elements = [
    submissionLanguageDetailView] as SubmissionLanguageDetailView[]

pagedList = builder.pagedList([elements: elements, pagedListInfo: pagedListInfo, totalCount: 1])

tasks=[] as Task[]

taskPagedList= new TaskPagedList<SubmissionLanguageDetailView>(pagedList)
taskPagedList.setTasks(tasks)
taskPagedList.setTotalCount(1L);
