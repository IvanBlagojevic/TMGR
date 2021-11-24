import org.gs4tr.foundation.modules.entities.model.PagedList
import org.gs4tr.termmanager.model.view.ProjectLanguageDetailView

pagedListInfo = builder.pagedListInfo([index: 0, size: 50])

command = builder.projectLanguageDetailRequest([languageIds: ["de", "en"] as Set, projectDetailId: 1L, user: null])

projectLanguageDetailsList = builder.projectLanguageDetailView([activeSubmissionCount: 1L, approvedTermCount: 1L, completedSubmissionCount: 1L, dateModified: 1410188035291,
    forbiddenTermCount: 0L, language: null, projectLanguageDetailViewId: 1L, termCount: 2L, termInSubmissionCount: 2L])

dateModified = new Date()

projectLanguageDetail = builder.projectLanguageDetail([languageId: "en", termCount: 6L, approvedTermCount: 2L, activeSubmissionCount: 1L, completedSubmissionCount: 0L, dateModified: dateModified, forbiddenTermCount: 0L, termInSubmissionCount: 3L, termEntryCount: 2L, onHoldTermCount: 1L, userDetails: [] as Set])


projectLanguageDetails = [projectLanguageDetailsList] as List

pagedList = new PagedList<ProjectLanguageDetailView>()
pagedList.setPagedListInfo(pagedListInfo)
pagedList.setElements(projectLanguageDetails)
pagedList.setTotalCount(1L)