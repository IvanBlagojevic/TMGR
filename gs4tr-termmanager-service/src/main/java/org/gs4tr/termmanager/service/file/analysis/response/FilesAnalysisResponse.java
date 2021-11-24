package org.gs4tr.termmanager.service.file.analysis.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisAlerts;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttributeReport;
import org.gs4tr.termmanager.service.file.analysis.model.ImportLanguageReport;
import org.gs4tr.termmanager.service.file.analysis.model.ImportTypeReport;

public class FilesAnalysisResponse implements Serializable {

    private static final long serialVersionUID = -1916511230900138430L;

    private final Map<ImportAttribute.Level, Set<String>> _attributesByLevel;

    private boolean _completed;

    private boolean _completedExceptionally;

    private final List<FileAnalysisAlerts> _fileAnalysisAlerts;

    private final List<ImportAttributeReport> _importAttributeReports;

    private final List<ImportLanguageReport> _importLanguageReports;

    private final List<ImportTypeReport> _importTypeReports;

    private boolean _isOverwriteByTermEntryId = true;

    public FilesAnalysisResponse() {
	_attributesByLevel = new HashMap<>(3);
	_importAttributeReports = new ArrayList<>();
	_importTypeReports = new ArrayList<>();
	_importLanguageReports = new ArrayList<>();
	_fileAnalysisAlerts = new ArrayList<>();
    }

    public void addIfNonNull(FileAnalysisAlerts alerts) {
	if (Objects.nonNull(alerts)) {
	    getFileAnalysisAlerts().add(alerts);
	}
    }

    public void addIfNonNull(ImportAttributeReport report) {
	if (Objects.nonNull(report)) {
	    getImportAttributeReports().add(report);
	}
    }

    public void addIfNonNull(ImportLanguageReport report) {
	if (Objects.nonNull(report)) {
	    getImportLanguageReports().add(report);
	}
    }

    public void addIfNonNull(ImportTypeReport report) {
	if (Objects.nonNull(report)) {
	    getImportTypeReports().add(report);
	}
    }

    public void addIfNotEmpty(Map<ImportAttribute.Level, Set<String>> attributesByLevel) {
	if (MapUtils.isNotEmpty(attributesByLevel)) {
	    getAttributesByLevel().putAll(attributesByLevel);
	}
    }

    public Map<ImportAttribute.Level, Set<String>> getAttributesByLevel() {
	return _attributesByLevel;
    }

    public List<FileAnalysisAlerts> getFileAnalysisAlerts() {
	return _fileAnalysisAlerts;
    }

    public List<ImportAttributeReport> getImportAttributeReports() {
	return _importAttributeReports;
    }

    public List<ImportLanguageReport> getImportLanguageReports() {
	return _importLanguageReports;
    }

    public List<ImportTypeReport> getImportTypeReports() {
	return _importTypeReports;
    }

    /**
     * Returns true if this analysis completed. Completion may be due to normal
     * termination or an exception -- in all of these cases, this method will
     * return true.
     */
    public boolean isCompleted() {
	return _completed;
    }

    public boolean isCompletedExceptionally() {
	return _completedExceptionally;
    }

    public boolean isOverwriteByTermEntryId() {
	return _isOverwriteByTermEntryId;
    }

    public void setCompleted(boolean completed) {
	_completed = completed;
    }

    public void setCompletedExceptionally(boolean completedExceptionally) {
	_completedExceptionally = completedExceptionally;
    }

    public void setOverwriteByTermEntryId(boolean termEntryIdExist) {
	_isOverwriteByTermEntryId &= termEntryIdExist;
    }
}
