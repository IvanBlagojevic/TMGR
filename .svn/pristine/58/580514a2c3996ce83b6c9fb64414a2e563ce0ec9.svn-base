package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static junit.framework.Assert.assertEquals;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.webmvc.test.annotations.ClientBean;
import org.gs4tr.termmanager.model.BatchJobInfo;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.service.batch.info.provider.BatchJobsInfoProvider;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Test;

@TestSuite("getBatchJobInfoController")
public class GetBatchJobInfoControllerTest extends AbstractMvcTest {

    private static final String KEEP_PINGING = "keepPinging";

    private static final String PROCESSING_COMPLETED_KEY = "processingCompleated";

    private static final String URL = "batchProcessController.ter";

    @ClientBean
    private BatchJobsInfoProvider<String> _batchJobsInfoProvider;

    @Test
    @TestCase("batchProcessingCompleted")
    @TestUser(roleName = RoleNameEnum.ADMIN)
    public void batchProcessingCompletedTest() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	BatchJobInfo batchJobInfo = getModelObject("processedItem", BatchJobInfo.class);

	when(getBatchJobsInfoProvider().provideInProcessBatchJobs(anyString()))
		.thenReturn(new ArrayList<BatchJobName>());
	when(getBatchJobsInfoProvider().provideCompletedBatchJobInfo(anyString())).thenReturn(batchJobInfo);

	Request request = createPostRequest(URL, null, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));

	responseContent.assertProperty(KEEP_PINGING, "false");

	responseContent.assertProperty(PROCESSING_COMPLETED_KEY, "true");

	JSONValidator processedItem = responseContent.getObject("processedItem");

	processedItem.assertProperty("batchProcess", batchJobInfo.getBatchJobName().getProcessDisplayName());
	processedItem.assertProperty("sucessMessage", batchJobInfo.getSuccessMessage());
	processedItem.assertProperty("longSucessMessage", batchJobInfo.getLongSuccessMessage());
	processedItem.assertProperty("title", batchJobInfo.getTitle());
	processedItem.assertProperty("msgSubTitle", batchJobInfo.getMsgSubTitle());
	processedItem.assertProperty("msgLongSubTitle", batchJobInfo.getMsgLongSubTitle());
	processedItem.assertPropertyNull("exceptionMessage");
	processedItem.assertPropertyNull("alertMessages");
    }

    @Test
    @TestCase("batchProcessingNotCompleted")
    @TestUser(roleName = RoleNameEnum.ADMIN)
    public void batchProcessingNotCompletedTest() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	when(getBatchJobsInfoProvider().provideInProcessBatchJobs(anyString()))
		.thenReturn(Arrays.asList(BatchJobName.DELETE_PROJECT_NOTE));

	Request request = createPostRequest(URL, null, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JSONValidator validator = new JSONValidator(response.getContent());

	validator.assertProperty(SUCCESS_KEY, String.valueOf(true));

	validator.assertProperty(KEEP_PINGING, "true");

	validator.assertProperty(PROCESSING_COMPLETED_KEY, "false");
    }

    public BatchJobsInfoProvider<String> getBatchJobsInfoProvider() {
	return _batchJobsInfoProvider;
    }
}
