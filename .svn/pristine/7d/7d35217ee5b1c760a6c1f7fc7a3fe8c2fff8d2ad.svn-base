package org.gs4tr.termmanager.service.batch.executor;

import java.util.Map;
import java.util.Objects;

import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.BatchMessage;
import org.gs4tr.termmanager.service.batch.BatchJob;
import org.gs4tr.termmanager.service.concurrency.RunnableCallback;
import org.gs4tr.termmanager.service.concurrency.ServiceThreadPoolHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("batchJobExecutorImpl")
public class BatchJobExecutorImpl implements BatchJobExecutor {

    @Override
    public void execute(BatchJob batchJob, BatchMessage message, NotifyingMessageListener<BatchMessage> listener) {
	if (Objects.nonNull(batchJob)) {
	    ServiceThreadPoolHandler.execute(new RunnableCallback() {
		@Override
		public void execute() {
		    batchJob.start(listener, message);
		}

		@Override
		public String getRunnableOperation() {
		    Map<String, Object> map = message.getPropertiesMap();
		    Object object = map.get(BatchMessage.BATCH_PROCESS);
		    BatchJobName batchJobName = (BatchJobName) object;
		    return batchJobName.getProcessDisplayName();
		}
	    }, SecurityContextHolder.getContext().getAuthentication());
	}
    }
}
