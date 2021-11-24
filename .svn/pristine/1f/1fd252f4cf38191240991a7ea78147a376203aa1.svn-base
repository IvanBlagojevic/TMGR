package org.gs4tr.termmanager.service.loghandler;

import java.util.Optional;

import org.gs4tr.foundation3.callback.ImportCallback;
import org.gs4tr.termmanager.io.tlog.TransactionLogIO;
import org.gs4tr.termmanager.io.tlog.impl.AbstractTransactionLogHandler;
import org.gs4tr.termmanager.io.tlog.impl.TransactionLogHandler;
import org.gs4tr.termmanager.io.utils.PropertyUtils;
import org.gs4tr.termmanager.model.TransactionalUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.EntityIterable;

@Component
public class ImportTransactionLogHandler extends AbstractTransactionLogHandler
	implements TransactionLogIO<EntityId, TransactionalUnit> {

    @Autowired
    private TransactionLogHandler _logHandler;

    @Value("${index.batchSize:500}")
    private Integer numOfTermEntriesPerChild;

    @Override
    public Optional<EntityId> appendAndLink(long projectId, EntityId parentKey, TransactionalUnit unit) {
	return getLogHandler().appendAndLink(projectId, parentKey, unit);
    }

    @Override
    public void finishAppending(long projectId, EntityId key) {
	getLogHandler().finishAppending(projectId, key);
    }

    public void finishAppendingWithCallback(long projectId, EntityId parentKey, ImportCallback callback) {
	try {
	    computeInExclusiveTransaction(projectId, txn -> {
		Entity entity = txn.getEntity(parentKey);

		entity.setProperty(PropertyUtils.FINISHED, Boolean.TRUE);
		entity.setProperty(PropertyUtils.FINISH_TIMESTAMP, System.currentTimeMillis());

		String collection = entity.getProperty(PropertyUtils.COLLECTION).toString();

		EntityIterable batchChildIterable = entity.getLinks(PropertyUtils.BATCH_LINK);

		int totalTerms = callback.getTotalTerms();

		int currentChildCount = 1;

		int percentage;

		try {
		    for (Entity child : batchChildIterable) {
			dispatch(collection, child);

			int currentTermCount = currentChildCount * getNumOfTermEntriesPerChild();

			percentage = getPercentage(totalTerms, currentTermCount);

			callback.handlePercentage(percentage);
			currentChildCount++;
		    }
		} finally {
		    if (!callback.isImportCanceled()) {
			callback.handlePercentage(100);
		    }
		}

		return entity.getId();
	    });
	} finally {
	    getStoreHandler().closeAndClear(projectId);
	}
    }

    @Override
    public boolean isLocked(long projectId) {
	return getLogHandler().isLocked(projectId);
    }

    @Override
    public Optional<EntityId> startAppending(long projectId, String user, String action, String collection) {
	return getLogHandler().startAppending(projectId, user, action, collection);
    }

    private TransactionLogHandler getLogHandler() {
	return _logHandler;
    }

    private Integer getNumOfTermEntriesPerChild() {
	return numOfTermEntriesPerChild;
    }

    /*
     * Percentage value at this point must be 51 and above due buffer flush
     * implementation(flush every time when percentage is equals 50)
     */
    private int getPercentage(int totalTerms, int currentTermCount) {
	int percent = ((int) (((float) currentTermCount / (float) totalTerms) * 49));
	return 51 + percent;
    }
}
