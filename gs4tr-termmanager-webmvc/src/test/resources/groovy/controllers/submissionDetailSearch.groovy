import org.gs4tr.foundation.modules.entities.model.PagedList
import org.gs4tr.foundation.modules.entities.model.PagedListInfo
import org.gs4tr.termmanager.model.ItemStatusTypeHolder


inTranslation = ItemStatusTypeHolder.IN_TRANSLATION_REVIEW;

detailView1 = builder.submissionDetailView([assignee: "marko",dateCompleted: 0L, dateModified: 1396338186000L,
    dateSubmitted: 1396338186000L, markerId: "4ea6a202-bec6-4aa2-8384-e1ea527f39b2", projectName: "My First Project",
    sourceLanguageId: "en-US", submissionId: 1, submissionName:	"sub", submitter: "marko", targetLanguageIds: "sr-RS;de-DE",
    termEntryCount: 1, status: inTranslation])

pageListInfo = new PagedListInfo();

pagedList = new PagedList<Object>();
pagedList.setElements(detailView1);
pagedList.setPagedListInfo(pageListInfo);
pagedList.setTotalCount(1L);

