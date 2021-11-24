package org.gs4tr.termmanager.persistence.solr;

import org.gs4tr.tm3.api.ConnectionInfo;

public class TmgrConnectionInfo extends ConnectionInfo {

    private static final long serialVersionUID = 8783762989626462582L;

    private int _batchSize;

    private String _collectionName;

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (!super.equals(obj))
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	TmgrConnectionInfo other = (TmgrConnectionInfo) obj;
	if (_batchSize != other._batchSize)
	    return false;
	if (_collectionName == null) {
	    if (other._collectionName != null)
		return false;
	} else if (!_collectionName.equals(other._collectionName))
	    return false;
	return true;
    }

    public int getBatchSize() {
	return _batchSize;
    }

    public String getCollectionName() {
	return _collectionName;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	result = prime * result + _batchSize;
	result = prime * result + ((_collectionName == null) ? 0 : _collectionName.hashCode());
	return result;
    }

    public void setBatchSize(int batchSize) {
	_batchSize = batchSize;
    }

    public void setCollectionName(String collectionName) {
	_collectionName = collectionName;
    }

    @Override
    public String toString() {
	return "TmgrConnectionInfo [_batchSize=" + _batchSize + ", _collectionName=" + _collectionName + "]";
    }
}
