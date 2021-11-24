package org.gs4tr.termmanager.service.batch.executor;

import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
import org.gs4tr.termmanager.model.BatchMessage;
import org.gs4tr.termmanager.service.batch.BatchJob;

@FunctionalInterface
public interface BatchJobExecutor {

    void execute(BatchJob batchJob, BatchMessage message, NotifyingMessageListener<BatchMessage> listener);
}
