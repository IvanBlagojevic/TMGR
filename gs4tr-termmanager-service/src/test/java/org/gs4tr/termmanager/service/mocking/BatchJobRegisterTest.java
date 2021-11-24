package org.gs4tr.termmanager.service.mocking;

import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.entry.processor.executor.HzEntryProcessorExecutor;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.BatchJobInfo;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.BatchJobsInfo;
import org.gs4tr.termmanager.service.batch.register.BatchJobRegister;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;

import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.map.AbstractEntryProcessor;
import org.springframework.beans.factory.annotation.Qualifier;

@TestSuite("service")
public class BatchJobRegisterTest extends AbstractServiceTest {
    private static final String USER_NAME = "donnie";

    @Autowired
    private BatchJobRegister<String> _batchJobRegister;

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, BatchJobsInfo> _cacheGateway;

    @Captor
    private ArgumentCaptor<AbstractEntryProcessor<String, BatchJobsInfo>> _entryProcessorCaptor;

    @Autowired
    private HzEntryProcessorExecutor _hzEntryProcessorExecutor;

    @Test
    @TestCase("batchJobRegister")
    public void registeBatchJobCompletedTest() {
	getBatchJobRegister().registeBatchJobCompleted(USER_NAME, new BatchJobInfo());
	verify(getHzEntryProcessorExecutor()).executeOnKey(any(CacheName.class), any(String.class),
		getEntryProcessorCaptor().capture());
	assertNotNull(getEntryProcessorCaptor().getValue());
    }

    @Test
    @TestCase("batchJobRegister")
    public void registerBatchJobTest() {
	getBatchJobRegister().registerBatchJob(USER_NAME, BatchJobName.DELETE_RENAME_PROJECT_ATTRIBUTE);
	verify(getCacheGateway()).putIfAbsent(any(CacheName.class), any(String.class), any(BatchJobsInfo.class));
	verify(getHzEntryProcessorExecutor()).executeOnKey(any(CacheName.class), any(String.class),
		getEntryProcessorCaptor().capture());
	assertNotNull(getEntryProcessorCaptor().getValue());
    }

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
	Mockito.reset(getCacheGateway());
	Mockito.reset(getHzEntryProcessorExecutor());
    }

    private BatchJobRegister<String> getBatchJobRegister() {
	return _batchJobRegister;
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
