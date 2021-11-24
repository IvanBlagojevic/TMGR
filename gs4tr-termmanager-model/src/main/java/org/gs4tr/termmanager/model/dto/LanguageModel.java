package org.gs4tr.termmanager.model.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class LanguageModel {

    private String _locale;

    private List<TermV2Model> _terms;

    public LanguageModel() {
	_terms = new ArrayList<>();
    }

    @ApiModelProperty(value = "Language locale code.")
    public String getLocale() {
	return _locale;
    }

    @ApiModelProperty(value = "Collection of terms with same language code.")
    public List<TermV2Model> getTerms() {
	return _terms;
    }

    public void setLocale(String locale) {
	_locale = locale;
    }

    public void setTerms(List<TermV2Model> terms) {
	_terms = terms;
    }

}
