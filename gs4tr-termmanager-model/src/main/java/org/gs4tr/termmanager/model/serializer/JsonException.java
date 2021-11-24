package org.gs4tr.termmanager.model.serializer;

public class JsonException extends RuntimeException {

    public JsonException(String message, Throwable throwable) {
	super(message, throwable);
    }
}
