package org.gs4tr.termmanager.webservice.exceptions;

public class UnauthorizedAccessException extends Exception {

    String _description;

    public UnauthorizedAccessException(String message) {
	super(message);
    }

    public UnauthorizedAccessException(String message, String description) {
	super(message);
	_description = description;
    }

    public UnauthorizedAccessException(String message, Throwable throwable) {
	super(message, throwable);
    }

    public UnauthorizedAccessException(String message, String description, Throwable throwable) {
	super(message, throwable);
	_description = description;
    }

    public UnauthorizedAccessException(Throwable throwable) {
	super(throwable);
    }

    public String getDescription() {
	return _description;
    }

    public void setDescription(String description) {
	_description = description;
    }

}
