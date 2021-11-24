package org.gs4tr.termmanager.service.mocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.entry.processor.executor.HzEntryProcessorExecutor;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.BatchJobInfo;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.BatchJobsInfo;
import org.gs4tr.termmanager.service.batch.info.provider.BatchJobsInfoProvider;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.hazelcast.map.AbstractEntryProcessor;

@TestSuite("service")
public class BatchJobsInfoProviderTest extends AbstractServiceTest {
    private static final String USER_NAME = "donnie";

    @Autowired
    private BatchJobsInfoProvider<String> _batchJobsInfoProvider;

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, BatchJobsInfo> _cacheGateway;

    @Captor
    private ArgumentCaptor<AbstractEntryProcessor<String, BatchJobsInfo>> _entryProcessorCaptor;

    @Autowired
    private HzEntryProcessorExecutor _hzEntryProcessorExecutor;

    @SuppressWarnings("unchecked")
    @Before
    public void cleanUp() {
	reset(getCacheGateway());
	reset(getHzEntryProcessorExecutor());
    }

    @Test
    @TestCase("batchJobsInfoProvider")
    public void provideCompletedBatchJobInfoTest() {
	BatchJobInfo batchJobInfo = getModelObject("batchJobInfo", BatchJobInfo.class);
	when(getHzEntryProcessorExecutor().executeOnKey(any(CacheName.class), any(String.class),
		getEntryProcessorCaptor().capture())).thenReturn(batchJobInfo);

	BatchJobInfo result = getBatchJobsInfoProvider().provideCompletedBatchJobInfo(USER_NAME);

	verify(getHzEntryProcessorExecutor()).executeOnKey(any(CacheName.class), any(String.class),
		getEntryProcessorCaptor().capture());
	assertNotNull(getEntryProcessorCaptor().getValue());

	assertEquals(result.getBatchJobName(), BatchJobName.REPORT_EXPORT);
	assertEquals(result.getLongSuccessMessage(), "Exporting of projects report is finished.");
    }

    @Test
    @TestCase("batchJobsInfoProvider")
    public void provideInProcessBatchJobsTest() {
	BatchJobsInfo batchJobsInfo = new BatchJobsInfo();

	batchJobsInfo.getBatchJobsInProcess().add(BatchJobName.DELETE_PROJECT_NOTE);
	when(getCacheGateway().get(CacheName.BATCH_PROCESSING_STATUS, USER_NAME)).thenReturn(batchJobsInfo);

	List<BatchJobName> batchJobsInProcess = getBatchJobsInfoProvider().provideInProcessBatchJobs(USER_NAME);

	verify(getCacheGateway()).get(CacheName.BATCH_PROCESSING_STATUS, USER_NAME);

	assertFalse(batchJobsInProcess.isEmpty());
	assertEquals(1, batchJobsInProcess.size());

	BatchJobName batchJobName = batchJobsInProcess.get(0);
	assertEquals(BatchJobName.DELETE_PROJECT_NOTE, batchJobName);
    }

    private BatchJobsInfoProvider<String> getBatchJobsInfoProvider() {
	return _batchJobsInfoProvider;
    }

    private CacheGateway<String, BatchJobsInfo> getCacheGateway() {
	return _cacheGateway;
    }

    private ArgumentCaptor<AbstractEntryProcessor<String, BatchJobsInfo>> getEntryProcessorCaptor() {
	return _entryProcessorCaptor;
    }

    private HzEntryProcessorExecutor getHzEntryProcessorExecutor() {
	return _hzEntryProcessorExecutor;
    }
}
