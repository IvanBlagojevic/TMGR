package org.gs4tr.termmanager.model.dto;

import java.util.LinkedList;

public class LanguagePreviewModel {

    private Boolean _isRTL;

    private Language _language;

    private LinkedList<TermPreviewModel> _termPreviewModels = new LinkedList<>();

    public Boolean getIsRTL() {
	return _isRTL;
    }

    public Language getLanguage() {
	return _language;
    }

    public LinkedList<TermPreviewModel> getTermPreviewModels() {
	return _termPreviewModels;
    }

    public void setIsRTL(Boolean isRTL) {
	_isRTL = isRTL;
    }

    public void setLanguage(Language language) {
	_language = language;
    }
}
