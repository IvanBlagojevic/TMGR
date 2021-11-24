import org.gs4tr.foundation.modules.entities.model.PagedList
import org.gs4tr.foundation.modules.entities.model.PagedListInfo


detailView1 = builder.projectDetailView([activeSubmissionCount: 1L, approvedTermCount: 4L, completedSubmissionCount: 1L,
    assignee: null, availableTasks: null, canceled: false, termInSubmissionCount: 1L, dateModified: 1396338186000L,
    forbiddenTermCount: 0L, inFinalReview: false, inTranslation: false, languageCount: 4L, name: "My First Project",
    projectDetailViewId: 1L, projectId: 1L, shortCode: "MYF000001", sourceInTranslation: false, status: null,
    submitter: null, termCount: 7L, termEntryCount: 7L, termInSubmissionCount: 0L])

pageListInfo = new PagedListInfo();

pagedList = new PagedList<Object>();
pagedList.setElements(detailView1);
pagedList.setPagedListInfo(pageListInfo);
pagedList.setTotalCount(1L);


