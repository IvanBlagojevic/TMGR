package org.gs4tr.termmanager.io.tlog;

import java.io.IOException;
import java.util.Optional;

import org.gs4tr.termmanager.io.exception.TransactionError;

import jetbrains.exodus.entitystore.EntityId;

/**
 * TransactionLogIO provides methods for reading, writing and deleting of
 * transaction units from/to transaction log. Transaction units are stored in
 * FIFO order in order to obtain atomicity.
 * 
 * @param <K>
 *            This is the key in transaction log and can be used for finding the
 *            transaction unit that is associated to it.
 * @param <U>
 *            This is the value or batch or transaction unit that is stored in
 *            transaction log.
 *
 * @author emisia TMGR backend
 */
public interface TransactionLogIO<K, U> {

    /**
     * Append transaction unit in transaction log and associated it to already saved
     * transaction transaction unit that acts as a parent unit to it.
     *
     * @param projectId
     *            Represents an project ID. We are using this param to initialize
     *            batch writing into the transaction log.
     * @param parentKey
     *            This is the parent entity key. It is used for linking param unit
     *            to it.
     * @param transactionalUnit
     *            This is an transaction unit and it is associated with parent
     *            entity.
     * @return optional transaction key associated with param unit.
     */
    Optional<K> appendAndLink(long projectId, K parentKey, U transactionalUnit) throws TransactionError, IOException;

    /**
     * This method appends transaction unit in transaction log. This unit represents
     * an info of the parent entity, like import info for example, and it should be
     * called before appendAndLink method in order to store parent entity/unit.
     *
     * @param projectId
     *            Represents an project ID. We are using this param to initialize
     * @param key
     *            We are using this param to find parent entity and to marked it as
     *            finished.
     */
    void finishAppending(long projectId, EntityId key);

    /**
     * Checks if transaction log is locked.
     *
     * @param projectId
     * @return True if transaction log is locked.
     */
    boolean isLocked(long projectId);

    /**
     * This method appends transaction unit in transaction log. This unit represents
     * an info of the parent entity, like import info for example, and it should be
     * called before appendAndLink method in order to store parent entity/unit.
     *
     * @param projectId
     *            Represents an project ID. We are using this param to initialize
     * @param user
     *            We are using this param to identify which user performed the
     *            action.
     * @param action
     *            Represents an action that user is performing. Like import for
     *            example.
     * @param collection
     *            Represents index collection. Eg regularV2 or submissionV2.
     *
     * @return Optional transaction key associated with param unit.
     */
    Optional<K> startAppending(long projectId, String user, String action, String collection);
}
