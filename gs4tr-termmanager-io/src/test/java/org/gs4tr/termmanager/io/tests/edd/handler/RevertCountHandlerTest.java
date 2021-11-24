package org.gs4tr.termmanager.io.tests.edd.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.List;

import org.gs4tr.termmanager.io.edd.event.ProcessDataEvent;
import org.gs4tr.termmanager.io.edd.event.RevertCountEvent;
import org.gs4tr.termmanager.io.edd.handler.CountsRevertHandler;
import org.gs4tr.termmanager.io.edd.handler.SolrUpdateHandler;
import org.gs4tr.termmanager.io.tests.AbstractIOTest;
import org.gs4tr.termmanager.io.tests.TestHelper;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RevertCountHandlerTest extends AbstractIOTest {

    private static final String REGULAR_COLLECTION_V2 = "regularV2";

    @Autowired
    private CountsRevertHandler _countsRevertHandler;

    @Autowired
    private SolrUpdateHandler _solrUpdateHandler;

    @Test(expected = IllegalArgumentException.class)
    public void dashboardCountRevertFailedTest() {
	getCountsRevertHandler().onEvent(new RevertCountEvent(TestHelper.PROJECT_ID_1, null));
    }

    @Test
    public void dashboardCountRevertedSuccessfullyTest() {

	ProjectDetail projectDetailBeforeUpdate = getProjectDetailDAO().findByProjectId(TestHelper.PROJECT_ID_1);
	assertNotNull(projectDetailBeforeUpdate);

	assertEquals(0, projectDetailBeforeUpdate.getTermEntryCount());
	assertEquals(0, projectDetailBeforeUpdate.getApprovedTermCount());
	assertEquals(0, projectDetailBeforeUpdate.getTermCount());

	ProjectLanguageDetail projectLanguageDetailBeforeUpdate = getProjectLanguageDetailFromDb(
		TestHelper.PROJECT_ID_1, LANGUAGE_ID);
	assertNotNull(projectLanguageDetailBeforeUpdate);

	assertEquals(0, projectLanguageDetailBeforeUpdate.getTermEntryCount());
	assertEquals(0, projectLanguageDetailBeforeUpdate.getApprovedTermCount());
	assertEquals(0, projectLanguageDetailBeforeUpdate.getTermCount());

	getSolrUpdateHandler().onEvent(
		new ProcessDataEvent(REGULAR_COLLECTION_V2, Collections.singletonList(TestHelper.createTermEntry())));

	getProjectDetailDAO().flush();
	getProjectDetailDAO().clear();

	// CountsRevertHandler will read data from Solr and update counts in database
	getCountsRevertHandler().onEvent(new RevertCountEvent(TestHelper.PROJECT_ID_1, REGULAR_COLLECTION_V2));

	ProjectDetail projectDetailAfterUpdate = getProjectDetailDAO().findByProjectId(TestHelper.PROJECT_ID_1);
	assertNotNull(projectDetailAfterUpdate);

	assertEquals(1, projectDetailAfterUpdate.getTermEntryCount());
	assertEquals(9, projectDetailAfterUpdate.getApprovedTermCount());
	assertEquals(9, projectDetailAfterUpdate.getTermCount());

	ProjectLanguageDetail projectLanguageDetailAfterUpdate = getProjectLanguageDetailFromDb(TestHelper.PROJECT_ID_1,
		LANGUAGE_ID);
	assertNotNull(projectLanguageDetailAfterUpdate);

	assertEquals(1, projectLanguageDetailAfterUpdate.getTermEntryCount());
	assertEquals(9, projectLanguageDetailAfterUpdate.getApprovedTermCount());
	assertEquals(9, projectLanguageDetailAfterUpdate.getTermCount());

    }

    private CountsRevertHandler getCountsRevertHandler() {
	return _countsRevertHandler;
    }

    private ProjectLanguageDetail getProjectLanguageDetailFromDb(long projectId, String languageId) {

	List<ProjectLanguageDetail> languageDetails = getProjectLanguageDetailDAO()
		.getProjectLanguageDetailsByProjectId(projectId);
	return languageDetails.stream().filter(e -> e.getLanguageId().equals(languageId)).findFirst().orElse(null);

    }

    private SolrUpdateHandler getSolrUpdateHandler() {
	return _solrUpdateHandler;
    }

}
