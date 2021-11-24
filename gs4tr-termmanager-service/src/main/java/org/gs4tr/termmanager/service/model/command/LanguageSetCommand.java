package org.gs4tr.termmanager.service.model.command;

import java.util.Set;

public class LanguageSetCommand {

    private String _languageSetName;

    private Set<String> _languageCodes;

    private Long _languageSetId;

    public Set<String> getLanguageCodes() {
	return _languageCodes;
    }

    public Long getLanguageSetId() {
	return _languageSetId;
    }

    public String getLanguageSetName() {
	return _languageSetName;
    }

    public void setLanguageCodes(Set<String> languageCodes) {
	_languageCodes = languageCodes;
    }

    public void setLanguageSetId(Long languageSetId) {
	_languageSetId = languageSetId;
    }

    public void setLanguageSetName(String languageSetName) {
	_languageSetName = languageSetName;
    }

}
