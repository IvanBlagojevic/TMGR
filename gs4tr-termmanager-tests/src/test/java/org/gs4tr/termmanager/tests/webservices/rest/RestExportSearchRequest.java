package org.gs4tr.termmanager.tests.webservices.rest;

public class RestExportSearchRequest {

    private String _afterDate;

    private String _blacklistTermsCount;

    private String _descriptionType;

    private String _exportAllDescriptions;

    private String _exportForbiddenTerms;

    private String _exportFormat;

    private String _generateStatistics;

    private String _sourceLocale;

    private String _targetLocale;

    public String getAfterDate() {
	return _afterDate;
    }

    public String getBlacklistTermsCount() {
	return _blacklistTermsCount;
    }

    public String getDescriptionType() {
	return _descriptionType;
    }

    public String getExportAllDescriptions() {
	return _exportAllDescriptions;
    }

    public String getExportForbiddenTerms() {
	return _exportForbiddenTerms;
    }

    public String getExportFormat() {
	return _exportFormat;
    }

    public String getGenerateStatistics() {
	return _generateStatistics;
    }

    public String getSourceLocale() {
	return _sourceLocale;
    }

    public String getTargetLocale() {
	return _targetLocale;
    }

    public void setAfterDate(String afterDate) {
	_afterDate = afterDate;
    }

    public void setBlacklistTermsCount(String blacklistTermsCount) {
	_blacklistTermsCount = blacklistTermsCount;
    }

    public void setDescriptionType(String descriptionType) {
	_descriptionType = descriptionType;
    }

    public void setExportAllDescriptions(String exportAllDescriptions) {
	_exportAllDescriptions = exportAllDescriptions;
    }

    public void setExportForbiddenTerms(String exportForbiddenTerms) {
	_exportForbiddenTerms = exportForbiddenTerms;
    }

    public void setExportFormat(String exportFormat) {
	_exportFormat = exportFormat;
    }

    public void setGenerateStatistics(String generateStatistics) {
	_generateStatistics = generateStatistics;
    }

    public void setSourceLocale(String sourceLocale) {
	_sourceLocale = sourceLocale;
    }

    public void setTargetLocale(String targetLocale) {
	_targetLocale = targetLocale;
    }
}
