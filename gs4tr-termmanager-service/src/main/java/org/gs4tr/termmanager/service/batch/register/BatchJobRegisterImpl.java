package org.gs4tr.termmanager.service.batch.register;

import java.util.Map.Entry;

import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.entry.processor.executor.HzEntryProcessorExecutor;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.BatchJobInfo;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.BatchJobsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hazelcast.map.AbstractEntryProcessor;

@Service("batchJobRegisterImpl")
public class BatchJobRegisterImpl implements BatchJobRegister<String> {

    private static class RegisterBatchJobCompletedEntryProcessor extends AbstractEntryProcessor<String, BatchJobsInfo> {

	private static final long serialVersionUID = -1604045999447864771L;

	private final BatchJobInfo _batchJobInfo;

	public RegisterBatchJobCompletedEntryProcessor(BatchJobInfo batchJobInfo) {
	    _batchJobInfo = batchJobInfo;
	}

	public BatchJobInfo getBatchJobInfo() {
	    return _batchJobInfo;
	}

	@Override
	public Object process(Entry<String, BatchJobsInfo> entry) {
	    BatchJobsInfo batchJobsInfo = entry.getValue();
	    batchJobsInfo.getBatchJobsCompleted().add(getBatchJobInfo());
	    entry.setValue(batchJobsInfo);
	    return null;
	}
    }

    private static class RegisterBatchJobEntryProcessor extends AbstractEntryProcessor<String, BatchJobsInfo> {

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

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, BatchJobsInfo> _cacheGateway;

    @Autowired
    private HzEntryProcessorExecutor _hzEntryProcessorExecutor;

    @Override
    public void registeBatchJobCompleted(String key, BatchJobInfo batchJobInfo) {
	getHzEntryProcessorExecutor().executeOnKey(CacheName.BATCH_PROCESSING_STATUS, key,
		new RegisterBatchJobCompletedEntryProcessor(batchJobInfo));
    }

    @Override
    public void registerBatchJob(String key, BatchJobName batchJobName) {
	getCacheGateway().putIfAbsent(CacheName.BATCH_PROCESSING_STATUS, key, new BatchJobsInfo());
	getHzEntryProcessorExecutor().executeOnKey(CacheName.BATCH_PROCESSING_STATUS, key,
		new RegisterBatchJobEntryProcessor(batchJobName));
    }

    private CacheGateway<String, BatchJobsInfo> getCacheGateway() {
	return _cacheGateway;
    }

    private HzEntryProcessorExecutor getHzEntryProcessorExecutor() {
	return _hzEntryProcessorExecutor;
    }
}
