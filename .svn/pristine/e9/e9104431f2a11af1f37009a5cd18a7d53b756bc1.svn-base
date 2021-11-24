package org.gs4tr.termmanager.io.tests.edd.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.gs4tr.termmanager.io.edd.event.UpdateCountEvent;
import org.gs4tr.termmanager.io.edd.handler.CountsUpdateHandler;
import org.gs4tr.termmanager.io.tests.AbstractIOTest;
import org.gs4tr.termmanager.io.tests.TestHelper;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.dto.ProjectDetailsIO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CountUpdateHandlerTest extends AbstractIOTest {

    @Autowired
    private CountsUpdateHandler _countsUpdateHandler;

    @Test(expected = IllegalArgumentException.class)
    public void projectDetailUpdateFailTest() {
	getCountsUpdateHandler().onEvent(new UpdateCountEvent(null, null));

    }

    @Test
    public void projectDetailUpdatedSuccessfullyTest() {

	ProjectDetail beforeUpdate = getProjectDetailDAO().findByProjectId(TestHelper.PROJECT_ID_1);
	assertNotNull(beforeUpdate);

	assertEquals(0, beforeUpdate.getTermEntryCount());
	assertEquals(0, beforeUpdate.getActiveSubmissionCount());
	assertEquals(0, beforeUpdate.getApprovedTermCount());
	assertEquals(0, beforeUpdate.getCompletedSubmissionCount());
	assertEquals(0, beforeUpdate.getForbiddenTermCount());
	assertEquals(0, beforeUpdate.getOnHoldTermCount());
	assertEquals(0, beforeUpdate.getPendingApprovalCount());
	assertEquals(0, beforeUpdate.getTermCount());
	assertEquals(0, beforeUpdate.getTermInSubmissionCount());

	ProjectDetailsIO infoDetails = TestHelper.createInfoDetails();

	getCountsUpdateHandler().onEvent(new UpdateCountEvent(infoDetails, null));

	getProjectDetailDAO().flush();
	getProjectDetailDAO().clear();

	ProjectDetail afterUpdate = getProjectDetailDAO().findByProjectId(TestHelper.PROJECT_ID_1);
	assertNotNull(afterUpdate);

	assertEquals(5, afterUpdate.getTermEntryCount());
	assertEquals(5, afterUpdate.getActiveSubmissionCount());
	assertEquals(5, afterUpdate.getApprovedTermCount());
	assertEquals(5, afterUpdate.getCompletedSubmissionCount());
	assertEquals(5, afterUpdate.getForbiddenTermCount());
	assertEquals(5, afterUpdate.getOnHoldTermCount());
	assertEquals(5, afterUpdate.getPendingApprovalCount());
	assertEquals(25, afterUpdate.getTermCount());
	assertEquals(5, afterUpdate.getTermInSubmissionCount());

    }

    private CountsUpdateHandler getCountsUpdateHandler() {
	return _countsUpdateHandler;
    }
}
