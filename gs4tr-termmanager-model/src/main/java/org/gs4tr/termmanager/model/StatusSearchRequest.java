package org.gs4tr.termmanager.model;

import java.util.List;

public class StatusSearchRequest {

    private List<String> _sourceLanguagesList;

    private List<String> _targetLanguagesList;

    public List<String> getSourceLanguagesList() {
	return _sourceLanguagesList;
    }

    public List<String> getTargetLanguagesList() {
	return _targetLanguagesList;
    }

    public void setSourceLanguagesList(List<String> sourceLanguagesList) {
	_sourceLanguagesList = sourceLanguagesList;
    }

    public void setTargetLanguagesList(List<String> targetLanguagesList) {
	_targetLanguagesList = targetLanguagesList;
    }

}