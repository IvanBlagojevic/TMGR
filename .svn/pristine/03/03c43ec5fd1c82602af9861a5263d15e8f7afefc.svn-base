package org.gs4tr.termmanager.service.file.analysis.model;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ImportAttributeReport implements Serializable {

    private static final long serialVersionUID = 8891730338863146797L;
    
    private final String _fileName;
    private final List<ImportAttribute> _importAttributes;

    public ImportAttributeReport(String fileName) {
	_fileName = Objects.requireNonNull(fileName);
	_importAttributes = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ImportAttributeReport other = (ImportAttributeReport) obj;
	if (_fileName == null) {
	    if (other._fileName != null)
		return false;
	} else if (!_fileName.equals(other._fileName))
	    return false;
	return true;
    }

    public String getFileName() {
	return _fileName;
    }

    public List<ImportAttribute> getImportAttributes() {
	return _importAttributes;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_fileName == null) ? 0 : _fileName.hashCode());
	return result;
    }

    /**
     * {@link #_importAttributes} will be ordered by <code>Status</code>:
     * <code>ImportAttribute</code> with warnings (i.e
     * {@link ImportAttribute#getProjectAttributes()}) should be on top,
     * followed by <code>Status.NEW</code> and ending with
     * <code>Status.EXISTING</code>. <code>ImportAttribute</code> with equal
     * <code>Status</code> will be listed in order they were read from file.
     */
    public void sortImportAttributes() {
	Comparator<ImportAttribute> c1 = comparing(ImportAttribute::getStatus);
	Comparator<ImportAttribute> c2 = comparingInt((ImportAttribute a) -> a.getProjectAttributes().size());
	getImportAttributes().sort(c1.thenComparing(c2.reversed()));
    }

    @Override
    public String toString() {
	return "ImportAttributeReport [_fileName=" + _fileName + ", _importAttributes=" + _importAttributes + "]";
    }
}
