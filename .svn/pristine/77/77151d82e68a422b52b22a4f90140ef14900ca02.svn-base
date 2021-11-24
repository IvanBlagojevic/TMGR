package org.gs4tr.termmanager.model.dto;

public class HistoryItem {
    private boolean _bold;
    private long _date;
    private String _fieldName;
    private Boolean _isRTL;
    private String _message;
    private String _newStatus;
    private String _newValue;
    private String _oldStatus;
    private String _oldValue;
    private String _path;
    private String _user;

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	HistoryItem other = (HistoryItem) obj;
	if (_date != other._date)
	    return false;
	if (_fieldName == null) {
	    if (other._fieldName != null)
		return false;
	} else if (!_fieldName.equals(other._fieldName))
	    return false;
	if (_newValue == null) {
	    if (other._newValue != null)
		return false;
	} else if (!_newValue.equals(other._newValue))
	    return false;
	if (_oldValue == null) {
	    if (other._oldValue != null)
		return false;
	} else if (!_oldValue.equals(other._oldValue))
	    return false;
	if (_path == null) {
	    if (other._path != null)
		return false;
	} else if (!_path.equals(other._path))
	    return false;
	return true;
    }

    public long getDate() {
	return _date;
    }

    public String getFieldName() {
	return _fieldName;
    }

    public Boolean getIsRTL() {
	return _isRTL;
    }

    public String getMessage() {
	return _message;
    }

    public String getNewStatus() {
	return _newStatus;
    }

    public String getNewValue() {
	return _newValue;
    }

    public String getOldStatus() {
	return _oldStatus;
    }

    public String getOldValue() {
	return _oldValue;
    }

    public String getPath() {
	return _path;
    }

    public String getUser() {
	return _user;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (int) (_date ^ (_date >>> 32));
	result = prime * result + ((_fieldName == null) ? 0 : _fieldName.hashCode());
	result = prime * result + ((_newValue == null) ? 0 : _newValue.hashCode());
	result = prime * result + ((_oldValue == null) ? 0 : _oldValue.hashCode());
	result = prime * result + ((_path == null) ? 0 : _path.hashCode());
	return result;
    }

    public boolean isBold() {
	return _bold;
    }

    public void setBold(boolean bold) {
	_bold = bold;
    }

    public void setDate(long date) {
	_date = date;
    }

    public void setFieldName(String fieldName) {
	_fieldName = fieldName;
    }

    public void setIsRTL(Boolean isRTL) {
	_isRTL = isRTL;
    }

    public void setMessage(String message) {
	_message = message;
    }

    public void setNewStatus(String newStatus) {
	_newStatus = newStatus;
    }

    public void setNewValue(String newValue) {
	_newValue = newValue;
    }

    public void setOldStatus(String oldStatus) {
	_oldStatus = oldStatus;
    }

    public void setOldValue(String oldValue) {
	_oldValue = oldValue;
    }

    public void setPath(String path) {
	_path = path;
    }

    public void setUser(String user) {
	_user = user;
    }
}