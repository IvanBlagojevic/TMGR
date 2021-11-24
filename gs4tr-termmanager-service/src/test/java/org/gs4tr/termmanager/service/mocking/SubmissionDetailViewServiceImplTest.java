package org.gs4tr.termmanager.service.mocking;

import java.util.List;

import junit.framework.Assert;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.termmanager.dao.SubmissionDetailViewDAO;
import org.gs4tr.termmanager.dao.SubmissionUserDAO;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.search.command.SubmissionSearchRequest;
import org.gs4tr.termmanager.model.view.SubmissionDetailView;
import org.gs4tr.termmanager.service.SubmissionDetailViewService;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("service")
public class SubmissionDetailViewServiceImplTest extends AbstractServiceTest {

    @Autowired
    private SubmissionDetailViewService _submissionDetailViewService;

    @Autowired
    private SubmissionDetailViewDAO _submissionDetailViewDAO;

    @Autowired
    private SubmissionUserDAO _submissionUserDAO;

    @Test
    @TestCase("submissionDetailViewService")
    public void searchWhenSubmissionIdsIsEmptyOneTest() throws Exception {
	PagedListInfo pagedListInfo = getModelObject("pagedListInfo", PagedListInfo.class);
	SubmissionSearchRequest command = getModelObject("commandWithEmptySubmissionIds",
		SubmissionSearchRequest.class);
	@SuppressWarnings("unchecked")
	List<Submission> submissions = getModelObject("emptySubmissions", List.class);

	Mockito.when(getSubmissionUserDAO().findSubmissionsByUserId(Mockito.anyLong())).thenReturn(submissions);

	TaskPagedList<SubmissionDetailView> result = (TaskPagedList<SubmissionDetailView>) getSubmissionDetailViewService()
		.search(command, pagedListInfo);

	Mockito.verify(getSubmissionUserDAO(), Mockito.atLeastOnce()).findSubmissionsByUserId(Mockito.anyLong());

	Assert.assertNotNull(result);
	Assert.assertNotNull(result.getTasks());
	Assert.assertTrue(result.getTasks().length == 0);
	Assert.assertEquals(pagedListInfo, result.getPagedListInfo());
	Assert.assertNull(result.getElements());
    }

    @Test
    @TestCase("submissionDetailViewService")
    public void searchWhenSubmissionIdsIsEmptyTwoTest() throws Exception {
	PagedListInfo pagedListInfo = getModelObject("pagedListInfo", PagedListInfo.class);
	SubmissionSearchRequest command = getModelObject("commandWithEmptySubmissionIds",
		SubmissionSearchRequest.class);
	@SuppressWarnings("unchecked")
	List<Submission> submissions = getModelObject("submissions", List.class);
	@SuppressWarnings("unchecked")
	PagedList<SubmissionDetailView> pagedList = getModelObject("pagedList", PagedList.class);

	Mockito.when(getSubmissionUserDAO().findSubmissionsByUserId(Mockito.anyLong())).thenReturn(submissions);
	Mockito.when(getSubmissionDetailViewDAO().getEntityPagedList(command, pagedListInfo)).thenReturn(pagedList);

	TaskPagedList<SubmissionDetailView> result = (TaskPagedList<SubmissionDetailView>) getSubmissionDetailViewService()
		.search(command, pagedListInfo);

	Assert.assertTrue(command.getSubmissionIds().contains(7L));
	Assert.assertEquals(command.getUserName(), "marko");
	Assert.assertNotNull(result);
	Assert.assertNotNull(result.getTasks());
	Assert.assertEquals(pagedListInfo, result.getPagedListInfo());
	Assert.assertNotNull(result.getElements());
	Assert.assertTrue(result.getElements().length == 1);
    }

    @Test
    @TestCase("submissionDetailViewService")
    public void searchWhenSubmissionIdsHaveOneElementTest() throws Exception {
	PagedListInfo pagedListInfo = getModelObject("pagedListInfo", PagedListInfo.class);
	SubmissionSearchRequest command = getModelObject("commandWithOneElementInSubmissionIds",
		SubmissionSearchRequest.class);
	@SuppressWarnings("unchecked")
	PagedList<SubmissionDetailView> pagedList = getModelObject("pagedList", PagedList.class);

	Mockito.when(getSubmissionDetailViewDAO().getEntityPagedList(command, pagedListInfo)).thenReturn(pagedList);

	TaskPagedList<SubmissionDetailView> result = (TaskPagedList<SubmissionDetailView>) getSubmissionDetailViewService()
		.search(command, pagedListInfo);

	Mockito.verify(getSubmissionDetailViewDAO(), new Times(1)).getEntityPagedList(command, pagedListInfo);

	Assert.assertNotNull(result);
	Assert.assertNotNull(result.getTasks());
	Assert.assertTrue(result.getTasks().length == 0);
	Assert.assertEquals(pagedListInfo, result.getPagedListInfo());
	Assert.assertNotNull(result.getElements());
	Assert.assertTrue(result.getElements().length == 1);
    }

    private SubmissionDetailViewService getSubmissionDetailViewService() {
	return _submissionDetailViewService;
    }

    private SubmissionDetailViewDAO getSubmissionDetailViewDAO() {
	return _submissionDetailViewDAO;
    }

    private SubmissionUserDAO getSubmissionUserDAO() {
	return _submissionUserDAO;
    }

}
