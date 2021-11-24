package org.gs4tr.termmanager.io.tlog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public interface TransactionProcessor<R, T> {

    Log LOGGER = LogFactory.getLog(TransactionProcessor.class);

    default void afterTransactionCompletion() {
	LOGGER.info("Transaction is finished.");
    }

    default void beforeTransactionCompletion() {
	LOGGER.info("Starting new transaction.");
    }

    default void onError(Exception e) {
	LOGGER.error(e);
    }

    R process(T txn);
}
