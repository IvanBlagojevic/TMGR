package org.gs4tr.termmanager.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map.Entry;

import org.gs4tr.termmanager.cache.entry.processor.executor.HzEntryProcessorExecutor;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.BatchJobsInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.map.AbstractEntryProcessor;

public class HzEntryProcessorExecutorTest extends AbstractSpringCacheGatewayTest<String, BatchJobsInfo> {

    private static final class RegisterBatchJobEntryProcessor extends AbstractEntryProcessor<String, BatchJobsInfo> {

	private static final long serialVersionUID = -2167735604332567361L;

	private final BatchJobName _batchJobName;

	public RegisterBatchJobEntryProcessor(BatchJobName batchJobName) {
	    _batchJobName = batchJobName;
	}

	public BatchJobName getBatchJobName() {
	    return _batchJobName;
	}

	@Override
	public Object process(Entry<String, BatchJobsInfo> entry) {
	    BatchJobsInfo batchJobsInfo = entry.getValue();
	    batchJobsInfo.getBatchJobsInProcess().add(getBatchJobName());
	    entry.setValue(batchJobsInfo);
	    return null;
	}
    }

    private static final String KEY_1 = "key1";

    private static final String KEY_2 = "key2";

    @Autowired
    private HzEntryProcessorExecutor _hzEntryProcessorExecutor;

    @Test
    public void executeOnEntriesTest() {
	getCacheGateway().putIfAbsent(CacheName.BATCH_PROCESSING_STATUS, KEY_2, new BatchJobsInfo());
	getCacheGateway().putIfAbsent(CacheName.BATCH_PROCESSING_STATUS, KEY_1, new BatchJobsInfo());

	RegisterBatchJobEntryProcessor registerBatchJob = new RegisterBatchJobEntryProcessor(
		BatchJobName.REPORT_EXPORT);
	getHzEntryProcessorExecutor().executeOnEntries(CacheName.BATCH_PROCESSING_STATUS, registerBatchJob);

	BatchJobsInfo batchJobsInfo = getCacheGateway().get(CacheName.BATCH_PROCESSING_STATUS, KEY_2);
	BatchJobsInfo batchJobsInfo1 = getCacheGateway().get(CacheName.BATCH_PROCESSING_STATUS, KEY_1);

	List<BatchJobName> batchJobsInProcess = batchJobsInfo.getBatchJobsInProcess();
	List<BatchJobName> batchJobsInProcess1 = batchJobsInfo1.getBatchJobsInProcess();

	assertEquals(1, batchJobsInProcess.size());
	assertEquals(1, batchJobsInProcess1.size());

	assertEquals(BatchJobName.REPORT_EXPORT, batchJobsInProcess.get(0));
	assertEquals(BatchJobName.REPORT_EXPORT, batchJobsInProcess1.get(0));
    }

    @Test
    public void executeOnKeyTest() {
	getCacheGateway().putIfAbsent(CacheName.BATCH_PROCESSING_STATUS, KEY_2, new BatchJobsInfo());
	RegisterBatchJobEntryProcessor registerBatchJob = new RegisterBatchJobEntryProcessor(
		BatchJobName.DELETE_PROJECT_NOTE);
	getHzEntryProcessorExecutor().executeOnKey(CacheName.BATCH_PROCESSING_STATUS, KEY_2, registerBatchJob);

	BatchJobsInfo batchJobsInfo = getCacheGateway().get(CacheName.BATCH_PROCESSING_STATUS, KEY_2);
	List<BatchJobName> batchJobsInProcess = batchJobsInfo.getBatchJobsInProcess();
	assertEquals(1, batchJobsInProcess.size());
	assertEquals(BatchJobName.DELETE_PROJECT_NOTE, batchJobsInProcess.get(0));

	RegisterBatchJobEntryProcessor registerNewBatchJob = new RegisterBatchJobEntryProcessor(
		BatchJobName.DELETE_RENAME_PROJECT_ATTRIBUTE);
	getHzEntryProcessorExecutor().executeOnKey(CacheName.BATCH_PROCESSING_STATUS, KEY_2, registerNewBatchJob);

	BatchJobsInfo newBatchJobsInfo = getCacheGateway().get(CacheName.BATCH_PROCESSING_STATUS, KEY_2);
	List<BatchJobName> newBatchJobsInProcess = newBatchJobsInfo.getBatchJobsInProcess();
	assertEquals(2, newBatchJobsInProcess.size());
	assertTrue(newBatchJobsInProcess.contains(BatchJobName.DELETE_PROJECT_NOTE));
	assertTrue(newBatchJobsInProcess.contains(BatchJobName.DELETE_RENAME_PROJECT_ATTRIBUTE));
    }

    private HzEntryProcessorExecutor getHzEntryProcessorExecutor() {
	return _hzEntryProcessorExecutor;
    }
}
