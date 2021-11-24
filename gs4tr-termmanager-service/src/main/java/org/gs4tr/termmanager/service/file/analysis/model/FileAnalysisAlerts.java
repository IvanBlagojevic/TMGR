package org.gs4tr.termmanager.service.file.analysis.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileAnalysisAlerts implements Serializable {

    private static final long serialVersionUID = -288654753220771719L;

    private final List<Alert> _alerts;
    private final String _fileName;

    public FileAnalysisAlerts(String fileName) {
	_fileName = Objects.requireNonNull(fileName);
	_alerts = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	FileAnalysisAlerts other = (FileAnalysisAlerts) obj;
	if (_fileName == null) {
	    if (other._fileName != null)
		return false;
	} else if (!_fileName.equals(other._fileName))
	    return false;
	return true;
    }

    public List<Alert> getAlerts() {
	return _alerts;
    }

    public String getFileName() {
	return _fileName;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_fileName == null) ? 0 : _fileName.hashCode());
	return result;
    }

    @Override
    public String toString() {
	return "FileAnalysisAlerts [_alerts=" + _alerts + ", _fileName=" + _fileName + "]";
    }
}
