package org.gs4tr.termmanager.webservice.exceptions;

public class MaximumPermittedSegmentsException extends Exception {

    String _description;

    public MaximumPermittedSegmentsException(String message, String description) {
	super(message);
	_description = description;
    }

    public MaximumPermittedSegmentsException(String message, String description, Throwable throwable) {
	super(message, throwable);
	_description = description;
    }

    public MaximumPermittedSegmentsException(String message) {
	super(message);
    }

    public MaximumPermittedSegmentsException(Throwable throwable) {
	super(throwable);
    }

    public String getDescription() {
	return _description;
    }

    public void setDescription(String description) {
	_description = description;
    }
}
