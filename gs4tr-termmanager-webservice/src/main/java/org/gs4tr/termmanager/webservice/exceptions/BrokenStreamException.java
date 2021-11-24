package org.gs4tr.termmanager.webservice.exceptions;

public class BrokenStreamException extends Exception {

    String _description;

    public BrokenStreamException(String message) {
	super(message);
    }

    public BrokenStreamException(String message, String description) {
	super(message);
	_description = description;
    }

    public BrokenStreamException(String message, String description, Throwable throwable) {
	super(message, throwable);
	_description = description;
    }

    public BrokenStreamException(String message, Throwable throwable) {
	super(message, throwable);
    }

    public BrokenStreamException(Throwable throwable) {
	super(throwable);
    }

    public String getDescription() {
	return _description;
    }

    public void setDescription(String description) {
	_description = description;
    }
}
