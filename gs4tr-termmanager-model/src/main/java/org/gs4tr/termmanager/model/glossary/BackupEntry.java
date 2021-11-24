package org.gs4tr.termmanager.model.glossary;

public class BackupEntry {

    public static enum SolrUpdateCommandType {
	ADD, CLEAR_HISTORY, COMMIT, DELETE_BY_ID, DELETE_BY_QUERY, END_BATCH, MERGE, REVERT, REVERT_LAST, ROLLBACK, UPDATE;
    }

    private String _collectionName;
    private Object _payload;
    private long _timestamp;
    private SolrUpdateCommandType _type;

    public BackupEntry() {

    }

    public BackupEntry(String collectionName, SolrUpdateCommandType type, long timestamp, Object payload) {
	_collectionName = collectionName;
	_type = type;
	_timestamp = timestamp;
	_payload = payload;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	BackupEntry other = (BackupEntry) obj;
	if (_collectionName == null) {
	    if (other._collectionName != null)
		return false;
	} else if (!_collectionName.equals(other._collectionName))
	    return false;
	if (_payload == null) {
	    if (other._payload != null)
		return false;
	} else if (!_payload.equals(other._payload))
	    return false;
	if (_timestamp != other._timestamp)
	    return false;
	if (_type != other._type)
	    return false;
	return true;
    }

    public String getCollectionName() {
	return _collectionName;
    }

    public Object getPayload() {
	return _payload;
    }

    public long getTimestamp() {
	return _timestamp;
    }

    public SolrUpdateCommandType getType() {
	return _type;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_collectionName == null) ? 0 : _collectionName.hashCode());
	result = prime * result + ((_payload == null) ? 0 : _payload.hashCode());
	result = prime * result + (int) (_timestamp ^ (_timestamp >>> 32));
	result = prime * result + ((_type == null) ? 0 : _type.hashCode());
	return result;
    }

    public void setCollectionName(String collectionName) {
	this._collectionName = collectionName;
    }

    public void setPayload(Object payload) {
	this._payload = payload;
    }

    public void setTimestamp(long timestamp) {
	this._timestamp = timestamp;
    }

    public void setType(SolrUpdateCommandType type) {
	this._type = type;
    }

    @Override
    public String toString() {
	return "BackupEntry [collectionName=" + _collectionName + ", type=" + _type + ", timestamp=" + _timestamp
		+ ", payload=" + _payload + "]";
    }

}
