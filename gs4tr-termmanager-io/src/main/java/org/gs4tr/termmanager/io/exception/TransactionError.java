package org.gs4tr.termmanager.io.exception;

public class TransactionError extends RuntimeException {

    public TransactionError(String message, Throwable throwable) {
	super(message, throwable);
    }
}
