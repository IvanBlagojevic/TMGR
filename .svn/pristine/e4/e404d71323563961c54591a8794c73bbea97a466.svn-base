package org.gs4tr.termmanager.service.mocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.dao.SubmissionLanguageDetailViewDAO;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.search.command.SubmissionLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.SubmissionLanguageDetailView;
import org.gs4tr.termmanager.service.SubmissionLanguageDetailViewService;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.tm3.api.TmException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("service")
public class SubmissionLanguageDetailViewServiceTest extends AbstractServiceTest {

    @Autowired
    private SubmissionLanguageDetailViewDAO _submissionLanguageDetailViewDAO;

    @Autowired
    private SubmissionLanguageDetailViewService _submissionLanguageDetailViewService;

    Submission _submission;

    public SubmissionLanguageDetailViewService getSubmissionLanguageDetailViewService() {
	return _submissionLanguageDetailViewService;
    }

    @Before
    public void setUpSubmission() {
	// EntityStatusPriority status = getModelObject("entityStatusPriority",
	// EntityStatusPriority.class);
	_submission = getModelObject("submission", Submission.class);
	when(getSubmissionDAO().load(any(Long.class))).thenReturn(_submission);
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("submissionLanguageDetailViewService")
    public void test() throws TmException {
	PagedListInfo pagedListInfo = getModelObject("pagedListInfo", PagedListInfo.class);

	SubmissionLanguageDetailRequest command = getModelObject("command", SubmissionLanguageDetailRequest.class);

	PagedList<SubmissionLanguageDetailView> pagedList = getModelObject("pagedList", PagedList.class);// new
													 // PagedList<SubmissionLanguageDetailView>();

	when(getSubmissionLanguageDetailViewDAO().getEntityPagedList(command, pagedListInfo)).thenReturn(pagedList);

	PagedList<SubmissionLanguageDetailView> result = getSubmissionLanguageDetailViewService().search(command,
		pagedListInfo);

	verify(getSubmissionLanguageDetailViewDAO()).getEntityPagedList(command, pagedListInfo);

	assertNotNull(result);
	assertEquals(1, result.getTotalPageCount());

    }

    private SubmissionLanguageDetailViewDAO getSubmissionLanguageDetailViewDAO() {
	return _submissionLanguageDetailViewDAO;
    }

}
