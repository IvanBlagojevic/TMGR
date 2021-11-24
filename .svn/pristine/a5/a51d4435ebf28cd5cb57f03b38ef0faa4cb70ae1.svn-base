package groovy.submissionDetailSearchController

import org.gs4tr.foundation.modules.entities.model.PagedList
import org.gs4tr.foundation.modules.entities.model.PagedListInfo
import org.gs4tr.termmanager.model.ItemStatusTypeHolder

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum
import org.gs4tr.foundation.modules.entities.model.Task
import org.gs4tr.foundation.modules.entities.model.TaskPagedList
import org.gs4tr.foundation.modules.entities.model.TaskPriority
import org.gs4tr.termmanager.model.view.SubmissionDetailView

submissionDetailView = builder.submissionDetailView([assignee: "translator", dateCompleted: 0L, dateModified: 1448352948000L,
    dateSubmitted: 1448352947000L, canceled: false, inFinalReview: false, inTranslation: true, projectId: 1L,
    markerId: "063e9d5d-5956-4361-ad6f-5f0bc6f8ff9c", projectName: "Skype", sourceLanguageId: "en-US",
    submissionId: 1L, submissionName:	"Test submission", submitter: "super", targetLanguageIds: "de-DE",
    termEntryCount: 1L, status: ItemStatusTypeHolder.IN_TRANSLATION_REVIEW])

pagedListInfo = builder.pagedListInfo([index: 0, indexesSize: 5, size: 50, sortDirection: "ASCENDING"])

elements = [submissionDetailView] as SubmissionDetailView[]

pagedList = builder.pagedList([elements: elements, pagedListInfo: pagedListInfo, totalCount: 1])

task_1 = builder.task([name: "add comment", selectStyle: SelectStyleEnum.MULTI_SELECT, priority: TaskPriority.LEVEL_FIVE])

task_2 = builder.task([name: "cancel translation", selectStyle: SelectStyleEnum.MULTI_SELECT, priority: TaskPriority.LEVEL_FIVE])

tasks=[task_1, task_2] as Task[]

taskPagedList= new TaskPagedList<SubmissionDetailView>(pagedList)
taskPagedList.setTasks(tasks)
taskPagedList.setTotalCount(1L);
