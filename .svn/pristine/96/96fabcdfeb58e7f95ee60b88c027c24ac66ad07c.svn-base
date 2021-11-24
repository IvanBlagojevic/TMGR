package org.gs4tr.termmanager.service.file.analysis.model;

import static java.util.Comparator.comparingInt;
import static java.util.Comparator.comparing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ImportLanguageReport implements Serializable{

    private static final long serialVersionUID = 6343965655172968803L;
    
    private final String _fileName;
    private final List<ImportLanguage> _importLanguages;
    private int _numberOfTermEntries;

    public ImportLanguageReport(final String fileName) {
	_fileName = Objects.requireNonNull(fileName);
	_importLanguages = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ImportLanguageReport other = (ImportLanguageReport) obj;
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

    public List<ImportLanguage> getImportLanguages() {
	return _importLanguages;
    }

    public int getNumberOfTermEntries() {
	return _numberOfTermEntries;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_fileName == null) ? 0 : _fileName.hashCode());
	return result;
    }

    public void setNumberOfTermEntries(int numberOfTermEntries) {
	_numberOfTermEntries = numberOfTermEntries;
    }

    /**
     * {@link #_importLanguages} will be ordered by <code>Status</code>:
     * <code>ImportLanguage</code> with warnings (i.e
     * {@link ImportLanguage#getSimilarProjectLanguages()}) should be on top,
     * followed by <code>Status.NEW</code> and ending with
     * <code>Status.EXISTING</code>. <code>ImportLanguage</code> with equal
     * <code>Status</code> will be listed in order they were read from file.
     * Source <code>ImportLanguage</code> will always be sorted on top.
     */
    public void sortImportLanguages() {
	Comparator<ImportLanguage> c1 = comparing(ImportLanguage::getStatus);
	Comparator<ImportLanguage> c2 = comparingInt((ImportLanguage l) -> l.getSimilarProjectLanguages().size());
	getImportLanguages().sort(c1.thenComparing(c2.reversed()));
    }

    @Override
    public String toString() {
	return "ImportLanguageReport [_fileName=" + _fileName + ", _importLanguages=" + _importLanguages
		+ ", _numberOfTermEntries=" + _numberOfTermEntries + "]";
    }
}
