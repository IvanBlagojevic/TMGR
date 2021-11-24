package org.gs4tr.termmanager.service.mocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.dao.ProjectDAO;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.search.command.ProjectLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.ProjectLanguageDetailView;
import org.gs4tr.termmanager.service.ProjectLanguageDetailService;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("service")
public class ProjectLanguageDetailServiceTest extends AbstractServiceTest {

    @Autowired
    private ProjectDAO _projectDAO;

    @Autowired
    private ProjectLanguageDetailService _projectLanguageDetailService;

    @Test
    @TestCase("projectLanguageDetailService")
    public void cloneProjectLanguageDetailTest() {
	String languageTo = "en-US";
	Long projectId = 1L;

	ProjectLanguageDetail pldForClone = getModelObject("projectLanguageDetail", ProjectLanguageDetail.class);
	pldForClone.setProjectDetail(getProjectDetail());

	when(getProjectDetailDAO().findByProjectId(projectId)).thenReturn(getProjectDetail());
	when(getProjectDAO().load(projectId)).thenReturn(getProjectDetail().getProject());

	ArgumentCaptor<ProjectLanguageDetail> argumentCaptor = ArgumentCaptor.forClass(ProjectLanguageDetail.class);

	getProjectLanguageDetailService().cloneProjectLangDetail(pldForClone, languageTo, projectId, 0);

	verify(getProjectLanguageDetailDAO()).saveOrUpdate(argumentCaptor.capture());

	ProjectLanguageDetail clonedPld = argumentCaptor.getValue();

	long termInSubmission = pldForClone.getTermInSubmissionCount();

	Assert.assertEquals(clonedPld.getTermCount(), pldForClone.getTermCount());
	Assert.assertEquals(clonedPld.getTermEntryCount(), pldForClone.getTermEntryCount());
	Assert.assertEquals(clonedPld.getTermInSubmissionCount(), 0);
	Assert.assertEquals(clonedPld.getApprovedTermCount(), pldForClone.getApprovedTermCount());
	Assert.assertEquals(clonedPld.getActiveSubmissionCount(), 0);
	Assert.assertEquals(clonedPld.getCompletedSubmissionCount(), 0);
	Assert.assertEquals(clonedPld.getDateModified(), pldForClone.getDateModified());
	Assert.assertEquals(clonedPld.getForbiddenTermCount(), pldForClone.getForbiddenTermCount());
	Assert.assertEquals(clonedPld.getOnHoldTermCount(), pldForClone.getOnHoldTermCount());
	/*
	 * Default term status is Pending Approval so we added all terms in submission
	 * to ProjectLanguageDetail Pending Approval count
	 */
	Assert.assertEquals(clonedPld.getPendingApprovalCount(),
		pldForClone.getPendingApprovalCount() + termInSubmission);
	Assert.assertEquals(clonedPld.getLanguageId(), languageTo);
	Assert.assertEquals(clonedPld.getProjectDetail(), getProjectDetail());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("projectLanguageDetailService")
    public void serachTest() throws TmException {
	PagedListInfo pagedListInfo = getModelObject("pagedListInfo", PagedListInfo.class);

	ProjectLanguageDetailRequest command = getModelObject("command", ProjectLanguageDetailRequest.class);

	PagedList<ProjectLanguageDetailView> pagedList = getModelObject("pagedList", PagedList.class);

	when(getProjectLanguageDetailDAO().getEntityPagedList(command, pagedListInfo)).thenReturn(pagedList);

	PagedList<ProjectLanguageDetailView> result = getProjectLanguageDetailService().search(command, pagedListInfo);

	verify(getProjectLanguageDetailDAO()).getEntityPagedList(command, pagedListInfo);

	assertNotNull(result);
	assertEquals(1, result.getTotalPageCount());

    }

    @Test
    public void tetic() {
	System.out.println(System.currentTimeMillis());
    }

    private ProjectDAO getProjectDAO() {
	return _projectDAO;
    }

    private ProjectLanguageDetailService getProjectLanguageDetailService() {
	return _projectLanguageDetailService;
    }

}
