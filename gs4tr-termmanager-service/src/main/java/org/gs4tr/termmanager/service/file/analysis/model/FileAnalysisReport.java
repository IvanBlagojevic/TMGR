package org.gs4tr.termmanager.service.file.analysis.model;

import java.io.Serializable;

public class FileAnalysisReport implements Serializable {

    private static final long serialVersionUID = 6206552235222755356L;

    private FileAnalysisAlerts _fileAnalysisAlerts;
    private ImportAttributeReport _importAttributeReport;
    private ImportLanguageReport _importLanguageReport;
    private ImportTypeReport _importTypeReport;
    private boolean _termEntryIdExist;

    public FileAnalysisReport(String fileName) {
	_fileAnalysisAlerts = new FileAnalysisAlerts(fileName);
    }

    public FileAnalysisReport() {
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	FileAnalysisReport other = (FileAnalysisReport) obj;
	if (_fileAnalysisAlerts == null) {
	    if (other._fileAnalysisAlerts != null)
		return false;
	} else if (!_fileAnalysisAlerts.equals(other._fileAnalysisAlerts))
	    return false;
	if (_importAttributeReport == null) {
	    if (other._importAttributeReport != null)
		return false;
	} else if (!_importAttributeReport.equals(other._importAttributeReport))
	    return false;
	if (_importLanguageReport == null) {
	    if (other._importLanguageReport != null)
		return false;
	} else if (!_importLanguageReport.equals(other._importLanguageReport))
	    return false;
	if (_importTypeReport == null) {
	    if (other._importTypeReport != null)
		return false;
	} else if (!_importTypeReport.equals(other._importTypeReport))
	    return false;
	if (_termEntryIdExist != other._termEntryIdExist)
	    return false;
	return true;
    }

    public FileAnalysisAlerts getFileAnalysisAlerts() {
	return _fileAnalysisAlerts;
    }

    public ImportAttributeReport getImportAttributeReport() {
	return _importAttributeReport;
    }

    public ImportLanguageReport getImportLanguageReport() {
	return _importLanguageReport;
    }

    public ImportTypeReport getImportTypeReport() {
	return _importTypeReport;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_fileAnalysisAlerts == null) ? 0 : _fileAnalysisAlerts.hashCode());
	result = prime * result + ((_importAttributeReport == null) ? 0 : _importAttributeReport.hashCode());
	result = prime * result + ((_importLanguageReport == null) ? 0 : _importLanguageReport.hashCode());
	result = prime * result + ((_importTypeReport == null) ? 0 : _importTypeReport.hashCode());
	result = prime * result + (_termEntryIdExist ? 1231 : 1237);
	return result;
    }

    public boolean isTermEntryIdExist() {
	return _termEntryIdExist;
    }

    public void setFileAnalysisAlerts(FileAnalysisAlerts fileAnalysisAlerts) {
	_fileAnalysisAlerts = fileAnalysisAlerts;
    }

    public void setImportAttributeReport(ImportAttributeReport importAttributeReport) {
	_importAttributeReport = importAttributeReport;
    }

    public void setImportLanguageReport(ImportLanguageReport importLanguageReport) {
	_importLanguageReport = importLanguageReport;
    }

    public void setImportTypeReport(ImportTypeReport importTypeReport) {
	_importTypeReport = importTypeReport;
    }

    public void setTermEntryIdExist(boolean termEntryIdExist) {
	_termEntryIdExist = termEntryIdExist;
    }

    @Override
    public String toString() {
	return "FileAnalysisReport [_fileAnalysisAlerts=" + _fileAnalysisAlerts + ", _importAttributeReport="
		+ _importAttributeReport + ", _importLanguageReport=" + _importLanguageReport + ", _importTypeReport="
		+ _importTypeReport + ", _termEntryIdExist=" + _termEntryIdExist + "]";
    }
}
