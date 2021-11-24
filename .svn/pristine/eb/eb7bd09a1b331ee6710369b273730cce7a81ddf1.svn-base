package org.gs4tr.termmanager.service.batch.info.provider;

import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
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

@Service("batchJobsInfoProviderImpl")
public class BatchJobsInfoProviderImpl implements BatchJobsInfoProvider<String> {

    @Autowired
    @Qualifier("guavaCacheGateway")
    private CacheGateway<String, BatchJobsInfo> _guavaCacheGateway;

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, BatchJobsInfo> _hzCacheGateway;

    @Autowired
    private HzEntryProcessorExecutor _hzEntryProcessorExecutor;

    @Override
    public BatchJobInfo provideCompletedBatchJobInfo(String key) {
	// first check guava cache
	BatchJobInfo batchJobInfo = checkGuavaJobInfo(key);
	if (Objects.nonNull(batchJobInfo)) {
	    return batchJobInfo;
	}

	return (BatchJobInfo) getHzEntryProcessorExecutor().executeOnKey(CacheName.BATCH_PROCESSING_STATUS, key,
		new GetCompletedBatchJobInfoEntryProcessor());
    }

    @Override
    public List<BatchJobName> provideInProcessBatchJobs(String key) {
	BatchJobsInfo batchJobsInfo = getHzCacheGateway().get(CacheName.BATCH_PROCESSING_STATUS, key);
	return batchJobsInfo.getBatchJobsInProcess();
    }

    private BatchJobInfo checkGuavaJobInfo(String key) {
	BatchJobsInfo jobs = getGuavaCacheGateway().get(CacheName.BATCH_PROCESSING_STATUS, key);
	if (Objects.isNull(jobs)) {
	    return null;
	}

	List<BatchJobInfo> jobsCompleted = jobs.getBatchJobsCompleted();
	if (CollectionUtils.isEmpty(jobsCompleted)) {
	    return null;
	}

	BatchJobInfo jobComplited = jobsCompleted.get(0);

	List<BatchJobName> batchJobsInProcess = jobs.getBatchJobsInProcess();
	if (CollectionUtils.isNotEmpty(batchJobsInProcess)) {
	    batchJobsInProcess.remove(jobComplited.getBatchJobName());
	}
	jobsCompleted.remove(0);

	return jobComplited;
    }

    private CacheGateway<String, BatchJobsInfo> getGuavaCacheGateway() {
	return _guavaCacheGateway;
    }

    private CacheGateway<String, BatchJobsInfo> getHzCacheGateway() {
	return _hzCacheGateway;
    }

    private HzEntryProcessorExecutor getHzEntryProcessorExecutor() {
	return _hzEntryProcessorExecutor;
    }

    private static class GetCompletedBatchJobInfoEntryProcessor extends AbstractEntryProcessor<String, BatchJobsInfo> {

	private static final long serialVersionUID = 9200946790685054214L;

	@Override
	public Object process(Entry<String, BatchJobsInfo> entry) {
	    BatchJobsInfo batchJobsInfo = entry.getValue();
	    if (Objects.isNull(batchJobsInfo)) {
		return null;
	    }
	    List<BatchJobInfo> batchJobsCompleted = batchJobsInfo.getBatchJobsCompleted();
	    if (CollectionUtils.isEmpty(batchJobsCompleted)) {
		return null;
	    }
	    BatchJobInfo batchJobInfo = batchJobsCompleted.get(0);

	    List<BatchJobName> batchJobsInProcess = batchJobsInfo.getBatchJobsInProcess();
	    if (CollectionUtils.isNotEmpty(batchJobsInProcess)) {
		batchJobsInProcess.remove(batchJobInfo.getBatchJobName());
	    }
	    batchJobsCompleted.remove(0);
	    entry.setValue(batchJobsInfo);

	    return batchJobInfo;
	}
    }
}
