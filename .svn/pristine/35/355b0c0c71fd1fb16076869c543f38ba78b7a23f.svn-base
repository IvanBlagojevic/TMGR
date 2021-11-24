package org.gs4tr.termmanager.service.batch.info.provider;

import java.util.List;

import org.gs4tr.termmanager.model.BatchJobInfo;
import org.gs4tr.termmanager.model.BatchJobName;

public interface BatchJobsInfoProvider<K> {

    BatchJobInfo provideCompletedBatchJobInfo(K key);

    List<BatchJobName> provideInProcessBatchJobs(K key);
}
