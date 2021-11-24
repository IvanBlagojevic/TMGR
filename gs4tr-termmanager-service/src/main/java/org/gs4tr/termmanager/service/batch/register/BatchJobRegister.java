package org.gs4tr.termmanager.service.batch.register;

import org.gs4tr.termmanager.model.BatchJobInfo;
import org.gs4tr.termmanager.model.BatchJobName;

public interface BatchJobRegister<K> {

    void registeBatchJobCompleted(K key, BatchJobInfo batchJobInfo);

    void registerBatchJob(K key, BatchJobName batchJobName);
}
