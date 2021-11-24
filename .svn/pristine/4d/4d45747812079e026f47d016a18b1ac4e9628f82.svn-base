package org.gs4tr.termmanager.model;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.gs4tr.termmanager.model.dto.DateRange;
import org.gs4tr.termmanager.model.glossary.Description;

public class ExportInfo implements Serializable {

    private static final long serialVersionUID = 525956921774161359L;

    private DateRange _creationDateRange;
    private Set<Description> _descriptionFilter;
    private String _descriptionName;
    private List<String> _descriptionsToExport;
    private String _exportFormat;
    private List<String> _exportTermType;
    private ExportLanguageCriteriaEnum _languageCriteriaEnum;
    private Set<String> _languagesToExport;
    private DateRange _modificationDateRange;
    private boolean _processingCanceled = false;
    private String _projectName;
    private String _shortCode;
    private String _sourceLanguage;
    private List<String> _targetLanguages;
    private File _tempFile;
    private int _totalTermEntriesExported;
    private int _totalTermsExported;
    private List<String> languageIds;

    public DateRange getCreationDateRange() {
	return _creationDateRange;
    }

    public Set<Description> getDescriptionFilter() {
	return _descriptionFilter;
    }

    public String getDescriptionName() {
	return _descriptionName;
    }

    public List<String> getDescriptionsToExport() {
	return _descriptionsToExport;
    }

    public String getExportFormat() {
	return _exportFormat;
    }

    public List<String> getExportTermType() {
	return _exportTermType;
    }

    public List<String> getLanguageIds() {
	return languageIds;
    }

    public ExportLanguageCriteriaEnum getLanguageCriteriaEnum() {
	return _languageCriteriaEnum;
    }

    public Set<String> getLanguagesToExport() {
	return _languagesToExport;
    }

    public DateRange getModificationDateRange() {
	return _modificationDateRange;
    }

    public String getProjectName() {
	return _projectName;
    }

    public String getShortCode() {
	return _shortCode;
    }

    public String getSourceLanguage() {
	return _sourceLanguage;
    }

    public List<String> getTargetLanguages() {
	return _targetLanguages;
    }

    public File getTempFile() {
	return _tempFile;
    }

    public int getTotalTermEntriesExported() {
	return _totalTermEntriesExported;
    }

    public int getTotalTermsExported() {
	return _totalTermsExported;
    }

    public void incrementTotalTermsExported() {
	_totalTermsExported++;
    }

    public boolean isProcessingCanceled() {
	return _processingCanceled;
    }

    public void setCreationDateRange(DateRange creationDateRange) {
	_creationDateRange = creationDateRange;
    }

    public void setDescriptionFilter(Set<Description> descriptionFilter) {
	_descriptionFilter = descriptionFilter;
    }

    public void setDescriptionName(String descriptionName) {
	_descriptionName = descriptionName;
    }

    public void setDescriptionsToExport(List<String> descriptionsToExport) {
	_descriptionsToExport = descriptionsToExport;
    }

    public void setExportFormat(String exportFormat) {
	_exportFormat = exportFormat;
    }

    public void setExportTermType(List<String> exportTermType) {
	_exportTermType = exportTermType;
    }

    public void setLanguageIds(List<String> languageIds) {
	this.languageIds = languageIds;
    }

    public void setLanguageCriteriaEnum(ExportLanguageCriteriaEnum languageCriteriaEnum) {
	_languageCriteriaEnum = languageCriteriaEnum;
    }

    public void setLanguagesToExport(Set<String> languagesToExport) {
	_languagesToExport = languagesToExport;
    }

    public void setModificationDateRange(DateRange modificationDateRange) {
	_modificationDateRange = modificationDateRange;
    }

    public void setProcessingCanceled(boolean processingCanceled) {
	_processingCanceled = processingCanceled;
    }

    public void setProjectName(String projectName) {
	_projectName = projectName;
    }

    public void setShortCode(String shortCode) {
	_shortCode = shortCode;
    }

    public void setSourceLanguage(String sourceLanguage) {
	_sourceLanguage = sourceLanguage;
    }

    public void setTargetLanguages(List<String> targetLanguages) {
	_targetLanguages = targetLanguages;
    }

    public void setTempFile(File tempFile) {
	_tempFile = tempFile;
    }

    public void setTotalTermEntriesExported(int totalTermEntriesExported) {
	_totalTermEntriesExported = totalTermEntriesExported;
    }

    public void setTotalTermsExported(int totalTermsExported) {
	_totalTermsExported = totalTermsExported;
    }
}
