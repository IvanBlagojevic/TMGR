package org.gs4tr.termmanager.service.persistence.importer.termentry;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.TransactionalUnit;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.loghandler.ImportTransactionLogHandler;

import jetbrains.exodus.entitystore.EntityId;

public class TermEntryBatchUpdater implements GenericBuffer<TermEntry> {

    /**
     * The internal buffer where data is stored.
     */
    private final List<TermEntry> _buffer;

    /**
     * The maximum size of _buffer to allocate.
     */
    private final int _bufferCapacity;

    private final EntityId _entityId;

    /**
     * Represents an function that consumes Throwable.
     */
    private final Consumer<Throwable> _exceptionHandler;

    /**
     * Status flag to determine when to exit a loop.
     */

    private ImportTransactionLogHandler _logHandler;

    private final long _projectId;

    public TermEntryBatchUpdater(int capacity, Consumer<Throwable> exceptionHandler,
	    ImportTransactionLogHandler logHandler, long projectId, EntityId entityId) {
	_exceptionHandler = requireNonNull(exceptionHandler);
	_buffer = new ArrayList<>(capacity);
	_bufferCapacity = capacity;
	_logHandler = logHandler;
	_projectId = projectId;
	_entityId = entityId;
    }

    @Override
    public void add(TermEntry termEntry) {
	if (isBufferFull()) {
	    try {
		flush();
	    } finally {
		clearBuffer();
	    }
	}
	getBuffer().add(termEntry);
    }

    @Override
    public void clear() {
	if (CollectionUtils.isNotEmpty(getBuffer())) {
	    flush();
	    clearBuffer();
	}
    }

    private void appendAndLinkInLog(TransactionalUnit transactionalUnit) {
	ImportTransactionLogHandler logHandler = getLogHandler();
	logHandler.appendAndLink(getProjectId(), getEntityId(), transactionalUnit);
    }

    private void clearBuffer() {
	getBuffer().clear();
    }

    private void flush() {
	appendAndLinkInLog(new TransactionalUnit(getBuffer()));
    }

    private List<TermEntry> getBuffer() {
	return _buffer;
    }

    private int getBufferCapacity() {
	return _bufferCapacity;
    }

    private int getBufferSize() {
	return getBuffer().size();
    }

    private EntityId getEntityId() {
	return _entityId;
    }

    private ImportTransactionLogHandler getLogHandler() {
	return _logHandler;
    }

    private long getProjectId() {
	return _projectId;
    }

    private boolean isBufferFull() {
	return getBufferSize() == getBufferCapacity();
    }
}