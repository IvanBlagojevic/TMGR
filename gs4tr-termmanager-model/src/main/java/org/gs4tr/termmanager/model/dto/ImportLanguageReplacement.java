package org.gs4tr.termmanager.model.dto;

public class ImportLanguageReplacement {

    private String _importLanguageCode;
    private String _replacement;

    public String getImportLanguageCode() {
	return _importLanguageCode;
    }

    public String getReplacement() {
	return _replacement;
    }

    public void setImportLanguageCode(String importLanguageCode) {
	_importLanguageCode = importLanguageCode;
    }

    public void setReplacement(String replacement) {
	_replacement = replacement;
    }
}
