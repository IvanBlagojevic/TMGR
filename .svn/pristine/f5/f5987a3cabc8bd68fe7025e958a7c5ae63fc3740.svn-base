package org.gs4tr.termmanager.dao.backup;

import org.hibernate.Session;

import java.util.function.Consumer;

public interface TransactionConsumer extends Consumer<Session> {

    default void afterTransactionCompletion() {
    }

    default void beforeTransactionCompletion() {
    }
}
