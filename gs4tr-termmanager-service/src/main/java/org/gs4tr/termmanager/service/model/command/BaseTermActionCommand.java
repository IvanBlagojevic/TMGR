package org.gs4tr.termmanager.service.model.command;

import java.util.List;

public class BaseTermActionCommand {

    private boolean _includeSourceSynonyms = false;

    private boolean _includeTargetSynonyms = false;

    private String _sourceLanguage;

    private List<String> _targetLanguages;

    public String getSourceLanguage() {
	return _sourceLanguage;
    }

    public List<String> getTargetLanguages() {
	return _targetLanguages;
    }

    public boolean isIncludeSourceSynonyms() {
	return _includeSourceSynonyms;
    }

    public boolean isIncludeTargetSynonyms() {
	return _includeTargetSynonyms;
    }

    public void setIncludeSourceSynonyms(boolean includeSourceSynonyms) {
	_includeSourceSynonyms = includeSourceSynonyms;
    }

    public void setIncludeTargetSynonyms(boolean includeTargetSynonyms) {
	_includeTargetSynonyms = includeTargetSynonyms;
    }

    public void setSourceLanguage(String sourceLanguage) {
	_sourceLanguage = sourceLanguage;
    }

    public void setTargetLanguages(List<String> targetLanguages) {
	_targetLanguages = targetLanguages;
    }
}
