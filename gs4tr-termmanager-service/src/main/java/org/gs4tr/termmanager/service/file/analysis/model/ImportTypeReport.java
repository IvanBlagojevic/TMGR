package org.gs4tr.termmanager.service.file.analysis.model;

import java.io.Serializable;

public class ImportTypeReport implements Serializable {

    private static final long serialVersionUID = -6127945628184145254L;

    private final String _fileName;

    private final boolean _overwriteByTermEntryId;

    public ImportTypeReport(String fileName, boolean overwriteByTermEntryId) {
	_fileName = fileName;
	_overwriteByTermEntryId = overwriteByTermEntryId;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ImportTypeReport other = (ImportTypeReport) obj;
	if (_fileName == null) {
	    if (other._fileName != null)
		return false;
	} else if (!_fileName.equals(other._fileName))
	    return false;
	return _overwriteByTermEntryId == other._overwriteByTermEntryId;
    }

    public String getFileName() {
	return _fileName;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_fileName == null) ? 0 : _fileName.hashCode());
	return prime * result + (_overwriteByTermEntryId ? 1231 : 1237);
    }

    public boolean isOverwriteByTermEntryId() {
	return _overwriteByTermEntryId;
    }

    @Override
    public String toString() {
	return "ImportTypeReport [_fileName=" + _fileName + ", _overwriteByTermEntryId=" + _overwriteByTermEntryId
		+ "]";
    }
}
