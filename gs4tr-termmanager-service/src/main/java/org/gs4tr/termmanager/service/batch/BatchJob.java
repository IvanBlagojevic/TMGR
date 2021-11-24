package org.gs4tr.termmanager.service.batch;

import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
import org.gs4tr.termmanager.model.BatchMessage;

@FunctionalInterface
public interface BatchJob {

    void start(NotifyingMessageListener<BatchMessage> listener, BatchMessage message);
}
