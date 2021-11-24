package org.gs4tr.termmanager.io.tlog.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.io.edd.api.EventDispatcher;
import org.gs4tr.termmanager.io.edd.event.ProcessDataEvent;
import org.gs4tr.termmanager.io.edd.event.RevertCountEvent;
import org.gs4tr.termmanager.io.edd.event.RevertDataEvent;
import org.gs4tr.termmanager.io.exception.EventException;
import org.gs4tr.termmanager.io.exception.TransactionError;
import org.gs4tr.termmanager.io.tlog.TransactionProcessor;
import org.gs4tr.termmanager.io.tlog.config.PersistentStoreHandler;
import org.gs4tr.termmanager.io.utils.PropertyUtils;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.serializer.JsonIO;
import org.springframework.beans.factory.annotation.Autowired;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.StoreTransaction;

public class AbstractTransactionLogHandler {

    @Autowired
    private EventDispatcher _dispatcher;

    @Autowired
    private PersistentStoreHandler storeHandler;

    protected final Log LOGGER = LogFactory.getLog(this.getClass());

    private void calculateCounts(String collection, long projectId) {

	try {
	    getDispatcher().dispatch(new RevertCountEvent(projectId, collection));
	} catch (Exception e) {
	    throw new EventException(e.getMessage(), e);
	}

    }

    private void revert(String collection, List<TermEntry> termEntries) {
	try {
	    getDispatcher().dispatch(new RevertDataEvent(collection, termEntries));
	} catch (Exception e) {
	    throw new EventException(e.getMessage(), e);
	}
    }

    protected <T> Optional<T> computeInExclusiveTransaction(long projectId,
	    TransactionProcessor<T, StoreTransaction> processor) throws TransactionError {
	T result;
	try {
	    processor.beforeTransactionCompletion();
	    PersistentEntityStore store = getStoreHandler().getOrOpen(projectId);
	    result = store.computeInExclusiveTransaction(processor::process);
	    StoreTransaction transaction = store.getCurrentTransaction();
	    if (Objects.nonNull(transaction)) {
		transaction.flush();
	    }
	} catch (Exception e) {
	    processor.onError(e);
	    throw new TransactionError(e.getMessage(), e);
	} finally {
	    processor.afterTransactionCompletion();
	}

	return Optional.of(result);
    }

    protected void dispatch(String collection, Entity entity) {

	List<TermEntry> termEntries = new ArrayList<>();
	try (InputStream blob = entity.getBlob(PropertyUtils.BLOB)) {
	    if (Objects.isNull(blob)) {
		LOGGER.info("Unable to dispatch entriesArray because they are null.");
		return;
	    }
	    TermEntry[] entriesArray = JsonIO.readValue(blob, TermEntry[].class);
	    if (ArrayUtils.isNotEmpty(entriesArray)) {

		termEntries = Arrays.asList(entriesArray);
		getDispatcher().dispatch(new ProcessDataEvent(collection, termEntries));
	    }

	} catch (Exception e) {
	    LOGGER.error(e);

	    long projectId = (long) entity.getProperty(PropertyUtils.PROJECT_ID);

	    revert(collection, termEntries);
	    calculateCounts(collection, projectId);
	    // TODO statistics update

	    throw new EventException(e.getMessage(), e);
	}
    }

    protected EventDispatcher getDispatcher() {
	return _dispatcher;
    }

    protected PersistentStoreHandler getStoreHandler() {
	return storeHandler;
    }

    protected void writeBlob(String property, Entity entity, byte[] bytes) throws IOException {
	try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
	    entity.setBlob(property, in);
	} catch (Exception e) {
	    throw new IOException(e.getMessage(), e);
	}
    }
}
