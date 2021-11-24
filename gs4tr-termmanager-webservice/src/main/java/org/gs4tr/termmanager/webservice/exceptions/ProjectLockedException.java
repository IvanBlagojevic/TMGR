package org.gs4tr.termmanager.webservice.exceptions;

import org.gs4tr.foundation.modules.entities.model.UserMessageTypeEnum;

public class ProjectLockedException extends Exception {

    private UserMessageTypeEnum _messageType;

    String _description;

    public ProjectLockedException(String message, String description, UserMessageTypeEnum messageType) {
	super(message);
	_description = description;
	_messageType = messageType;
    }

    public ProjectLockedException(String message, String description, Throwable throwable) {
	super(message, throwable);
	_description = description;
    }

    public ProjectLockedException(String message) {
	super(message);
    }

    public ProjectLockedException(Throwable throwable) {
	super(throwable);
    }

    public String getDescription() {
	return _description;
    }

    public UserMessageTypeEnum getMessageType() {
	return _messageType;
    }

    public void setDescription(String description) {
	_description = description;
    }

    public void setMessageType(UserMessageTypeEnum messageType) {
	_messageType = messageType;
    }
}
