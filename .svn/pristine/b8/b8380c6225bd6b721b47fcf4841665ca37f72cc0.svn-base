import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.model.view.SubmissionDetailView;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.termmanager.model.Submission;

pagedListInfo = builder.pagedListInfo([index: 0, size: 50])

commandWithEmptySubmissionIds = builder.submissionSearchRequest([folder:ItemFolderEnum.SUBMISSIONDETAILS])

submissionIdForCommand = [1L] as Set<Long>

commandWithOneElementInSubmissionIds = builder.submissionSearchRequest([folder:ItemFolderEnum.SUBMISSIONDETAILS],
                                       submissionIds: submissionIdForCommand)

submissionDetailView = builder.submissionDetailView([dateCompleted:1396338186000L, dateModified: 3003338186000L,
    		       dateSubmitted: 1908338186000L, markerId: null, projectName: "Emisia Project", sourceLanguageId: "en-US",
		       submissionName: "Fist Submission", targetLanguageIds: "de-DE"])

elements = [submissionDetailView] as SubmissionDetailView[]

pagedList = new PagedList<SubmissionDetailView>(); 
pagedList.setElements(elements);
pagedList.setPagedListInfo(pagedListInfo);
pagedList.setTotalCount(1L);

emptySubmissions = [] as List<Submission>

submissionOne = builder.submission([submitter: "Donnie Brasco", sourceLanguageId: "en-US", targetLanguageIds: "de-DE",
    		submissionId: 7L])
submissionTwo = builder.submission([submitter: "Johnny Depp", sourceLanguageId: "en-US", targetLanguageIds: "fr-FR",
    submissionId: 1L])

submissions = [submissionOne, submissionTwo] as List<Submission>