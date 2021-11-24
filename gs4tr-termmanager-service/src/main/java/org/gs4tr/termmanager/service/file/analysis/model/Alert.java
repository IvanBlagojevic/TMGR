package org.gs4tr.termmanager.service.file.analysis.model;

import java.io.Serializable;

/**
 * DTO class which represents foundation for showing specific errors per file.
 * 
 * @since 5.0
 */

public class Alert implements Serializable {

    private static final long serialVersionUID = 752783546135899056L;

    private final String _message;
    private final AlertSubject _subject;
    private final AlertType _type;

    public Alert(AlertSubject subject, AlertType type, String message) {
	_message = message;
	_subject = subject;
	_type = type;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Alert other = (Alert) obj;
	if (_message == null) {
	    if (other._message != null)
		return false;
	} else if (!_message.equals(other._message))
	    return false;
	if (_subject != other._subject)
	    return false;
	return _type == other._type;
    }

    public String getMessage() {
	return _message;
    }

    public AlertSubject getSubject() {
	return _subject;
    }

    public AlertType getType() {
	return _type;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_message == null) ? 0 : _message.hashCode());
	result = prime * result + ((_subject == null) ? 0 : _subject.hashCode());
	return prime * result + ((_type == null) ? 0 : _type.hashCode());
    }

    @Override
    public String toString() {
	return "Alert [_subject=" + _subject + ", _type=" + _type + ", _message=" + _message + "]";
    }
}
