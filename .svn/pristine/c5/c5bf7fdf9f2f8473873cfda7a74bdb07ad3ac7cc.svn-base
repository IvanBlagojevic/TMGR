package org.gs4tr.termmanager.service;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.termmanager.model.search.command.SubmissionSearchRequest;
import org.gs4tr.termmanager.model.view.SubmissionDetailView;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SubmissionDetailViewServiceTest extends AbstractSpringServiceTests {

    @Autowired
    private SubmissionDetailViewService _submissionDetailViewService;

    @Test
    public void testSearchSubmissionDetailView() {
	Long submissionId = 1L;
	Set<Long> submissionIds = new HashSet<Long>();
	submissionIds.add(submissionId);

	SubmissionSearchRequest command = new SubmissionSearchRequest();
	command.setName("First");
	command.setSubmissionIds(submissionIds);

	PagedListInfo info = new PagedListInfo();

	TaskPagedList<SubmissionDetailView> pagedList = (TaskPagedList<SubmissionDetailView>) getSubmissionDetailViewService()
		.search(command, info);
	Assert.assertNotNull(pagedList);

	SubmissionDetailView[] elements = pagedList.getElements();
	Assert.assertTrue(ArrayUtils.isNotEmpty(elements));

	Assert.assertTrue(1 == elements.length);
    }

    private SubmissionDetailViewService getSubmissionDetailViewService() {
	return _submissionDetailViewService;
    }
}
